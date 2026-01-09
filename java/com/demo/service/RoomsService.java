package com.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.pojo.Rooms;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @author 11516
 * @description 针对表【rooms】的数据库操作Service
 * @createDate 2025-12-30 15:48:00
 */
public interface RoomsService extends IService<Rooms> {
    Rooms getRoomsById(Integer roomId);
    List<Rooms> getAllRooms();
    List<Rooms> getRoomsByCondition(String roomNumber, String roomType, Integer status);
    Boolean addRoom(Rooms room);
    void createRoomWithBeds(Rooms room);
    Boolean updateRoom(Rooms room);
    Boolean deleteRoom(Integer roomId);
    Boolean updateRoomStatus(Integer roomId, Integer status);
    List<Rooms> getAvailableRooms();
    IPage<Rooms> getRoomsList(String roomNumber, String roomType, Integer status, Integer page, Integer pageSize);
}