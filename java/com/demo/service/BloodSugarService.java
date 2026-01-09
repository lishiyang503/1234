package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.BloodSugar;

import java.util.List;

/**
 * 血糖记录Service接口
 */
public interface BloodSugarService extends IService<BloodSugar> {

    /**
     * 获取所有血糖记录
     * @return 血糖记录列表
     */
    List<BloodSugar> getAllBloodSugarRecords();

    /**
     * 添加血糖记录
     * @param bloodSugar 血糖记录实体
     * @return 是否添加成功
     */
    boolean addBloodSugarRecord(BloodSugar bloodSugar);

    /**
     * 根据老人ID获取血糖记录
     * @param residentId 老人ID
     * @return 血糖记录列表
     */
    List<BloodSugar> getBloodSugarRecordsByResidentId(Integer residentId);

    /**
     * 根据老人ID获取最新的血糖记录
     * @param residentId 老人ID
     * @return 最新的血糖记录
     */
    BloodSugar getLatestByResidentId(Integer residentId);
}
