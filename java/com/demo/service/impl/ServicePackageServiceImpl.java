package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.ServicePackageMapper;
import com.demo.pojo.ServicePackage;
import com.demo.service.ServicePackageService;
import org.springframework.stereotype.Service;

/**
 * 服务套餐表服务实现类
 */
@Service
public class ServicePackageServiceImpl extends ServiceImpl<ServicePackageMapper, ServicePackage> implements ServicePackageService {

    /**
     * 获取服务套餐列表（支持分页、名称和级别搜索）
     * @param name 套餐名称
     * @param level 级别
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<ServicePackage> getServicePackageList(String name, String level, Integer page, Integer pageSize) {
        Page<ServicePackage> packagePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<ServicePackage> wrapper = new LambdaQueryWrapper<>();
        
        // 根据套餐名称模糊查询
        if (name != null && !name.isEmpty()) {
            wrapper.like(ServicePackage::getName, name);
        }
        
        // 根据级别模糊查询
        if (level != null && !level.isEmpty()) {
            wrapper.like(ServicePackage::getLevel, level);
        }
        
        // 按id升序排序
        wrapper.orderByAsc(ServicePackage::getId);
        
        return this.page(packagePage, wrapper);
    }
}