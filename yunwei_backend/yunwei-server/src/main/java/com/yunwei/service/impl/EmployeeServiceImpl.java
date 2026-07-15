package com.yunwei.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunwei.common.exception.AuthenticationException;
import com.yunwei.common.exception.BaseException;
import com.yunwei.common.result.PageResult;
import com.yunwei.constant.StatusConstant;
import com.yunwei.context.BaseContext;
import com.yunwei.mapper.EmployeeMapper;
import com.yunwei.pojo.dto.EmployeeAddDTo;
import com.yunwei.pojo.dto.EmployeeLoginDTO;
import com.yunwei.pojo.entity.Employee;
import com.yunwei.pojo.vo.EmployeeListVo;
import com.yunwei.pojo.vo.EmployeeLoginVO;
import com.yunwei.service.EmployeeService;
import com.yunwei.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    // 查询员工数据
    private final EmployeeMapper employeeMapper;

    // 校验 BCrypt 密码
    private final PasswordEncoder passwordEncoder;

    // 生成 JWT
    private final JwtUtil jwtUtil;

    /**
     * 校验账号密码，成功后返回员工信息和 Token。
     */

    @Override
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        Employee employee = employeeMapper.selectByUsername(employeeLoginDTO.getUsername());

        // 账号不存在时，不能明确提示账号不存在，避免泄露账号信息
        if (employee == null) {
            log.warn("员工登录失败，账号不存在：{}", employeeLoginDTO.getUsername());
            throw new BaseException("账号或密码错误");
        }

        // matches(明文密码, 数据库中的 BCrypt 哈希密码)
        if (!passwordEncoder.matches(employeeLoginDTO.getPassword(), employee.getPassword())) {
            log.warn("员工登录失败，密码错误：{}", employeeLoginDTO.getUsername());
            throw new BaseException("账号或密码错误");
        }

        // 状态为 0 表示员工账号已被禁用
        if (Integer.valueOf(0).equals(employee.getStatus())) {
            log.warn("员工登录失败，账号已被禁用：{}", employeeLoginDTO.getUsername());
            throw new BaseException("账号已被禁用");
        }

        String token = jwtUtil.createToken(employee.getId(), employee.getUsername());
        long expiresIn = jwtUtil.getExpiresInSeconds();

        // 登录成功只记录账号，不记录密码和 Token
        log.info("员工登录成功：{}", employee.getUsername());

        return new EmployeeLoginVO(
                employee.getId(),
                employee.getName(),
                employee.getUsername(),
                token,
                expiresIn);
    }

    /**
     * 新增员工。
     *
     * @param employeeAddDTo 前端提交的新增员工信息
     */
    @Override
    public void save(EmployeeAddDTo employeeAddDTo) {
        // 查询用户名是否已经存在
        Employee oldEmployee = employeeMapper.selectByUsername(employeeAddDTo.getUsername());
        if (oldEmployee != null) {
            throw new BaseException("用户名已存在");
        }

        // 创建员工实体
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeAddDTo, employee);

        // 设置初始密码，并使用 BCrypt 加密
        String defaultPassword = "123456";
        employee.setPassword(passwordEncoder.encode(defaultPassword));

        // 新员工默认启用
        employee.setStatus(StatusConstant.ENABLED);

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        employee.setCreateTime(now);
        employee.setUpdateTime(now);

        // 设置当前记录的创建人id和修改人id
        // 获取当前登录员工ID
        Long currentEmployeeId = BaseContext.getCurrentId();
        if (currentEmployeeId == null) {
            throw new AuthenticationException("请先登录");
        }
        employee.setCreateUser(currentEmployeeId);
        employee.setUpdateUser(currentEmployeeId);

        // 保存到数据库
        employeeMapper.insert(employee);

        log.info("新增员工成功，{}", employee);
    }

    /**
     * 查询员工列表。
     * @return 员工列表
     */
    @Override
    public PageResult<EmployeeListVo> list(Integer page, Integer pageSize) {
        //开启分页，必须紧挨着Mapper查询
        PageHelper.startPage(page,pageSize);

        //查询数据库中的员工数据
        List<Employee> employees = employeeMapper.selectAll();

        // PageHelper 会在这里提供总条数
        PageInfo<Employee> pageInfo = new PageInfo<>(employees);

        List<EmployeeListVo> records = employees.stream().map(employee -> {
            EmployeeListVo employeeListVo = new EmployeeListVo();
            BeanUtils.copyProperties(employee,employeeListVo);
            return employeeListVo;
        }).toList();

        return new PageResult<>(pageInfo.getTotal(), records);
    }

}
