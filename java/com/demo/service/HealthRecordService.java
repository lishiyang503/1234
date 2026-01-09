package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.HealthRecord;

import java.util.List;

/**
 * 健康档案表服务接口
 */
public interface HealthRecordService extends IService<HealthRecord> {
    /**
     * 获取健康档案列表（支持分页、姓名搜索）
     * @param name 姓名
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<HealthRecord> getHealthRecordList(String name, Integer page, Integer pageSize);
    
    /**
     * 获取所有健康档案列表
     * @return 健康档案列表
     */
    List<HealthRecord> getAll();
}