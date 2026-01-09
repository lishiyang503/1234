package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.ResidentHistoryMapper;
import com.demo.pojo.ResidentHistory;
import com.demo.service.ResidentHistoryService;
import org.springframework.stereotype.Service;

/**
 * 入住历史表服务实现类
 */
@Service
public class ResidentHistoryServiceImpl extends ServiceImpl<ResidentHistoryMapper, ResidentHistory> implements ResidentHistoryService {

    /**
     * 获取入住历史列表（支持分页、姓名和床位号搜索）
     * @param name 姓名
     * @param bedNumber 床位号
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public IPage<ResidentHistory> getResidentHistoryList(String name, String bedNumber, Integer page, Integer pageSize) {
        Page<ResidentHistory> historyPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<ResidentHistory> wrapper = new LambdaQueryWrapper<>();
        
        // 根据姓名模糊查询
        if (name != null && !name.isEmpty()) {
            wrapper.like(ResidentHistory::getResidentName, name);
        }
        
        // 根据床位号模糊查询
        if (bedNumber != null && !bedNumber.isEmpty()) {
            wrapper.like(ResidentHistory::getBedNumber, bedNumber);
        }
        
        // 按id降序排序
        wrapper.orderByDesc(ResidentHistory::getId);
        
        return this.page(historyPage, wrapper);
    }
}