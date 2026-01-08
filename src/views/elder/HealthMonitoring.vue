<template>
  <div class="health-monitoring-container">
    <div class="page-header">
      <h2 class="page-title">每日健康指标</h2>
      <p class="page-subtitle">实时监测和记录我的健康数据</p>
    </div>
    
    <div class="content-section">
      <!-- 每日健康数据概览 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">今日健康数据概览</h3>
          <el-date-picker 
            v-model="currentDate" 
            type="date" 
            placeholder="选择日期" 
            value-format="YYYY-MM-DD"
            class="date-picker"
            @change="fetchDailyData"
          />
        </div>
        
        <div class="card-body">
          <div class="daily-overview">
            <div class="overview-card blood-pressure-card">
              <div class="overview-icon blood-pressure-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="overview-content">
                <h4>血压</h4>
                <div class="overview-value" :class="getBloodPressureClass(todayBloodPressure)">
                  {{ todayBloodPressure.systolic }}/{{ todayBloodPressure.diastolic }} mmHg
                </div>
                <div class="overview-status" :class="getBloodPressureStatusClass(todayBloodPressure)">
                  {{ getBloodPressureStatus(todayBloodPressure) }}
                </div>
              </div>
            </div>
            
            <div class="overview-card blood-sugar-card">
              <div class="overview-icon blood-sugar-icon">
                <el-icon><Sugar /></el-icon>
              </div>
              <div class="overview-content">
                <h4>血糖</h4>
                <div class="overview-value" :class="getBloodSugarClass(todayBloodSugar)">
                  {{ todayBloodSugar.level }} mmol/L
                </div>
                <div class="overview-status" :class="getBloodSugarStatusClass(todayBloodSugar)">
                  {{ todayBloodSugar.status || '空腹' }}
                </div>
              </div>
            </div>
            
            <div class="overview-card heart-rate-card">
              <div class="overview-icon heart-rate-icon">
                <el-icon><Link /></el-icon>
              </div>
              <div class="overview-content">
                <h4>心率</h4>
                <div class="overview-value" :class="getHeartRateClass(todayHeartRate)">
                  {{ todayHeartRate.rate }} 次/分
                </div>
                <div class="overview-status" :class="getHeartRateStatusClass(todayHeartRate)">
                  {{ getHeartRateStatus(todayHeartRate) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 健康数据录入卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">记录健康数据</h3>
          <el-button type="primary" @click="toggleInputForm">
            <el-icon><Edit /></el-icon>
            {{ showInputForm ? '收起录入' : '展开录入' }}
          </el-button>
        </div>
        
        <div class="card-body" v-if="showInputForm">
          <el-tabs v-model="activeTab" class="health-tabs">
            <!-- 血压记录 -->
            <el-tab-pane label="血压记录" name="bloodPressure">
              <div class="tab-content">
                <div class="form-container">
                  <el-form :model="bloodPressureForm" label-width="120px" class="health-form">
                    <el-form-item label="收缩压(mmHg)" required>
                      <el-input-number v-model="bloodPressureForm.systolic" :min="60" :max="200" style="width: 150px;"></el-input-number>
                    </el-form-item>
                    <el-form-item label="舒张压(mmHg)" required>
                      <el-input-number v-model="bloodPressureForm.diastolic" :min="40" :max="120" style="width: 150px;"></el-input-number>
                    </el-form-item>
                    <el-form-item label="测量时间">
                      <el-date-picker v-model="bloodPressureForm.measureTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择测量时间"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="测量备注">
                      <el-input v-model="bloodPressureForm.notes" type="textarea" :rows="3" placeholder="请输入备注信息"></el-input>
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="saveBloodPressure">保存记录</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
            </el-tab-pane>
            
            <!-- 血糖记录 -->
            <el-tab-pane label="血糖记录" name="bloodSugar">
              <div class="tab-content">
                <div class="form-container">
                  <el-form :model="bloodSugarForm" label-width="120px" class="health-form">
                    <el-form-item label="血糖值(mmol/L)" required>
                      <el-input-number v-model="bloodSugarForm.level" :min="2" :max="30" :step="0.1" style="width: 150px;"></el-input-number>
                    </el-form-item>
                    <el-form-item label="测量时间">
                      <el-date-picker v-model="bloodSugarForm.measureTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择测量时间"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="测量状态">
                      <el-radio-group v-model="bloodSugarForm.status">
                        <el-radio label="空腹" />
                        <el-radio label="餐后" />
                        <el-radio label="随机" />
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item label="测量备注">
                      <el-input v-model="bloodSugarForm.notes" type="textarea" :rows="3" placeholder="请输入备注信息"></el-input>
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="saveBloodSugar">保存记录</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
            </el-tab-pane>
            
            <!-- 心率记录 -->
            <el-tab-pane label="心率记录" name="heartRate">
              <div class="tab-content">
                <div class="form-container">
                  <el-form :model="heartRateForm" label-width="120px" class="health-form">
                    <el-form-item label="心率值(次/分)" required>
                      <el-input-number v-model="heartRateForm.rate" :min="40" :max="200" style="width: 150px;"></el-input-number>
                    </el-form-item>
                    <el-form-item label="测量时间">
                      <el-date-picker v-model="heartRateForm.measureTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择测量时间"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="测量备注">
                      <el-input v-model="heartRateForm.notes" type="textarea" :rows="3" placeholder="请输入备注信息"></el-input>
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="saveHeartRate">保存记录</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <!-- 健康数据历史记录 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">健康数据历史记录</h3>
        </div>
        
        <div class="card-body">
          <el-tabs v-model="historyTab" class="history-tabs">
            <!-- 血压历史 -->
            <el-tab-pane label="血压历史" name="bloodPressureHistory">
              <el-table :data="bloodPressureRecords" stripe style="width: 100%;">
                <el-table-column prop="measureTime" label="测量时间" width="180" />
                <el-table-column label="血压值" width="150">
                  <template #default="scope">
                    <span :class="getBloodPressureClass(scope.row)">{{ scope.row.systolic }}/{{ scope.row.diastolic }} mmHg</span>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <span :class="getBloodPressureStatusClass(scope.row)">{{ getBloodPressureStatus(scope.row) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="notes" label="备注" />
              </el-table>
            </el-tab-pane>
            
            <!-- 血糖历史 -->
            <el-tab-pane label="血糖历史" name="bloodSugarHistory">
              <el-table :data="bloodSugarRecords" stripe style="width: 100%;">
                <el-table-column prop="measureTime" label="测量时间" width="180" />
                <el-table-column label="血糖值" width="120">
                  <template #default="scope">
                    <span :class="getBloodSugarClass(scope.row)">{{ scope.row.level }} mmol/L</span>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="测量状态" width="100" />
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <span :class="getBloodSugarStatusClass(scope.row)">{{ getBloodSugarStatus(scope.row) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="notes" label="备注" />
              </el-table>
            </el-tab-pane>
            
            <!-- 心率历史 -->
            <el-tab-pane label="心率历史" name="heartRateHistory">
              <el-table :data="heartRateRecords" stripe style="width: 100%;">
                <el-table-column prop="measureTime" label="测量时间" width="180" />
                <el-table-column label="心率值" width="120">
                  <template #default="scope">
                    <span :class="getHeartRateClass(scope.row)">{{ scope.row.rate }} 次/分</span>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <span :class="getHeartRateStatusClass(scope.row)">{{ getHeartRateStatus(scope.row) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="notes" label="备注" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <!-- 健康建议卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">健康建议</h3>
        </div>
        
        <div class="card-body">
          <div class="health-tips">
            <el-alert
              v-for="(tip, index) in healthTips"
              :key="index"
              :type="tip.type"
              :title="tip.title"
              :description="tip.description"
              show-icon
              class="health-tip"
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
import { Edit, Warning, Link } from '@element-plus/icons-vue'

// 标签页切换
const activeTab = ref('bloodPressure')
const historyTab = ref('bloodPressureHistory')

// 当前日期
const currentDate = ref(new Date().toISOString().slice(0, 10))

// 显示录入表单
const showInputForm = ref(false)

// 今日健康数据
const todayBloodPressure = reactive({
  systolic: 120,
  diastolic: 80
})

const todayBloodSugar = reactive({
  level: 5.6,
  status: '空腹'
})

const todayHeartRate = reactive({
  rate: 75
})

// 血压记录表单
const bloodPressureForm = reactive({
  systolic: 0,
  diastolic: 0,
  measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  notes: ''
})

// 血糖记录表单
const bloodSugarForm = reactive({
  level: 0,
  measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  status: '空腹',
  notes: ''
})

// 心率记录表单
const heartRateForm = reactive({
  rate: 0,
  measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  notes: ''
})

// 健康记录列表
const bloodPressureRecords = ref([
  { measureTime: '2026-01-06 08:00', systolic: 122, diastolic: 82, notes: '早晨测量' },
  { measureTime: '2026-01-05 18:00', systolic: 118, diastolic: 78, notes: '晚上测量' },
  { measureTime: '2026-01-04 08:30', systolic: 125, diastolic: 85, notes: '早晨测量' }
])

const bloodSugarRecords = ref([
  { measureTime: '2026-01-06 07:30', level: 5.8, status: '空腹', notes: '早餐前' },
  { measureTime: '2026-01-05 12:30', level: 7.2, status: '餐后', notes: '午餐后2小时' },
  { measureTime: '2026-01-04 07:45', level: 5.5, status: '空腹', notes: '早餐前' }
])

const heartRateRecords = ref([
  { measureTime: '2026-01-06 09:00', rate: 72, notes: '平静状态' },
  { measureTime: '2026-01-05 15:00', rate: 78, notes: '活动后' },
  { measureTime: '2026-01-04 20:00', rate: 68, notes: '睡前' }
])

// 健康建议
const healthTips = ref([
  {
    type: 'success',
    title: '血压正常',
    description: '您的血压在正常范围内，请继续保持良好的生活习惯。'
  },
  {
    type: 'info',
    title: '血糖稳定',
    description: '您的血糖水平稳定，建议继续保持规律的饮食和运动。'
  },
  {
    type: 'warning',
    title: '心率正常',
    description: '您的心率在正常范围内，建议保持适当的运动锻炼。'
  }
])

// 获取每日数据
const fetchDailyData = () => {
  // 这里可以根据选择的日期获取对应的数据
  console.log('获取日期数据:', currentDate.value)
  // 暂时使用模拟数据
  ElMessage.info('已切换到 ' + currentDate.value + ' 的数据')
}

// 保存血压记录
const saveBloodPressure = async () => {
  if (!bloodPressureForm.systolic || !bloodPressureForm.diastolic) {
    ElMessage.warning('请填写完整的血压记录信息')
    return
  }
  
  try {
    // 这里应该调用API保存数据
    console.log('保存血压记录:', bloodPressureForm)
    
    // 更新今日数据
    todayBloodPressure.systolic = bloodPressureForm.systolic
    todayBloodPressure.diastolic = bloodPressureForm.diastolic
    
    // 添加到历史记录
    bloodPressureRecords.value.unshift({
      measureTime: bloodPressureForm.measureTime,
      systolic: bloodPressureForm.systolic,
      diastolic: bloodPressureForm.diastolic,
      notes: bloodPressureForm.notes
    })
    
    ElMessage.success('血压记录保存成功')
    
    // 重置表单
    Object.assign(bloodPressureForm, {
      systolic: 0,
      diastolic: 0,
      measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      notes: ''
    })
  } catch (error) {
    ElMessage.error('血压记录保存失败')
    console.error('保存血压记录失败:', error)
  }
}

// 保存血糖记录
const saveBloodSugar = async () => {
  if (!bloodSugarForm.level) {
    ElMessage.warning('请填写完整的血糖记录信息')
    return
  }
  
  try {
    // 这里应该调用API保存数据
    console.log('保存血糖记录:', bloodSugarForm)
    
    // 更新今日数据
    todayBloodSugar.level = bloodSugarForm.level
    todayBloodSugar.status = bloodSugarForm.status
    
    // 添加到历史记录
    bloodSugarRecords.value.unshift({
      measureTime: bloodSugarForm.measureTime,
      level: bloodSugarForm.level,
      status: bloodSugarForm.status,
      notes: bloodSugarForm.notes
    })
    
    ElMessage.success('血糖记录保存成功')
    
    // 重置表单
    Object.assign(bloodSugarForm, {
      level: 0,
      measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      status: '空腹',
      notes: ''
    })
  } catch (error) {
    ElMessage.error('血糖记录保存失败')
    console.error('保存血糖记录失败:', error)
  }
}

// 保存心率记录
const saveHeartRate = async () => {
  if (!heartRateForm.rate) {
    ElMessage.warning('请填写完整的心率记录信息')
    return
  }
  
  try {
    // 这里应该调用API保存数据
    console.log('保存心率记录:', heartRateForm)
    
    // 更新今日数据
    todayHeartRate.rate = heartRateForm.rate
    
    // 添加到历史记录
    heartRateRecords.value.unshift({
      measureTime: heartRateForm.measureTime,
      rate: heartRateForm.rate,
      notes: heartRateForm.notes
    })
    
    ElMessage.success('心率记录保存成功')
    
    // 重置表单
    Object.assign(heartRateForm, {
      rate: 0,
      measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      notes: ''
    })
  } catch (error) {
    ElMessage.error('心率记录保存失败')
    console.error('保存心率记录失败:', error)
  }
}

// 获取血压值样式
const getBloodPressureClass = (row) => {
  if (row.systolic > 140 || row.diastolic > 90) {
    return 'blood-pressure-high'
  } else if (row.systolic < 90 || row.diastolic < 60) {
    return 'blood-pressure-low'
  }
  return 'blood-pressure-normal'
}

// 获取血压状态
const getBloodPressureStatus = (row) => {
  if (row.systolic > 140 || row.diastolic > 90) {
    return '偏高'
  } else if (row.systolic < 90 || row.diastolic < 60) {
    return '偏低'
  }
  return '正常'
}

// 获取血压状态样式
const getBloodPressureStatusClass = (row) => {
  if (row.systolic > 140 || row.diastolic > 90) {
    return 'status-high'
  } else if (row.systolic < 90 || row.diastolic < 60) {
    return 'status-low'
  }
  return 'status-normal'
}

// 获取血糖值样式
const getBloodSugarClass = (row) => {
  if ((row.status === '空腹' && row.level > 7.0) || 
      (row.status === '餐后' && row.level > 10.0)) {
    return 'blood-sugar-high'
  } else if (row.level < 3.9) {
    return 'blood-sugar-low'
  }
  return 'blood-sugar-normal'
}

// 获取血糖状态
const getBloodSugarStatus = (row) => {
  if ((row.status === '空腹' && row.level > 7.0) || 
      (row.status === '餐后' && row.level > 10.0)) {
    return '偏高'
  } else if (row.level < 3.9) {
    return '偏低'
  }
  return '正常'
}

// 获取血糖状态样式
const getBloodSugarStatusClass = (row) => {
  if ((row.status === '空腹' && row.level > 7.0) || 
      (row.status === '餐后' && row.level > 10.0)) {
    return 'status-high'
  } else if (row.level < 3.9) {
    return 'status-low'
  }
  return 'status-normal'
}

// 获取心率值样式
const getHeartRateClass = (row) => {
  if (row.rate > 100) {
    return 'heart-rate-high'
  } else if (row.rate < 60) {
    return 'heart-rate-low'
  }
  return 'heart-rate-normal'
}

// 获取心率状态
const getHeartRateStatus = (row) => {
  if (row.rate > 100) {
    return '偏快'
  } else if (row.rate < 60) {
    return '偏慢'
  }
  return '正常'
}

// 获取心率状态样式
const getHeartRateStatusClass = (row) => {
  if (row.rate > 100) {
    return 'status-high'
  } else if (row.rate < 60) {
    return 'status-low'
  }
  return 'status-normal'
}

// 切换录入表单
const toggleInputForm = () => {
  showInputForm.value = !showInputForm.value
}

// 组件挂载时获取数据
onMounted(() => {
  console.log('组件挂载')
  // 这里可以调用API获取初始数据
})
</script>

<style scoped>
.health-monitoring-container {
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

.health-tabs {
  width: 100%;
}

.tab-content {
  margin-top: 20px;
}

.form-container {
  width: 100%;
}

.health-form {
  width: 100%;
}

.history-tabs {
  width: 100%;
}

/* 日期选择器 */
.date-picker {
  width: 180px;
}

/* 每日健康数据概览 */
.daily-overview {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.overview-card {
  flex: 1;
  min-width: 280px;
  padding: 24px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.blood-pressure-card {
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  border: 1px solid #fbc4c4;
}

.blood-sugar-card {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #93c5fd;
}

.heart-rate-card {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 1px solid #86efac;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.blood-pressure-icon {
  background: #fee2e2;
  color: #dc2626;
  border: 2px solid #fca5a5;
}

.blood-sugar-icon {
  background: #dbeafe;
  color: #2563eb;
  border: 2px solid #93c5fd;
}

.heart-rate-icon {
  background: #dcfce7;
  color: #16a34a;
  border: 2px solid #86efac;
}

.overview-content {
  flex: 1;
}

.overview-content h4 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #374151;
}

.overview-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.overview-status {
  font-size: 14px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

/* 健康建议 */
.health-tips {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.health-tip {
  width: 100%;
}

/* 状态样式 */
.status-normal {
  color: #16a34a;
  background: #dcfce7;
}

.status-high {
  color: #dc2626;
  background: #fee2e2;
}

.status-low {
  color: #d97706;
  background: #fef3c7;
}

/* 健康数据样式 */
.blood-pressure-normal {
  color: #67c23a;
  font-weight: bold;
}

.blood-pressure-high {
  color: #f56c6c;
  font-weight: bold;
}

.blood-pressure-low {
  color: #e6a23c;
  font-weight: bold;
}

.blood-sugar-normal {
  color: #67c23a;
  font-weight: bold;
}

.blood-sugar-high {
  color: #f56c6c;
  font-weight: bold;
}

.blood-sugar-low {
  color: #e6a23c;
  font-weight: bold;
}

.heart-rate-normal {
  color: #67c23a;
  font-weight: bold;
}

.heart-rate-high {
  color: #f56c6c;
  font-weight: bold;
}

.heart-rate-low {
  color: #e6a23c;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .daily-overview {
    flex-direction: column;
  }
  
  .overview-card {
    min-width: 100%;
  }
}

@media (max-width: 768px) {
  .health-monitoring-container {
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
  
  .date-picker {
    width: 100%;
  }
  
  .overview-card {
    padding: 16px;
    gap: 16px;
  }
  
  .overview-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .overview-value {
    font-size: 20px;
  }
}
</style>