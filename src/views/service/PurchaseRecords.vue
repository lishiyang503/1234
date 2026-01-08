<template>
  <div class="purchase-records-container">
    <div class="page-header">
      <h2 class="page-title">服务管理</h2>
      <p class="page-subtitle">查看服务购买记录</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">服务购买记录</h3>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.residentName"
              placeholder="搜索老人姓名"
              prefix-icon="User"
              clearable
              class="search-input"
            />
            <el-select
              v-model="searchParams.serviceLevel"
              placeholder="筛选套餐级别"
              clearable
              class="filter-select"
            >
              <el-option label="基础套餐" value="基础" />
              <el-option label="中级套餐" value="中级" />
              <el-option label="高级套餐" value="高级" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 购买记录卡片列表 -->
          <div class="records-container">
            <div v-if="purchaseRecordsList.length > 0" class="records-grid">
              <el-card 
                v-for="record in purchaseRecordsList" 
                :key="record.id"
                class="record-card"
              >
                <div class="record-header">
                  <h4 class="service-name">{{ record.serviceName }}</h4>
                  <el-tag 
                    :type="record.status === '有效' ? 'success' : 'warning'"
                    class="status-tag"
                  >
                    {{ record.status }}
                  </el-tag>
                </div>
                
                <div class="record-body">
                  <div class="resident-info">
                    <span class="resident-name">{{ record.residentName }}</span>
                    <span class="room-info">房间 {{ record.roomNumber }} | 床位 {{ record.bedNumber }}</span>
                  </div>
                  
                  <div class="record-details">
                    <div class="detail-item">
                      <span class="detail-label">套餐级别：</span>
                      <el-tag :type="getLevelType(record.serviceLevel)" size="small">
                        {{ record.serviceLevel }}
                      </el-tag>
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">下单时间：</span>
                      <span class="detail-value">{{ formatDate(record.purchaseDate) }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">到期时间：</span>
                      <span class="detail-value">{{ formatDate(record.expireDate) }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">操作人：</span>
                      <span class="detail-value">{{ record.operator }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="record-footer">
                  <div class="price-info">
                    <span class="price-label">金额：</span>
                    <span class="price-value">￥{{ record.price }}</span>
                  </div>
                </div>
              </el-card>
            </div>
            
            <div v-else class="empty-state">
              <el-empty description="暂无购买记录" />
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              background
              layout="prev, pager, next, jumper, ->, total"
              :total="total"
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import { getPurchaseRecords } from '@/api/service'

// 搜索和筛选参数
const searchParams = reactive({
  residentName: '',
  serviceLevel: ''
})

// 表格数据
const purchaseRecordsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 生命周期
onMounted(() => {
  fetchPurchaseRecords()
})

// 获取套餐级别对应的标签类型
const getLevelType = (level) => {
  switch (level) {
    case '基础':
      return 'success'
    case '中级':
      return 'warning'
    case '高级':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取购买记录列表
const fetchPurchaseRecords = async () => {
  loading.value = true
  try {
    const response = await getPurchaseRecords({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.data.success) {
      purchaseRecordsList.value = response.data.data.list || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取服务购买记录列表失败')
    }
  } catch (error) {
    console.error('获取服务购买记录列表失败:', error)
    ElMessage.error('获取服务购买记录列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchPurchaseRecords()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchPurchaseRecords()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 时间格式化函数
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}年${month}月${day}日 ${hours}:${minutes}`
}
</script>

<style scoped>
.purchase-records-container {
  width: 100%;
  height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: var(--text-primary);
}

.page-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card {
  background: var(--gradient-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.card-body {
  padding: 24px;
}

.search-filter {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  width: 250px;
}

.filter-select {
  width: 200px;
}

.date-picker {
  width: 350px;
}

.records-container {
  margin-bottom: 24px;
}

.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.record-card {
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: all 0.3s ease;
}

.record-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.service-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.status-tag {
  font-size: 14px;
  padding: 4px 12px;
}

.record-body {
  margin-bottom: 16px;
}

.resident-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.resident-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.room-info {
  font-size: 14px;
  color: var(--text-secondary);
}

.record-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-label {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  min-width: 100px;
  flex-shrink: 0;
}

.detail-value {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.record-footer {
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.price-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

.price-label {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
}

.price-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--danger-color);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .records-grid {
    grid-template-columns: 1fr;
  }
  
  .record-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .detail-label {
    min-width: unset;
  }
  
  .price-info {
    justify-content: flex-start;
  }
}
</style>