package com.yunwei.utils;


import com.yunwei.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

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
     * <p>
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
     * <p>
     * Token 被篡改或过期时，这里会抛出异常。
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 根据用户端原始文本密钥生成签名密钥。
     * <p>
     * 用户端密钥不再按 Base64 解码，因此配置值必须至少有 32 个 ASCII 字符。
     *
     * @param secretKey 用户端 JWT 原始文本密钥
     * @return JWT 签名密钥
     */
    private static SecretKey getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 创建用户端 JWT。
     *
     * @param secretKey 用户端原始文本密钥
     * @param ttl       Token 有效期，单位：毫秒
     * @param claims    要写入 Token 的数据，例如用户 ID
     * @return 生成后的 Token
     */
    public static String createJWT(
            String secretKey,
            long ttl,
            Map<String, Object> claims
    ) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + ttl);

        return Jwts.builder()
                // 将 userId 等业务数据写入 JWT
                .claims(claims)
                // 记录签发和过期时间
                .issuedAt(now)
                .expiration(expireTime)
                // 使用用户端密钥进行签名
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    /**
     * 解析并校验用户端 JWT。
     *
     * @param secretKey 用户端原始文本密钥
     * @param token     小程序请求头携带的 Token
     * @return JWT 中保存的载荷数据
     */
    public static Claims parseJWT(String secretKey, String token) {
        return Jwts.parser()
                //用户端Token使用user-secret-key校验
                .verifyWith(getSigningKey(secretKey))
                .build()
                // 签名错误、Token 过期或格式错误时会抛出 JwtException
                .parseSignedClaims(token)
                .getPayload();
    }
}
