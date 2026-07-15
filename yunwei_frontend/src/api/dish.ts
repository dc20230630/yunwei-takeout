import request from "@/utils/request";

export interface DishFlavorRequest {
    name: string,
    value: string
}
export interface DishAddRequest {
    name: string,
    categoryId: number
    price: number
    image: string
    description: string
    flavors: DishFlavorRequest[]
}

/**
 * 新增菜品和口味
 */
export function addDish(data: DishAddRequest) {
    return request.post('/admin/dish', data)
}


export interface DishPageItem {
    id: number
    name: string
    categoryId: number
    categoryName: string
    price: number
    image: string
    description: string
    status: number
    updateTime: string
}


export interface DishPageResult {
    total: number
    records: DishPageItem[]
}

/**
 * 分页查询菜品。
 */
export function getDishPage(params: {
    page: number,
    pageSize: number
    name: string
    categoryId: number | undefined
}) {
    return request.get<DishPageResult, DishPageResult>(
        '/admin/dish/page',
        { params }
    )
}

/**
 * 删除一个或多个菜品。
 */
export function deleteDish(ids: number[]) {
  return request.delete<void, void>('/admin/dish', {
    params: {
      // 后端接收 ids=1,2,3
      ids: ids.join(',')
    }
  })
}


export interface DishDetail {
  id: number
  name: string
  categoryId: number
  price: number
  image: string
  description: string
  status: number
  flavors: DishFlavorRequest[]
}

export interface DishUpdateRequest extends DishAddRequest {
  id: number
}

/**
 * 查询一条菜品的详情和口味。
 */
export function getDishDetail(id: number) {
  return request.get<DishDetail, DishDetail>(`/admin/dish/${id}`)
}

/**
 * 修改菜品和口味。
 */
export function updateDish(data: DishUpdateRequest) {
  return request.put<void, void>('/admin/dish', data)
}

/**
 * 起售或停售菜品。
 */
export function updateDishStatus(id: number, status: number) {
  return request.put<void, void>('/admin/dish/status', null, {
    params: { id, status }
  })
}
