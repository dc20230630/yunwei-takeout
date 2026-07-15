package com.yunwei.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "新增员工请求参数")
public class EmployeeAddDTo {
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "员工姓名")
    @NotBlank(message = "用户名不能为空")
    private String name;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "性别")
    @NotBlank(message = "性别不能为空")
    private String sex;
    
    @Schema(description = "身份证号")
    @NotBlank(message = "身份证号不能为空") 
    private String idNumber;
}
