// 开发者工具访问本机 Spring Boot 服务时使用的地址
const BASE_URL = 'http://127.0.0.1:8080';

/**
 * 统一发送后端请求。
 *
 * @param {Object} options 请求配置
 * @param {String} options.url 接口路径
 * @param {String} options.method 请求方式
 * @param {Object} options.data 请求参数
 * @returns {Promise<Object>} 后端 Result 中的 data
 */
function request(options) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    const header = {
      'content-type': 'application/json'
    };

    // 登录接口没有 Token；登录后的用户端接口会自动携带 Token
    if (token) {
      header.authentication = token;
    }

    wx.request({
      url: BASE_URL + options.url,
      method: options.method,
      data: options.data,
      header,
      success(response) {
        const result = response.data;

        // 当前后端 Result.success(...) 的成功状态码为 200
        if (result.code === 200) {
          resolve(result.data);
          return;
        }

        reject(new Error(result.message));
      },
      fail(error) {
        reject(error);
      }
    });
  });
}

module.exports = {
  request
};
