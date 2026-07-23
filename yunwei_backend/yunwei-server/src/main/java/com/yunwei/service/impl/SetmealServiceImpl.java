package com.yunwei.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunwei.common.exception.BaseException;
import com.yunwei.common.result.PageResult;
import com.yunwei.constant.StatusConstant;
import com.yunwei.mapper.SetmealDishMapper;
import com.yunwei.mapper.SetmealMapper;
import com.yunwei.pojo.dto.SetmealDTO;
import com.yunwei.pojo.dto.SetmealPageQueryDTO;
import com.yunwei.pojo.entity.Setmeal;
import com.yunwei.pojo.entity.SetmealDish;
import com.yunwei.pojo.vo.DishItemVo;
import com.yunwei.pojo.vo.SetmealVo;
import com.yunwei.service.SetmealService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现。
 */
@Service
@RequiredArgsConstructor
public class SetmealServiceImpl implements SetmealService {

    private final SetmealMapper setmealMapper;

    private final SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult<SetmealVo> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<SetmealVo> setmeals = setmealMapper.pageQuery(setmealPageQueryDTO);
        PageInfo<SetmealVo> pageInfo = new PageInfo<>(setmeals);
        return new PageResult<>(pageInfo.getTotal(), setmeals);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (setmeal == null) {
                throw new BaseException("套餐不存在");
            }
            if (StatusConstant.ENABLED.equals(setmeal.getStatus())) {
                throw new BaseException("起售中的套餐不能删除");
            }
        }

        setmealDishMapper.deleteBySetmealIds(ids);
        setmealMapper.deleteByIds(ids);
    }

    @Override
    public SetmealVo getByIdWithDish(Long id) {
        SetmealVo setmealVo = setmealMapper.getByIdWithDish(id);
        if (setmealVo == null) {
            throw new BaseException("套餐不存在");
        }
        return setmealVo;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {
        if (setmealMapper.selectById(setmealDTO.getId()) == null) {
            throw new BaseException("套餐不存在");
        }

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishMapper.deleteBySetmealIds(List.of(setmeal.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (setmealMapper.selectById(id) == null) {
            throw new BaseException("套餐不存在");
        }
        if (StatusConstant.ENABLED.equals(status)
                && setmealDishMapper.countDisabledDishBySetmealId(id, StatusConstant.DISABLED) > 0) {
            throw new BaseException("套餐内包含停售菜品，无法起售");
        }

        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVo> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
