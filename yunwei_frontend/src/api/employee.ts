import request from '@/utils/request';


/**
 * 登录接口请求参数。
 */
export interface EmployeeLoginRequest {
    username: string; // 管理员登录账号
    password: string; // 管理员登录密码
}

/**
 * 登录接口响应数据。
 */

export interface EmployeeLoginResponse {
    id: number; // 员工 ID
    name: string; // 员工姓名
    username: string; // 登录账号
    token: string; // 后端生成的 JWT
    expiresIn: number; // Token 有效时间，单位：秒
}


/**
 * 调用管理端员工登录接口。
 */

export function employeeLogin(data: EmployeeLoginRequest) {
    return request.post<EmployeeLoginResponse, EmployeeLoginResponse>(
        '/admin/employee/login',
        data
    );
}


/**
 * 新增员工请求参数
 */

export interface EmployeeAddRequest {
    username: string;
    name: string;
    phone: string;
    sex: string;
    idNumber: string
}

/**
 * 新增员工接口
 */
export function addEmployee(data: EmployeeAddRequest) {
    return request.post('/admin/employee', data);
}


/**
 * 员工列表数据。
 */
export interface EmployeeListItem {
    id: number,
    name: string,
    username: string,
    phone: string,
    sex: string,
    status: number,
    createTime: string,
    updateTime: string
}

/**
 * 分页结果。
 */
export interface PageResult<T> {
    total: number
    records: T[]
}

/**
 * 查询员工列表。
 */
export function getEmployeeList(page: number, pageSize: number) {
    return request.get<PageResult<EmployeeListItem>, PageResult<EmployeeListItem>>('/admin/employee', {
        params: {
            page,
            pageSize
        }
    });
}


/**
 * 启用或禁用员工账号。
 *
 * @param id 员工 ID
 * @param status 账号状态：1 启用，0 禁用
 */

export function updateEmployeeStatus(id: number, status: number) {
    return request.put(`/admin/employee/status`,null, {
        params: {
            id,
            status
        }
    })
}

/**
 * 根据Id查询员工信息
 */
export interface EmployeeDetail {
    id:number,
    username:string,
    name:string,
    phone:string,
    sex:string,
    idNumber:string,
}

/**
 * 查询员工详情
 */
export function getEmployeeDetail(id: number) {
    return request.get<EmployeeDetail, EmployeeDetail>(`/admin/employee/${id}`);
}

/**
 * 修改员工接口的请求参数。
 */
export interface EmployeeUpdateRequest {
    id: number;
    username: string;
    name: string;
    phone: string;
    sex: string;
    idNumber: string;
}

/**
 * 修改员工基本信息。
 */
export function updateEmployee(data: EmployeeUpdateRequest) {
    return request.put(`/admin/employee`, data);
}


/**
 * 根据员工 ID 删除员工账号。
 */
export function deleteEmployee(id:number){
    return request.delete(`/admin/employee/${id}`)
}