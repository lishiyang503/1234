import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  
  // // 管理端路由
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    redirect: 'rooms',
    children: [
      // 房间/床位管理
      {
        path: 'rooms',
        name: 'RoomList',
        component: () => import('@/views/room/RoomList.vue')
      },
      {
        path: 'beds/:roomId',
        name: 'BedList',
        component: () => import('@/views/bed/BedList.vue'),
        props: true
      },
      
      // 膳食管理
      {
        path: 'diet',
        name: 'DietManagement',
        component: () => import('@/views/diet/DietManagement.vue')
      },
      
      // 档案管理
      {
        path: 'residents',
        name: 'ResidentRegistration',
        component: () => import('@/views/resident/ResidentRegistration.vue')
      },
      {
        path: 'health',
        name: 'HealthRecords',
        component: () => import('@/views/resident/HealthRecords.vue')
      },
      {
        path: 'health-monitoring/daily',
        name: 'HealthMonitoringDaily',
        component: () => import('@/views/resident/HealthMonitoring.vue')
      },
      {
        path: 'health-monitoring/visualization',
        name: 'HealthMonitoringVisualization',
        component: () => import('@/views/resident/HealthMonitoringVisualization.vue')
      },
      {
        path: 'health-monitoring',
        redirect: 'health-monitoring/daily'
      },
      {
        path: 'history',
        name: 'ResidentHistory',
        component: () => import('@/views/resident/ResidentHistory.vue')
      },
      
      // 服务管理
      {
        path: 'service',
        name: 'ServicePackages',
        component: () => import('@/views/service/ServicePackages.vue')
      },
      {
        path: 'purchase',
        name: 'PurchaseRecords',
        component: () => import('@/views/service/PurchaseRecords.vue')
      },
      
      // 护理管理
      {
        path: 'nursing/levels',
        name: 'NursingLevels',
        component: () => import('@/views/nursing/NursingLevels.vue')
      },
      {
        path: 'nursing/contents',
        name: 'NursingContents',
        component: () => import('@/views/nursing/NursingContents.vue')
      },
      {
        path: 'nursing/records',
        name: 'NursingRecords',
        component: () => import('@/views/nursing/NursingRecords.vue')
      }
    ]
  },
  
  // 老人端路由
  {
    path: '/elder',
    component: () => import('@/views/elder/ElderLayout.vue'),
    meta: { requiresAuth: true, role: 'elder' },
    redirect: 'home',
    children: [
      {
        path: 'home',
        name: 'ElderHome',
        component: () => import('@/views/elder/ElderHome.vue')
      },
      {
        path: 'profile',
        name: 'ElderProfile',
        component: () => import('@/views/elder/Profile.vue')
      },
      {
        path: 'diet-calendar',
        name: 'ElderDietCalendar',
        component: () => import('@/views/elder/DietCalendar.vue')
      },
      
      
      
      {
        path: 'health-monitoring/daily',
        name: 'ElderHealthMonitoringDaily',
        component: () => import('@/views/elder/HealthMonitoringDaily.vue')
      },
      
      {
        path: 'health-monitoring',
        redirect: 'health-monitoring/daily'
      },
      {
        path: 'nursing/levels',
        name: 'ElderNursingLevels',
        component: () => import('@/views/elder/NursingLevels.vue')
      },
      {
        path: 'nursing/contents',
        name: 'ElderNursingContents',
        component: () => import('@/views/elder/NursingContents.vue')
      },
      {
        path: 'nursing/records',
        name: 'ElderNursingRecords',
        component: () => import('@/views/elder/NursingRecords.vue')
      },
      {
        path: 'service/packages',
        name: 'ElderServicePackages',
        component: () => import('@/views/elder/ServicePackages.vue')
      },
      {
        path: 'service/purchase',
        name: 'ElderPurchaseRecords',
        component: () => import('@/views/elder/PurchaseRecords.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// // 路由导航守卫
// router.beforeEach((to, from, next) => {
//   console.log('路由跳转:', from.path, '->', to.path)

//   const token = localStorage.getItem('token')
//   const userType = localStorage.getItem('userType') // 必须是 'admin' / 'elder'
//   const requiresAuth = to.matched.some(r => r.meta.requiresAuth)

//   // 1. 需要登录但没 token
//   if (requiresAuth && !token) {
//     return next('/login')
//   }

//   // 2. 已登录访问登录页
//   if (to.path === '/login' && token) {
//     return userType === 'elder'
//       ? next('/elder/home')
//       : next('/rooms')
//   }

//   // 3. 角色校验（只校验一次）
//   const routeRole = to.matched
//     .map(r => r.meta.role)
//     .find(Boolean)

//   // 根据路由的role要求和用户的userType进行校验
//   if (routeRole === 'admin' && userType !== 'admin') {
//     return next('/login')
//   }
//   if (routeRole === 'elder' && userType !== 'elder') {
//     return next('/login')
//   }

//   next()
// })

export default router