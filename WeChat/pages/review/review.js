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
      5: '非常满意，菜品体验很好'
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
    this.setData({ score: e.currentTarget.dataset.val });
  },

  onInput(e) {
    this.setData({ content: e.detail.value });
  },

  onAnonymousChange(e) {
    this.setData({ isAnonymous: e.detail.value });
  },

  submitReview() {
    wx.showToast({
      title: '评价功能暂未接入',
      icon: 'none'
    });
  },

  goBack() {
    wx.navigateBack();
  }
});
