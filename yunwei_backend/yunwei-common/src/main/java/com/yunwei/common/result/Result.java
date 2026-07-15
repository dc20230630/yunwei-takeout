package com.yunwei.common.result;

public record Result<T>(Integer code, String message, T data) {

    //当成功时，返回结果  一般用于获取数据
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    //当成功时，返回结果，没有数据 一般用于删除、修改
    public static Result<Void> success() {
        return new Result<>(200, "success", null);
    }

    //当失败时，返回结果
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
