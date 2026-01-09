package com.demo.controller;

import com.demo.pojo.Result;
import com.demo.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/visualization")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VisualizationController {

    @Autowired
    private ResidentService residentService;

    // 模拟实时位置数据 (Mock 3D Coordinates)
    @GetMapping("/locations")
    public Result<List<Map<String, Object>>> getRealTimeLocations() {
        // 获取所有在住老人，实际项目中应该从Redis或硬件接口获取坐标
        // 这里我们针对特定的四个人做模拟运动
        String[] targetNames = {"张卫国", "李牛花", "赵桂兰", "唐莹"};
        List<Map<String, Object>> liveData = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < targetNames.length; i++) {
            Map<String, Object> person = new HashMap<>();
            person.put("name", targetNames[i]);
            person.put("id", i + 1); // 模拟ID
            
            // 模拟坐标 (基于颐养中心中心点 0,0)
            // 简单的随机漫步算法
            double baseX = (i % 2 == 0 ? -10 : 10) * (i < 2 ? 1 : 1.5); // 分散在不同区域
            double baseZ = (i < 2 ? -5 : 5);
            
            // 添加微小的随机移动，模拟实时走动
            double currentX = baseX + (random.nextDouble() * 2 - 1);
            double currentZ = baseZ + (random.nextDouble() * 2 - 1);

            person.put("x", currentX);
            person.put("y", 0); // 地面高度
            person.put("z", currentZ);
            person.put("room", "10" + (i + 1));
            person.put("status", random.nextInt(10) > 8 ? "warning" : "normal"); // 偶尔模拟异常状态
            person.put("nursingLevel", i == 2 ? "特级护理" : "一级护理"); // 示例
            
            liveData.add(person);
        }
        return Result.success(liveData);
    }
}