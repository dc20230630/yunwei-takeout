package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 管理员登录时接收前端传来的账号和密码。
 */
@Schema(description = "员工登录请求参数")
@Data
public class EmployeeLoginDTO {

    // 不允许用户名为空
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    // 不允许密码为空
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}