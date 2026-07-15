import axios, { type AxiosInstance, type AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import { storage } from './storage';

// 定义接口返回的通用结构
export interface ApiResponse<T = any> {
  code: number;
  data: T;
  message: string;
}

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 支持环境变量，默认使用 /api
  timeout: 10000, // 超时时间 10 秒
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从本地存储中获取 token 并拼装到 headers
    const token = storage.get<string>('token');
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;

    // 如果是二进制流等其他数据，直接返回
    if (response.config.responseType === 'blob') {
      return response;
    }

    // 与后端约定的业务状态码，假设 200 为成功
    if (res.code === 200) {
      return res.data;
    } else {
      // 业务报错提示
      ElMessage.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message || 'Error'));
    }
  },
  (error) => {
    let message = '网络请求异常，请稍后重试';
    if (error.response) {
      const status = error.response.status;
      switch (status) {
        case 400:
          // 优先显示后端返回的业务错误，例如“账号或密码错误”
          message = error.response.data?.message || '请求参数错误';
          break;
        case 401:
          message = '未授权，请重新登录';
          // 清除凭证并跳转登录
          storage.remove('token');
          storage.remove('userInfo');
          if (window.location.hash !== '#/login') {
            window.location.href = '#/login';
          }
          break;
        case 403:
          message = '拒绝访问，您没有权限';
          break;
        case 404:
          message = '请求地址错误';
          break;
        case 500:
          message = '服务器端开小差了，请联系管理员';
          break;
        default:
          message = error.response.data?.message || message;
      }
    }
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default request;
