package com.yunwei.pojo.vo;

import lombok.Data;

/**
 * 编辑员工时返回的详情数据。
 * 不返回密码和账号状态。
 */
@Data
public class EmployeeDetailVo {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;
}