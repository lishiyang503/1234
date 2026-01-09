package com.demo.mapper;

import com.demo.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 11516
* @description 针对表【users】的数据库操作Mapper
* @createDate 2026-01-04 18:57:51
* @Entity com.demo.pojo.User
*/
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);
}
