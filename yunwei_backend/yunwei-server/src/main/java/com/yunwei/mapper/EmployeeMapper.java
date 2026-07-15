package com.yunwei.mapper;


import com.yunwei.pojo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工表的数据访问接口。
 *
 * @Mapper 表示这是 MyBatis 的 Mapper 接口，
 * Spring Boot 启动时会自动创建它的实现对象。
 */

@Mapper
public interface EmployeeMapper {
    /**
     * 根据登录账号查询员工。
     *
     * @Param("username") 指定 XML 中使用的参数名称。
     */
    Employee selectByUsername(@Param("username") String username);

    /**
     * 保存员工信息。
     *
     * @param employee 员工信息
     */
    int insert(Employee employee);

    /**
     * 查询所有员工。
     *
     * @return 员工实体列表
     */
    List<Employee> selectAll();
}
