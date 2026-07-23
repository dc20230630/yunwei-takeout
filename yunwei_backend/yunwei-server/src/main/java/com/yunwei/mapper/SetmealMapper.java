package com.yunwei.mapper;

import com.yunwei.annotation.AutoFill;
import com.yunwei.common.enumeration.Operation;
import com.yunwei.pojo.dto.SetmealPageQueryDTO;
import com.yunwei.pojo.entity.Setmeal;
import com.yunwei.pojo.vo.DishItemVo;
import com.yunwei.pojo.vo.SetmealVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @AutoFill(Operation.INSERT)
    void insert(Setmeal setmeal);

    @AutoFill(Operation.UPDATE)
    void update(Setmeal setmeal);

    List<SetmealVo> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    Setmeal selectById(@Param("id") Long id);

    void deleteByIds(@Param("ids") List<Long> ids);

    SetmealVo getByIdWithDish(@Param("id") Long id);

    /**
     * 按分类和售卖状态查询套餐。
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 查询套餐中包含的菜品。
     */
    List<DishItemVo> getDishItemBySetmealId(@Param("setmealId") Long setmealId);
}
