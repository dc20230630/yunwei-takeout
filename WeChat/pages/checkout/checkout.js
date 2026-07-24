// pages/checkout/checkout.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,

    cart: [],
    address: null,
    
    // 账单明细
    packFee: 2.00,
    deliveryFee: 2.00,
    discount: 0.00,
    goodsTotal: 0.00,
    finalTotal: 0.00
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  async onShow() {
    // 1. 读取购物车
    const cart = app.globalData.cart || [];

    const addresses = await app.loadAddresses();
    
    // 2. 匹配地址逻辑：若已全局暂存当前选择的地址则直接用，否则在地址库中寻找默认地址
    let address = app.globalData.currentAddress;
    if (!address && addresses.length > 0) {
      address = addresses.find(addr => addr.isDefault) || addresses[0];
      app.globalData.currentAddress = address;
    }

    this.setData({
      cart,
      address
    }, () => {
      this.calculateBill();
    });
  },

  // 账单金额计算
  calculateBill() {
    const { cart, packFee, deliveryFee } = this.data;
    const goodsTotal = app.getCartTotal();

    // 满减逻辑：满 30 减 5 元，完全对接原型上的商户公告规则
    const discount = goodsTotal >= 30 ? 5.00 : 0.00;
    
    let finalTotal = goodsTotal + packFee + deliveryFee - discount;
    if (finalTotal < 0) finalTotal = 0;

    this.setData({
      goodsTotal: parseFloat(goodsTotal.toFixed(2)),
      discount: parseFloat(discount.toFixed(2)),
      finalTotal: parseFloat(finalTotal.toFixed(2))
    });
  },

  selectAddress() {
    wx.navigateTo({
      url: '/pages/addresses/addresses?from=checkout'
    });
  },

  // 提交并支付订单
  submitOrder() {
    if (!this.data.address) {
      wx.showToast({
        title: '请选择收货地址',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '正在创建支付...'
    });

    // 模拟 1.2 秒支付网络耗时
    setTimeout(async () => {
      wx.hideLoading();

      // 1. 创建订单数据对象
      const now = new Date();
      const timeStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
      
      const newOrderId = 'YW' + Math.floor(Math.random() * 900000000 + 100000000);
      
      // 深度拷贝购物车内容加入订单
      const orderItems = this.data.cart.map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        qty: item.qty,
        specs: item.specs,
        image: item.image
      }));

      const newOrder = {
        id: newOrderId,
        time: timeStr,
        status: '待接单',
        total: this.data.finalTotal,
        items: orderItems
      };

      // 2. 将新订单存入全局列表的顶端 (最先展示)
      app.globalData.orders.unshift(newOrder);

      // 3. 后端清空成功后，才能跳转到支付成功页
      await app.clearCart();

      // 4. 重定向去成功支付页面 (避免用户按返回键重复提交)
      wx.redirectTo({
        url: `/pages/paySuccess/paySuccess?id=${newOrderId}&amount=${this.data.finalTotal}`
      });
    }, 1200);
  },

  goBack() {
    wx.navigateBack();
  }
});
