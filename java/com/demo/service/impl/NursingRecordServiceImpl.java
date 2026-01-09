package com.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.NursingRecordMapper;
import com.demo.pojo.NursingRecord;
import com.demo.service.NursingRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 护理记录Service实现类
 */
@Service
public class NursingRecordServiceImpl extends ServiceImpl<NursingRecordMapper, NursingRecord> implements NursingRecordService {

    /**
     * 获取护理记录列表（支持分页、老人姓名和完成状态搜索）
     */
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<NursingRecord> getNursingRecordList(String residentName, Integer isCompleted, Integer page, Integer pageSize) {
        Page<NursingRecord> nursingRecordPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<NursingRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 根据老人姓名模糊查询
        if (residentName != null && !residentName.isEmpty()) {
            wrapper.like(NursingRecord::getResidentName, residentName);
        }
        
        // 根据完成状态精确查询
        if (isCompleted != null) {
            wrapper.eq(NursingRecord::getCompleted, isCompleted);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(NursingRecord::getId);
        
        return this.page(nursingRecordPage, wrapper);
    }

    /**
     * 获取所有护理记录列表
     */
    @Override
    public List<NursingRecord> getAll() {
        LambdaQueryWrapper<NursingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(NursingRecord::getId);
        return this.list(wrapper);
    }
}