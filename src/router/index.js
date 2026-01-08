import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  /* ================= 登录 ================= */
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },

  /* ================= 管理端 ================= */
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    redirect: '/rooms',
    children: [
      { path: 'rooms', component: () => import('@/views/room/RoomList.vue') },
      { path: 'beds/:roomId', component: () => import('@/views/bed/BedList.vue'), props: true },

      { path: 'diet', component: () => import('@/views/diet/DietManagement.vue') },

      { path: 'residents', component: () => import('@/views/resident/ResidentRegistration.vue') },
      { path: 'health', component: () => import('@/views/resident/HealthRecords.vue') },

      { path: 'health-monitoring/daily', component: () => import('@/views/resident/HealthMonitoring.vue') },
      { path: 'health-monitoring/visualization', component: () => import('@/views/resident/HealthMonitoringVisualization.vue') },
      { path: 'health-monitoring', redirect: 'health-monitoring/daily' },

      { path: 'history', component: () => import('@/views/resident/ResidentHistory.vue') },

      { path: 'service', component: () => import('@/views/service/ServicePackages.vue') },
      { path: 'purchase', component: () => import('@/views/service/PurchaseRecords.vue') },

      { path: 'nursing/levels', component: () => import('@/views/nursing/NursingLevels.vue') },
      { path: 'nursing/contents', component: () => import('@/views/nursing/NursingContents.vue') },
      { path: 'nursing/records', component: () => import('@/views/nursing/NursingRecords.vue') }
    ]
  },

  /* ================= 老人端 ================= */
  {
    path: '/elder',
    component: () => import('@/views/elder/ElderLayout.vue'),
    meta: { requiresAuth: true, role: 'elder' },
    redirect: '/elder/home',
    children: [
      { path: 'home', component: () => import('@/views/elder/ElderHome.vue') },
      { path: 'profile', component: () => import('@/views/elder/Profile.vue') },
      { path: 'diet-calendar', component: () => import('@/views/elder/DietCalendar.vue') },

      { path: 'health-monitoring/daily', component: () => import('@/views/elder/HealthMonitoringDaily.vue') },
      { path: 'health-monitoring', redirect: 'health-monitoring/daily' },

      { path: 'nursing/levels', component: () => import('@/views/elder/NursingLevels.vue') },
      { path: 'nursing/contents', component: () => import('@/views/elder/NursingContents.vue') },
      { path: 'nursing/records', component: () => import('@/views/elder/NursingRecords.vue') },

      { path: 'service/packages', component: () => import('@/views/elder/ServicePackages.vue') },
      { path: 'service/purchase', component: () => import('@/views/elder/PurchaseRecords.vue') },

      /* ⭐ AI 聊天页面（重点） */
      {
        path: 'ai-chat',
        name: 'ElderAiChat',
        component: () => import('@/views/elder/AiChat.vue')
      }
    ]
  },

  /* ================= 兜底 ================= */
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/* ================= 路由守卫 ================= */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userType = localStorage.getItem('userType') // admin / elder
  const requiresAuth = to.matched.some(r => r.meta.requiresAuth)

  // 未登录但访问受保护页面
  if (requiresAuth && !token) {
    return next('/login')
  }

  // 已登录访问登录页
  if (to.path === '/login' && token) {
    return userType === 'elder'
      ? next('/elder/home')
      : next('/rooms')
  }

  // 角色校验
  const routeRole = to.matched.find(r => r.meta.role)?.meta.role
  if (routeRole && routeRole !== userType) {
    return next('/login')
  }

  next()
})

export default router
