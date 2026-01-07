<template>
  <div class="health-visualization-container">
    <div class="page-header">
      <h2 class="page-title">健康数据可视化</h2>
      <p class="page-subtitle">通过图表直观展示老人健康数据趋势</p>
    </div>
    
    <div class="content-section">
      <!-- 老人选择卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">选择监测对象</h3>
        </div>
        <div class="card-body">
          <el-form :model="filterForm" label-width="120px" class="filter-form">
            <el-form-item label="老人姓名" required>
              <el-select v-model="filterForm.residentId" placeholder="请选择老人姓名" style="width: 200px;" @change="fetchResidentData">
                <el-option
                  v-for="resident in residentsList"
                  :key="resident.id"
                  :label="resident.name"
                  :value="resident.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="数据类型">
              <el-select v-model="filterForm.dataType" placeholder="请选择数据类型" style="width: 200px;" @change="fetchResidentData">
                <el-option label="血压" value="bloodPressure" />
                <el-option label="血糖" value="bloodSugar" />
                <el-option label="心率" value="heartRate" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 300px;"
                @change="fetchResidentData"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <!-- 数据图表卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">健康数据趋势</h3>
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
        <div class="card-body">
          <div v-if="!filterForm.residentId" class="no-data">
            <el-empty description="请选择老人姓名" />
          </div>
          <div v-else-if="loading" class="loading-data">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else class="chart-container">
            <div ref="chartRef" class="chart" style="height: 400px;"></div>
          </div>
        </div>
      </div>
      
      <!-- 异常分析卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">健康异常分析</h3>
        </div>
        <div class="card-body">
          <div v-if="!filterForm.residentId" class="no-data">
            <el-empty description="请选择老人姓名" />
          </div>
          <div v-else-if="loading" class="loading-data">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else class="analysis-container">
            <el-table :data="abnormalData" stripe style="width: 100%;">
              <el-table-column prop="date" label="时间" width="180" />
              <el-table-column prop="type" label="数据类型" width="120" />
              <el-table-column prop="value" label="数值" width="120" />
              <el-table-column prop="status" label="状态" width="100" />
              <el-table-column prop="notes" label="备注" />
            </el-table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getAllResidents,
  getBloodPressureRecords,
  getBloodSugarRecords,
  getHeartRateRecords
} from '@/api/resident'

// 老人列表
const residentsList = ref([])

// 筛选表单
const filterForm = reactive({
  residentId: '',
  dataType: 'bloodPressure',
  dateRange: []
})

// 加载状态
const loading = ref(false)

// 异常数据
const abnormalData = ref([])

// 图表实例
const chartRef = ref(null)
let chartInstance = null

// 获取老人列表
const fetchResidentsList = async () => {
  try {
    const response = await getAllResidents()
    if (response.data.success) {
      residentsList.value = (response.data.data || []).filter(resident => resident.status === '入住')
    } else {
      ElMessage.error(response.data.message || '获取老人列表失败')
    }
  } catch (error) {
    ElMessage.error('获取老人列表失败')
    console.error('获取老人列表失败:', error)
  }
}

// 获取老人健康数据
const fetchResidentData = async () => {
  if (!filterForm.residentId) return
  
  loading.value = true
  try {
    let data = []
    
    switch (filterForm.dataType) {
      case 'bloodPressure':
        data = await fetchBloodPressureData()
        break
      case 'bloodSugar':
        data = await fetchBloodSugarData()
        break
      case 'heartRate':
        data = await fetchHeartRateData()
        break
    }
    
    // 处理异常数据
    processAbnormalData(data)
    
    // 渲染图表
    renderChart(data)
  } catch (error) {
    ElMessage.error('获取健康数据失败')
    console.error('获取健康数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取血压数据
const fetchBloodPressureData = async () => {
  const response = await getBloodPressureRecords({ residentId: filterForm.residentId })
  if (response.data.success) {
    return (response.data.data || [])
      .filter(item => item.residentId == filterForm.residentId)
      .map(item => ({
        date: item.measureTime,
        systolic: item.systolic,
        diastolic: item.diastolic
      }))
  }
  return []
}

// 获取血糖数据
const fetchBloodSugarData = async () => {
  const response = await getBloodSugarRecords({ residentId: filterForm.residentId })
  if (response.data.success) {
    return (response.data.data || [])
      .filter(item => item.residentId == filterForm.residentId)
      .map(item => ({
        date: item.measureTime,
        level: item.level,
        status: item.status
      }))
  }
  return []
}

// 获取心率数据
const fetchHeartRateData = async () => {
  const response = await getHeartRateRecords({ residentId: filterForm.residentId })
  if (response.data.success) {
    return (response.data.data || [])
      .filter(item => item.residentId == filterForm.residentId)
      .map(item => ({
        date: item.measureTime,
        rate: item.rate
      }))
  }
  return []
}

// 处理异常数据
const processAbnormalData = (data) => {
  abnormalData.value = []
  
  data.forEach(item => {
    switch (filterForm.dataType) {
      case 'bloodPressure':
        if (item.systolic > 140 || item.diastolic > 90 || item.systolic < 90 || item.diastolic < 60) {
          abnormalData.value.push({
            date: item.date,
            type: '血压',
            value: `${item.systolic}/${item.diastolic} mmHg`,
            status: item.systolic > 140 || item.diastolic > 90 ? '偏高' : '偏低',
            notes: ''
          })
        }
        break
      case 'bloodSugar':
        if ((item.status === '空腹' && item.level > 7.0) || (item.status === '餐后' && item.level > 10.0) || item.level < 3.9) {
          abnormalData.value.push({
            date: item.date,
            type: '血糖',
            value: `${item.level} mmol/L`,
            status: item.level > 7.0 ? '偏高' : '偏低',
            notes: item.status
          })
        }
        break
      case 'heartRate':
        if (item.rate > 100 || item.rate < 60) {
          abnormalData.value.push({
            date: item.date,
            type: '心率',
            value: `${item.rate} 次/分`,
            status: item.rate > 100 ? '偏高' : '偏低',
            notes: ''
          })
        }
        break
    }
  })
}

// 渲染图表
const renderChart = (data) => {
  if (!chartRef.value) return
  
  // 初始化图表
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  
  // 准备图表数据
  const dates = data.map(item => item.date)
  let series = []
  let yAxis = {}
  
  switch (filterForm.dataType) {
    case 'bloodPressure':
      series = [
        {
          name: '收缩压',
          type: 'line',
          data: data.map(item => item.systolic),
          smooth: true,
          lineStyle: { color: '#f56c6c' }
        },
        {
          name: '舒张压',
          type: 'line',
          data: data.map(item => item.diastolic),
          smooth: true,
          lineStyle: { color: '#409eff' }
        }
      ]
      yAxis = {
        name: '血压值 (mmHg)',
        min: 40,
        max: 200
      }
      break
    case 'bloodSugar':
      series = [
        {
          name: '血糖值',
          type: 'line',
          data: data.map(item => item.level),
          smooth: true,
          lineStyle: { color: '#67c23a' },
          markLine: {
            data: [
              { yAxis: 3.9, name: '低血糖', lineStyle: { color: '#e6a23c' } },
              { yAxis: 7.0, name: '空腹上限', lineStyle: { color: '#f56c6c' } },
              { yAxis: 10.0, name: '餐后上限', lineStyle: { color: '#f56c6c' } }
            ]
          }
        }
      ]
      yAxis = {
        name: '血糖值 (mmol/L)',
        min: 2,
        max: 30
      }
      break
    case 'heartRate':
      series = [
        {
          name: '心率值',
          type: 'line',
          data: data.map(item => item.rate),
          smooth: true,
          lineStyle: { color: '#e6a23c' },
          markLine: {
            data: [
              { yAxis: 60, name: '心率下限', lineStyle: { color: '#409eff' } },
              { yAxis: 100, name: '心率上限', lineStyle: { color: '#f56c6c' } }
            ]
          }
        }
      ]
      yAxis = {
        name: '心率值 (次/分)',
        min: 40,
        max: 200
      }
      break
  }
  
  // 图表配置
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: series.map(item => item.name)
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: yAxis,
    series: series
  }
  
  // 渲染图表
  chartInstance.setOption(option)
}

// 刷新数据
const refreshData = () => {
  fetchResidentData()
}

// 监听窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 组件挂载时
onMounted(() => {
  fetchResidentsList()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时
onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.health-visualization-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #303133;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.card-body {
  padding: 24px;
}

.filter-form {
  max-width: 800px;
}

.no-data,
.loading-data {
  padding: 40px 0;
  text-align: center;
}

.chart-container {
  position: relative;
}

.analysis-container {
  min-height: 300px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .health-visualization-container {
    padding: 16px;
  }
  
  .card-body {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .health-visualization-container {
    padding: 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .card-body {
    padding: 16px;
  }
  
  .card-header {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .filter-form {
    max-width: 100%;
  }
  
  .chart {
    height: 300px !important;
  }
}
</style>