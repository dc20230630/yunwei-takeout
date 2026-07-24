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

  async onLoad(options) {
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight,
      navBarHeight: app.globalData.navBarHeight,
      totalNavHeight: app.globalData.totalNavHeight
    });

    // 编辑时按主键查询，避免依赖地址列表页是否已经加载过数据。
    if (options.id) {
      const id = parseInt(options.id);
      const address = await app.getAddressById(id);
      this.setData({
        isEditMode: true,
        addressId: id,
        name: address.name,
        gender: address.gender,
        phone: address.phone,
        region: address.region,
        regionArray: address.regionArray,
        detail: address.detail,
        tag: address.tag,
        isDefault: address.isDefault
      });
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
  async saveAddress() {
    const { name, gender, phone, detail, tag, isDefault, isEditMode, addressId } = this.data;

    // 表单合法性验证
    if (!name) {
      wx.showToast({ title: '请填写联系人姓名', icon: 'none' });
      return;
    }
    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请填写11位手机号', icon: 'none' });
      return;
    }
    if (!this.data.region) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }
    if (!detail) {
      wx.showToast({ title: '请填写详细门牌号', icon: 'none' });
      return;
    }

    const address = {
      id: addressId,
      name,
      gender,
      phone,
      regionArray: this.data.regionArray,
      detail,
      tag,
      isDefault
    };

    if (isEditMode) {
      await app.updateAddress(address);
    } else {
      await app.saveAddress(address);
    }

    if (isDefault) {
      // 新增或修改为默认地址后，使用后端重新查询到的默认记录作为结算地址。
      app.globalData.currentAddress = app.globalData.addresses.find(item => item.isDefault);
    } else if (!isEditMode && app.globalData.addresses.length === 1) {
      app.globalData.currentAddress = app.globalData.addresses[0];
    }

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
      success: async (res) => {
        if (res.confirm) {
          const { addressId } = this.data;
          await app.deleteAddress(addressId);

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
