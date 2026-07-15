package com.yunwei.context;

/**
 * 保存当前请求中的登录员工 ID。
 */

public class BaseContext {
    
    // 每个请求使用独立的线程变量，保存当前登录员工 ID
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 保存当前登录员工 ID。
     */
    public static void setCurrentId(Long employeeId){
        THREAD_LOCAL.set(employeeId);
    }

    /**
     * 获取当前登录员工 ID。
     */
    public static Long getCurrentId(){
        return THREAD_LOCAL.get();
    }
    
    /**
     * 清除当前请求的员工 ID。
     */
    public static void removeCurrentId(){
        THREAD_LOCAL.remove();
    }

    private BaseContext(){

    }
}