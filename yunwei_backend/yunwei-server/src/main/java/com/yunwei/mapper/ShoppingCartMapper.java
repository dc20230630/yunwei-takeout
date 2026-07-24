package com.yunwei.mapper;

import com.yunwei.pojo.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    ShoppingCart findOne(ShoppingCart condition);

    void updateNumberById(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    List<ShoppingCart> list(Long userId);

    ShoppingCart getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
