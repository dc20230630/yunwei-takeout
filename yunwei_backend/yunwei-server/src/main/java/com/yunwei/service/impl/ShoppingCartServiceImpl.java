package com.yunwei.service.impl;

import com.yunwei.common.exception.BaseException;
import com.yunwei.constant.StatusConstant;
import com.yunwei.context.BaseContext;
import com.yunwei.mapper.DishMapper;
import com.yunwei.mapper.ShoppingCartMapper;
import com.yunwei.mapper.SetmealMapper;
import com.yunwei.pojo.dto.ShoppingCartDTO;
import com.yunwei.pojo.entity.Dish;
import com.yunwei.pojo.entity.ShoppingCart;
import com.yunwei.pojo.entity.Setmeal;
import com.yunwei.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车业务：负责商品合并、数量变化和商品信息快照。
 */
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 请求中只能选择菜品或套餐中的一种
        checkProduct(shoppingCartDTO);

        // DTO 只带商品标识和口味，复制后补上当前登录用户作为查询条件
        ShoppingCart condition = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, condition);
        condition.setUserId(BaseContext.getCurrentId());

        // 同一用户、同一商品、同一口味视为同一条购物车记录
        ShoppingCart existedCart = shoppingCartMapper.findOne(condition);
        if (existedCart != null) {
            // 商品已在购物车中，只增加数量，不重复插入
            existedCart.setNumber(existedCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(existedCart);
            return;
        }

        // 新记录不能相信前端传来的名称、图片和价格，要从数据库查询商品快照
        if (shoppingCartDTO.getDishId() != null) {
            Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
            if (dish == null || !StatusConstant.ENABLED.equals(dish.getStatus())) {
                throw new BaseException("菜品不存在或已停售");
            }

            // 保存加入购物车时的名称、图片和单价，避免菜品后续修改影响当前记录
            condition.setName(dish.getName());
            condition.setImage(dish.getImage());
            condition.setAmount(dish.getPrice());
        } else {
            Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
            if (setmeal == null || !StatusConstant.ENABLED.equals(setmeal.getStatus())) {
                throw new BaseException("套餐不存在或已停售");
            }

            // 套餐同样保存加入购物车时的名称、图片和单价，amount 不能为空
            condition.setName(setmeal.getName());
            condition.setImage(setmeal.getImage());
            condition.setAmount(setmeal.getPrice());
        }

        // 首次加入默认一份，并记录加入时间
        condition.setNumber(1);
        condition.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(condition);
    }

    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.list(userId);
    }

    @Override
    public void sub(Long cartId) {
        Long userId = BaseContext.getCurrentId();
        // 查询时同时限定用户，不能仅凭购物车主键修改其他用户的数据
        ShoppingCart shoppingCart = shoppingCartMapper.getByIdAndUserId(cartId, userId);
        if (shoppingCart == null) {
            throw new BaseException("购物车商品不存在");
        }

        if (shoppingCart.getNumber() > 1) {
            // 剩余数量大于一时只减一，保留原来的商品和口味记录
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
            return;
        }

        // 数量减到零后删除整条记录，列表中不再保留数量为零的商品
        shoppingCartMapper.deleteByIdAndUserId(cartId, userId);
    }

    @Override
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }


    /**
     * 校验商品类型，避免一条购物车记录同时关联菜品和套餐。
     */
    private void checkProduct(ShoppingCartDTO shoppingCartDTO) {
        boolean hasDish = shoppingCartDTO.getDishId() != null;
        boolean hasSetmeal = shoppingCartDTO.getSetmealId() != null;
        if (hasDish == hasSetmeal) {
            throw new BaseException("菜品和套餐必须且只能选择一个");
        }

        if (hasSetmeal && shoppingCartDTO.getDishFlavor() != null) {
            throw new BaseException("套餐不能选择菜品口味");
        }
    }
}
