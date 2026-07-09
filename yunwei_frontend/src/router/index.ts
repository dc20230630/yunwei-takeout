import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/store/user';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/pages/Dashboard/index.vue'),
        meta: { title: '工作台', requiresAuth: true }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/pages/Order/index.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('@/pages/Dish/index.vue'),
        meta: { title: '菜品管理', requiresAuth: true }
      },
      {
        path: 'combo',
        name: 'Combo',
        component: () => import('@/pages/Combo/index.vue'),
        meta: { title: '套餐管理', requiresAuth: true }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/pages/Category/index.vue'),
        meta: { title: '分类管理', requiresAuth: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/pages/User/index.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'staff',
        name: 'Staff',
        component: () => import('@/pages/Staff/index.vue'),
        meta: { title: '员工管理', requiresAuth: true }
      },
      {
        path: 'stat',
        name: 'Stat',
        component: () => import('@/pages/Stat/index.vue'),
        meta: { title: '数据统计', requiresAuth: true }
      },
      {
        path: 'setting',
        name: 'Setting',
        component: () => import('@/pages/Setting/index.vue'),
        meta: { title: '系统设置', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/layouts/AuthLayout.vue'),
    children: [
      {
        path: '',
        name: 'Login',
        component: () => import('@/pages/Login/index.vue'),
        meta: { title: '登录' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/NotFound/index.vue'),
    meta: { title: '404' }
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

// 全局路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore();
  const isAuthenticated = !!userStore.token;

  // 路由元信息标题设置
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 云味外卖管理系统`;
  }

  // 鉴权拦截
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({ name: 'Login' });
    } else {
      next();
    }
  } else {
    // 已登录状态下访问登录页，直接跳转到首页
    if (to.name === 'Login' && isAuthenticated) {
      next({ path: '/' });
    } else {
      next();
    }
  }
});

export default router;
