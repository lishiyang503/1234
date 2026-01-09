package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 护理记录实体类
 */
@Data
@TableName("nursing_records")
public class NursingRecord implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 护理记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 老人ID（关联resident表）
     */
    private Integer residentId;

    /**
     * 老人姓名
     */
    private String residentName;

    /**
     * 护理内容
     */
    private String nursingContent;

    /**
     * 关联护理内容ID
     */
    private Integer nursingContentId;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    /**
     * 执行人
     */
    private String executor;

    /**
     * 是否完成（1-已完成，0-未完成）
     */
    private Integer completed;

    // 自定义setter方法，处理字符串到Integer的转换
    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    // 处理字符串类型的setter方法，用于兼容前端发送的字符串格式
    public void setCompleted(String completed) {
        if (completed != null) {
            this.completed = Integer.parseInt(completed);
        }
    }

    // 处理布尔类型的setter方法，用于兼容前端发送的布尔格式
    public void setCompleted(Boolean completed) {
        if (completed != null) {
            this.completed = completed ? 1 : 0;
        }
    }

    /**
     * 效果评估（1-差，2-中，3-好）
     */
    private Integer evaluation;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}