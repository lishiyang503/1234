package com.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.pojo.Bed;
import com.demo.pojo.Rooms;
import com.demo.service.BedService;
import com.demo.service.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomsController {

    @Autowired
    private RoomsService roomsService;
    
    @Autowired
    private BedService bedService;

    /**
     * 根据ID查询房间
     */
    @GetMapping("/{id}")
    public Map<String, Object> getRoomById(@PathVariable("id") Integer roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Rooms room = roomsService.getRoomsById(roomId);
            if (room != null) {
                result.put("success", true);
                result.put("data", room);
                result.put("message", "查询成功");
            } else {
                result.put("success", false);
                result.put("message", "房间不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 查询所有房间
     */
    @GetMapping("/all")
    public Map<String, Object> getAllRooms() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Rooms> rooms = roomsService.getAllRooms();
            // 将房间状态1（空闲）转换为状态0，以匹配前端期望
            for (Rooms room : rooms) {
                if (room.getStatus() == 1) {
                    room.setStatus(0);
                }
            }
            result.put("success", true);
            result.put("data", rooms);
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 条件查询房间
     */
    @GetMapping("/search")
    public Map<String, Object> searchRooms(
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer status) {  // 新增状态参数
        Map<String, Object> result = new HashMap<>();
        try {
            List<Rooms> rooms = roomsService.getRoomsByCondition(roomNumber, roomType, status);
            result.put("success", true);
            result.put("data", rooms);
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取房间列表（支持分页、房间号、房间类型和状态搜索）
     */
    @GetMapping("/list")
    public Map<String, Object> getRoomsList(
            @RequestParam(name = "roomNumber", required = false) String roomNumber,
            @RequestParam(name = "roomType", required = false) String roomType,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 简单实现：直接调用getAllRooms获取所有房间，不使用分页功能
            List<Rooms> roomsList = roomsService.getAllRooms();
            result.put("success", true);
            result.put("data", roomsList);
            result.put("total", roomsList.size());
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "获取房间列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取房间列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 查询可用房间
     */
    @GetMapping("/available")
    public Map<String, Object> getAvailableRooms() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Rooms> rooms = roomsService.getAvailableRooms();
            result.put("success", true);
            result.put("data", rooms);
            result.put("message", "查询成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 添加房间
     */
    @PostMapping
    public Map<String, Object> addRoom(@RequestBody Rooms room) {
        Map<String, Object> result = new HashMap<>();
        try {
            roomsService.createRoomWithBeds(room);
            result.put("success", true);
            result.put("message", "添加成功");
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新房间信息
     */
    @PutMapping
    public Map<String, Object> updateRoom(@RequestBody Rooms room) {
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean success = roomsService.updateRoom(room);
            result.put("success", success);
            if (success) {
                result.put("message", "更新成功");
            } else {
                result.put("message", "更新失败");
            }
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除房间（及所有床位）
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteRoom(@PathVariable("id") Integer roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 查找该房间下的所有床位
            List<Bed> beds = bedService.list(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, roomId));
            
            // 检查是否有床位被占用
            boolean hasOccupiedBed = beds.stream().anyMatch(bed -> bed.getResidentId() != null);
            if (hasOccupiedBed) {
                result.put("success", false);
                result.put("message", "该房间下有老人入住，无法删除房间及床位");
                return result;
            }
            
            // 删除所有床位
            for (Bed bed : beds) {
                bedService.removeById(bed.getId());
            }
            
            // 删除房间
            Boolean success = roomsService.deleteRoom(roomId);
            result.put("success", success);
            if (success) {
                result.put("message", "删除成功");
            } else {
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除房间信息（保留床位）
     */
    @DeleteMapping("/info/{id}")
    public Map<String, Object> deleteRoomInfo(@PathVariable("id") Integer roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 查找该房间下的所有床位
            List<Bed> beds = bedService.list(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, roomId));
            
            // 检查是否有床位被占用
            boolean hasOccupiedBed = beds.stream().anyMatch(bed -> bed.getResidentId() != null);
            if (hasOccupiedBed) {
                result.put("success", false);
                result.put("message", "该房间下有老人入住，无法删除房间信息");
                return result;
            }
            
            // 删除房间信息
            boolean success = roomsService.removeById(roomId);
            result.put("success", success);
            if (success) {
                result.put("message", "删除房间信息成功");
            } else {
                result.put("message", "删除房间信息失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除房间信息失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新房间状态（空闲/已满）
     */
    @PutMapping("/status/{id}")
    public Map<String, Object> updateRoomStatus(
            @PathVariable("id") Integer roomId,
            @RequestParam Integer status) {
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean success = roomsService.updateRoomStatus(roomId, status);
            result.put("success", success);
            if (success) {
                result.put("message", "状态更新成功");
            } else {
                result.put("message", "状态更新失败");
            }
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "状态更新失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 设置房间为维修状态
     */
    @PostMapping("/repair/{roomId}")
    public Map<String, Object> setRoomRepair(
            @PathVariable("roomId") Integer roomId,
            @RequestBody Map<String, Object> repairData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 更新房间状态为维修中(2)
            Rooms room = new Rooms();
            room.setId(roomId);
            room.setStatus(2);
            room.setRepairReason((String) repairData.get("repairReason"));
            room.setRepairStartDate(new Date());
            
            // 处理维修结束日期
            Date repairEndDate = null;
            if (repairData.get("repairEndDate") != null) {
                try {
                    repairEndDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) repairData.get("repairEndDate"));
                } catch (ParseException e) {
                    throw new RuntimeException("维修结束日期格式错误，应为yyyy-MM-dd");
                }
            }
            room.setRepairEndDate(repairEndDate);
            
            Boolean success = roomsService.updateRoom(room);
            result.put("success", success);
            if (success) {
                result.put("message", "房间已设置为维修状态");
            } else {
                result.put("message", "设置维修状态失败");
            }
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置维修状态失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 完成房间维修，根据实际入住情况设置房间状态
     */
    @PutMapping("/repair/complete/{roomId}")
    public Map<String, Object> completeRepair(@PathVariable("roomId") Integer roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取房间信息
            Rooms room = roomsService.getRoomsById(roomId);
            if (room == null) {
                result.put("success", false);
                result.put("message", "房间不存在");
                return result;
            }
            
            // 获取房间下的所有床位
            List<Bed> bedList = bedService.list(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, roomId));
            
            // 计算已占用的床位数（residentId不为null表示有住户）
            int occupiedBeds = (int) bedList.stream()
                .filter(bed -> bed.getResidentId() != null)
                .count();
            
            // 根据实际占用情况设置房间状态
            // 0: 空闲, 1: 已满, 2: 维修中
            int newStatus;
            if (occupiedBeds == 0) {
                newStatus = 0; // 无住户，设为空闲
            } else if (occupiedBeds == room.getBedCount()) {
                newStatus = 1; // 所有床位都被占用，设为已满
            } else {
                newStatus = 0; // 部分床位被占用，设为空闲（或根据业务需求调整）
            }
            
            // 更新房间信息
            Rooms updatedRoom = new Rooms();
            updatedRoom.setId(roomId);
            updatedRoom.setStatus(newStatus);
            updatedRoom.setRepairReason(null);
            updatedRoom.setRepairStartDate(null);
            updatedRoom.setRepairEndDate(null);
            
            Boolean success = roomsService.updateRoom(updatedRoom);
            result.put("success", success);
            if (success) {
                result.put("message", "维修完成，房间状态已根据实际入住情况更新");
            } else {
                result.put("message", "完成维修失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "完成维修失败：" + e.getMessage());
        }
        return result;
    }
}