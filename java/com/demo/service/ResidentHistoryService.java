package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.ResidentHistory;

/**
 * 入住历史表服务接口
 */
public interface ResidentHistoryService extends IService<ResidentHistory> {
    /**
     * 获取入住历史列表（支持分页、姓名和床位号搜索）
     * @param name 姓名
     * @param bedNumber 床位号
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<ResidentHistory> getResidentHistoryList(String name, String bedNumber, Integer page, Integer pageSize);
}