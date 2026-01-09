package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.Resident;

import java.util.List;

/**
 * 入住登记表服务接口
 */
public interface ResidentService extends IService<Resident> {
    /**
     * 获取入住登记列表（支持分页、姓名和身份证号搜索）
     * @param name 姓名
     * @param idCard 身份证号
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<Resident> getResidentList(String name, String idCard, Integer page, Integer pageSize);
    
    /**
     * 获取所有入住登记列表
     * @return 入住登记列表
     */
    List<Resident> getAll();
    
    /**
     * 老人出院操作
     * @param residentId 老人ID
     * @param reason 出院原因
     * @param operator 操作人
     */
    void dischargeResident(Integer residentId, String reason, String operator);
    
    /**
     * 根据身份证号查询老人信息
     * @param idCard 身份证号
     * @return 老人信息
     */
    Resident getByidCard(String idCard);
}