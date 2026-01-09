package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 健康预警实体类
 */
@Data
@TableName("health_alerts")
public class HealthAlert implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 老人ID
     */
    private Integer residentId;

    /**
     * 老人姓名
     */
    private String residentName;

    /**
     * 预警类型：血压异常、血糖异常、心率异常
     */
    private String alertType;

    /**
     * 预警级别：低、中、高
     */
    private String alertLevel;

    /**
     * 预警标题
     */
    private String title;

    /**
     * 预警描述
     */
    private String description;

    /**
     * 测量时间
     */
    private Date measureTime;

    /**
     * 是否处理
     */
    private Integer isHandled;

    /**
     * 处理时间
     */
    private Date handledTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
