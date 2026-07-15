package com.yunwei.mapper;

import com.yunwei.annotation.AutoFill;
import com.yunwei.common.enumeration.Operation;
import com.yunwei.pojo.dto.CategoryPageQueryDTO;
import com.yunwei.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类表的数据访问接口。
 */
@Mapper
public interface CategoryMapper {

    /**
     * 新增分类。
     */
    @AutoFill(value = Operation.INSERT)
    int insert(Category category);

    /**
     * 根据条件查询分类，分页由 PageHelper 自动处理。
     */
    List<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据分类 ID 删除分类。
     */
    int deleteById(@Param("id") Long id);

    /**
     * 修改分类信息或分类状态。
     */
    @AutoFill(value = Operation.UPDATE)
    int update(Category category);

    /**
     * 查询启用的分类，可按类型筛选。
     */
    List<Category> list(@Param("type") Integer type);
}
