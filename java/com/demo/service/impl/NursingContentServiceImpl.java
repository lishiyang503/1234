package com.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.NursingContentMapper;
import com.demo.pojo.NursingContent;
import com.demo.service.NursingContentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 护理内容Service实现类
 */
@Service
public class NursingContentServiceImpl extends ServiceImpl<NursingContentMapper, NursingContent> implements NursingContentService {

    /**
     * 获取护理内容列表（支持分页、名称和类别搜索）
     */
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<NursingContent> getNursingContentList(String name, Integer category, Integer page, Integer pageSize) {
        Page<NursingContent> nursingContentPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<NursingContent> wrapper = new LambdaQueryWrapper<>();
        
        // 根据名称模糊查询
        if (name != null && !name.isEmpty()) {
            wrapper.like(NursingContent::getName, name);
        }
        
        // 根据类别精确查询
        if (category != null) {
            wrapper.eq(NursingContent::getCategory, category);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(NursingContent::getId);
        
        return this.page(nursingContentPage, wrapper);
    }

    /**
     * 获取所有护理内容列表
     */
    @Override
    public List<NursingContent> getAll() {
        LambdaQueryWrapper<NursingContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(NursingContent::getId);
        return this.list(wrapper);
    }
    
    /**
     * 获取所有护理内容（兼容用户提供的方法名）
     */
    @Override
    public List<NursingContent> getAllNursingContents() {
        return getAll();
    }
}