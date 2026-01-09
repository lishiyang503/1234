package com.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.ServicePackage;
import com.demo.pojo.ServicePurchaseRecord;
import com.demo.service.ServicePackageService;
import com.demo.service.ServicePurchaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.demo.mapper.ServicePurchaseRecordMapper;

@RestController
@RequestMapping("/service")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceController {

    @Autowired
    private ServicePackageService servicePackageService;
    
    @Autowired
    private ServicePurchaseRecordService servicePurchaseRecordService;
    
    @Autowired
    private ServicePurchaseRecordMapper servicePurchaseRecordMapper;

    /**
     * 获取服务套餐列表（支持分页、名称和级别搜索）
     */
    @GetMapping("/packages")
    public Map<String, Object> getServicePackageList(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "level", defaultValue = "") String level,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<ServicePackage> packageList = servicePackageService.getServicePackageList(name, level, page, pageSize);
            Map<String, Object> data = new HashMap<>();
            data.put("list", packageList.getRecords());
            data.put("total", packageList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取服务套餐列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取服务套餐列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取服务购买记录列表（支持分页、老人姓名搜索）
     */
    @GetMapping("/purchase/records")
    public Map<String, Object> getPurchaseRecords(
            @RequestParam(name = "residentName", defaultValue = "") String residentName,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<ServicePurchaseRecord> recordList = servicePurchaseRecordService.getPurchaseRecordList(residentName, page, pageSize);
            Map<String, Object> data = new HashMap<>();
            data.put("list", recordList.getRecords());
            data.put("total", recordList.getTotal());
            result.put("success", true);
            result.put("data", data);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取服务购买记录列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取服务购买记录列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 创建服务购买记录
     */
    @PostMapping("/purchase")
    public Map<String, Object> createPurchaseRecord(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("接收到的购买记录数据: " + data);
            
            // 直接构建一个简单的购买记录，只包含必要的字段
            ServicePurchaseRecord record = new ServicePurchaseRecord();
            
            // 老人ID - 确保有值
            Integer residentId = 1; // 默认值
            if (data.containsKey("resident_id")) {
                Object rid = data.get("resident_id");
                if (rid instanceof Integer) {
                    residentId = (Integer) rid;
                } else if (rid instanceof String) {
                    residentId = Integer.parseInt((String) rid);
                }
            } else if (data.containsKey("residentId")) {
                Object rid = data.get("residentId");
                if (rid instanceof Integer) {
                    residentId = (Integer) rid;
                } else if (rid instanceof String) {
                    residentId = Integer.parseInt((String) rid);
                }
            }
            record.setResidentId(residentId);
            System.out.println("设置residentId: " + residentId);
            
            // 老人姓名
            String residentName = "未知";
            if (data.containsKey("resident_name")) {
                residentName = (String) data.get("resident_name");
            } else if (data.containsKey("residentName")) {
                residentName = (String) data.get("residentName");
            }
            record.setResidentName(residentName);
            System.out.println("设置residentName: " + residentName);
            
            // 服务ID
            Integer serviceId = 1; // 默认值
            if (data.containsKey("service_id")) {
                Object sid = data.get("service_id");
                if (sid instanceof Integer) {
                    serviceId = (Integer) sid;
                } else if (sid instanceof String) {
                    serviceId = Integer.parseInt((String) sid);
                }
            } else if (data.containsKey("servicePackageId")) {
                Object sid = data.get("servicePackageId");
                if (sid instanceof Integer) {
                    serviceId = (Integer) sid;
                } else if (sid instanceof String) {
                    serviceId = Integer.parseInt((String) sid);
                }
            }
            record.setServiceId(serviceId);
            System.out.println("设置serviceId: " + serviceId);
            
            // 服务名称
            String serviceName = "服务";
            if (data.containsKey("service_name")) {
                serviceName = (String) data.get("service_name");
            } else if (data.containsKey("servicePackageName")) {
                serviceName = (String) data.get("servicePackageName");
            }
            record.setServiceName(serviceName);
            System.out.println("设置serviceName: " + serviceName);
            
            // 价格
            Double price = 0.0;
            if (data.containsKey("price")) {
                Object p = data.get("price");
                if (p instanceof Double) {
                    price = (Double) p;
                } else if (p instanceof Integer) {
                    price = ((Integer) p).doubleValue();
                } else if (p instanceof String) {
                    price = Double.parseDouble((String) p);
                }
            }
            record.setPrice(price);
            System.out.println("设置price: " + price);
            
            // 操作人
            String operator = residentName;
            if (data.containsKey("operator")) {
                operator = (String) data.get("operator");
            }
            record.setOperator(operator);
            System.out.println("设置operator: " + operator);
            
            // 状态
            String status = "有效";
            if (data.containsKey("status")) {
                status = (String) data.get("status");
            }
            record.setStatus(status);
            System.out.println("设置status: " + status);
            
            // 其他必填字段
            record.setRoomNumber("");
            record.setBedNumber("");
            record.setServiceLevel("");
            record.setPurchaseDate(new Date());
            record.setExpireDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
            record.setCreateTime(new Date());
            
            // 使用底层Mapper直接插入，确保所有字段都被包含
            boolean success = servicePurchaseRecordMapper.insert(record) > 0;
            System.out.println("保存结果: " + success);
            if (success) {
                result.put("success", true);
                result.put("message", "服务购买成功");
            } else {
                result.put("success", false);
                result.put("message", "服务购买失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "服务购买失败：" + e.getMessage());
        }
        return result;
    }
}