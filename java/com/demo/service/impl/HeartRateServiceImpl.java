package com.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.HeartRateMapper;
import com.demo.pojo.HeartRate;
import com.demo.service.HeartRateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心率记录Service实现类
 */
@Service
public class HeartRateServiceImpl extends ServiceImpl<HeartRateMapper, HeartRate> implements HeartRateService {

    /**
     * 获取所有心率记录
     * @return 心率记录列表
     */
    @Override
    public List<HeartRate> getAllHeartRateRecords() {
        return this.list();
    }

    /**
     * 添加心率记录
     * @param heartRate 心率记录实体
     * @return 是否添加成功
     */
    @Override
    public boolean addHeartRateRecord(HeartRate heartRate) {
        return this.save(heartRate);
    }

    /**
     * 根据老人ID获取心率记录
     * @param residentId 老人ID
     * @return 心率记录列表
     */
    @Override
    public List<HeartRate> getHeartRateRecordsByResidentId(Integer residentId) {
        return this.lambdaQuery().eq(HeartRate::getResidentId, residentId).list();
    }
}
