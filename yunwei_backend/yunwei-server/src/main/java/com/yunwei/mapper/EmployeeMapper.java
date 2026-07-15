package com.yunwei.mapper;


import com.yunwei.annotation.AutoFill;
import com.yunwei.common.enumeration.Operation;
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
    @AutoFill(value = Operation.INSERT)
    int insert(Employee employee);

    /**
     * 查询所有员工。
     *
     * @return 员工实体列表
     */
    List<Employee> selectAll();

    /**
     * 更新员工账号状态。
     *
     * @param employee 包含员工 ID、状态、修改时间和修改人的对象
     * @return 受影响的数据库记录数
     */
    @AutoFill(value = Operation.UPDATE)
    int updateStatus(Employee employee);

    /**
     * 根据员工 ID 查询员工。
     *
     * @param id 员工 ID
     * @return 员工实体
     */
    Employee selectById(@Param("id") Long id);

    /**
     * 修改员工基本信息。
     *
     * @param employee 要修改的员工信息
     * @return 受影响的记录数
     */
    @AutoFill(value = Operation.UPDATE)
    int update(Employee employee);

    /**
     * 根据员工 ID 删除员工记录。
     *
     * @param id 员工 ID
     * @return 受影响的记录数
     */
    int deleteById(@Param("id") Long id);
}
