package com.yunwei.utils;


import com.yunwei.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    // 自动注入 application.yaml 中的 JWT 配置
    private final JwtProperties jwtProperties;

    /**
     * 将 Base64 格式的密钥转换为 JWT 使用的签名密钥。
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取 JWT 的有效时间。
     *
     * 前端不能自己写死有效时间，应该使用后端配置的值。
     * 返回值单位是秒，方便前端保存本地过期时间。
     */
    public long getExpiresInSeconds() {
        return jwtProperties.getExpireHours() * 60 * 60;
    }

    /**
     * 根据员工信息生成 JWT。
     */
    public String createToken(Long employeeId, String username) {
        Date now = new Date();

        //配置文件中的小时数转换为毫秒
        long expireMilliseconds = jwtProperties.getExpireHours() * 60 * 60 * 1000;
        Date expireTime = new Date(now.getTime() + expireMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("employeeId", employeeId)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(getSigningKey())
                .compact();
    }


    /**
     * 解析并验证 JWT。
     *
     * Token 被篡改或过期时，这里会抛出异常。
     */
    public Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
