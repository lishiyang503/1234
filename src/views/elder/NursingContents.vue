<template>
  <div class="elder-nursing-contents">
    <div class="page-header">
      <h2>护理内容管理</h2>
      <p>查看您的护理服务具体内容</p>
    </div>
    
    <!-- 护理内容分类 -->
    <div class="nursing-categories-section">
      <el-card class="categories-card">
        <template #header>
          <div class="card-header">
            <el-icon><DataAnalysis /></el-icon>
            <span>护理内容分类</span>
          </div>
        </template>
        <div class="categories-list">
          <el-button
            v-for="category in nursingCategories"
            :key="category.id"
            :type="activeCategoryId === category.id ? 'primary' : 'default'"
            @click="selectCategory(category.id)"
            class="category-button"
          >
            <el-icon :size="20">{{ category.icon }}</el-icon>
            <span>{{ category.name }}</span>
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 护理内容列表 -->
    <div class="nursing-contents-section">
      <el-card class="contents-card">
        <template #header>
          <div class="card-header">
            <el-icon><List /></el-icon>
            <span>{{ activeCategory?.name || '护理内容' }}</span>
          </div>
        </template>
        <div class="contents-list">
          <el-card
            v-for="content in filteredContents"
            :key="content.id"
            class="content-item"
            @click="viewContentDetails(content)"
          >
            <div class="content-header">
              <div class="content-title">
                <h3>{{ content.name }}</h3>
                <span class="content-code">{{ content.code }}</span>
              </div>
              <div class="content-status" :class="content.status === 'ACTIVE' ? 'status-active' : 'status-inactive'">
                {{ content.status === 'ACTIVE' ? '可用' : '不可用' }}
              </div>
            </div>
            <div class="content-description">
              {{ content.description }}
            </div>
            <div class="content-info">
              <div class="info-item">
                <el-icon><Timer /></el-icon>
                <span>预计时长：{{ content.estimatedDuration }} 分钟</span>
              </div>
              <div class="info-item">
                <el-icon><UserFilled /></el-icon>
                <span>护理人员：{{ content.nurseName || '待定' }}</span>
              </div>
              <div class="info-item" v-if="content.frequency">
                <el-icon><Refresh /></el-icon>
                <span>服务频率：{{ content.frequency }}</span>
              </div>
            </div>
            <div class="content-actions">
              <el-button size="small" @click.stop="viewContentDetails(content)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button size="small" type="primary" @click.stop="requestService(content)" v-if="content.status === 'ACTIVE'">
                <el-icon><Bell /></el-icon>
                申请服务
              </el-button>
            </div>
          </el-card>
        </div>
        <div v-if="filteredContents.length === 0" class="no-contents">
          <el-empty description="该分类下暂无护理内容" />
        </div>
      </el-card>
    </div>
    
    <!-- 护理内容详情 -->
    <div v-if="selectedContent" class="content-detail-section">
      <el-card class="detail-card">
        <template #header>
          <div class="card-header">
            <el-icon><View /></el-icon>
            <span>{{ selectedContent.name }} - 详细信息</span>
          </div>
        </template>
        <div class="detail-content">
          <div class="detail-basic">
            <div class="detail-item">
              <span class="detail-label">护理项目：</span>
              <span class="detail-value">{{ selectedContent.name }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">项目代码：</span>
              <span class="detail-value">{{ selectedContent.code }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">护理分类：</span>
              <span class="detail-value">{{ getCategoryName(selectedContent.categoryId) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务状态：</span>
              <span class="detail-value status" :class="selectedContent.status === 'ACTIVE' ? 'status-active' : 'status-inactive'">
                {{ selectedContent.status === 'ACTIVE' ? '可用' : '不可用' }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">预计时长：</span>
              <span class="detail-value">{{ selectedContent.estimatedDuration }} 分钟</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">护理人员：</span>
              <span class="detail-value">{{ selectedContent.nurseName || '待定' }}</span>
            </div>
            <div class="detail-item" v-if="selectedContent.frequency">
              <span class="detail-label">服务频率：</span>
              <span class="detail-value">{{ selectedContent.frequency }}</span>
            </div>
          </div>
          
          <div class="detail-description">
            <h4>服务描述</h4>
            <p>{{ selectedContent.description }}</p>
          </div>
          
          <div class="detail-procedure" v-if="selectedContent.procedure">
            <h4>服务流程</h4>
            <ol class="procedure-list">
              <li v-for="(step, index) in selectedContent.procedure" :key="index">
                {{ step }}
              </li>
            </ol>
          </div>
          
          <div class="detail-notes" v-if="selectedContent.notes">
            <h4>注意事项</h4>
            <ul class="notes-list">
              <li v-for="(note, index) in selectedContent.notes" :key="index">
                {{ note }}
              </li>
            </ul>
          </div>
          
          <div class="detail-actions">
            <el-button type="primary" size="large" @click="requestService(selectedContent)" v-if="selectedContent.status === 'ACTIVE'">
              <el-icon><Bell /></el-icon>
              申请此服务
            </el-button>
            <el-button size="large" @click="closeDetails">
              <el-icon><Close /></el-icon>
              关闭详情
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 我的护理计划 -->
    <div class="my-plan-section">
      <el-card class="plan-card">
        <template #header>
          <div class="card-header">
            <el-icon><Calendar /></el-icon>
            <span>我的护理计划</span>
          </div>
        </template>
        <div class="plan-content">
          <div class="plan-date-selector">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="选择日期"
              @change="fetchPlanForDate"
            />
          </div>
          <div class="daily-plan" v-if="dailyPlan.length > 0">
            <h4>{{ formatDate(selectedDate) }} 护理计划</h4>
            <div class="plan-items">
              <el-timeline>
                <el-timeline-item
                  v-for="(item, index) in dailyPlan"
                  :key="index"
                  :timestamp="item.time"
                  :type="item.status === 'COMPLETED' ? 'success' : 'primary'"
                  :icon="item.status === 'COMPLETED' ? 'Check' : 'Clock'"
                >
                  <div class="timeline-content">
                    <h5>{{ item.contentName }}</h5>
                    <p class="timeline-description">{{ item.description }}</p>
                    <div class="timeline-status" :class="item.status === 'COMPLETED' ? 'status-completed' : 'status-pending'">
                      {{ item.status === 'COMPLETED' ? '已完成' : '待执行' }}
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
          <div v-else class="no-plan">
            <el-empty description="该日期暂无护理计划" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  List,
  View,
  Bell,
  Calendar,
  Timer,
  UserFilled,
  Refresh,
  Close,
  Check,
  Clock
} from '@element-plus/icons-vue'

// 导入护理相关API
import { getNursingContents } from '@/api/nursing'

const activeCategoryId = ref(null)
const selectedContent = ref(null)
const selectedDate = ref(new Date())

const nursingCategories = ref([
  { id: 1, name: '生活护理', icon: 'House' },
  { id: 2, name: '医疗护理', icon: 'MedicineBox' },
  { id: 3, name: '康复护理', icon: 'Operation' },
  { id: 4, name: '心理护理', icon: 'ChatLineRound' },
  { id: 5, name: '特殊护理', icon: 'Warning' }
])

const nursingContents = ref([])

// 获取护理内容列表
const fetchNursingContents = async () => {
  try {
    const response = await getNursingContents()
    if (response.data && response.data.success) {
      // 处理后端返回的数据，确保格式正确
      const contents = response.data.data.list || []
      nursingContents.value = contents.map(content => ({
        id: content.id,
        code: content.code,
        name: content.name,
        categoryId: content.categoryId || content.category_id,
        status: content.status,
        description: content.description,
        estimatedDuration: content.estimatedDuration || content.estimated_duration,
        frequency: content.frequency,
        procedure: content.procedure ? content.procedure.split(';').filter(p => p.trim()) : [],
        notes: content.notes ? content.notes.split(';').filter(n => n.trim()) : []
      }))
    } else {
      ElMessage.error('获取护理内容列表失败')
    }
  } catch (error) {
    ElMessage.error('获取护理内容列表失败')
    console.error('获取护理内容列表失败:', error)
  }
}

const dailyPlan = ref([
  {
    time: '08:00',
    contentName: '个人卫生护理',
    description: '洗脸、刷牙、梳头、剃须、洗脚等个人卫生护理',
    status: 'COMPLETED'
  },
  {
    time: '09:30',
    contentName: '康复训练',
    description: '肢体功能训练、关节活动、肌力训练',
    status: 'PENDING'
  },
  {
    time: '12:00',
    contentName: '饮食护理',
    description: '协助午餐进食、饮水',
    status: 'PENDING'
  },
  {
    time: '15:00',
    contentName: '药物护理',
    description: '协助服用下午药物',
    status: 'PENDING'
  },
  {
    time: '18:00',
    contentName: '饮食护理',
    description: '协助晚餐进食、饮水',
    status: 'PENDING'
  }
])

const activeCategory = computed(() => {
  return nursingCategories.value.find(category => category.id === activeCategoryId.value)
})

const filteredContents = computed(() => {
  if (!activeCategoryId.value) {
    return nursingContents.value
  }
  return nursingContents.value.filter(content => content.categoryId === activeCategoryId.value)
})

const selectCategory = (categoryId) => {
  activeCategoryId.value = categoryId
}

const viewContentDetails = (content) => {
  selectedContent.value = content
  ElMessage.info('查看护理内容详情')
}

const closeDetails = () => {
  selectedContent.value = null
}

const requestService = (content) => {
  ElMessageBox.confirm(
    `确定要申请 ${content.name} 服务吗？`,
    '申请确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('服务申请成功')
    // 这里将来会调用API申请服务
  }).catch(() => {
    ElMessage.info('已取消申请')
  })
}

const getCategoryName = (categoryId) => {
  const category = nursingCategories.value.find(cat => cat.id === categoryId)
  return category?.name || '未知分类'
}

const fetchPlanForDate = () => {
  // 这里将来会根据选择的日期调用API获取护理计划
  console.log('Fetching plan for date:', selectedDate.value)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

onMounted(() => {
  // 初始化选择第一个分类
  if (nursingCategories.value.length > 0) {
    activeCategoryId.value = nursingCategories.value[0].id
  }
  // 初始化加载护理内容
  fetchNursingContents()
  // 初始化加载当天护理计划
  fetchPlanForDate()
})
</script>

<style scoped>
.elder-nursing-contents {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.nursing-categories-section {
  margin-bottom: 30px;
}

.categories-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.categories-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.category-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: var(--border-radius);
  transition: all 0.3s ease;
  min-width: 140px;
  text-align: center;
  justify-content: center;
  white-space: nowrap;
  overflow: visible;
  font-size: 14px;
}

.category-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.nursing-contents-section {
  margin-bottom: 30px;
}

.contents-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.contents-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.content-item {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.content-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.content-title h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.content-code {
  font-size: 12px;
  color: var(--text-secondary);
  background-color: var(--bg-primary);
  padding: 2px 8px;
  border-radius: 10px;
}

.content-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 10px;
  font-weight: 500;
}

.status-active {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-inactive {
  background-color: #fef0f0;
  color: #f56c6c;
}

.content-description {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 16px;
}

.content-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-primary);
}

.content-actions {
  display: flex;
  gap: 10px;
}

.content-detail-section {
  margin-bottom: 30px;
}

.detail-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.detail-content {
  margin-top: 20px;
}

.detail-basic {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 30px;
  padding: 20px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.detail-value {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.detail-description {
  margin-bottom: 30px;
}

.detail-description h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.detail-description p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
}

.detail-procedure {
  margin-bottom: 30px;
}

.detail-procedure h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.procedure-list {
  padding-left: 20px;
  margin: 0;
}

.procedure-list li {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  margin-bottom: 8px;
}

.detail-notes {
  margin-bottom: 30px;
}

.detail-notes h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.notes-list {
  padding-left: 20px;
  margin: 0;
}

.notes-list li {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  margin-bottom: 8px;
}

.detail-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.no-contents {
  text-align: center;
  padding: 40px 0;
}

.my-plan-section {
  margin-bottom: 30px;
}

.plan-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.plan-content {
  margin-top: 20px;
}

.plan-date-selector {
  margin-bottom: 20px;
}

.daily-plan h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.timeline-content {
  padding: 10px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.timeline-content h5 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.timeline-description {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0 0 8px 0;
}

.timeline-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 500;
  width: fit-content;
}

.status-completed {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-pending {
  background-color: #ecf5ff;
  color: #409eff;
}

.no-plan {
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .elder-nursing-contents {
    padding: 10px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .categories-list {
    flex-direction: column;
    align-items: stretch;
  }
  
  .contents-list {
    grid-template-columns: 1fr;
  }
  
  .content-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .detail-basic {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .detail-actions {
    flex-direction: column;
  }
  
  .category-button {
    justify-content: center;
  }
}
</style>