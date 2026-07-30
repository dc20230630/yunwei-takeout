package com.yunwei.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwei.common.exception.BaseException;
import com.yunwei.context.BaseContext;
import com.yunwei.mapper.UserMapper;
import com.yunwei.pojo.dto.UserLoginDTO;
import com.yunwei.pojo.dto.UserProfileDTO;
import com.yunwei.pojo.entity.User;
import com.yunwei.properties.WechatProperties;
import com.yunwei.service.UserService;
import com.yunwei.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final WechatProperties wechatProperties;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", wechatProperties.getAppId());
        params.put("secret", wechatProperties.getAppSecret());
        params.put("js_code", userLoginDTO.getCode());
        params.put("grant_type", "authorization_code");

        String json;
        try {
            json = HttpClientUtil.doGet(WX_LOGIN_URL, params);
        } catch (IOException e) {
            log.error("调用微信登录接口失败", e);
            throw new BaseException("微信登录失败");
        }

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("微信登录接口返回的数据无法解析", e);
            throw new BaseException("微信登录失败");
        }

        JsonNode openidNode = jsonNode.get("openid");
        if (openidNode == null || openidNode.asText().isBlank()) {
            log.warn("微信登录失败，微信接口未返回 openid");
            throw new BaseException("微信登录失败");
        }

        String openid = openidNode.asText();
        User user = userMapper.getByOpenid(openid);
        if (user != null) {
            return user;
        }

        user = User.builder()
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateProfile(UserProfileDTO userProfileDTO) {
        // 当前用户编号只从 JWT 中读取，不能由前端指定
        User user = User.builder()
                .id(BaseContext.getCurrentId())
                .name(userProfileDTO.getName())
                .avatar(userProfileDTO.getAvatar())
                .build();
        userMapper.updateProfile(user);
        return user;
    }
}
