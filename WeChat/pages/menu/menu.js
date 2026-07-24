// pages/menu/menu.js
const app = getApp();
const { loadProductData } = require('../../utils/product');

Page({
  data: {
    // 布局尺寸
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,

    categories: [],
    foods: [],
    filteredFoods: [],
    activeCategory: '热销',

    // 购物车浮层控制
    showCartDrawer: false,
    cart: [],
    cartCount: 0,
    cartTotal: 0,
    minCheckoutAmount: 20, // 20元起送

    // 各个菜品已加入购物车的数量映射 (key为food.id, value为已加数量小计)
    foodCartCounts: {},

    // 多规格弹窗控制
    showSpecsDialog: false,
    specFood: null,
    flavorGroups: []
  },

  async onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });

    try {
      const { categories, foods } = await loadProductData();

      app.globalData.categories = categories;
      app.globalData.foods = foods;

      this.setData({
        categories,
        foods,
        activeCategory: options.category || categories[0].name
      });

      this.updateFilteredFoods();
    } catch (error) {
      console.error('加载点餐数据失败：', error);
      wx.showToast({
        title: '加载商品失败，请重试',
        icon: 'none'
      });
    }

    // 注册购物车更新监听
    app.registerCartCallback(this.onCartUpdated);
  },

  async onShow() {
    // 重新进入菜单时读取数据库，避免本地缓存不是最新状态
    await app.loadCart();

    // 每次显示页面更新右侧菜品显示并刷新购物车
    this.updateFilteredFoods();
    this.onCartUpdated(app.globalData.cart);

    // 处理从首页快捷购买带规格商品等特殊路由
    const curPages = getCurrentPages();
    const curPage = curPages[curPages.length - 1];
    const options = curPage.options;
    if (options && options.openSpec === 'true' && options.foodId) {
      const food = this.data.foods.find(f => f.id === parseInt(options.foodId));
      if (food) {
        this.openSpecDialog(food);
      }
      // 清理 options 参数，防止再次 onShow 重复弹窗
      curPage.options.openSpec = undefined;
    }
  },

  onUnload() {
    // 销毁页面时注销购物车监听器，防止内存积压
    app.unregisterCartCallback(this.onCartUpdated);
  },

  // 购物车更新的回调
  onCartUpdated(cart) {
    // 计算购物车总数、总价
    const count = app.getCartCount();
    const total = app.getCartTotal();

    // 计算各个菜品在购物车中添加的总量 (合并多规格的情况)
    const counts = {};
    cart.forEach(item => {
      counts[item.id] = (counts[item.id] || 0) + item.qty;
    });

    this.setData({
      cart: cart,
      cartCount: count,
      cartTotal: parseFloat(total.toFixed(2)),
      foodCartCounts: counts
    });

    // 如果购物车被清空了，自动合上抽屉
    if (count === 0 && this.data.showCartDrawer) {
      this.setData({ showCartDrawer: false });
    }
  },

  updateFilteredFoods() {
    const { foods, activeCategory } = this.data;
    const filtered = foods.filter(item => item.category === activeCategory);
    this.setData({
      filteredFoods: filtered
    });
  },

  switchCategory(e) {
    const name = e.currentTarget.dataset.name;
    this.setData({
      activeCategory: name
    }, () => {
      this.updateFilteredFoods();
    });
  },

  // --- 购物车交互 (无规格菜品直接加减) ---
  async increaseQtyDirect(e) {
    const food = e.currentTarget.dataset.food;
    await app.addToCart(food, null, 1);
  },

  async decreaseQtyDirect(e) {
    const food = e.currentTarget.dataset.food;
    // 查找购物车内该无规格商品的项
    const cartItem = this.data.cart.find(x => x.id === food.id && x.specs === null);
    if (cartItem) {
      await app.changeCartQty(cartItem.cartId, -1);
    }
  },

  // --- 购物车详情抽屉操作 ---
  toggleCartDrawer() {
    if (this.data.cartCount > 0) {
      this.setData({
        showCartDrawer: !this.data.showCartDrawer
      });
    }
  },

  clearAllCart() {
    wx.showModal({
      title: '清空确认',
      content: '确定要清空购物车吗？',
      success: async (res) => {
        if (res.confirm) {
          await app.clearCart();
          wx.showToast({
            title: '购物车已清空',
            icon: 'success'
          });
        }
      }
    });
  },

  async increaseCartQty(e) {
    const cartId = e.currentTarget.dataset.cartid;
    await app.changeCartQty(cartId, 1);
  },

  async decreaseCartQty(e) {
    const cartId = e.currentTarget.dataset.cartid;
    await app.changeCartQty(cartId, -1);
  },

  // --- 多规格选择逻辑 ---
  openSpecs(e) {
    const food = e.currentTarget.dataset.food;

    this.openSpecDialog(food);
  },

  // 将后端 dish_flavor.value 中的 JSON 数组转换为可选择的口味项
  openSpecDialog(food) {
    const flavorGroups = food.flavors.map(flavor => {
      // 忌口、加料、配料等分组支持多选
      const isMulti = flavor.name.includes('忌口') || flavor.name.includes('加料') || flavor.name.includes('配料');
      return {
        name: flavor.name,
        isMulti: isMulti,
        options: JSON.parse(flavor.value).map((value, index) => ({
          value,
          selected: isMulti ? false : index === 0 // 多选组默认都不选，单选组默认选第1项
        }))
      };
    });

    this.setData({
      specFood: food,
      showSpecsDialog: true,
      flavorGroups
    });
  },

  closeSpecs() {
    this.setData({
      showSpecsDialog: false,
      specFood: null,
      flavorGroups: []
    });
  },

  selectFlavor(e) {
    const { name, value } = e.currentTarget.dataset;
    const flavorGroups = this.data.flavorGroups.map(group => {
      if (group.name !== name) {
        return group;
      }

      if (group.isMulti) {
        // 多选分组：反转当前点击选项的选中状态
        return {
          ...group,
          options: group.options.map(option => ({
            ...option,
            selected: option.value === value ? !option.selected : option.selected
          }))
        };
      } else {
        // 单选分组：切换选中项，其他设为 false
        return {
          ...group,
          options: group.options.map(option => ({
            ...option,
            selected: option.value === value
          }))
        };
      }
    });

    this.setData({ flavorGroups });
  },

  async addSpecFoodToCart() {
    const { specFood, flavorGroups } = this.data;
    const specs = {};

    flavorGroups.forEach(group => {
      const selectedOptions = group.options.filter(option => option.selected);
      if (selectedOptions.length > 0) {
        specs[group.name] = selectedOptions.map(o => o.value).join('、');
      }
    });

    await app.addToCart(specFood, Object.keys(specs).length > 0 ? specs : null, 1);
    this.closeSpecs();
  },

  // --- 结算与跳转 ---
  goToCheckout() {
    // 1. 拦截未登录情况
    const userInfo = app.globalData.userInfo;
    if (!userInfo) {
      wx.showToast({
        title: '请先登录账号',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        });
      }, 1000);
      return;
    }

    // 2. 正常跳转
    wx.navigateTo({
      url: '/pages/checkout/checkout'
    });
  },

  goToFoodDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/itemDetail/itemDetail?id=${id}`
    });
  },

  goBack() {
    wx.navigateBack().catch(() => {
      // 容错返回首页 tab
      wx.switchTab({
        url: '/pages/home/home'
      });
    });
  },

  preventBubble() {}
});
