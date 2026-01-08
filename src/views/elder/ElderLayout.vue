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

          <!-- ✅ 唯一 AI 入口 -->
          <el-menu-item index="/elder/ai-chat">
            <el-icon class="menu-icon"><ChatLineRound /></el-icon>
            <span>智慧助手</span>
          </el-menu-item>

          <el-sub-menu index="/elder/health-monitoring">
            <template #title>
              <el-icon class="menu-icon"><DataAnalysis /></el-icon>
              <span>每日健康指标</span>
            </template>
            <el-menu-item index="/elder/health-monitoring/daily">
              日常健康检测
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="/elder/nursing">
            <template #title>
              <el-icon class="menu-icon"><Bell /></el-icon>
              <span>护理与服务管理</span>
            </template>
            <el-menu-item index="/elder/nursing/levels">护理等级</el-menu-item>
            <el-menu-item index="/elder/nursing/contents">护理内容</el-menu-item>
            <el-menu-item index="/elder/nursing/records">护理记录</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="/elder/service">
            <template #title>
              <el-icon class="menu-icon"><ShoppingCart /></el-icon>
              <span>生活服务管理</span>
            </template>
            <el-menu-item index="/elder/service/packages">
              服务套餐
            </el-menu-item>
            <el-menu-item index="/elder/service/purchase">
              购买记录
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <div class="sidebar-footer">
        <el-button type="primary" plain class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <div class="top-nav">
        <div class="nav-left">
          <h3>{{ currentPageTitle }}</h3>
        </div>

        <div class="nav-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar size="small">{{ userInitial }}</el-avatar>
              <span>{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  House,
  UserFilled,
  DataAnalysis,
  Bell,
  ShoppingCart,
  ArrowDown,
  SwitchButton,
  Calendar,
  ChatLineRound
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = ref('/elder/home')
const currentPageTitle = ref('首页')
const userName = ref('用户')

const userInitial = computed(() => userName.value.charAt(0))

const handleMenuSelect = (key) => {
  activeMenu.value = key
  updatePageTitle(key)
}

const updatePageTitle = (path) => {
  const map = {
    '/elder/home': '首页',
    '/elder/profile': '个人信息',
    '/elder/service/packages': '护理服务订购',
    '/elder/diet-calendar': '膳食日历',
    '/elder/health-monitoring/daily': '日常健康检测',
    '/elder/nursing/levels': '护理等级',
    '/elder/nursing/contents': '护理内容',
    '/elder/nursing/records': '护理记录',
    '/elder/service/purchase': '购买记录',
    '/elder/ai-chat': '智慧助手'
  }
  currentPageTitle.value = map[path] || '首页'
}

const handleLogout = () => {
  localStorage.clear()
  router.push('/login')
}

watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
    updatePageTitle(newPath)
  },
  { immediate: true }
)

onMounted(() => {
  const name = localStorage.getItem('username')
  if (name) userName.value = name
})
</script>

<style scoped>
.elder-layout {
  display: flex;
  height: 100vh;
}
.sidebar {
  width: 240px;
  background: #ffffff;
  border-right: 1px solid #eee;
}
.sidebar-header {
  padding: 20px;
  text-align: center;
}
.menu-icon {
  margin-right: 10px;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.top-nav {
  height: 60px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.content {
  flex: 1;
  padding: 20px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
