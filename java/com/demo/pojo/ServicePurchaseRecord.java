package com.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务购买记录表实体类
 */
@Data
@TableName("service_purchase_records")
public class ServicePurchaseRecord implements Serializable {

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
     * 服务套餐ID
     */
    private Integer serviceId;

    /**
     * 套餐名称
     */
    private String serviceName;

    /**
     * 套餐级别
     */
    private String serviceLevel;

    /**
     * 价格
     */
    private Double price;

    /**
     * 购买日期
     */
    private Date purchaseDate;

    /**
     * 到期日期
     */
    private Date expireDate;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 状态：'过期'，'有效'
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;
}
