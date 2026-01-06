<template>
  <div class="health-monitoring-container">
    <div class="page-header">
      <h2 class="page-title">健康监测</h2>
      <p class="page-subtitle">实时记录和监测老人的健康数据</p>
    </div>
    
    <div class="content-section">
      <!-- 健康数据录入卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">日常健康数据录入</h3>
        </div>
        
        <div class="card-body">
          <el-tabs v-model="activeTab" class="health-tabs">
            <!-- 血压记录 -->
            <el-tab-pane label="血压记录" name="bloodPressure">
              <div class="tab-content">
                <el-form :model="bloodPressureForm" label-width="120px" class="health-form">
                  <el-form-item label="老人姓名" required>
                    <el-select v-model="bloodPressureForm.residentId" placeholder="请选择老人姓名" style="width: 200px;">
                      <el-option
                        v-for="resident in residentsList"
                        :key="resident.id"
                        :label="resident.name"
                        :value="resident.id"
                      />
                    </el-select>
                  </el-form-item>
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
                
                <!-- 血压记录列表 -->
                <div class="record-list">
                  <h4>近期记录</h4>
                  <el-table :data="bloodPressureRecords" stripe style="width: 100%;" max-height="300">
                    <el-table-column prop="measureTime" label="测量时间" width="180" />
                    <el-table-column label="血压值" width="150">
                      <template #default="scope">
                        <span :class="getBloodPressureClass(scope.row)">{{ scope.row.systolic }}/{{ scope.row.diastolic }} mmHg</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="notes" label="备注" />
                  </el-table>
                </div>
              </div>
            </el-tab-pane>
            
            <!-- 血糖记录 -->
            <el-tab-pane label="血糖记录" name="bloodSugar">
              <div class="tab-content">
                <el-form :model="bloodSugarForm" label-width="120px" class="health-form">
                  <el-form-item label="老人姓名" required>
                    <el-select v-model="bloodSugarForm.residentId" placeholder="请选择老人姓名" style="width: 200px;">
                      <el-option
                        v-for="resident in residentsList"
                        :key="resident.id"
                        :label="resident.name"
                        :value="resident.id"
                      />
                    </el-select>
                  </el-form-item>
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
                
                <!-- 血糖记录列表 -->
                <div class="record-list">
                  <h4>近期记录</h4>
                  <el-table :data="bloodSugarRecords" stripe style="width: 100%;" max-height="300">
                    <el-table-column prop="measureTime" label="测量时间" width="180" />
                    <el-table-column label="血糖值" width="120">
                      <template #default="scope">
                        <span :class="getBloodSugarClass(scope.row)">{{ scope.row.level }} mmol/L</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="status" label="测量状态" width="100" />
                    <el-table-column prop="notes" label="备注" />
                  </el-table>
                </div>
              </div>
            </el-tab-pane>
            
            <!-- 心率记录 -->
            <el-tab-pane label="心率记录" name="heartRate">
              <div class="tab-content">
                <el-form :model="heartRateForm" label-width="120px" class="health-form">
                  <el-form-item label="老人姓名" required>
                    <el-select v-model="heartRateForm.residentId" placeholder="请选择老人姓名" style="width: 200px;">
                      <el-option
                        v-for="resident in residentsList"
                        :key="resident.id"
                        :label="resident.name"
                        :value="resident.id"
                      />
                    </el-select>
                  </el-form-item>
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
                
                <!-- 心率记录列表 -->
                <div class="record-list">
                  <h4>近期记录</h4>
                  <el-table :data="heartRateRecords" stripe style="width: 100%;" max-height="300">
                    <el-table-column prop="measureTime" label="测量时间" width="180" />
                    <el-table-column label="心率值" width="120">
                      <template #default="scope">
                        <span :class="getHeartRateClass(scope.row)">{{ scope.row.rate }} 次/分</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="notes" label="备注" />
                  </el-table>
                </div>
              </div>
            </el-tab-pane>
            

          </el-tabs>
        </div>
      </div>
      
      <!-- 异常预警卡片 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">健康异常预警</h3>
          <el-button type="success" @click="checkAllHealthData">
            <el-icon><Refresh /></el-icon>
            检查所有数据
          </el-button>
        </div>
        
        <div class="card-body">
          <div class="alerts-container">
            <div v-if="healthAlerts.length === 0" class="no-alerts">
              <el-empty description="暂无健康异常预警" />
            </div>
            <el-alert
              v-for="(alert, index) in healthAlerts"
              :key="index"
              :type="alert.type === 'danger' ? 'danger' : 'warning'"
              :title="alert.title"
              :description="alert.description"
              show-icon
              :closable="true"
              @close="removeAlert(index)"
              class="health-alert"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
// 导入健康监测相关API
import {
  getBloodPressureRecords,
  addBloodPressureRecord,
  getBloodSugarRecords,
  addBloodSugarRecord,
  getHeartRateRecords,
  addHeartRateRecord,
  getHealthAlerts,
  getAllResidents
} from '@/api/resident'

// 标签页切换
const activeTab = ref('bloodPressure')

// 老人列表
const residentsList = ref([])

// 血压记录表单
const bloodPressureForm = reactive({
  residentId: '',
  systolic: 0,
  diastolic: 0,
  measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  notes: ''
})

// 血糖记录表单
const bloodSugarForm = reactive({
  residentId: '',
  level: 0,
  measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  status: '空腹',
  notes: ''
})

// 心率记录表单
const heartRateForm = reactive({
  residentId: '',
  rate: 0,
  measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  notes: ''
})

// 健康记录列表
const bloodPressureRecords = ref([])
const bloodSugarRecords = ref([])
const heartRateRecords = ref([])

// 健康异常预警
const healthAlerts = ref([])

// 获取血压记录
const fetchBloodPressureRecords = async () => {
  try {
    const response = await getBloodPressureRecords()
    if (response.data.success) {
      bloodPressureRecords.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '获取血压记录失败')
    }
  } catch (error) {
    ElMessage.error('获取血压记录失败')
    console.error('获取血压记录失败:', error)
  }
}

// 获取血糖记录
const fetchBloodSugarRecords = async () => {
  try {
    const response = await getBloodSugarRecords()
    if (response.data.success) {
      bloodSugarRecords.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '获取血糖记录失败')
    }
  } catch (error) {
    ElMessage.error('获取血糖记录失败')
    console.error('获取血糖记录失败:', error)
  }
}

// 获取心率记录
const fetchHeartRateRecords = async () => {
  try {
    const response = await getHeartRateRecords()
    if (response.data.success) {
      heartRateRecords.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '获取心率记录失败')
    }
  } catch (error) {
    ElMessage.error('获取心率记录失败')
    console.error('获取心率记录失败:', error)
  }
}

// 获取老人列表
const fetchResidentsList = async () => {
  try {
    const response = await getAllResidents()
    if (response.data.success) {
      // 过滤出当前入住的老人
      residentsList.value = (response.data.data || []).filter(resident => resident.status === '入住')
    } else {
      ElMessage.error(response.data.message || '获取老人列表失败')
    }
  } catch (error) {
    ElMessage.error('获取老人列表失败')
    console.error('获取老人列表失败:', error)
  }
}

// 获取健康预警
const fetchHealthAlerts = async () => {
  try {
    const response = await getHealthAlerts()
    if (response.data.success) {
      healthAlerts.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '获取健康预警失败')
    }
  } catch (error) {
    ElMessage.error('获取健康预警失败')
    console.error('获取健康预警失败:', error)
  }
}

// 保存血压记录
const saveBloodPressure = async () => {
  if (!bloodPressureForm.residentId || !bloodPressureForm.systolic || !bloodPressureForm.diastolic) {
    ElMessage.warning('请填写完整的血压记录信息')
    return
  }
  
  try {
    const response = await addBloodPressureRecord(bloodPressureForm)
    if (response.data.success) {
      ElMessage.success(response.data.message || '血压记录保存成功')
      fetchBloodPressureRecords()
      fetchHealthAlerts()
      
      // 重置表单
      Object.assign(bloodPressureForm, {
        residentId: '',
        systolic: 0,
        diastolic: 0,
        measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
        notes: ''
      })
    } else {
      ElMessage.error(response.data.message || '血压记录保存失败')
    }
  } catch (error) {
    ElMessage.error('血压记录保存失败')
    console.error('保存血压记录失败:', error)
  }
}

// 保存血糖记录
const saveBloodSugar = async () => {
  if (!bloodSugarForm.residentId || !bloodSugarForm.level) {
    ElMessage.warning('请填写完整的血糖记录信息')
    return
  }
  
  try {
    const response = await addBloodSugarRecord(bloodSugarForm)
    if (response.data.success) {
      ElMessage.success(response.data.message || '血糖记录保存成功')
      fetchBloodSugarRecords()
      fetchHealthAlerts()
      
      // 重置表单
      Object.assign(bloodSugarForm, {
        residentId: '',
        level: 0,
        measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
        status: '空腹',
        notes: ''
      })
    } else {
      ElMessage.error(response.data.message || '血糖记录保存失败')
    }
  } catch (error) {
    ElMessage.error('血糖记录保存失败')
    console.error('保存血糖记录失败:', error)
  }
}

// 保存心率记录
const saveHeartRate = async () => {
  if (!heartRateForm.residentId || !heartRateForm.rate) {
    ElMessage.warning('请填写完整的心率记录信息')
    return
  }
  
  try {
    const response = await addHeartRateRecord(heartRateForm)
    if (response.data.success) {
      ElMessage.success(response.data.message || '心率记录保存成功')
      fetchHeartRateRecords()
      fetchHealthAlerts()
      
      // 重置表单
      Object.assign(heartRateForm, {
        residentId: '',
        rate: 0,
        measureTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
        notes: ''
      })
    } else {
      ElMessage.error(response.data.message || '心率记录保存失败')
    }
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

// 获取心率值样式
const getHeartRateClass = (row) => {
  if (row.rate > 100) {
    return 'heart-rate-high'
  } else if (row.rate < 60) {
    return 'heart-rate-low'
  }
  return 'heart-rate-normal'
}

// 检查所有健康数据
const checkAllHealthData = async () => {
  try {
    await fetchHealthAlerts()
    ElMessage.success('健康数据检查完成')
  } catch (error) {
    ElMessage.error('健康数据检查失败')
    console.error('检查健康数据失败:', error)
  }
}

// 移除预警
const removeAlert = (index) => {
  healthAlerts.value.splice(index, 1)
}



// 组件挂载时获取数据
onMounted(() => {
  fetchResidentsList()
  fetchBloodPressureRecords()
  fetchBloodSugarRecords()
  fetchHeartRateRecords()
  fetchHealthAlerts()
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
  display: flex;
  gap: 24px;
  margin-top: 20px;
}

.health-form {
  flex: 1;
}

.record-list {
  flex: 1;
}

.record-list h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.alerts-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.no-alerts {
  text-align: center;
  padding: 40px 0;
}

.health-alert {
  width: 100%;
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
  .tab-content {
    flex-direction: column;
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
}
</style>