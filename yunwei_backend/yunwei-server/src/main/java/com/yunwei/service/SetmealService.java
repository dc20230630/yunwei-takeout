package com.yunwei.service;

import com.yunwei.common.result.PageResult;
import com.yunwei.pojo.dto.SetmealDTO;
import com.yunwei.pojo.dto.SetmealPageQueryDTO;
import com.yunwei.pojo.entity.Setmeal;
import com.yunwei.pojo.vo.DishItemVo;
import com.yunwei.pojo.vo.SetmealVo;

import java.util.List;

/**
 * 套餐业务接口。
 */
public interface SetmealService {

    void saveWithDish(SetmealDTO setmealDTO);

    PageResult<SetmealVo> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatch(List<Long> ids);

    SetmealVo getByIdWithDish(Long id);

    void updateWithDish(SetmealDTO setmealDTO);

    void updateStatus(Long id, Integer status);

    /**
     * 按条件查询套餐。
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 查询套餐包含的菜品项。
     */
    List<DishItemVo> getDishItemById(Long id);
}
