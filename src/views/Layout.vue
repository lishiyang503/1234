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

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧侧边栏 -->
      <div 
        class="sidebar" 
        :class="{ 
          'sidebar-expanded': isSidebarHovered || isSidebarPinned, 
          'sidebar-pinned': isSidebarPinned 
        }"
        @mouseenter="handleSidebarMouseEnter"
        @mouseleave="handleSidebarMouseLeave"
      >
        <div class="sidebar-pin" :class="{ 'pinned': isSidebarPinned }" @click="toggleSidebarPin" :title="isSidebarPinned ? '取消固定' : '固定侧边栏'">
          <div class="pin-icon"></div>
        </div>
        <div class="sidebar-header">
          <h2 class="sidebar-title">功能菜单</h2>
          
        </div>
        
        <div class="sidebar-menu">
          <!-- 房间/床位管理 -->
          <div class="menu-section">
            <h3 class="menu-section-title">房间/床位管理</h3>
            <div 
              class="menu-item" 
              :class="{ active: $route.path.includes('/rooms') }"
              @click="navigateTo('/rooms')"
            >
              <el-icon><House /></el-icon>
              <span>房间列表</span>
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
import { ArrowDown } from '@element-plus/icons-vue'
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

const navigateTo = (path) => {
  router.push(path)
}

const handleLogout = () => {
  // 清除localStorage
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 侧边栏相关函数
const handleSidebarMouseEnter = () => {
  if (isSidebarPinned.value) return
  isSidebarHovered.value = true
}

const handleSidebarMouseLeave = () => {
  if (isSidebarPinned.value) return
  isSidebarHovered.value = false
}

const toggleSidebarPin = () => {
  isSidebarPinned.value = !isSidebarPinned.value
  // 保存状态到localStorage
  localStorage.setItem('sidebarPinned', isSidebarPinned.value.toString())
}

// 健康监测子菜单展开/收起
const toggleHealth = () => {
  healthOpen.value = !healthOpen.value
}

// 路由命中时自动展开健康监测
watch(
  () => router.currentRoute.value.path,
  (path) => {
    if (path.startsWith('/health-monitoring')) {
      healthOpen.value = true
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  overflow: hidden;
}

.top-navbar {
  height: 80px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  z-index: 100;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.navbar-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.brand-section {
  display: flex;
  flex-direction: column;
  margin-left: 0;
}

.user-info {
  margin-right: 0;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 32px;
}

.logo-icon img {
  width: 32px;
  height: 32px;
}

.brand-name {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.brand-subtitle {
  display: none;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .brand-section {
    margin-left: 10px;
  }
  
  .brand-name {
    font-size: 18px;
  }
  
  .logo-icon {
    font-size: 28px;
  }
  
  .logo-icon img {
    width: 28px;
    height: 28px;
  }
}

@media (max-width: 480px) {
  .brand-section {
    margin-left: 5px;
  }
  
  .brand-name {
    font-size: 16px;
  }
  
  .logo-icon {
    font-size: 24px;
  }
  
  .logo-icon img {
    width: 24px;
    height: 24px;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  display: none;
}

.dropdown-icon {
  font-size: 18px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.3s ease;
}

.dropdown-icon:hover {
  color: var(--primary-color);
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 60px;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  padding: 24px 8px;
  overflow-y: auto;
  position: relative;
  z-index: 100;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

/* 折叠状态：收紧整体上下空间 */
.sidebar:not(.sidebar-expanded) {
  padding: 6px 4px;
}

/* 折叠态：header 不占任何空间 */
.sidebar:not(.sidebar-expanded) .sidebar-header {
  display: none;
}

/* 折叠状态下菜单项更紧凑 */
.sidebar:not(.sidebar-expanded) .menu-item {
  padding: 0;
  justify-content: center;
  border-radius: 6px;
  height: 40px;
}

/* 折叠态下完全隐藏菜单组标题 */
.sidebar:not(.sidebar-expanded) .menu-section-title {
  display: none;
}

/* 折叠态下防止submenu结构撑开menu-item */
.sidebar:not(.sidebar-expanded) .menu-item.has-submenu {
  margin-bottom: 0;
}

/* 折叠态减少菜单组之间的空隙 */
.sidebar:not(.sidebar-expanded) .menu-section {
  margin-bottom: 4px;
}

/* 折叠态 hover 不要"跳太大" */
.sidebar:not(.sidebar-expanded) .menu-item:hover {
  background-color: #f3f6f8;
}

/* 折叠态 active 不要左边框（会显胖） */
.sidebar:not(.sidebar-expanded) .menu-item.active {
  border-left: none;
  background-color: #e9f2fb;
}

.sidebar.sidebar-expanded {
  width: 280px;
  padding: 24px;
}

/* 固定状态的侧边栏样式 */
.sidebar.sidebar-pinned {
  box-shadow: inset -1px 0 0 #dbe3ea;
}

/* Pin按钮样式 */
.sidebar-pin {
  position: absolute;
  top: 20px;
  right: 12px;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid transparent;
  background: transparent;
  cursor: pointer;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 101;
}

.sidebar.sidebar-expanded .sidebar-pin {
  opacity: 0.6;
  visibility: visible;
}

.sidebar-pin:hover {
  opacity: 1;
  background: #f2f6f9;
}

.sidebar-pin.pinned {
  border-color: #b9c8d3;
  background: #eef4f8;
  opacity: 1;
}

/* Pin图标样式 */
.pin-icon {
  width: 14px;
  height: 14px;
  background: currentColor;
  mask: url('data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 24 24%22%3E%3Cpath d=%22M12 17c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm5-6h-10l2-6 2 2 2-2 2 6z%22/%3E%3C/svg%3E') no-repeat center / contain;
  transition: transform 0.2s ease;
}

.sidebar-pin.pinned .pin-icon {
  transform: rotate(-45deg);
}



.sidebar-header {
  margin-bottom: 32px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.sidebar.sidebar-expanded .sidebar-header {
  opacity: 1;
  visibility: visible;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: relative;
  overflow: visible;
}

/* 垂直子菜单样式 */
/* 可展开菜单项样式 */
.menu-item.expandable {
  flex-direction: column;
  align-items: stretch;
  padding: 0;
  border-radius: 8px;
  overflow: hidden;
}

.menu-item.expandable .menu-item-main {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.menu-item.expandable .menu-item-main:hover {
  background-color: var(--bg-hover);
  color: var(--primary-color);
}

.menu-item.expandable .arrow {
  margin-left: auto;
  font-size: 14px;
  color: #9aa4af;
  transition: transform 0.25s ease;
}

.menu-item.expandable.open .arrow {
  transform: rotate(180deg);
}

/* 垂直子菜单样式 */
.submenu-vertical {
  padding-left: 44px;
  margin-top: 4px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.menu-item.expandable.open .submenu-vertical {
  max-height: 200px;
}

.submenu-vertical .submenu-item {
  padding: 8px 12px;
  font-size: 14px;
  color: #6b7280;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  min-width: 180px;
  text-align: left;
  margin: 0;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 8px;
}

.submenu-vertical .submenu-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.submenu-vertical .submenu-item.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: 600;
}

/* 折叠态下不显示子菜单 */
.sidebar:not(.sidebar-expanded) .submenu-vertical {
  display: none;
}

.sidebar.sidebar-expanded .sidebar-menu {
  padding-right: 0;
}

.menu-section-title {
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.sidebar.sidebar-expanded .menu-section-title {
  opacity: 1;
  visibility: visible;
}

.menu-item span {
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  width: 0;
  overflow: hidden;
}

.sidebar.sidebar-expanded .menu-item span {
  opacity: 1;
  visibility: visible;
  width: auto;
  overflow: visible;
}

.menu-item-main span {
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  width: 0;
  overflow: hidden;
}

.sidebar.sidebar-expanded .menu-item-main span {
  opacity: 1;
  visibility: visible;
  width: auto;
  overflow: visible;
}

.submenu {
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.sidebar.sidebar-expanded .menu-item.has-submenu:hover .submenu {
  opacity: 1;
  visibility: visible;
}

.sidebar-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: var(--text-primary);
}

.sidebar-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.menu-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all 0.3s ease;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.menu-item:hover {
  background-color: var(--bg-hover);
  color: var(--primary-color);
  font-weight: 500;
}

.menu-item.active {
  background-color: var(--bg-hover);
  color: var(--primary-color);
  font-weight: 600;
  border-left: 3px solid var(--primary-color);
}

.menu-item el-icon {
  font-size: 18px;
  transition: color 0.3s ease;
}

/* 子菜单样式 */
.menu-item.has-submenu {
  position: relative;
  cursor: pointer;
}

.menu-item-main {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s ease;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  width: 100%;
}

.menu-item.has-submenu:hover .menu-item-main {
  background-color: var(--bg-hover);
  color: var(--primary-color);
  font-weight: 500;
}

.menu-item.has-submenu.active .menu-item-main {
  background-color: var(--bg-hover);
  color: var(--primary-color);
  font-weight: 600;
  border-left: 3px solid var(--primary-color);
}

.submenu {
  position: absolute;
  left: 100%;
  top: 0;
  min-width: 220px;
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 12px 0;
  margin-left: 12px;
  opacity: 0;
  visibility: hidden;
  transform: translateX(-12px);
  transition: all 0.3s ease;
  z-index: 9999;
  border: 1px solid var(--border-color);
}

/* 子菜单箭头 */
.submenu::before {
  content: '';
  position: absolute;
  left: -6px;
  top: 20px;
  width: 12px;
  height: 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-top: none;
  border-right: none;
  transform: rotate(-45deg);
}

/* 确保子菜单在屏幕内显示 */
.menu-item.has-submenu {
  position: relative;
  cursor: pointer;
}

.menu-item.has-submenu:hover .submenu {
  opacity: 1;
  visibility: visible;
  transform: translateX(0) scale(1);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* 确保侧边栏有足够的z-index */
.sidebar {
  position: relative;
  z-index: 100;
}

/* 调整子菜单项的宽度和样式 */
.submenu-item {
  padding: 12px 20px;
  color: #4a4a4a;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  min-width: 200px;
  text-align: left;
  border-radius: 8px;
  margin: 0 8px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 子菜单项装饰 */
.submenu-item::before {
  content: '';
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #d1d5db;
  transition: all 0.25s ease;
  opacity: 0;
  transform: scale(0.8);
}

.submenu-item:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
  font-weight: 500;
}

.submenu-item:hover::before {
  opacity: 1;
  transform: scale(1);
  background: var(--primary-color);
}

.submenu-item.active {
  background: var(--bg-hover);
  color: var(--primary-color);
  font-weight: 600;
}

.submenu-item.active::before {
  opacity: 1;
  transform: scale(1);
  background: var(--primary-color);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .submenu {
    right: auto;
    left: 0;
    top: 100%;
  }
  
  .submenu::before {
    right: auto;
    left: 20px;
  }
}



.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: var(--bg-primary);
  position: relative;
  z-index: 1;
}
</style>