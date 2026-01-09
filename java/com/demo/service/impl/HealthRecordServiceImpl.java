package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.HealthRecordMapper;
import com.demo.pojo.HealthRecord;
import com.demo.service.HealthRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 健康档案表服务实现类
 */
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    /**
     * 获取健康档案列表（支持分页、姓名搜索）
     * @param name 姓名
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<HealthRecord> getHealthRecordList(String name, Integer page, Integer pageSize) {
        Page<HealthRecord> healthPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 根据姓名模糊查询
        if (name != null && !name.isEmpty()) {
            wrapper.like(HealthRecord::getResidentName, name);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(HealthRecord::getId);
        
        return this.page(healthPage, wrapper);
    }
    
    /**
     * 获取所有健康档案列表
     * @return 健康档案列表
     */
    @Override
    public List<HealthRecord> getAll() {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(HealthRecord::getId);
        return this.list(wrapper);
    }
}