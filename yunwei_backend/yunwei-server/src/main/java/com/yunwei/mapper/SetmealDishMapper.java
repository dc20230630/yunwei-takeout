package com.yunwei.mapper;

import com.yunwei.pojo.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 统计指定菜品被套餐关联的次数。
     *
     * @param ids 待删除菜品的 id 集合
     * @return 关联数量
     */
    Integer countByDishIds(@Param("ids") List<Long> ids);

    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    void deleteBySetmealIds(@Param("setmealIds") List<Long> setmealIds);

    Integer countDisabledDishBySetmealId(@Param("setmealId") Long setmealId,
                                         @Param("status") Integer status);
}
