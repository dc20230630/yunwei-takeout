import { defineStore } from 'pinia';
import { ref } from 'vue';
import { storage } from '@/utils/storage';

export interface UserInfo {
  id: number;
  username: string;
  avatar?: string;
  role?: string;
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(storage.get<string>('token'));
  const userInfo = ref<UserInfo | null>(storage.get<UserInfo>('userInfo'));

  const setToken = (newToken: string) => {
    token.value = newToken;
    storage.set('token', newToken);
  };

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info;
    storage.set('userInfo', info);
  };

  const logout = () => {
    token.value = null;
    userInfo.value = null;
    storage.remove('token');
    storage.remove('userInfo');
  };

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    logout,
  };
});
