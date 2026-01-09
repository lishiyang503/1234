package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.BloodPressure;

import java.util.List;

/**
 * 血压记录Service接口
 */
public interface BloodPressureService extends IService<BloodPressure> {

    /**
     * 获取所有血压记录
     * @return 血压记录列表
     */
    List<BloodPressure> getAllBloodPressureRecords();

    /**
     * 添加血压记录
     * @param bloodPressure 血压记录实体
     * @return 是否添加成功
     */
    boolean addBloodPressureRecord(BloodPressure bloodPressure);

    /**
     * 根据老人ID获取血压记录
     * @param residentId 老人ID
     * @return 血压记录列表
     */
    List<BloodPressure> getBloodPressureRecordsByResidentId(Integer residentId);
}
