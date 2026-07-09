<template>
  <div class="space-y-6">
    <!-- 头部欢迎语 -->
    <div class="flex justify-between items-baseline">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">数据概览</h2>
        <p class="text-gray-400 text-sm mt-1">您好，{{ username }}，欢迎使用云味外卖管理系统！</p>
      </div>
    </div>

    <!-- 数据指标卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- 今日订单 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 relative overflow-hidden">
        <h4 class="text-gray-400 text-sm font-medium">今日订单</h4>
        <div class="text-3xl font-bold text-gray-800 mt-2">1,286</div>
        <div class="text-gray-400 text-xs mt-2">昨日 1,152</div>
        <div class="absolute right-4 top-4 w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center text-blue-500">
          <el-icon size="22"><Document /></el-icon>
        </div>
        <div class="text-blue-500 text-xs mt-3">↑ 11.63%</div>
      </div>

      <!-- 今日营业额 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 relative overflow-hidden">
        <h4 class="text-gray-400 text-sm font-medium">今日营业额</h4>
        <div class="text-3xl font-bold text-gray-800 mt-2">¥23,560</div>
        <div class="text-gray-400 text-xs mt-2">昨日 ¥19,876</div>
        <div class="absolute right-4 top-4 w-12 h-12 rounded-full bg-green-50 flex items-center justify-center text-green-500">
          <el-icon size="22"><Money /></el-icon>
        </div>
        <div class="text-green-500 text-xs mt-3">↑ 18.56%</div>
      </div>

      <!-- 待处理订单 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 relative overflow-hidden">
        <h4 class="text-gray-400 text-sm font-medium">待处理订单</h4>
        <div class="text-3xl font-bold text-gray-800 mt-2">84</div>
        <div class="text-gray-400 text-xs mt-2">昨日 67</div>
        <div class="absolute right-4 top-4 w-12 h-12 rounded-full bg-orange-50 flex items-center justify-center text-orange-500">
          <el-icon size="22"><Clock /></el-icon>
        </div>
        <div class="text-orange-500 text-xs mt-3">↑ 25.37%</div>
      </div>

      <!-- 新增用户 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 relative overflow-hidden">
        <h4 class="text-gray-400 text-sm font-medium">新增用户</h4>
        <div class="text-3xl font-bold text-gray-800 mt-2">216</div>
        <div class="text-gray-400 text-xs mt-2">昨日 178</div>
        <div class="absolute right-4 top-4 w-12 h-12 rounded-full bg-purple-50 flex items-center justify-center text-purple-500">
          <el-icon size="22"><User /></el-icon>
        </div>
        <div class="text-purple-500 text-xs mt-3">↑ 21.35%</div>
      </div>
    </div>

    <!-- 趋势与状态分布图表区 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 折线图 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 lg:col-span-2 flex flex-col justify-between">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">近7日营业趋势</h3>
          <el-select v-model="trendDays" size="small" style="width: 100px">
            <el-option label="近7日" value="7" />
            <el-option label="近30日" value="30" />
          </el-select>
        </div>
        <!-- Echarts 折线图容器 -->
        <div ref="trendChartRef" class="h-60 w-full"></div>
      </div>

      <!-- 环形图 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 flex flex-col justify-between">
        <h3 class="font-bold text-gray-800 mb-4">订单状态分布</h3>
        <!-- Echarts 圆环图容器 -->
        <div ref="donutChartRef" class="h-60 w-full"></div>
      </div>
    </div>

    <!-- 底部最新订单与快捷操作 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 最新订单 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 lg:col-span-2">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">最新订单</h3>
          <el-button link type="primary" @click="goToPage('Order')">管理全部</el-button>
        </div>
        <el-table :data="latestOrders" style="width: 100%" size="small">
          <el-table-column prop="id" label="订单编号" width="140" />
          <el-table-column prop="user" label="用户" width="100">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <span class="w-6 h-6 rounded-full bg-blue-50 text-blue-500 text-xs flex items-center justify-center font-bold">
                  {{ row.user.slice(-1) }}
                </span>
                <span>{{ row.user }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="90">
            <template #default="{ row }">
              ¥{{ row.amount.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="下单时间" width="150" />
          <el-table-column prop="delivery" label="配送方式" />
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="viewOrderDetail(row.id)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 快捷操作 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
        <h3 class="font-bold text-gray-800 mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-4">
          <button 
            @click="quickAction('addDish')" 
            class="flex items-center gap-3 p-4 bg-blue-50/50 hover:bg-blue-50 border border-blue-50 hover:border-blue-100 rounded-lg text-left transition-all group cursor-pointer"
          >
            <div class="w-10 h-10 rounded-lg bg-blue-500 text-white flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <el-icon size="20"><Plus /></el-icon>
            </div>
            <div>
              <strong class="text-sm font-semibold text-gray-800 block">新增菜品</strong>
              <span class="text-xs text-gray-400 mt-1 block">录入新商品信息</span>
            </div>
          </button>

          <button 
            @click="quickAction('publishActivity')" 
            class="flex items-center gap-3 p-4 bg-orange-50/50 hover:bg-orange-50 border border-orange-50 hover:border-orange-100 rounded-lg text-left transition-all group cursor-pointer"
          >
            <div class="w-10 h-10 rounded-lg bg-orange-500 text-white flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <el-icon size="20"><Promotion /></el-icon>
            </div>
            <div>
              <strong class="text-sm font-semibold text-gray-800 block">发布活动</strong>
              <span class="text-xs text-gray-400 mt-1 block">创建营销折扣</span>
            </div>
          </button>

          <button 
            @click="quickAction('viewStats')" 
            class="flex items-center gap-3 p-4 bg-green-50/50 hover:bg-green-50 border border-green-50 hover:border-green-100 rounded-lg text-left transition-all group cursor-pointer"
          >
            <div class="w-10 h-10 rounded-lg bg-green-500 text-white flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <el-icon size="20"><DataAnalysis /></el-icon>
            </div>
            <div>
              <strong class="text-sm font-semibold text-gray-800 block">查看报表</strong>
              <span class="text-xs text-gray-400 mt-1 block">门店经营分析</span>
            </div>
          </button>

          <button 
            @click="quickAction('processRefund')" 
            class="flex items-center gap-3 p-4 bg-purple-50/50 hover:bg-purple-50 border border-purple-50 hover:border-purple-100 rounded-lg text-left transition-all group cursor-pointer"
          >
            <div class="w-10 h-10 rounded-lg bg-purple-500 text-white flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <el-icon size="20"><RefreshLeft /></el-icon>
            </div>
            <div>
              <strong class="text-sm font-semibold text-gray-800 block">退款处理</strong>
              <span class="text-xs text-gray-400 mt-1 block">审批客户退款</span>
            </div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { useAppStore } from '@/store/app';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';
import { 
  Document, 
  Money, 
  Clock, 
  User, 
  Plus, 
  Promotion, 
  DataAnalysis, 
  RefreshLeft 
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const appStore = useAppStore();

const username = computed(() => userStore.userInfo?.username || '管理员');
const trendDays = ref('7');

// 模拟最新订单数据
const latestOrders = ref([
  { id: 'DD202405210001', user: '张三', amount: 45.60, status: 'pending', time: '2024-05-21 10:23:45', delivery: '蜂鸟专送' },
  { id: 'DD202405210002', user: '李四', amount: 32.80, status: 'making', time: '2024-05-21 10:22:18', delivery: '商家配送' },
  { id: 'DD202405210003', user: '王五', amount: 28.90, status: 'shipping', time: '2024-05-21 10:20:55', delivery: '蜂鸟专送' },
  { id: 'DD202405210004', user: '赵六', amount: 56.70, status: 'done', time: '2024-05-21 10:19:33', delivery: '蜂鸟专送' },
  { id: 'DD202405210005', user: '孙七', amount: 19.90, status: 'done', time: '2024-05-21 10:18:07', delivery: '商家配送' }
]);

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待接单',
    making: '制作中',
    shipping: '配送中',
    done: '已完成',
    cancel: '已取消'
  };
  return map[status] || '未知';
};

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'danger',
    making: 'primary',
    shipping: 'warning',
    done: 'success',
    cancel: 'info'
  };
  return map[status] || 'info';
};

const goToPage = (name: string) => {
  router.push({ name });
};

const viewOrderDetail = (orderId: string) => {
  router.push({ name: 'Order', query: { id: orderId } });
};

const quickAction = (action: string) => {
  if (action === 'addDish') {
    router.push({ name: 'Dish', query: { action: 'add' } });
  } else if (action === 'viewStats') {
    router.push({ name: 'Stat' });
  } else if (action === 'publishActivity') {
    ElMessage.success('已跳转至发布活动页面（接口正在接入中...）');
  } else if (action === 'processRefund') {
    ElMessage.success('已为您跳转退款队列（接口正在接入中...）');
  }
};

// --- Echarts 图表实现 ---
const trendChartRef = ref<HTMLElement | null>(null);
const donutChartRef = ref<HTMLElement | null>(null);

let trendChart: echarts.ECharts | null = null;
let donutChart: echarts.ECharts | null = null;

const initCharts = () => {
  // 1. 折线图
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value);
    trendChart.setOption({
      tooltip: {
        trigger: 'axis',
        formatter: '{b}日: {c} 元'
      },
      grid: {
        left: '2%',
        right: '2%',
        top: '12%',
        bottom: '8%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['05-15', '05-16', '05-17', '05-18', '05-19', '05-20', '05-21'],
        axisLine: { lineStyle: { color: '#e5e7eb' } },
        axisLabel: { color: '#9ca3af' }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: '#f3f4f6' } },
        axisLabel: { color: '#9ca3af' }
      },
      series: [
        {
          name: '营业额',
          type: 'line',
          smooth: true,
          showSymbol: true,
          symbolSize: 8,
          itemStyle: { color: '#3b82f6' },
          lineStyle: { width: 3, color: '#3b82f6' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(59, 130, 246, 0.15)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.01)' }
            ])
          },
          data: [15000, 23000, 32000, 21000, 18000, 28000, 36000]
        }
      ]
    });
  }

  // 2. 环形图
  if (donutChartRef.value) {
    donutChart = echarts.init(donutChartRef.value);
    donutChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}单 ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: '0%',
        top: 'middle',
        itemWidth: 12,
        itemHeight: 8,
        itemGap: 12,
        textStyle: { fontSize: 12, color: '#4b5563' }
      },
      series: [
        {
          name: '订单状态',
          type: 'pie',
          radius: ['55%', '80%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold',
              formatter: '{b}\n{c}单'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 84, name: '待接单', itemStyle: { color: '#3b82f6' } },
            { value: 218, name: '制作中', itemStyle: { color: '#10b981' } },
            { value: 326, name: '待配送', itemStyle: { color: '#f59e0b' } },
            { value: 512, name: '配送中', itemStyle: { color: '#06b6d4' } },
            { value: 210, name: '已完成', itemStyle: { color: '#8b5cf6' } }
          ]
        }
      ]
    });
  }
};

const resizeCharts = () => {
  trendChart?.resize();
  donutChart?.resize();
};

onMounted(() => {
  initCharts();
  window.addEventListener('resize', resizeCharts);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts);
  trendChart?.dispose();
  donutChart?.dispose();
});

// 监听侧边栏折叠以自动 resize 图表
watch(() => appStore.isCollapse, () => {
  setTimeout(resizeCharts, 320);
});
</script>

<style scoped>
</style>
