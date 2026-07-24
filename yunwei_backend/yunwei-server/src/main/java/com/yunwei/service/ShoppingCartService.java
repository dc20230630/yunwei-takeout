package com.yunwei.service;

import com.yunwei.pojo.dto.ShoppingCartDTO;
import com.yunwei.pojo.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void sub(Long cartId);

    void clean();
}
