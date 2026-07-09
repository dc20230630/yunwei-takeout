/**
 * 本地存储封装工具
 * 包含对 localStorage 和 sessionStorage 的安全读写，自动处理 JSON 序列化
 */

const PREFIX = 'takeout_admin_';

export const storage = {
  /**
   * 设置本地存储
   */
  set(key: string, value: any, expire: number | null = null): void {
    const data = {
      value,
      expire: expire ? new Date().getTime() + expire * 1000 : null,
    };
    localStorage.setItem(PREFIX + key, JSON.stringify(data));
  },

  /**
   * 获取本地存储
   */
  get<T = any>(key: string): T | null {
    const raw = localStorage.getItem(PREFIX + key);
    if (!raw) return null;

    try {
      const data = JSON.parse(raw);
      if (data.expire && new Date().getTime() > data.expire) {
        this.remove(PREFIX + key);
        return null;
      }
      return data.value as T;
    } catch (e) {
      return null;
    }
  },

  /**
   * 移除本地存储
   */
  remove(key: string): void {
    localStorage.removeItem(PREFIX + key);
  },

  /**
   * 清空所有本项目相关的本地存储
   */
  clear(): void {
    const keys = Object.keys(localStorage);
    keys.forEach((key) => {
      if (key.startsWith(PREFIX)) {
        localStorage.removeItem(key);
      }
    });
  },
};
