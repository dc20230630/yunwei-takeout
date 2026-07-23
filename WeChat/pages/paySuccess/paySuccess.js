// pages/paySuccess/paySuccess.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    orderId: '',
    amount: '0.00'
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight,
      orderId: options.id || '',
      amount: options.amount || '0.00'
    });
  },

  // 去往订单详情页
  goToOrderDetail() {
    wx.redirectTo({
      url: `/pages/orderDetail/orderDetail?id=${this.data.orderId}`
    });
  },

  // 返回首页 Tab
  goToHome() {
    wx.switchTab({
      url: '/pages/home/home'
    }).catch(() => {
      wx.navigateTo({
        url: '/pages/home/home'
      });
    });
  }
});
