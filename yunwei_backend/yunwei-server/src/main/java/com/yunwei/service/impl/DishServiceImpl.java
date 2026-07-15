package com.yunwei.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunwei.common.exception.BaseException;
import com.yunwei.common.result.PageResult;
import com.yunwei.constant.StatusConstant;
import com.yunwei.mapper.DishFlavorMapper;
import com.yunwei.mapper.DishMapper;
import com.yunwei.mapper.SetmealDishMapper;
import com.yunwei.pojo.dto.DishDTO;
import com.yunwei.pojo.dto.DishPageQueryDTO;
import com.yunwei.pojo.dto.DishUpdateDTO;
import com.yunwei.pojo.entity.Dish;
import com.yunwei.pojo.entity.DishFlavor;
import com.yunwei.pojo.vo.DishVo;
import com.yunwei.service.DishService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;

    private final DishFlavorMapper dishFlavorMapper;

    private final SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品和口味。
     *
     * 菜品与口味必须同时成功；
     * 任意一步失败，事务会回滚。
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish oldDish = dishMapper.selectByName(dishDTO.getName());
        if(oldDish != null){
            throw new BaseException("菜品已存在");
        }

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 新增菜品默认起售
        dish.setStatus(StatusConstant.ENABLED);

        // @AutoFill 会自动填充时间和操作人
        dishMapper.insert(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();

        // 菜品可以没有口味；有口味时才批量保存
        if(flavors != null && !flavors.isEmpty()){
            for(DishFlavor flavor : flavors){
                // dishMapper.insert 后，dish.id 已经有数据库生成的主键
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }


    @Override
    public PageResult<DishVo> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        List<DishVo> dishes = dishMapper.pageQuery(dishPageQueryDTO);

        PageInfo<DishVo> pageInfo = new PageInfo<>(dishes);

        return new PageResult<>(pageInfo.getTotal(),dishes);
    }

    /**
     * 只更新菜品状态，其余菜品信息保持不变。
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);

        dishMapper.updateStatus(dish);
    }


    /**
     * 删除菜品前先校验业务规则。
     * 所有校验通过后，再删除口味和菜品。
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //起售中的菜品不允许删除
        Integer sellingDishCount   = dishMapper.countByIdsAndStatus(ids, StatusConstant.ENABLED);
        
        if(sellingDishCount > 0){
            throw new BaseException("起售中的菜品不能删除");
        }
        
        //被套餐关联的菜品不允许删除
        Integer setmealDishCount  = setmealDishMapper.countByDishIds(ids);
        if(setmealDishCount > 0){
            throw new BaseException("菜品已被套餐关联，不能删除");
        }

        //先删从表口味，再删主表菜品
        dishFlavorMapper.deleteByDishIds(ids);
        dishMapper.deleteByIds(ids);
    }

    @Override
    public DishVo getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        if(dish == null){
            throw new BaseException("菜品不存在");
        }
        DishVo dishVo = new DishVo();
        BeanUtils.copyProperties(dish,dishVo);

        // 口味在独立表中，需要单独查询
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        dishVo.setFlavors(flavors);
        return dishVo;
    }

    /**
     * 修改菜品和口味。
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishUpdateDTO dishUpdateDTO) {
        //先确认菜品在不在
        Dish currentDish  = dishMapper.selectById(dishUpdateDTO.getId());
        if(currentDish  == null){
            throw new BaseException("菜品不存在");
        }

        // 同名但不是当前菜品时，才属于重名
        Dish sameNameDish  = dishMapper.selectByName(dishUpdateDTO.getName());

        if(sameNameDish != null && !sameNameDish.getId().equals(dishUpdateDTO.getId())){
            throw new BaseException("菜品已存在");
        }

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishUpdateDTO, dish);

        // 更新菜品基本信息，@AutoFill 自动填写修改时间和修改人
        dishMapper.update(dish);

        // 旧口味全部失效，先删除
        dishFlavorMapper.deleteByDishId(dish.getId());

        List<DishFlavor> flavors = dishUpdateDTO.getFlavors();

        // 菜品允许没有口味；有口味才重新保存
        if(flavors != null && !flavors.isEmpty()){
            for (DishFlavor flavor : flavors) {
                // 新口味必须重新关联当前菜品
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(flavors);

        }

    }
}
