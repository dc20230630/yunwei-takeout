package com.yunwei.interceptor;

import com.yunwei.common.exception.AuthenticationException;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {
    /**
     * JWT 工具类。
     * <p>
     * 通过它解析 Token，同时验证 Token 的签名和过期时间。
     */
    private final JwtUtil jwtUtil;

    /**
     * Controller 执行之前调用。
     * <p>
     * 返回 true：允许继续访问 Controller。
     * 抛出异常：阻止访问 Controller。
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        // 获取请求头中的Authorization内容
        String authorization = request.getHeader("Authorization");

        // 请求头不存在，说明没有携带token
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AuthenticationException("请先登录");
        }

        // 去掉前面的"Bear ",只保留真正的JWT字符串
        String token = authorization.substring(7);

        try {
            /*
             * 解析 Token。
             *
             * 这里会自动检查：
             * 1. Token 格式是否正确
             * 2. Token 是否被修改
             * 3. Token 是否过期
             * 4. Token 签名是否正确
             */
            Claims claims = jwtUtil.parseToken(token);

            // 从JWT中获取当前登录员工ID
            Long employeeId = claims.get("employeeId", Long.class);

            if (employeeId == null) {
                throw new AuthenticationException("Token中缺少员工信息");
            }

            // 保存当前请求的员工Id
            BaseContext.setCurrentId(employeeId);

            // Token校验成功，允许请求继续执行
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 只记录请求路径，不记录 Token 内容，避免敏感信息进入日志
            log.warn("JWT校验失败，请求路径:{}", request.getRequestURI());

            // 把底层JWT异常转换成项目自己的认证异常
            throw new AuthenticationException("登录已过去，请重新登录");
        }
    }   

    /**
     * 请求完成后清理当前线程中的员工 ID。
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) {
        BaseContext.removeCurrentId();
    }
}
