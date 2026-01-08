<template>
  <div class="elder-service-packages">
    <div class="page-header">
      <h2>护理服务订购</h2>
      <p>查看和订购护理服务套餐</p>
    </div>
    
    <!-- 服务套餐分类 -->
    <div class="packages-categories-section">
      <el-card class="categories-card">
        <template #header>
          <div class="card-header">
            <el-icon><Menu /></el-icon>
            <span>服务分类</span>
          </div>
        </template>
        <div class="categories-list">
          <el-button
            v-for="category in serviceCategories"
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
    
    <!-- 服务套餐列表 -->
    <div class="packages-section">
      <el-card class="packages-card">
        <template #header>
          <div class="card-header">
            <el-icon><ShoppingCart /></el-icon>
            <span>{{ activeCategory?.name || '服务套餐' }}</span>
          </div>
        </template>
        <div class="packages-list">
          <el-card
            v-for="pkg in filteredPackages"
            :key="pkg.id"
            class="package-item"
            @click="viewPackageDetails(pkg)"
          >
            <div class="package-header">
              <div class="package-title">
                <h3>{{ pkg.name }}</h3>
                <span class="package-code">{{ pkg.code }}</span>
              </div>
              <div class="package-price">
                ¥{{ pkg.price }}/{{ pkg.unit }}
              </div>
            </div>
            <div class="package-description">
              {{ pkg.description }}
            </div>
            <div class="package-features">
              <div class="feature-item" v-for="(feature, index) in pkg.features.slice(0, 3)" :key="index">
                <el-icon class="feature-icon"><Check /></el-icon>
                <span>{{ feature }}</span>
              </div>
              <div v-if="pkg.features.length > 3" class="feature-more">
                +{{ pkg.features.length - 3 }} 更多服务
              </div>
            </div>
            <div class="package-info">
              <div class="info-item">
                <el-icon><Timer /></el-icon>
                <span>服务时长：{{ pkg.duration }} {{ pkg.durationUnit }}</span>
              </div>
              <div class="info-item">
                <el-icon><Star /></el-icon>
                <span>满意度：{{ pkg.satisfactionRate }}%</span>
              </div>
            </div>
            <div class="package-actions">
              <el-button type="primary" size="small" @click.stop="orderPackage(pkg)">
                <el-icon><ShoppingCart /></el-icon>
                立即订购
              </el-button>
              <el-button size="small" @click.stop="viewPackageDetails(pkg)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>
    
    <!-- 服务套餐详情 -->
    <div v-if="selectedPackage" class="package-detail-section">
      <el-card class="detail-card">
        <template #header>
          <div class="card-header">
            <el-icon><View /></el-icon>
            <span>{{ selectedPackage.name }} - 详细信息</span>
          </div>
        </template>
        <div class="detail-content">
          <div class="detail-basic">
            <div class="detail-item">
              <span class="detail-label">服务名称：</span>
              <span class="detail-value">{{ selectedPackage.name }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务代码：</span>
              <span class="detail-value">{{ selectedPackage.code }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务分类：</span>
              <span class="detail-value">{{ getCategoryName(selectedPackage.categoryId) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务价格：</span>
              <span class="detail-value price">{{ selectedPackage.price }} 元/{{ selectedPackage.unit }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务时长：</span>
              <span class="detail-value">{{ selectedPackage.duration }} {{ selectedPackage.durationUnit }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务状态：</span>
              <span class="detail-value status" :class="selectedPackage.status === 'ACTIVE' ? 'status-active' : 'status-inactive'">
                {{ selectedPackage.status === 'ACTIVE' ? '可用' : '不可用' }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">满意度：</span>
              <span class="detail-value">{{ selectedPackage.satisfactionRate }}%</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">已售数量：</span>
              <span class="detail-value">{{ selectedPackage.soldCount }} 份</span>
            </div>
          </div>
          
          <div class="detail-description">
            <h4>服务描述</h4>
            <p>{{ selectedPackage.description }}</p>
          </div>
          
          <div class="detail-features">
            <h4>服务内容</h4>
            <div class="features-grid">
              <div class="feature-item" v-for="(feature, index) in selectedPackage.features" :key="index">
                <el-icon class="feature-icon"><Check /></el-icon>
                <span>{{ feature }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-actions">
            <el-button type="primary" size="large" @click="orderPackage(selectedPackage)" v-if="selectedPackage.status === 'ACTIVE'">
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
    
    <!-- 我的服务 -->
    <div class="my-services-section">
      <el-card class="my-services-card">
        <template #header>
          <div class="card-header">
            <el-icon><Ticket /></el-icon>
            <span>我的服务</span>
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
                <h3>{{ service.packageName }}</h3>
                <span class="service-period">{{ service.startDate }} 至 {{ service.endDate }}</span>
              </div>
              <div class="service-status" :class="getServiceStatusClass(service.status)">
                {{ getServiceStatusText(service.status) }}
              </div>
            </div>
            <div class="service-details">
              <div class="detail-item">
                <span class="detail-label">服务价格：</span>
                <span class="detail-value">{{ service.price }} 元/{{ service.unit }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">服务时长：</span>
                <span class="detail-value">{{ service.duration }} {{ service.durationUnit }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">订单号：</span>
                <span class="detail-value">{{ service.orderNo }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">购买日期：</span>
                <span class="detail-value">{{ service.purchaseDate }}</span>
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
              <el-button size="small" type="info" @click="rateService(service)" v-if="service.status === 'COMPLETED' && !service.rated">
                <el-icon><Star /></el-icon>
                评价
              </el-button>
            </div>
          </el-card>
        </div>
        <div v-else class="no-services">
          <el-empty description="您还没有购买服务套餐" />
          <el-button type="primary" style="margin-top: 20px" @click="scrollToPackages">
            <el-icon><ShoppingCart /></el-icon>
            去购买服务
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Menu,
  ShoppingCart,
  View,
  Check,
  Timer,
  Star,
  Ticket,
  Refresh,
  Close
} from '@element-plus/icons-vue'

const activeCategoryId = ref(null)
const selectedPackage = ref(null)

const serviceCategories = ref([
  { id: 1, name: '生活照料', icon: 'House' },
  { id: 2, name: '医疗护理', icon: 'MedicineBox' },
  { id: 3, name: '康复理疗', icon: 'Operation' },
  { id: 4, name: '精神慰藉', icon: 'ChatLineRound' },
  { id: 5, name: '其他服务', icon: 'More' }
])

const servicePackages = ref([
  {
    id: 1,
    code: 'SP-001',
    name: '基础生活照料套餐',
    categoryId: 1,
    price: 800,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'ACTIVE',
    description: '为老人提供基础的生活照料服务，包括日常起居、个人卫生、饮食协助等。',
    features: [
      '每日生活照料',
      '个人卫生护理',
      '饮食起居协助',
      '室内活动陪伴',
      '基础健康监测'
    ],
    satisfactionRate: 95,
    soldCount: 128
  },
  {
    id: 2,
    code: 'SP-002',
    name: '专业医疗护理套餐',
    categoryId: 2,
    price: 1500,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'ACTIVE',
    description: '为需要医疗护理的老人提供专业的医疗护理服务，包括药物管理、伤口护理等。',
    features: [
      '药物管理与提醒',
      '伤口护理',
      '专业健康监测',
      '医疗协助服务',
      '康复护理指导'
    ],
    satisfactionRate: 92,
    soldCount: 86
  },
  {
    id: 3,
    code: 'SP-003',
    name: '康复理疗套餐',
    categoryId: 3,
    price: 1200,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'ACTIVE',
    description: '为需要康复理疗的老人提供专业的康复训练和理疗服务，帮助恢复身体功能。',
    features: [
      '专业康复训练',
      '肢体功能锻炼',
      '关节活动训练',
      '肌力恢复训练',
      '康复设备使用指导'
    ],
    satisfactionRate: 90,
    soldCount: 64
  },
  {
    id: 4,
    code: 'SP-004',
    name: '精神慰藉套餐',
    categoryId: 4,
    price: 600,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'ACTIVE',
    description: '为老人提供精神慰藉和心理支持服务，包括聊天陪伴、心理疏导等。',
    features: [
      '聊天陪伴',
      '心理疏导',
      '情绪安抚',
      '兴趣活动陪伴',
      '家属沟通协调'
    ],
    satisfactionRate: 96,
    soldCount: 72
  },
  {
    id: 5,
    code: 'SP-005',
    name: '综合护理套餐',
    categoryId: 5,
    price: 2500,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'ACTIVE',
    description: '为老人提供全方位的综合护理服务，包括生活照料、医疗护理、康复理疗和精神慰藉。',
    features: [
      '基础生活照料套餐所有内容',
      '专业医疗护理套餐所有内容',
      '康复理疗套餐所有内容',
      '精神慰藉套餐所有内容',
      '24小时紧急呼叫服务'
    ],
    satisfactionRate: 98,
    soldCount: 45
  }
])

const myServices = ref([
  {
    id: 1,
    packageName: '基础生活照料套餐',
    startDate: '2024-01-01',
    endDate: '2024-01-31',
    price: 800,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'ACTIVE',
    orderNo: 'ORD-20240101-001',
    purchaseDate: '2023-12-30',
    rated: false
  },
  {
    id: 2,
    packageName: '精神慰藉套餐',
    startDate: '2023-12-01',
    endDate: '2023-12-31',
    price: 600,
    unit: '月',
    duration: 30,
    durationUnit: '天',
    status: 'COMPLETED',
    orderNo: 'ORD-20231201-001',
    purchaseDate: '2023-11-28',
    rated: false
  }
])

const activeCategory = computed(() => {
  return serviceCategories.value.find(category => category.id === activeCategoryId.value)
})

const filteredPackages = computed(() => {
  if (!activeCategoryId.value) {
    return servicePackages.value
  }
  return servicePackages.value.filter(pkg => pkg.categoryId === activeCategoryId.value)
})

const selectCategory = (categoryId) => {
  activeCategoryId.value = categoryId
}

const viewPackageDetails = (pkg) => {
  selectedPackage.value = pkg
  ElMessage.info('查看服务套餐详情')
}

const closeDetails = () => {
  selectedPackage.value = null
}

const orderPackage = (pkg) => {
  ElMessageBox.confirm(
    `确定要订购 ${pkg.name} 服务吗？价格为 ${pkg.price} 元/${pkg.unit}`,
    '订购确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('服务订购成功')
    // 这里将来会调用API创建订单
  }).catch(() => {
    ElMessage.info('已取消订购')
  })
}

const getCategoryName = (categoryId) => {
  const category = serviceCategories.value.find(cat => cat.id === categoryId)
  return category?.name || '未知分类'
}

const viewServiceDetails = (service) => {
  ElMessage.info('查看服务详情')
}

const renewService = (service) => {
  ElMessageBox.confirm(
    `确定要续费 ${service.packageName} 服务吗？`,
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

const rateService = (service) => {
  ElMessage.info('评价服务功能开发中')
  // 这里将来会实现服务评价功能
}

const scrollToPackages = () => {
  const packagesSection = document.querySelector('.packages-list')
  if (packagesSection) {
    packagesSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const getServiceStatusClass = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'status-active'
    case 'COMPLETED':
      return 'status-completed'
    case 'EXPIRED':
      return 'status-expired'
    default:
      return 'status-default'
  }
}

const getServiceStatusText = (status) => {
  switch (status) {
    case 'ACTIVE':
      return '使用中'
    case 'COMPLETED':
      return '已完成'
    case 'EXPIRED':
      return '已过期'
    default:
      return '未知状态'
  }
}

onMounted(() => {
  // 初始化选择第一个分类
  if (serviceCategories.value.length > 0) {
    activeCategoryId.value = serviceCategories.value[0].id
  }
})
</script>

<style scoped>
.elder-service-packages {
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

.packages-categories-section {
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
  padding: 10px 20px;
  border-radius: var(--border-radius);
  transition: all 0.3s ease;
}

.category-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.packages-section {
  margin-bottom: 30px;
}

.packages-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.packages-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.package-item {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.package-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.package-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.package-title h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.package-code {
  font-size: 12px;
  color: var(--text-secondary);
  background-color: var(--bg-primary);
  padding: 2px 8px;
  border-radius: 10px;
}

.package-price {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

.package-description {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 16px;
}

.package-features {
  margin-bottom: 16px;
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

.package-info {
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

.package-actions {
  display: flex;
  gap: 10px;
}

.package-detail-section {
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

.detail-value.price {
  color: var(--primary-color);
}

.detail-value.status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
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

.status-completed {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-active {
  background-color: #ecf5ff;
  color: #409eff;
}

.status-expired {
  background-color: #f5f7fa;
  color: #909399;
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
  .elder-service-packages {
    padding: 10px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .categories-list {
    flex-direction: column;
    align-items: stretch;
  }
  
  .packages-list {
    grid-template-columns: 1fr;
  }
  
  .package-header {
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
  
  .category-button {
    justify-content: center;
  }
}
</style>