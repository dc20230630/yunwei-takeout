package com.yunwei.mapper;

import com.yunwei.annotation.AutoFill;
import com.yunwei.common.enumeration.Operation;
import com.yunwei.pojo.dto.DishPageQueryDTO;
import com.yunwei.pojo.entity.Dish;
import com.yunwei.pojo.vo.DishVo;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜品表的最小查询接口。
 * 仅用于删除分类前检查是否仍关联菜品。
 */
@Mapper
public interface DishMapper {

    /**
     * 统计指定分类下的菜品数量。
     */
    @Select("SELECT COUNT(id) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据名称查询菜品，用于避免重复新增。
     */
    Dish selectByName(@Param("name") String name);

    @AutoFill(Operation.INSERT)
    void insert(Dish dish);


    // 分页查询菜品列表
    List<DishVo> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 统计指定菜品中，仍处于起售状态的数量。
     *
     * @param ids 要删除的菜品 id 集合
     * @param status 菜品状态，1 表示起售
     */
    Integer countByIdsAndStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 批量删除菜品。
     *
     * @param ids 要删除的菜品 id 集合
     */
    void deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 根据 id 查询一条菜品。
     */
    Dish selectById(Long id);

    @AutoFill(Operation.UPDATE)
    void update(Dish dish);

    /**
     * 更新菜品售卖状态。
     */
    @AutoFill(Operation.UPDATE)
    void updateStatus(Dish dish);
}
