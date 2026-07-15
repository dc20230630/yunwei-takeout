package com.yunwei.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 套餐表的最小查询接口。
 * 仅用于删除分类前检查是否仍关联套餐。
 */
@Mapper
public interface SetmealMapper {

    /**
     * 统计指定分类下的套餐数量。
     */
    @Select("SELECT COUNT(id) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(@Param("categoryId") Long categoryId);
}
