<template>
  <div class="elder-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>东软颐养中心</h2>
        <p>老人服务端</p>
      </div>
      
      <div class="sidebar-menu">
        <el-menu
          :default-active="activeMenu"
          class="menu"
          router
          @select="handleMenuSelect"
        >
          <el-menu-item index="/elder/home">
            <el-icon class="menu-icon"><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <el-menu-item index="/elder/profile">
            <el-icon class="menu-icon"><UserFilled /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          
          <el-menu-item index="/elder/service/packages">
            <el-icon class="menu-icon"><ShoppingCart /></el-icon>
            <span>护理服务订购</span>
          </el-menu-item>
          
          <el-menu-item index="/elder/diet-calendar">
            <el-icon class="menu-icon"><Calendar /></el-icon>
            <span>膳食日历</span>
          </el-menu-item>
          

          
          <el-sub-menu index="/elder/health-monitoring">
            <template #title>
              <el-icon class="menu-icon"><DataAnalysis /></el-icon>
              <span>每日健康指标</span>
            </template>
            <el-menu-item index="/elder/health-monitoring/daily">
              <span>日常健康检测</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="/elder/nursing">
            <template #title>
              <el-icon class="menu-icon"><Bell /></el-icon>
              <span>护理与服务管理</span>
            </template>
            <el-menu-item index="/elder/nursing/levels">
              <span>护理等级管理</span>
            </el-menu-item>
            <el-menu-item index="/elder/nursing/contents">
              <span>护理内容管理</span>
            </el-menu-item>
            <el-menu-item index="/elder/nursing/records">
              <span>护理记录管理</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="/elder/service">
            <template #title>
              <el-icon class="menu-icon"><ShoppingCart /></el-icon>
              <span>生活服务管理</span>
            </template>
            <el-menu-item index="/elder/service/packages">
              <span>服务套餐管理</span>
            </el-menu-item>
            <el-menu-item index="/elder/service/purchase">
              <span>购买记录管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
      
      <div class="sidebar-footer">
        <el-button
          type="primary"
          plain
          @click="handleLogout"
          class="logout-btn"
        >
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-button>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="top-nav">
        <div class="nav-left">
          <el-button
            type="text"
            class="toggle-btn"
            @click="toggleSidebar"
          >
            <el-icon><Menu /></el-icon>
          </el-button>
          <h3>{{ currentPageTitle }}</h3>
        </div>
        <div class="nav-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar size="small" class="user-avatar">
                {{ userInitial }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  User,
  UserFilled,
  Reading,
  DataAnalysis,
  Bell,
  ShoppingCart,
  Menu,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const sidebarCollapsed = ref(false)
const activeMenu = ref('/elder/home')
const currentPageTitle = ref('首页')
const userName = ref('用户')

const userInitial = computed(() => {
  return userName.value.charAt(0).toUpperCase()
})

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const handleMenuSelect = (key) => {
  activeMenu.value = key
  updatePageTitle(key)
}

const updatePageTitle = (path) => {
  const titleMap = {
    '/elder/home': '首页',
    '/elder/profile': '个人信息',
    '/elder/service/packages': '护理服务订购',
    '/elder/diet-calendar': '膳食日历',
    '/elder/health-monitoring/daily': '日常健康检测',
    '/elder/health-monitoring/visualization': '健康数据可视化',
    '/elder/nursing/levels': '护理等级管理',
    '/elder/nursing/contents': '护理内容管理',
    '/elder/nursing/records': '护理记录管理',
    '/elder/service/purchase': '购买记录管理'
  }
  currentPageTitle.value = titleMap[path] || '首页'
}

const handleProfile = () => {
  ElMessage.info('个人中心功能开发中')
}

const handleLogout = () => {
  // 清除本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('userType')
  localStorage.removeItem('phone')
  
  // 跳转到登录页面
  router.push('/login')
  ElMessage.success('退出登录成功')
}

onMounted(() => {
  // 获取用户信息
  const storedUsername = localStorage.getItem('username')
  if (storedUsername) {
    userName.value = storedUsername
  }
  
  // 初始化当前菜单和页面标题
  activeMenu.value = route.path
  updatePageTitle(route.path)
})
</script>

<style scoped>
.elder-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 250px;
  background: var(--sidebar-bg);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 30px 20px;
  border-bottom: 1px solid var(--border-color);
  text-align: center;
}

.sidebar-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.sidebar-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.menu {
  border-right: none;
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.logout-btn {
  width: 100%;
  justify-content: center;
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--bg-primary);
}

/* 顶部导航栏 */
.top-nav {
  height: 60px;
  background: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.toggle-btn {
  font-size: 20px;
  color: var(--text-primary);
}

.nav-left h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.nav-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: var(--border-radius);
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background-color: var(--hover-bg);
}

.user-avatar {
  background-color: var(--primary-color);
  color: white;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.arrow-icon {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 内容区域 */
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: var(--bg-primary);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .sidebar.show {
    transform: translateX(0);
  }
  
  .main-content {
    width: 100%;
  }
}
</style>