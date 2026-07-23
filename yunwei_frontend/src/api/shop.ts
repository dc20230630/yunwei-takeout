import request from '@/utils/request'

/**
 * 查询店铺营业状态。
 */
export function getShopStatus() {
  return request.get<number, number>('/admin/shop/status')
}

/**
 * 修改店铺营业状态。
 */
export function updateShopStatus(status: number) {
  return request.put<void, void>('/admin/shop/status', null, {
    params: { status }
  })
}
