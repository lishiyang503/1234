package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.ResidentMapper;
import com.demo.pojo.Bed;
import com.demo.pojo.HealthRecord;
import com.demo.pojo.Resident;
import com.demo.pojo.ResidentHistory;
import com.demo.pojo.Rooms;
import com.demo.service.BedService;
import com.demo.service.HealthRecordService;
import com.demo.service.ResidentHistoryService;
import com.demo.service.ResidentService;
import com.demo.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * 入住登记表服务实现类
 */
@Service
public class ResidentServiceImpl extends ServiceImpl<ResidentMapper, Resident> implements ResidentService {

    @Autowired
    private BedService bedService;
    
    @Autowired
    private ResidentHistoryService residentHistoryService;
    
    @Autowired
    private RoomsService roomsService;
    
    @Autowired
    private HealthRecordService healthRecordService;

    /**
     * 获取入住登记列表（支持分页、姓名和身份证号搜索）
     * @param name 姓名
     * @param idCard 身份证号
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<Resident> getResidentList(String name, String idCard, Integer page, Integer pageSize) {
        Page<Resident> residentPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        
        // 根据姓名模糊查询
        if (name != null && !name.isEmpty()) {
            wrapper.like(Resident::getName, name);
        }
        
        // 根据身份证号模糊查询
        if (idCard != null && !idCard.isEmpty()) {
            wrapper.like(Resident::getIdCard, idCard);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(Resident::getId);
        
        return this.page(residentPage, wrapper);
    }
    
    /**
     * 获取所有入住登记列表
     * @return 入住登记列表
     */
    @Override
    public List<Resident> getAll() {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Resident::getId);
        return this.list(wrapper);
    }
    
    /**
     * 老人出院操作
     * @param residentId 老人ID
     * @param reason 出院原因
     * @param operator 操作人
     */
    @Override
    @Transactional
    public void dischargeResident(Integer residentId, String reason, String operator) {
        // 1. 查询老人
        Resident resident = this.getById(residentId);
        if (resident == null || !"入住".equals(resident.getStatus())) {
            throw new RuntimeException("老人当前不在入住状态");
        }

        // 2. 查询床位
        Bed bed = bedService.getOne(
            new LambdaQueryWrapper<Bed>()
                .eq(Bed::getResidentId, residentId)
        );
        if (bed == null) {
            throw new RuntimeException("未找到老人床位");
        }

        // 3. 写入住历史
        ResidentHistory history = new ResidentHistory();
        history.setResidentId(resident.getId());
        history.setResidentName(resident.getName());
        history.setRoomNumber(resident.getRoomNumber());
        history.setBedNumber(resident.getBedNumber());
        history.setEntryDate(resident.getEntryDate());
        history.setExitDate(new Date());

        // 计算入住时长
        LocalDate entryLocalDate = resident.getEntryDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate exitLocalDate = LocalDate.now();
        long days = ChronoUnit.DAYS.between(entryLocalDate, exitLocalDate);
        history.setDuration((int) days);
        history.setReason(reason);
        history.setOperator(operator);

        residentHistoryService.save(history);

        // 4. 删除健康档案
        LambdaQueryWrapper<HealthRecord> healthRecordWrapper = new LambdaQueryWrapper<>();
        healthRecordWrapper.eq(HealthRecord::getResidentId, residentId);
        healthRecordService.remove(healthRecordWrapper);

        // 5. 更新老人状态
        resident.setStatus("退房");
        resident.setExitDate(new Date());
        this.updateById(resident);

        // 6. 释放床位
        UpdateWrapper<Bed> bedWrapper = new UpdateWrapper<>();
        bedWrapper.eq("id", bed.getId())
                  .set("resident_id", null)
                  .set("status", 1);

        bedService.update(bedWrapper);

        // 7. 判断房间是否空闲
        Long usedCount = bedService.count(
            new LambdaQueryWrapper<Bed>()
                .eq(Bed::getRoomId, bed.getRoomId())
                .ne(Bed::getStatus, 1)
        );

        if (usedCount == 0) {
            // 查找房间
            Rooms room = roomsService.getOne(
                new LambdaQueryWrapper<Rooms>()
                    .eq(Rooms::getRoomNumber, resident.getRoomNumber())
            );
            if (room != null) {
                roomsService.update(
                    new UpdateWrapper<Rooms>()
                        .eq("id", room.getId())
                        .set("status", 1)
                );
            }
        }
    }

    /**
     * 根据身份证号查询老人信息
     * @param idCard 身份证号
     * @return 老人信息
     */
    @Override
    public Resident getByidCard(String idCard) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getIdCard, idCard);
        return this.getOne(wrapper);
    }
}