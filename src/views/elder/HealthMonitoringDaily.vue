<template>
  <div class="elder-health-monitoring-daily">
    <div class="page-header">
      <h2>日常健康检测</h2>
      <p>记录和查看您的日常健康数据</p>
    </div>
    
    <!-- 健康数据概览卡片 -->
    <div class="health-overview">
      <el-card class="health-card">
        <template #header>
          <div class="card-header">
            <el-icon><Timer /></el-icon>
            <span>今日健康数据</span>
          </div>
        </template>
        <div class="health-data-grid">
          <div class="health-item">
            <div class="health-label">血压</div>
            <div class="health-value">{{ todayData.bloodPressure || '~' }} mmHg</div>
            <div class="health-status" :class="getHealthStatus('bloodPressure')">
              {{ getStatusText('bloodPressure') }}
            </div>
          </div>
          <div class="health-item">
            <div class="health-label">血糖</div>
            <div class="health-value">{{ todayData.bloodSugar || '~' }} mmol/L</div>
            <div class="health-status" :class="getHealthStatus('bloodSugar')">
              {{ getStatusText('bloodSugar') }}
            </div>
          </div>
          <div class="health-item">
            <div class="health-label">心率</div>
            <div class="health-value">{{ todayData.heartRate || '~' }} 次/分</div>
            <div class="health-status" :class="getHealthStatus('heartRate')">
              {{ getStatusText('heartRate') }}
            </div>
          </div>

        </div>
      </el-card>
    </div>
    

    
    <!-- 健康数据历史记录 -->
    <div class="health-history-section">
      <el-card class="history-card">
        <template #header>
          <div class="card-header">
            <el-icon><Clock /></el-icon>
            <span>健康数据历史（最近七天）</span>
          </div>
        </template>
        <el-table :data="healthHistory" style="width: 100%">
          <el-table-column label="记录时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.recordTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="bloodPressure" label="血压" />
          <el-table-column prop="bloodSugar" label="血糖" />
          <el-table-column prop="heartRate" label="心率" />
          <el-table-column prop="notes" label="备注" min-width="120" />
        </el-table>
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 健康建议 -->
    <div class="health-suggestions-section">
      <el-card class="suggestions-card">
        <template #header>
          <div class="card-header">
            <el-icon><Message /></el-icon>
            <span>健康建议</span>
          </div>
        </template>
        <div class="suggestions-list">
          <el-alert
            v-for="(suggestion, index) in healthSuggestions"
            :key="index"
            :title="suggestion.title"
            :type="suggestion.type"
            show-icon
            :description="suggestion.content"
            class="suggestion-item"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Timer,
  Clock,
  Message
} from '@element-plus/icons-vue'
// 导入健康监测相关API
import {
  getBloodPressureRecords,
  getBloodSugarRecords,
  getHeartRateRecords
} from '@/api/resident'

// 假设老人ID从登录状态获取，这里暂时使用固定值
const elderId = ref('1') // 实际项目中应从登录状态或路由参数中获取

const loading = ref(false)
const todayData = ref({
  bloodPressure: '',
  bloodSugar: '',
  heartRate: ''
})



// 设置日期范围为最近七天
const dateRange = ref([
  new Date(new Date().setDate(new Date().getDate() - 7)),
  new Date()
])

const healthHistory = ref([])
const healthSuggestions = ref([])

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取健康状态
const getHealthStatus = (type) => {
  const value = todayData.value[type]
  switch (type) {
    case 'bloodPressure':
      if (!value) return 'normal'
      const [systolic, diastolic] = value.split('/').map(Number)
      if (systolic > 140 || diastolic > 90) return 'danger'
      if (systolic < 90 || diastolic < 60) return 'warning'
      return 'normal'
    case 'bloodSugar':
      if (!value) return 'normal'
      const sugarValue = parseFloat(value)
      if (sugarValue > 7.0) return 'danger'
      if (sugarValue < 3.9) return 'warning'
      return 'normal'
    case 'heartRate':
      if (!value) return 'normal'
      const rateValue = parseInt(value)
      if (rateValue > 100) return 'danger'
      if (rateValue < 60) return 'warning'
      return 'normal'

    default:
      return 'normal'
  }
}

// 获取状态文本
const getStatusText = (type) => {
  const status = getHealthStatus(type)
  switch (status) {
    case 'danger':
      return '异常'
    case 'warning':
      return '偏低'
    case 'normal':
      return '正常'
    default:
      return '正常'
  }
}

// 获取今日健康数据
const fetchTodayData = async () => {
  loading.value = true
  try {
    // 重置今日数据
    todayData.value = {
      bloodPressure: '',
      bloodSugar: '',
      heartRate: ''
    }
    
    // 获取今日日期
    const today = new Date()
    const todayStr = today.toISOString().split('T')[0]
    
    // 获取血压数据
    const bpResponse = await getBloodPressureRecords()
    if (bpResponse.data.success && bpResponse.data.data.length > 0) {
      // 筛选今日的血压数据
      const todayBpRecords = bpResponse.data.data.filter(record => {
        const recordDate = new Date(record.measureTime)
        return recordDate.toISOString().split('T')[0] === todayStr
      })
      if (todayBpRecords.length > 0) {
        // 按时间排序，取最新的记录
        todayBpRecords.sort((a, b) => new Date(b.measureTime) - new Date(a.measureTime))
        const latestBp = todayBpRecords[0]
        todayData.value.bloodPressure = `${latestBp.systolic}/${latestBp.diastolic}`
      }
    }
    
    // 获取血糖数据
    const bsResponse = await getBloodSugarRecords()
    if (bsResponse.data.success && bsResponse.data.data.length > 0) {
      // 筛选今日的血糖数据
      const todayBsRecords = bsResponse.data.data.filter(record => {
        const recordDate = new Date(record.measureTime)
        return recordDate.toISOString().split('T')[0] === todayStr
      })
      if (todayBsRecords.length > 0) {
        // 按时间排序，取最新的记录
        todayBsRecords.sort((a, b) => new Date(b.measureTime) - new Date(a.measureTime))
        const latestBs = todayBsRecords[0]
        todayData.value.bloodSugar = latestBs.level.toString()
      }
    }
    
    // 获取心率数据
    const hrResponse = await getHeartRateRecords()
    if (hrResponse.data.success && hrResponse.data.data.length > 0) {
      // 筛选今日的心率数据
      const todayHrRecords = hrResponse.data.data.filter(record => {
        const recordDate = new Date(record.measureTime)
        return recordDate.toISOString().split('T')[0] === todayStr
      })
      if (todayHrRecords.length > 0) {
        // 按时间排序，取最新的记录
        todayHrRecords.sort((a, b) => new Date(b.measureTime) - new Date(a.measureTime))
        const latestHr = todayHrRecords[0]
        todayData.value.heartRate = latestHr.rate.toString()
      }
    }
    
    // 更新健康建议
    updateHealthSuggestions()
  } catch (error) {
    ElMessage.error('获取今日健康数据失败')
    console.error('获取今日健康数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新健康建议
const updateHealthSuggestions = () => {
  const suggestions = []
  
  // 血压建议
  if (todayData.value.bloodPressure) {
    const status = getHealthStatus('bloodPressure')
    if (status === 'normal') {
      suggestions.push({
        title: '血压正常',
        content: '您的血压在正常范围内，请继续保持健康的生活方式。',
        type: 'success'
      })
    } else if (status === 'danger') {
      suggestions.push({
        title: '血压异常',
        content: '您的血压偏高，建议休息并咨询医生。',
        type: 'danger'
      })
    } else {
      suggestions.push({
        title: '血压偏低',
        content: '您的血压偏低，建议适当补充水分和营养。',
        type: 'warning'
      })
    }
  }
  
  // 血糖建议
  if (todayData.value.bloodSugar) {
    const status = getHealthStatus('bloodSugar')
    if (status === 'normal') {
      suggestions.push({
        title: '血糖良好',
        content: '血糖水平稳定，建议继续保持规律饮食。',
        type: 'success'
      })
    } else if (status === 'danger') {
      suggestions.push({
        title: '血糖偏高',
        content: '您的血糖偏高，建议控制饮食并咨询医生。',
        type: 'danger'
      })
    } else {
      suggestions.push({
        title: '血糖偏低',
        content: '您的血糖偏低，建议及时补充糖分。',
        type: 'warning'
      })
    }
  }
  
  // 心率建议
  if (todayData.value.heartRate) {
    const status = getHealthStatus('heartRate')
    if (status === 'normal') {
      suggestions.push({
        title: '心率正常',
        content: '心率在正常范围内，无需担忧。',
        type: 'success'
      })
    } else if (status === 'danger') {
      suggestions.push({
        title: '心率偏快',
        content: '您的心率偏快，建议休息并咨询医生。',
        type: 'danger'
      })
    } else {
      suggestions.push({
        title: '心率偏慢',
        content: '您的心率偏慢，建议咨询医生。',
        type: 'warning'
      })
    }
  }
  

  
  // 通用健康建议
  suggestions.push({
    title: '健康建议',
    content: '建议每天保持适量运动，多喝水，保证充足睡眠。',
    type: 'info'
  })
  
  healthSuggestions.value = suggestions
}



// 获取历史数据
const fetchHistoryData = async () => {
  loading.value = true
  try {
    const startDate = dateRange.value[0]
    const endDate = dateRange.value[1]
    
    // 获取所有类型的健康数据
    const [bpResponse, bsResponse, hrResponse] = await Promise.all([
      getBloodPressureRecords(),
      getBloodSugarRecords(),
      getHeartRateRecords()
    ])
    
    // 合并数据
    const historyData = []
    
    // 处理血压数据
    if (bpResponse.data.success) {
      bpResponse.data.data.forEach(item => {
        const recordDate = new Date(item.measureTime)
        // 筛选最近七天的数据
        if (recordDate >= startDate && recordDate <= endDate) {
          historyData.push({
            recordTime: item.measureTime,
            bloodPressure: `${item.systolic}/${item.diastolic}`,
            bloodSugar: '',
            heartRate: '',
            notes: item.notes
          })
        }
      })
    }
    
    // 处理血糖数据
    if (bsResponse.data.success) {
      bsResponse.data.data.forEach(item => {
        const recordDate = new Date(item.measureTime)
        // 筛选最近七天的数据
        if (recordDate >= startDate && recordDate <= endDate) {
          historyData.push({
            recordTime: item.measureTime,
            bloodPressure: '',
            bloodSugar: item.level.toString(),
            heartRate: '',
            notes: item.notes
          })
        }
      })
    }
    
    // 处理心率数据
    if (hrResponse.data.success) {
      hrResponse.data.data.forEach(item => {
        const recordDate = new Date(item.measureTime)
        // 筛选最近七天的数据
        if (recordDate >= startDate && recordDate <= endDate) {
          historyData.push({
            recordTime: item.measureTime,
            bloodPressure: '',
            bloodSugar: '',
            heartRate: item.rate.toString(),
            notes: item.notes
          })
        }
      })
    }
    
    // 按时间排序
    historyData.sort((a, b) => {
      return new Date(b.recordTime) - new Date(a.recordTime)
    })
    
    // 处理分页
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const paginatedData = historyData.slice(startIndex, endIndex)
    
    healthHistory.value = paginatedData
    total.value = historyData.length
  } catch (error) {
    ElMessage.error('获取历史数据失败')
    console.error('获取历史数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchHistoryData()
}

// 处理页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  fetchHistoryData()
}

// 格式化日期时间为"几月几日几时几分"的形式
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  
  const date = new Date(dateTimeStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hours = date.getHours()
  const minutes = date.getMinutes()
  
  return `${month}月${day}日${hours}时${minutes}分`
}

// 组件挂载时获取数据
onMounted(() => {
  fetchTodayData()
  fetchHistoryData()
})
</script>

<style scoped>
.elder-health-monitoring-daily {
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

.health-overview {
  margin-bottom: 30px;
}

.health-card {
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

.health-data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.health-item {
  text-align: center;
  padding: 20px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
}

.health-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.health-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.health-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

.health-status.normal {
  background-color: #f0f9eb;
  color: #67c23a;
}

.health-status.warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.health-status.danger {
  background-color: #fef0f0;
  color: #f56c6c;
}

.health-form-section {
  margin-bottom: 30px;
}

.form-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.health-form {
  margin-top: 20px;
}

.health-history-section {
  margin-bottom: 30px;
}

.history-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.health-suggestions-section {
  margin-bottom: 30px;
}

.suggestions-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.suggestions-list {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  border-radius: var(--border-radius);
}

@media (max-width: 768px) {
  .health-data-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .elder-health-monitoring-daily {
    padding: 10px;
  }
}
</style>