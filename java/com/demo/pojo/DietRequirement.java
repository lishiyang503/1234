package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 膳食需求表实体类
 */
@Data
@TableName("diet_requirements")
public class DietRequirement implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 显示用序号（1,2,3,4...）
     */
    @TableField(exist = false)
    private Integer displayId;

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
     * 饮食类型
     */
    @TableField("diet_type")
    private String dietType;

    /**
     * 需求描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
