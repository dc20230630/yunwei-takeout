package com.yunwei.common.exception;

/**
 * 用户未登录、Token 无效或 Token 过期时使用的异常。
 */
public class AuthenticationException extends BaseException {

    public AuthenticationException(String message) {
        super(message);
    }
}