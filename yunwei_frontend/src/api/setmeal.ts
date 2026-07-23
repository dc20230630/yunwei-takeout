import request from '@/utils/request'

export interface SetmealDish {
  id?: number
  setmealId?: number
  dishId: number
  name: string
  price: number
  copies: number
}

export interface SetmealRequest {
  id?: number
  categoryId: number
  name: string
  price: number
  status: number
  description: string
  image: string
  setmealDishes: SetmealDish[]
}

export interface SetmealPageItem {
  id: number
  categoryId: number
  categoryName: string
  name: string
  price: number
  status: number
  description: string
  image: string
  updateTime: string
}

export interface SetmealDetail extends SetmealPageItem {
  setmealDishes: SetmealDish[]
}

export interface SetmealPageResult {
  total: number
  records: SetmealPageItem[]
}

export function getSetmealPage(params: {
  page: number
  pageSize: number
  name: string
  categoryId?: number
  status?: number
}) {
  return request.get<SetmealPageResult, SetmealPageResult>(
    '/admin/setmeal/page',
    { params }
  )
}

export function addSetmeal(data: SetmealRequest) {
  return request.post<void, void>('/admin/setmeal', data)
}

export function getSetmealDetail(id: number) {
  return request.get<SetmealDetail, SetmealDetail>(`/admin/setmeal/${id}`)
}

export function updateSetmeal(data: SetmealRequest) {
  return request.put<void, void>('/admin/setmeal', data)
}

export function deleteSetmeal(ids: number[]) {
  return request.delete<void, void>('/admin/setmeal', {
    params: { ids: ids.join(',') }
  })
}

export function updateSetmealStatus(id: number, status: number) {
  return request.put<void, void>('/admin/setmeal/status', null, {
    params: { id, status }
  })
}