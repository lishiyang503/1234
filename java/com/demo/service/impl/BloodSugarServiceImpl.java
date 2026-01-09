package com.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.BloodSugarMapper;
import com.demo.pojo.BloodSugar;
import com.demo.service.BloodSugarService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 血糖记录Service实现类
 */
@Service
public class BloodSugarServiceImpl extends ServiceImpl<BloodSugarMapper, BloodSugar> implements BloodSugarService {

    /**
     * 获取所有血糖记录
     * @return 血糖记录列表
     */
    @Override
    public List<BloodSugar> getAllBloodSugarRecords() {
        return this.list();
    }

    /**
     * 添加血糖记录
     * @param bloodSugar 血糖记录实体
     * @return 是否添加成功
     */
    @Override
    public boolean addBloodSugarRecord(BloodSugar bloodSugar) {
        return this.save(bloodSugar);
    }

    /**
     * 根据老人ID获取血糖记录
     * @param residentId 老人ID
     * @return 血糖记录列表
     */
    @Override
    public List<BloodSugar> getBloodSugarRecordsByResidentId(Integer residentId) {
        return this.lambdaQuery().eq(BloodSugar::getResidentId, residentId).list();
    }

    /**
     * 根据老人ID获取最新的血糖记录
     * @param residentId 老人ID
     * @return 最新的血糖记录
     */
    @Override
    public BloodSugar getLatestByResidentId(Integer residentId) {
        return this.lambdaQuery().eq(BloodSugar::getResidentId, residentId)
                .orderByDesc(BloodSugar::getMeasureTime)
                .last("LIMIT 1")
                .one();
    }
}
