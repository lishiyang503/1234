import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  /* ================= 登录 ================= */
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
<<<<<<< HEAD

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
=======
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      // 房间/床位管理
      {
        path: '/rooms',
        name: 'RoomList',
        component: () => import('@/views/room/RoomList.vue')
      },
      {
        path: '/beds/:roomId',
        name: 'BedList',
        component: () => import('@/views/bed/BedList.vue'),
        props: true
      },
      
      // 膳食管理
      {
        path: '/diet',
        name: 'DietManagement',
        component: () => import('@/views/diet/DietManagement.vue')
      },
      
      // 档案管理
      {
        path: '/residents',
        name: 'ResidentRegistration',
        component: () => import('@/views/resident/ResidentRegistration.vue')
      },
      {
        path: '/health',
        name: 'HealthRecords',
        component: () => import('@/views/resident/HealthRecords.vue')
      },
      {
        path: '/health-monitoring/daily',
        name: 'HealthMonitoringDaily',
        component: () => import('@/views/resident/HealthMonitoring.vue')
      },
      {
        path: '/health-monitoring/visualization',
        name: 'HealthMonitoringVisualization',
        component: () => import('@/views/resident/HealthMonitoringVisualization.vue')
      },
      {
        path: '/health-monitoring',
        redirect: '/health-monitoring/daily'
      },
      {
        path: '/history',
        name: 'ResidentHistory',
        component: () => import('@/views/resident/ResidentHistory.vue')
      },
      
      // 服务管理
      {
        path: '/service',
        name: 'ServicePackages',
        component: () => import('@/views/service/ServicePackages.vue')
      },
      {
        path: '/purchase',
        name: 'PurchaseRecords',
        component: () => import('@/views/service/PurchaseRecords.vue')
      },
      
      // 护理管理
      {
        path: '/nursing/levels',
        name: 'NursingLevels',
        component: () => import('@/views/nursing/NursingLevels.vue')
      },
      {
        path: '/nursing/contents',
        name: 'NursingContents',
        component: () => import('@/views/nursing/NursingContents.vue')
      },
      {
        path: '/nursing/records',
        name: 'NursingRecords',
        component: () => import('@/views/nursing/NursingRecords.vue')
      },
      // 健康监测可视化
      {
        path: '/monitor',
        name: 'Monitor',
        component: () => import('@/views/MonitorView.vue')
>>>>>>> 3ec801c29210fbb302c6ac605d1c36a871037984
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
<<<<<<< HEAD
  const token = localStorage.getItem('token')
  const userType = localStorage.getItem('userType') // admin / elder
  const requiresAuth = to.matched.some(r => r.meta.requiresAuth)

  // 未登录但访问受保护页面
=======
  console.log('路由跳转:', from.path, '->', to.path)
  
  // 检查路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  // 获取token
  const token = localStorage.getItem('token')
  
>>>>>>> 3ec801c29210fbb302c6ac605d1c36a871037984
  if (requiresAuth && !token) {
    // 需要认证但没有token，重定向到登录页面
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录但访问登录页面，重定向到首页
    next('/rooms')
  } else {
    // 正常跳转
    next()
  }
<<<<<<< HEAD

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
=======
>>>>>>> 3ec801c29210fbb302c6ac605d1c36a871037984
})

export default router
