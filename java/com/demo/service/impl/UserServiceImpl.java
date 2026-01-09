package com.demo.service.impl;

import com.demo.mapper.UserMapper;
import com.demo.mapper.ResidentMapper;
import com.demo.mapper.HealthRecordMapper;
import com.demo.pojo.User;
import com.demo.pojo.Resident;
import com.demo.pojo.HealthRecord;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ResidentMapper residentMapper;
    
    @Autowired
    private HealthRecordMapper healthRecordMapper;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User login(String username, String password) {
        try {
            System.out.println("登录尝试: username=" + username + ", password=" + password);
            User user = userMapper.selectByUsername(username);
            System.out.println("用户查询结果: " + (user != null ? "存在" : "不存在"));
            if (user != null) {
                System.out.println("用户密码: " + user.getPassword());
                System.out.println("密码长度: " + user.getPassword().length());
                System.out.println("密码是否为BCrypt格式: " + user.getPassword().startsWith("$2a$"));
                
                boolean passwordMatch = false;
                // 检查密码是否为BCrypt格式
                if (user.getPassword().startsWith("$2a$")) {
                    // 使用BCrypt验证
                    passwordMatch = passwordEncoder.matches(password, user.getPassword());
                } else {
                    // 直接比较明文密码（用于兼容旧数据）
                    passwordMatch = password.equals(user.getPassword());
                    // 如果验证成功，更新密码为BCrypt格式
                    if (passwordMatch) {
                        user.setPassword(passwordEncoder.encode(password));
                        userMapper.updateById(user);
                        System.out.println("密码已更新为BCrypt格式");
                    }
                }
                
                // 验证密码和用户状态
                if (passwordMatch && user.getStatus() == 1 && user.getDeleted() == 0) {
                    System.out.println("密码验证成功");
                    return user;
                } else {
                    System.out.println("密码验证失败");
                    System.out.println("用户状态: " + user.getStatus());
                    System.out.println("用户删除状态: " + user.getDeleted());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    @Override
    public boolean register(String username, String password, String realName, String phone, String role) {
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.selectByUsername(username);
            if (existingUser != null) {
                return false; // 用户名已存在
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password)); // 密码加密
            newUser.setRole(role);
            newUser.setStatus(1); // 默认状态为启用
            newUser.setRealName(realName);
            newUser.setPhone(phone);
            newUser.setDeleted(0);
            
            // 保存用户
            int result = userMapper.insert(newUser);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    @Override
    public boolean registerElder(String username, String password, String realName, String idCard, String phone) {
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.selectByUsername(username);
            if (existingUser != null) {
                return false; // 用户名已存在
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password)); // 密码加密
            newUser.setRole("elder"); // 老人角色
            newUser.setStatus(1); // 默认状态为启用
            newUser.setRealName(realName);
            newUser.setIdCard(idCard);
            newUser.setPhone(phone);
            newUser.setDeleted(0);
            
            // 保存用户
            int userResult = userMapper.insert(newUser);
            if (userResult <= 0) {
                return false;
            }
            
            // 创建老人档案
            Resident resident = new Resident();
            resident.setUserId(newUser.getId());
            resident.setName(realName);
            resident.setIdCard(idCard);
            resident.setGender("未知"); // 设置默认性别
            resident.setAge(0); // 设置默认年龄
            resident.setPhone(phone);
            resident.setEmergencyContact("无"); // 设置默认紧急联系人
            resident.setEmergencyPhone("无"); // 设置默认紧急联系人电话
            resident.setEntryDate(new Date());
            resident.setExitDate(null); // 设置默认退房日期
            resident.setRoomNumber("无"); // 设置默认房间号
            resident.setBedNumber("无"); // 设置默认床位号
            resident.setStatus("入住");
            resident.setCreateTime(new Date());
            resident.setUpdateTime(new Date());
            
            // 保存老人档案
            int residentResult = residentMapper.insert(resident);
            if (residentResult <= 0) {
                return false;
            }
            
            // 创建健康档案
            HealthRecord healthRecord = new HealthRecord();
            healthRecord.setResidentId(resident.getId());
            healthRecord.setResidentName(realName);
            healthRecord.setRoomNumber("无"); // 设置默认房间号
            healthRecord.setBedNumber("无"); // 设置默认床位号
            healthRecord.setMedicalHistory("无"); // 设置默认既往病史
            healthRecord.setAllergyHistory("无"); // 设置默认过敏史
            healthRecord.setBloodType("未知"); // 设置默认血型
            healthRecord.setHeight(0.0); // 设置默认身高
            healthRecord.setWeight(0.0); // 设置默认体重
            healthRecord.setHealthNotes("无"); // 设置默认健康状况
            healthRecord.setUpdateTime(new Date());
            
            // 保存健康档案
            int healthRecordResult = healthRecordMapper.insert(healthRecord);
            return healthRecordResult > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("注册失败：" + e.getMessage());
        }
    }
}