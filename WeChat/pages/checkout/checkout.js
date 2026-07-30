// pages/checkout/checkout.js
const app = getApp();
const { request } = require('../../utils/request');

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    cart: [],
    address: null,
    remark: '',
    payMethod: 1,
    // 1 为立即送出，0 为预约配送，必须和后端 OrdersSubmitDTO 保持一致
    deliveryStatus: 1,
    deliveryDate: '',
    deliveryTime: '',
    // 1 为按餐量提供，0 为用户自选数量，必须和后端 OrdersSubmitDTO 保持一致
    tablewareStatus: 1,
    tablewareNumber: '',
    packFee: 2.00,
    deliveryFee: 2.00,
    discount: 0.00,
    goodsTotal: 0.00,
    finalTotal: 0.00
  },

  onLoad() {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  async onShow() {
    const cart = app.globalData.cart || [];
    const addresses = await app.loadAddresses();
    let address = app.globalData.currentAddress;

    if (!address && addresses.length > 0) {
      address = addresses.find(item => item.isDefault) || addresses[0];
      app.globalData.currentAddress = address;
    }

    this.setData({ cart, address }, () => this.calculateBill());
  },

  calculateBill() {
    const { packFee, deliveryFee } = this.data;
    const goodsTotal = app.getCartTotal();
    const discount = goodsTotal >= 30 ? 5.00 : 0.00;
    const finalTotal = Math.max(goodsTotal + packFee + deliveryFee - discount, 0);

    this.setData({
      goodsTotal: Number(goodsTotal.toFixed(2)),
      discount: Number(discount.toFixed(2)),
      finalTotal: Number(finalTotal.toFixed(2))
    });
  },

  selectAddress() {
    wx.navigateTo({ url: '/pages/addresses/addresses?from=checkout' });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  onDeliveryStatusChange(e) {
    this.setData({ deliveryStatus: Number(e.detail.value) });
  },

  onDeliveryDateChange(e) {
    this.setData({ deliveryDate: e.detail.value });
  },

  onDeliveryTimeChange(e) {
    this.setData({ deliveryTime: e.detail.value });
  },

  onTablewareStatusChange(e) {
    const tablewareStatus = Number(e.detail.value);
    this.setData({
      tablewareStatus,
      // 切换到自选数量时默认提供 1 份，减号不能将数量减到 0
      tablewareNumber: tablewareStatus === 0 && !this.data.tablewareNumber ? '1' : this.data.tablewareNumber
    });
  },

  onTablewareNumberInput(e) {
    this.setData({ tablewareNumber: e.detail.value });
  },

  changeTablewareNumber(e) {
    const delta = Number(e.currentTarget.dataset.delta);
    const currentNumber = Number(this.data.tablewareNumber);
    const tablewareNumber = Number.isInteger(currentNumber) && currentNumber > 0
      ? Math.max(1, currentNumber + delta)
      : 1;
    this.setData({ tablewareNumber: String(tablewareNumber) });
  },

  buildSubmitData() {
    const {
      address,
      remark,
      payMethod,
      deliveryStatus,
      deliveryDate,
      deliveryTime,
      tablewareStatus,
      tablewareNumber
    } = this.data;

    if (deliveryStatus === 0 && (!deliveryDate || !deliveryTime)) {
      wx.showToast({ title: '请选择预约配送时间', icon: 'none' });
      return null;
    }

    const tablewareCount = Number(tablewareNumber);
    if (tablewareStatus === 0 && (!Number.isInteger(tablewareCount) || tablewareCount < 1)) {
      wx.showToast({ title: '请输入餐具数量', icon: 'none' });
      return null;
    }

    return {
      addressBookId: address.id,
      remark: remark.trim(),
      payMethod,
      deliveryStatus,
      // 后端 LocalDateTime 使用 yyyy-MM-dd HH:mm:ss 格式
      deliveryTime: deliveryStatus === 0 ? `${deliveryDate} ${deliveryTime}:00` : null,
      tablewareStatus,
      tablewareNumber: tablewareStatus === 0 ? tablewareCount : null
    };
  },

  async submitOrder() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }

    const submitData = this.buildSubmitData();
    if (!submitData) {
      return;
    }

    wx.showLoading({ title: '正在提交订单', mask: true });
    try {
      const orderSubmitVO = await request({
        url: '/user/order/submit',
        method: 'POST',
        data: submitData
      });

      // 当前未接入真实微信商户支付，这里调用本地模拟支付接口更新订单状态
      await request({
        url: '/user/order/mock-payment',
        method: 'PUT',
        data: { orderNumber: orderSubmitVO.orderNumber }
      });

      // 后端事务已清空购物车；请求成功后再同步本地购物车，避免本地仍显示旧商品
      app.globalData.cart = [];
      wx.setStorageSync('cart', []);
      app.triggerCartCallbacks();

      wx.redirectTo({
        url: `/pages/paySuccess/paySuccess?id=${orderSubmitVO.id}&amount=${orderSubmitVO.orderAmount}`
      });
    } catch (error) {
      console.error('提交订单失败：', error);
      wx.showToast({ title: error.message || '提交订单失败，请重试', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  goBack() {
    wx.navigateBack();
  }
});
