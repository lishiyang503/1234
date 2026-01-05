<template>
  <div class="floating-nursing-panel">
    <div class="panel-toggle" @click="togglePanel">
      <el-icon><House /></el-icon>
      <span v-if="isPanelOpen">收起</span>
      <span v-else>护理面板</span>
    </div>
    
    <div class="panel-content" v-if="isPanelOpen">
      <div class="panel-header">
        <h3>快速护理管理</h3>
        <el-button 
          type="text" 
          @click="togglePanel"
          class="close-btn"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      
      <div class="panel-body">
        <!-- 护理级别快速入口 -->
        <div class="quick-entry">
          <h4><el-icon><Rank /></el-icon>护理级别</h4>
          <div class="quick-stats">
            <div class="stat-item">
              <span class="stat-label">基础护理</span>
              <span class="stat-value">{{ basicLevelCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">中级护理</span>
              <span class="stat-value">{{ mediumLevelCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">高级护理</span>
              <span class="stat-value">{{ advancedLevelCount }}</span>
            </div>
          </div>
          <el-button 
            type="primary" 
            size="small"
            @click="navigateTo('/nursing/levels')"
          >
            管理
          </el-button>
        </div>
        
        <!-- 护理内容快速入口 -->
        <div class="quick-entry">
          <h4><el-icon><Document /></el-icon>护理内容</h4>
          <div class="quick-stats">
            <div class="stat-item">
              <span class="stat-label">日常护理</span>
              <span class="stat-value">{{ dailyContentCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">医疗护理</span>
              <span class="stat-value">{{ medicalContentCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">心理护理</span>
              <span class="stat-value">{{ mentalContentCount }}</span>
            </div>
          </div>
          <el-button 
            type="primary" 
            size="small"
            @click="navigateTo('/nursing/contents')"
          >
            管理
          </el-button>
        </div>
        
        <!-- 护理记录快速入口 -->
        <div class="quick-entry">
          <h4><el-icon><EditPen /></el-icon>护理记录</h4>
          <div class="quick-stats">
            <div class="stat-item">
              <span class="stat-label">待完成</span>
              <span class="stat-value stat-warning">{{ pendingRecordCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已完成</span>
              <span class="stat-value stat-success">{{ completedRecordCount }}</span>
            </div>
          </div>
          <el-button 
            type="primary" 
            size="small"
            @click="navigateTo('/nursing/records')"
          >
            管理
          </el-button>
        </div>
        
        <!-- 快捷操作按钮 -->
        <div class="quick-actions">
          <el-button 
            type="success" 
            plain
            @click="addNewRecord"
          >
            <el-icon><Plus /></el-icon>
            新增护理记录
          </el-button>
          <el-button 
            type="warning" 
            plain
            @click="navigateTo('/nursing/records?status=pending')"
          >
            <el-icon><Check /></el-icon>
            处理待办记录
          </el-button>
        </div>
      </div>
      
      <div class="panel-footer">
        <span class="update-time">
          <el-icon><Timer /></el-icon>
          数据更新于: {{ lastUpdateTime }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNursingLevels, getNursingContents, getNursingRecords } from '@/api/nursing'

const router = useRouter()

// 面板状态
const isPanelOpen = ref(false)

// 统计数据
const basicLevelCount = ref(0)
const mediumLevelCount = ref(0)
const advancedLevelCount = ref(0)
const dailyContentCount = ref(0)
const medicalContentCount = ref(0)
const mentalContentCount = ref(0)
const pendingRecordCount = ref(0)
const completedRecordCount = ref(0)
const lastUpdateTime = ref('')

// 更新数据
const updateStats = async () => {
  try {
    // 获取护理级别统计
    const levelsRes = await getNursingLevels({ page: 1, pageSize: 100 })
    if (levelsRes.data.success) {
      const levels = levelsRes.data.data.list
      basicLevelCount.value = levels.filter(l => l.level === 1).length
      mediumLevelCount.value = levels.filter(l => l.level === 2).length
      advancedLevelCount.value = levels.filter(l => l.level === 3).length
    }
    
    // 获取护理内容统计
    const contentsRes = await getNursingContents({ page: 1, pageSize: 100 })
    if (contentsRes.data.success) {
      const contents = contentsRes.data.data.list
      dailyContentCount.value = contents.filter(c => c.category === 1).length
      medicalContentCount.value = contents.filter(c => c.category === 2).length
      mentalContentCount.value = contents.filter(c => c.category === 3).length
    }
    
    // 获取护理记录统计
    const recordsRes = await getNursingRecords({ page: 1, pageSize: 100 })
    if (recordsRes.data.success) {
      const records = recordsRes.data.data.list
      pendingRecordCount.value = records.filter(r => r.completed === 0).length
      completedRecordCount.value = records.filter(r => r.completed === 1).length
    }
    
    // 更新时间
    lastUpdateTime.value = new Date().toLocaleString('zh-CN')
  } catch (error) {
    console.error('更新护理统计数据失败:', error)
  }
}

// 切换面板
const togglePanel = () => {
  isPanelOpen.value = !isPanelOpen.value
}

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
  isPanelOpen.value = false
}

// 添加新记录
const addNewRecord = () => {
  router.push('/nursing/records?action=add')
  isPanelOpen.value = false
}

// 自动更新定时器
let updateTimer = null

onMounted(() => {
  updateStats()
  // 每5分钟自动更新一次数据
  updateTimer = setInterval(updateStats, 5 * 60 * 1000)
})

onUnmounted(() => {
  if (updateTimer) {
    clearInterval(updateTimer)
  }
})
</script>

<style scoped>
.floating-nursing-panel {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.panel-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: #409eff;
  color: white;
  border-radius: 25px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.3s ease;
  font-weight: 500;
}

.panel-toggle:hover {
  background: #66b1ff;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.5);
  transform: translateY(-2px);
}

.panel-content {
  width: 350px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  margin-top: 12px;
  animation: slideInRight 0.3s ease;
  overflow: hidden;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.panel-body {
  padding: 20px;
}

.quick-entry {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #e9ecef;
}

.quick-entry:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.quick-entry h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
}

.quick-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.stat-warning {
  color: #e6a23c;
}

.stat-success {
  color: #67c23a;
}

.quick-entry .el-button {
  width: 100%;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.quick-actions .el-button {
  flex: 1;
}

.panel-footer {
  padding: 12px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.update-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .panel-content {
    width: calc(100vw - 40px);
    max-width: 350px;
  }
  
  .floating-nursing-panel {
    bottom: 10px;
    right: 10px;
  }
}
</style>