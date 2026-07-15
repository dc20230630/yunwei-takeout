package com.yunwei.controller.admin;


import com.yunwei.common.result.PageResult;
import com.yunwei.pojo.dto.EmployeeAddDTo;
import com.yunwei.pojo.dto.EmployeeUpdateDTO;
import com.yunwei.pojo.vo.EmployeeDetailVo;
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




    /**
     * 启用或禁用员工账号。
     *
     * @param id 员工 ID
     * @param status 账号状态：1 启用，0 禁用
     */
    @PutMapping("/status")
    @Operation(summary = "启用或禁用员工账号")
    public Result updateStatus(@RequestParam Long id,@RequestParam Integer status) {
        employeeService.updateStatus(id,status);
        return Result.success();
    }


    /**
     * 根据员工 ID 查询详情，用于编辑弹窗回显。
     *
     * @param id 员工 ID
     * @return 员工可编辑的字段
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询员工详情")
    public Result<EmployeeDetailVo> getById(@PathVariable Long id){
        EmployeeDetailVo employeeDetailVo = employeeService.getById(id);
        return Result.success(employeeDetailVo);
    }


    /**
     * 修改员工基本信息。
     */
    @PutMapping
    @Operation(summary = "修改员工")
    public Result update(@RequestBody @Valid EmployeeUpdateDTO employeeUpdateDTO){
        employeeService.update(employeeUpdateDTO);
        return Result.success();
    }

    /**
     * 根据员工 ID 删除员工账号。
     * @param id 要删除的员工 ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工")
    public Result deleteById(@PathVariable Long id){
        employeeService.deleteById(id);
        return Result.success();
    }
}
