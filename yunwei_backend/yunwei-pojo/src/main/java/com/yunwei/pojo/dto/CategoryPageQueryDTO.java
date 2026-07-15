package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类分页查询的请求参数。
 */
@Data
public class CategoryPageQueryDTO {

    @NotNull(message = "页码不能为空")
    private Integer page;

    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    // 按分类名称模糊查询
    private String name;

    // 1 表示菜品分类，2 表示套餐分类
    private Integer type;
}
