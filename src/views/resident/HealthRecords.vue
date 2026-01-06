<template>
  <div class="health-records-container">
    <div class="page-header">
      <h2 class="page-title">健康档案</h2>
      <p class="page-subtitle">管理老人的核心健康信息</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">核心健康档案</h3>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.name"
              placeholder="搜索老人姓名"
              prefix-icon="Search"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 健康档案列表 -->
          <div class="table-container">
            <el-table
              :data="healthRecordsList"
              border
              stripe
              style="width: 100%"
              :row-class-name="tableRowClassName"
              v-loading="loading"
              element-loading-text="加载中..."
              element-loading-background="rgba(255, 255, 255, 0.8)"
              empty-text="暂无健康档案数据"
              :row-style="tableRowStyle"
            >
              <el-table-column label="ID" width="80" align="center">
                <template #default="scope">
                  {{ scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column prop="residentName" label="老人姓名" width="150" sortable />
              <el-table-column prop="roomNumber" label="房间号" width="120" align="center" sortable />
              <el-table-column prop="bedNumber" label="床位号" width="120" align="center" sortable />
              <el-table-column prop="medicalHistory" label="既往病史" min-width="200">
                <template #default="scope">
                  <div class="medical-history">
                    {{ scope.row.medicalHistory }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="allergyHistory" label="过敏史" min-width="200">
                <template #default="scope">
                  <div class="allergy-history">
                    {{ scope.row.allergyHistory || '无' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="bloodType" label="血型" width="100" align="center" />
              <el-table-column prop="height" label="身高(cm)" width="120" align="center" />
              <el-table-column prop="weight" label="体重(kg)" width="120" align="center" />
              <el-table-column label="BMI指数" width="120" align="center" sortable>
                <template #default="scope">
                  <el-tag :type="getBmiTagType(scope.row)">
                    {{ calculateBMI(scope.row) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="健康状态" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="getHealthStatusType(scope.row)">
                    {{ getHealthStatusText(scope.row) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="updateTime" label="更新时间" width="180" align="center" sortable>
                <template #default="scope">
                  {{ formatDate(scope.row.updateTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" align="center">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="showEditDialog(scope.row)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
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
    
    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="healthFormRef"
        :model="healthForm"
        :rules="healthRules"
        label-width="120px"
      >
        <div class="form-row">
          <el-form-item label="老人姓名" prop="residentName" class="form-item">
            <el-input v-model="healthForm.residentName" placeholder="请输入老人姓名" />
          </el-form-item>
          <el-form-item label="房间号" prop="roomNumber" class="form-item">
            <el-input v-model="healthForm.roomNumber" type="number" placeholder="请输入房间号" min="1" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="床位号" prop="bedNumber" class="form-item">
            <el-input v-model="healthForm.bedNumber" type="number" placeholder="请输入床位号" min="1" />
          </el-form-item>
          <el-form-item label="血型" prop="bloodType" class="form-item">
            <el-select v-model="healthForm.bloodType" placeholder="请选择血型" clearable>
              <el-option label="A型" value="A型" />
              <el-option label="B型" value="B型" />
              <el-option label="AB型" value="AB型" />
              <el-option label="O型" value="O型" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="身高(cm)" prop="height" class="form-item">
            <el-input v-model="healthForm.height" type="number" placeholder="请输入身高" min="50" max="250" step="0.1" />
          </el-form-item>
          <el-form-item label="体重(kg)" prop="weight" class="form-item">
            <el-input v-model="healthForm.weight" type="number" placeholder="请输入体重" min="20" max="200" step="0.1" />
          </el-form-item>
        </div>
        
        <el-form-item label="既往病史" prop="medicalHistory">
          <el-input
            v-model="healthForm.medicalHistory"
            type="textarea"
            :rows="5"
            placeholder="请输入既往病史"
          />
        </el-form-item>
        
        <el-form-item label="过敏史" prop="allergyHistory">
          <el-input
            v-model="healthForm.allergyHistory"
            type="textarea"
            :rows="3"
            placeholder="请输入过敏史，无则填写'无'"
          />
        </el-form-item>
        
        <el-form-item label="健康状况说明">
          <el-input
            v-model="healthForm.healthNotes"
            type="textarea"
            :rows="4"
            placeholder="请输入健康状况说明"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getHealthRecords, updateHealthRecord, addHealthRecord } from '@/api/resident'

// 搜索和筛选参数
const searchParams = reactive({
  name: ''
})

// 表格数据
const healthRecordsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('编辑健康档案')

// 表单引用
const healthFormRef = ref()

// 表单数据
const healthForm = reactive({
  id: null,
  residentName: '',
  roomNumber: '',
  bedNumber: '',
  medicalHistory: '',
  allergyHistory: '',
  bloodType: '',
  height: '',
  weight: '',
  healthNotes: ''
})

// 表单验证规则
const healthRules = {
  residentName: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }, { pattern: /^\d+$/, message: '房间号必须为数字', trigger: 'blur' }],
  bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }, { pattern: /^\d+$/, message: '床位号必须为数字', trigger: 'blur' }],
  medicalHistory: [{ required: true, message: '请输入既往病史', trigger: 'blur' }],
  allergyHistory: [{ required: true, message: '请输入过敏史', trigger: 'blur' }],
  bloodType: [{ required: true, message: '请选择血型', trigger: 'change' }],
  height: [
    { required: true, message: '请输入身高', trigger: 'blur' },
    { type: 'number', min: 50, max: 250, message: '身高必须在50-250cm之间', trigger: 'blur,change' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', min: 20, max: 200, message: '体重必须在20-200kg之间', trigger: 'blur,change' }
  ]
}

// 生命周期
onMounted(() => {
  fetchHealthRecords()
})

// 获取健康档案列表
const fetchHealthRecords = async () => {
  loading.value = true
  try {
    const response = await getHealthRecords({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    console.log('健康档案列表响应:', response)
    
    // 增强数据格式兼容性，支持多种响应格式
    let processedData = { list: [], total: 0 }
    
    if (response && response.data) {
      const data = response.data
      
      // 兼容不同响应结构
      if (typeof data === 'object') {
        if (data.success !== undefined) {
          // 结构1: { success: boolean, message: string, data: { list: [], total: number } }
          if (data.success) {
            processedData = data.data || { list: [], total: 0 }
            // 兼容不同数据字段名
            processedData.list = processedData.list || processedData.records || processedData.items || []
            processedData.total = processedData.total || processedData.totalCount || processedData.list.length
          } else {
            ElMessage.error(data.message || '获取健康档案列表失败')
          }
        } else if (data.list && Array.isArray(data.list)) {
          // 结构2: { list: [], total: number }
          processedData = data
          processedData.total = processedData.total || processedData.list.length
        } else if (Array.isArray(data)) {
          // 结构3: [{}, {}, ...] 直接返回数组
          processedData.list = data
          processedData.total = data.length
        } else if (data.records && Array.isArray(data.records)) {
          // 结构4: { records: [], total: number } (PageHelper格式)
          processedData.list = data.records
          processedData.total = data.total || data.records.length
        } else {
          ElMessage.error('获取健康档案列表失败: 数据格式不正确')
        }
      } else {
        ElMessage.error('获取健康档案列表失败: 数据格式不正确')
      }
    } else if (Array.isArray(response)) {
      // 直接返回数组的情况
      processedData.list = response
      processedData.total = response.length
    } else {
      ElMessage.error('获取健康档案列表失败: 数据格式不正确')
    }
    
    // 确保数据类型正确
    healthRecordsList.value = Array.isArray(processedData.list) ? processedData.list : []
    total.value = typeof processedData.total === 'number' ? processedData.total : healthRecordsList.value.length
    
    // 为每条记录添加默认值，防止表格显示异常
    healthRecordsList.value = healthRecordsList.value.map(record => ({
      ...record,
      id: record.id || '',
      residentName: record.residentName || '未知',
      roomNumber: record.roomNumber || '',
      bedNumber: record.bedNumber || '',
      medicalHistory: record.medicalHistory || '无',
      allergyHistory: record.allergyHistory || '无',
      bloodType: record.bloodType || '',
      height: record.height || '',
      weight: record.weight || '',
      updateTime: record.updateTime || ''
    }))
    
  } catch (error) {
    console.error('获取健康档案列表失败:', error)
    // 改进错误处理
    if (error.response) {
      // 服务器返回了错误响应
      const errorMsg = error.response.data?.message || error.response.statusText || '获取健康档案列表失败'
      ElMessage.error(`获取健康档案列表失败: ${errorMsg}`)
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error('获取健康档案列表失败: 服务器无响应，请检查网络连接或服务器状态')
    } else if (error.message) {
      // 请求配置错误
      ElMessage.error(`获取健康档案列表失败: ${error.message}`)
    } else {
      // 其他错误
      ElMessage.error('获取健康档案列表失败: 发生未知错误')
    }
    // 确保列表为空，避免表格显示异常
    healthRecordsList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchHealthRecords()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchHealthRecords()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 表格行样式
const tableRowStyle = ({ row }) => {
  // 根据健康状态添加不同的背景色
  const statusType = getHealthStatusType(row)
  if (statusType === 'warning') {
    return { backgroundColor: 'rgba(250, 173, 20, 0.05)' }
  } else if (statusType === 'danger') {
    return { backgroundColor: 'rgba(245, 34, 45, 0.05)' }
  }
  return {}
}

// 计算BMI指数
const calculateBMI = (row) => {
  if (!row.height || !row.weight) return '-'
  const height = Number(row.height) / 100 // 转换为米
  const weight = Number(row.weight)
  if (height === 0) return '-'
  const bmi = weight / (height * height)
  return bmi.toFixed(1)
}

// 获取BMI标签类型
const getBmiTagType = (row) => {
  const bmi = parseFloat(calculateBMI(row))
  if (isNaN(bmi)) return 'info'
  if (bmi < 18.5) return 'warning'
  if (bmi >= 18.5 && bmi < 24) return 'success'
  if (bmi >= 24 && bmi < 28) return 'warning'
  return 'danger'
}

// 获取健康状态标签类型
const getHealthStatusType = (row) => {
  // 简单的健康状态判断，可根据实际需求扩展
  const bmi = parseFloat(calculateBMI(row))
  const hasSeriousDisease = row.medicalHistory && 
    (row.medicalHistory.includes('高血压') || 
     row.medicalHistory.includes('糖尿病') || 
     row.medicalHistory.includes('心脏病') || 
     row.medicalHistory.includes('癌症'))
  
  if (hasSeriousDisease) return 'danger'
  if (isNaN(bmi)) return 'info'
  if (bmi < 18.5 || bmi >= 28) return 'warning'
  return 'success'
}

// 获取健康状态文本
const getHealthStatusText = (row) => {
  const bmi = parseFloat(calculateBMI(row))
  const hasSeriousDisease = row.medicalHistory && 
    (row.medicalHistory.includes('高血压') || 
     row.medicalHistory.includes('糖尿病') || 
     row.medicalHistory.includes('心脏病') || 
     row.medicalHistory.includes('癌症'))
  
  if (hasSeriousDisease) return '重点关注'
  if (isNaN(bmi)) return '待评估'
  if (bmi < 18.5) return '偏瘦'
  if (bmi >= 18.5 && bmi < 24) return '正常'
  if (bmi >= 24 && bmi < 28) return '超重'
  return '肥胖'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '-'
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化失败:', error)
    return '-'
  }
}

// 重置表单
const resetForm = () => {
  if (healthFormRef.value) {
    healthFormRef.value.resetFields()
  }
  Object.assign(healthForm, {
    id: null,
    residentName: '',
    roomNumber: '',
    bedNumber: '',
    medicalHistory: '',
    allergyHistory: '',
    bloodType: '',
    height: '',
    weight: '',
    healthNotes: ''
  })
}



// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑健康档案'
  Object.assign(healthForm, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await healthFormRef.value.validate()
    
    let response
    let successMessage
    let errorMessage
    
    if (healthForm.id) {
      // 更新健康档案
      response = await updateHealthRecord(healthForm)
      successMessage = '健康档案更新成功'
      errorMessage = '健康档案更新失败'
    } else {
      // 添加健康档案
      response = await addHealthRecord(healthForm)
      successMessage = '健康档案添加成功'
      errorMessage = '健康档案添加失败'
    }
    
    console.log('提交响应:', response)
    
    // 增强响应处理兼容性
    if (response && response.data) {
      const data = response.data
      if (data.success !== undefined) {
        if (data.success) {
          ElMessage.success(successMessage)
          dialogVisible.value = false
          fetchHealthRecords()
        } else {
          ElMessage.error(data.message || errorMessage)
        }
      } else {
        // 兼容不同响应格式
        ElMessage.success(successMessage)
        dialogVisible.value = false
        fetchHealthRecords()
      }
    } else {
      // 兼容直接返回成功的情况
      ElMessage.success(successMessage)
      dialogVisible.value = false
      fetchHealthRecords()
    }
  } catch (error) {
    console.error('提交失败:', error)
    
    // 表单验证失败，不显示额外提示
    if (error.name === 'Error' && error.message.includes('表单验证')) {
      return
    }
    
    // 区分不同类型的错误
    if (error.response) {
      // 服务器返回了错误响应
      const errorMsg = error.response.data?.message || error.response.statusText || '操作失败'
      ElMessage.error(`${healthForm.id ? '更新' : '添加'}健康档案失败: ${errorMsg}`)
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error(`${healthForm.id ? '更新' : '添加'}健康档案失败: 服务器无响应`)
    } else {
      // 请求配置错误
      ElMessage.error(`${healthForm.id ? '更新' : '添加'}健康档案失败: ${error.message}`)
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.health-records-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
}

/* 页面头部 */
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
  transition: all 0.3s ease;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
  transition: all 0.3s ease;
}

/* 内容区域 */
.content-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 卡片样式 */
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

/* 搜索筛选区域 */
.search-filter {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  width: 300px;
  transition: all 0.3s ease;
}

/* 表格容器 */
.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  background: white;
  border-radius: 4px;
  transition: all 0.3s ease;
}

/* 表格行样式 */
.medical-history,
.allergy-history {
  max-height: 60px;
  overflow-y: auto;
  text-overflow: ellipsis;
  padding: 0 4px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
}

/* 表格行样式 */
.even-row {
  background-color: #f5f7fa;
}

.odd-row {
  background-color: white;
}

/* 表单行样式 */
.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.form-item {
  flex: 1;
  min-width: 250px;
  transition: all 0.3s ease;
}

/* 输入框聚焦效果 */
:deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 2px rgba(144, 147, 153, 0.1) !important;
}

/* 按钮悬停效果 */
:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 表格加载动画 */
:deep(.el-loading-spinner) {
  margin-top: -20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-records-container {
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
  
  .search-input {
    width: 100%;
  }
  
  .search-filter {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-item {
    min-width: 100%;
  }
  
  .pagination {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .card-header {
    padding: 12px;
  }
  
  .card-body {
    padding: 12px;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>