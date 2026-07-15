package com.yunwei.controller.admin;


import com.yunwei.common.result.PageResult;
import com.yunwei.pojo.dto.EmployeeAddDTo;
import com.yunwei.pojo.vo.EmployeeListVo;
import org.springframework.web.bind.annotation.*;

import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.EmployeeLoginDTO;
import com.yunwei.pojo.vo.EmployeeLoginVO;
import com.yunwei.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/employee")
@RequiredArgsConstructor
@Tag(name = "员工管理")
public class EmployeeController {
    /**
     * 员工业务对象。
     *
     * @RequiredArgsConstructor 会生成构造方法，
     * Spring 会自动把 EmployeeServiceImpl 注入进来。
     */
    private final EmployeeService employeeService;


    /**
     * 管理员登录接口。
     * 请求地址：
     * POST /admin/employee/login
     * 请求参数：
     * {
     *     "username": "admin",
     *     "password": "123456"
     * }
     */
    @Operation(summary = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody @Valid EmployeeLoginDTO employeeLoginDTO) {
        /*
         * @RequestBody：
         * 把前端发送的 JSON 转换成 EmployeeLoginDTO 对象。
         *
         * @Valid：
         * 触发 EmployeeLoginDTO 中的 @NotBlank 校验。
         */
        EmployeeLoginVO employeeLoginVO = employeeService.login(employeeLoginDTO);

        // 将登录结果统一包装后返回给前端
        return Result.success(employeeLoginVO);
    }

    /**
     * 新增员工接口。
     * 请求地址：
     * POST /admin/employee
     */
    @Operation(summary = "新增员工")
    @PostMapping("")
    public Result<Void> save(@RequestBody @Valid EmployeeAddDTo employeeAddDTo){
        log.info("新增员工：{}", employeeAddDTo);
        employeeService.save(employeeAddDTo);
        return Result.success();
    }


    /**
     * 查询员工列表。
     * 请求地址：
     * GET /admin/employee
     */
    @GetMapping
    @Operation(summary = "查询员工列表")
    public Result<PageResult<EmployeeListVo>> list(@RequestParam Integer page,@RequestParam Integer pageSize) {

        PageResult<EmployeeListVo> pageResult = employeeService.list(page,pageSize);
        return Result.success(pageResult);
    }

}
