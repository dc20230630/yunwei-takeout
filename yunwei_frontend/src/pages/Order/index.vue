<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-bold text-gray-800">订单管理</h2>
      <p class="text-gray-400 text-sm mt-1">查看并处理门店订单</p>
    </div>

    <!-- 过滤工具栏 -->
    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">订单号</label>
        <el-input v-model="filterForm.id" placeholder="请输入订单号" size="default" style="width: 200px;" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">用户手机号</label>
        <el-input v-model="filterForm.phone" placeholder="请输入手机号" size="default" style="width: 180px;" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">订单状态</label>
        <el-select v-model="filterForm.status" placeholder="全部状态" size="default" style="width: 150px;" clearable>
          <el-option label="待接单" value="pending" />
          <el-option label="制作中" value="making" />
          <el-option label="配送中" value="shipping" />
          <el-option label="已完成" value="done" />
          <el-option label="已取消" value="cancel" />
        </el-select>
      </div>
      <div class="flex gap-2">
        <el-button type="primary" @click="handleFilter">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 订单列表卡片 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">订单列表</h3>
        <span class="text-xs text-gray-400">共 {{ filteredOrders.length }} 条</span>
      </div>

      <el-table :data="filteredOrders" style="width: 100%" size="default">
        <el-table-column prop="id" label="订单编号" width="160" />
        <el-table-column prop="user" label="用户" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="pay" label="支付方式" width="110" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="下单时间" width="170" />
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="{ row }">
            <div class="flex gap-2">
              <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
              <el-button 
                v-if="row.status === 'pending'" 
                link 
                type="success" 
                size="small" 
                @click="acceptOrder(row.id)"
              >
                接单
              </el-button>
              <el-button 
                v-if="['pending', 'making', 'shipping'].includes(row.status)" 
                link 
                type="danger" 
                size="small" 
                @click="cancelOrder(row.id)"
              >
                取消
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 订单详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="订单详情"
      size="500px"
      direction="rtl"
      destroy-on-close
    >
      <div v-if="selectedOrder" class="space-y-6 text-sm">
        <!-- 基础信息 -->
        <div>
          <h4 class="font-bold text-gray-800 border-b border-gray-100 pb-2 mb-3">基本信息</h4>
          <div class="grid grid-cols-2 gap-y-3 gap-x-4">
            <div><span class="text-gray-400 block mb-0.5">订单编号</span> <span class="text-gray-700 font-medium">{{ selectedOrder.id }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">订单状态</span> 
              <el-tag :type="getStatusTagType(selectedOrder.status)" size="small">
                {{ getStatusText(selectedOrder.status) }}
              </el-tag>
            </div>
            <div><span class="text-gray-400 block mb-0.5">下单时间</span> <span class="text-gray-700">{{ selectedOrder.time }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">支付方式</span> <span class="text-gray-700">{{ selectedOrder.pay }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">用户名</span> <span class="text-gray-700 font-medium">{{ selectedOrder.user }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">联系电话</span> <span class="text-gray-700">{{ selectedOrder.phone }}</span></div>
            <div class="col-span-2"><span class="text-gray-400 block mb-0.5">配送方式</span> <span class="text-gray-700">{{ selectedOrder.delivery }}</span></div>
            <div class="col-span-2"><span class="text-gray-400 block mb-0.5">收货地址</span> <span class="text-gray-700">{{ selectedOrder.address }}</span></div>
            <div class="col-span-2"><span class="text-gray-400 block mb-0.5">订单备注</span> <span class="text-gray-700">{{ selectedOrder.note || '无' }}</span></div>
          </div>
        </div>

        <!-- 商品明细 -->
        <div>
          <h4 class="font-bold text-gray-800 border-b border-gray-100 pb-2 mb-3">商品明细</h4>
          <el-table :data="selectedOrder.items" size="small" border style="width: 100%">
            <el-table-column prop="0" label="商品" />
            <el-table-column prop="1" label="规格" width="100" />
            <el-table-column prop="2" label="数量" width="60" align="center">
              <template #default="{ row }">
                x{{ row[2] }}
              </template>
            </el-table-column>
            <el-table-column label="单价" width="80" align="right">
              <template #default="{ row }">
                ¥{{ row[3].toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 结算明细 -->
        <div class="bg-gray-50 rounded-lg p-4 space-y-2 border border-gray-100">
          <div class="flex justify-between text-gray-600">
            <span>商品小计</span>
            <span>¥{{ getSubtotal(selectedOrder).toFixed(2) }}</span>
          </div>
          <div class="flex justify-between text-gray-600">
            <span>配送费</span>
            <span>¥{{ selectedOrder.fee.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between text-gray-600">
            <span>优惠</span>
            <span class="text-red-500">- ¥{{ selectedOrder.discount.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between font-bold text-base text-gray-800 pt-2 border-t border-gray-200">
            <span>实付金额</span>
            <span class="text-red-500">¥{{ selectedOrder.amount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 抽屉脚部按钮 -->
        <div class="flex justify-end gap-2 pt-4">
          <el-button v-if="selectedOrder.status === 'pending'" type="primary" @click="acceptOrder(selectedOrder.id)">
            立即接单
          </el-button>
          <el-button @click="drawerVisible = false">关闭</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();

// 模拟初始订单数据
const orders = ref([
  {
    id: 'DD202405210001',
    user: '张三',
    phone: '13812345678',
    amount: 45.60,
    status: 'pending',
    time: '2024-05-21 10:23:45',
    delivery: '蜂鸟专送',
    pay: '微信支付',
    address: '北京市朝阳区望京 SOHO T2 1502',
    note: '微辣，不要香菜',
    items: [
      ['招牌红烧肉套餐', '标准/微辣', 1, 38],
      ['酸梅汤', '少冰', 1, 5]
    ],
    fee: 4,
    discount: 1.4
  },
  {
    id: 'DD202405210002',
    user: '李四',
    phone: '18698765432',
    amount: 32.80,
    status: 'making',
    time: '2024-05-21 10:22:18',
    delivery: '商家配送',
    pay: '支付宝',
    address: '北京市朝阳区金辉大厦 8 层',
    note: '餐具 2 份',
    items: [
      ['老北京炸酱面', '加面', 1, 28]
    ],
    fee: 5,
    discount: 0.2
  },
  {
    id: 'DD202405210003',
    user: '王五',
    phone: '15544332211',
    amount: 28.90,
    status: 'shipping',
    time: '2024-05-21 10:20:55',
    delivery: '蜂鸟专送',
    pay: '微信支付',
    address: '北京市朝阳区望京花园 7-2-601',
    note: '到门口电话联系',
    items: [
      ['麻婆豆腐盖饭', '中辣', 1, 25]
    ],
    fee: 4,
    discount: 0.1
  },
  {
    id: 'DD202405210004',
    user: '赵六',
    phone: '17788990011',
    amount: 56.70,
    status: 'done',
    time: '2024-05-21 10:19:33',
    delivery: '蜂鸟专送',
    pay: '云闪付',
    address: '北京市朝阳区融科望京 A 座',
    note: '少油',
    items: [
      ['双拼商务套餐', '标准', 1, 52]
    ],
    fee: 5,
    discount: 0.3
  },
  {
    id: 'DD202405210005',
    user: '孙七',
    phone: '13955667788',
    amount: 19.90,
    status: 'done',
    time: '2024-05-21 10:18:07',
    delivery: '商家配送',
    pay: '微信支付',
    address: '北京市东城区东直门外大街 12 号',
    note: '',
    items: [
      ['经典酸辣粉', '微辣', 1, 16]
    ],
    fee: 4,
    discount: 0.1
  }
]);

// 过滤表单
const filterForm = reactive({
  id: '',
  phone: '',
  status: ''
});

// 过滤结果
const filteredOrders = computed(() => {
  return orders.value.filter(o => {
    const matchId = !filterForm.id || o.id.toLowerCase().includes(filterForm.id.trim().toLowerCase());
    const matchPhone = !filterForm.phone || o.phone.includes(filterForm.phone.trim());
    const matchStatus = !filterForm.status || o.status === filterForm.status;
    return matchId && matchPhone && matchStatus;
  });
});

// 抽屉管理
const drawerVisible = ref(false);
const selectedOrder = ref<any>(null);

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

const getSubtotal = (order: any) => {
  return order.items.reduce((sum: number, item: any) => sum + (item[2] * item[3]), 0);
};

const handleFilter = () => {
  // 依靠 computed 自动更新
};

const handleReset = () => {
  filterForm.id = '';
  filterForm.phone = '';
  filterForm.status = '';
};

// 接单
const acceptOrder = (id: string) => {
  const order = orders.value.find(o => o.id === id);
  if (order) {
    order.status = 'making';
    ElMessage.success('接单成功，已进入制作阶段！');
    if (selectedOrder.value && selectedOrder.value.id === id) {
      selectedOrder.value.status = 'making';
    }
  }
};

// 取消订单
const cancelOrder = (id: string) => {
  ElMessageBox.confirm(
    '确认要取消该订单吗？此操作不可逆。',
    '提示',
    {
      confirmButtonText: '确定取消',
      cancelButtonText: '暂不取消',
      type: 'warning',
    }
  ).then(() => {
    const order = orders.value.find(o => o.id === id);
    if (order) {
      order.status = 'cancel';
      ElMessage.error('订单已被取消');
      if (selectedOrder.value && selectedOrder.value.id === id) {
        selectedOrder.value.status = 'cancel';
      }
    }
  }).catch(() => {});
};

// 查看详情
const viewDetail = (order: any) => {
  selectedOrder.value = order;
  drawerVisible.value = true;
};

// 页面加载时的行为
onMounted(() => {
  // 如果是从外面带 id 路由跳转来的，默认筛选出该订单并弹出详情
  const queryId = route.query.id as string;
  if (queryId) {
    const matched = orders.value.find(o => o.id === queryId);
    if (matched) {
      viewDetail(matched);
    }
  }
});
</script>

<style scoped>
</style>
