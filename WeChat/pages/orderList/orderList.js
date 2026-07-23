// pages/orderList/orderList.js
const app = getApp();

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

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  onShow() {
    // 每次显示页面重新拉取全局订单数据
    this.loadOrders();
  },

  loadOrders() {
    const orders = app.globalData.orders || [];
    this.setData({
      orders: orders
    }, () => {
      this.filterOrdersByTab();
    });
  },

  filterOrdersByTab() {
    const { orders, selectedTab } = this.data;
    if (selectedTab === '全部') {
      this.setData({ filteredOrders: orders });
    } else {
      const filtered = orders.filter(order => order.status === selectedTab);
      this.setData({ filteredOrders: filtered });
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      selectedTab: tab
    }, () => {
      this.filterOrdersByTab();
    });
  },

  // 催单
  urgeOrder(e) {
    wx.showToast({
      title: '已催促商家加紧处理',
      icon: 'success',
      duration: 1500
    });
  },

  // 支付订单 (针对待付款订单)
  payOrder(e) {
    const id = e.currentTarget.dataset.id;
    const orders = this.data.orders;
    const order = orders.find(x => x.id === id);
    if (order) {
      order.status = '待接单'; // 改变状态
      app.globalData.orders = orders;
      
      wx.showLoading({ title: '支付中...' });
      setTimeout(() => {
        wx.hideLoading();
        wx.navigateTo({
          url: `/pages/paySuccess/paySuccess?id=${id}&amount=${order.total}`
        });
      }, 800);
    }
  },

  // 再来一单 (Reorder)
  reorder(e) {
    const order = e.currentTarget.dataset.order;
    // 1. 清空当前购物车
    app.clearCart();
    
    // 2. 将订单中的全部商品塞入全局购物车
    order.items.forEach(item => {
      // 在本地 foods 库中查找到对应完整信息补全属性 (主要是为了获取图片等描述)
      const originFood = app.globalData.foods.find(f => f.id === item.id) || {};
      const newFood = {
        ...originFood,
        price: item.price,
        id: item.id,
        name: item.name
      };
      app.addToCart(newFood, item.specs, item.qty);
    });

    wx.showToast({
      title: '已添加进购物车',
      icon: 'success',
      duration: 1000
    });

    // 3. 延迟跳转到点餐页进行结算或修改
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/menu/menu'
      }).catch(() => {
        wx.navigateTo({
          url: '/pages/menu/menu'
        });
      });
    }, 1000);
  },

  // 页面跳转
  goToOrderDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/orderDetail/orderDetail?id=${id}`
    });
  },

  goToReview(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/review/review?id=${id}`
    });
  },

  goToMenu() {
    wx.switchTab({
      url: '/pages/menu/menu'
    }).catch(() => {
      wx.navigateTo({
        url: '/pages/menu/menu'
      });
    });
  },

  // 阻止冒泡
  preventBubble() {}
});
