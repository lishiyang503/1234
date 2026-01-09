package com.demo.controller;

import com.demo.pojo.BloodPressure;
import com.demo.pojo.BloodSugar;
import com.demo.pojo.HeartRate;
import com.demo.pojo.Resident;
import com.demo.service.BloodPressureService;
import com.demo.service.BloodSugarService;
import com.demo.service.HeartRateService;
import com.demo.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
     * 健康监测控制器
     */
@RestController
@RequestMapping("/health-monitoring")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthMonitoringController {
     

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BloodPressureService bloodPressureService;

    @Autowired
    private BloodSugarService bloodSugarService;

    @Autowired
    private HeartRateService heartRateService;

    @Autowired
    private ResidentService residentService;
    
    // 初始化居民数据，确保唐莹和李牛花的记录存在
    @PostConstruct
    public void initResidents() {
        try {
            System.out.println("=== 初始化居民数据 ===");
            
            // 先处理李牛花的居民记录
            String checkLiSql = "SELECT COUNT(*) FROM residents WHERE id = 6";
            Integer liCount = jdbcTemplate.queryForObject(checkLiSql, Integer.class);
            
            if (liCount == 0) {
                // 创建李牛花记录 (resident_id = 6)，添加id_card字段
                String insertLiSql = "INSERT INTO residents (id, name, gender, age, phone, id_card, emergency_contact, emergency_phone, entry_date, room_number, bed_number, status, create_time, update_time) VALUES (6, '李牛花', '女', 75, '13800138006', '110101195001011234', '李小花', '13900139006', NOW(), '301', '301-A', '入住', NOW(), NOW())";
                jdbcTemplate.update(insertLiSql);
                System.out.println("创建李牛花记录成功");
            } else {
                // 更新李牛花记录
                String updateLiSql = "UPDATE residents SET name = '李牛花', gender = '女', age = 75, phone = '13800138006', id_card = '110101195001011234', emergency_contact = '李小花', emergency_phone = '13900139006', room_number = '301', bed_number = '301-A', status = '入住', update_time = NOW() WHERE id = 6";
                jdbcTemplate.update(updateLiSql);
                System.out.println("更新李牛花记录成功");
            }
            
            // 处理唐莹的居民记录
            String checkTangSql = "SELECT COUNT(*) FROM residents WHERE id = 7";
            Integer tangCount = jdbcTemplate.queryForObject(checkTangSql, Integer.class);
            
            if (tangCount == 0) {
                // 创建唐莹记录 (resident_id = 7)，添加id_card字段
                String insertTangSql = "INSERT INTO residents (id, name, gender, age, phone, id_card, emergency_contact, emergency_phone, entry_date, room_number, bed_number, status, create_time, update_time) VALUES (7, '唐莹', '女', 70, '13800138007', '110101195501011235', '唐小明', '13900139007', NOW(), '302', '302-A', '入住', NOW(), NOW())";
                jdbcTemplate.update(insertTangSql);
                System.out.println("创建唐莹记录成功");
            } else {
                // 更新唐莹记录
                String updateTangSql = "UPDATE residents SET name = '唐莹', gender = '女', age = 70, phone = '13800138007', id_card = '110101195501011235', emergency_contact = '唐小明', emergency_phone = '13900139007', room_number = '302', bed_number = '302-A', status = '入住', update_time = NOW() WHERE id = 7";
                jdbcTemplate.update(updateTangSql);
                System.out.println("更新唐莹记录成功");
            }
            
            // 居民记录存在后，再初始化健康档案数据
            initHealthRecords();
            
            System.out.println("=== 居民数据初始化完成 ===");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("居民数据初始化失败");
        }
    }
    
    // 初始化健康档案数据，确保李牛花和唐莹的健康档案记录存在
    private void initHealthRecords() {
        try {
            System.out.println("=== 初始化健康档案数据 ===");
            
            // 使用INSERT INTO ... ON DUPLICATE KEY UPDATE语法，确保数据存在
            
            // 添加或更新李牛花的健康档案
            String upsertLiSql = "INSERT INTO health_records (resident_id, resident_name, room_number, bed_number, medical_history, allergy_history, blood_type, height, weight, health_notes, update_time) VALUES (6, '李牛花', '302', '302-A', '脑梗后遗症，行动不便', '无过敏史', 'AB型', 165, 65, '需康复训练，预防复发', NOW()) ON DUPLICATE KEY UPDATE resident_name = '李牛花', room_number = '302', bed_number = '302-A', medical_history = '脑梗后遗症，行动不便', allergy_history = '无过敏史', blood_type = 'AB型', height = 165, weight = 65, health_notes = '需康复训练，预防复发', update_time = NOW()";
            jdbcTemplate.update(upsertLiSql);
            System.out.println("添加/更新李牛花健康档案记录成功");
            
            // 添加或更新唐莹的健康档案
            String upsertTangSql = "INSERT INTO health_records (resident_id, resident_name, room_number, bed_number, medical_history, allergy_history, blood_type, height, weight, health_notes, update_time) VALUES (7, '唐莹', '401', '401-A', '无', '猫毛过敏', 'B型', 169, 46, '良好', NOW()) ON DUPLICATE KEY UPDATE resident_name = '唐莹', room_number = '401', bed_number = '401-A', medical_history = '无', allergy_history = '猫毛过敏', blood_type = 'B型', height = 169, weight = 46, health_notes = '良好', update_time = NOW()";
            jdbcTemplate.update(upsertTangSql);
            System.out.println("添加/更新唐莹健康档案记录成功");
            
            System.out.println("=== 健康档案数据初始化完成 ===");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("健康档案数据初始化失败");
        }
    }

    // 1. 获取血氧记录
    @GetMapping("/blood-oxygen/{residentId}")
    public Map<String, Object> getBloodOxygen(@PathVariable("residentId") Integer residentId) {
        try {
            String sql = "SELECT * FROM health_blood_oxygen WHERE resident_id = ? ORDER BY measure_time ASC";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, residentId);
            return success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return success(new ArrayList<>());
        }
    }

    /** 
     * 获取体温记录 (对应前端今日体温监测)
     * 按时间正序排列，用于绘制趋势图 
     */ 
    @GetMapping("/body-temperature/{residentId}") 
    public Map<String, Object> getBodyTemperature(@PathVariable("residentId") Integer residentId) {
        try {
            System.out.println("=== 获取体温记录 ===");
            System.out.println("residentId: " + residentId);
            
            // 查询当日或最近的体温记录，按时间正序排列
            String sql = "SELECT * FROM health_body_temperature WHERE resident_id = ? ORDER BY measure_time ASC";
            System.out.println("SQL: " + sql);
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, residentId);
            System.out.println("查询结果数量: " + list.size());
            
            // 数据处理：将数据库字段名转换为前端需要的字段名
            List<Map<String, Object>> formattedList = new ArrayList<>();
            
            // 如果数据库中没有数据，添加模拟数据
            if (list.isEmpty()) {
                System.out.println("数据库中没有体温数据，添加模拟数据");
                
                // 生成过去24小时的体温数据
                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < 24; i++) {
                    Map<String, Object> tempData = new HashMap<>();
                    
                    // 设置时间
                    calendar.add(Calendar.HOUR, -1);
                    Date measureTime = calendar.getTime();
                    tempData.put("measureTime", measureTime);
                    
                    // 设置体温（36.5-37.0之间的随机值）
                    double temp = 36.5 + Math.random() * 0.5;
                    temp = Math.round(temp * 10) / 10.0; // 保留一位小数
                    tempData.put("temperature", temp);
                    
                    // 设置测量部位
                    String[] measureParts = {"腋下", "口腔", "额头"};
                    tempData.put("measurePart", measureParts[(int) (Math.random() * measureParts.length)]);
                    
                    // 设置备注
                    tempData.put("notes", "模拟体温数据");
                    
                    formattedList.add(tempData);
                }
            } else {
                for (Map<String, Object> record : list) {
                    Map<String, Object> formattedRecord = new HashMap<>();
                    
                    // 1. 从数据库获取体温值
                    Double temperature = record.containsKey("temperature") ? Double.parseDouble(record.get("temperature").toString()) : 0.0;
                    
                    // 2. 从数据库获取测量时间
                    Object measureTimeObj = record.get("measure_time");
                    Date measureTime = null;
                    if (measureTimeObj instanceof Date) {
                        measureTime = (Date) measureTimeObj;
                    } else if (measureTimeObj instanceof String) {
                        measureTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) measureTimeObj);
                    }
                    
                    // 3. 构建返回数据
                    formattedRecord.put("temperature", temperature);
                    formattedRecord.put("measureTime", measureTime);
                    formattedRecord.put("measurePart", record.get("measure_part"));
                    formattedRecord.put("notes", record.get("notes"));
                    
                    formattedList.add(formattedRecord);
                }
            }
            
            return success(formattedList);
        } catch (Exception e) {
            e.printStackTrace();
            return success(new ArrayList<>());
        }
    }

    /** 
     * 获取最新睡眠质量记录 (对应前端昨夜睡眠质量) 
     */ 
    @GetMapping("/sleep-quality/{residentId}") 
    public Map<String, Object> getSleepQuality(@PathVariable("residentId") Integer residentId) {
        try {
            System.out.println("=== 获取睡眠质量记录 ===");
            System.out.println("residentId: " + residentId);
            
            // 获取最新的一条睡眠记录 (按创建时间或入睡时间倒序)
            String sql = "SELECT * FROM health_sleep_quality WHERE resident_id = ? ORDER BY create_time DESC LIMIT 1";
            System.out.println("SQL: " + sql);
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, residentId);
            System.out.println("查询结果数量: " + list.size());
            
            // 数据处理：计算深睡/浅睡时长（模拟逻辑），处理评分
            List<Map<String, Object>> formattedList = new ArrayList<>();
            
            if (!list.isEmpty()) {
                Map<String, Object> sleepData = new HashMap<>(list.get(0)); // 复制一份数据
                System.out.println("原始睡眠数据: " + sleepData);
                
                // 处理睡眠数据
                // 1. 从数据库获取睡眠时长
                double sleepDuration = sleepData.containsKey("sleep_duration") ? Double.parseDouble(sleepData.get("sleep_duration").toString()) : 0.0;
                
                // 2. 从数据库获取睡眠质量
                String sleepQuality = sleepData.containsKey("sleep_quality") ? sleepData.get("sleep_quality").toString() : "未知";
                
                // 3. 从数据库获取入睡时间和醒来时间
                String fallAsleepTime = sleepData.containsKey("fall_asleep_time") ? sleepData.get("fall_asleep_time").toString() : "";
                String wakeUpTime = sleepData.containsKey("wake_up_time") ? sleepData.get("wake_up_time").toString() : "";
                
                // 4. 从数据库获取夜间醒来次数
                int nightWakeCount = sleepData.containsKey("night_wake_count") ? Integer.parseInt(sleepData.get("night_wake_count").toString()) : 0;
                
                // 5. 模拟计算深睡浅睡时长 (假设深睡占35%)
                double deepSleepDuration = Math.round(sleepDuration * 0.35 * 10) / 10.0;
                double lightSleepDuration = Math.round((sleepDuration - deepSleepDuration) * 10) / 10.0;
                
                // 6. 计算睡眠评分
                int score = 80; // 默认分
                if ("优".equals(sleepQuality)) score = 95;
                else if ("良".equals(sleepQuality)) score = 85;
                else if ("中".equals(sleepQuality)) score = 70;
                else if ("差".equals(sleepQuality)) score = 50;
                
                // 7. 转换睡眠状态
                String sleepStatus = "未知";
                if ("优".equals(sleepQuality)) sleepStatus = "优秀";
                else if ("良".equals(sleepQuality)) sleepStatus = "良好";
                else if ("中".equals(sleepQuality)) sleepStatus = "一般";
                else if ("差".equals(sleepQuality)) sleepStatus = "较差";
                
                // 8. 构建返回数据
                Map<String, Object> formattedSleepData = new HashMap<>();
                formattedSleepData.put("sleepDuration", sleepDuration);
                formattedSleepData.put("deepSleepDuration", deepSleepDuration);
                formattedSleepData.put("lightSleepDuration", lightSleepDuration);
                formattedSleepData.put("sleepScore", score);
                formattedSleepData.put("sleepStatus", sleepStatus);
                formattedSleepData.put("sleepQuality", sleepQuality);
                formattedSleepData.put("fallAsleepTime", fallAsleepTime);
                formattedSleepData.put("wakeUpTime", wakeUpTime);
                formattedSleepData.put("nightWakeCount", nightWakeCount);
                
                formattedList.add(formattedSleepData);
            } else {
                // 数据库中没有睡眠数据，添加模拟数据
                System.out.println("数据库中没有睡眠数据，添加模拟数据");
                
                // 生成模拟睡眠数据
                Map<String, Object> formattedSleepData = new HashMap<>();
                
                // 随机生成睡眠时长 (6-9小时)
                double sleepDuration = 6 + Math.random() * 3;
                sleepDuration = Math.round(sleepDuration * 10) / 10.0;
                
                // 计算深睡浅睡时长 (深睡占35%)
                double deepSleepDuration = Math.round(sleepDuration * 0.35 * 10) / 10.0;
                double lightSleepDuration = Math.round((sleepDuration - deepSleepDuration) * 10) / 10.0;
                
                // 随机生成睡眠质量
                String[] sleepQualities = {"优", "良", "中", "差"};
                String sleepQuality = sleepQualities[(int) (Math.random() * sleepQualities.length)];
                
                // 计算睡眠评分
                int score = 80; // 默认分
                if ("优".equals(sleepQuality)) score = 95;
                else if ("良".equals(sleepQuality)) score = 85;
                else if ("中".equals(sleepQuality)) score = 70;
                else if ("差".equals(sleepQuality)) score = 50;
                
                // 转换睡眠状态
                String sleepStatus = "未知";
                if ("优".equals(sleepQuality)) sleepStatus = "优秀";
                else if ("良".equals(sleepQuality)) sleepStatus = "良好";
                else if ("中".equals(sleepQuality)) sleepStatus = "一般";
                else if ("差".equals(sleepQuality)) sleepStatus = "较差";
                
                // 设置入睡和醒来时间
                String fallAsleepTime = "22:30";
                String wakeUpTime = "06:00";
                
                // 随机生成夜间醒来次数
                int nightWakeCount = (int) (Math.random() * 3);
                
                // 构建返回数据
                formattedSleepData.put("sleepDuration", sleepDuration);
                formattedSleepData.put("deepSleepDuration", deepSleepDuration);
                formattedSleepData.put("lightSleepDuration", lightSleepDuration);
                formattedSleepData.put("sleepScore", score);
                formattedSleepData.put("sleepStatus", sleepStatus);
                formattedSleepData.put("sleepQuality", sleepQuality);
                formattedSleepData.put("fallAsleepTime", fallAsleepTime);
                formattedSleepData.put("wakeUpTime", wakeUpTime);
                formattedSleepData.put("nightWakeCount", nightWakeCount);
                
                formattedList.add(formattedSleepData);
            }
            
            return success(formattedList);
        } catch (Exception e) {
            e.printStackTrace();
            return success(new ArrayList<>());
        }
    }
    
    /**
     * 获取在住居民列表 (只显示未退房的老人)
     */
    @GetMapping("/residents")
    public Map<String, Object> getResidents() {
        try {
            System.out.println("=== 获取在住居民列表 ===");
            
            // 【关键修改】在SQL中直接过滤 status = '入住'
            String sql = "SELECT id, name, age, gender, room_number, bed_number, status " +
                         "FROM residents " +
                         "WHERE status = '入住' " +
                         "ORDER BY room_number ASC, bed_number ASC";
            
            System.out.println("SQL: " + sql);
            List<Map<String, Object>> residents = jdbcTemplate.queryForList(sql);
            
            // 如果数据库中没有数据，调用初始化方法并重新查询
            if (residents.isEmpty()) {
                // 调用初始化方法添加居民数据
                initResidents();
                // 重新查询
                residents = jdbcTemplate.queryForList(sql);
            }

            // 打印查询到的居民信息，便于调试
            for (Map<String, Object> resident : residents) {
                System.out.println("居民信息: " + resident);
            }

            return success(residents);
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取居民列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发居民数据初始化，添加李牛花和唐莹的记录
     */
    @GetMapping("/init-residents")
    public Map<String, Object> initResidentsManually() {
        try {
            System.out.println("=== 手动初始化居民数据 ===");
            
            // 手动执行居民数据初始化逻辑
            initResidents();
            
            return success("居民数据初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            return error("居民数据初始化失败: " + e.getMessage());
        }
    }
    /**
     * 解析日期字符串
     * @param dateStr 日期字符串
     * @return 解析后的Date对象
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return new Date();
        }
        
        // 尝试多种日期格式
        String[] formats = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd"
        };
        
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format).parse(dateStr);
            } catch (ParseException e) {
                // 格式不匹配，继续尝试下一种
            }
        }
        
        return new Date();
    }
    
    /**
     * 将Object转换为Integer
     * @param obj 要转换的对象
     * @return 转换后的Integer，如果转换失败则返回null
     */
    private Integer getIntegerFromObject(Object obj) {
        if (obj == null) {
            return null;
        }
        
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * 获取血压记录列表
     */
    @GetMapping("/blood-pressure")
    public Map<String, Object> getBloodPressureRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<BloodPressure> records = bloodPressureService.getAllBloodPressureRecords();
            result.put("success", true);
            result.put("data", records);
            result.put("message", "获取血压记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取血压记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据老人ID获取血压记录
     */
    @GetMapping("/blood-pressure/{residentId}")
    public Map<String, Object> getBloodPressureRecordsByResidentId(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<BloodPressure> records = bloodPressureService.getBloodPressureRecordsByResidentId(residentId);
            result.put("success", true);
            result.put("data", records);
            result.put("message", "获取老人血压记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取老人血压记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加血压记录
     */
    @PostMapping("/blood-pressure")
    public Map<String, Object> addBloodPressureRecord(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 创建血压记录对象
            BloodPressure bloodPressure = new BloodPressure();
            
            // 设置老人信息 - 支持多种字段名称格式
            Integer residentId = null;
            String residentName = null;
            
            // 尝试从requestData中获取residentId，支持多种字段名称
            if (requestData.containsKey("residentId")) {
                residentId = getIntegerFromObject(requestData.get("residentId"));
            } else if (requestData.containsKey("resident_id")) {
                residentId = getIntegerFromObject(requestData.get("resident_id"));
            } else if (requestData.containsKey("老人ID")) {
                residentId = getIntegerFromObject(requestData.get("老人ID"));
            }
            
            // 尝试从requestData中获取residentName，支持多种字段名称
            if (requestData.containsKey("residentName")) {
                residentName = requestData.get("residentName").toString();
            } else if (requestData.containsKey("resident_name")) {
                residentName = requestData.get("resident_name").toString();
            } else if (requestData.containsKey("老人姓名")) {
                residentName = requestData.get("老人姓名").toString();
            }
            
            // 如果没有residentId，但有residentName，尝试根据姓名查询ID
            if (residentId == null && residentName != null) {
                try {
                    // 根据老人姓名查询residentId
                    List<Resident> residents = residentService.lambdaQuery().eq(Resident::getName, residentName).list();
                    if (!residents.isEmpty()) {
                        residentId = residents.get(0).getId();
                    } else {
                        result.put("success", false);
                        result.put("message", "未找到该老人信息，请检查姓名是否正确");
                        return result;
                    }
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("message", "根据老人姓名查询ID失败：" + e.getMessage());
                    return result;
                }
            }
            
            // 如果还是没有residentId，返回错误
            if (residentId == null) {
                result.put("success", false);
                result.put("message", "缺少老人ID，无法保存血压记录");
                return result;
            }
            
            // 设置老人信息
            bloodPressure.setResidentId(residentId);
            if (residentName != null) {
                bloodPressure.setResidentName(residentName);
            } else {
                // 自动根据residentId查询老人姓名
                try {
                    Resident resident = residentService.getById(residentId);
                    if (resident != null) {
                        bloodPressure.setResidentName(resident.getName());
                    }
                } catch (Exception e) {
                    // 查询失败不影响保存，但添加日志
                    System.err.println("根据residentId查询老人姓名失败：" + e.getMessage());
                }
            }
            
            // 设置血压值 - 支持多种字段名称格式
            Integer systolic = null;
            Integer diastolic = null;
            
            // 获取收缩压
            if (requestData.containsKey("systolic")) {
                systolic = getIntegerFromObject(requestData.get("systolic"));
            } else if (requestData.containsKey("收缩压")) {
                systolic = getIntegerFromObject(requestData.get("收缩压"));
            }
            
            // 获取舒张压
            if (requestData.containsKey("diastolic")) {
                diastolic = getIntegerFromObject(requestData.get("diastolic"));
            } else if (requestData.containsKey("舒张压")) {
                diastolic = getIntegerFromObject(requestData.get("舒张压"));
            }
            
            // 验证血压值是否有效
            if (systolic == null) {
                result.put("success", false);
                result.put("message", "缺少收缩压值，无法保存血压记录");
                return result;
            }
            
            if (diastolic == null) {
                result.put("success", false);
                result.put("message", "缺少舒张压值，无法保存血压记录");
                return result;
            }
            
            // 血压值范围验证
            if (systolic < 50 || systolic > 250) {
                result.put("success", false);
                result.put("message", "收缩压值超出正常范围(50-250 mmHg)");
                return result;
            }
            
            if (diastolic < 30 || diastolic > 150) {
                result.put("success", false);
                result.put("message", "舒张压值超出正常范围(30-150 mmHg)");
                return result;
            }
            
            // 设置血压值
            bloodPressure.setSystolic(systolic);
            bloodPressure.setDiastolic(diastolic);
            
            // 设置测量时间 - 支持多种字段名称格式
            Date measureTime = null;
            if (requestData.containsKey("measureTime")) {
                if (requestData.get("measureTime") instanceof String) {
                    measureTime = parseDate((String) requestData.get("measureTime"));
                } else if (requestData.get("measureTime") instanceof Date) {
                    measureTime = (Date) requestData.get("measureTime");
                }
            } else if (requestData.containsKey("测量时间")) {
                if (requestData.get("测量时间") instanceof String) {
                    measureTime = parseDate((String) requestData.get("测量时间"));
                } else if (requestData.get("测量时间") instanceof Date) {
                    measureTime = (Date) requestData.get("测量时间");
                }
            }
            
            // 如果没有提供测量时间，使用当前时间
            if (measureTime == null) {
                measureTime = new Date();
            }
            bloodPressure.setMeasureTime(measureTime);
            
            // 设置备注 - 支持多种字段名称格式
            if (requestData.containsKey("notes")) {
                bloodPressure.setNotes(requestData.get("notes").toString());
            } else if (requestData.containsKey("备注")) {
                bloodPressure.setNotes(requestData.get("备注").toString());
            }
            
            // 设置创建和更新时间
            bloodPressure.setCreateTime(new Date());
            bloodPressure.setUpdateTime(new Date());
            
            // 保存记录
            boolean success = bloodPressureService.addBloodPressureRecord(bloodPressure);
            result.put("success", success);
            if (success) {
                result.put("message", "添加血压记录成功");
                result.put("data", bloodPressure);
            } else {
                result.put("message", "添加血压记录失败：保存到数据库失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加血压记录失败：" + e.getMessage());
            // 添加异常堆栈信息，便于调试
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取血糖记录列表
     */
    @GetMapping("/blood-sugar")
    public Map<String, Object> getBloodSugarRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<BloodSugar> records = bloodSugarService.getAllBloodSugarRecords();
            result.put("success", true);
            result.put("data", records);
            result.put("message", "获取血糖记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取血糖记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据老人ID获取血糖记录
     */
    @GetMapping("/blood-sugar/{residentId}")
    public Map<String, Object> getBloodSugarRecordsByResidentId(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<BloodSugar> records = bloodSugarService.getBloodSugarRecordsByResidentId(residentId);
            result.put("success", true);
            result.put("data", records);
            result.put("message", "获取老人血糖记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取老人血糖记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加血糖记录
     */
    @PostMapping("/blood-sugar")
    public Map<String, Object> addBloodSugarRecord(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 创建血糖记录对象
            BloodSugar bloodSugar = new BloodSugar();
            
            // 设置老人信息
            Integer residentId = null;
            if (requestData.containsKey("residentId")) {
                Object residentIdObj = requestData.get("residentId");
                if (residentIdObj instanceof Integer) {
                    residentId = (Integer) residentIdObj;
                    bloodSugar.setResidentId(residentId);
                } else if (residentIdObj instanceof String) {
                    try {
                        residentId = Integer.parseInt((String) residentIdObj);
                        bloodSugar.setResidentId(residentId);
                    } catch (NumberFormatException e) {
                        result.put("success", false);
                        result.put("message", "老人ID格式错误");
                        return result;
                    }
                }
            } else {
                result.put("success", false);
                result.put("message", "缺少老人ID");
                return result;
            }
            
            // 设置老人姓名
            if (requestData.containsKey("residentName")) {
                bloodSugar.setResidentName(requestData.get("residentName").toString());
            } else if (residentId != null) {
                // 自动根据residentId查询老人姓名
                try {
                    Resident resident = residentService.getById(residentId);
                    if (resident != null) {
                        bloodSugar.setResidentName(resident.getName());
                    }
                } catch (Exception e) {
                    // 查询失败不影响保存
                }
            }
            
            // 设置血糖值
            if (requestData.containsKey("level")) {
                Object levelObj = requestData.get("level");
                if (levelObj instanceof Double) {
                    bloodSugar.setLevel((Double) levelObj);
                } else if (levelObj instanceof Integer) {
                    bloodSugar.setLevel(((Integer) levelObj).doubleValue());
                } else if (levelObj instanceof String) {
                    try {
                        bloodSugar.setLevel(Double.parseDouble((String) levelObj));
                    } catch (NumberFormatException e) {
                        result.put("success", false);
                        result.put("message", "血糖值格式错误");
                        return result;
                    }
                }
            }
            
            // 设置测量状态
            if (requestData.containsKey("status")) {
                bloodSugar.setStatus(requestData.get("status").toString());
            }
            
            // 设置测量时间
            if (requestData.containsKey("measureTime")) {
                Object measureTimeObj = requestData.get("measureTime");
                if (measureTimeObj instanceof String) {
                    bloodSugar.setMeasureTime(parseDate((String) measureTimeObj));
                } else if (measureTimeObj instanceof Date) {
                    bloodSugar.setMeasureTime((Date) measureTimeObj);
                }
            } else {
                bloodSugar.setMeasureTime(new Date());
            }
            
            // 设置备注
            if (requestData.containsKey("notes")) {
                bloodSugar.setNotes(requestData.get("notes").toString());
            }
            
            // 设置创建和更新时间
            bloodSugar.setCreateTime(new Date());
            bloodSugar.setUpdateTime(new Date());
            
            // 保存记录
            boolean success = bloodSugarService.addBloodSugarRecord(bloodSugar);
            result.put("success", success);
            if (success) {
                result.put("message", "添加血糖记录成功");
            } else {
                result.put("message", "添加血糖记录失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加血糖记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取心率记录列表
     */
    @GetMapping("/heart-rate")
    public Map<String, Object> getHeartRateRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<HeartRate> records = heartRateService.getAllHeartRateRecords();
            result.put("success", true);
            result.put("data", records);
            result.put("message", "获取心率记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取心率记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据老人ID获取心率记录
     */
    @GetMapping("/heart-rate/{residentId}")
    public Map<String, Object> getHeartRateRecordsByResidentId(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<HeartRate> records = heartRateService.getHeartRateRecordsByResidentId(residentId);
            result.put("success", true);
            result.put("data", records);
            result.put("message", "获取老人心率记录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取老人心率记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加心率记录
     */
    @PostMapping("/heart-rate")
    public Map<String, Object> addHeartRateRecord(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 创建心率记录对象
            HeartRate heartRate = new HeartRate();
            
            // 设置老人信息
            Integer residentId = null;
            if (requestData.containsKey("residentId")) {
                Object residentIdObj = requestData.get("residentId");
                if (residentIdObj instanceof Integer) {
                    residentId = (Integer) residentIdObj;
                    heartRate.setResidentId(residentId);
                } else if (residentIdObj instanceof String) {
                    try {
                        residentId = Integer.parseInt((String) residentIdObj);
                        heartRate.setResidentId(residentId);
                    } catch (NumberFormatException e) {
                        result.put("success", false);
                        result.put("message", "老人ID格式错误");
                        return result;
                    }
                }
            } else {
                result.put("success", false);
                result.put("message", "缺少老人ID");
                return result;
            }
            
            // 设置老人姓名
            if (requestData.containsKey("residentName")) {
                heartRate.setResidentName(requestData.get("residentName").toString());
            } else if (residentId != null) {
                // 自动根据residentId查询老人姓名
                try {
                    Resident resident = residentService.getById(residentId);
                    if (resident != null) {
                        heartRate.setResidentName(resident.getName());
                    }
                } catch (Exception e) {
                    // 查询失败不影响保存
                }
            }
            
            // 设置心率值
            if (requestData.containsKey("rate")) {
                Object rateObj = requestData.get("rate");
                if (rateObj instanceof Integer) {
                    heartRate.setRate((Integer) rateObj);
                } else if (rateObj instanceof String) {
                    try {
                        heartRate.setRate(Integer.parseInt((String) rateObj));
                    } catch (NumberFormatException e) {
                        result.put("success", false);
                        result.put("message", "心率值格式错误");
                        return result;
                    }
                }
            }
            
            // 设置测量时间
            if (requestData.containsKey("measureTime")) {
                Object measureTimeObj = requestData.get("measureTime");
                if (measureTimeObj instanceof String) {
                    heartRate.setMeasureTime(parseDate((String) measureTimeObj));
                } else if (measureTimeObj instanceof Date) {
                    heartRate.setMeasureTime((Date) measureTimeObj);
                }
            } else {
                heartRate.setMeasureTime(new Date());
            }
            
            // 设置备注
            if (requestData.containsKey("notes")) {
                heartRate.setNotes(requestData.get("notes").toString());
            }
            
            // 设置创建和更新时间
            heartRate.setCreateTime(new Date());
            heartRate.setUpdateTime(new Date());
            
            // 保存记录
            boolean success = heartRateService.addHeartRateRecord(heartRate);
            result.put("success", success);
            if (success) {
                result.put("message", "添加心率记录成功");
            } else {
                result.put("message", "添加心率记录失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加心率记录失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取健康预警
     */
    @GetMapping("/alerts")
    public Map<String, Object> getHealthAlerts() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> alerts = new ArrayList<>();
            
            // 获取最新的血压记录并检查异常
            List<BloodPressure> bloodPressureList = bloodPressureService.getAllBloodPressureRecords();
            if (!bloodPressureList.isEmpty()) {
                BloodPressure latestBP = bloodPressureList.get(bloodPressureList.size() - 1);
                if (latestBP.getSystolic() > 140 || latestBP.getDiastolic() > 90) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("type", "danger");
                    alert.put("title", "血压异常警告");
                    alert.put("description", latestBP.getResidentName() + "的血压值过高：" + latestBP.getSystolic() + "/" + latestBP.getDiastolic() + " mmHg");
                    alert.put("time", new Date());
                    alerts.add(alert);
                }
            }
            
            // 获取最新的血糖记录并检查异常
            List<BloodSugar> bloodSugarList = bloodSugarService.getAllBloodSugarRecords();
            if (!bloodSugarList.isEmpty()) {
                BloodSugar latestBS = bloodSugarList.get(bloodSugarList.size() - 1);
                if (("空腹".equals(latestBS.getStatus()) && latestBS.getLevel() > 7.0) || 
                    ("餐后".equals(latestBS.getStatus()) && latestBS.getLevel() > 10.0) ||
                    latestBS.getLevel() < 3.9) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("type", "danger");
                    alert.put("title", "血糖异常警告");
                    alert.put("description", latestBS.getResidentName() + "的血糖值异常：" + latestBS.getLevel() + " mmol/L");
                    alert.put("time", new Date());
                    alerts.add(alert);
                }
            }
            
            // 获取最新的心率记录并检查异常
            List<HeartRate> heartRateList = heartRateService.getAllHeartRateRecords();
            if (!heartRateList.isEmpty()) {
                HeartRate latestHR = heartRateList.get(heartRateList.size() - 1);
                if (latestHR.getRate() > 100 || latestHR.getRate() < 60) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("type", "danger");
                    alert.put("title", "心率异常警告");
                    alert.put("description", latestHR.getResidentName() + "的心率异常：" + latestHR.getRate() + " 次/分");
                    alert.put("time", new Date());
                    alerts.add(alert);
                }
            }
            
            // 检查血氧异常
            try {
                String sql = "SELECT * FROM health_blood_oxygen ORDER BY measure_time DESC LIMIT 1";
                List<Map<String, Object>> oxygens = jdbcTemplate.queryForList(sql);
                if (!oxygens.isEmpty()) {
                    Double spo2 = Double.valueOf(oxygens.get(0).get("oxygen_saturation").toString());
                    if (spo2 < 95) {
                        Map<String, Object> alert = new HashMap<>();
                        alert.put("type", "danger");
                        alert.put("title", "血氧异常警告");
                        alert.put("description", oxygens.get(0).get("resident_name") + "的血氧值过低：" + spo2 + "%");
                        alert.put("time", new Date());
                        alerts.add(alert);
                    }
                }
            } catch (Exception e) {
                // 忽略异常，继续执行
            }
            
            result.put("success", true);
            result.put("data", alerts);
            result.put("message", "获取健康预警成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取健康预警失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取实时体征快照（心率、血氧）
     */
    @GetMapping("/vitals-snapshot/{residentId}")
    public Map<String, Object> getVitalsSnapshot(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> snapshot = new HashMap<>();
            
            // 获取最新心率
            String heartRateSql = "SELECT * FROM health_heart_rate WHERE resident_id = ? ORDER BY measure_time DESC LIMIT 1";
            List<Map<String, Object>> heartRates = jdbcTemplate.queryForList(heartRateSql, residentId);
            if (!heartRates.isEmpty()) {
                snapshot.put("heartRate", heartRates.get(0).get("rate"));
            } else {
                snapshot.put("heartRate", 0);
            }
            
            // 获取最新血氧
            String oxygenSql = "SELECT * FROM health_blood_oxygen WHERE resident_id = ? ORDER BY measure_time DESC LIMIT 1";
            List<Map<String, Object>> oxygens = jdbcTemplate.queryForList(oxygenSql, residentId);
            if (!oxygens.isEmpty()) {
                snapshot.put("bloodOxygen", oxygens.get(0).get("oxygen_saturation"));
            } else {
                snapshot.put("bloodOxygen", 0);
            }
            
            // 获取最新体温
            String tempSql = "SELECT * FROM health_body_temperature WHERE resident_id = ? ORDER BY measure_time DESC LIMIT 1";
            List<Map<String, Object>> temps = jdbcTemplate.queryForList(tempSql, residentId);
            if (!temps.isEmpty()) {
                snapshot.put("bodyTemperature", temps.get(0).get("temperature"));
            } else {
                snapshot.put("bodyTemperature", 36.5);
            }
            
            result.put("success", true);
            result.put("data", snapshot);
            result.put("message", "获取实时体征快照成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取实时体征快照失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取护理等级和责任护工信息
     */
    @GetMapping("/nursing-info/{residentId}")
    public Map<String, Object> getNursingInfo(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里可以根据实际情况从数据库获取护理等级和护工信息
            // 暂时返回模拟数据
            Map<String, Object> nursingInfo = new HashMap<>();
            nursingInfo.put("nursingLevel", "二级护理");
            nursingInfo.put("nurseName", "张护工");
            
            result.put("success", true);
            result.put("data", nursingInfo);
            result.put("message", "获取护理信息成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取护理信息失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取居民健康档案信息
     */
    @GetMapping("/health-records/{residentId}")
    public Map<String, Object> getHealthRecords(@PathVariable("residentId") Integer residentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("=== 获取居民健康档案信息 ===");
            System.out.println("residentId: " + residentId);
            
            // 查询健康档案表中的数据
            String sql = "SELECT * FROM health_records WHERE resident_id = ?";
            System.out.println("SQL: " + sql);
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, residentId);
            System.out.println("查询结果数量: " + records.size());
            
            // 数据处理：将数据库字段名转换为前端需要的字段名
            List<Map<String, Object>> formattedRecords = new ArrayList<>();
            
            for (Map<String, Object> record : records) {
                Map<String, Object> formattedRecord = new HashMap<>();
                
                // 转换字段名，确保与前端需求匹配
                formattedRecord.put("id", record.get("id"));
                formattedRecord.put("residentId", record.get("resident_id"));
                formattedRecord.put("residentName", record.get("resident_name"));
                formattedRecord.put("roomNumber", record.get("room_number"));
                formattedRecord.put("bedNumber", record.get("bed_number"));
                formattedRecord.put("medicalHistory", record.get("medical_history"));
                formattedRecord.put("allergyHistory", record.get("allergy_history"));
                formattedRecord.put("bloodType", record.get("blood_type"));
                formattedRecord.put("height", record.get("height"));
                formattedRecord.put("weight", record.get("weight"));
                formattedRecord.put("healthNotes", record.get("health_notes"));
                formattedRecord.put("updateTime", record.get("update_time"));
                
                formattedRecords.add(formattedRecord);
            }
            
            result.put("success", true);
            result.put("data", formattedRecords);
            result.put("message", "获取健康档案信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取健康档案信息失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有居民健康档案列表
     */
    @GetMapping("/health-records")
    public Map<String, Object> getAllHealthRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("=== 获取所有居民健康档案列表 ===");
            
            // 查询所有健康档案数据，包括李牛花和唐莹
            String sql = "SELECT * FROM health_records";
            System.out.println("SQL: " + sql);
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql);
            System.out.println("查询结果数量: " + records.size());
            
            // 打印每条记录的详细信息，便于调试
            for (Map<String, Object> record : records) {
                System.out.println("健康档案记录: " + record);
            }
            
            // 数据处理：将数据库字段名转换为前端需要的字段名
            List<Map<String, Object>> formattedRecords = new ArrayList<>();
            
            for (Map<String, Object> record : records) {
                Map<String, Object> formattedRecord = new HashMap<>();
                
                // 转换字段名，确保与前端需求匹配
                formattedRecord.put("id", record.get("id"));
                formattedRecord.put("residentId", record.get("resident_id"));
                formattedRecord.put("residentName", record.get("resident_name"));
                formattedRecord.put("roomNumber", record.get("room_number"));
                formattedRecord.put("bedNumber", record.get("bed_number"));
                formattedRecord.put("medicalHistory", record.get("medical_history"));
                formattedRecord.put("allergyHistory", record.get("allergy_history"));
                formattedRecord.put("bloodType", record.get("blood_type"));
                formattedRecord.put("height", record.get("height"));
                formattedRecord.put("weight", record.get("weight"));
                formattedRecord.put("healthNotes", record.get("health_notes"));
                formattedRecord.put("updateTime", record.get("update_time"));
                
                formattedRecords.add(formattedRecord);
            }
            
            result.put("success", true);
            result.put("data", formattedRecords);
            result.put("message", "获取所有健康档案信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取所有健康档案信息失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 手动初始化健康档案数据
     */
    @GetMapping("/init-health-records")
    public Map<String, Object> initHealthRecordsManually() {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("=== 手动初始化健康档案数据 ===");
            
            // 直接添加或更新李牛花和唐莹的健康档案数据
            // 使用INSERT INTO ... ON DUPLICATE KEY UPDATE语法，确保数据存在
            
            // 添加或更新李牛花的健康档案
            String upsertLiSql = "INSERT INTO health_records (resident_id, resident_name, room_number, bed_number, medical_history, allergy_history, blood_type, height, weight, health_notes, update_time) VALUES (6, '李牛花', '302', '302-A', '脑梗后遗症，行动不便', '无过敏史', 'AB型', 165, 65, '需康复训练，预防复发', NOW()) ON DUPLICATE KEY UPDATE resident_name = '李牛花', room_number = '302', bed_number = '302-A', medical_history = '脑梗后遗症，行动不便', allergy_history = '无过敏史', blood_type = 'AB型', height = 165, weight = 65, health_notes = '需康复训练，预防复发', update_time = NOW()";
            jdbcTemplate.update(upsertLiSql);
            System.out.println("添加/更新李牛花健康档案记录成功");
            
            // 添加或更新唐莹的健康档案
            String upsertTangSql = "INSERT INTO health_records (resident_id, resident_name, room_number, bed_number, medical_history, allergy_history, blood_type, height, weight, health_notes, update_time) VALUES (7, '唐莹', '401', '401-A', '无', '猫毛过敏', 'B型', 169, 46, '良好', NOW()) ON DUPLICATE KEY UPDATE resident_name = '唐莹', room_number = '401', bed_number = '401-A', medical_history = '无', allergy_history = '猫毛过敏', blood_type = 'B型', height = 169, weight = 46, health_notes = '良好', update_time = NOW()";
            jdbcTemplate.update(upsertTangSql);
            System.out.println("添加/更新唐莹健康档案记录成功");
            
            result.put("success", true);
            result.put("message", "健康档案数据初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "健康档案数据初始化失败：" + e.getMessage());
        }
        return result;
    }
    
    // --- 辅助方法 ---
    private void addAlert(List<Map<String, Object>> list, String level, String title, String value) {
        Map<String, Object> map = new HashMap<>();
        map.put("time", new SimpleDateFormat("HH:mm").format(new Date()));
        map.put("level", level);
        map.put("content", title);
        map.put("value", value);
        list.add(map);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("data", data);
        map.put("message", "操作成功");
        return map;
    }

    private Map<String, Object> result(boolean success) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", success ? "操作成功" : "操作失败");
        return map;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", msg);
        return map;
    }
}