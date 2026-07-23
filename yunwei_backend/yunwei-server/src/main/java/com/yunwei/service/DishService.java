package com.yunwei.service;

import com.yunwei.common.result.PageResult;
import com.yunwei.pojo.dto.DishDTO;
import com.yunwei.pojo.dto.DishPageQueryDTO;
import com.yunwei.pojo.dto.DishUpdateDTO;
import com.yunwei.pojo.entity.Dish;
import com.yunwei.pojo.vo.DishVo;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 菜品业务接口。
 */
public interface DishService {
    /**
     * 新增菜品和它的口味。
     */
    void saveWithFlavor(@Valid DishDTO dishDTO);

    /**
     * 分页查询菜品。
     */
    PageResult<DishVo> pageQuery(@Valid DishPageQueryDTO dishPageQueryDTO);

    /**
     * 起售或停售指定菜品。
     */
    void updateStatus(Long id, Integer status);


    /**
     * 删除一个或多个菜品，以及这些菜品的口味。
     *
     * @param ids 要删除的菜品 id 集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 查询菜品详情和口味，用于编辑回显。
     */
    DishVo getByIdWithFlavor(Long id);

    /**
     * 修改菜品及其口味。
     */
    void updateWithFlavor(DishUpdateDTO dishUpdateDTO);

    /**
     * 按条件查询菜品及其口味，供用户端浏览菜品。
     */
    List<DishVo> listWithFlavor(Dish dish);
}
