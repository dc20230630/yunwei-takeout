package com.yunwei.mapper;

import com.yunwei.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 根据微信用户唯一标识查询用户。
     */
    User getByOpenid(@Param("openid") String openid);

    /**
     * 首次登录时新增用户。
     */
    void insert(User user);
}