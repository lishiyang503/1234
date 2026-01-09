package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 护理内容实体类
 */
@Data
@TableName("nursing_contents")
public class NursingContent implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 护理内容ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 内容名称
     */
    private String name;

    /**
     * 护理类别（1-日常护理，2-医疗护理，3-康复训练）
     */
    private Integer category;

    /**
     * 上级内容ID（0表示无上级）
     */
    private Integer parentId;

    /**
     * 适用护理级别
     */
    @TableField(exist = false)
    private String applicableLevel;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 护理时长（分钟）
     */
    private Integer duration;

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

    /**
     * 项目代码
     */
    @TableField(exist = false)
    private String code;

    /**
     * 预计时长（分钟）
     */
    @TableField(exist = false)
    private Integer estimatedDuration;

    /**
     * 服务频率
     */
    @TableField(exist = false)
    private String frequency;

    /**
     * 服务流程
     */
    @TableField(exist = false)
    private String procedure;

    /**
     * 注意事项
     */
    @TableField(exist = false)
    private String notes;
}