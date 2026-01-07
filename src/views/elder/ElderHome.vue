<template>
  <div class="elder-home">
    <div class="home-header">
      <h1>欢迎使用东软颐养中心</h1>
      <p>为您提供全方位的养老服务管理</p>
    </div>

    <div class="home-stats">
      <el-card shadow="hover" class="stat-card">
        <el-icon class="stat-icon"><HomeFilled /></el-icon>
        <div class="stat-content">
          <h3>舒适居住环境</h3>
          <p>提供温馨舒适的居住空间</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <el-icon class="stat-icon"><Medal /></el-icon>
        <div class="stat-content">
          <h3>专业健康管理</h3>
          <p>定期健康检测与档案管理</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <el-icon class="stat-icon"><KnifeFork /></el-icon>
        <div class="stat-content">
          <h3>营养膳食服务</h3>
          <p>个性化营养配餐方案</p>
        </div>
      </el-card>

      <el-card shadow="hover" class="stat-card">
        <el-icon class="stat-icon"><UserFilled /></el-icon>
        <div class="stat-content">
          <h3>专业护理服务</h3>
          <p>24小时贴心护理照顾</p>
        </div>
      </el-card>
    </div>

    <div class="home-features">
      <h2>服务功能</h2>
      <div class="feature-grid">
        <el-card
          shadow="hover"
          class="feature-card"
          @click="navigateTo('/elder/health-monitoring')"
        >
          <el-icon class="feature-icon"><DataAnalysis /></el-icon>
          <h3>健康检测管理</h3>
          <p>记录和分析健康检测数据</p>
        </el-card>

        <el-card
          shadow="hover"
          class="feature-card"
          @click="navigateTo('/elder/nursing')"
        >
          <el-icon class="feature-icon"><Bell /></el-icon>
          <h3>护理与服务管理</h3>
          <p>管理护理内容和服务记录</p>
        </el-card>

        <el-card
          shadow="hover"
          class="feature-card"
          @click="navigateTo('/elder/service')"
        >
          <el-icon class="feature-icon"><ShoppingCart /></el-icon>
          <h3>生活服务管理</h3>
          <p>管理生活服务和购买记录</p>
        </el-card>
      </div>
    </div>

    <div class="home-notices">
      <h2>最新通知</h2>
      <el-card shadow="hover">
        <el-timeline>
          <el-timeline-item
            v-for="(notice, index) in notices"
            :key="index"
            :timestamp="notice.time"
            placement="top"
          >
            <el-card>
              <h4>{{ notice.title }}</h4>
              <p>{{ notice.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeFilled,
  Medal,
  KnifeFork,
  UserFilled,
  Document,
  DataAnalysis,
  Bell,
  ShoppingCart
} from '@element-plus/icons-vue'

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}

const notices = ref([
  {
    title: '中心活动通知',
    content: '本周六将举办健康讲座，欢迎各位老人参加。',
    time: '2026-01-06'
  },
  {
    title: '服务升级通知',
    content: '健康检测系统已升级，新增多项检测功能。',
    time: '2026-01-05'
  },
  {
    title: '温馨提示',
    content: '近期天气变化较大，请各位老人注意保暖。',
    time: '2026-01-04'
  }
])
</script>

<style scoped>
.elder-home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.home-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
  background: linear-gradient(135deg, var(--primary-lighter) 0%, var(--bg-primary) 100%);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-md);
}

.home-header h1 {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.home-header p {
  font-size: 18px;
  color: var(--text-secondary);
  margin: 0;
}

.home-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: var(--border-radius);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}

.stat-icon {
  font-size: 48px;
  color: var(--primary-color);
  margin-right: 20px;
}

.stat-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.stat-content p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.home-features {
  margin-bottom: 40px;
}

.home-features h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.feature-card {
  padding: 30px;
  border-radius: var(--border-radius);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid var(--border-color);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

.feature-icon {
  font-size: 48px;
  color: var(--primary-color);
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.feature-card p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.home-notices {
  margin-bottom: 40px;
}

.home-notices h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .home-header h1 {
    font-size: 28px;
  }

  .home-header p {
    font-size: 16px;
  }

  .home-stats {
    grid-template-columns: 1fr;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }
}
</style>