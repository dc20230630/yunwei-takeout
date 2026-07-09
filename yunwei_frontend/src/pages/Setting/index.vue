<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-bold text-gray-800">系统设置</h2>
      <p class="text-gray-400 text-sm mt-1">门店基础配置及运营规则设置</p>
    </div>

    <!-- 设置容器卡片 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-6 min-h-[500px]">
      <el-tabs v-model="activeTab" tab-position="left" class="h-full">
        <!-- 门店设置 -->
        <el-tab-pane label="门店设置" name="store" class="px-6">
          <h3 class="text-lg font-bold text-gray-800 mb-6">门店设置</h3>
          <el-form :model="storeSettings" label-position="top" style="max-width: 600px">
            <div class="grid grid-cols-2 gap-4">
              <el-form-item label="门店名称" class="col-span-2">
                <el-input v-model="storeSettings.storeName" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="storeSettings.phone" />
              </el-form-item>
              <el-form-item label="营业时间">
                <el-input v-model="storeSettings.hours" placeholder="例如: 09:00 - 22:30" />
              </el-form-item>
              <el-form-item label="门店地址" class="col-span-2">
                <el-input v-model="storeSettings.address" />
              </el-form-item>
              <el-form-item label="门店公告" class="col-span-2">
                <el-input v-model="storeSettings.notice" type="textarea" :rows="3" />
              </el-form-item>
            </div>
            <el-form-item class="mt-6">
              <el-button type="primary" @click="saveStoreSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 配送设置 -->
        <el-tab-pane label="配送设置" name="delivery" class="px-6">
          <h3 class="text-lg font-bold text-gray-800 mb-6">配送设置</h3>
          <el-form :model="deliverySettings" label-position="top" style="max-width: 600px">
            <div class="grid grid-cols-2 gap-4">
              <el-form-item label="配送范围 (公里)">
                <el-input-number v-model="deliverySettings.range" :min="0" :precision="1" class="w-full" />
              </el-form-item>
              <el-form-item label="配送费 (元)">
                <el-input-number v-model="deliverySettings.fee" :min="0" :precision="2" class="w-full" />
              </el-form-item>
              <el-form-item label="起送金额 (元)">
                <el-input-number v-model="deliverySettings.min" :min="0" :precision="2" class="w-full" />
              </el-form-item>
              <el-form-item label="预计配送时间 (分钟)">
                <el-input-number v-model="deliverySettings.estimate" :min="0" :precision="0" class="w-full" />
              </el-form-item>
            </div>
            <el-form-item class="mt-6">
              <el-button type="primary" @click="saveDeliverySettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 支付设置 -->
        <el-tab-pane label="支付设置" name="payment" class="px-6">
          <h3 class="text-lg font-bold text-gray-800 mb-6">支付设置</h3>
          <el-form style="max-width: 500px">
            <div class="space-y-6">
              <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex flex-col">
                  <span class="font-semibold text-gray-700">微信支付</span>
                  <span class="text-xs text-gray-400 mt-1">开启后支持微信扫码及微信小程序内支付</span>
                </div>
                <el-switch v-model="paymentSettings.wechat" />
              </div>

              <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex flex-col">
                  <span class="font-semibold text-gray-700">支付宝</span>
                  <span class="text-xs text-gray-400 mt-1">开启后支持支付宝扫码及手机网页沙箱支付</span>
                </div>
                <el-switch v-model="paymentSettings.alipay" />
              </div>

              <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex flex-col">
                  <span class="font-semibold text-gray-700">云闪付</span>
                  <span class="text-xs text-gray-400 mt-1">中国银联支付通道（需配置商户证书）</span>
                </div>
                <el-switch v-model="paymentSettings.union" />
              </div>
            </div>
            <el-form-item class="mt-8">
              <el-button type="primary" @click="savePaymentSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 通知设置 -->
        <el-tab-pane label="通知设置" name="notify" class="px-6">
          <h3 class="text-lg font-bold text-gray-800 mb-6">通知设置</h3>
          <el-form style="max-width: 500px">
            <div class="space-y-6">
              <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex flex-col">
                  <span class="font-semibold text-gray-700">新订单语音提醒</span>
                  <span class="text-xs text-gray-400 mt-1">收到新外卖订单时，播报“您有新的外卖订单，请注意查收”</span>
                </div>
                <el-switch v-model="notifySettings.newOrder" />
              </div>

              <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex flex-col">
                  <span class="font-semibold text-gray-700">退款及取消提醒</span>
                  <span class="text-xs text-gray-400 mt-1">当用户申请退款或异常取消时触发强提醒</span>
                </div>
                <el-switch v-model="notifySettings.refund" />
              </div>

              <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
                <div class="flex flex-col">
                  <span class="font-semibold text-gray-700">系统更新广播</span>
                  <span class="text-xs text-gray-400 mt-1">接收云味平台的重要公告与功能更新升级推送</span>
                </div>
                <el-switch v-model="notifySettings.system" />
              </div>
            </div>
            <el-form-item class="mt-8">
              <el-button type="primary" @click="saveNotifySettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

const activeTab = ref('store');

// 门店设置
const storeSettings = reactive({
  storeName: '云味小馆（望京SOHO店）',
  phone: '010-84728888',
  address: '北京市朝阳区望京 SOHO 塔2 三层 2301号',
  hours: '09:00 - 22:30',
  notice: '云端美味，品质传承！主打高品质健康外卖。'
});

// 配送设置
const deliverySettings = reactive({
  range: 5.0,
  fee: 5.00,
  min: 20.00,
  estimate: 40
});

// 支付设置
const paymentSettings = reactive({
  wechat: true,
  alipay: true,
  union: false
});

// 通知设置
const notifySettings = reactive({
  newOrder: true,
  refund: true,
  system: false
});

const saveStoreSettings = () => {
  ElMessage.success('门店设置已成功保存！');
};

const saveDeliverySettings = () => {
  ElMessage.success('配送规则及服务参数已更新！');
};

const savePaymentSettings = () => {
  ElMessage.success('支付渠道开关状态配置已保存！');
};

const saveNotifySettings = () => {
  ElMessage.success('消息提醒及语音推送偏好设置已更新！');
};
</script>

<style scoped>
:deep(.el-tabs__item) {
  height: 50px;
  font-size: 14px;
}
</style>
