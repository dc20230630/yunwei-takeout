<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-baseline">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">套餐管理</h2>
        <p class="text-gray-400 text-sm mt-1">维护套餐组合与售卖信息</p>
      </div>
      <el-button type="primary" @click="openAddDialog">+ 新增套餐</el-button>
    </div>

    <!-- 过滤工具栏 -->
    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">套餐名称</label>
        <el-input v-model="filterName" placeholder="请输入套餐名称" size="default" style="width: 200px;" clearable />
      </div>
      <div class="flex gap-2">
        <el-button type="primary" @click="handleFilter">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 数据列表 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">套餐列表</h3>
        <span class="text-xs text-gray-400">共 {{ filteredCombos.length }} 个套餐</span>
      </div>

      <el-table :data="filteredCombos" style="width: 100%" size="default">
        <el-table-column label="套餐名称" min-width="180">
          <template #default="{ row }">
            <span class="font-bold text-gray-700">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="套餐分类" width="140" />
        <el-table-column label="套餐价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="count" label="包含菜品数" width="120" align="center" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column label="售卖状态" width="120">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              active-text="在售"
              inactive-text="停售"
              inline-prompt
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑套餐' : '新增套餐'"
      width="500px"
      destroy-on-close
    >
      <el-form 
        ref="comboFormRef"
        :model="comboForm"
        :rules="comboRules"
        label-position="top"
      >
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="comboForm.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="套餐分类" prop="category">
          <el-select v-model="comboForm.category" placeholder="请选择分类" class="w-full">
            <el-option label="超值套餐" value="超值套餐" />
            <el-option label="单人简餐" value="单人简餐" />
          </el-select>
        </el-form-item>
        <el-form-item label="套餐价格" prop="price">
          <el-input-number v-model="comboForm.price" :min="0" :precision="2" :step="5" class="w-full" />
        </el-form-item>
        <el-form-item label="包含菜品数量" prop="count">
          <el-input-number v-model="comboForm.count" :min="1" :precision="0" :step="1" class="w-full" />
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

// 模拟初始套餐数据
const combos = ref([
  { id: 'C01', name: '双人超值大盘鸡套餐', category: '超值套餐', price: 88, count: 3, sales: 124, status: true },
  { id: 'C02', name: '单人精致红烧肉套餐', category: '单人简餐', price: 62, count: 4, sales: 285, status: true },
  { id: 'C03', name: '工作日能量午餐', category: '单人简餐', price: 39, count: 3, sales: 366, status: true }
]);

const filterName = ref('');

const filteredCombos = computed(() => {
  return combos.value.filter(c => {
    return !filterName.value || c.name.toLowerCase().includes(filterName.value.trim().toLowerCase());
  });
});

const dialogVisible = ref(false);
const isEdit = ref(false);
const currentComboId = ref('');
const comboFormRef = ref<FormInstance>();

const comboForm = reactive({
  name: '',
  category: '单人简餐',
  price: 0,
  count: 1
});

const comboRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  count: [{ required: true, message: '请输入菜品数量', trigger: 'blur' }]
};

const handleFilter = () => {};
const handleReset = () => {
  filterName.value = '';
};

const toggleStatus = (row: any) => {
  ElMessage.success(`套餐“${row.name}”已成功${row.status ? '上架' : '下架'}`);
};

const handleDelete = (id: string) => {
  ElMessageBox.confirm(
    '确认要删除该套餐吗？',
    '提示',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    combos.value = combos.value.filter(c => c.id !== id);
    ElMessage.success('套餐已成功删除');
  }).catch(() => {});
};

const openAddDialog = () => {
  isEdit.value = false;
  currentComboId.value = '';
  comboForm.name = '';
  comboForm.category = '单人简餐';
  comboForm.price = 0;
  comboForm.count = 3;
  dialogVisible.value = true;
};

const openEditDialog = (row: any) => {
  isEdit.value = true;
  currentComboId.value = row.id;
  comboForm.name = row.name;
  comboForm.category = row.category;
  comboForm.price = row.price;
  comboForm.count = row.count;
  dialogVisible.value = true;
};

const handleSave = async () => {
  if (!comboFormRef.value) return;
  await comboFormRef.value.validate((valid) => {
    if (valid) {
      if (isEdit.value) {
        const combo = combos.value.find(c => c.id === currentComboId.value);
        if (combo) {
          combo.name = comboForm.name;
          combo.category = comboForm.category;
          combo.price = comboForm.price;
          combo.count = comboForm.count;
          ElMessage.success('套餐信息修改成功');
        }
      } else {
        const newId = 'C' + String(combos.value.length + 1).padStart(2, '0');
        combos.value.unshift({
          id: newId,
          name: comboForm.name,
          category: comboForm.category,
          price: comboForm.price,
          count: comboForm.count,
          sales: 0,
          status: true
        });
        ElMessage.success('新套餐添加成功');
      }
      dialogVisible.value = false;
    }
  });
};
</script>

<style scoped>
</style>
