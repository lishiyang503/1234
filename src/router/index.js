import { createRouter, createWebHistory } from 'vue-router'

const routes = [
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
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由导航守卫
router.beforeEach((to, from, next) => {
  console.log('路由跳转:', from.path, '->', to.path)
  
  // 检查路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  // 获取token
  const token = localStorage.getItem('token')
  
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
})

export default router