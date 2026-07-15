<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">菜品管理</h2>
        <p class="text-gray-400 text-sm mt-1">维护菜品、库存与售卖状态</p>
      </div>

      <!-- 两个按钮属于同一个右侧操作区域 -->
      <div class="flex gap-2">
        <el-button type="primary" @click="openAddDialog"> + 新增菜品 </el-button>

        <el-button
          type="danger"
          plain
          :disabled="selectedDishIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 过滤工具栏 -->
    <div
      class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end"
    >
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">菜品名称</label>
        <el-input
          v-model="filterForm.name"
          placeholder="请输入菜品名称"
          size="default"
          style="width: 200px"
          clearable
        />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">菜品分类</label>
        <el-select
          v-model="filterForm.categoryId"
          placeholder="全部分类"
          size="default"
          style="width: 160px"
          clearable
        >
          <el-option
            v-for="category in dishCategories"
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

    <!-- 菜品数据列表 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">菜品列表</h3>
        <span class="text-xs text-gray-400">共 {{ total }} 个菜品</span>
      </div>

      <el-table
        :data="dishList"
        style="width: 100%"
        size="default"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="菜品">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-image v-if="row.image" :src="row.image" fit="cover" class="w-9 h-9 rounded" />
              <span class="font-semibold text-gray-700">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column label="价格">
          <template #default="{ row }"> ¥{{ row.price.toFixed(2) }} </template>
        </el-table-column>
        <!-- <el-table-column prop="stock" label="库存" width="100" /> -->
        <!-- <el-table-column prop="sales" label="月销量" width="100" /> -->
        <el-table-column label="售卖状态">
          <template #default="{ row }">
            <!-- <el-switch
              v-model="row.status"
              active-text="在售"
              inactive-text="下架"
              inline-prompt
              @change="toggleStatus(row)"
            /> -->
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在售' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="260" />
        <el-table-column label="操作" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)"
              >编辑</el-button
            >
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '停售' : '起售' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete([row.id])"
              >删除</el-button
            >
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

    <!-- 新增 / 编辑 对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜品' : '新增菜品'"
      width="780px"
      top="5vh"
      :lock-scroll="true"
      append-to-body
      modal-class="dish-dialog-overlay"
      class="dish-dialog"
      destroy-on-close
    >
      <el-form ref="dishFormRef" :model="dishForm" :rules="dishRules" label-position="top">
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="菜品名称" prop="name" class="col-span-2">
            <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
          </el-form-item>
          <el-form-item label="菜品分类" prop="categoryId">
            <el-select v-model="dishForm.categoryId" placeholder="请选择分类" class="w-full">
              <el-option
                v-for="category in dishCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="价格 (元)" prop="price">
            <el-input-number
              v-model="dishForm.price"
              :min="0"
              :precision="2"
              :step="1"
              class="w-full"
            />
          </el-form-item>
          <el-form-item label="菜品口味" class="col-span-2">
            <div class="w-full">
              <div v-for="(flavor, index) in flavorForms" :key="index" class="flex gap-2 mb-2">
                <!-- 选择口味名称 -->
                <el-select
                  v-model="flavor.name"
                  placeholder="选择口味"
                  style="width: 180px"
                  @change="() => handleFlavorNameChange(flavor)"
                >
                  <el-option
                    v-for="preset in flavorPresets"
                    :key="preset.name"
                    :label="preset.name"
                    :value="preset.name"
                  />
                </el-select>

                <!-- multiple 会把多个选项展示成标签 -->
                <el-select
                  v-model="flavor.options"
                  multiple
                  filterable
                  allow-create
                  default-first-option
                  placeholder="选择或输入可选值"
                  style="flex: 1"
                >
                  <el-option
                    v-for="option in flavorPresets.find((item) => item.name === flavor.name)
                      ?.options || []"
                    :key="option"
                    :label="option"
                    :value="option"
                  />
                </el-select>

                <el-button type="danger" plain @click="removeFlavor(index)"> 删除 </el-button>
              </div>
              <el-button type="primary" plain @click="addFlavor"> + 添加口味 </el-button>
            </div>
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input-number
              v-model="dishForm.stock"
              :min="0"
              :precision="0"
              :step="10"
              class="w-full"
            />
          </el-form-item>
          <el-form-item label="菜品描述" prop="desc" class="col-span-2">
            <el-input
              v-model="dishForm.desc"
              type="textarea"
              :rows="3"
              placeholder="请输入菜品材料或特色介绍..."
            />
          </el-form-item>
          <el-form-item label="菜品图片" prop="image" class="col-span-2">
            <el-upload
              class="dish-image-upload"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              :on-change="handleImageChange"
            >
              <img v-if="dishForm.image" :src="dishForm.image" class="dish-image-preview" />
              <div v-else class="dish-upload-placeholder">
                <el-icon class="dish-upload-icon"><Plus /></el-icon>
                <span>点击选择图片</span>
              </div>
            </el-upload>
          </el-form-item>
        </div>
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile } from 'element-plus'
import { getCategoryList, type CategoryItem } from '@/api/category'
import { uploadDishImage } from '@/api/common'
import {
  addDish,
  deleteDish,
  getDishDetail,
  getDishPage,
  updateDish,
  updateDishStatus,
  type DishPageItem
} from '@/api/dish'

const route = useRoute()

interface FlavorForm {
  name: string
  options: string[]
}

// 常用口味模板
const flavorPresets = [
  {
    name: '辣度',
    options: ['不辣', '微辣', '中辣', '重辣']
  },
  {
    name: '甜度',
    options: ['无糖', '少糖', '半糖', '多糖', '全糖']
  },
  {
    name: '温度',
    options: ['常温', '少冰', '去冰', '热']
  }
]

const dishCategories = ref<CategoryItem[]>([])
const flavorForms = ref<FlavorForm[]>([])
const imageUploading = ref(false)
// 当前表格中被勾选的菜品 id
const selectedDishIds = ref<number[]>([])

// 当前页码
const currentPage = ref(1)

// 每页数量
const pageSize = ref(10)

// 后端返回的总条数
const total = ref(0)

// 当前页的菜品数据
const dishList = ref<DishPageItem[]>([])

// 过滤表单
const filterForm = reactive({
  name: '',
  // undefined 表示不按分类筛选
  categoryId: undefined as number | undefined
})

/**
 * 根据当前页码、每页数量和筛选条件查询菜品。
 */

const loadDishPage = async () => {
  const pageResult = await getDishPage({
    page: currentPage.value,
    pageSize: pageSize.value,
    name: filterForm.name,
    categoryId: filterForm.categoryId
  })

  total.value = pageResult.total
  dishList.value = pageResult.records
}

/**
 * 查询时回到第一页。
 */
const handleFilter = () => {
  currentPage.value = 1
  loadDishPage()
}

/**
 * 清空条件后重新查询。
 */
const handleReset = () => {
  filterForm.name = ''
  filterForm.categoryId = undefined
  currentPage.value = 1
  loadDishPage()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadDishPage()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadDishPage()
}

// 对话框与表单管理
const dialogVisible = ref(false)
const isEdit = ref(false)
// 当前正在编辑的菜品 id；新增时为 undefined
const currentDishId = ref<number | undefined>()
const dishFormRef = ref<FormInstance>()

const dishForm = reactive({
  name: '',
  category: '',
  price: 0,
  stock: 0,
  desc: '',
  image: '',
  categoryId: undefined as number | undefined
})

const dishRules = {
  categoryId: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
  image: [{ required: true, message: '请上传菜品图片', trigger: 'change' }]
}

// 新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  currentDishId.value = undefined
  dishForm.name = ''
  dishForm.price = 0
  dishForm.categoryId = undefined
  dishForm.stock = 100
  dishForm.desc = ''
  dishForm.image = ''
  flavorForms.value = []
  dialogVisible.value = true
}

const handleImageChange = async (file: UploadFile) => {
  if (!file.raw) return
  imageUploading.value = true
  try {
    dishForm.image = await uploadDishImage(file.raw)
    ElMessage.success('图片上传成功')
  } finally {
    imageUploading.value = false
  }
}

const getNowTimeString = () => {
  const d = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}


/**
 * 查询详情后，将菜品和口味回显到弹框。
 */
const openEditDialog = async (row: DishPageItem) => {
  const dish = await getDishDetail(row.id)

  isEdit.value = true
  currentDishId.value = dish.id

  dishForm.name = dish.name
  dishForm.categoryId = dish.categoryId
  dishForm.price = dish.price
  dishForm.desc = dish.description
  dishForm.image = dish.image

  // 数据库中的 value 是 JSON 字符串，转回前端多选数组
  flavorForms.value = dish.flavors.map((flavor) => ({
    name: flavor.name,
    options: JSON.parse(flavor.value)
  }))

  dialogVisible.value = true
}
//口味
const addFlavor = () => {
  // options 改成数组，用于存放多个可选值
  flavorForms.value.push({ name: '', options: [] })
}

const handleFlavorNameChange = (flavor: FlavorForm) => {
  // 找到当前选择的口味模板
  const preset = flavorPresets.find((item) => item.name === flavor.name)

  if (!preset) {
    return
  }

  // 复制模板选项，避免多个口味行共用同一个数组
  flavor.options = [...preset.options]
}

const removeFlavor = (index: number) => {
  flavorForms.value.splice(index, 1)
}

// 保存
const handleSave = async () => {
  if (!dishFormRef.value) return
  await dishFormRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        const hasIncompleteFlavor = flavorForms.value.some(
          (flavor) => !flavor.name || flavor.options.length === 0
        )
        if (hasIncompleteFlavor) {
          ElMessage.warning('请填写完整的口味名称和可选值')
          return
        }

        if (currentDishId.value === undefined || dishForm.categoryId === undefined) {
          return
        }

        await updateDish({
          id: currentDishId.value,
          name: dishForm.name,
          categoryId: dishForm.categoryId,
          price: dishForm.price,
          image: dishForm.image,
          description: dishForm.desc,
          flavors: flavorForms.value.map((flavor) => ({
            name: flavor.name,
            value: JSON.stringify(flavor.options)
          }))
        })

        ElMessage.success('菜品修改成功')
        await loadDishPage()
      } else {
        //检查用户是否新增了口味行，但没有填写完整
        const hasIncompleteFlavor = flavorForms.value.some(
          (flavor) => !flavor.name || flavor.options.length === 0
        )
        if (hasIncompleteFlavor) {
          ElMessage.warning('请填写完整的口味名称和可选值')
          return
        }

        //分类是必选项
        if (dishForm.categoryId === undefined) {
          return
        }

        await addDish({
          //菜品基本信息
          name: dishForm.name,
          categoryId: dishForm.categoryId,
          price: dishForm.price,
          image: dishForm.image,
          description: dishForm.desc,

          //把页面的口味表单转换为后端需要的格式
          flavors: flavorForms.value.map((flavor) => ({
            //辣度
            name: flavor.name,

            // 例如 ["无糖","少糖","半糖"] 转成后端要保存的 JSON 字符串
            value: JSON.stringify(flavor.options)
          }))
        })
        ElMessage.success('菜品添加成功')
        await loadDishPage()
      }
      dialogVisible.value = false
    }
  })
}

/**
 * 表格勾选变化时，保存所有被选中的菜品 id。
 */
const handleSelectionChange = (rows: DishPageItem[]) => {
  selectedDishIds.value = rows.map((row) => row.id)
}

//删除方法
const handleDelete = async (ids: number[]) => {
  try {
    await ElMessageBox.confirm('确认删除选中的菜品吗？关联口味也会同时删除。', '提示', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteDish(ids)
  ElMessage.success('删除成功')

  // 当前页仅剩一条且不是第一页时，删除后回到上一页
  if (dishList.value.length === ids.length && currentPage.value > 1) {
    currentPage.value--
  }

  selectedDishIds.value = []
  await loadDishPage()
}

const handleBatchDelete = () => {
  handleDelete(selectedDishIds.value)
}

/**
 * 根据当前状态决定下一步是起售还是停售。
 */
const handleStatusChange = async (row: DishPageItem) => {
  const nextStatus = row.status === 1 ? 0 : 1
  const actionText = nextStatus === 1 ? '起售' : '停售'

  try {
    await ElMessageBox.confirm(
      `确认${actionText}菜品“${row.name}”吗？`,
      '提示',
      {
        confirmButtonText: `确定${actionText}`,
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    // 用户取消时不发送状态更新请求
    return
  }

  await updateDishStatus(row.id, nextStatus)
  ElMessage.success(`菜品已${actionText}`)

  // 重新查询，确保状态和更新时间都来自数据库
  await loadDishPage()
}

//页面加载时请求分类
const loadDishCategories = async () => {
  dishCategories.value = await getCategoryList(1)
}

onMounted(async () => {
  await loadDishCategories()
  await loadDishPage()
  // 如果是从快捷操作跳过来的新增
  if (route.query.action === 'add') {
    openAddDialog()
  }
})
</script>

<style scoped>
.dish-image-upload :deep(.el-upload) {
  border: 1px dashed #cbd5e1;
  border-radius: 6px;
  overflow: hidden;
}

.dish-image-upload :deep(.el-upload:hover) {
  border-color: #409eff;
}

.dish-upload-placeholder,
.dish-image-preview {
  width: 160px;
  height: 160px;
}

.dish-upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #94a3b8;
  background: #f8fafc;
}

.dish-upload-icon {
  font-size: 28px;
}

.dish-image-preview {
  display: block;
  object-fit: cover;
}

.dish-upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #94a3b8;
}

/* 弹框固定在可视区域内，不能被内容撑高 */
:global(.dish-dialog) {
  height: 90vh;
  margin: 5vh auto 0 !important;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 标题区域不参与滚动 */
:global(.dish-dialog .el-dialog__header) {
  flex-shrink: 0;
}

/* 只有表单内容区域滚动 */
:global(.dish-dialog .el-dialog__body) {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 20px;
}

/* 底部按钮始终显示 */
:global(.dish-dialog .el-dialog__footer) {
  flex-shrink: 0;
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  background: #ffffff;
}
</style>
