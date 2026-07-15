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

export function employeeLogin(data:EmployeeLoginRequest){
    return request.post<EmployeeLoginResponse,EmployeeLoginResponse>(
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
    id:number,
    name:string,
    username:string,
    phone:string,
    sex:string,
    status:number,
    createTime:string,
    updateTime:string
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
export function getEmployeeList(page: number, pageSize: number){
    return request.get<PageResult<EmployeeListItem>,PageResult<EmployeeListItem>>('/admin/employee',{
        params:{
            page,
            pageSize
        }
    });
}