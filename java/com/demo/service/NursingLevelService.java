package com.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.NursingLevel;

/**
 * 护理级别Service接口
 */
public interface NursingLevelService extends IService<NursingLevel> {

    /**
     * 获取护理级别列表（支持分页、名称搜索）
     * @param name 级别名称
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<NursingLevel> getNursingLevelList(String name, Integer page, Integer pageSize);

    /**
     * 获取所有护理级别列表
     * @return 护理级别列表
     */
    java.util.List<NursingLevel> getAll();
}