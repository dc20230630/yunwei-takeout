// pages/profile/profile.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    isLoggedIn: false,
    userInfo: null
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  onShow() {
    this.checkLoginStatus();
  },

  checkLoginStatus() {
    const userInfo = app.globalData.userInfo;
    if (userInfo) {
      this.setData({
        isLoggedIn: true,
        userInfo: userInfo
      });
    } else {
      this.setData({
        isLoggedIn: false,
        userInfo: null
      });
    }
  },

  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    });
  },

  goToAddresses() {
    // 限制必须登录后才能查看收货地址
    if (!this.data.isLoggedIn) {
      wx.showToast({
        title: '请先登录账号',
        icon: 'none'
      });
      setTimeout(() => {
        this.goToLogin();
      }, 1000);
      return;
    }
    
    wx.navigateTo({
      url: '/pages/addresses/addresses'
    });
  },

  contactService() {
    wx.showModal({
      title: '拨打客服电话',
      content: '客服电话：010-84728888',
      confirmText: '拨打',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '010-84728888'
          });
        }
      }
    });
  },

  showAgreement() {
    wx.showModal({
      title: '协议声明',
      content: '本小程序为云味外卖演示原型，用户协议与隐私条款均遵循国家法律法规，确保用户信息安全。',
      showCancel: false
    });
  },

  logout() {
    wx.showModal({
      title: '温馨提示',
      content: '确定要退出当前登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除全局数据和本地持久化
          app.globalData.userInfo = null;
          wx.removeStorageSync('userInfo');
          // 清除 Token，避免退出后仍以旧登录态访问用户端接口
          wx.removeStorageSync('token');
          
          this.setData({
            isLoggedIn: false,
            userInfo: null
          });
          
          wx.showToast({
            title: '已安全退出',
            icon: 'success'
          });
        }
      }
    });
  }
});
