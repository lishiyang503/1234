package com.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.BloodSugar;
import com.demo.pojo.HealthRecord;
import com.demo.service.BloodSugarService;
import com.demo.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    @Autowired
    private HealthRecordService healthRecordService;
    
    @Autowired
    private BloodSugarService bloodSugarService;

    /**
     * 获取健康档案列表（支持分页、姓名搜索）
     */
    @GetMapping("/list")
    public Map<String, Object> getHealthRecordList(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<HealthRecord> healthList = healthRecordService.getHealthRecordList(name, page, pageSize);
            Map<String, Object> data = new HashMap<>();
            data.put("list", healthList.getRecords());
            data.put("total", healthList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取健康档案列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取健康档案列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有健康档案列表
     */
    @GetMapping("/all")
    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<HealthRecord> healthList = healthRecordService.getAll();
            result.put("success", true);
            result.put("data", healthList);
            result.put("message", "获取所有健康档案列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有健康档案列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID获取健康档案
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> getById(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            HealthRecord healthRecord = healthRecordService.getById(id);
            if (healthRecord != null) {
                result.put("success", true);
                result.put("data", healthRecord);
                result.put("message", "获取健康档案成功");
            } else {
                result.put("success", false);
                result.put("message", "健康档案不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取健康档案失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加健康档案
     */
    @PostMapping("/add")
    public Map<String, Object> addHealthRecord(@RequestBody HealthRecord healthRecord) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = healthRecordService.save(healthRecord);
            result.put("success", success);
            if (success) {
                result.put("message", "添加健康档案成功");
            } else {
                result.put("message", "添加健康档案失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加健康档案失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新健康档案 - 支持两个端点路径
     */
    @PutMapping({"", "/update"})
    public Map<String, Object> updateHealthRecord(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取健康档案ID
            Integer id = null;
            if (data.containsKey("id")) {
                Object idObj = data.get("id");
                if (idObj instanceof Integer) {
                    id = (Integer) idObj;
                } else if (idObj instanceof String) {
                    try {
                        id = Integer.parseInt((String) idObj);
                    } catch (NumberFormatException e) {
                        result.put("success", false);
                        result.put("message", "健康档案ID格式错误");
                        return result;
                    }
                }
            }
            
            if (id == null) {
                result.put("success", false);
                result.put("message", "健康档案ID不能为空");
                return result;
            }
            
            // 查询原有健康档案，保留不可修改的字段
            HealthRecord existingRecord = healthRecordService.getById(id);
            if (existingRecord == null) {
                result.put("success", false);
                result.put("message", "健康档案不存在");
                return result;
            }
            
            // 创建更新对象，保留不可修改的字段
            HealthRecord healthRecord = new HealthRecord();
            healthRecord.setId(id);
            // 保留原有字段（不可修改）
            healthRecord.setResidentId(existingRecord.getResidentId());
            healthRecord.setResidentName(existingRecord.getResidentName());
            healthRecord.setRoomNumber(existingRecord.getRoomNumber());
            healthRecord.setBedNumber(existingRecord.getBedNumber());
            
            // 忽略前端发送的床位号和房间号字段，始终使用现有值
            // 这样即使前端发送了格式不正确的床位号，也不会影响后端处理
            
            // 更新可修改的字段
            // 血型
            if (data.containsKey("bloodType")) {
                Object bloodTypeObj = data.get("bloodType");
                if (bloodTypeObj != null) {
                    healthRecord.setBloodType(bloodTypeObj.toString());
                }
            }
            
            // 身高
            if (data.containsKey("height")) {
                Object heightObj = data.get("height");
                if (heightObj != null) {
                    if (heightObj instanceof Double) {
                        healthRecord.setHeight((Double) heightObj);
                    } else if (heightObj instanceof Integer) {
                        healthRecord.setHeight(((Integer) heightObj).doubleValue());
                    } else if (heightObj instanceof String) {
                        try {
                            healthRecord.setHeight(Double.parseDouble((String) heightObj));
                        } catch (NumberFormatException e) {
                            result.put("success", false);
                            result.put("message", "身高格式错误，请输入数字");
                            return result;
                        }
                    }
                }
            }
            
            // 体重
            if (data.containsKey("weight")) {
                Object weightObj = data.get("weight");
                if (weightObj != null) {
                    if (weightObj instanceof Double) {
                        healthRecord.setWeight((Double) weightObj);
                    } else if (weightObj instanceof Integer) {
                        healthRecord.setWeight(((Integer) weightObj).doubleValue());
                    } else if (weightObj instanceof String) {
                        try {
                            healthRecord.setWeight(Double.parseDouble((String) weightObj));
                        } catch (NumberFormatException e) {
                            result.put("success", false);
                            result.put("message", "体重格式错误，请输入数字");
                            return result;
                        }
                    }
                }
            }
            
            // 既往病史
            if (data.containsKey("medicalHistory")) {
                Object medicalHistoryObj = data.get("medicalHistory");
                if (medicalHistoryObj != null) {
                    healthRecord.setMedicalHistory(medicalHistoryObj.toString());
                }
            }
            
            // 过敏史
            if (data.containsKey("allergyHistory")) {
                Object allergyHistoryObj = data.get("allergyHistory");
                if (allergyHistoryObj != null) {
                    healthRecord.setAllergyHistory(allergyHistoryObj.toString());
                }
            }
            
            // 健康备注
            if (data.containsKey("healthNotes")) {
                Object healthNotesObj = data.get("healthNotes");
                if (healthNotesObj != null) {
                    healthRecord.setHealthNotes(healthNotesObj.toString());
                }
            }
            
            // 更新时间
            healthRecord.setUpdateTime(new Date());
            
            // 保存更新
            boolean success = healthRecordService.updateById(healthRecord);
            result.put("success", success);
            if (success) {
                result.put("message", "更新健康档案成功");
                result.put("data", healthRecord);
            } else {
                result.put("message", "更新健康档案失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新健康档案失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除健康档案
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteHealthRecord(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = healthRecordService.removeById(id);
            result.put("success", success);
            if (success) {
                result.put("message", "删除健康档案成功");
            } else {
                result.put("message", "删除健康档案失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除健康档案失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指定老人最新的血糖数据
     * @param residentId 老人ID
     * @return 血糖对象
     */
    @GetMapping("/blood-sugar/latest/{residentId}")
    public Map<String, Object> getLatestBloodSugar(@PathVariable Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            BloodSugar bloodSugar = bloodSugarService.getLatestByResidentId(residentId);
            if (bloodSugar != null) {
                result.put("success", true);
                result.put("message", "获取血糖数据成功");
                result.put("data", bloodSugar);
            } else {
                result.put("success", false);
                result.put("message", "暂无该老人的血糖记录");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取血糖数据失败：" + e.getMessage());
        }
        return result;
    }
}