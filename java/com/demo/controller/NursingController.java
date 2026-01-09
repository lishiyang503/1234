package com.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.NursingContent;
import com.demo.pojo.NursingLevel;
import com.demo.pojo.NursingRecord;
import com.demo.service.NursingContentService;
import com.demo.service.NursingLevelService;
import com.demo.service.NursingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 护理管理Controller
 */
@RestController
@RequestMapping({"/nursing", "/api/nursing"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class NursingController {

    @Autowired
    private NursingLevelService nursingLevelService;
    
    @Autowired
    private NursingContentService nursingContentService;
    
    @Autowired
    private NursingRecordService nursingRecordService;

    // ================== 护理级别管理 ==================

    // 完全独立的路径设计，避免任何冲突
    
    /**
     * 获取护理级别选项（用于前端下拉选择，返回value-label格式）
     */
    @GetMapping("/nursing-level-options")
    public Map<String, Object> getNursingLevelOptions() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 直接返回固定的护理级别选项，确保value是正确的字符串格式
            List<Map<String, Object>> options = new ArrayList<>();
            
            // 添加基础护理
            Map<String, Object> basicOption = new HashMap<>();
            basicOption.put("value", "基础护理");
            basicOption.put("label", "基础护理");
            options.add(basicOption);
            
            // 添加中级护理
            Map<String, Object> intermediateOption = new HashMap<>();
            intermediateOption.put("value", "中级护理");
            intermediateOption.put("label", "中级护理");
            options.add(intermediateOption);
            
            // 添加高级护理
            Map<String, Object> advancedOption = new HashMap<>();
            advancedOption.put("value", "高级护理");
            advancedOption.put("label", "高级护理");
            options.add(advancedOption);
            
            // 添加VIP护理
            Map<String, Object> vipOption = new HashMap<>();
            vipOption.put("value", "VIP");
            vipOption.put("label", "VIP护理");
            options.add(vipOption);
            
            // 添加SVIP护理
            Map<String, Object> svipOption = new HashMap<>();
            svipOption.put("value", "SVIP");
            svipOption.put("label", "SVIP护理");
            options.add(svipOption);
            
            result.put("success", true);
            result.put("data", options);
            result.put("message", "获取护理级别选项成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理级别选项失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取所有护理级别（用于下拉选择）
     */
    @GetMapping("/nursing-level-all")
    public Map<String, Object> getAllNursingLevels() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NursingLevel> allLevels = nursingLevelService.getAll();
            result.put("success", true);
            result.put("data", allLevels);
            result.put("message", "获取所有护理级别成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有护理级别失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取护理级别列表（支持分页、名称搜索）
     */
    @GetMapping({"/level/list", "/levels"})
    public Map<String, Object> getNursingLevels(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<NursingLevel> nursingLevelList = nursingLevelService.getNursingLevelList(name, page, pageSize);
            Map<String, Object> data = new HashMap<>();
            data.put("list", nursingLevelList.getRecords());
            data.put("total", nursingLevelList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取护理级别列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理级别列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据ID获取护理级别
     */
    @GetMapping({"/level/{id}", "/levels/{id}"})
    public Map<String, Object> getNursingLevelById(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            NursingLevel nursingLevel = nursingLevelService.getById(id);
            if (nursingLevel != null) {
                result.put("success", true);
                result.put("data", nursingLevel);
                result.put("message", "获取护理级别成功");
            } else {
                result.put("success", false);
                result.put("message", "护理级别不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理级别失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加护理级别
     */
    @PostMapping({"/levels", "/level", "/level/add"})
    public Map<String, Object> addNursingLevel(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 转换请求参数
            NursingLevel nursingLevel = new NursingLevel();
            nursingLevel.setName((String) request.get("name"));

            // 直接使用字符串级别值
            nursingLevel.setLevel((String) request.get("level"));

            nursingLevel.setDescription((String) request.get("description"));

            // 处理基础费用 - 转换为BigDecimal
            Object baseCostObj = request.get("baseCost");
            BigDecimal baseCost;
            if (baseCostObj instanceof String) {
                baseCost = new BigDecimal((String) baseCostObj);
            } else if (baseCostObj instanceof Number) {
                baseCost = new BigDecimal(baseCostObj.toString());
            } else {
                baseCost = BigDecimal.ZERO;
            }
            nursingLevel.setBaseCost(baseCost);

            // 设置默认状态
            nursingLevel.setStatus(1);

            boolean success = nursingLevelService.save(nursingLevel);
            result.put("success", success);
            if (success) {
                result.put("message", "添加护理级别成功");
            } else {
                result.put("message", "添加护理级别失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加护理级别失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新护理级别
     */
    @PutMapping({"/levels/{id}", "/level/{id}"})
    public Map<String, Object> updateNursingLevel(@PathVariable("id") Integer id, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 转换请求参数
            NursingLevel nursingLevel = nursingLevelService.getById(id);
            if (nursingLevel == null) {
                result.put("success", false);
                result.put("message", "更新护理级别失败：护理级别不存在");
                return result;
            }
            
            // 更新级别名称
            if (request.containsKey("name")) {
                nursingLevel.setName((String) request.get("name"));
            }
            
            // 直接使用字符串级别值
            if (request.containsKey("level")) {
                nursingLevel.setLevel((String) request.get("level"));
            }
            
            // 更新级别描述
            if (request.containsKey("description")) {
                nursingLevel.setDescription((String) request.get("description"));
            }
            
            // 处理基础费用 - 转换为BigDecimal
            if (request.containsKey("baseCost")) {
                Object baseCostObj = request.get("baseCost");
                BigDecimal baseCost;
                if (baseCostObj instanceof String) {
                    baseCost = new BigDecimal((String) baseCostObj);
                } else if (baseCostObj instanceof Number) {
                    baseCost = new BigDecimal(baseCostObj.toString());
                } else {
                    baseCost = BigDecimal.ZERO;
                }
                nursingLevel.setBaseCost(baseCost);
            }
            
            // 更新状态
            if (request.containsKey("status")) {
                nursingLevel.setStatus(((Number) request.get("status")).intValue());
            }
            
            boolean success = nursingLevelService.updateById(nursingLevel);
            result.put("success", success);
            if (success) {
                result.put("message", "更新护理级别成功");
            } else {
                result.put("message", "更新护理级别失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新护理级别失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 更新护理级别（从请求体获取ID）
     */
    @PutMapping({"/levels/update", "/level/update"})
    public Map<String, Object> updateNursingLevel(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!request.containsKey("id")) {
                result.put("success", false);
                result.put("message", "更新护理级别失败：ID不能为空");
                return result;
            }
            
            Integer id = ((Number) request.get("id")).intValue();
            // 转换请求参数
            NursingLevel nursingLevel = nursingLevelService.getById(id);
            if (nursingLevel == null) {
                result.put("success", false);
                result.put("message", "更新护理级别失败：护理级别不存在");
                return result;
            }
            
            // 更新级别名称
            if (request.containsKey("name")) {
                nursingLevel.setName((String) request.get("name"));
            }
            
            // 直接使用字符串级别值
            if (request.containsKey("level")) {
                nursingLevel.setLevel((String) request.get("level"));
            }
            
            // 更新级别描述
            if (request.containsKey("description")) {
                nursingLevel.setDescription((String) request.get("description"));
            }
            
            // 处理基础费用 - 转换为BigDecimal
            if (request.containsKey("baseCost")) {
                Object baseCostObj = request.get("baseCost");
                BigDecimal baseCost;
                if (baseCostObj instanceof String) {
                    baseCost = new BigDecimal((String) baseCostObj);
                } else if (baseCostObj instanceof Number) {
                    baseCost = new BigDecimal(baseCostObj.toString());
                } else {
                    baseCost = BigDecimal.ZERO;
                }
                nursingLevel.setBaseCost(baseCost);
            }
            
            // 更新状态
            if (request.containsKey("status")) {
                nursingLevel.setStatus(((Number) request.get("status")).intValue());
            }
            
            boolean success = nursingLevelService.updateById(nursingLevel);
            result.put("success", success);
            if (success) {
                result.put("message", "更新护理级别成功");
            } else {
                result.put("message", "更新护理级别失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新护理级别失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除护理级别
     */
    @DeleteMapping({"/levels/{id}", "/level/{id}", "/level/delete/{id}"})
    public Map<String, Object> deleteNursingLevel(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 确保使用正确的删除方法
            boolean success = nursingLevelService.removeById(id);
            result.put("success", success);
            if (success) {
                result.put("message", "删除护理级别成功");
                // 确保返回的数据结构一致
                result.put("data", null);
            } else {
                result.put("message", "删除护理级别失败：未找到对应记录");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除护理级别失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    // ================== 护理内容管理 ==================

    /**
     * 辅助方法：从请求Map中暴力提取适用护理级别
     * 无论前端传的是 applicableLevels(数组), applicableLevel(字符串) 还是其他写法，都能识别
     */
    private String getApplicableLevelStr(Map<String, Object> request) {
        // 1. 优先尝试提取 applicableLevels (通常是前端传来的数组)
        if (request.containsKey("applicableLevels")) {
            Object obj = request.get("applicableLevels");
            System.out.println("DEBUG - 收到 applicableLevels: " + obj); // 调试日志
            
            if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                if (!list.isEmpty()) {
                    // 将 List ["1", "2"] 转换为 String "1,2"
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        sb.append(list.get(i).toString());
                        if (i < list.size() - 1) {
                            sb.append(",");
                        }
                    }
                    return sb.toString();
                }
                return ""; // 空数组返回空字符串
            }
        }

        // 2. 其次尝试 applicableLevel (单数)
        if (request.containsKey("applicableLevel")) {
            Object val = request.get("applicableLevel");
            if (val != null) return val.toString();
        }

        // 3. 再次尝试数据库字段名 applicable_level
        if (request.containsKey("applicable_level")) {
            Object val = request.get("applicable_level");
            if (val != null) return val.toString();
        }

        return null; // 未找到相关字段
    }

    /**
     * 获取护理内容列表
     */
    @GetMapping({"/contents", "/content/list"})
    public Map<String, Object> getNursingContents(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "category", required = false) Integer category,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<NursingContent> nursingContentList = nursingContentService.getNursingContentList(name, category, page, pageSize);
            
            // 转换护理内容数据，处理状态字段
            List<Map<String, Object>> transformedContents = new ArrayList<>();
            for (NursingContent content : nursingContentList.getRecords()) {
                Map<String, Object> transformedContent = new HashMap<>();
                transformedContent.put("id", content.getId());
                transformedContent.put("code", "NC" + String.format("%04d", content.getId())); // 生成默认代码
                transformedContent.put("name", content.getName());
                transformedContent.put("categoryId", content.getCategory());
                transformedContent.put("category_id", content.getCategory());
                transformedContent.put("status", content.getStatus() == 1 ? "ACTIVE" : "INACTIVE");
                transformedContent.put("description", content.getDescription());
                transformedContent.put("estimatedDuration", 30); // 默认30分钟
                transformedContent.put("estimated_duration", 30); // 默认30分钟
                transformedContent.put("frequency", "按需" ); // 默认按需
                transformedContent.put("procedure", "1. 准备工作\n2. 实施护理\n3. 记录结果"); // 默认流程
                transformedContent.put("notes", "请在专业人员指导下进行"); // 默认注意事项
                transformedContents.add(transformedContent);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", transformedContents);
            data.put("total", nursingContentList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("message", "获取护理内容列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理内容列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加护理内容
     */
    @PostMapping({"/contents", "/content", "/content/add"})
    public Map<String, Object> addNursingContent(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("DEBUG - 添加护理内容请求参数: " + request); // 调试日志

            NursingContent nursingContent = new NursingContent();
            
            if (request.containsKey("name")) nursingContent.setName((String) request.get("name"));
            if (request.containsKey("description")) nursingContent.setDescription((String) request.get("description"));
            
            // 处理分类
            if (request.containsKey("category")) {
                Object cat = request.get("category");
                if (cat instanceof Integer) nursingContent.setCategory((Integer) cat);
                else if (cat instanceof String) {
                    try { nursingContent.setCategory(Integer.parseInt((String) cat)); } 
                    catch (Exception e) { /* 忽略转换失败 */ }
                }
            }

            // 【关键修改】处理适用护理级别
            String levelStr = getApplicableLevelStr(request);
            if (levelStr != null) {
                nursingContent.setApplicableLevel(levelStr);
                System.out.println("DEBUG - 解析到的适用级别: " + levelStr);
            }

            // 处理时长字段
            if (request.containsKey("duration")) {
                Object duration = request.get("duration");
                if (duration instanceof Integer) {
                    nursingContent.setDuration((Integer) duration);
                } else if (duration instanceof String) {
                    try {
                        nursingContent.setDuration(Integer.parseInt((String) duration));
                    } catch (Exception e) {
                        // 忽略转换失败
                    }
                }
            }

            nursingContent.setStatus(1); // 默认启用

            boolean success = nursingContentService.save(nursingContent);
            result.put("success", success);
            result.put("message", success ? "添加成功" : "添加失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新护理内容 (带ID路径参数)
     */
    @PutMapping({"/contents/{id}", "/content/{id}"})
    public Map<String, Object> updateNursingContent(@PathVariable("id") Integer id, @RequestBody Map<String, Object> request) {
        // 将ID放入request，复用下面的逻辑
        request.put("id", id);
        return updateNursingContent(request);
    }
    
    /**
     * 更新护理内容 (统一处理逻辑)
     */
    @PutMapping({"/contents/update", "/content/update"})
    public Map<String, Object> updateNursingContent(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("DEBUG - 更新护理内容请求参数: " + request); // 调试日志

            Integer id = null;
            if (request.containsKey("id")) {
                Object idObj = request.get("id");
                if (idObj instanceof Integer) id = (Integer) idObj;
                else if (idObj instanceof String) id = Integer.parseInt((String) idObj);
            }

            if (id == null) {
                result.put("success", false);
                result.put("message", "ID不能为空");
                return result;
            }

            NursingContent nursingContent = nursingContentService.getById(id);
            if (nursingContent == null) {
                result.put("success", false);
                result.put("message", "记录不存在");
                return result;
            }

            // 更新常规字段
            if (request.containsKey("name")) nursingContent.setName((String) request.get("name"));
            if (request.containsKey("description")) nursingContent.setDescription((String) request.get("description"));
            
            if (request.containsKey("category")) {
                Object cat = request.get("category");
                if (cat instanceof Integer) nursingContent.setCategory((Integer) cat);
                else if (cat instanceof String) {
                    try { nursingContent.setCategory(Integer.parseInt((String) cat)); } 
                    catch (Exception e) {}
                }
            }

            // 【关键修改】处理适用护理级别
            String levelStr = getApplicableLevelStr(request);
            if (levelStr != null) {
                nursingContent.setApplicableLevel(levelStr);
                System.out.println("DEBUG - 更新时解析到的适用级别: " + levelStr);
            }

            // 处理时长字段
            if (request.containsKey("duration")) {
                Object duration = request.get("duration");
                if (duration instanceof Integer) {
                    nursingContent.setDuration((Integer) duration);
                } else if (duration instanceof String) {
                    try {
                        nursingContent.setDuration(Integer.parseInt((String) duration));
                    } catch (Exception e) {
                        // 忽略转换失败
                    }
                }
            }

            boolean success = nursingContentService.updateById(nursingContent);
            result.put("success", success);
            result.put("message", success ? "更新成功" : "更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除护理内容
     */
    @DeleteMapping({"/contents/{id}", "/content/{id}", "/content/delete/{id}"})
    public Map<String, Object> deleteNursingContent(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = nursingContentService.removeById(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }

    // ================== 护理记录管理 ==================

    /**
     * 获取护理记录列表（支持分页、老人姓名和完成状态搜索）
     */
    @GetMapping({"/records", "/record/list"})
    public Map<String, Object> getNursingRecords(
            @RequestParam(name = "residentName", defaultValue = "") String residentName,
            @RequestParam(name = "completed", required = false) Integer completed,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<NursingRecord> nursingRecordList = nursingRecordService.getNursingRecordList(residentName, completed, page, pageSize);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", nursingRecordList.getRecords());
            data.put("total", nursingRecordList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取护理记录列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理记录列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID获取护理记录
     */
    @GetMapping({"/records/{id}", "/record/{id}"})
    public Map<String, Object> getNursingRecordById(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            NursingRecord nursingRecord = nursingRecordService.getById(id);
            if (nursingRecord != null) {
                result.put("success", true);
                result.put("data", nursingRecord);
                result.put("message", "获取护理记录成功");
            } else {
                result.put("success", false);
                result.put("message", "护理记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加护理记录
     */
    @PostMapping({"/records", "/record"})
    public Map<String, Object> addNursingRecord(@RequestBody NursingRecord nursingRecord) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = nursingRecordService.save(nursingRecord);
            result.put("success", success);
            if (success) {
                result.put("message", "添加护理记录成功");
            } else {
                result.put("message", "添加护理记录失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加护理记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新护理记录（支持带ID路径参数和不带路径参数两种方式）
     */
    @PutMapping({"/records/{id}", "/record/{id}"})
    public Map<String, Object> updateNursingRecord(@PathVariable("id") Integer id, @RequestBody NursingRecord nursingRecord) {
        Map<String, Object> result = new HashMap<>();
        try {
            nursingRecord.setId(id);
            boolean success = nursingRecordService.updateById(nursingRecord);
            result.put("success", success);
            if (success) {
                result.put("message", "更新护理记录成功");
            } else {
                result.put("message", "更新护理记录失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新护理记录失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 更新护理记录（从请求体获取ID）
     */
    @PutMapping({"/records/update", "/record/update"})
    public Map<String, Object> updateNursingRecord(@RequestBody NursingRecord nursingRecord) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (nursingRecord.getId() == null) {
                result.put("success", false);
                result.put("message", "更新护理记录失败：ID不能为空");
                return result;
            }
            boolean success = nursingRecordService.updateById(nursingRecord);
            result.put("success", success);
            if (success) {
                result.put("message", "更新护理记录成功");
            } else {
                result.put("message", "更新护理记录失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新护理记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除护理记录
     */
    @DeleteMapping({"/records/{id}", "/record/{id}"})
    public Map<String, Object> deleteNursingRecord(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = nursingRecordService.removeById(id);
            result.put("success", success);
            if (success) {
                result.put("message", "删除护理记录成功");
            } else {
                result.put("message", "删除护理记录失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除护理记录失败：" + e.getMessage());
        }
        return result;
    }
}