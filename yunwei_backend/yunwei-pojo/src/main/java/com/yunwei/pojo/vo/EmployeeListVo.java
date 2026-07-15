package com.yunwei.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 员工列表返回数据。
 *
 * 不返回密码、创建人和修改人等敏感字段。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "员工列表返回数据")
public class EmployeeListVo {

    @Schema(description = "员工 ID")
    private Long id;

    @Schema(description = "员工姓名")
    private String name;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "账号状态，1 启用，0 禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}