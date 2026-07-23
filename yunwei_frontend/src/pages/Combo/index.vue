<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">套餐管理</h2>
        <p class="text-gray-400 text-sm mt-1">维护套餐及其关联菜品</p>
      </div>
      <el-button type="primary" @click="openAddDialog">+ 新增套餐</el-button>
    </div>

    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">套餐名称</label>
        <el-input v-model="filterForm.name" placeholder="请输入套餐名称" style="width: 200px" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">套餐分类</label>
        <el-select v-model="filterForm.categoryId" placeholder="全部分类" style="width: 160px" clearable>
          <el-option
            v-for="category in setmealCategories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </div>
      <div class="flex gap-2">
        <el-button type="primary" @click="handleFilter">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">套餐列表</h3>
        <span class="text-xs text-gray-400">共 {{ total }} 个套餐</span>
      </div>

      <el-table :data="setmealList" style="width: 100%">
        <el-table-column label="套餐" min-width="220">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-image v-if="row.image" :src="row.image" fit="cover" class="w-9 h-9 rounded" />
              <span class="font-semibold text-gray-700">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="套餐分类" width="150" />
        <el-table-column label="套餐价格" width="130">
          <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="200" />
        <el-table-column label="售卖状态" width="130">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              active-text="在售"
              inactive-text="停售"
              inline-prompt
              @change="(value:object) => handleStatusChange(row, value ? 1 : 0)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
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
          @current-change="loadSetmealPage"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑套餐' : '新增套餐'"
      width="900px"
      destroy-on-close
    >
      <el-form ref="comboFormRef" :model="comboForm" :rules="comboRules" label-position="top">
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="套餐名称" prop="name">
            <el-input v-model="comboForm.name" placeholder="请输入套餐名称" />
          </el-form-item>
          <el-form-item label="套餐分类" prop="categoryId">
            <el-select v-model="comboForm.categoryId" placeholder="请选择套餐分类" class="w-full">
              <el-option
                v-for="category in setmealCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="套餐价格" prop="price">
            <el-input-number v-model="comboForm.price" :min="0.01" :precision="2" class="w-full" />
          </el-form-item>
          <el-form-item label="套餐图片" prop="image">
            <el-upload
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              :on-change="handleImageChange"
            >
              <el-button :loading="imageUploading">上传图片</el-button>
            </el-upload>
            <el-image v-if="comboForm.image" :src="comboForm.image" fit="cover" class="w-10 h-10 ml-3 rounded" />
          </el-form-item>
          <el-form-item label="套餐描述" class="col-span-2">
            <el-input v-model="comboForm.description" type="textarea" :rows="2" placeholder="请输入套餐描述" />
          </el-form-item>
        </div>

        <el-divider content-position="left">已选菜品</el-divider>
        <el-table :data="selectedDishes" border size="small">
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column label="单价" width="130">
            <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="份数" width="180">
            <template #default="{ row }">
              <el-input-number v-model="row.copies" :min="1" :precision="0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90">
            <template #default="{ $index }">
              <el-button link type="danger" @click="removeDish($index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">可选菜品</el-divider>
        <el-table :data="availableDishes" border size="small" max-height="220">
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column label="价格" width="130">
            <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="150" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="primary" :disabled="isDishSelected(row.id)" @click="addDish(row)">
                {{ isDishSelected(row.id) ? '已添加' : '添加' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile } from 'element-plus'
import { getCategoryList, type CategoryItem } from '@/api/category'
import { uploadDishImage } from '@/api/common'
import { getDishPage, type DishPageItem } from '@/api/dish'
import {
  addSetmeal,
  deleteSetmeal,
  getSetmealDetail,
  getSetmealPage,
  updateSetmeal,
  updateSetmealStatus,
  type SetmealDish,
  type SetmealPageItem,
  type SetmealRequest
} from '@/api/setmeal'

const setmealCategories = ref<CategoryItem[]>([])
const availableDishes = ref<DishPageItem[]>([])
const selectedDishes = ref<SetmealDish[]>([])
const setmealList = ref<SetmealPageItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const imageUploading = ref(false)
const comboFormRef = ref<FormInstance>()

const filterForm = reactive({
  name: '',
  categoryId: undefined as number | undefined
})

const comboForm = reactive({
  id: undefined as number | undefined,
  name: '',
  categoryId: undefined as number | undefined,
  price: 0,
  status: 1,
  description: '',
  image: ''
})

const comboRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择套餐分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入套餐价格', trigger: 'blur' }],
  image: [{ required: true, message: '请上传套餐图片', trigger: 'change' }]
}

const loadSetmealPage = async () => {
  const result = await getSetmealPage({
    page: currentPage.value,
    pageSize: pageSize.value,
    name: filterForm.name,
    categoryId: filterForm.categoryId
  })
  total.value = result.total
  setmealList.value = result.records
}

const loadSetmealCategories = async () => {
  setmealCategories.value = await getCategoryList(2)
}

const loadAvailableDishes = async () => {
  const result = await getDishPage({
    page: 1,
    pageSize: 100,
    name: '',
    categoryId: undefined
  })
  availableDishes.value = result.records.filter((dish) => dish.status === 1)
}

const handleFilter = () => {
  currentPage.value = 1
  loadSetmealPage()
}

const handleReset = () => {
  filterForm.name = ''
  filterForm.categoryId = undefined
  currentPage.value = 1
  loadSetmealPage()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadSetmealPage()
}

const resetComboForm = () => {
  Object.assign(comboForm, {
    id: undefined,
    name: '',
    categoryId: undefined,
    price: 0,
    status: 1,
    description: '',
    image: ''
  })
  selectedDishes.value = []
}

const openAddDialog = async () => {
  isEdit.value = false
  resetComboForm()
  dialogVisible.value = true
  await nextTick()
  comboFormRef.value?.clearValidate()
}

const openEditDialog = async (row: SetmealPageItem) => {
  const detail = await getSetmealDetail(row.id)
  isEdit.value = true
  Object.assign(comboForm, {
    id: detail.id,
    name: detail.name,
    categoryId: detail.categoryId,
    price: detail.price,
    status: detail.status,
    description: detail.description,
    image: detail.image
  })
  selectedDishes.value = detail.setmealDishes.map((dish) => ({ ...dish }))
  dialogVisible.value = true
}

const isDishSelected = (dishId: number) => {
  return selectedDishes.value.some((dish) => dish.dishId === dishId)
}

const addDish = (dish: DishPageItem) => {
  selectedDishes.value.push({
    dishId: dish.id,
    name: dish.name,
    price: dish.price,
    copies: 1
  })
}

const removeDish = (index: number) => {
  selectedDishes.value.splice(index, 1)
}

const handleImageChange = async (uploadFile: UploadFile) => {
  if (!uploadFile.raw) {
    return
  }
  imageUploading.value = true
  try {
    comboForm.image = await uploadDishImage(uploadFile.raw)
  } finally {
    imageUploading.value = false
  }
}

const buildRequest = (): SetmealRequest => ({
  id: comboForm.id,
  name: comboForm.name,
  categoryId: comboForm.categoryId!,
  price: comboForm.price,
  status: comboForm.status,
  description: comboForm.description,
  image: comboForm.image,
  setmealDishes: selectedDishes.value
})

const handleSave = async () => {
  await comboFormRef.value?.validate()
  if (selectedDishes.value.length === 0) {
    ElMessage.warning('套餐至少需要关联一道菜品')
    return
  }

  const requestData = buildRequest()
  if (isEdit.value) {
    await updateSetmeal(requestData)
  } else {
    await addSetmeal(requestData)
  }

  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadSetmealPage()
}

const handleStatusChange = async (row: SetmealPageItem, status: number) => {
  await updateSetmealStatus(row.id, status)
  ElMessage.success(status === 1 ? '套餐已起售' : '套餐已停售')
  loadSetmealPage()
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认要删除该套餐吗？', '提示', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteSetmeal([id])
      ElMessage.success('套餐已删除')
      loadSetmealPage()
    })
    .catch(() => undefined)
}

onMounted(() => {
  loadSetmealCategories()
  loadAvailableDishes()
  loadSetmealPage()
})
</script>
