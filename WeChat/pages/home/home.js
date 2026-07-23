// pages/home/home.js
const app = getApp();
const { loadProductData } = require('../../utils/product');

Page({
  data: {
    // 顶部栏尺寸几何参数
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    // 数据源
    categories: [],
    foods: [],
    recommendFoods: []
  },

  onLoad(options) {
    // 读取全局高度用于自定义顶部标题栏
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  onShow() {
    // 每次显示页面时，重新加载数据（如同步购物车或最新分类数据）
    this.loadHomeData();
  },

  async loadHomeData() {
    try {
      const { categories, foods } = await loadProductData();

      // 详情、搜索和点餐页都从全局数据读取当前商品
      app.globalData.categories = categories;
      app.globalData.foods = foods;

      this.setData({
        categories,
        foods,
        // 今日推荐暂时按后端返回顺序展示前三个商品
        recommendFoods: foods.slice(0, 3)
      });
    } catch (error) {
      console.error('加载商品浏览数据失败：', error);
      wx.showToast({
        title: '加载商品失败，请重试',
        icon: 'none'
      });
    }
  },

  // 快捷添加到购物车
  quickAdd(e) {
    const item = e.currentTarget.dataset.item;
    if (item.hasSpecs) {
      // 含有规格的菜品，跳转到点餐页进行选配
      wx.navigateTo({
        url: `/pages/menu/menu?categoryId=1&foodId=${item.id}&openSpec=true`
      });
    } else {
      // 无规格菜品直接调用全局加车
      app.addToCart(item, null, 1);
      wx.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1000
      });
    }
  },

  // 导航事件
  goToAddresses() {
    wx.navigateTo({
      url: '/pages/addresses/addresses'
    });
  },

  goToSearch() {
    wx.navigateTo({
      url: '/pages/search/search'
    });
  },

  goToMenu() {
    wx.switchTab({
      url: '/pages/menu/menu'
    }).catch(() => {
      // 如果不是 tabbar 页面，降级使用 navigateTo
      wx.navigateTo({
        url: '/pages/menu/menu'
      });
    });
  },

  selectCategory(e) {
    const categoryName = e.currentTarget.dataset.name;
    // 将选择的分类名称暂存到全局，方便点单页切换
    app.globalData.activeCategory = categoryName;
    
    wx.navigateTo({
      url: `/pages/menu/menu?category=${categoryName}`
    });
  },

  goToFoodDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/itemDetail/itemDetail?id=${id}`
    });
  }
});
