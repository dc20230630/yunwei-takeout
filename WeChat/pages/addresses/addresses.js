// pages/addresses/addresses.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,

    addresses: [],
    isFromCheckout: false,
    selectedAddressId: 0
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight,
      isFromCheckout: options.from === 'checkout'
    });
  },

  async onShow() {
    // 每次展示都从后端查询，确保新增、编辑和删除后的列表是最新数据。
    const addresses = await app.loadAddresses();
    
    // 如果是从结算页跳来的，获取结算页当前选中的地址id用于高亮勾选
    let selectedId = 0;
    if (this.data.isFromCheckout && app.globalData.currentAddress) {
      selectedId = app.globalData.currentAddress.id;
    }

    this.setData({
      addresses: addresses,
      selectedAddressId: selectedId
    });
  },

  // 选中地址行为
  onSelectAddress(e) {
    const item = e.currentTarget.dataset.item;
    
    // 如果是从结算页进来，点击直接回填并返回
    if (this.data.isFromCheckout) {
      app.globalData.currentAddress = item;
      wx.navigateBack();
    }
  },

  goToAdd() {
    wx.navigateTo({
      url: '/pages/addAddress/addAddress'
    });
  },

  goToEdit(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/addAddress/addAddress?id=${id}`
    });
  },

  goBack() {
    wx.navigateBack();
  }
});
