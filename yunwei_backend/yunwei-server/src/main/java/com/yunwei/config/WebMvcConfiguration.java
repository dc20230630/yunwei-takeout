package com.yunwei.config;

import com.yunwei.interceptor.JwtTokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类。
 * 当前主要负责注册 JWT 拦截器。
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * JWT 管理端拦截器。
     * 使用构造方法注入，Spring 会自动传入 JwtTokenInterceptor 对象。
     */
    private final JwtTokenInterceptor jwtTokenInterceptor;

    /**
     * 注册 JWT 拦截器。
     * /admin/**：
     * 表示所有管理端接口都需要校验 Token。
     * /admin/employee/login：
     * 登录接口不能校验 Token，因为用户登录前还没有 Token。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册 JWT 管理端拦截器");

        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }
}