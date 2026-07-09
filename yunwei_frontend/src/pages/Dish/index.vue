<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-baseline">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">菜品管理</h2>
        <p class="text-gray-400 text-sm mt-1">维护菜品、库存与售卖状态</p>
      </div>
      <el-button type="primary" @click="openAddDialog">+ 新增菜品</el-button>
    </div>

    <!-- 过滤工具栏 -->
    <div class="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex flex-wrap gap-4 items-end">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">菜品名称</label>
        <el-input v-model="filterForm.name" placeholder="请输入菜品名称" size="default" style="width: 200px;" clearable />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-500">菜品分类</label>
        <el-select v-model="filterForm.category" placeholder="全部分类" size="default" style="width: 160px;" clearable>
          <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
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
        <span class="text-xs text-gray-400">共 {{ filteredDishes.length }} 个菜品</span>
      </div>

      <el-table :data="filteredDishes" style="width: 100%" size="default">
        <el-table-column label="菜品" min-width="160">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="text-lg">🍲</span>
              <span class="font-semibold text-gray-700">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="130" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="月销量" width="100" />
        <el-table-column label="售卖状态" width="110">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              active-text="在售"
              inactive-text="下架"
              inline-prompt
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="updated" label="更新时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增 / 编辑 对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜品' : '新增菜品'"
      width="560px"
      destroy-on-close
    >
      <el-form 
        ref="dishFormRef"
        :model="dishForm"
        :rules="dishRules"
        label-position="top"
      >
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="菜品名称" prop="name" class="col-span-2">
            <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
          </el-form-item>
          <el-form-item label="菜品分类" prop="category">
            <el-select v-model="dishForm.category" placeholder="请选择分类" class="w-full">
              <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
            </el-select>
          </el-form-item>
          <el-form-item label="价格 (元)" prop="price">
            <el-input-number v-model="dishForm.price" :min="0" :precision="2" :step="1" class="w-full" />
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input-number v-model="dishForm.stock" :min="0" :precision="0" :step="10" class="w-full" />
          </el-form-item>
          <el-form-item label="菜品描述" prop="desc" class="col-span-2">
            <el-input 
              v-model="dishForm.desc" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入菜品材料或特色介绍..." 
            />
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
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';

const route = useRoute();

// 静态可用分类
const categories = ['精品热菜', '特色主菜', '海鲜水产', '冰镇饮品', '风味小吃'];

// 模拟初始菜品数据
const dishes = ref([
  { id: 'D01', name: '招牌红烧肉', category: '精品热菜', price: 58, stock: 99, sales: 540, status: true, updated: '2026-07-07 18:30', desc: '选五花肉慢火细炖，酱香浓郁，肥而不腻。' },
  { id: 'D02', name: '秘制大盘鸡', category: '特色主菜', price: 68, stock: 45, sales: 412, status: true, updated: '2026-07-07 15:24', desc: '鸡肉与土豆炖煮入味，配手擀面。' },
  { id: 'D03', name: '清蒸鲈鱼', category: '海鲜水产', price: 78, stock: 20, sales: 231, status: true, updated: '2026-07-06 11:10', desc: '鲜活鲈鱼清蒸，豉油提鲜。' },
  { id: 'D04', name: '麻婆豆腐', category: '精品热菜', price: 22, stock: 150, sales: 680, status: true, updated: '2026-07-08 08:00', desc: '川味经典，麻辣鲜香。' },
  { id: 'D05', name: '经典酸梅汤', category: '冰镇饮品', price: 12, stock: 200, sales: 920, status: true, updated: '2026-07-07 20:00', desc: '乌梅山楂手工熬制。' },
  { id: 'D06', name: '红糖糍粑', category: '风味小吃', price: 16, stock: 80, sales: 340, status: false, updated: '2026-07-05 14:15', desc: '外酥里糯，红糖香甜。' }
]);

// 过滤表单
const filterForm = reactive({
  name: '',
  category: ''
});

const filteredDishes = computed(() => {
  return dishes.value.filter(d => {
    const matchName = !filterForm.name || d.name.toLowerCase().includes(filterForm.name.trim().toLowerCase());
    const matchCat = !filterForm.category || d.category === filterForm.category;
    return matchName && matchCat;
  });
});

// 对话框与表单管理
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentDishId = ref('');
const dishFormRef = ref<FormInstance>();

const dishForm = reactive({
  name: '',
  category: '',
  price: 0,
  stock: 0,
  desc: ''
});

const dishRules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
};

const handleFilter = () => {};

const handleReset = () => {
  filterForm.name = '';
  filterForm.category = '';
};

// 状态切换
const toggleStatus = (row: any) => {
  const text = row.status ? '上架' : '下架';
  ElMessage.success(`菜品“${row.name}”已成功${text}`);
};

// 删除菜品
const handleDelete = (id: string) => {
  ElMessageBox.confirm(
    '确认要删除该菜品吗？此操作无法撤销。',
    '提示',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    dishes.value = dishes.value.filter(d => d.id !== id);
    ElMessage.success('删除成功');
  }).catch(() => {});
};

// 新增弹窗
const openAddDialog = () => {
  isEdit.value = false;
  currentDishId.value = '';
  dishForm.name = '';
  dishForm.category = categories[0];
  dishForm.price = 0;
  dishForm.stock = 100;
  dishForm.desc = '';
  dialogVisible.value = true;
};

// 编辑弹窗
const openEditDialog = (row: any) => {
  isEdit.value = true;
  currentDishId.value = row.id;
  dishForm.name = row.name;
  dishForm.category = row.category;
  dishForm.price = row.price;
  dishForm.stock = row.stock;
  dishForm.desc = row.desc || '';
  dialogVisible.value = true;
};

const getNowTimeString = () => {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

// 保存
const handleSave = async () => {
  if (!dishFormRef.value) return;
  await dishFormRef.value.validate((valid) => {
    if (valid) {
      if (isEdit.value) {
        const dish = dishes.value.find(d => d.id === currentDishId.value);
        if (dish) {
          dish.name = dishForm.name;
          dish.category = dishForm.category;
          dish.price = dishForm.price;
          dish.stock = dishForm.stock;
          dish.desc = dishForm.desc;
          dish.updated = getNowTimeString();
          ElMessage.success('更新成功');
        }
      } else {
        const newId = 'D' + String(dishes.value.length + 1).padStart(2, '0');
        dishes.value.unshift({
          id: newId,
          name: dishForm.name,
          category: dishForm.category,
          price: dishForm.price,
          stock: dishForm.stock,
          sales: 0,
          status: true,
          updated: getNowTimeString(),
          desc: dishForm.desc
        });
        ElMessage.success('添加成功');
      }
      dialogVisible.value = false;
    }
  });
};

onMounted(() => {
  // 如果是从快捷操作跳过来的新增
  if (route.query.action === 'add') {
    openAddDialog();
  }
});
</script>

<style scoped>
</style>
