// pages/orderDetail/orderDetail.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    order: null
  },

  onLoad(options) {
    const orderId = options.id || '';
    const order = app.globalData.orders.find(o => o.id === orderId);

    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight,
      order: order
    });
  },

  // 复制订单号到剪贴板
  copyOrderId() {
    if (!this.data.order) return;
    wx.setClipboardData({
      data: this.data.order.id,
      success: () => {
        // 微信默认会有Toast提示，此处可做提示修饰或略过
      }
    });
  },

  // 呼叫骑手
  callRider() {
    wx.showModal({
      title: '呼叫骑手',
      content: '是否给配送员王小二拨打电话？',
      confirmText: '呼叫',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '13800138000'
          });
        }
      }
    });
  },

  goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
      wx.navigateBack();
    } else {
      // 容错：直接跳转到订单列表 Tab 页
      wx.switchTab({
        url: '/pages/orderList/orderList'
      }).catch(() => {
        wx.navigateTo({
          url: '/pages/orderList/orderList'
        });
      });
    }
  }
});
