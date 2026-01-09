package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.ServicePurchaseRecordMapper;
import com.demo.pojo.ServicePurchaseRecord;
import com.demo.service.ServicePurchaseRecordService;
import org.springframework.stereotype.Service;

/**
 * 服务购买记录表服务实现类
 */
@Service
public class ServicePurchaseRecordServiceImpl extends ServiceImpl<ServicePurchaseRecordMapper, ServicePurchaseRecord> implements ServicePurchaseRecordService {

    /**
     * 获取服务购买记录列表（支持分页、老人姓名搜索）
     * @param residentName 老人姓名
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<ServicePurchaseRecord> getPurchaseRecordList(String residentName, Integer page, Integer pageSize) {
        Page<ServicePurchaseRecord> recordPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<ServicePurchaseRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 根据老人姓名模糊查询
        if (residentName != null && !residentName.isEmpty()) {
            wrapper.like(ServicePurchaseRecord::getResidentName, residentName);
        }
        
        // 按id降序排序
        wrapper.orderByDesc(ServicePurchaseRecord::getId);
        
        return this.page(recordPage, wrapper);
    }
}