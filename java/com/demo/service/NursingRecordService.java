package com.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.NursingRecord;

/**
 * 护理记录Service接口
 */
public interface NursingRecordService extends IService<NursingRecord> {

    /**
     * 获取护理记录列表（支持分页、老人姓名和完成状态搜索）
     * @param residentName 老人姓名
     * @param isCompleted 是否完成：0未完成，1已完成
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<NursingRecord> getNursingRecordList(String residentName, Integer isCompleted, Integer page, Integer pageSize);

    /**
     * 获取所有护理记录列表
     * @return 护理记录列表
     */
    java.util.List<NursingRecord> getAll();
}