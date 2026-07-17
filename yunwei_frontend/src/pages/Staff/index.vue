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
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <!-- 新增员工对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增员工"
      width="500px"
      destroy-on-close
    >
      <el-form 
        ref="staffFormRef"
        :model="staffForm"
        :rules="staffRules"
        label-position="top"
      >
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
          <el-form-item label="职位" prop="role">
            <el-select v-model="staffForm.role" placeholder="选择职位" class="w-full">
              <el-option label="店长" value="店长" />
              <el-option label="前台主管" value="前台主管" />
              <el-option label="主厨" value="主厨" />
              <el-option label="配送员" value="配送员" />
            </el-select>
          </el-form-item>
          <el-form-item label="初始密码" prop="password">
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
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';

// 模拟初始员工数据
const staffList = ref([
  { id: 'S101', name: '王晓东', username: 'wangxd', phone: '13566778899', role: '店长', status: true, time: '2026-01-01', last: '2026-07-08 08:30' },
  { id: 'S102', name: '李丽华', username: 'lilh', phone: '13611223344', role: '前台主管', status: true, time: '2026-02-10', last: '2026-07-08 09:00' },
  { id: 'S103', name: '张大厨', username: 'zhangdc', phone: '13755664422', role: '主厨', status: true, time: '2026-01-15', last: '2026-07-07 21:00' },
  { id: 'S104', name: '赵小跑', username: 'zhaoxp', phone: '13900998877', role: '配送员', status: false, time: '2026-03-01', last: '2026-07-05 18:22' }
]);

const getRoleTagType = (role: string) => {
  const map: Record<string, string> = {
    '店长': 'danger',
    '前台主管': 'warning',
    '主厨': 'primary',
    '配送员': 'success'
  };
  return map[role] || 'info';
};

const toggleStatus = (row: any) => {
  ElMessage.success(`员工“${row.name}”的账户已成功${row.status ? '开启' : '关闭'}`);
};

const handleDelete = (id: string) => {
  ElMessageBox.confirm(
    '确认要删除该员工账号吗？此操作将立即注销其登录权限。',
    '提示',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    staffList.value = staffList.value.filter(s => s.id !== id);
    ElMessage.success('员工账号已删除');
  }).catch(() => {});
};

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
  staffForm.name = '';
  staffForm.username = '';
  staffForm.phone = '';
  staffForm.role = '配送员';
  dialogVisible.value = true;
};

const getNowDateString = () => {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
};

const handleSave = async () => {
  if (!staffFormRef.value) return;
  await staffFormRef.value.validate((valid) => {
    if (valid) {
      const nextId = 'S' + (100 + staffList.value.length + 1);
      staffList.value.push({
        id: nextId,
        name: staffForm.name,
        username: staffForm.username,
        phone: staffForm.phone,
        role: staffForm.role,
        status: true,
        time: getNowDateString(),
        last: '-'
      });
      ElMessage.success('新增员工成功');
      dialogVisible.value = false;
    }
  });
};
</script>

<style scoped></style>
