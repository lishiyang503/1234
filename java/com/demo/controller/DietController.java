package com.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.DietRequirement;
import com.demo.service.DietRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diet")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DietController {

    @Autowired
    private DietRequirementService dietRequirementService;

    /**
     * 获取膳食需求列表（支持分页、名称和膳食类型筛选）
     */
    @GetMapping("/list")
    public Map<String, Object> getDietList(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "dietType", defaultValue = "") String dietType,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<DietRequirement> dietListPage = dietRequirementService.getDietList(name, dietType, page, pageSize);
            
            // 生成连续的显示序号
            int startIndex = (int) ((page - 1) * pageSize + 1);
            int index = startIndex;
            for (DietRequirement diet : dietListPage.getRecords()) {
                diet.setDisplayId(index++);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", dietListPage.getRecords());
            data.put("total", dietListPage.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取膳食需求列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取膳食需求列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有膳食需求列表
     */
    @GetMapping("/all")
    public Map<String, Object> getAll(@RequestParam(required = false) String dietType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DietRequirement> dietList = dietRequirementService.getAll(dietType);
            result.put("success", true);
            result.put("data", dietList);
            result.put("message", "获取所有膳食需求列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有膳食需求列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID获取膳食需求
     */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DietRequirement diet = dietRequirementService.getById(id);
            if (diet != null) {
                result.put("success", true);
                result.put("data", diet);
                result.put("message", "获取膳食需求成功");
            } else {
                result.put("success", false);
                result.put("message", "膳食需求不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取膳食需求失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加膳食需求
     */
    @PostMapping("/add")
    public Map<String, Object> addDiet(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 创建膳食需求对象
            DietRequirement diet = new DietRequirement();
            
            // 填充字段
            // 老人ID（必填）
            if (data.containsKey("residentId")) {
                Object residentIdObj = data.get("residentId");
                if (residentIdObj instanceof Integer) {
                    diet.setResidentId((Integer) residentIdObj);
                } else if (residentIdObj instanceof String) {
                    try {
                        diet.setResidentId(Integer.parseInt((String) residentIdObj));
                    } catch (NumberFormatException e) {
                        result.put("success", false);
                        result.put("message", "老人ID格式错误");
                        return result;
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "老人ID格式错误");
                    return result;
                }
            } else {
                result.put("success", false);
                result.put("message", "老人ID不能为空");
                return result;
            }
            
            // 老人姓名（必填）
            if (data.containsKey("residentName")) {
                Object residentNameObj = data.get("residentName");
                if (residentNameObj instanceof List) {
                    // 如果是数组，取第一个元素
                    List<?> residentNameList = (List<?>) residentNameObj;
                    if (!residentNameList.isEmpty()) {
                        diet.setResidentName(residentNameList.get(0).toString());
                    } else {
                        result.put("success", false);
                        result.put("message", "老人姓名不能为空");
                        return result;
                    }
                } else {
                    String residentName = residentNameObj.toString();
                    if (residentName.isEmpty()) {
                        result.put("success", false);
                        result.put("message", "老人姓名不能为空");
                        return result;
                    }
                    diet.setResidentName(residentName);
                }
            } else {
                result.put("success", false);
                result.put("message", "老人姓名不能为空");
                return result;
            }
            
            // 房间号（必填）
            if (data.containsKey("roomNumber")) {
                Object roomNumberObj = data.get("roomNumber");
                if (roomNumberObj instanceof List) {
                    // 如果是数组，取第一个元素
                    List<?> roomNumberList = (List<?>) roomNumberObj;
                    if (!roomNumberList.isEmpty()) {
                        diet.setRoomNumber(roomNumberList.get(0).toString());
                    } else {
                        result.put("success", false);
                        result.put("message", "房间号不能为空");
                        return result;
                    }
                } else {
                    String roomNumber = roomNumberObj.toString();
                    if (roomNumber.isEmpty()) {
                        result.put("success", false);
                        result.put("message", "房间号不能为空");
                        return result;
                    }
                    diet.setRoomNumber(roomNumber);
                }
            } else {
                result.put("success", false);
                result.put("message", "房间号不能为空");
                return result;
            }
            
            // 床位号（必填）
            if (data.containsKey("bedNumber")) {
                Object bedNumberObj = data.get("bedNumber");
                if (bedNumberObj instanceof List) {
                    // 如果是数组，取第一个元素
                    List<?> bedNumberList = (List<?>) bedNumberObj;
                    if (!bedNumberList.isEmpty()) {
                        diet.setBedNumber(bedNumberList.get(0).toString());
                    } else {
                        result.put("success", false);
                        result.put("message", "床位号不能为空");
                        return result;
                    }
                } else {
                    String bedNumber = bedNumberObj.toString();
                    if (bedNumber.isEmpty()) {
                        result.put("success", false);
                        result.put("message", "床位号不能为空");
                        return result;
                    }
                    diet.setBedNumber(bedNumber);
                }
            } else {
                result.put("success", false);
                result.put("message", "床位号不能为空");
                return result;
            }
            
            // 饮食类型（必填）
            if (data.containsKey("dietType")) {
                Object dietTypeObj = data.get("dietType");
                if (dietTypeObj instanceof List) {
                    // 如果是数组，取第一个元素
                    List<?> dietTypeList = (List<?>) dietTypeObj;
                    if (!dietTypeList.isEmpty()) {
                        diet.setDietType(dietTypeList.get(0).toString());
                    } else {
                        result.put("success", false);
                        result.put("message", "饮食类型不能为空");
                        return result;
                    }
                } else {
                    String dietType = dietTypeObj.toString();
                    if (dietType.isEmpty()) {
                        result.put("success", false);
                        result.put("message", "饮食类型不能为空");
                        return result;
                    }
                    diet.setDietType(dietType);
                }
            } else {
                result.put("success", false);
                result.put("message", "饮食类型不能为空");
                return result;
            }
            
            // 详细说明（必填）
            if (data.containsKey("description")) {
                Object descriptionObj = data.get("description");
                if (descriptionObj instanceof List) {
                    // 如果是数组，取第一个元素
                    List<?> descriptionList = (List<?>) descriptionObj;
                    if (!descriptionList.isEmpty()) {
                        diet.setDescription(descriptionList.get(0).toString());
                    } else {
                        result.put("success", false);
                        result.put("message", "详细说明不能为空");
                        return result;
                    }
                } else {
                    String description = descriptionObj.toString();
                    if (description.isEmpty()) {
                        result.put("success", false);
                        result.put("message", "详细说明不能为空");
                        return result;
                    }
                    diet.setDescription(description);
                }
            } else {
                result.put("success", false);
                result.put("message", "详细说明不能为空");
                return result;
            }
            
            // 设置创建和更新时间
            diet.setCreateTime(new Date());
            diet.setUpdateTime(new Date());
            
            // 保存数据
            boolean success = dietRequirementService.save(diet);
            result.put("success", success);
            if (success) {
                result.put("message", "添加膳食需求成功");
                result.put("data", diet);
            } else {
                result.put("message", "添加膳食需求失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加膳食需求失败：" + e.getMessage());
            // 打印异常信息，方便调试
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新膳食需求
     */
    @PutMapping("/update")
    public Map<String, Object> updateDiet(@RequestBody DietRequirement diet) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 先查询原有记录，保留不可修改的字段
            DietRequirement existingDiet = dietRequirementService.getById(diet.getId());
            if (existingDiet == null) {
                result.put("success", false);
                result.put("message", "膳食需求不存在");
                return result;
            }
            
            // 只允许更新饮食类型和详细说明，保留原有老人信息
            existingDiet.setDietType(diet.getDietType());
            existingDiet.setDescription(diet.getDescription());
            
            boolean success = dietRequirementService.updateById(existingDiet);
            result.put("success", success);
            if (success) {
                result.put("message", "更新膳食需求成功");
            } else {
                result.put("message", "更新膳食需求失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新膳食需求失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除膳食需求
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteDiet(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = dietRequirementService.removeById(id);
            result.put("success", success);
            if (success) {
                result.put("message", "删除膳食需求成功");
            } else {
                result.put("message", "删除膳食需求失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除膳食需求失败：" + e.getMessage());
        }
        return result;
    }
}