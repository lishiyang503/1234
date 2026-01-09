package com.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.demo.pojo.Bed;
import com.demo.pojo.Resident;
import com.demo.pojo.Rooms;
import com.demo.service.BedService;
import com.demo.service.ResidentService;
import com.demo.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/beds")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BedController {
    @Autowired
    private BedService bedService;
    
    @Autowired
    private ResidentService residentService;
    
    @Autowired
    private RoomsService roomsService;

    @GetMapping("/by-resident")
    public Map<String, Object> getBedByResidentId(@RequestParam("residentId") Integer residentId){
        Map<String, Object> result = new HashMap<>();
        try {
            LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Bed::getResidentId, residentId);
            Bed bed = this.bedService.getOne(wrapper);
            result.put("success", true);
            result.put("data", bed);
            result.put("message", "获取床位信息成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取床位信息失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据房间ID获取床位列表，包含入住人信息
     */
    @GetMapping("/{roomId}")
    public Map<String, Object> getBedsByRoomId(@PathVariable("roomId") Integer roomId){
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取房间下的所有床位
            List<Bed> bedList = this.bedService.list(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, roomId).orderByAsc(Bed::getId));
            List<Map<String, Object>> bedInfoList = bedList.stream().map(bed -> {
                Map<String, Object> bedInfo = new HashMap<>();
                bedInfo.put("id", bed.getId());
                bedInfo.put("bedNumber", bed.getBedNumber());
                bedInfo.put("status", bed.getStatus());
                bedInfo.put("residentId", bed.getResidentId());
                bedInfo.put("createTime", bed.getCreateTime());
                bedInfo.put("updateTime", bed.getUpdateTime());
                
                // 添加备注字段，默认为"无备注"
                bedInfo.put("notes", "无备注");
                
                // 如果有入住人，获取入住人信息
                if (bed.getResidentId() != null) {
                    // 根据residentId查询Resident信息
                    Resident resident = this.residentService.getById(bed.getResidentId());
                    if (resident != null) {
                        bedInfo.put("residentName", resident.getName());
                        bedInfo.put("residentPhone", resident.getPhone());
                        bedInfo.put("checkInDate", resident.getEntryDate());
                        bedInfo.put("checkOutDate", resident.getExitDate());
                    } else {
                        bedInfo.put("residentName", "暂无住户");
                        bedInfo.put("residentPhone", "");
                        bedInfo.put("checkInDate", "");
                        bedInfo.put("checkOutDate", "");
                    }
                } else {
                    bedInfo.put("residentName", "暂无住户");
                    bedInfo.put("residentPhone", "");
                    bedInfo.put("checkInDate", "");
                    bedInfo.put("checkOutDate", "");
                }
                return bedInfo;
            }).collect(Collectors.toList());
            result.put("success", true);
            result.put("data", bedInfoList);
            result.put("message", "获取床位列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取床位列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有床位
     */
    @GetMapping("/all")
    public Map<String, Object> getAll(){
        Map<String, Object> result = new HashMap<>();
        try {
            List<Bed> bedList = this.bedService.getAll();
            // 获取床位详细信息，包含入住人信息
            List<Map<String, Object>> bedInfoList = bedList.stream().map(bed -> {
                Map<String, Object> bedInfo = new HashMap<>();
                bedInfo.put("id", bed.getId());
                bedInfo.put("bedNumber", bed.getBedNumber());
                // 将状态1（空闲）转换为状态0，以匹配前端期望
                int status = bed.getStatus();
                if (status == 1) {
                    status = 0;
                }
                bedInfo.put("status", status);
                bedInfo.put("roomId", bed.getRoomId());
                bedInfo.put("residentId", bed.getResidentId());
                bedInfo.put("createTime", bed.getCreateTime());
                bedInfo.put("updateTime", bed.getUpdateTime());
                
                // 添加备注字段，默认为"无备注"
                bedInfo.put("notes", "无备注");
                
                // 如果有入住人，获取入住人信息
                if (bed.getResidentId() != null) {
                    // 根据residentId查询Resident信息
                    Resident resident = this.residentService.getById(bed.getResidentId());
                    if (resident != null) {
                        bedInfo.put("residentName", resident.getName());
                        bedInfo.put("residentPhone", resident.getPhone());
                        bedInfo.put("checkInDate", resident.getEntryDate());
                        bedInfo.put("checkOutDate", resident.getExitDate());
                    } else {
                        bedInfo.put("residentName", "暂无住户");
                        bedInfo.put("residentPhone", "");
                        bedInfo.put("checkInDate", "");
                        bedInfo.put("checkOutDate", "");
                    }
                } else {
                    bedInfo.put("residentName", "暂无住户");
                    bedInfo.put("residentPhone", "");
                    bedInfo.put("checkInDate", "");
                    bedInfo.put("checkOutDate", "");
                }
                return bedInfo;
            }).collect(Collectors.toList());
            result.put("success", true);
            result.put("data", bedInfoList);
            result.put("message", "获取所有床位成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有床位失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取床位列表（支持分页、房间ID和状态搜索）
     */
    @GetMapping("/list")
    public Map<String, Object> getBedList(
            @RequestParam(name = "roomId", required = false) Integer roomId,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<Bed> bedList = bedService.getBedList(roomId, status, page, pageSize);
            result.put("success", true);
            result.put("data", bedList.getRecords());
            result.put("total", bedList.getTotal());
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取床位列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取床位列表失败：" + e.getMessage());
        }
        return result;
    }
    
    @PostMapping
    public Map<String, Object> addBed(@RequestBody Bed bed){
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = this.bedService.save(bed);
            result.put("success", success);
            if (success) {
                result.put("message", "添加床位成功");
            } else {
                result.put("message", "添加床位失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加床位失败：" + e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping
    public Map<String, Object> deleteBedById(@RequestParam("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = this.bedService.removeById(id);
            result.put("success", success);
            if (success) {
                result.put("message", "删除床位成功");
            } else {
                result.put("message", "删除床位失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除床位失败：" + e.getMessage());
        }
        return result;
    }
    
    @PutMapping
    public Map<String, Object> updateBed(@RequestBody Map<String, Object> bedMap){
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查床位ID是否存在
            Integer bedId = (Integer) bedMap.get("id");
            if (bedId == null) {
                result.put("success", false);
                result.put("message", "床位ID不能为空");
                return result;
            }
            
            // 检查是否有住户
            Object residentIdObj = bedMap.get("residentId");
            Integer residentId = residentIdObj != null && !"null".equals(residentIdObj) ? 
                                (residentIdObj instanceof String ? Integer.parseInt((String) residentIdObj) : (Integer) residentIdObj) : null;
            
            if (residentId != null) {
                // 查找该老人当前所在的所有床位
                LambdaQueryWrapper<Bed> oldBedWrapper = new LambdaQueryWrapper<>();
                oldBedWrapper.eq(Bed::getResidentId, residentId);
                oldBedWrapper.ne(Bed::getId, bedId); // 排除当前要更新的床位
                List<Bed> oldBeds = this.bedService.list(oldBedWrapper);
                
                // 如果该老人已经在其他床位，将所有原来的床位设置为空闲
                for (Bed oldBed : oldBeds) {
                    // 使用UpdateWrapper强制更新null值
                    UpdateWrapper<Bed> wrapper = new UpdateWrapper<>();
                    wrapper.eq("id", oldBed.getId())
                           .set("resident_id", null)
                           .set("status", 0);
                    this.bedService.update(wrapper);
                }
                
                // 获取当前要更新的床位信息
                Bed currentBed = this.bedService.getById(bedId);
                if (currentBed != null) {
                    // 确保新床位状态为已占用
                    currentBed.setStatus(1); // 1表示已占用
                    currentBed.setResidentId(residentId);
                    
                    // 更新居民信息中的房间号和床位号
                    Resident resident = this.residentService.getById(residentId);
                    if (resident != null) {
                        // 获取当前床位所属的房间信息
                        Rooms room = roomsService.getRoomsById(currentBed.getRoomId());
                        if (room != null) {
                            // 更新居民的房间号和床位号
                            resident.setRoomNumber(room.getRoomNumber());
                            resident.setBedNumber(currentBed.getBedNumber());
                            this.residentService.updateById(resident);
                        }
                    }
                    
                    // 更新新床位信息
                    boolean success = this.bedService.updateById(currentBed);
                    result.put("success", success);
                    if (success) {
                        result.put("message", "更新床位成功");
                    } else {
                        result.put("message", "更新床位失败");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "床位不存在");
                }
            } else {
                // 如果没有住户，确保床位状态为空闲
                // 使用UpdateWrapper强制更新null值
                UpdateWrapper<Bed> wrapper = new UpdateWrapper<>();
                wrapper.eq("id", bedId)
                       .set("status", 0)
                       .set("resident_id", null);
                boolean success = this.bedService.update(wrapper);
                
                result.put("success", success);
                if (success) {
                    result.put("message", "更新床位成功");
                } else {
                    result.put("message", "更新床位失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息
            result.put("success", false);
            result.put("message", "更新床位失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 修复重复的resident_id问题：确保一个老人只能占用一个床位
     * 保留第一个出现的resident_id记录，其他相同resident_id的床位设为空闲
     */
    @PostMapping("/fix-duplicate-resident")
    public Map<String, Object> fixDuplicateResident() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取所有床位，按resident_id分组，按update_time降序排序
            List<Bed> allBeds = this.bedService.list(new LambdaQueryWrapper<Bed>().orderByAsc(Bed::getResidentId).orderByDesc(Bed::getUpdateTime));
            
            // 使用Map记录每个resident_id已处理的床位
            Map<Integer, Boolean> processedMap = new HashMap<>();
            
            // 修复重复的床位
            int fixedCount = 0;
            for (Bed bed : allBeds) {
                if (bed.getResidentId() != null) {
                    if (!processedMap.containsKey(bed.getResidentId())) {
                        // 第一个出现的resident_id（最新的），保留
                        processedMap.put(bed.getResidentId(), true);
                    } else {
                        // 不是第一个出现的，设为空闲
                        bed.setStatus(0);
                        bed.setResidentId(null);
                        this.bedService.updateById(bed);
                        fixedCount++;
                    }
                }
            }
            
            result.put("success", true);
            result.put("message", "修复完成，共处理了" + fixedCount + "个重复床位");
            result.put("fixedCount", fixedCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修复失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有房间列表
     */
    @GetMapping("/available-rooms")
    public Map<String, Object> getAvailableRooms() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取所有房间
            List<Rooms> allRooms = roomsService.list();
            
            // 转换为前端需要的格式
            List<Map<String, Object>> roomList = allRooms.stream().map(room -> {
                Map<String, Object> roomInfo = new HashMap<>();
                roomInfo.put("roomId", room.getId());
                roomInfo.put("roomNumber", room.getRoomNumber());
                roomInfo.put("capacity", room.getBedCount());
                return roomInfo;
            }).collect(Collectors.toList());
            
            result.put("success", true);
            result.put("data", roomList);
            result.put("message", "获取房间列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取房间列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据房间ID获取空闲床位列表
     */
    @GetMapping("/available-beds/{roomId}")
    public Map<String, Object> getAvailableBedsByRoomId(@PathVariable("roomId") Integer roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取指定房间内的空闲床位（status=0）
            List<Bed> availableBeds = this.bedService.list(new LambdaQueryWrapper<Bed>()
                    .eq(Bed::getRoomId, roomId)
                    .eq(Bed::getStatus, 0)
                    .orderByAsc(Bed::getBedNumber));
            
            // 转换为前端需要的格式
            List<Map<String, Object>> bedList = availableBeds.stream().map(bed -> {
                Map<String, Object> bedInfo = new HashMap<>();
                bedInfo.put("bedId", bed.getId());
                bedInfo.put("bedNumber", bed.getBedNumber());
                return bedInfo;
            }).collect(Collectors.toList());
            
            result.put("success", true);
            result.put("data", bedList);
            result.put("message", "获取空闲床位列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取空闲床位列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据房间号获取空闲床位列表
     */
    @GetMapping("/available-beds-by-number/{roomNumber}")
    public Map<String, Object> getAvailableBedsByRoomNumber(@PathVariable("roomNumber") String roomNumber) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 先根据房间号获取房间ID
            Rooms room = roomsService.getOne(new LambdaQueryWrapper<Rooms>().eq(Rooms::getRoomNumber, roomNumber));
            if (room == null) {
                result.put("success", false);
                result.put("message", "房间不存在");
                return result;
            }
            
            // 获取指定房间内的空闲床位（status=0）
            List<Bed> availableBeds = this.bedService.list(new LambdaQueryWrapper<Bed>()
                    .eq(Bed::getRoomId, room.getId())
                    .eq(Bed::getStatus, 0)
                    .orderByAsc(Bed::getBedNumber));
            
            // 转换为前端需要的格式
            List<Map<String, Object>> bedList = availableBeds.stream().map(bed -> {
                Map<String, Object> bedInfo = new HashMap<>();
                bedInfo.put("bedId", bed.getId());
                bedInfo.put("bedNumber", bed.getBedNumber());
                return bedInfo;
            }).collect(Collectors.toList());
            
            result.put("success", true);
            result.put("data", bedList);
            result.put("message", "获取空闲床位列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取空闲床位列表失败：" + e.getMessage());
        }
        return result;
    }
}
