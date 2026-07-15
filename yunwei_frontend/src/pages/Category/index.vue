<template>
  <div class="space-y-6">
    <div class="flex justify-between items-baseline">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">分类管理</h2>
        <p class="text-gray-400 text-sm mt-1">管理菜品与套餐分类</p>
      </div>
      <el-button type="primary" @click="openAddDialog">+ 新增分类</el-button>
    </div>

    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">分类名称</label>
        <el-input
          v-model="filterForm.name"
          placeholder="请输入分类名称"
          style="width: 200px"
          clearable
        />
      </div>

      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">分类类型</label>
        <el-select
          v-model="filterForm.type"
          placeholder="全部类型"
          style="width: 150px"
          clearable
        >
          <el-option label="菜品分类" :value="1" />
          <el-option label="套餐分类" :value="2" />
        </el-select>
      </div>

      <div class="flex gap-2">
        <el-button type="primary" @click="handleFilter">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">分类列表</h3>
        <span class="text-xs text-gray-400">共 {{ total }} 个分类</span>
      </div>

      <el-table :data="categoryList" style="width: 100%">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="150" />

        <el-table-column label="分类类型" width="130">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : 'success'">
              {{ row.type === 1 ? '菜品分类' : '套餐分类' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="sort" label="排序" width="100" align="center" />

        <el-table-column label="启用状态" width="120">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" />

        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="450px"
      destroy-on-close
    >
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-position="top">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>

        <el-form-item label="分类类型" prop="type">
          <el-select v-model="categoryForm.type" placeholder="请选择分类类型" class="w-full">
            <el-option label="菜品分类" :value="1" />
            <el-option label="套餐分类" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :precision="0" class="w-full" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  addCategory,
  deleteCategory,
  getCategoryPage,
  updateCategory,
  updateCategoryStatus,
  type CategoryItem
} from '@/api/category'

// 当前页码
const currentPage = ref(1)

// 每页显示的分类数量
const pageSize = ref(10)

// 所有符合筛选条件的分类总数
const total = ref(0)

// 表格当前页数据
const categoryList = ref<CategoryItem[]>([])

// 查询条件。type 为空时表示不按类型筛选
const filterForm = reactive({
  name: '',
  type: undefined as number | undefined
})

// 新增和编辑共用同一个弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const categoryFormRef = ref<FormInstance>()

const categoryForm = reactive({
  id: 0,
  name: '',
  type: 1,
  sort: 0
})

const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择分类类型', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排序值', trigger: 'blur' }]
}

/**
 * 按当前分页和筛选条件查询分类。
 */
const loadCategoryPage = async () => {
  const pageResult = await getCategoryPage({
    page: currentPage.value,
    pageSize: pageSize.value,
    name: filterForm.name,
    type: filterForm.type
  })

  total.value = pageResult.total
  categoryList.value = pageResult.records
}

/**
 * 查询时回到第一页，避免筛选后当前页没有数据。
 */
const handleFilter = () => {
  currentPage.value = 1
  loadCategoryPage()
}

/**
 * 清空筛选条件后重新查询。
 */
const handleReset = () => {
  filterForm.name = ''
  filterForm.type = undefined
  currentPage.value = 1
  loadCategoryPage()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadCategoryPage()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadCategoryPage()
}

/**
 * 分类状态使用数字，1 为启用，0 为禁用。
 */
const toggleStatus = async (row: CategoryItem) => {
  await updateCategoryStatus(row.id, row.status)

  ElMessage.success(`分类“${row.name}”已${row.status === 1 ? '启用' : '禁用'}`)

  // 状态更新会同时修改更新时间，重新查询后表格数据才准确
  await loadCategoryPage()
}

/**
 * 确认后删除分类。
 */
const handleDelete = async (id: number) => {
  try {
    // 取消确认框时只结束当前操作，不发送删除请求
    await ElMessageBox.confirm('确认要删除该分类吗？', '提示', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  await deleteCategory(id)
  ElMessage.success('分类已删除')
  await loadCategoryPage()
}

/**
 * 打开新增弹窗并重置表单。
 */
const openAddDialog = () => {
  isEdit.value = false
  categoryForm.id = 0
  categoryForm.name = ''
  categoryForm.type = 1
  categoryForm.sort = 0
  dialogVisible.value = true
}

/**
 * 将表格行数据回显到编辑弹窗。
 */
const openEditDialog = (category: CategoryItem) => {
  isEdit.value = true
  categoryForm.id = category.id
  categoryForm.name = category.name
  categoryForm.type = category.type
  categoryForm.sort = category.sort
  dialogVisible.value = true
}

/**
 * 根据当前弹窗模式调用新增或修改接口。
 */
const handleSave = async () => {
  if (!categoryFormRef.value) return

  const valid = await categoryFormRef.value.validate()
  if (!valid) return

  if (isEdit.value) {
    await updateCategory({
      id: categoryForm.id,
      name: categoryForm.name,
      type: categoryForm.type,
      sort: categoryForm.sort
    })
    ElMessage.success('分类修改成功')
  } else {
    await addCategory({
      name: categoryForm.name,
      type: categoryForm.type,
      sort: categoryForm.sort
    })
    ElMessage.success('分类新增成功')
  }

  dialogVisible.value = false
  await loadCategoryPage()
}

// 页面首次打开时加载分类分页数据
onMounted(loadCategoryPage)
</script>
