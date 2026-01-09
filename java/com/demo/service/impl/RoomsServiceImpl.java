package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.pojo.Bed;
import com.demo.pojo.Rooms;
import com.demo.service.BedService;
import com.demo.service.RoomsService;
import com.demo.mapper.RoomsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;

/**
 * @author 11516
 * @description 针对表【rooms】的数据库操作Service实现
 * @createDate 2025-12-30 15:48:00
 */
@Service
public class RoomsServiceImpl extends ServiceImpl<RoomsMapper, Rooms> implements RoomsService {

    @Autowired
    private RoomsMapper roomsMapper;
    
    @Autowired
    private BedService bedService;

    @Override
    public Rooms getRoomsById(Integer roomId) {
        return roomsMapper.selectById(roomId);
    }

    @Override
    public List<Rooms> getAllRooms() {
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Rooms::getId);
        return roomsMapper.selectList(wrapper);
    }

    @Override
    public List<Rooms> getRoomsByCondition(String roomNumber, String roomType, Integer status) {
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(roomNumber)) {
            wrapper.like(Rooms::getRoomNumber, roomNumber);
        }

        if (StringUtils.isNotBlank(roomType)) {
            wrapper.eq(Rooms::getRoomType, roomType);
        }
        
        if (status != null) {
            wrapper.eq(Rooms::getStatus, status);
        }

        wrapper.orderByAsc(Rooms::getId);
        return roomsMapper.selectList(wrapper);
    }
    
    @Override
    public IPage<Rooms> getRoomsList(String roomNumber, String roomType, Integer status, Integer page, Integer pageSize) {
        Page<Rooms> roomsPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(roomNumber)) {
            wrapper.like(Rooms::getRoomNumber, roomNumber);
        }
        
        if (StringUtils.isNotBlank(roomType)) {
            wrapper.eq(Rooms::getRoomType, roomType);
        }
        
        if (status != null) {
            wrapper.eq(Rooms::getStatus, status);
        }
        
        wrapper.orderByAsc(Rooms::getId);
        return this.page(roomsPage, wrapper);
    }

    @Override
    @Transactional
    public Boolean addRoom(Rooms room) {
        // 检查房间号是否已存在
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rooms::getRoomNumber, room.getRoomNumber());
        Long count = roomsMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("房间号已存在");
        }

        // 设置默认状态为空闲
        if (room.getStatus() == null) {
            room.setStatus(0);
        }

        int result = roomsMapper.insert(room);
        if (result > 0 && room.getBedCount() != null && room.getBedCount() > 0) {
            // 自动生成床位
            int bedCount = room.getBedCount();
            String roomNumber = room.getRoomNumber();
            for (int i = 0; i < bedCount; i++) {
                Bed bed = new Bed();
                bed.setRoomId(room.getId());
                bed.setBedNumber(roomNumber + "-" + (char)('A' + i)); // 房间号-A, 房间号-B, 房间号-C
                bed.setStatus(0); // 0表示空闲
                bed.setResidentId(null);
                bedService.save(bed);
            }
        }
        return result > 0;
    }

    @Override
    @Transactional
    public void createRoomWithBeds(Rooms room) {
        // 检查房间号是否已存在
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rooms::getRoomNumber, room.getRoomNumber());
        Long count = roomsMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("房间号已存在");
        }

        // 设置默认状态为空闲
        if (room.getStatus() == null) {
            room.setStatus(0);
        }

        // 1. 先保存房间
        this.save(room);

        // 2. 拿到房间ID（这一步非常关键）
        Integer roomId = room.getId();

        // 3. 根据床位数生成床位
        int bedCount = room.getBedCount();

        List<Bed> beds = new ArrayList<>();

        for (int i = 0; i < bedCount; i++) {
            Bed bed = new Bed();
            bed.setRoomId(roomId);
            bed.setResidentId(null);
            bed.setStatus(0);

            // A / B / C / D ...
            char suffix = (char) ('A' + i);
            bed.setBedNumber(room.getRoomNumber() + "-" + suffix);

            beds.add(bed);
        }

        // 4. 批量插入床位
        bedService.saveBatch(beds);
    }

    @Override
    public Boolean updateRoom(Rooms room) {
        // 检查除当前房间外的其他房间是否有相同的房间号
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rooms::getRoomNumber, room.getRoomNumber())
                .ne(Rooms::getId, room.getId());
        Long count = roomsMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("房间号已存在");
        }

        int result = roomsMapper.updateById(room);
        return result > 0;
    }

    @Override
    public Boolean deleteRoom(Integer roomId) {
        // 可以添加业务逻辑，比如检查房间是否有床位
        int result = roomsMapper.deleteById(roomId);
        return result > 0;
    }

    @Override
    public Boolean updateRoomStatus(Integer roomId, Integer status) {
        UpdateWrapper<Rooms> wrapper = new UpdateWrapper<>();
        wrapper.set("status", status)
                .eq("id", roomId);

        int result = roomsMapper.update(null, wrapper);
        return result > 0;
    }

    @Override
    public List<Rooms> getAvailableRooms() {
        LambdaQueryWrapper<Rooms> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rooms::getStatus, 0)  // 0表示空闲
                .orderByAsc(Rooms::getId);
        return roomsMapper.selectList(wrapper);
    }
}