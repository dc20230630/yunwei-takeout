// pages/login/login.js
const app = getApp();
const { request } = require('../../utils/request');

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,
    
    phone: '',
    code: '',
    agree: true,
    countdown: 0,
    wechatLoading: false
  },

  timer: null,

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });
  },

  onUnload() {
    // 销毁页面时清除定时器，防泄露
    if (this.timer) {
      clearInterval(this.timer);
    }
  },

  onPhoneInput(e) {
    this.setData({
      phone: e.detail.value.trim()
    });
  },

  onCodeInput(e) {
    this.setData({
      code: e.detail.value.trim()
    });
  },

  toggleAgree() {
    this.setData({
      agree: !this.data.agree
    });
  },

  // 获取短信验证码
  getSMSCode() {
    const { phone } = this.data;
    if (!phone || phone.length !== 11) {
      wx.showToast({
        title: '请输入正确的手机号码',
        icon: 'none'
      });
      return;
    }

    wx.showToast({
      title: '验证码已发送：123456',
      icon: 'none',
      duration: 3000
    });

    // 开启 60 秒倒计时
    this.setData({ countdown: 60 });
    this.timer = setInterval(() => {
      let { countdown } = this.data;
      if (countdown <= 1) {
        clearInterval(this.timer);
        this.setData({ countdown: 0 });
      } else {
        this.setData({ countdown: countdown - 1 });
      }
    }, 1000);
  },

  // 手机验证码登录
  login() {
    const { phone, code, agree } = this.data;
    if (!agree) {
      wx.showToast({
        title: '请勾选同意协议和政策',
        icon: 'none'
      });
      return;
    }

    if (!phone || phone.length !== 11) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      });
      return;
    }

    if (!code || code.length !== 6) {
      wx.showToast({
        title: '请输入 6 位数验证码',
        icon: 'none'
      });
      return;
    }

    // 模拟登录成功
    const userInfo = {
      nickname: '云味客_' + phone.substring(7),
      phone: phone.substring(0, 3) + '****' + phone.substring(7),
      avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80'
    };

    app.globalData.userInfo = userInfo;
    wx.setStorageSync('userInfo', userInfo);
    

    wx.showToast({
      title: '登录成功',
      icon: 'success'
    });

    setTimeout(() => {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({
          url: '/pages/home/home'
        });
      }
    }, 1000);
  },

  // 微信快捷登录
  async wechatLogin() {
    if (!this.data.agree) {
      wx.showToast({
        title: '请勾选同意协议和政策',
        icon: 'none'
      });
      return;
    }

    if (this.data.wechatLoading) {
      return;
    }

    this.setData({ wechatLoading: true });
    wx.showLoading({
      title: '登录中',
      mask: true
    });

    try {
      // 向微信客户端获取一次性登录凭证 code
      const loginResult = await wx.login();

      if (!loginResult.code) {
        throw new Error('获取微信登录凭证失败');
      }

      // 后端使用 code 换取 openid，并返回用户信息和 Token
      const userLoginVO = await request({
        url: '/user/user/login',
        method: 'POST',
        data: {
          code: loginResult.code
        }
      });

      // Token 用于后续需要登录状态的用户端接口
      wx.setStorageSync('token', userLoginVO.token);
      await app.loadCart();

      const userInfo = {
        id: userLoginVO.id,
        openid: userLoginVO.openid
      };

      // 全局数据供当前运行中的页面读取，本地缓存供下次启动时恢复
      app.globalData.userInfo = userInfo;
      wx.setStorageSync('userInfo', userInfo);

      wx.showToast({
        title: '微信登录成功',
        icon: 'success'
      });

      setTimeout(() => {
        const pages = getCurrentPages();
        if (pages.length > 1) {
          wx.navigateBack();
        } else {
          wx.switchTab({
            url: '/pages/home/home'
          });
        }
      }, 1000);
    } catch (error) {
      // 控制台保留错误详情，页面只提示用户可理解的信息
      console.error('微信登录失败：', error);

      wx.showToast({
        title: '微信登录失败，请重试',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
      this.setData({ wechatLoading: false });
    }
  },

  showUserAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '本协议是您与云味外卖就外卖服务相关事项所订立的契约，请认真阅读并遵守。',
      showCancel: false
    });
  },

  showPrivacyPolicy() {
    wx.showModal({
      title: '隐私政策',
      content: '我们高度重视用户个人隐私保护，收集和使用您的定位和电话信息仅用于为您提供外卖配送服务。',
      showCancel: false
    });
  },

  goBack() {
    wx.navigateBack().catch(() => {
      // 容错处理：如果直接进入登录页，返回首页
      wx.switchTab({
        url: '/pages/home/home'
      });
    });
  }
});
