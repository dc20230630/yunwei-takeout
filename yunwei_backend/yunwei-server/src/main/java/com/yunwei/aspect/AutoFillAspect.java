package com.yunwei.aspect;

import com.yunwei.annotation.AutoFill;
import com.yunwei.common.enumeration.Operation;
import com.yunwei.common.exception.AuthenticationException;
import com.yunwei.context.BaseContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面。
 * <p>
 * 在 Mapper 执行新增或修改 SQL 前，
 * 自动补齐时间和当前操作人。
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.yunwei.mapper.*.*(..)) && @annotation(com.yunwei.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始进行公共字段自动填充...");

        // 获取当前被拦截的方法上的数据库操作类型
        // 获取当前实际执行的 Mapper 方法，例如 CategoryMapper.insert()
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();// 方法签名对象
        // 获取 Mapper 方法上的 @AutoFill 注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);// 获得方法上的注解对象
        // 读取注解中指定的操作类型：INSERT 或 UPDATE
        Operation operationType = autoFill.value();//获得数据库的操作类型


        //获取到当前拦截的方法参数--实体对象
        Object[] args = joinPoint.getArgs();

        // 当前项目的 insert/update 方法第一个参数都是实体对象
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        //准备赋值
        //当前时间
        LocalDateTime now = LocalDateTime.now();

        //获取当前登录员工ID
        // 从 JWT 拦截器写入的 ThreadLocal 中获取当前登录员工 ID
        Long currentEmployeeId = BaseContext.getCurrentId();

        // 管理端写操作必须能拿到当前操作人
        if (currentEmployeeId == null) {
            throw new AuthenticationException("请先登录");
        }

        //通过反射获取实体中的Setter方法
        Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
        Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

        //新增和修改都要更新修改时间、修改人
        setUpdateTime.invoke(entity, now);
        setUpdateUser.invoke(entity, currentEmployeeId);

        // 只有新增时才填创建时间、创建人
        if(operationType == Operation.INSERT){
            Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
            setCreateTime.invoke(entity, now);
            setCreateUser.invoke(entity, currentEmployeeId);
        }
        
    }

}
