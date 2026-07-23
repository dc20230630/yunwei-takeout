// app.js
App({
  onLaunch() {
    // 1. 获取系统信息，用于计算自定义顶部导航栏高度
    const systemInfo = wx.getSystemInfoSync();
    
    // 获取胶囊按钮的尺寸位置信息
    let menuButtonInfo = { top: 0, height: 0 };
    try {
      menuButtonInfo = wx.getMenuButtonBoundingClientRect();
    } catch (e) {
      // 容错处理
      menuButtonInfo = {
        top: systemInfo.statusBarHeight + 4,
        height: 32
      };
    }
    
    // 状态栏高度
    const statusBarHeight = systemInfo.statusBarHeight;
    // 胶囊按钮上下边距算出导航条高度
    const navBarHeight = (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
    
    this.globalData.statusBarHeight = statusBarHeight;
    this.globalData.navBarHeight = navBarHeight;
    this.globalData.totalNavHeight = statusBarHeight + navBarHeight;
    
    // 2. 初始化从 LocalStorage 读取数据 (可选)
    const localCart = wx.getStorageSync('cart') || [];
    this.globalData.cart = localCart;

    // 恢复微信登录后缓存在本地的用户信息
    const localUserInfo = wx.getStorageSync('userInfo');
    if (localUserInfo) {
      this.globalData.userInfo = localUserInfo;
    }
  },

  // 全局业务操作方法
  addToCart(food, specs = null, qty = 1) {
    const cart = this.globalData.cart;
    const existing = cart.find(x => x.id === food.id && JSON.stringify(x.specs) === JSON.stringify(specs));
    
    if (existing) {
      existing.qty += qty;
    } else {
      cart.push({
        ...food,
        qty,
        specs,
        cartId: Date.now() + Math.random().toString(36).substr(2, 5)
      });
    }
    
    this.globalData.cart = cart;
    wx.setStorageSync('cart', cart);
    
    // 通知页面购物车已更新
    this.triggerCartCallbacks();
  },

  changeCartQty(cartId, delta) {
    let cart = this.globalData.cart;
    const item = cart.find(x => x.cartId === cartId);
    if (!item) return;
    
    item.qty += delta;
    if (item.qty <= 0) {
      cart = cart.filter(x => x.cartId !== cartId);
    }
    
    this.globalData.cart = cart;
    wx.setStorageSync('cart', cart);
    
    this.triggerCartCallbacks();
  },

  clearCart() {
    this.globalData.cart = [];
    wx.setStorageSync('cart', []);
    this.triggerCartCallbacks();
  },

  getCartTotal() {
    return this.globalData.cart.reduce((sum, item) => sum + item.price * item.qty, 0);
  },

  getCartCount() {
    return this.globalData.cart.reduce((sum, item) => sum + item.qty, 0);
  },

  // 购物车更新的回调机制 (非必选，但为了多页面购物车同步非常实用)
  cartCallbacks: [],
  registerCartCallback(cb) {
    this.cartCallbacks.push(cb);
  },
  unregisterCartCallback(cb) {
    this.cartCallbacks = this.cartCallbacks.filter(c => c !== cb);
  },
  triggerCartCallbacks() {
    this.cartCallbacks.forEach(cb => {
      if (typeof cb === 'function') {
        cb(this.globalData.cart);
      }
    });
  },

  globalData: {
    // 导航栏高度数据
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    // 登录态用户信息
    userInfo: null,
    
    // 购物车数据
    cart: [],
    
    // 历史搜索
    searchHistory: ['宫保鸡丁', '柠檬茶'],
    
    // 用户端商品浏览接口加载的真实数据
    categories: [],
    foods: [],
    
    // 地址数据
    addresses: [
      { id: 1, name: '张先生', phone: '13812345678', region: '广东省 深圳市 南山区', detail: '科技园大厦 A座 801', tag: '公司', isDefault: true },
      { id: 2, name: '张先生', phone: '13812345678', region: '广东省 深圳市 宝安区', detail: '幸福小区 3栋 2单元 501', tag: '家', isDefault: false }
    ],
    
    // 默认历史订单
    orders: [
      { id: 'YW123456789', time: '2026-07-20 12:30', status: '已完成', total: 37.9, items: [
        { id: 101, name: '招牌黄焖鸡米饭', price: 22.9, qty: 1, specs: { size: '大份', spice: '微辣' } },
        { id: 105, name: '手打渣男柠檬茶', price: 15.0, qty: 1, specs: { size: '标准', spice: '少冰' } }
      ]},
      { id: 'YW987654321', time: '2026-07-15 18:15', status: '已完成', total: 35.0, items: [
        { id: 102, name: '金汤酸菜鱼', price: 35.0, qty: 1, specs: null }
      ]}
    ]
  }
})
