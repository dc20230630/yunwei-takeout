package com.yunwei.service;


import com.yunwei.pojo.dto.UserLoginDTO;
import com.yunwei.pojo.entity.User;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);
}
