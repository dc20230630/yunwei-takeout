// pages/addAddress/addAddress.js
const app = getApp();

Page({
  data: {
    statusBarHeight: 0,
    navBarHeight: 0,
    totalNavHeight: 0,

    isEditMode: false,
    addressId: null,

    // 表单字段
    name: '',
    gender: '先生',
    phone: '',
    region: '',
    regionArray: ['广东省', '深圳市', '南山区'],
    detail: '',
    tag: '家',
    isDefault: false,

    // 静态选项
    tagOptions: ['家', '公司', '学校']
  },

  onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });

    // 检查是否为编辑模式
    if (options.id) {
      const id = parseInt(options.id);
      const address = app.globalData.addresses.find(a => a.id === id);
      if (address) {
        // 根据原有数据拆分 region 数组，供 picker 展示
        const regionArray = address.region.split(' ');
        this.setData({
          isEditMode: true,
          addressId: id,
          name: address.name,
          gender: address.gender || '先生',
          phone: address.phone,
          region: address.region,
          regionArray: regionArray.length === 3 ? regionArray : ['广东省', '深圳市', '南山区'],
          detail: address.detail,
          tag: address.tag,
          isDefault: address.isDefault
        });
      }
    }
  },

  onNameInput(e) { this.setData({ name: e.detail.value.trim() }); },
  onPhoneInput(e) { this.setData({ phone: e.detail.value.trim() }); },
  onDetailInput(e) { this.setData({ detail: e.detail.value.trim() }); },
  
  selectGender(e) {
    this.setData({ gender: e.currentTarget.dataset.val });
  },

  selectTag(e) {
    this.setData({ tag: e.currentTarget.dataset.val });
  },

  onRegionChange(e) {
    const valArray = e.detail.value;
    this.setData({
      regionArray: valArray,
      region: valArray.join(' ')
    });
  },

  onDefaultChange(e) {
    this.setData({ isDefault: e.detail.value });
  },

  // 保存地址逻辑
  saveAddress() {
    const { name, gender, phone, region, detail, tag, isDefault, isEditMode, addressId } = this.data;

    // 表单合法性验证
    if (!name) {
      wx.showToast({ title: '请填写联系人姓名', icon: 'none' });
      return;
    }
    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请填写11位手机号', icon: 'none' });
      return;
    }
    if (!region) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }
    if (!detail) {
      wx.showToast({ title: '请填写详细门牌号', icon: 'none' });
      return;
    }

    const addresses = app.globalData.addresses || [];

    // 排他机制：如果要将当前地址设为默认，则先将其他地址改为非默认
    if (isDefault) {
      addresses.forEach(addr => addr.isDefault = false);
    }

    const newAddressData = {
      id: isEditMode ? addressId : Date.now(),
      name,
      gender,
      phone,
      region,
      detail,
      tag,
      isDefault
    };

    if (isEditMode) {
      // 1. 编辑已有地址
      const index = addresses.findIndex(a => a.id === addressId);
      if (index !== -1) {
        addresses[index] = newAddressData;
      }
      
      // 同步更新结算页正在选用的地址缓存
      if (app.globalData.currentAddress && app.globalData.currentAddress.id === addressId) {
        app.globalData.currentAddress = newAddressData;
      }
    } else {
      // 2. 新建地址
      addresses.push(newAddressData);
      
      // 如果之前地址库为空，或者新设为了默认，直接将其设为当前结算首选地址
      if (addresses.length === 1 || isDefault) {
        app.globalData.currentAddress = newAddressData;
      }
    }

    app.globalData.addresses = addresses;
    wx.setStorageSync('addresses', addresses);

    wx.showToast({
      title: isEditMode ? '修改成功' : '保存成功',
      icon: 'success'
    });

    // 返回列表页
    setTimeout(() => {
      wx.navigateBack();
    }, 1000);
  },

  // 删除地址
  deleteAddress() {
    wx.showModal({
      title: '删除确认',
      content: '确定要删除该收货地址吗？',
      success: (res) => {
        if (res.confirm) {
          const { addressId } = this.data;
          let addresses = app.globalData.addresses || [];
          addresses = addresses.filter(a => a.id !== addressId);
          
          app.globalData.addresses = addresses;
          wx.setStorageSync('addresses', addresses);

          // 级联处理：如果被删地址是当前结算选用的地址，清空它，避免结算页拉取异常
          if (app.globalData.currentAddress && app.globalData.currentAddress.id === addressId) {
            app.globalData.currentAddress = null;
          }

          wx.showToast({
            title: '删除成功',
            icon: 'success'
          });

          setTimeout(() => {
            wx.navigateBack();
          }, 1000);
        }
      }
    });
  },

  goBack() {
    wx.navigateBack();
  }
});
