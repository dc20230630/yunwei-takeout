<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-bold text-gray-800">用户管理</h2>
      <p class="text-gray-400 text-sm mt-1">查看用户注册与消费情况</p>
    </div>

    <!-- 用户数据卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 flex items-center justify-between">
        <div>
          <span class="text-xs text-gray-400 font-medium">用户总数</span>
          <strong class="text-2xl font-bold text-gray-800 block mt-2">12,860</strong>
          <span class="text-xs text-gray-400 block mt-1.5">累计注册用户</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-blue-50 text-blue-500 flex items-center justify-center">
          <el-icon size="22"><User /></el-icon>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 flex items-center justify-between">
        <div>
          <span class="text-xs text-gray-400 font-medium">今日新增</span>
          <strong class="text-2xl font-bold text-gray-800 block mt-2">216</strong>
          <span class="text-xs text-green-500 block mt-1.5">较昨日 +21.35%</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-green-50 text-green-500 flex items-center justify-center">
          <el-icon size="22"><User /></el-icon>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 flex items-center justify-between">
        <div>
          <span class="text-xs text-gray-400 font-medium">本月新增</span>
          <strong class="text-2xl font-bold text-gray-800 block mt-2">2,384</strong>
          <span class="text-xs text-gray-400 block mt-1.5">本月累计</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-orange-50 text-orange-500 flex items-center justify-center">
          <el-icon size="22"><User /></el-icon>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 flex items-center justify-between">
        <div>
          <span class="text-xs text-gray-400 font-medium">活跃用户</span>
          <strong class="text-2xl font-bold text-gray-800 block mt-2">8,642</strong>
          <span class="text-xs text-purple-500 block mt-1.5">近30日活跃</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-purple-50 text-purple-500 flex items-center justify-center">
          <el-icon size="22"><User /></el-icon>
        </div>
      </div>
    </div>

    <!-- 用户列表卡片 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">用户列表</h3>
        <span class="text-xs text-gray-400">共 {{ users.length }} 个会员</span>
      </div>

      <el-table :data="users" style="width: 100%" size="default">
        <el-table-column label="用户" min-width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="w-6.5 h-6.5 rounded-full bg-blue-50 text-blue-600 font-bold flex items-center justify-center text-xs">
                {{ row.name.slice(-1) }}
              </span>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="register" label="注册时间" width="170" />
        <el-table-column prop="orders" label="订单数量" width="100" align="center" />
        <el-table-column label="累计消费" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.total.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="账号状态" width="110">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status"
              active-text="正常"
              inactive-text="禁用"
              inline-prompt
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="last" label="最近登录" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewUser(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 用户详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="用户详情"
      size="460px"
      direction="rtl"
      destroy-on-close
    >
      <div v-if="selectedUser" class="space-y-6 text-sm">
        <!-- 头部个人简介 -->
        <div class="flex items-center gap-4 border-b border-gray-100 pb-4">
          <div class="w-14 h-14 rounded-full bg-blue-100 text-blue-600 font-bold text-2xl flex items-center justify-center">
            {{ selectedUser.name.slice(-1) }}
          </div>
          <div>
            <b class="text-lg text-gray-800">{{ selectedUser.name }}</b>
            <div class="text-gray-400 text-xs mt-1">{{ selectedUser.phone }}</div>
          </div>
        </div>

        <!-- 详细概览 -->
        <div>
          <h4 class="font-bold text-gray-800 mb-3">账号概况</h4>
          <div class="grid grid-cols-2 gap-y-3 gap-x-4">
            <div><span class="text-gray-400 block mb-0.5">用户 ID</span> <span class="text-gray-700 font-medium">{{ selectedUser.id }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">账号状态</span> 
              <el-tag :type="selectedUser.status ? 'success' : 'danger'" size="small">
                {{ selectedUser.status ? '正常' : '停用' }}
              </el-tag>
            </div>
            <div><span class="text-gray-400 block mb-0.5">注册时间</span> <span class="text-gray-700">{{ selectedUser.register }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">最近登录</span> <span class="text-gray-700">{{ selectedUser.last }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">历史订单数</span> <span class="text-gray-700 font-medium">{{ selectedUser.orders }} 单</span></div>
            <div><span class="text-gray-400 block mb-0.5">累计消费额</span> <span class="text-red-500 font-semibold">¥{{ selectedUser.total.toFixed(2) }}</span></div>
          </div>
        </div>
        
        <!-- 按钮 -->
        <div class="flex justify-end pt-4 border-t border-gray-100">
          <el-button @click="drawerVisible = false">关闭</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { User } from '@element-plus/icons-vue';

// 模拟初始用户数据
const users = ref([
  { id: 'U1001', name: '张伟', phone: '13812345678', register: '2026-01-12 14:22', orders: 24, total: 1450.50, status: true, last: '2026-07-08 11:35' },
  { id: 'U1002', name: '李娜', phone: '18698765432', register: '2026-02-15 09:12', orders: 18, total: 890.00, status: true, last: '2026-07-08 11:20' },
  { id: 'U1003', name: '王敏', phone: '15544332211', register: '2026-03-01 18:45', orders: 32, total: 2890.00, status: true, last: '2026-07-08 10:45' },
  { id: 'U1004', name: '刘洋', phone: '17788990011', register: '2026-04-20 11:05', orders: 5, total: 168.50, status: true, last: '2026-07-08 11:42' },
  { id: 'U1005', name: '陈强', phone: '13955667788', register: '2026-05-02 16:30', orders: 12, total: 612.00, status: false, last: '2026-07-08 09:15' }
]);

const drawerVisible = ref(false);
const selectedUser = ref<any>(null);

const toggleStatus = (row: any) => {
  ElMessage.success(`用户“${row.name}”的账户已被${row.status ? '启用' : '禁用'}`);
};

const viewUser = (row: any) => {
  selectedUser.value = row;
  drawerVisible.value = true;
};
</script>

<style scoped>
</style>
