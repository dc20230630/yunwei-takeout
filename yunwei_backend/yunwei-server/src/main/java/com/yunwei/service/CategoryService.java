package com.yunwei.service;

import com.yunwei.common.result.PageResult;
import com.yunwei.pojo.dto.CategoryDTO;
import com.yunwei.pojo.dto.CategoryPageQueryDTO;
import com.yunwei.pojo.entity.Category;

import java.util.List;

/**
 * 分类相关的业务接口。
 */
public interface CategoryService {

    /**
     * 新增分类。
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询分类。
     */
    PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 删除分类。
     */
    void deleteById(Long id);

    /**
     * 修改分类名称、类型和排序。
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用或禁用分类。
     */
    void updateStatus(Long id, Integer status);

    /**
     * 查询已启用的分类。
     */
    List<Category> list(Integer type);
}
