package com.yunwei.pojo.dto;

import lombok.Data;

/**
 * 管理端订单列表筛选条件。
 */
@Data
public class OrderAdminQueryDTO {

    private String number;
    private String phone;
    private Integer status;
}
