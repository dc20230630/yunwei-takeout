<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-bold text-gray-800">数据统计</h2>
      <p class="text-gray-400 text-sm mt-1">门店经营数据 analysis</p>
    </div>

    <!-- 时间筛选栏 -->
    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex items-center justify-between flex-wrap gap-4">
      <div class="flex gap-2">
        <el-button 
          v-for="time in timeOptions" 
          :key="time.value" 
          :type="selectedTime === time.value ? 'primary' : 'default'"
          @click="selectedTime = time.value"
        >
          {{ time.label }}
        </el-button>
      </div>
      <div>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="default"
          style="width: 280px"
        />
      </div>
    </div>

    <!-- 核心经营数据卡片 -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
      <div v-for="item in statMetrics" :key="item.title" class="bg-white rounded-lg shadow-sm border border-gray-100 p-4">
        <span class="text-xs text-gray-400 font-medium">{{ item.title }}</span>
        <strong class="text-xl font-bold text-gray-800 block mt-3">{{ item.value }}</strong>
      </div>
    </div>

    <!-- 趋势图与菜品排行 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 订单量柱状图 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 lg:col-span-2 flex flex-col justify-between">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">订单量趋势</h3>
        </div>
        <!-- ECharts 柱状图容器 -->
        <div ref="barChartRef" class="h-60 w-full"></div>
      </div>

      <!-- 菜品销售排行 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
        <h3 class="font-bold text-gray-800 mb-4">菜品销售排行</h3>
        <el-table :data="dishRanking" style="width: 100%" size="small" border>
          <el-table-column type="index" label="排名" width="60" align="center" />
          <el-table-column prop="name" label="菜品" min-width="120" />
          <el-table-column prop="sales" label="销量" width="70" align="center" />
          <el-table-column label="销售额" width="90" align="right">
            <template #default="{ row }">
              ¥{{ (row.sales * row.price).toFixed(0) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 经营简析 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <h3 class="font-bold text-gray-800 mb-3">经营概况</h3>
      <p class="text-sm text-gray-500 leading-relaxed">
        近 7 日订单量整体保持稳定上升，晚餐时段 18:00—20:00 为营业额主要高峰。
        其中经典酸梅汤和麻婆豆腐在引流方面表现极佳，月度销量稳居前二，可作为搭配套餐的常驻引流单品；
        目前配送中订单占总单量比例较高，建议高峰期加紧出餐效率并合理调配配送运力，以保证用户体验。
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { useAppStore } from '@/store/app';
import * as echarts from 'echarts';

const appStore = useAppStore();

const selectedTime = ref('7');
const dateRange = ref<[Date, Date] | null>(null);

const timeOptions = [
  { label: '今日', value: '1' },
  { label: '昨日', value: '2' },
  { label: '近7日', value: '7' },
  { label: '近30日', value: '30' }
];

const statMetrics = [
  { title: '营业额', value: '¥ 156,820' },
  { title: '订单量', value: '7,936' },
  { title: '客单价', value: '¥ 42.68' },
  { title: '新增用户', value: '1,286' },
  { title: '有效订单率', value: '94.2%' },
  { title: '复购率', value: '38.6%' }
];

// 销售排行数据
const dishRanking = ref([
  { name: '经典酸梅汤', sales: 920, price: 12 },
  { name: '麻婆豆腐', sales: 680, price: 22 },
  { name: '招牌红烧肉', sales: 540, price: 58 },
  { name: '秘制大盘鸡', sales: 412, price: 68 },
  { name: '红糖糍粑', sales: 340, price: 16 }
]);

// --- Echarts 柱状图实现 ---
const barChartRef = ref<HTMLElement | null>(null);
let barChart: echarts.ECharts | null = null;

const initChart = () => {
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value);
    barChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: '{b}日: {c}单'
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
        data: ['1日', '2日', '3日', '4日', '5日', '6日', '7日', '8日', '9日', '10日', '11日', '12日'],
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
          name: '订单量',
          type: 'bar',
          barWidth: '40%',
          itemStyle: {
            color: '#3b82f6',
            borderRadius: [4, 4, 0, 0]
          },
          emphasis: {
            itemStyle: { color: '#2563eb' }
          },
          data: [58, 78, 52, 92, 66, 84, 73, 96, 82, 105, 90, 112]
        }
      ]
    });
  }
};

const handleResize = () => {
  barChart?.resize();
};

onMounted(() => {
  initChart();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  barChart?.dispose();
});

watch(() => appStore.isCollapse, () => {
  setTimeout(handleResize, 320);
});
</script>

<style scoped>
</style>
