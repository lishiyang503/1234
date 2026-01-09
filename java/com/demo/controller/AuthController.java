package com.demo.controller;

import com.demo.pojo.User;
import com.demo.service.UserService;
import org.example.springboot_vaue_demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            // 用户名登录
            User user = userService.login(username, password);
            
            if (user != null) {
                // 生成token
                String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
                
                result.put("success", true);
                result.put("data", Map.of(
                    "token", token,
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
                ));
                result.put("message", "登录成功");
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登录失败：" + e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证token
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (JwtUtil.validateToken(token)) {
                    String username = JwtUtil.getUsernameFromToken(token);
                    
                    // 获取完整的用户信息
                    User user = userService.getUserByUsername(username);
                    if (user != null) {
                        result.put("success", true);
                        result.put("data", Map.of(
                            "username", user.getUsername(),
                            "role", user.getRole(),
                            "realName", user.getRealName(),
                            "idCard", user.getIdCard(),
                            "phone", user.getPhone()
                        ));
                        result.put("message", "获取用户信息成功");
                    } else {
                        result.put("success", false);
                        result.put("message", "用户不存在");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "token无效");
                }
            } else {
                result.put("success", false);
                result.put("message", "token不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户信息失败：" + e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> registerRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("接收到注册请求: " + registerRequest);
            
            String username = (String) registerRequest.get("username");
            String password = (String) registerRequest.get("password");
            String phone = (String) registerRequest.get("phone");
            String realName = (String) registerRequest.get("realName");
            String idCard = (String) registerRequest.get("idCard");
            String role = (String) registerRequest.get("role");
            
            System.out.println("注册请求参数: username=" + username + ", password=" + password + ", phone=" + phone + ", realName=" + realName + ", idCard=" + idCard + ", role=" + role);
            
            boolean success = false;
            if ("elder".equals(role)) {
                // 老人注册
                System.out.println("执行老人注册");
                success = userService.registerElder(username, password, realName, idCard, phone);
            } else {
                // 普通用户注册
                System.out.println("执行普通用户注册");
                success = userService.register(username, password, realName, phone, role);
            }
            
            System.out.println("注册结果: " + success);
            
            if (success) {
                result.put("success", true);
                result.put("message", "注册成功");
            } else {
                if (phone != null && !phone.isEmpty()) {
                    result.put("message", "手机号已存在");
                } else {
                    result.put("message", "用户名已存在");
                }
                result.put("success", false);
            }
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "注册失败：" + e.getMessage());
        }
        
        System.out.println("返回注册结果: " + result);
        return result;
    }
}