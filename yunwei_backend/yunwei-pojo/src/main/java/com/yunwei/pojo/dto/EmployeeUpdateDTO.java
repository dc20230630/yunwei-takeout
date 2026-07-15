package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改员工信息的请求参数。
 */
@Data
public class EmployeeUpdateDTO {
    // 要修改的员工 ID
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "性别不能为空")
    private String sex;

    @NotBlank(message = "身份证号不能为空")
    private String idNumber;
}
