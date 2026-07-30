// pages/orderDetail/orderDetail.js
const app = getApp();
const { request } = require('../../utils/request');

const statusTextMap = {
  1: '待支付',
  2: '待接单',
  3: '制作中',
  4: '配送中',
  5: '已完成',
  6: '已取消',
  7: '退款中'
};

const statusDescriptionMap = {
  1: '请尽快完成支付，订单超时会自动取消。',
  2: '商家正在处理您的订单。',
  3: '商家正在制作餐品，请耐心等待。',
  4: '订单正在配送中。',
  5: '订单已完成，感谢您的购买。',
  6: '订单已取消。',
  7: '订单正在退款中。'
};

const formatDateTime = (dateTime) => dateTime.replace('T', ' ').slice(0, 16);

const formatFlavor = (dishFlavor) => {
  if (dishFlavor === null) {
    return '';
  }
  return Object.values(JSON.parse(dishFlavor)).join(' / ');
};

const toPageOrder = (order) => ({
  id: order.id,
  number: order.number,
  status: statusTextMap[order.status],
  statusDescription: statusDescriptionMap[order.status],
  total: order.amount,
  consignee: order.consignee,
  phone: order.phone,
  address: order.address,
  remark: order.remark,
  orderTime: formatDateTime(order.orderTime),
  payMethod: order.payMethod,
  deliveryText: order.deliveryStatus === 1
    ? '立即配送'
    : `预约配送：${formatDateTime(order.deliveryTime)}`,
  cancelReason: order.cancelReason,
  items: order.orderDetails.map((detail) => ({
    id: detail.id,
    name: detail.name,
    image: detail.image,
    specs: formatFlavor(detail.dishFlavor),
    amount: detail.amount,
    number: detail.number
  }))
});

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    order: null
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
    this.loadOrder(options.id);
  },

  async loadOrder(id) {
    wx.showLoading({ title: '加载中...' });
    try {
      const order = await request({
        url: `/user/order/${id}`,
        method: 'GET'
      });
      this.setData({ order: toPageOrder(order) });
    } catch (error) {
      wx.showToast({
        title: error.message,
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  copyOrderId() {
    wx.setClipboardData({ data: this.data.order.number });
  },

  goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
      wx.navigateBack();
      return;
    }
    wx.switchTab({ url: '/pages/orderList/orderList' });
  }
});
