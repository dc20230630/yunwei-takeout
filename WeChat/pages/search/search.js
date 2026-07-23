// pages/search/search.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    keyword: '',
    isSearching: false,
    history: [],
    hotSearches: ['黄焖鸡', '招牌套餐', '炸鸡', '柠檬茶', '盖饭'],
    results: []
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight,
      history: app.globalData.searchHistory || []
    });
  },

  onShow() {
    this.setData({
      history: app.globalData.searchHistory || []
    });
  },

  onInput(e) {
    const val = e.detail.value;
    this.setData({
      keyword: val
    });

    // 如果把搜索输入框全部删空，返回初始页面状态
    if (!val.trim()) {
      this.setData({
        isSearching: false,
        results: []
      });
    }
  },

  clearInput() {
    this.setData({
      keyword: '',
      isSearching: false,
      results: []
    });
  },

  onSearchConfirm(e) {
    const val = e.detail.value.trim();
    this.runSearch(val);
  },

  clickChip(e) {
    const val = e.currentTarget.dataset.val;
    this.setData({
      keyword: val
    });
    this.runSearch(val);
  },

  runSearch(keyword) {
    if (!keyword) return;

    // 1. 更新搜索历史队列 (去重，保持最多 10 条)
    let history = this.data.history;
    history = history.filter(item => item !== keyword);
    history.unshift(keyword);
    if (history.length > 10) {
      history = history.slice(0, 10);
    }

    app.globalData.searchHistory = history;
    
    // 2. 执行前端数据检索
    const foods = app.globalData.foods || [];
    const matched = foods.filter(item => 
      item.name.indexOf(keyword) !== -1 || 
      item.desc.indexOf(keyword) !== -1 ||
      item.category.indexOf(keyword) !== -1
    );

    this.setData({
      history,
      results: matched,
      isSearching: true
    });
  },

  clearHistory() {
    app.globalData.searchHistory = [];
    this.setData({
      history: []
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/itemDetail/itemDetail?id=${id}`
    });
  },

  goBack() {
    wx.navigateBack().catch(() => {
      wx.switchTab({
        url: '/pages/home/home'
      });
    });
  }
});
