package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.DietRequirement;

import java.util.List;

/**
 * 膳食需求表服务接口
 */
public interface DietRequirementService extends IService<DietRequirement> {
    /**
     * 获取膳食需求列表（支持分页、名称和膳食类型筛选）
     * @param name 姓名
     * @param dietType 膳食类型
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<DietRequirement> getDietList(String name, String dietType, Integer page, Integer pageSize);
    
    /**
     * 获取所有膳食需求列表
     * @param dietType 膳食类型（可选，用于筛选）
     * @return 膳食需求列表
     */
    List<DietRequirement> getAll(String dietType);
}