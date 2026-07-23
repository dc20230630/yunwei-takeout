package com.yunwei.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取 application.yaml 中 yunwei.jwt 开头的配置。
 */


@Data
@Component
@ConfigurationProperties(prefix = "yunwei.jwt")
public class JwtProperties {

    //JWT签名密钥
    private String secret;

    // JWT 过期时间，单位是小时
    private long expireHours;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;
}
