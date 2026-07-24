package com.yunwei.pojo.entity;

import lombok.Data;

/**
 * 地址簿实体，对应 address_book 表。
 */
@Data
public class AddressBook {

    private Long id;
    private Long userId;
    private String consignee;
    private String sex;
    private String phone;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String detail;
    private String label;
    private Integer isDefault;
}
