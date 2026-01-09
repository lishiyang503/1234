package com.demo.service;

import com.demo.pojo.User;

public interface UserService {
    User login(String username, String password);
    User getUserByUsername(String username);
    boolean register(String username, String password, String realName, String phone, String role);
    boolean registerElder(String username, String password, String realName, String idCard, String phone);
}