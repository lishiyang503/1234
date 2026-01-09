package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 入住历史表实体类
 */
@Data
@TableName("resident_history")
public class ResidentHistory implements Serializable {

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
     * 入住日期
     */
    private Date entryDate;

    /**
     * 退房日期
     */
    private Date exitDate;

    /**
     * 入住时长
     */
    private Integer duration;

    /**
     * 退房原因
     */
    private String reason;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间
     */
    private Date createTime;
}
