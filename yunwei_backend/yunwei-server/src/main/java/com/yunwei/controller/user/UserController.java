package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.config.JwtProperties;
import com.yunwei.pojo.dto.UserLoginDTO;
import com.yunwei.pojo.entity.User;
import com.yunwei.pojo.vo.UserLoginVO;
import com.yunwei.service.UserService;
import com.yunwei.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
@Tag(name = "用户端用户")
public class UserController {

    private final UserService userService;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "微信登录")
    public Result<UserLoginVO> login(@RequestBody @Valid UserLoginDTO userLoginDTO){
        log.info("用户登录：{}", userLoginDTO.getCode());

        //调用业务层
        User user = userService.wxLogin(userLoginDTO);

        //从JWT中只保存当前登录用户的ID
        Map<String,Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());

        // 根据用户端密钥和有效期生成 Token
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        UserLoginVO userLoginVO  = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }
}
