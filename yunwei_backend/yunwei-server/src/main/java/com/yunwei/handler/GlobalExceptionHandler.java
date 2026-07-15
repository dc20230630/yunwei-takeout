package com.yunwei.handler;


import com.yunwei.common.exception.AuthenticationException;
import com.yunwei.common.exception.BaseException;
import com.yunwei.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 * <p>
 * Controller 或 Service 中出现异常时，
 * Spring 会优先到这里查找对应的处理方法。
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理自己定义的业务异常。
     * 例如：
     * - 账号或密码错误
     * - 账号已被禁用
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBaseException(BaseException baseException){
        // 打印业务异常的完整堆栈，便于在控制台定位异常来源
        log.error("业务异常", baseException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, baseException.getMessage()));
    }
    

     /**
     * 处理 @Valid 校验失败的异常
     * 例如用户名或密码为空时，
     * EmployeeLoginDTO 中的 @NotBlank 会触发这个异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException exception){

        // 打印参数校验异常的完整堆栈
        log.error("参数校验异常", exception);

        //获取第一个未通过校验的字段提示信息
        String message = exception.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
                
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, message));
    }

    /**
     * 处理没有被前面方法处理到的系统异常。
     * 例如数据库连接失败、代码空指针等。
     * 日志记录完整异常，但不把技术细节直接返回给前端。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception exception){
        log.error("系统异常", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误"));
    }

    /**
     * 处理登录认证失败异常。
     * 例如没有 Token、Token 过期、Token 被修改等情况。
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException exception){
        // 打印认证异常的完整堆栈
        log.error("认证失败", exception);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(401, exception.getMessage()));
    }

}
