package com.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.BloodPressureMapper;
import com.demo.pojo.BloodPressure;
import com.demo.service.BloodPressureService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 血压记录Service实现类
 */
@Service
public class BloodPressureServiceImpl extends ServiceImpl<BloodPressureMapper, BloodPressure> implements BloodPressureService {

    /**
     * 获取所有血压记录
     * @return 血压记录列表
     */
    @Override
    public List<BloodPressure> getAllBloodPressureRecords() {
        return this.list();
    }

    /**
     * 添加血压记录
     * @param bloodPressure 血压记录实体
     * @return 是否添加成功
     */
    @Override
    public boolean addBloodPressureRecord(BloodPressure bloodPressure) {
        return this.save(bloodPressure);
    }

    /**
     * 根据老人ID获取血压记录
     * @param residentId 老人ID
     * @return 血压记录列表
     */
    @Override
    public List<BloodPressure> getBloodPressureRecordsByResidentId(Integer residentId) {
        return this.lambdaQuery().eq(BloodPressure::getResidentId, residentId).list();
    }
}
