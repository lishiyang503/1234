package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 血糖记录实体类
 */
@Data
@TableName("health_blood_sugar")
public class BloodSugar implements Serializable {

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
     * 血糖值(mmol/L)
     */
    private Double level;

    /**
     * 测量状态：空腹、餐后、随机
     */
    private String status;

    /**
     * 测量时间
     */
    private Date measureTime;

    /**
     * 测量备注
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
