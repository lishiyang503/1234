<template>
  <div class="elder-nursing-levels">
    <div class="page-header">
      <h2>护理等级管理</h2>
      <p>查看和了解不同的护理等级服务</p>
    </div>
    
    <!-- 护理等级列表 -->
    <div class="nursing-levels-section">
      <el-card class="levels-card">
        <template #header>
          <div class="card-header">
            <el-icon><DataAnalysis /></el-icon>
            <span>护理等级列表</span>
          </div>
        </template>
        <div class="levels-list">
          <el-card
            v-for="level in nursingLevels"
            :key="level.id"
            class="level-item"
            :class="{ active: selectedLevelId === level.id }"
            @click="selectLevel(level.id)"
          >
            <div class="level-header">
              <div class="level-title">
                <h3>{{ level.name }}</h3>
                <span class="level-code">{{ level.code }}</span>
              </div>
              <div class="level-price">
                ¥{{ level.price }}/月
              </div>
            </div>
            <div class="level-description">
              {{ level.description }}
            </div>
            <div class="level-features">
              <div class="feature-item" v-for="(feature, index) in level.features.slice(0, 3)" :key="index">
                <el-icon class="feature-icon"><Check /></el-icon>
                <span>{{ feature }}</span>
              </div>
              <div v-if="level.features.length > 3" class="feature-more">
                +{{ level.features.length - 3 }} 更多服务
              </div>
            </div>
            <div class="level-actions">
              <el-button type="primary" size="small" @click.stop="orderService(level)">
                <el-icon><ShoppingCart /></el-icon>
                立即订购
              </el-button>
              <el-button size="small" @click.stop="viewDetails(level)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>
    
    <!-- 护理等级详情 -->
    <div v-if="selectedLevel" class="nursing-level-detail-section">
      <el-card class="detail-card">
        <template #header>
          <div class="card-header">
            <el-icon><View /></el-icon>
            <span>{{ selectedLevel.name }} - 详细信息</span>
          </div>
        </template>
        <div class="detail-content">
          <div class="detail-basic">
            <div class="detail-item">
              <span class="detail-label">护理等级：</span>
              <span class="detail-value">{{ selectedLevel.name }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">等级代码：</span>
              <span class="detail-value">{{ selectedLevel.code }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务价格：</span>
              <span class="detail-value price">{{ selectedLevel.price }} 元/月</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务状态：</span>
              <span class="detail-value status" :class="selectedLevel.status === 'ACTIVE' ? 'status-active' : 'status-inactive'">
                {{ selectedLevel.status === 'ACTIVE' ? '可用' : '不可用' }}
              </span>
            </div>
          </div>
          
          <div class="detail-description">
            <h4>服务描述</h4>
            <p>{{ selectedLevel.description }}</p>
          </div>
          
          <div class="detail-features">
            <h4>服务内容</h4>
            <div class="features-grid">
              <div class="feature-item" v-for="(feature, index) in selectedLevel.features" :key="index">
                <el-icon class="feature-icon"><Check /></el-icon>
                <span>{{ feature }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-actions">
            <el-button type="primary" size="large" @click="orderService(selectedLevel)">
              <el-icon><ShoppingCart /></el-icon>
              立即订购此服务
            </el-button>
            <el-button size="large" @click="closeDetails">
              <el-icon><Close /></el-icon>
              关闭详情
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 我的护理服务 -->
    <div class="my-services-section">
      <el-card class="my-services-card">
        <template #header>
          <div class="card-header">
            <el-icon><Ticket /></el-icon>
            <span>我的护理服务</span>
          </div>
        </template>
        <div v-if="myServices.length > 0" class="services-list">
          <el-card
            v-for="service in myServices"
            :key="service.id"
            class="service-item"
          >
            <div class="service-header">
              <div class="service-info">
                <h3>{{ service.servicePackageName || service.nursingLevelName || '服务名称' }}</h3>
                <span class="service-period">{{ service.startDate }} 至 {{ service.endDate }}</span>
              </div>
              <div class="service-status" :class="getServiceStatusClass(service.status)">
                {{ getServiceStatusText(service.status) }}
              </div>
            </div>
            <div class="service-details">
              <div class="detail-item">
                <span class="detail-label">服务价格：</span>
                <span class="detail-value">{{ service.price }} 元/月</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">服务时长：</span>
                <span class="detail-value">{{ service.duration }} 个月</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">订单号：</span>
                <span class="detail-value">{{ service.orderNo }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">老人姓名：</span>
                <span class="detail-value">{{ service.residentName }}</span>
              </div>
            </div>
            <div class="service-actions">
              <el-button size="small" @click="viewServiceDetails(service)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button size="small" type="warning" @click="renewService(service)" v-if="service.status === 'ACTIVE'">
                <el-icon><Refresh /></el-icon>
                续费
              </el-button>
            </div>
          </el-card>
        </div>
        <div v-else class="no-services">
          <el-empty description="您还没有订购护理服务" />
          <el-button type="primary" style="margin-top: 20px" @click="scrollToLevels">
            <el-icon><ShoppingCart /></el-icon>
            去订购服务
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  Check,
  ShoppingCart,
  View,
  Ticket,
  Refresh,
  Close
} from '@element-plus/icons-vue'

// 导入服务相关API
import { getServicePackages, createPurchaseRecord, getPurchaseRecords } from '@/api/service'
import { getCurrentUser } from '@/api/auth'

const selectedLevelId = ref(null)
const selectedLevel = ref(null)
const loading = ref(false)

const nursingLevels = ref([])
const myServices = ref([])
const currentUser = ref(null)

// 获取护理等级列表
const fetchNursingLevels = async () => {
  loading.value = true
  try {
    const response = await getServicePackages()
    if (response.data.success) {
      // 处理后端返回的数据，确保格式正确
      const packages = response.data.data.list || []
      nursingLevels.value = packages.map(pkg => ({
        id: pkg.id,
        code: pkg.code,
        name: pkg.name,
        price: pkg.price,
        status: pkg.status,
        description: pkg.description,
        features: pkg.features ? pkg.features.split(';').filter(f => f.trim()) : []
      }))
    } else {
      ElMessage.error('获取护理等级列表失败')
    }
  } catch (error) {
    ElMessage.error('获取护理等级列表失败')
    console.error('获取护理等级列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    const response = await getCurrentUser()
    if (response.data && response.data.success) {
      currentUser.value = response.data.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取我的护理服务
const fetchMyServices = async () => {
  try {
    // 确保获取到用户信息后再请求服务记录
    if (!currentUser.value) {
      await fetchCurrentUser()
    }
    
    const response = await getPurchaseRecords({
      residentId: currentUser.value?.id || '',
      residentName: currentUser.value?.realName || '',
      page: 1,
      pageSize: 10
    })
    if (response.data && response.data.success) {
      myServices.value = response.data.data.list || []
    }
  } catch (error) {
    console.error('获取我的服务失败:', error)
    myServices.value = []
  }
}

const selectLevel = (levelId) => {
  selectedLevelId.value = levelId
  selectedLevel.value = nursingLevels.value.find(level => level.id === levelId)
}

const viewDetails = (level) => {
  selectedLevelId.value = level.id
  selectedLevel.value = level
  ElMessage.info('查看护理等级详情')
}

const closeDetails = () => {
  selectedLevelId.value = null
  selectedLevel.value = null
}

const orderService = async (level) => {
  ElMessageBox.confirm(
    `确定要订购 ${level.name} 服务吗？价格为 ${level.price} 元/月`,
    '订购确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 准备购买记录数据
      const purchaseData = {
        residentId: currentUser.value?.id || 1, // 老人ID，设置默认值1
        residentName: currentUser.value?.realName || '未知', // 老人姓名
        roomNumber: currentUser.value?.roomNumber || '', // 房间号
        bedNumber: currentUser.value?.bedNumber || '', // 床位号
        serviceId: level.id, // 服务ID
        serviceName: level.name, // 服务名称
        serviceLevel: level.code || '', // 服务等级
        price: level.price, // 服务价格
        purchaseDate: new Date().toISOString().slice(0, 10), // 购买日期
        expireDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10), // 过期日期
        operator: currentUser.value?.realName || '未知', // 操作人
        status: '有效' // 状态，使用后端期望的值
      }
      
      // 调用API创建购买记录
      const response = await createPurchaseRecord(purchaseData)
      if (response.data.success) {
        ElMessage.success('服务订购成功')
        // 刷新我的服务列表
        fetchMyServices()
      } else {
        ElMessage.error('服务订购失败：' + response.data.message)
      }
    } catch (error) {
      console.error('订购服务时发生错误:', error)
      ElMessage.error('服务订购失败，请稍后重试')
    }
  }).catch(() => {
    ElMessage.info('已取消订购')
  })
}

const getServiceStatusClass = (status) => {
  return status === 'ACTIVE' ? 'status-active' : 'status-inactive'
}

const getServiceStatusText = (status) => {
  return status === 'ACTIVE' ? '使用中' : '已过期'
}

const viewServiceDetails = (service) => {
  ElMessage.info('查看服务详情')
}

const renewService = (service) => {
  ElMessageBox.confirm(
    `确定要续费 ${service.nursingLevelName} 服务吗？`,
    '续费确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('服务续费成功')
    // 这里将来会调用API续费服务
  }).catch(() => {
    ElMessage.info('已取消续费')
  })
}

const scrollToLevels = () => {
  const levelsSection = document.querySelector('.levels-list')
  if (levelsSection) {
    levelsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

onMounted(() => {
  // 初始化加载数据
  fetchCurrentUser()
  fetchNursingLevels()
  fetchMyServices()
})
</script>

<style scoped>
.elder-nursing-levels {
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

.nursing-levels-section {
  margin-bottom: 30px;
}

.levels-card {
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

.levels-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.level-item {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.level-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.level-item.active {
  border: 2px solid var(--primary-color);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.level-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.level-title h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.level-code {
  font-size: 12px;
  color: var(--text-secondary);
  background-color: var(--bg-primary);
  padding: 2px 8px;
  border-radius: 10px;
}

.level-price {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

.level-description {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 16px;
}

.level-features {
  margin-bottom: 20px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--text-primary);
}

.feature-icon {
  color: #67c23a;
  font-size: 16px;
  margin-top: 2px;
}

.feature-more {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.level-actions {
  display: flex;
  gap: 10px;
}

.nursing-level-detail-section {
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
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
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

.detail-value.price {
  color: var(--primary-color);
}

.detail-value.status {
  padding: 4px 12px;
  border-radius: 12px;
  width: fit-content;
}

.status-active {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-inactive {
  background-color: #fef0f0;
  color: #f56c6c;
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

.detail-features {
  margin-bottom: 30px;
}

.detail-features h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}

.detail-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.my-services-section {
  margin-bottom: 30px;
}

.my-services-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.services-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.service-item {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.service-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.service-period {
  font-size: 12px;
  color: var(--text-secondary);
}

.service-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 10px;
  font-weight: 500;
}

.service-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.service-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.no-services {
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .elder-nursing-levels {
    padding: 10px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .levels-list {
    grid-template-columns: 1fr;
  }
  
  .level-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .detail-basic {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-actions {
    flex-direction: column;
  }
  
  .service-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .service-details {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .service-actions {
    flex-direction: column;
  }
}
</style>