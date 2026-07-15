package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishPageQueryDTO {

    // 当前页码，例如第 1 页
    @NotNull(message = "页码不能为空")
    private Integer page;

    // 每页显示多少条
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

    // 菜品名称，模糊查询
    private String name;

    // 菜品分类 id
    private Long categoryId;
}