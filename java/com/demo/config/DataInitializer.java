package com.demo.config;

import com.demo.mapper.UserMapper;
import com.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 数据初始化器
 * 在应用启动时创建默认管理员账号
 */
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查是否已存在管理员账号
        User adminUser = userMapper.selectByUsername("admin");
        if (adminUser == null) {
            // 创建默认管理员账号
            User newAdmin = new User();
            newAdmin.setUsername("admin");
            newAdmin.setPassword(passwordEncoder.encode("123456")); // 密码加密
            newAdmin.setRole("admin"); // 管理员角色
            newAdmin.setStatus(1); // 启用状态
            newAdmin.setRealName("管理员");
            newAdmin.setDeleted(0);
            
            userMapper.insert(newAdmin);
            System.out.println("默认管理员账号已创建: admin/123456");
        }
    }
}
