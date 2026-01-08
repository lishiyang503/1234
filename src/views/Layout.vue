<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-content">
        <div class="brand-section">
          <div class="brand-logo">
            <div class="logo-icon">
              <svg width="40" height="40" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                <!-- 渐变背景 -->
                <defs>
                  <linearGradient id="skyGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" stop-color="#FF8C00" />
                    <stop offset="100%" stop-color="#FFA500" />
                  </linearGradient>
                  <linearGradient id="sunGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stop-color="#FF4500" />
                    <stop offset="100%" stop-color="#DC143C" />
                  </linearGradient>
                  <linearGradient id="mountainGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" stop-color="#696969" />
                    <stop offset="100%" stop-color="#808080" />
                  </linearGradient>
                </defs>
                <!-- 背景 -->
                <circle cx="50" cy="50" r="45" fill="url(#skyGradient)" />
                <!-- 太阳光芒 -->
                <g fill="#FFD700" stroke="#FFD700" stroke-width="2">
                  <path d="M50 10 L50 20" />
                  <path d="M50 80 L50 90" />
                  <path d="M10 50 L20 50" />
                  <path d="M80 50 L90 50" />
                  <path d="M22 22 L32 32" />
                  <path d="M68 68 L78 78" />
                  <path d="M22 78 L32 68" />
                  <path d="M68 32 L78 22" />
                </g>
                <!-- 像手一样的大山 -->
                <path d="M20 80 Q35 65 50 70 Q65 65 80 80 L90 100 L10 100 Z" fill="url(#mountainGradient)" />
                <!-- 缓缓落下的夕阳 -->
                <circle cx="50" cy="65" r="15" fill="url(#sunGradient)" />
              </svg>
            </div>
            <h1 class="brand-name">东软颐养中心</h1>
          </div>
        </div>
        
        <div class="user-info">
          <el-avatar :size="40" src="https://api.dicebear.com/7.x/avataaars/svg?seed=admin" />
          <div class="user-details">
            <span class="user-name">{{ greeting }}, {{ username || '管理员' }}</span>
          </div>
          <el-dropdown trigger="click">
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧侧边栏 -->
      <div 
        class="sidebar" 
        :class="{ hovered: isSidebarHovered, pinned: isSidebarPinned }"
        @mouseenter="isSidebarHovered = true"
        @mouseleave="isSidebarHovered = false"
      >
        <!-- 侧边栏顶部 -->
        <div class="sidebar-header">
          <div class="logo-small">
            <svg width="24" height="24" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
              <circle cx="50" cy="50" r="45" fill="url(#skyGradient)" />
              <circle cx="50" cy="65" r="15" fill="url(#sunGradient)" />
            </svg>
          </div>
          <h2 class="sidebar-title">东软颐养</h2>
          <button 
            class="pin-button" 
            :class="{ pinned: isSidebarPinned }"
            @click="toggleSidebarPin"
            title="固定侧边栏"
          >
            <el-icon>
              <svg v-if="isSidebarPinned" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 1024 1024"><path fill="currentColor" d="m698.304 256-55.68 60.352l100.352 107.584-181.248 189.76l-156.16-155.392L315.136 640l239.68 250.24l110.592-115.2l165.12-175.616l60.352-64.896l36.672-39.424V302.336z"></path></svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 1024 1024"><path fill="currentColor" d="M928 472H856v288.064l-70.336-70.336l-265.152 265.152l-194.56-194.56l265.216-265.28L440 320H80v368h48v160h832V472h-72zm-720-200h272v160H208V272z"></path></svg>
            </el-icon>
          </button>
        </div>
        
        <!-- 侧边栏菜单 -->
        <div class="sidebar-menu">
          <!-- 首页 -->
          <div 
            class="menu-item" 
            :class="{ active: $route.path === '/' }"
            @click="navigateTo('/')"
          >
            <el-icon><House /></el-icon>
            <span>首页</span>
          </div>
          
          <!-- 房间床位管理 -->
          <div class="menu-section">
            <h3 class="menu-section-title">房间床位管理</h3>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/rooms') }"
              @click="navigateTo('/rooms')"
            >
              <el-icon><House /></el-icon>
              <span>房间列表</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/beds') }"
              @click="navigateTo('/beds')"
            >
              <el-icon><Document /></el-icon>
              <span>床位列表</span>
            </div>
          </div>
          
          <!-- 膳食管理 -->
          <div class="menu-section">
            <h3 class="menu-section-title">膳食管理</h3>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/diet') }"
              @click="navigateTo('/diet')"
            >
              <el-icon><Food /></el-icon>
              <span>膳食需求</span>
            </div>
          </div>
          
          <!-- 档案管理 -->
          <div class="menu-section">
            <h3 class="menu-section-title">档案管理</h3>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/residents') }"
              @click="navigateTo('/residents')"
            >
              <el-icon><User /></el-icon>
              <span>入住登记</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/health') }"
              @click="navigateTo('/health')"
            >
              <el-icon><Document /></el-icon>
              <span>健康档案</span>
            </div>
            <!-- 健康监测（可展开） -->
            <div class="menu-item expandable" :class="{ open: healthOpen, active: $route.path.includes('/health-monitoring') }">
              <div class="menu-item-main" @click="toggleHealth">
                <el-icon><Monitor /></el-icon>
                <span>健康监测</span>
                <el-icon class="arrow">
                  <ArrowDown />
                </el-icon>
              </div>

              <div class="submenu-vertical">
                <div 
                  class="submenu-item" 
                  :class="{ active: $route.path === '/health-monitoring/daily' }"
                  @click="navigateTo('/health-monitoring/daily')"
                >
                  日常数据录入
                </div>
                <div 
                  class="submenu-item" 
                  :class="{ active: $route.path === '/health-monitoring/visualization' }"
                  @click="navigateTo('/health-monitoring/visualization')"
                >
                  可视化监测数据图
                </div>
              </div>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/history') }"
              @click="navigateTo('/history')"
            >
              <el-icon><Clock /></el-icon>
              <span>入住历史</span>
            </div>
          </div>
          
          <!-- 服务管理 -->
          <div class="menu-section">
            <h3 class="menu-section-title">服务管理</h3>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/service') }"
              @click="navigateTo('/service')"
            >
              <el-icon><Service /></el-icon>
              <span>服务套餐</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/purchase') }"
              @click="navigateTo('/purchase')"
            >
              <el-icon><ShoppingCart /></el-icon>
              <span>购买记录</span>
            </div>
          </div>
          
          <!-- 护理管理 -->
          <div class="menu-section">
            <h3 class="menu-section-title">护理管理</h3>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/nursing/levels') }"
              @click="navigateTo('/nursing/levels')"
            >
              <el-icon><Document /></el-icon>
              <span>护理级别管理</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/nursing/contents') }"
              @click="navigateTo('/nursing/contents')"
            >
              <el-icon><List /></el-icon>
              <span>护理内容管理</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/nursing/records') }"
              @click="navigateTo('/nursing/records')"
            >
              <el-icon><Calendar /></el-icon>
              <span>护理记录管理</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区域 -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
    
    <!-- 护理管理悬浮窗 -->
    <FloatingNursingPanel />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import FloatingNursingPanel from '@/components/FloatingNursingPanel.vue'
import {
  House,
  Food,
  User,
  Document,
  Monitor,
  Clock,
  Service,
  ShoppingCart,
  List,
  Calendar,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const username = ref('')
const role = ref('')
const greeting = ref('')
const isSidebarHovered = ref(false)
const isSidebarPinned = ref(false)
const healthOpen = ref(false)

// 计算问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

onMounted(() => {
  // 从localStorage获取用户信息
  username.value = localStorage.getItem('username') || '管理员'
  role.value = localStorage.getItem('role') || '系统管理员'
  // 从localStorage获取侧边栏固定状态
  isSidebarPinned.value = localStorage.getItem('sidebarPinned') === 'true'
  // 设置问候语
  greeting.value = getGreeting()
  // 每分钟更新一次问候语
  setInterval(() => {
    greeting.value = getGreeting()
  }, 60000)
})

// 固定/取消固定侧边栏
const toggleSidebarPin = () => {
  isSidebarPinned.value = !isSidebarPinned.value
  localStorage.setItem('sidebarPinned', isSidebarPinned.value)
}

// 导航到指定路由
const navigateTo = (path) => {
  router.push(path)
}

// 退出登录
const handleLogout = () => {
  // 清除localStorage
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('sidebarPinned')
  // 跳转到登录页
  router.push('/login')
  ElMessage.success('已成功退出登录')
}

// 切换健康监测子菜单
const toggleHealth = () => {
  healthOpen.value = !healthOpen.value
}
</script>

<style scoped>
/* 全局样式 */
.layout-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 顶部导航栏 */
.top-navbar {
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 100;
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* 品牌标识 */
.brand-section {
  display: flex;
  align-items: center;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon svg {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  transition: transform 0.3s ease;
}

.logo-icon:hover svg {
  transform: scale(1.1);
}

.brand-name {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.brand-name:hover {
  transform: translateY(-1px);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.user-name {
  font-weight: 500;
  font-size: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-role {
  font-size: 12px;
  opacity: 0.8;
}

.dropdown-icon {
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dropdown-icon:hover {
  transform: translateY(2px);
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 70px;
  background: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.sidebar.hovered, .sidebar.pinned {
  width: 220px;
}

/* 侧边栏头部 */
.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.logo-small {
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-title {
  margin: 0 10px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  opacity: 0;
  transition: opacity 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar.hovered .sidebar-title,
.sidebar.pinned .sidebar-title {
  opacity: 1;
}

.pin-button {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  font-size: 18px;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s ease;
  margin-left: auto;
  opacity: 0;
}

.sidebar.hovered .pin-button,
.sidebar.pinned .pin-button {
  opacity: 1;
}

.pin-button:hover {
  background: #f5f5f5;
  color: #667eea;
}

.pin-button.pinned {
  color: #667eea;
}

/* 侧边栏菜单 */
.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
}

/* 菜单分组标题 */
.menu-section {
  margin: 15px 0;
  padding: 0 15px;
}

.menu-section-title {
  font-size: 12px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0 0 10px 0;
  opacity: 0;
  transition: opacity 0.3s ease;
  white-space: nowrap;
}

.sidebar.hovered .menu-section-title,
.sidebar.pinned .menu-section-title {
  opacity: 1;
}

/* 菜单项 */
.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 0 10px;
  color: #666;
  position: relative;
  overflow: hidden;
}

.menu-item:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateX(5px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.menu-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.menu-item el-icon {
  font-size: 20px;
  margin-right: 15px;
  flex-shrink: 0;
}

.menu-item span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.sidebar.hovered .menu-item span,
.sidebar.pinned .menu-item span {
  opacity: 1;
}

/* 可展开菜单项 */
.menu-item.expandable {
  margin-bottom: 5px;
}

.menu-item-main {
  display: flex;
  align-items: center;
  width: 100%;
}

.menu-item-main .arrow {
  margin-left: auto;
  transition: transform 0.3s ease;
}

.menu-item.expandable.open .arrow {
  transform: rotate(180deg);
}

/* 垂直子菜单 */
.submenu-vertical {
  background: #f9f9f9;
  border-radius: 8px;
  margin: 5px 10px;
  overflow: hidden;
  max-height: 0;
  transition: max-height 0.3s ease;
}

.menu-item.expandable.open .submenu-vertical {
  max-height: 200px;
}

.submenu-item {
  padding: 10px 15px 10px 45px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.submenu-item:hover {
  background: #e8e8e8;
  color: #667eea;
}

.submenu-item.active {
  background: #667eea;
  color: white;
}

/* 内容区域 */
.content-area {
  flex: 1;
  overflow-y: auto;
  background: #f5f7fa;
  padding: 20px;
}

/* 滚动条样式 */
.content-area::-webkit-scrollbar {
  width: 8px;
}

.content-area::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.content-area::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.content-area::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: absolute;
    left: -220px;
    height: calc(100vh - 60px);
    z-index: 99;
    transition: left 0.3s ease;
  }
  
  .sidebar.hovered {
    left: 0;
  }
  
  .sidebar.pinned {
    left: 0;
    width: 70px;
  }
  
  .sidebar.pinned.hovered {
    width: 220px;
  }
  
  .content-area {
    padding: 10px;
  }
}
</style>