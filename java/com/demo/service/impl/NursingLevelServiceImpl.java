package com.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.NursingLevelMapper;
import com.demo.pojo.NursingLevel;
import com.demo.service.NursingLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 护理级别Service实现类
 */
@Service
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements NursingLevelService {

    /**
     * 获取护理级别列表（支持分页、名称搜索）
     */
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<NursingLevel> getNursingLevelList(String name, Integer page, Integer pageSize) {
        Page<NursingLevel> nursingLevelPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<NursingLevel> wrapper = new LambdaQueryWrapper<>();
        
        // 根据名称模糊查询
        if (name != null && !name.isEmpty()) {
            wrapper.like(NursingLevel::getName, name);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(NursingLevel::getId);
        
        return this.page(nursingLevelPage, wrapper);
    }

    /**
     * 获取所有护理级别列表
     */
    @Override
    public List<NursingLevel> getAll() {
        LambdaQueryWrapper<NursingLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(NursingLevel::getId);
        return this.list(wrapper);
    }
}