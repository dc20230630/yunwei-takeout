package com.yunwei.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunwei.common.exception.AuthenticationException;
import com.yunwei.common.exception.BaseException;
import com.yunwei.common.result.PageResult;
import com.yunwei.constant.StatusConstant;
import com.yunwei.context.BaseContext;
import com.yunwei.mapper.CategoryMapper;
import com.yunwei.mapper.DishMapper;
import com.yunwei.mapper.SetmealMapper;
import com.yunwei.pojo.dto.CategoryDTO;
import com.yunwei.pojo.dto.CategoryPageQueryDTO;
import com.yunwei.pojo.entity.Category;
import com.yunwei.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类相关的业务实现。
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    // 删除分类前，用于检查是否关联菜品
    private final DishMapper dishMapper;

    // 删除分类前，用于检查是否关联套餐
    private final SetmealMapper setmealMapper;

    /**
     * 新增分类，默认状态为禁用。
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 新建分类需要手动启用后，才能被菜品或套餐选择
        category.setStatus(StatusConstant.DISABLED);
        categoryMapper.insert(category);
    }

    /**
     * 按名称和类型分页查询分类。
     */
    @Override
    public PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 必须紧挨 Mapper 查询，PageHelper 才会拦截下一条 SQL
        PageHelper.startPage(
                categoryPageQueryDTO.getPage(),
                categoryPageQueryDTO.getPageSize()
        );

        List<Category> categories = categoryMapper.pageQuery(categoryPageQueryDTO);
        PageInfo<Category> pageInfo = new PageInfo<>(categories);

        return new PageResult<>(pageInfo.getTotal(), categories);
    }

    /**
     * 删除没有关联菜品和套餐的分类。
     */
    @Override
    public void deleteById(Long id) {
        // 分类仍关联菜品时，删除会造成菜品没有所属分类
        Integer dishCount = dishMapper.countByCategoryId(id);
        if (dishCount > 0) {
            throw new BaseException("当前分类关联了菜品，不能删除");
        }

        // 分类仍关联套餐时，删除会造成套餐没有所属分类
        Integer setmealCount = setmealMapper.countByCategoryId(id);
        if (setmealCount > 0) {
            throw new BaseException("当前分类关联了套餐，不能删除");
        }

        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类名称、类型和排序，并记录本次修改信息。
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    /**
     * 启用或禁用分类。
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);

        categoryMapper.update(category);
    }

    /**
     * 查询已启用的分类。
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    /**
     * 读取 JWT 拦截器存入当前线程的操作人 ID。
     */
    private Long getCurrentEmployeeId() {
        Long currentEmployeeId = BaseContext.getCurrentId();
        if (currentEmployeeId == null) {
            throw new AuthenticationException("请先登录");
        }
        return currentEmployeeId;
    }
}
