<template>
  <!-- 登录、注册等页面使用的空白基础布局 -->
  <div class="auth-layout-container w-full h-full min-h-screen">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 布局脚本，目前不需要额外逻辑
</script>

<style scoped>
.auth-layout-container {
  background-color: #f9fafb;
}
</style>
