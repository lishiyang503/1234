package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 病史记录实体类
 */
@Data
@TableName("health_medical_history")
public class MedicalHistory implements Serializable {

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
     * 记录类型：疾病诊断、手术记录
     */
    private String recordType;

    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * 诊断日期
     */
    private Date diagnoseDate;

    /**
     * 医院
     */
    private String hospital;

    /**
     * 医生
     */
    private String doctor;

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
