<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-gray-50 px-4">
    <div class="text-center">
      <div class="text-9xl font-extrabold text-blue-500 select-none animate-pulse">404</div>
      <h3 class="text-2xl font-bold mt-4 text-gray-800">页面未找到</h3>
      <p class="text-gray-500 mt-2">抱歉，您要访问的网页可能已被删除、更名或暂时不可用。</p>
      <div class="mt-8">
        <el-button type="primary" size="large" @click="goHome">
          返回控制台
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.push('/');
};
</script>

<style scoped>
</style>
