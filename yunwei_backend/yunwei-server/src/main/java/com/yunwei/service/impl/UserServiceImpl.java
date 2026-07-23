package com.yunwei.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwei.common.exception.BaseException;
import com.yunwei.mapper.UserMapper;
import com.yunwei.pojo.dto.UserLoginDTO;
import com.yunwei.pojo.entity.User;
import com.yunwei.properties.WechatProperties;
import com.yunwei.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.yunwei.service.UserService;

import lombok.extern.slf4j.Slf4j;

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
            //调用微信接口，用code换取当前微信用户的openId
            json = HttpClientUtil.doGet(WX_LOGIN_URL, params);
        } catch (IOException e) {
            log.error("调用微信登录接口失败", e);
            throw new BaseException("微信登录失败");
        }
        JsonNode jsonNode;

        try {
            // 微信返回的是 JSON，例如：{"openid":"xxx","session_key":"xxx"}
            jsonNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("微信登录接口返回的数据无法解析", e);
            throw new BaseException("微信登录失败");
        }

        // openid 是微信用户在当前小程序下的唯一标识
        JsonNode openidNode = jsonNode.get("openid");

        if (openidNode == null || openidNode.asText().isBlank()) {
            // 不记录完整响应，避免 session_key 等敏感信息写入日志
            log.warn("微信登录失败，微信接口未返回 openid");
            throw new BaseException("微信登录失败");
        }
        String openid = openidNode.asText();
        // 老用户：根据 openid 查询已存在的用户
        User user = userMapper.getByOpenid(openid);

        if (user == null) {
            // 新用户：首次登录时创建本地用户记录
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            // useGeneratedKeys 会将数据库生成的主键回填到 user.id
            userMapper.insert(user);
        }

        return user;
    }
}
