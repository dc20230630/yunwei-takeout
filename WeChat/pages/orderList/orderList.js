// pages/orderList/orderList.js
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

const formatDateTime = (dateTime) => dateTime.replace('T', ' ').slice(0, 16);

const toPageOrder = (order) => {
  const items = order.orderDetails.map((detail) => ({
    id: detail.id,
    dishId: detail.dishId,
    setmealId: detail.setmealId,
    name: detail.name,
    image: detail.image,
    price: detail.amount,
    qty: detail.number,
    dishFlavor: detail.dishFlavor
  }));

  return {
    id: order.id,
    number: order.number,
    status: statusTextMap[order.status],
    total: order.amount,
    time: formatDateTime(order.orderTime),
    totalQuantity: items.reduce((sum, item) => sum + item.qty, 0),
    items
  };
};

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    tabs: ['全部', '待接单', '制作中', '配送中', '已完成'],
    selectedTab: '全部',
    orders: [],
    filteredOrders: []
  },

  onLoad() {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  onShow() {
    this.loadOrders();
  },

  async loadOrders() {
    try {
      const orderList = await request({
        url: '/user/order/list',
        method: 'GET'
      });
      this.setData({ orders: orderList.map(toPageOrder) }, () => {
        this.filterOrdersByTab();
      });
    } catch (error) {
      wx.showToast({
        title: error.message,
        icon: 'none'
      });
    }
  },

  filterOrdersByTab() {
    const { orders, selectedTab } = this.data;
    const filteredOrders = selectedTab === '全部'
      ? orders
      : orders.filter((order) => order.status === selectedTab);
    this.setData({ filteredOrders });
  },

  switchTab(e) {
    this.setData({ selectedTab: e.currentTarget.dataset.tab }, () => {
      this.filterOrdersByTab();
    });
  },

  async payOrder(e) {
    const { id, number, amount } = e.currentTarget.dataset;
    wx.showLoading({ title: '支付中...' });
    try {
      await request({
        url: '/user/order/mock-payment',
        method: 'PUT',
        data: { orderNumber: number }
      });
      wx.redirectTo({
        url: `/pages/paySuccess/paySuccess?id=${id}&amount=${amount}`
      });
    } catch (error) {
      wx.showToast({
        title: error.message,
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  },

  async reorder(e) {
    const { items } = e.currentTarget.dataset.order;
    await app.clearCart();

    for (const item of items) {
      const isDish = item.dishId !== null;
      const food = {
        id: isDish ? item.dishId : item.setmealId,
        productType: isDish ? 'dish' : 'setmeal',
        name: item.name,
        image: item.image,
        price: item.price
      };
      const specs = item.dishFlavor === null ? null : JSON.parse(item.dishFlavor);
      await app.addToCart(food, specs, item.qty);
    }

    wx.showToast({
      title: '已添加进购物车',
      icon: 'success',
      duration: 1000
    });
    setTimeout(() => {
      wx.switchTab({ url: '/pages/menu/menu' });
    }, 1000);
  },

  goToOrderDetail(e) {
    wx.navigateTo({
      url: `/pages/orderDetail/orderDetail?id=${e.currentTarget.dataset.id}`
    });
  },

  goToMenu() {
    wx.switchTab({ url: '/pages/menu/menu' });
  },

  preventBubble() {}
});
