package com.yunwei.service;

import com.yunwei.common.result.PageResult;
import com.yunwei.pojo.dto.EmployeeAddDTo;
import com.yunwei.pojo.dto.EmployeeLoginDTO;
import com.yunwei.pojo.dto.EmployeeUpdateDTO;
import com.yunwei.pojo.vo.EmployeeDetailVo;
import com.yunwei.pojo.vo.EmployeeListVo;
import com.yunwei.pojo.vo.EmployeeLoginVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 员工相关的业务接口。
 *
 * 接口只负责声明业务方法，不负责编写具体实现。
 * 具体代码后面放在 EmployeeServiceImpl 中。
 */
public interface EmployeeService {

    /**
     * 管理员登录。
     *
     * @param employeeLoginDTO 前端提交的用户名和密码
     * @return 登录成功后的员工信息和 JWT
     */
    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工。
     *
     * @param employeeAddDTo 前端提交的新增员工信息
     */
    void save(EmployeeAddDTo employeeAddDTo);

    /**
     * 查询员工列表。
     *
     * @return 员工列表
     */
    PageResult<EmployeeListVo> list(Integer page, Integer pageSize);

    /**
     * 修改员工账号状态。
     *
     * @param id 员工 ID
     * @param status 账号状态：1 启用，0 禁用
     */
    void updateStatus(Long id, Integer status);

    /**
     * 查询员工详情。
     *
     * @param id 员工 ID
     * @return 员工可编辑的信息
     */
    EmployeeDetailVo getById(Long id);


    /**
     * 修改员工信息。
     *
     * @param employeeUpdateDTO 前端提交的员工信息
     */
    void update(@Valid EmployeeUpdateDTO employeeUpdateDTO);

    /**
     * 根据员工 ID 删除员工。
     *
     * @param id 员工 ID
     */
    void deleteById(Long id);
}
