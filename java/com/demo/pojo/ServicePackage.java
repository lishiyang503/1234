package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务套餐表实体类
 */
@Data
@TableName("service_packages")
public class ServicePackage implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 级别
     */
    private String level;

    /**
     * 价格
     */
    private Double price;

    /**
     * 描述
     */
    private String description;

    /**
     * 服务内容
     */
    private String features;

    /**
     * 状态：'禁用'，'启用'
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
