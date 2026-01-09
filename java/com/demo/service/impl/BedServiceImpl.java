package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.BedMapper;
import com.demo.pojo.Bed;
import com.demo.service.BedService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 床位表服务实现类
 */
@Service
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {

    /**
     * 获取所有床位
     * @return 床位列表
     */
    @Override
    public List<Bed> getAll() {
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Bed::getId);
        return this.list(wrapper);
    }
    
    /**
     * 获取床位列表（支持分页、搜索）
     * @param roomId 房间ID
     * @param status 床位状态
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<Bed> getBedList(Integer roomId, Integer status, Integer page, Integer pageSize) {
        Page<Bed> bedPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
        
        // 根据房间ID搜索
        if (roomId != null) {
            wrapper.eq(Bed::getRoomId, roomId);
        }
        
        // 根据床位状态搜索
        if (status != null) {
            wrapper.eq(Bed::getStatus, status);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(Bed::getId);
        
        return this.page(bedPage, wrapper);
    }
}