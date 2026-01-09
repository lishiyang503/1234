package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 健康档案表实体类
 */
@Data
@TableName("health_records")
public class HealthRecord implements Serializable {

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
     * 姓名
     */
    private String residentName;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 床位号
     */
    private String bedNumber;

    /**
     * 既往病史
     */
    private String medicalHistory;

    /**
     * 过敏史
     */
    private String allergyHistory;

    /**
     * 血型
     */
    private String bloodType;

    /**
     * 身高
     */
    private Double height;

    /**
     * 体重
     */
    private Double weight;

    /**
     * 健康备注
     */
    private String healthNotes;

    /**
     * 更新时间
     */
    private Date updateTime;
}
