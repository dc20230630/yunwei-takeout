<template>
  <el-container class="h-screen w-screen overflow-hidden">
    <!-- 侧边栏 -->
    <el-aside
      :width="appStore.isCollapse ? '64px' : '220px'"
      class="transition-all duration-300 bg-[#102d48] text-white flex flex-col shrink-0"
    >
      <div
        class="h-16 flex items-center justify-center border-b border-[#0b243b] font-bold text-lg overflow-hidden whitespace-nowrap"
      >
        <span v-if="!appStore.isCollapse" class="flex items-center gap-2">
          <span class="text-xl"
            ><img src="/yunwei_icon.svg" alt="yunwei" width="36" height="36"
          /></span>
          云味外卖管理
        </span>
        <span v-else class="text-xl"
          ><img src="/yunwei_icon.svg" alt="yunwei" width="36" height="36"
        /></span>
      </div>

      <!-- 侧边菜单 (开启路由模式) -->
      <el-menu
        :collapse="appStore.isCollapse"
        background-color="#102d48"
        text-color="#d9e5f1"
        active-text-color="#ffffff"
        :default-active="route.path"
        router
        class="border-none flex-1 overflow-y-auto"
      >
        <el-menu-item index="/">
          <el-icon><Monitor /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>

        <el-menu-item index="/order">
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>

        <el-menu-item index="/dish">
          <el-icon><Food /></el-icon>
          <template #title>菜品管理</template>
        </el-menu-item>

        <el-menu-item index="/combo">
          <el-icon><Box /></el-icon>
          <template #title>套餐管理</template>
        </el-menu-item>

        <el-menu-item index="/category">
          <el-icon><MenuIcon /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>

        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/staff">
          <el-icon><Avatar /></el-icon>
          <template #title>员工管理</template>
        </el-menu-item>

        <el-menu-item index="/stat">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>

        <el-menu-item index="/setting">
          <el-icon><Setting /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>

      <!-- 门店状态标识 -->
      <div
        v-if="!appStore.isCollapse"
        class="p-4 border-t border-[#0b243b] text-xs text-[#a9bdd0] space-y-1 bg-[#0b243b]"
      >
        <strong>望京 SOHO 店</strong>
        <div>门店编号：YW-1001</div>
        <div class="flex items-center gap-1.5 mt-1">
          <span class="w-2 h-2 rounded-full bg-green-500 animate-ping"></span>
          <span class="text-green-400">今日营业中</span>
        </div>
      </div>
    </el-aside>

    <!-- 主体内容容器 -->
    <el-container class="flex flex-col h-full overflow-hidden">
      <!-- 顶部头部 -->
      <el-header
        class="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-6 shadow-sm z-10"
      >
        <div class="flex items-center gap-4">
          <el-icon class="cursor-pointer text-xl" @click="appStore.toggleCollapse">
            <Fold v-if="!appStore.isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb>
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.path !== '/'">{{
              currentRouteTitle
            }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 用户信息下拉菜单 -->
        <div class="flex items-center gap-4">
          <!-- 消息提醒 -->
          <div class="relative cursor-pointer mr-2">
            <el-badge :value="12" class="item">
              <el-icon size="20" class="text-gray-600 hover:text-gray-800"><Bell /></el-icon>
            </el-badge>
          </div>

          <el-divider direction="vertical" />

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="flex items-center gap-2 cursor-pointer outline-none">
              <div
                class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center font-bold text-blue-600 text-sm"
              >
                管
              </div>
              <div class="text-left leading-tight hidden md:block">
                <span class="text-sm font-medium text-gray-700 block">{{
                  userStore.userInfo?.username || 'admin'
                }}</span>
                <span class="text-[10px] text-gray-400">超级管理员</span>
              </div>
              <span style="color: #7e8794">⌄</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="bg-gray-50 p-6 overflow-y-auto">
        <!-- 路由出口 -->
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'
import {
  Monitor,
  Document,
  Food,
  Box,
  Menu as MenuIcon,
  User,
  Avatar,
  DataAnalysis,
  Setting,
  Fold,
  Expand,
  Bell
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const currentRouteTitle = computed(() => {
  return route.meta?.title || '详情'
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
/* 激活菜单背景色定制 */
.el-menu-item.is-active {
  background: linear-gradient(90deg, #1479ff, #2f8cff) !important;
  color: #ffffff !important;
  box-shadow: 0 5px 14px rgba(0, 98, 255, 0.2);
}
</style>
