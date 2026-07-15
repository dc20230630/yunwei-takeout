package com.yunwei.mapper;

import com.yunwei.pojo.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜品口味数据库操作。
 */
@Mapper
public interface DishFlavorMapper {
    /**
     * 批量保存一个菜品的口味。
     */
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);


    /**
     * 根据菜品 id 集合，批量删除关联口味。
     *
      * @param dishIds 被删除菜品的 id 集合
     */
    void deleteByDishIds(@Param("dishIds") List<Long> dishIds);

    /**
     * 查询指定菜品的全部口味，用于编辑回显。
     */
    List<DishFlavor> selectByDishId(@Param("dishId") Long dishId);

    /**
     * 删除指定菜品的旧口味。
     */
    void deleteByDishId(@Param("dishId") Long dishId);
}
