// pages/review/review.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,

    orderId: '',
    score: 5,
    content: '',
    isAnonymous: false,
    
    scoreTexts: {
      1: '非常差，菜品体验极其糟糕',
      2: '差，菜品口感或配送有待改进',
      3: '一般，味道中规中矩',
      4: '满意，味道不错，包装完好',
      5: '非常满意，味道极佳，强力推荐！'
    }
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight,
      orderId: options.id || ''
    });
  },

  selectStar(e) {
    this.setData({
      score: e.currentTarget.dataset.val
    });
  },

  onInput(e) {
    this.setData({
      content: e.detail.value
    });
  },

  onAnonymousChange(e) {
    this.setData({
      isAnonymous: e.detail.value
    });
  },

  submitReview() {
    // 模拟提交成功
    wx.showLoading({
      title: '正在提交评价...'
    });

    setTimeout(() => {
      wx.hideLoading();

      // 在全局订单缓存中给该订单打上“已评价”标签，改变按钮文案或屏蔽评价按钮
      const orders = app.globalData.orders || [];
      const order = orders.find(o => o.id === this.data.orderId);
      if (order) {
        order.isReviewed = true; // 打上标识
        app.globalData.orders = orders;
      }

      wx.showToast({
        title: '评价成功，感谢反馈',
        icon: 'success'
      });

      // 延迟返回订单列表
      setTimeout(() => {
        wx.navigateBack();
      }, 1000);
    }, 800);
  },

  goBack() {
    wx.navigateBack();
  }
});
