package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.HeartRate;

import java.util.List;

/**
 * 心率记录Service接口
 */
public interface HeartRateService extends IService<HeartRate> {

    /**
     * 获取所有心率记录
     * @return 心率记录列表
     */
    List<HeartRate> getAllHeartRateRecords();

    /**
     * 添加心率记录
     * @param heartRate 心率记录实体
     * @return 是否添加成功
     */
    boolean addHeartRateRecord(HeartRate heartRate);

    /**
     * 根据老人ID获取心率记录
     * @param residentId 老人ID
     * @return 心率记录列表
     */
    List<HeartRate> getHeartRateRecordsByResidentId(Integer residentId);
}
