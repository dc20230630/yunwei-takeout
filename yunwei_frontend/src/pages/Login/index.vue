<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-lg border border-gray-100">
      <div>
        <div class="flex justify-center text-4xl">
          🍔
        </div>
        <h2 class="mt-4 text-center text-3xl font-extrabold text-gray-900">云味外卖管理系统</h2>
        <p class="mt-2 text-center text-sm text-gray-500">请输入您的管理员账号进行登录</p>
      </div>
      
      <el-form :model="loginForm" label-position="top" class="mt-8 space-y-6" @submit.prevent="handleLogin">
        <el-form-item label="账号" required>
          <el-input 
            v-model="loginForm.username" 
            placeholder="管理员账号" 
            size="large"
          />
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            show-password 
            placeholder="管理员密码" 
            size="large"
          />
        </el-form-item>
        
        <div class="flex items-center justify-between text-sm">
          <el-checkbox v-model="rememberMe" label="记住我" />
          <a href="#" class="font-medium text-blue-600 hover:text-blue-500">忘记密码？</a>
        </div>

        <div>
          <el-button 
            type="primary" 
            class="w-full" 
            style="height: 44px; font-size: 16px;"
            :loading="loading" 
            @click="handleLogin"
          >
            登 录
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const rememberMe = ref(false);

const loginForm = reactive({
  username: '',
  password: '',
});

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }
  
  loading.value = true;
  
  // 模拟网络请求延迟
  setTimeout(() => {
    try {
      userStore.setToken('mock-token-takeout-admin');
      userStore.setUserInfo({
        id: 1,
        username: loginForm.username,
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        role: 'admin',
      });
      ElMessage.success('登录成功，欢迎回来！');
      router.push('/');
    } catch (error) {
      ElMessage.error('登录出现异常，请稍后重试');
    } finally {
      loading.value = false;
    }
  }, 600);
};
</script>

<style scoped>
</style>
