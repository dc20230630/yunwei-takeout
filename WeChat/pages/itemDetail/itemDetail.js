// pages/itemDetail/itemDetail.js
const app = getApp();
const { request } = require('../../utils/request');

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    food: null,
    setmealDishes: [],
    cartCount: 0,
    foodInCartQty: 0, // 当前商品在购物车内的数量小计 (针对无规格商品有用)

    // 规格选择
    showSpecsDialog: false,
    flavorGroups: []
  },

  async onLoad(options) {
    const foodId = parseInt(options.id);
    const food = app.globalData.foods.find(f => f.id === foodId);
    
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight,
      food: food
    });

    if (food.productType === 'setmeal') {
      try {
        const setmealDishes = await request({
          url: `/user/setmeal/dish/${food.id}`,
          method: 'GET'
        });

        this.setData({ setmealDishes });
      } catch (error) {
        console.error('加载套餐菜品失败：', error);
        wx.showToast({
          title: '加载套餐详情失败',
          icon: 'none'
        });
      }
    }

    // 注册购物车更新事件
    app.registerCartCallback(this.onCartUpdated);
  },

  async onShow() {
    // 从数据库同步后，再计算当前商品在购物车中的数量
    await app.loadCart();
    // 显示页面时刷新购物车状态
    this.onCartUpdated(app.globalData.cart);
  },

  onUnload() {
    // 销毁时注销监听器
    app.unregisterCartCallback(this.onCartUpdated);
  },

  onCartUpdated(cart) {
    const count = app.getCartCount();
    
    // 计算当前菜品在购物车里的总量 (包括不同规格)
    let qty = 0;
    if (this.data.food) {
      const matchedItems = cart.filter(item => item.id === this.data.food.id);
      qty = matchedItems.reduce((sum, item) => sum + item.qty, 0);
    }

    this.setData({
      cartCount: count,
      foodInCartQty: qty
    });
  },

  // 快捷加减车（针对无规格菜品）
  async increaseQtyDirect() {
    await app.addToCart(this.data.food, null, 1);
  },

  async decreaseQtyDirect() {
    const { cart, food } = this.data;
    // 查找购物车中该商品的无规格项
    const cartItem = app.globalData.cart.find(x => x.id === food.id && x.specs === null);
    if (cartItem) {
      await app.changeCartQty(cartItem.cartId, -1);
    }
  },

  // --- 规格选择弹层 ---
  openSpecs() {
    const flavorGroups = this.data.food.flavors.map(flavor => ({
      name: flavor.name,
      options: JSON.parse(flavor.value).map((value, index) => ({
        value,
        selected: index === 0
      }))
    }));

    this.setData({
      showSpecsDialog: true,
      flavorGroups
    });
  },

  closeSpecs() {
    this.setData({
      showSpecsDialog: false,
      flavorGroups: []
    });
  },

  selectFlavor(e) {
    const { name, value } = e.currentTarget.dataset;
    const flavorGroups = this.data.flavorGroups.map(group => {
      if (group.name !== name) {
        return group;
      }

      return {
        ...group,
        options: group.options.map(option => ({
          ...option,
          selected: option.value === value
        }))
      };
    });

    this.setData({ flavorGroups });
  },

  async addSpecFoodToCart() {
    const { food, flavorGroups } = this.data;
    const specs = {};

    flavorGroups.forEach(group => {
      const selectedOption = group.options.find(option => option.selected);
      specs[group.name] = selectedOption.value;
    });

    await app.addToCart(food, specs, 1);
    this.closeSpecs();
    wx.showToast({
      title: '已加入购物车',
      icon: 'success'
    });
  },

  goToMenu() {
    // 跳转到 Tab 菜单页，微信小程序中必须用 switchTab
    wx.switchTab({
      url: '/pages/menu/menu'
    }).catch(() => {
      wx.navigateTo({
        url: '/pages/menu/menu'
      });
    });
  },

  goBack() {
    wx.navigateBack().catch(() => {
      wx.switchTab({
        url: '/pages/home/home'
      });
    });
  },

  preventBubble() {}
});
