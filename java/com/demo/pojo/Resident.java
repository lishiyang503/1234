package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 入住登记表实体类
 */
@Data
@TableName("residents")
public class Resident implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 紧急联系人
     */
    @TableField("emergency_contact")
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    @TableField("emergency_phone")
    private String emergencyPhone;

    /**
     * 入住日期
     */
    @TableField("entry_date")
    private Date entryDate;

    /**
     * 退房日期
     */
    @TableField("exit_date")
    private Date exitDate;

    /**
     * 房间号
     */
    @TableField("room_number")
    private String roomNumber;

    /**
     * 床位号
     */
    @TableField("bed_number")
    private String bedNumber;

    /**
     * 状态："退房"或"入住"
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 关联用户ID（users.id）
     */
    @TableField("user_id")
    private Long userId;
}
