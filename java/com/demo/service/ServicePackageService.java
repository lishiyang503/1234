package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.ServicePackage;

/**
 * 服务套餐表服务接口
 */
public interface ServicePackageService extends IService<ServicePackage> {
    /**
     * 获取服务套餐列表（支持分页、名称和级别搜索）
     * @param name 套餐名称
     * @param level 级别
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<ServicePackage> getServicePackageList(String name, String level, Integer page, Integer pageSize);
}