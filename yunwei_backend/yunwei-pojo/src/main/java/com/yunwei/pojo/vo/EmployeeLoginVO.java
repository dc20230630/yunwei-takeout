package com.yunwei.pojo.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "员工登录返回的数据格式")
public class EmployeeLoginVO {
    @Schema(description = "主键值")
    private Long id; // 员工 ID
    @Schema(description = "员工姓名")
    private String name; // 员工姓名
    @Schema(description = "用户名")
    private String username; // 登录账号
    @Schema(description = "JWT 登录令牌")
    private String token; // JWT 登录令牌
    @Schema(description = "Token 剩余有效时间，单位：秒")
    private Long expiresIn; // Token 剩余有效时间，单位：秒
}
