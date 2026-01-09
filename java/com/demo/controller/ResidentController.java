package com.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.pojo.Resident;
import com.demo.pojo.ResidentHistory;
import com.demo.pojo.Bed;
import com.demo.pojo.HealthRecord;
import com.demo.service.ResidentService;
import com.demo.service.ResidentHistoryService;
import com.demo.service.BedService;
import com.demo.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resident")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResidentController {

    /**
     * 解析日期字符串为Date对象
     * @param dateStr 日期字符串
     * @return Date对象
     */
    private Date parseDate(Object dateStr) {
        if (dateStr == null) {
            return null;
        }
        
        if (dateStr instanceof Date) {
            return (Date) dateStr;
        }
        
        String dateString = dateStr.toString();
        if (dateString.isEmpty()) {
            return null;
        }
        
        // 支持多种日期格式
        String[] formats = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",  // ISO格式
            "yyyy-MM-dd'T'HH:mm:ss",        // ISO格式（无毫秒）
            "yyyy-MM-dd",                  // 简单日期格式
            "yyyy/MM/dd",                  // 斜杠分隔格式
            "MM/dd/yyyy"                   // 月/日/年格式
        };
        
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // 尝试下一种格式
            }
        }
        
        return null;
    }

    @Autowired
    private ResidentService residentService;
    
    @Autowired
    private ResidentHistoryService residentHistoryService;
    
    @Autowired
    private BedService bedService;
    
    @Autowired
    private HealthRecordService healthRecordService;

    /**
     * 获取入住登记列表（支持分页、姓名和身份证号搜索）
     */
    @GetMapping("/list")
    public Map<String, Object> getResidentList(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "idCard", defaultValue = "") String idCard,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<Resident> residentList = residentService.getResidentList(name, idCard, page, pageSize);
            
            // 转换为带有连续显示ID的格式
            List<Resident> records = residentList.getRecords();
            List<Map<String, Object>> formattedList = new ArrayList<>();
            for (int i = 0; i < records.size(); i++) {
                Resident resident = records.get(i);
                Map<String, Object> item = new HashMap<>();
                // 计算连续的显示ID
                int displayId = (page - 1) * pageSize + i + 1;
                item.put("id", displayId); // 显示ID
                item.put("actualId", resident.getId()); // 实际数据库ID
                item.put("name", resident.getName());
                item.put("idCard", resident.getIdCard());
                item.put("gender", resident.getGender());
                item.put("age", resident.getAge());
                item.put("phone", resident.getPhone());
                item.put("roomNumber", resident.getRoomNumber());
                item.put("bedNumber", resident.getBedNumber());
                item.put("entryDate", resident.getEntryDate());
                item.put("status", resident.getStatus());
                formattedList.add(item);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", formattedList);
            data.put("total", residentList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取入住登记列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取入住登记列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有入住登记列表
     */
    @GetMapping("/all")
    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Resident> residentList = residentService.getAll();
            result.put("success", true);
            result.put("data", residentList);
            result.put("message", "获取所有入住登记列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有入住登记列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID获取入住登记
     */
    @GetMapping("/get/{id}")
    public Map<String, Object> getById(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Resident resident = residentService.getById(id);
            if (resident != null) {
                result.put("success", true);
                result.put("data", resident);
                result.put("message", "获取入住登记成功");
            } else {
                result.put("success", false);
                result.put("message", "入住登记不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取入住登记失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 验证身份证号格式
     */
    private boolean validateIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        // 简单的身份证号格式验证（18位数字，最后一位可以是X）
        return idCard.matches("[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]");
    }
    
    /**
     * 验证手机号格式
     */
    private boolean validatePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }
        // 简单的手机号格式验证（11位数字，以1开头）
        return phone.matches("1[3-9]\\d{9}");
    }
    
    /**
     * 根据身份证号计算年龄
     */
    private int calculateAgeFromIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return 0;
        }
        try {
            // 从身份证号中提取出生年月（第7-14位）
            String birthDateStr = idCard.substring(6, 14);
            int birthYear = Integer.parseInt(birthDateStr.substring(0, 4));
            int birthMonth = Integer.parseInt(birthDateStr.substring(4, 6));
            int birthDay = Integer.parseInt(birthDateStr.substring(6, 8));
            
            // 获取当前日期
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentYear = cal.get(java.util.Calendar.YEAR);
            int currentMonth = cal.get(java.util.Calendar.MONTH) + 1; // 月份从0开始
            int currentDay = cal.get(java.util.Calendar.DAY_OF_MONTH);
            
            // 计算年龄
            int age = currentYear - birthYear;
            
            // 调整年龄（如果生日还没过）
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }
            
            return age;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * 添加入住登记
     */
    @PostMapping("/add")
    public Map<String, Object> addResident(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 验证身份证号格式
            String idCard = (String) data.get("idCard");
            if (idCard != null && !validateIdCard(idCard)) {
                result.put("success", false);
                result.put("message", "身份证号格式不正确，必须是18位有效证件号");
                return result;
            }
            
            // 验证手机号格式
            String phone = (String) data.get("phone");
            if (phone != null && !validatePhone(phone)) {
                result.put("success", false);
                result.put("message", "联系电话格式不正确，必须是11位有效手机号");
                return result;
            }
            
            // 验证紧急联系电话格式
            String emergencyPhone = (String) data.get("emergencyPhone");
            if (emergencyPhone != null && !validatePhone(emergencyPhone)) {
                result.put("success", false);
                result.put("message", "紧急联系电话格式不正确，必须是11位有效手机号");
                return result;
            }
            
            // 检查身份证号是否已存在
            LambdaQueryWrapper<Resident> idCardWrapper = new LambdaQueryWrapper<>();
            idCardWrapper.eq(Resident::getIdCard, idCard);
            Resident existingResident = residentService.getOne(idCardWrapper);
            if (existingResident != null) {
                result.put("success", false);
                result.put("message", "该身份证号已存在，请检查输入或联系管理员");
                return result;
            }
            
            // 解析入住信息
            Resident resident = new Resident();
            resident.setName((String) data.get("name"));
            resident.setIdCard(idCard);
            resident.setGender((String) data.get("gender"));
            
            // 从身份证号自动计算年龄
            int age = calculateAgeFromIdCard(idCard);
            resident.setAge(age);
            
            resident.setPhone(phone);
            resident.setEmergencyContact((String) data.get("emergencyContact"));
            resident.setEmergencyPhone(emergencyPhone);
            resident.setEntryDate(parseDate(data.get("entryDate")));
            resident.setRoomNumber((String) data.get("roomNumber"));
            resident.setBedNumber((String) data.get("bedNumber"));
            resident.setStatus((String) data.get("status"));
            
            // 保存入住信息
            boolean success = residentService.save(resident);
            if (success) {
                // 查找对应的床位（根据床位号和房间号）
                LambdaQueryWrapper<Bed> bedWrapper = new LambdaQueryWrapper<>();
                bedWrapper.eq(Bed::getBedNumber, resident.getBedNumber());
                Bed bed = bedService.getOne(bedWrapper);
                
                // 将床位设置为已占用状态
                if (bed != null) {
                    bed.setStatus(2); // 2表示已占用（与数据库一致）
                    bed.setResidentId(resident.getId());
                    bedService.updateById(bed);
                }
                
                // 处理健康信息 - 无论是否提供健康信息，都创建健康档案
                HealthRecord healthRecord = new HealthRecord();
                healthRecord.setResidentId(resident.getId());
                healthRecord.setResidentName(resident.getName());
                healthRecord.setRoomNumber(resident.getRoomNumber());
                healthRecord.setBedNumber(resident.getBedNumber());
                // 为必填字段设置默认值
                healthRecord.setMedicalHistory("");
                healthRecord.setAllergyHistory("");
                healthRecord.setBloodType("");
                healthRecord.setHeight(0.0);
                healthRecord.setWeight(0.0);
                healthRecord.setHealthNotes("");
                healthRecord.setUpdateTime(new Date());
                
                // 处理健康信息 - 支持多种格式
                // 格式1：健康信息嵌套在healthRecord对象中
                if (data.containsKey("healthRecord")) {
                    Map<String, Object> healthData = (Map<String, Object>) data.get("healthRecord");
                    // 只有当提供了具体值时才覆盖默认值
                    if (healthData.containsKey("medicalHistory")) {
                        healthRecord.setMedicalHistory((String) healthData.get("medicalHistory"));
                    }
                    if (healthData.containsKey("allergyHistory")) {
                        healthRecord.setAllergyHistory((String) healthData.get("allergyHistory"));
                    }
                    if (healthData.containsKey("bloodType")) {
                        healthRecord.setBloodType((String) healthData.get("bloodType"));
                    }
                    
                    // 处理身高和体重字段，支持字符串和数字类型
                    Object heightObj = healthData.get("height");
                    if (heightObj != null) {
                        if (heightObj instanceof Double) {
                            healthRecord.setHeight((Double) heightObj);
                        } else if (heightObj instanceof String) {
                            try {
                                healthRecord.setHeight(Double.parseDouble((String) heightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setHeight(0.0);
                            }
                        }
                    }
                    
                    Object weightObj = healthData.get("weight");
                    if (weightObj != null) {
                        if (weightObj instanceof Double) {
                            healthRecord.setWeight((Double) weightObj);
                        } else if (weightObj instanceof String) {
                            try {
                                healthRecord.setWeight(Double.parseDouble((String) weightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setWeight(0.0);
                            }
                        }
                    }
                    
                    if (healthData.containsKey("healthNotes")) {
                        healthRecord.setHealthNotes((String) healthData.get("healthNotes"));
                    }
                } else {
                    // 格式2：健康信息直接作为顶级字段传递
                    // 既往病史
                    if (data.containsKey("medicalHistory")) {
                        healthRecord.setMedicalHistory((String) data.get("medicalHistory"));
                    } else if (data.containsKey("既往病史")) {
                        healthRecord.setMedicalHistory((String) data.get("既往病史"));
                    }
                    
                    // 过敏史
                    if (data.containsKey("allergyHistory")) {
                        healthRecord.setAllergyHistory((String) data.get("allergyHistory"));
                    } else if (data.containsKey("过敏史")) {
                        healthRecord.setAllergyHistory((String) data.get("过敏史"));
                    }
                    
                    // 血型
                    if (data.containsKey("bloodType")) {
                        healthRecord.setBloodType((String) data.get("bloodType"));
                    } else if (data.containsKey("血型")) {
                        healthRecord.setBloodType((String) data.get("血型"));
                    }
                    
                    // 身高
                    if (data.containsKey("height")) {
                        Object heightObj = data.get("height");
                        if (heightObj instanceof Double) {
                            healthRecord.setHeight((Double) heightObj);
                        } else if (heightObj instanceof String) {
                            try {
                                healthRecord.setHeight(Double.parseDouble((String) heightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setHeight(0.0);
                            }
                        }
                    } else if (data.containsKey("身高")) {
                        Object heightObj = data.get("身高");
                        if (heightObj instanceof Double) {
                            healthRecord.setHeight((Double) heightObj);
                        } else if (heightObj instanceof String) {
                            try {
                                healthRecord.setHeight(Double.parseDouble((String) heightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setHeight(0.0);
                            }
                        }
                    }
                    
                    // 体重
                    if (data.containsKey("weight")) {
                        Object weightObj = data.get("weight");
                        if (weightObj instanceof Double) {
                            healthRecord.setWeight((Double) weightObj);
                        } else if (weightObj instanceof String) {
                            try {
                                healthRecord.setWeight(Double.parseDouble((String) weightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setWeight(0.0);
                            }
                        }
                    } else if (data.containsKey("体重")) {
                        Object weightObj = data.get("体重");
                        if (weightObj instanceof Double) {
                            healthRecord.setWeight((Double) weightObj);
                        } else if (weightObj instanceof String) {
                            try {
                                healthRecord.setWeight(Double.parseDouble((String) weightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setWeight(0.0);
                            }
                        }
                    }
                    
                    // 健康状况
                    if (data.containsKey("healthNotes")) {
                        healthRecord.setHealthNotes((String) data.get("healthNotes"));
                    } else if (data.containsKey("健康状况")) {
                        healthRecord.setHealthNotes((String) data.get("健康状况"));
                    }
                }
                
                // 保存健康档案
                healthRecordService.save(healthRecord);
                
                result.put("message", "添加入住登记成功");
            } else {
                result.put("message", "添加入住登记失败");
            }
            result.put("success", success);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加入住登记失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新入住登记
     */
    @PutMapping("/update")
    public Map<String, Object> updateResident(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 解析入住信息
            Integer residentId = (Integer) data.get("id");
            if (residentId == null) {
                result.put("success", false);
                result.put("message", "入住登记ID不能为空");
                return result;
            }
            
            // 获取原始入住信息
            Resident originalResident = residentService.getById(residentId);
            if (originalResident == null) {
                result.put("success", false);
                result.put("message", "入住登记不存在");
                return result;
            }
            
            // 验证身份证号格式
            String idCard = (String) data.get("idCard");
            if (idCard != null && !validateIdCard(idCard)) {
                result.put("success", false);
                result.put("message", "身份证号格式不正确，必须是18位有效证件号");
                return result;
            }
            
            // 验证手机号格式
            String phone = (String) data.get("phone");
            if (phone != null && !validatePhone(phone)) {
                result.put("success", false);
                result.put("message", "联系电话格式不正确，必须是11位有效手机号");
                return result;
            }
            
            // 验证紧急联系电话格式
            String emergencyPhone = (String) data.get("emergencyPhone");
            if (emergencyPhone != null && !validatePhone(emergencyPhone)) {
                result.put("success", false);
                result.put("message", "紧急联系电话格式不正确，必须是11位有效手机号");
                return result;
            }
            
            // 检查身份证号是否已被其他老人使用
            LambdaQueryWrapper<Resident> idCardWrapper = new LambdaQueryWrapper<>();
            idCardWrapper.eq(Resident::getIdCard, idCard);
            idCardWrapper.ne(Resident::getId, residentId); // 排除当前老人
            Resident existingResident = residentService.getOne(idCardWrapper);
            if (existingResident != null) {
                result.put("success", false);
                result.put("message", "该身份证号已被其他老人使用，请检查输入或联系管理员");
                return result;
            }
            
            // 更新入住信息
            Resident resident = new Resident();
            resident.setId(residentId);
            resident.setName((String) data.get("name"));
            resident.setIdCard(idCard);
            resident.setGender((String) data.get("gender"));
            
            // 从身份证号自动计算年龄
            int age = calculateAgeFromIdCard(idCard);
            resident.setAge(age);
            
            resident.setPhone(phone);
            resident.setEmergencyContact((String) data.get("emergencyContact"));
            resident.setEmergencyPhone(emergencyPhone);
            resident.setEntryDate(parseDate(data.get("entryDate")));
            resident.setExitDate(parseDate(data.get("exitDate")));
            resident.setRoomNumber((String) data.get("roomNumber"));
            resident.setBedNumber((String) data.get("bedNumber"));
            resident.setStatus((String) data.get("status"));
            
            // 身份证号唯一性检查已在方法开头完成，此处不再重复检查
            
            boolean success = residentService.updateById(resident);
            
            if (success) {
                // 检查房间号或床位号是否发生变化
                if (!originalResident.getRoomNumber().equals(resident.getRoomNumber()) || 
                    !originalResident.getBedNumber().equals(resident.getBedNumber())) {
                    
                    // 1. 查找老人当前占用的所有床位
                    LambdaQueryWrapper<Bed> oldBedWrapper = new LambdaQueryWrapper<>();
                    oldBedWrapper.eq(Bed::getResidentId, resident.getId());
                    List<Bed> oldBeds = bedService.list(oldBedWrapper);
                    
                    // 2. 将原床位设置为可用状态
                    for (Bed oldBed : oldBeds) {
                        oldBed.setStatus(0); // 0表示空闲（与数据库一致）
                        oldBed.setResidentId(null);
                        bedService.updateById(oldBed);
                    }
                    
                    // 3. 查找新床位（根据床位号）
                    LambdaQueryWrapper<Bed> newBedWrapper = new LambdaQueryWrapper<>();
                    newBedWrapper.eq(Bed::getBedNumber, resident.getBedNumber());
                    Bed newBed = bedService.getOne(newBedWrapper);
                    
                    // 4. 将新床位设置为已占用状态
                    if (newBed != null) {
                        newBed.setStatus(2); // 2表示已占用（与数据库一致）
                        newBed.setResidentId(resident.getId());
                        bedService.updateById(newBed);
                    }
                }
                
                // 处理健康信息
                // 查找现有的健康档案
                LambdaQueryWrapper<HealthRecord> healthWrapper = new LambdaQueryWrapper<>();
                healthWrapper.eq(HealthRecord::getResidentId, residentId);
                HealthRecord existingHealthRecord = healthRecordService.getOne(healthWrapper);
                
                if (existingHealthRecord != null) {
                    // 更新现有健康档案
                    existingHealthRecord.setResidentName(resident.getName());
                    existingHealthRecord.setRoomNumber(resident.getRoomNumber());
                    existingHealthRecord.setBedNumber(resident.getBedNumber());
                    existingHealthRecord.setMedicalHistory((String) data.get("medicalHistory"));
                    existingHealthRecord.setAllergyHistory((String) data.get("allergyHistory"));
                    existingHealthRecord.setBloodType((String) data.get("bloodType"));
                    
                    // 处理身高和体重字段，支持字符串和数字类型
                    Object heightObj = data.get("height");
                    if (heightObj != null) {
                        if (heightObj instanceof Double) {
                            existingHealthRecord.setHeight((Double) heightObj);
                        } else if (heightObj instanceof String) {
                            try {
                                existingHealthRecord.setHeight(Double.parseDouble((String) heightObj));
                            } catch (NumberFormatException e) {
                                existingHealthRecord.setHeight(0.0);
                            }
                        }
                    }
                    
                    Object weightObj = data.get("weight");
                    if (weightObj != null) {
                        if (weightObj instanceof Double) {
                            existingHealthRecord.setWeight((Double) weightObj);
                        } else if (weightObj instanceof String) {
                            try {
                                existingHealthRecord.setWeight(Double.parseDouble((String) weightObj));
                            } catch (NumberFormatException e) {
                                existingHealthRecord.setWeight(0.0);
                            }
                        }
                    }
                    
                    existingHealthRecord.setHealthNotes((String) data.get("healthStatus"));
                    existingHealthRecord.setUpdateTime(new Date());
                    
                    healthRecordService.updateById(existingHealthRecord);
                } else {
                    // 创建新健康档案
                    HealthRecord healthRecord = new HealthRecord();
                    healthRecord.setResidentId(resident.getId());
                    healthRecord.setResidentName(resident.getName());
                    healthRecord.setRoomNumber(resident.getRoomNumber());
                    healthRecord.setBedNumber(resident.getBedNumber());
                    healthRecord.setMedicalHistory((String) data.get("medicalHistory"));
                    healthRecord.setAllergyHistory((String) data.get("allergyHistory"));
                    healthRecord.setBloodType((String) data.get("bloodType"));
                    
                    // 处理身高和体重字段，支持字符串和数字类型
                    Object heightObj = data.get("height");
                    if (heightObj != null) {
                        if (heightObj instanceof Double) {
                            healthRecord.setHeight((Double) heightObj);
                        } else if (heightObj instanceof String) {
                            try {
                                healthRecord.setHeight(Double.parseDouble((String) heightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setHeight(0.0);
                            }
                        }
                    }
                    
                    Object weightObj = data.get("weight");
                    if (weightObj != null) {
                        if (weightObj instanceof Double) {
                            healthRecord.setWeight((Double) weightObj);
                        } else if (weightObj instanceof String) {
                            try {
                                healthRecord.setWeight(Double.parseDouble((String) weightObj));
                            } catch (NumberFormatException e) {
                                healthRecord.setWeight(0.0);
                            }
                        }
                    }
                    
                    healthRecord.setHealthNotes((String) data.get("healthStatus"));
                    healthRecord.setUpdateTime(new Date());
                    
                    healthRecordService.save(healthRecord);
                }
                
                result.put("message", "更新入住登记成功");
                result.put("success", true);
            } else {
                result.put("message", "更新入住登记失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新入住登记失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除入住登记
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteResident(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 先获取入住登记信息，以便更新床位状态
            Resident resident = residentService.getById(id);
            if (resident != null) {
                // 查找对应的床位（根据床位号）
                LambdaQueryWrapper<Bed> bedWrapper = new LambdaQueryWrapper<>();
                bedWrapper.eq(Bed::getBedNumber, resident.getBedNumber());
                Bed bed = bedService.getOne(bedWrapper);
                
                // 将床位设置为空闲状态
                if (bed != null) {
                    bed.setStatus(1); // 1表示空闲（与数据库一致）
                    bed.setResidentId(null);
                    bedService.updateById(bed);
                }
            }
            
            // 删除入住登记
            boolean success = residentService.removeById(id);
            result.put("success", success);
            if (success) {
                result.put("message", "删除入住登记成功");
            } else {
                result.put("message", "删除入住登记失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除入住登记失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 办理退房
     */
    @PutMapping("/checkout/{id}")
    public Map<String, Object> checkoutResident(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Resident resident = residentService.getById(id);
            if (resident != null) {
                // 设置退房状态
                resident.setStatus("退房");
                resident.setExitDate(new Date());
                boolean success = residentService.updateById(resident);
                result.put("success", success);
                if (success) {
                    result.put("message", "办理退房成功");
                } else {
                    result.put("message", "办理退房失败");
                }
            } else {
                result.put("success", false);
                result.put("message", "入住登记不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "办理退房失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据老人ID获取健康信息
     */
    @GetMapping("/health/{residentId}")
    public Map<String, Object> getHealthByResidentId(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 查找老人的健康档案
            LambdaQueryWrapper<HealthRecord> healthWrapper = new LambdaQueryWrapper<>();
            healthWrapper.eq(HealthRecord::getResidentId, residentId);
            HealthRecord healthRecord = healthRecordService.getOne(healthWrapper);
            
            result.put("success", true);
            result.put("data", healthRecord);
            result.put("message", "获取健康信息成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取健康信息失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取入住历史列表（支持分页、姓名和床位号搜索）
     */
    @GetMapping("/history")
    public Map<String, Object> getResidentHistoryList(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "bedNumber", defaultValue = "") String bedNumber,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<ResidentHistory> historyList = residentHistoryService.getResidentHistoryList(name, bedNumber, page, pageSize);
            Map<String, Object> data = new HashMap<>();
            data.put("list", historyList.getRecords());
            data.put("total", historyList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取入住历史列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取入住历史列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 老人出院操作
     */
    @PostMapping("/discharge")
    public Map<String, Object> dischargeResident(
            @RequestParam(name = "residentId") Integer residentId,
            @RequestParam(name = "reason") String reason,
            @RequestParam(name = "operator") String operator) {
        Map<String, Object> result = new HashMap<>();
        try {
            residentService.dischargeResident(residentId, reason, operator);
            result.put("success", true);
            result.put("message", "出院操作成功");
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "出院操作失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据身份证号查询老人信息
     */
    @GetMapping("/getByIdCard")
    public Map<String, Object> getResidentByIdCard(
            @RequestParam(name = "idCard") String idCard) {
        Map<String, Object> result = new HashMap<>();
        try {
            Resident resident = residentService.getByidCard(idCard);
            result.put("success", true);
            result.put("data", resident);
            result.put("message", "获取老人信息成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取老人信息失败：" + e.getMessage());
        }
        return result;
    }
}