package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName rooms
 */
@TableName(value ="rooms")
@Data
public class Rooms implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String roomNumber;

    private String roomType;

    private Integer bedCount;

    private Integer status;

    private String description;

    private Date createTime;

    private Date updateTime;
    
    /**
     * 维修原因
     */
    private String repairReason;
    
    /**
     * 维修开始日期
     */
    private Date repairStartDate;
    
    /**
     * 维修结束日期
     */
    private Date repairEndDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}