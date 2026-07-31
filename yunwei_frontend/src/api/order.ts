import request from '@/utils/request'

export interface OrderDetailItem {
  id: number
  name: string
  image: string | null
  dishFlavor: string | null
  number: number
  amount: number
}

export interface AdminOrder {
  id: number
  number: string
  status: number
  payMethod: number
  amount: number
  remark: string | null
  consignee: string
  phone: string
  address: string
  cancelReason: string | null
  orderTime: string
  deliveryStatus: number
  deliveryTime: string | null
  orderDetails: OrderDetailItem[]
}

export interface OrderQueryParams {
  number: string
  phone: string
  status?: number
}

/**
 * 查询管理端订单列表。
 */
export function getAdminOrderList(params: OrderQueryParams) {
  return request.get<AdminOrder[], AdminOrder[]>('/admin/order/list', { params })
}

/**
 * 查询一笔订单的完整明细。
 */
export function getAdminOrderDetail(id: number) {
  return request.get<AdminOrder, AdminOrder>(`/admin/order/${id}`)
}

/**
 * 将待接单订单改为制作中。
 */
export function acceptAdminOrder(id: number) {
  return request.put<void, void>(`/admin/order/${id}/accept`)
}

/**
 * 取消待接单或制作中的订单。
 */
export function cancelAdminOrder(id: number) {
  return request.put<void, void>(`/admin/order/${id}/cancel`)
}
