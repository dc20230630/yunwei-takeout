import request from '@/utils/request'

/**
 * 分类列表中的一条数据。
 */
export interface CategoryItem {
  id: number
  type: number
  name: string
  sort: number
  status: number
  createTime: string
  updateTime: string
}

/**
 * 分类新增和修改时提交的数据。
 */
export interface CategoryRequest {
  id?: number
  type: number
  name: string
  sort: number
}

/**
 * 分类分页接口返回的数据。
 */
export interface CategoryPageResult {
  total: number
  records: CategoryItem[]
}

/**
 * 查询分类分页数据。
 */
export function getCategoryPage(params: {
  page: number
  pageSize: number
  name: string
  type: number | undefined
}) {
  return request.get<CategoryPageResult, CategoryPageResult>(
    '/admin/category/page',
    { params }
  )
}

/**
 * 新增分类。
 */
export function addCategory(data: CategoryRequest) {
  return request.post<void, void>('/admin/category', data)
}

/**
 * 修改分类名称、类型和排序。
 */
export function updateCategory(data: CategoryRequest) {
  return request.put<void, void>('/admin/category', data)
}

/**
 * 启用或禁用分类。
 */
export function updateCategoryStatus(id: number, status: number) {
  return request.put<void, void>('/admin/category/status', null, {
    params: { id, status }
  })
}

/**
 * 根据 ID 删除分类。
 */
export function deleteCategory(id: number) {
  return request.delete<void, void>(`/admin/category/${id}`)
}

export function getCategoryList(type: number) {
  return request.get<CategoryItem[], CategoryItem[]>('/admin/category/list', {
    params: { type }
  })
}