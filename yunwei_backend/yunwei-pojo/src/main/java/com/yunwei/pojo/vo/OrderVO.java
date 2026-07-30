package com.yunwei.pojo.vo;

import com.yunwei.pojo.entity.OrderDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户端订单展示数据。
 */
@Data
public class OrderVO {

    private Long id;
    private String number;
    private Integer status;
    private Integer payMethod;
    private Integer payStatus;
    private BigDecimal amount;
    private String remark;
    private String consignee;
    private String phone;
    private String address;
    private String cancelReason;
    private LocalDateTime orderTime;
    private LocalDateTime checkoutTime;
    private Integer deliveryStatus;
    private LocalDateTime deliveryTime;
    private List<OrderDetail> orderDetails;
}
