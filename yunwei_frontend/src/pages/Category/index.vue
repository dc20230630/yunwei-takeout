<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-baseline">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">分类管理</h2>
        <p class="text-gray-400 text-sm mt-1">管理菜品与套餐分类</p>
      </div>
      <el-button type="primary" @click="openAddDialog">+ 新增分类</el-button>
    </div>

    <!-- 过滤工具栏 -->
    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">分类名称</label>
        <el-input v-model="filterForm.name" placeholder="请输入分类名称" size="default" style="width: 200px;" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">分类类型</label>
        <el-select v-model="filterForm.type" placeholder="全部类型" size="default" style="width: 150px;" clearable>
          <el-option label="菜品分类" value="菜品分类" />
          <el-option label="套餐分类" value="套餐分类" />
        </el-select>
      </div>
      <div class="flex gap-2">
        <el-button type="primary" @click="handleFilter">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 数据列表 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">分类列表</h3>
        <span class="text-xs text-gray-400">共 {{ filteredCategories.length }} 个分类</span>
      </div>

      <el-table :data="filteredCategories" style="width: 100%" size="default">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="type" label="分类类型" width="130">
          <template #default="{ row }">
            <el-tag :type="row.type === '菜品分类' ? 'primary' : 'success'">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="关联数量" width="110" align="center">
          <template #default="{ row }">
            <span class="font-medium">{{ row.count }} 项</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column label="启用状态" width="120">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="time" label="创建时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增分类"
      width="450px"
      destroy-on-close
    >
      <el-form 
        ref="catFormRef"
        :model="catForm"
        :rules="catRules"
        label-position="top"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="catForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类类型" prop="type">
          <el-select v-model="catForm.type" placeholder="请选择分类类型" class="w-full">
            <el-option label="菜品分类" value="菜品分类" />
            <el-option label="套餐分类" value="套餐分类" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="catForm.sort" :min="1" :precision="0" class="w-full" />
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
import { ref, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';

// 模拟初始分类数据
const categories = ref([
  { id: 'CAT01', name: '特色主菜', type: '菜品分类', count: 8, sort: 1, status: true, time: '2026-05-10 10:00' },
  { id: 'CAT02', name: '精品热菜', type: '菜品分类', count: 12, sort: 2, status: true, time: '2026-05-10 10:15' },
  { id: 'CAT03', name: '海鲜水产', type: '菜品分类', count: 5, sort: 3, status: true, time: '2026-05-11 11:20' },
  { id: 'CAT04', name: '冰镇饮品', type: '菜品分类', count: 6, sort: 4, status: true, time: '2026-05-12 09:00' },
  { id: 'CAT05', name: '超值套餐', type: '套餐分类', count: 3, sort: 5, status: true, time: '2026-05-15 15:00' }
]);

const filterForm = reactive({
  name: '',
  type: ''
});

const filteredCategories = computed(() => {
  return categories.value.filter(c => {
    const matchName = !filterForm.name || c.name.toLowerCase().includes(filterForm.name.trim().toLowerCase());
    const matchType = !filterForm.type || c.type === filterForm.type;
    return matchName && matchType;
  });
});

const dialogVisible = ref(false);
const catFormRef = ref<FormInstance>();

const catForm = reactive({
  name: '',
  type: '菜品分类',
  sort: 1
});

const catRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择分类类型', trigger: 'change' }]
};

const handleFilter = () => {};
const handleReset = () => {
  filterForm.name = '';
  filterForm.type = '';
};

const toggleStatus = (row: any) => {
  ElMessage.success(`分类“${row.name}”已成功${row.status ? '开启' : '禁用'}`);
};

const handleDelete = (id: string) => {
  ElMessageBox.confirm(
    '确认要删除该分类吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    categories.value = categories.value.filter(c => c.id !== id);
    ElMessage.success('分类已删除');
  }).catch(() => {});
};

const openAddDialog = () => {
  catForm.name = '';
  catForm.type = '菜品分类';
  catForm.sort = categories.value.length + 1;
  dialogVisible.value = true;
};

const getNowTimeString = () => {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const handleSave = async () => {
  if (!catFormRef.value) return;
  await catFormRef.value.validate((valid) => {
    if (valid) {
      const newId = 'CAT' + String(categories.value.length + 1).padStart(2, '0');
      categories.value.push({
        id: newId,
        name: catForm.name,
        type: catForm.type,
        count: 0,
        sort: catForm.sort,
        status: true,
        time: getNowTimeString()
      });
      ElMessage.success('分类添加成功');
      dialogVisible.value = false;
    }
  });
};
</script>

<style scoped>
</style>
