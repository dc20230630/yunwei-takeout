// app.js
const { request } = require('./utils/request');

// 后端地址簿字段与页面展示字段不同，在全局层统一转换，页面不需要关心接口字段。
const toPageAddress = (addressBook) => ({
  id: addressBook.id,
  name: addressBook.consignee,
  gender: addressBook.sex,
  phone: addressBook.phone,
  regionArray: [addressBook.provinceName, addressBook.cityName, addressBook.districtName],
  region: `${addressBook.provinceName} ${addressBook.cityName} ${addressBook.districtName}`,
  detail: addressBook.detail,
  tag: addressBook.label,
  isDefault: addressBook.isDefault === 1
});

const toAddressBookPayload = (address) => {
  const [provinceName, cityName, districtName] = address.regionArray;
  return {
    id: address.id,
    consignee: address.name,
    sex: address.gender,
    phone: address.phone,
    provinceName,
    cityName,
    districtName,
    detail: address.detail,
    label: address.tag,
    isDefault: address.isDefault ? 1 : 0
  };
};

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
  async addToCart(food, specs = null, qty = 1) {
    const token = wx.getStorageSync('token');
    if(!token){
      wx.showToast({
        title:"请先登录",
        icon:"none"
      });
      return false;
    }

    const data = food.productType === 'setmeal'?{
      setmealId:food.id
    }:{
      dishId:food.id,
      //规格对象转成固定字符串，后端区分不同口味
      dishFlavor:specs ? JSON.stringify(specs):null
    };
    // 后端 add 接口一次只增加一份；“再来一单”可能包含多份，需要逐份提交
    for (let index = 0; index < qty; index++) {
      await request({
        url:'/user/shoppingCart/add',
        method:'POST',
        data
      });
    }
    //接口成功后，暂时仍更新本地数据，让现有页面立即刷新
    const cart = this.globalData.cart;
    const existing = cart.find((item) => item.id === food.id && JSON.stringify(item.specs) === JSON.stringify(specs));
    
    if (existing) {
      existing.qty += qty;
    } else {
      cart.push({
        ...food,
        qty,
        specs,
        cartId: Date.now() + Math.random().toString(36).substr(2, 7)
      });
    }
    
    this.globalData.cart = cart;
    wx.setStorageSync('cart', cart);
    
    // 通知页面购物车已更新
    this.triggerCartCallbacks();
    return true;
  },

  async loadCart(){
    const token = wx.getStorageSync('token');
    // 购物车查询接口要求登录，未登录不会发送请求
    if(!token){
      return;
    }
    const cartList = await request({
      url:'/user/shoppingCart/list',
      method:'GET'
    })

    //后端使用number amount dishFlavor
    //现在页面使用qty price specs
    const cart = cartList.map((cartItem)=>{
      const isDish = cartItem.dishId !== null;
      return {
        //页面通过id统计某个商品加入购物车的总数量
        id:isDish ? cartItem.dishId : cartItem.setmealId,
        productType:isDish ? 'dish' : 'setmeal',
        name:cartItem.name,
        image:cartItem.image,
        price:cartItem.amount,
        qty:cartItem.number,
        specs:cartItem.dishFlavor === null ? null:JSON.parse(cartItem.dishFlavor),
        //后端购物车主键，后面减少或删除购物车商品时会使用
        cartId:cartItem.id
      };
    });
    this.globalData.cart = cart
    wx.setStorageSync('cart', cart);
    // 菜单页和详情页已注册监听器，通知它们刷新数量和总价
    this.triggerCartCallbacks();
  },

  async changeCartQty(cartId, delta) {
    const item = this.globalData.cart.find(x => x.cartId === cartId);
    if (!item) return;

    if (delta > 0) {
      // 加号复用已有的添加接口，后端会按商品和口味合并数量
      await this.addToCart(item, item.specs, delta);
      return;
    }

    // cartId 是后端购物车主键，减一后重新查询，确保数量为零时页面也能移除该项
    await request({
      url: `/user/shoppingCart/sub/${cartId}`,
      method: 'POST'
    });
    await this.loadCart();
  },

  async clearCart() {
    await request({
      url: '/user/shoppingCart/clean',
      method: 'DELETE'
    });

    // 后端删除成功后再清空本地状态，避免请求失败时页面提前显示为空
    this.globalData.cart = [];
    wx.setStorageSync('cart', []);
    this.triggerCartCallbacks();
  },

  async loadAddresses() {
    const addressBooks = await request({
      url: '/user/addressBook/list',
      method: 'GET'
    });
    const addresses = addressBooks.map(toPageAddress);
    this.globalData.addresses = addresses;

    // 地址可能已在其他页面删除，重新查询后清理失效的结算页选择。
    if (this.globalData.currentAddress) {
      this.globalData.currentAddress = addresses.find(
        item => item.id === this.globalData.currentAddress.id
      ) || null;
    }
    return addresses;
  },

  async getAddressById(id) {
    const addressBook = await request({
      url: `/user/addressBook/${id}`,
      method: 'GET'
    });
    return toPageAddress(addressBook);
  },

  async saveAddress(address) {
    await request({
      url: '/user/addressBook',
      method: 'POST',
      data: toAddressBookPayload(address)
    });
    return this.loadAddresses();
  },

  async updateAddress(address) {
    await request({
      url: '/user/addressBook',
      method: 'PUT',
      data: toAddressBookPayload(address)
    });

    if (address.isDefault) {
      // 设置默认地址会先取消当前用户的其他默认地址。
      await this.setDefaultAddress(address.id);
      return this.globalData.addresses;
    }
    return this.loadAddresses();
  },

  async setDefaultAddress(id) {
    await request({
      url: '/user/addressBook/default',
      method: 'PUT',
      data: { id }
    });
    return this.loadAddresses();
  },

  async deleteAddress(id) {
    await request({
      url: `/user/addressBook?id=${id}`,
      method: 'DELETE'
    });
    return this.loadAddresses();
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
    
    // 地址数据由地址簿接口加载
    addresses: [],
    currentAddress: null,
    
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
