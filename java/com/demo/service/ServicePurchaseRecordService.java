package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.ServicePurchaseRecord;

/**
 * 服务购买记录表服务接口
 */
public interface ServicePurchaseRecordService extends IService<ServicePurchaseRecord> {
    /**
     * 获取服务购买记录列表（支持分页、老人姓名搜索）
     * @param residentName 老人姓名
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<ServicePurchaseRecord> getPurchaseRecordList(String residentName, Integer page, Integer pageSize);
}