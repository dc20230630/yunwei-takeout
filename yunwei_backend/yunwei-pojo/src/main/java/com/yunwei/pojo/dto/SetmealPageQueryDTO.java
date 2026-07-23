package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理端套餐分页查询条件。
 */
@Data
public class SetmealPageQueryDTO {

    @NotNull(message = "页码不能为空")
    private Integer page;

    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    private String name;
    private Long categoryId;
    private Integer status;
}
