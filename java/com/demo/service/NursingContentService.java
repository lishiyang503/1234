package com.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.NursingContent;

/**
 * 护理内容Service接口
 */
public interface NursingContentService extends IService<NursingContent> {

    /**
     * 获取护理内容列表（支持分页、名称和类别搜索）
     * @param name 内容名称
     * @param category 类别
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<NursingContent> getNursingContentList(String name, Integer category, Integer page, Integer pageSize);

    /**
     * 获取所有护理内容列表
     * @return 护理内容列表
     */
    java.util.List<NursingContent> getAll();
    
    /**
     * 获取所有护理内容（兼容用户提供的方法名）
     * @return 护理内容列表
     */
    java.util.List<NursingContent> getAllNursingContents();
}