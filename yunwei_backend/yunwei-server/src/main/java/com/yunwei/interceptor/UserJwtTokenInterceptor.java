package com.yunwei.interceptor;

import com.yunwei.common.exception.AuthenticationException;
import com.yunwei.config.JwtProperties;
import com.yunwei.context.BaseContext;
import com.yunwei.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户端 JWT 拦截器。
 * <p>
 * 校验小程序请求头中的 Token，并将当前用户 ID 保存到 BaseContext。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserJwtTokenInterceptor implements HandlerInterceptor {
    /**
     * 读取用户端 JWT 密钥和请求头名称。
     */
    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 小程序请求工具中发送的请求头：authentication: Token
        String token = request.getHeader(jwtProperties.getUserTokenName());
        if (token == null || token.isBlank()) {
            throw new AuthenticationException("请先登录");
        }
        try {
            //校验用户端Token的签名和过期时间
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);

            //字段名必须和UserController生成Token保持一致
            Long userId = claims.get("user_id", Long.class);
            if (userId == null) {
                throw new AuthenticationException("Token 中缺少用户信息");
            }
            // 后续购物车、地址、订单接口通过 BaseContext 获取当前用户 ID
            BaseContext.setCurrentId(userId);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("用户端 Token 校验失败，请求路径：{}", request.getRequestURI());
            throw new AuthenticationException("登录已过期，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后必须清理 ThreadLocal，避免线程复用时串用户
        BaseContext.removeCurrentId();
    }
}
