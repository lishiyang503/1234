package com.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.pojo.Bed;
import java.util.List;

/**
 * 床位表服务接口
 */
public interface BedService extends IService<Bed> {
    /**
     * 获取所有床位
     * @return 床位列表
     */
    List<Bed> getAll();
    
    /**
     * 获取床位列表（支持分页、搜索）
     * @param roomId 房间ID
     * @param status 床位状态
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<Bed> getBedList(Integer roomId, Integer status, Integer page, Integer pageSize);
}