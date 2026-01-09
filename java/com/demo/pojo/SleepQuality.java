package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 睡眠质量记录实体类
 */
@Data
@TableName("health_sleep_quality")
public class SleepQuality implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    private Integer residentId;

    /**
     * 老人姓名
     */
    private String residentName;

    /**
     * 睡眠时长(小时)
     */
    private Double sleepDuration;

    /**
     * 深睡时长(小时)
     */
    private Double deepSleepDuration;

    /**
     * 浅睡时长(小时)
     */
    private Double lightSleepDuration;

    /**
     * 睡眠质量评分(0-100)
     */
    private Integer sleepScore;

    /**
     * 睡眠状态(良好、一般、较差)
     */
    private String sleepStatus;

    /**
     * 睡眠日期
     */
    private Date sleepDate;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}