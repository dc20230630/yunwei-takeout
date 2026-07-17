<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-baseline">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">员工管理</h2>
        <p class="text-gray-400 text-sm mt-1">管理员工账号</p>
      </div>
      <el-button type="primary" @click="openAddDialog">+ 新增员工</el-button>
    </div>

    <!-- 员工列表卡片 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">员工列表</h3>
        <span class="text-xs text-gray-400">共 {{ total }} 个员工账号</span>
      </div>

      <el-table :data="staffList" style="width: 100%" size="default">
        <el-table-column prop="name" label="员工姓名" />
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="sex" label="性别">
          <template #default="{ row }">
            <span>{{ row.sex === '1' ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="账号状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-text="启用"
              inactive-text="停用"
              inline-prompt
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row.id)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)"
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

    <!-- 新增员工对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑员工' : '新增员工'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="staffFormRef" :model="staffForm" :rules="staffRules" label-position="top">
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="staffForm.name" placeholder="请输入员工姓名" />
          </el-form-item>
          <el-form-item label="账号" prop="username">
            <el-input v-model="staffForm.username" placeholder="建议使用拼音，如 zhangsan" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone" class="col-span-2">
            <el-input v-model="staffForm.phone" placeholder="请输入11位手机号" />
          </el-form-item>
          <el-form-item v-if="!isEdit" label="初始密码" prop="password">
            <el-input v-model="staffForm.password" type="password" show-password disabled />
          </el-form-item>

          <el-form-item label="性别" prop="sex">
            <el-select v-model="staffForm.sex" placeholder="请选择性别" class="w-full">
              <el-option label="男" value="1" />
              <el-option label="女" value="2" />
            </el-select>
          </el-form-item>

          <el-form-item label="身份证号" prop="idNumber" class="col-span-2">
            <el-input v-model="staffForm.idNumber" placeholder="请输入身份证号" />
          </el-form-item>

          <el-form-item label="性别" prop="sex">
            <el-select v-model="staffForm.sex" placeholder="请选择性别" class="w-full">
              <el-option label="男" value="1" />
              <el-option label="女" value="2" />
            </el-select>
          </el-form-item>

          <el-form-item label="身份证号" prop="idNumber" class="col-span-2">
            <el-input v-model="staffForm.idNumber" placeholder="请输入身份证号" />
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
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  addEmployee,
  getEmployeeList,
  updateEmployeeStatus,
  getEmployeeDetail,
  updateEmployee,
  deleteEmployee,
  type EmployeeListItem
} from '@/api/employee'

// 当前页码
const currentPage = ref(1)

// 每页条数
const pageSize = ref(10)

// 员工总数
const total = ref(0)

interface StaffItem extends Omit<EmployeeListItem, 'status'> {
  // Element Plus 开关需要布尔值
  status: boolean
}

// 初始员工数据
const staffList = ref<StaffItem[]>([])

/**
 * 查询员工列表。
 */

const loadEmployeeList = async () => {
  const employeeList = await getEmployeeList(currentPage.value, pageSize.value)

  //保存后端返回的总条数
  total.value = employeeList.total

  // 后端状态是 1/0，前端开关需要 true/false
  staffList.value = employeeList.records.map((employee) => ({
    ...employee,
    status: employee.status === 1
  }))
}

/**
 * 切换页码后重新查询。
 */
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadEmployeeList()
}

/**
 * 修改每页条数后回到第一页重新查询。
 */
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadEmployeeList()
}

// 页面加载完成后查询员工
onMounted(loadEmployeeList)

/**
 * 点击开关后，更新员工的启用或禁用状态。
 */
const toggleStatus = async (row: StaffItem) => {
  const status = row.status ? 1 : 0

  await updateEmployeeStatus(row.id, status)

  ElMessage.success(`员工“${row.name}”的账户已成功${row.status ? '开启' : '关闭'}`)

  await loadEmployeeList()
}

const handleDelete = async (id: number) => {
  try {
    // 点击“取消”或关闭弹窗时，confirm 会抛出取消结果
    await ElMessageBox.confirm('确认要删除该员工账号吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    // 用户主动取消删除，不调用后端接口
    return
  }

  // 确认后才调用后端 DELETE 接口
  await deleteEmployee(id)
  
  ElMessage.success('员工账号已删除')

  await loadEmployeeList()
}

// true 表示当前弹窗用于编辑，false 表示新增
const isEdit = ref(false)
// 新增对话框
const dialogVisible = ref(false)
const staffFormRef = ref<FormInstance>()

const staffForm = reactive({
  id: 0,
  name: '',
  username: '',
  phone: '',
  sex: '1',
  idNumber: '',
  role: '配送员',
  password: '默认密码: 123456'
})

const staffRules = {
  name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idNumber: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
}

const openAddDialog = () => {
  // 新增模式不需要员工 ID
  isEdit.value = false
  staffForm.id = 0
  staffForm.name = ''
  staffForm.username = ''
  staffForm.phone = ''
  dialogVisible.value = true
  staffForm.sex = '1'
  staffForm.idNumber = ''
}

/**
 * 查询员工详情后，打开编辑弹窗。
 */
const openEditDialog = async (id: number) => {
  // 切换为编辑模式
  isEdit.value = true

  // 身份证号不在列表数据中，需要按 ID 查询详情
  const employee = await getEmployeeDetail(id)

  // 将接口数据回显到表单
  staffForm.id = employee.id
  staffForm.name = employee.name
  staffForm.username = employee.username
  staffForm.phone = employee.phone
  staffForm.sex = employee.sex
  staffForm.idNumber = employee.idNumber

  dialogVisible.value = true
}

const handleSave = async () => {
  if (!staffFormRef.value) return

  const valid = await staffFormRef.value.validate()
  if (!valid) return

  if (isEdit.value) {
    //编辑时带员工Id，调用PUT
    await updateEmployee({
      id: staffForm.id,
      name: staffForm.name,
      username: staffForm.username,
      phone: staffForm.phone,
      sex: staffForm.sex,
      idNumber: staffForm.idNumber
    })
    ElMessage.success('员工信息修改成功')
  } else {
    await addEmployee({
      username: staffForm.username,
      name: staffForm.name,
      phone: staffForm.phone,
      sex: staffForm.sex,
      idNumber: staffForm.idNumber
    })

    ElMessage.success('新增员工成功')
  }

  dialogVisible.value = false

  // 重新查询列表，让新员工显示出来
  await loadEmployeeList()
}
</script>

<style scoped></style>
