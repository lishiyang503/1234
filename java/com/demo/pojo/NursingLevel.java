package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 护理级别实体类
 */
@Data
@TableName("nursing_levels")
public class NursingLevel implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 护理级别ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 级别名称
     */
    private String name;

    /**
     * 级别（基础、中级、高级、VIP、SVIP）
     */
    private String level;

    /**
     * 级别描述
     */
    private String description;

    /**
     * 基础费用（元/月）
     */
    private BigDecimal baseCost;
    
    /**
     * 负责该护理级别的护工姓名
     */
    @TableField("nurse_name")
    private String nurseName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态（1-启用，0-禁用）
     */
    private Integer status;
}