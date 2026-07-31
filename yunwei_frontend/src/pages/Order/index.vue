<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-bold text-gray-800">订单管理</h2>
      <p class="text-gray-400 text-sm mt-1">查看并处理门店订单</p>
    </div>

    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">订单号</label>
        <el-input v-model="filterForm.number" placeholder="请输入订单号" style="width: 200px" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">收货人手机号</label>
        <el-input v-model="filterForm.phone" placeholder="请输入手机号" style="width: 180px" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">订单状态</label>
        <el-select v-model="filterForm.status" placeholder="全部状态" style="width: 150px" clearable>
          <el-option label="待支付" :value="1" />
          <el-option label="待接单" :value="2" />
          <el-option label="制作中" :value="3" />
          <el-option label="配送中" :value="4" />
          <el-option label="已完成" :value="5" />
          <el-option label="已取消" :value="6" />
          <el-option label="退款中" :value="7" />
        </el-select>
      </div>
      <div class="flex gap-2">
        <el-button type="primary" @click="loadOrders">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">订单列表</h3>
        <span class="text-xs text-gray-400">共 {{ orders.length }} 条</span>
      </div>

      <el-table v-loading="loading" :data="orders" style="width: 100%">
        <el-table-column prop="number" label="订单编号" min-width="180" />
        <el-table-column prop="consignee" label="收货人" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column label="支付方式" width="110">
          <template #default="{ row }">{{ getPayMethodText(row.payMethod) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.orderTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="{ row }">
            <div class="flex gap-2">
              <el-button link type="primary" size="small" @click="viewDetail(row.id)">详情</el-button>
              <el-button v-if="row.status === 2" link type="success" size="small" @click="acceptOrder(row.id)">
                接单
              </el-button>
              <el-button v-if="row.status === 2 || row.status === 3" link type="danger" size="small" @click="cancelOrder(row.id)">
                取消
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer v-model="drawerVisible" title="订单详情" size="500px" destroy-on-close>
      <div v-if="selectedOrder" class="space-y-6 text-sm">
        <div>
          <h4 class="font-bold text-gray-800 border-b border-gray-100 pb-2 mb-3">基本信息</h4>
          <div class="grid grid-cols-2 gap-y-3 gap-x-4">
            <div><span class="text-gray-400 block mb-0.5">订单编号</span><span class="text-gray-700 font-medium">{{ selectedOrder.number }}</span></div>
            <div>
              <span class="text-gray-400 block mb-0.5">订单状态</span>
              <el-tag :type="getStatusTagType(selectedOrder.status)" size="small">{{ getStatusText(selectedOrder.status) }}</el-tag>
            </div>
            <div><span class="text-gray-400 block mb-0.5">下单时间</span><span class="text-gray-700">{{ formatDateTime(selectedOrder.orderTime) }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">支付方式</span><span class="text-gray-700">{{ getPayMethodText(selectedOrder.payMethod) }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">收货人</span><span class="text-gray-700 font-medium">{{ selectedOrder.consignee }}</span></div>
            <div><span class="text-gray-400 block mb-0.5">联系电话</span><span class="text-gray-700">{{ selectedOrder.phone }}</span></div>
            <div class="col-span-2"><span class="text-gray-400 block mb-0.5">配送方式</span><span class="text-gray-700">{{ getDeliveryText(selectedOrder) }}</span></div>
            <div class="col-span-2"><span class="text-gray-400 block mb-0.5">收货地址</span><span class="text-gray-700">{{ selectedOrder.address }}</span></div>
            <div v-if="selectedOrder.remark" class="col-span-2"><span class="text-gray-400 block mb-0.5">订单备注</span><span class="text-gray-700">{{ selectedOrder.remark }}</span></div>
            <div v-if="selectedOrder.cancelReason" class="col-span-2"><span class="text-gray-400 block mb-0.5">取消原因</span><span class="text-gray-700">{{ selectedOrder.cancelReason }}</span></div>
          </div>
        </div>

        <div>
          <h4 class="font-bold text-gray-800 border-b border-gray-100 pb-2 mb-3">商品明细</h4>
          <el-table :data="selectedOrder.orderDetails" size="small" border style="width: 100%">
            <el-table-column prop="name" label="商品" />
            <el-table-column label="规格" min-width="100">
              <template #default="{ row }">{{ formatFlavor(row.dishFlavor) }}</template>
            </el-table-column>
            <el-table-column prop="number" label="数量" width="60" align="center">
              <template #default="{ row }">×{{ row.number }}</template>
            </el-table-column>
            <el-table-column label="单价" width="80" align="right">
              <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <div class="bg-gray-50 rounded-lg p-4 border border-gray-100 flex justify-between font-bold text-base text-gray-800">
          <span>实付金额</span>
          <span class="text-red-500">¥{{ formatAmount(selectedOrder.amount) }}</span>
        </div>

        <div class="flex justify-end gap-2 pt-4">
          <el-button v-if="selectedOrder.status === 2" type="primary" @click="acceptOrder(selectedOrder.id)">立即接单</el-button>
          <el-button @click="drawerVisible = false">关闭</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  acceptAdminOrder,
  cancelAdminOrder,
  getAdminOrderDetail,
  getAdminOrderList,
  type AdminOrder
} from '@/api/order'

const route = useRoute()
const orders = ref<AdminOrder[]>([])
const loading = ref(false)
const drawerVisible = ref(false)
const selectedOrder = ref<AdminOrder | null>(null)
const filterForm = reactive({
  number: '',
  phone: '',
  status: undefined as number | undefined
})

const statusTextMap: Record<number, string> = {
  1: '待支付',
  2: '待接单',
  3: '制作中',
  4: '配送中',
  5: '已完成',
  6: '已取消',
  7: '退款中'
}

const statusTagTypeMap: Record<number, 'danger' | 'primary' | 'warning' | 'success' | 'info'> = {
  1: 'info',
  2: 'danger',
  3: 'primary',
  4: 'warning',
  5: 'success',
  6: 'info',
  7: 'info'
}

const formatAmount = (amount: number) => amount.toFixed(2)

const formatDateTime = (dateTime: string) => dateTime.replace('T', ' ').slice(0, 19)

const formatFlavor = (dishFlavor: string | null) => {
  if (dishFlavor === null) {
    return ''
  }
  return Object.values(JSON.parse(dishFlavor)).join(' / ')
}

const getStatusText = (status: number) => statusTextMap[status]

const getStatusTagType = (status: number) => statusTagTypeMap[status]

const getPayMethodText = (payMethod: number) => payMethod === 1 ? '微信支付' : '支付宝支付'

const getDeliveryText = (order: AdminOrder) => order.deliveryStatus === 1
  ? '立即配送'
  : `预约配送：${formatDateTime(order.deliveryTime as string)}`

const loadOrders = async () => {
  loading.value = true
  try {
    orders.value = await getAdminOrderList(filterForm)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  filterForm.number = ''
  filterForm.phone = ''
  filterForm.status = undefined
  loadOrders()
}

const viewDetail = async (id: number) => {
  selectedOrder.value = await getAdminOrderDetail(id)
  drawerVisible.value = true
}

const refreshSelectedOrder = async () => {
  if (selectedOrder.value) {
    selectedOrder.value = await getAdminOrderDetail(selectedOrder.value.id)
  }
}

const acceptOrder = async (id: number) => {
  await acceptAdminOrder(id)
  ElMessage.success('接单成功，订单已进入制作中')
  await loadOrders()
  await refreshSelectedOrder()
}

const cancelOrder = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认取消该订单吗？', '提示', {
      confirmButtonText: '确定取消',
      cancelButtonText: '暂不取消',
      type: 'warning'
    })
  } catch {
    // 用户关闭确认框或点击“暂不取消”时，不发起取消订单请求。
    return
  }

  await cancelAdminOrder(id)
  ElMessage.success('订单已取消')
  await loadOrders()
  await refreshSelectedOrder()
}

onMounted(async () => {
  await loadOrders()
  const orderId = Number(route.query.id)
  if (orderId) {
    await viewDetail(orderId)
  }
})
</script>
