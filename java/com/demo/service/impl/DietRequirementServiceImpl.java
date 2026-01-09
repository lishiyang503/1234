package com.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.DietRequirementMapper;
import com.demo.pojo.DietRequirement;
import com.demo.service.DietRequirementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 膳食需求表服务实现类
 */
@Service
public class DietRequirementServiceImpl extends ServiceImpl<DietRequirementMapper, DietRequirement> implements DietRequirementService {

    /**
     * 获取膳食需求列表（支持分页、名称和膳食类型筛选）
     * @param name 姓名
     * @param dietType 膳食类型
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<DietRequirement> getDietList(String name, String dietType, Integer page, Integer pageSize) {
        Page<DietRequirement> dietPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<DietRequirement> wrapper = new LambdaQueryWrapper<>();
        
        // 根据姓名模糊查询
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(DietRequirement::getResidentName, name.trim());
        }
        
        // 根据膳食类型精确查询，将前端英文值映射为数据库中文值
        if (dietType != null && !dietType.trim().isEmpty()) {
            String mappedDietType = mapDietType(dietType.trim());
            wrapper.eq(DietRequirement::getDietType, mappedDietType);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(DietRequirement::getId);
        
        return this.page(dietPage, wrapper);
    }
    
    /**
     * 将前端英文饮食类型值映射为数据库中文值
     * @param dietType 前端传入的英文饮食类型
     * @return 数据库中对应的中文饮食类型
     */
    private String mapDietType(String dietType) {
        switch (dietType) {
            case "diabetes":
                return "糖尿病饮食";
            case "hypertension":
                return "高血压饮食";
            case "hyperlipidemia":
                return "高血脂饮食";
            case "lowSalt":
                return "低盐饮食";
            case "lowFat":
                return "低脂肪饮食";
            case "other":
                return "其他";
            default:
                return dietType; // 若为中文则直接返回
        }
    }
    
    /**
     * 获取所有膳食需求列表
     * @param dietType 膳食类型（可选，用于筛选）
     * @return 膳食需求列表
     */
    @Override
    public List<DietRequirement> getAll(String dietType) {
        LambdaQueryWrapper<DietRequirement> wrapper = new LambdaQueryWrapper<>();
        
        // 根据膳食类型精确查询，将前端英文值映射为数据库中文值
        if (dietType != null && !dietType.trim().isEmpty()) {
            String mappedDietType = mapDietType(dietType.trim());
            wrapper.eq(DietRequirement::getDietType, mappedDietType);
        }
        
        wrapper.orderByAsc(DietRequirement::getId);
        return this.list(wrapper);
    }
}