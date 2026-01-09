package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@TableName(value = "users")
@Data
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String role;

    private Integer status;

    @TableField("real_name")
    private String realName;

    @TableField("id_card")
    private String idCard;

    private String phone;

    @TableField("created_at")
    private Date createdAt;

    @TableField("updated_at")
    private Date updatedAt;

    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}