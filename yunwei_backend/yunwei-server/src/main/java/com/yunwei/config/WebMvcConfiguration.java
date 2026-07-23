package com.yunwei.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yunwei.interceptor.JwtTokenInterceptor;
import com.yunwei.interceptor.UserJwtTokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
     * JWT 用户端拦截器。
     */
    private final UserJwtTokenInterceptor userJwtTokenInterceptor;

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

        registry.addInterceptor(userJwtTokenInterceptor)
                // 用户端接口默认需要登录
                .addPathPatterns("/user/**")
                // 登录前没有 Token；查询营业状态也应允许未登录用户访问
                .excludePathPatterns("/user/user/login", "/user/shop/status");
    }

    /**
     * 扩展Spring MVC框架的消息转化器
     *
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter =
                        (MappingJackson2HttpMessageConverter) converter;

                ObjectMapper objectMapper = jsonConverter.getObjectMapper();

                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(
                        LocalDateTime.class,
                        new LocalDateTimeSerializer(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
                );

                objectMapper.registerModule(simpleModule);
                break;
            }
        }
    }
}
