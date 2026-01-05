<template>
  <div class="diet-management-container">
    <div class="page-header">
      <h2 class="page-title">膳食管理</h2>
      <p class="page-subtitle">管理老人的特殊饮食需求</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">膳食需求记录</h3>
          <el-button 
            type="primary" 
            @click="showAddDialog"
            size="large"
            class="add-btn"
          >
            <el-icon><Plus /></el-icon>
            添加需求
          </el-button>
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
              size="large"
            />
            <el-select
              v-model="searchParams.dietType"
              placeholder="筛选饮食类型"
              clearable
              class="filter-select"
              size="large"
            >
              <el-option label="糖尿病饮食" value="糖尿病饮食" />
              <el-option label="高血压饮食" value="高血压饮食" />
              <el-option label="高血脂饮食" value="高血脂饮食" />
              <el-option label="低盐饮食" value="低盐饮食" />
              <el-option label="低脂肪饮食" value="低脂肪饮食" />
              <el-option label="软食" value="软食" />
              <el-option label="其他" value="其他" />
            </el-select>
            <el-button 
              type="primary" 
              @click="handleSearch"
              size="large"
              class="search-btn"
            >
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 膳食需求卡片网格 -->
          <div class="table-container">
            <div class="diet-cards-grid">
              <div 
                v-for="(diet, index) in dietList" 
                :key="diet.id"
                class="diet-card"
              >
                <!-- 卡片头部 -->
                <div class="diet-card-header">
                  <div class="diet-id-section">
                    <span class="diet-id">ID: {{ index + 1 }}</span>
                    <el-tag 
                      :type="getDietTypeColor(diet.dietType)" 
                      class="diet-type-tag"
                    >
                      {{ getDietTypeName(diet.dietType) }}
                    </el-tag>
                  </div>
                  <div class="diet-dates">
                    <div class="date-item">
                      <el-icon><Calendar /></el-icon>
                      <span>{{ diet.createTime }}</span>
                    </div>
                  </div>
                </div>
                
                <!-- 卡片内容 -->
                <div class="diet-card-content">
                  <div class="resident-section">
                    <h3 class="resident-name">{{ diet.residentName }}</h3>
                    <div class="room-bed-info">
                      <el-icon><House /></el-icon>
                      <span>房间: {{ diet.roomNumber }} | 床位: {{ diet.bedNumber }}</span>
                    </div>
                  </div>
                  
                  <div class="description-section">
                    <div class="description-title">
                      <el-icon><Document /></el-icon>
                      <span>详细说明</span>
                    </div>
                    <div class="description-text">{{ diet.description }}</div>
                  </div>
                </div>
                
                <!-- 卡片底部 -->
                <div class="diet-card-footer">
                  <div class="action-buttons">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="showEditDialog(diet)"
                      class="edit-btn"
                    >
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="handleDelete(diet.id)"
                      class="delete-btn"
                    >
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="!dietList.length" class="empty-state">
              <el-empty 
                description="暂无膳食需求记录"
                :image-size="100"
              >
                <el-button type="primary" @click="showAddDialog">
                  <el-icon><Plus /></el-icon>
                  添加膳食需求
                </el-button>
              </el-empty>
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
              class="pagination-component"
              size="large"
            />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      center
      destroy-on-close
      class="diet-dialog"
    >
      <el-form
        ref="dietFormRef"
        :model="dietForm"
        :rules="dietRules"
        label-width="140px"
        size="large"
        class="diet-form"
      >
        <el-row :gutter="25">
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="residentName" class="form-item">
              <el-select
                v-model="dietForm.residentId"
                placeholder="请选择老人"
                size="large"
                class="form-input"
                :disabled="isEditMode"
                @change="handleResidentChange"
                filterable
              >
                <el-option
                  v-for="resident in residentList"
                  :key="resident.id"
                  :label="resident.name"
                  :value="resident.id"
                >
                  <span>{{ resident.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">
                    {{ resident.roomNumber }} - {{ resident.bedNumber }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber" class="form-item">
              <el-input
                v-model="dietForm.roomNumber"
                placeholder="自动填充"
                size="large"
                class="form-input"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="25">
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNumber" class="form-item">
              <el-input
                v-model="dietForm.bedNumber"
                placeholder="自动填充"
                size="large"
                class="form-input"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="饮食类型" prop="dietType" class="form-item">
              <el-select
                v-model="dietForm.dietType"
                placeholder="请选择饮食类型"
                size="large"
                class="form-select"
              >
                <el-option label="糖尿病饮食" value="糖尿病饮食" />
                <el-option label="高血压饮食" value="高血压饮食" />
                <el-option label="高血脂饮食" value="高血脂饮食" />
                <el-option label="低盐饮食" value="低盐饮食" />
                <el-option label="低脂肪饮食" value="低脂肪饮食" />
                <el-option label="软食" value="软食" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细说明" prop="description" class="form-item form-item-textarea">
          <el-input
            v-model="dietForm.description"
            type="textarea"
            :rows="5"
            placeholder="请输入详细说明"
            resize="vertical"
            class="form-textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            @click="dialogVisible = false" 
            size="large"
            class="footer-btn"
          >
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit"
            size="large"
            class="footer-btn primary-btn"
          >
            {{ isEditMode ? '更新' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Edit, 
  Delete, 
  Search, 
  House, 
  Document, 
  Calendar, 
  List,
  User
} from '@element-plus/icons-vue'
import { getDietList, addDiet, updateDiet, deleteDiet } from '@/api/diet'
import { getResidentList } from '@/api/resident'

// 搜索和筛选参数
const searchParams = reactive({
  name: '',
  dietType: ''
})

// 老人列表数据
const residentList = ref([])

// 表格数据
const dietList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加膳食需求')
const isEditMode = ref(false)

// 表单引用
const dietFormRef = ref()

// 表单数据
const dietForm = reactive({
  id: null,
  residentName: '',
  roomNumber: '',
  bedNumber: '',
  dietType: '',
  description: ''
})

// 表单验证规则
const dietRules = {
  residentName: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }],
  dietType: [{ required: true, message: '请选择饮食类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入详细说明', trigger: 'blur' }]
}

// 生命周期
onMounted(() => {
  fetchDietList()
  fetchResidentList()
})

// 获取老人列表
const fetchResidentList = async () => {
  try {
    // 先获取已添加膳食需求的老人ID列表
    let existingResidentIds = []
    try {
      const dietResponse = await getDietList({})
      if (dietResponse.data && dietResponse.data.success) {
        const dietListData = dietResponse.data.data.list || []
        existingResidentIds = dietListData.map(diet => diet.residentId || diet.resident_id)
      } else if (dietResponse.data && dietResponse.data.data) {
        const dietListData = dietResponse.data.data.list || dietResponse.data.data || []
        existingResidentIds = dietListData.map(diet => diet.residentId || diet.resident_id)
      }
    } catch (e) {
      console.error('获取膳食需求列表失败:', e)
    }
    
    // 获取老人列表并过滤
    const response = await getResidentList({})
    let allResidents = []
    if (response.data && response.data.success) {
      allResidents = response.data.data.list || []
    } else if (Array.isArray(response.data)) {
      allResidents = response.data
    } else if (response.data && response.data.data) {
      allResidents = response.data.data.list || response.data.data || []
    }
    
    // 过滤条件：
    // 1. 状态为'入住'的老人
    // 2. 未添加膳食需求的老人
    residentList.value = allResidents.filter(resident => 
      resident.status === '入住' && !existingResidentIds.includes(resident.id)
    )
  } catch (error) {
    console.error('获取老人列表失败:', error)
  }
}

// 选择老人后自动填充房间号和床位号
const handleResidentChange = (residentId) => {
  if (!residentId) {
    dietForm.roomNumber = ''
    dietForm.bedNumber = ''
    dietForm.residentName = ''
    return
  }
  const selectedResident = residentList.value.find(r => r.id === residentId)
  if (selectedResident) {
    dietForm.residentName = selectedResident.name
    dietForm.roomNumber = selectedResident.roomNumber
    dietForm.bedNumber = selectedResident.bedNumber
  }
}

// 获取膳食需求列表
const fetchDietList = async () => {
  loading.value = true
  try {
    // 构建查询参数，确保dietType正确传递
    const params = {}
    // 只有当dietType不为空时才添加到查询参数
    if (searchParams.dietType && searchParams.dietType !== '') {
      params.dietType = searchParams.dietType
    }
    // 只有当name不为空时才添加到查询参数
    if (searchParams.name && searchParams.name.trim() !== '') {
      params.name = searchParams.name.trim()
    }
    params.page = currentPage.value
    params.pageSize = pageSize.value
    
    console.log('请求参数:', params)
    const response = await getDietList(params)
    
    console.log('响应数据:', response)
    
    // 简化响应处理，适配不同的返回格式
    if (response.data) {
      if (response.data.success) {
        dietList.value = response.data.data.list || []
        total.value = response.data.data.total || 0
      } else {
        // 如果success为false，尝试直接使用data
        dietList.value = response.data.data || []
        total.value = response.data.data ? response.data.data.length : 0
        ElMessage.warning(response.data.message || '数据获取成功但状态标记为失败')
      }
    } else if (Array.isArray(response)) {
      // 兼容直接返回数组的情况
      dietList.value = response
      total.value = response.length
    } else {
      ElMessage.error('获取膳食需求列表失败: 数据格式不正确')
      dietList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取膳食需求列表失败:', error)
    ElMessage.error('获取膳食需求列表失败，请重试')
    dietList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchDietList()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchDietList()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 获取饮食类型显示名称
const getDietTypeName = (type) => {
  // 数据库中存储的是中文值，直接返回
  const typeMap = {
    '糖尿病饮食': '糖尿病饮食',
    '高血压饮食': '高血压饮食',
    '高血脂饮食': '高血脂饮食',
    '低盐饮食': '低盐饮食',
    '低脂肪饮食': '低脂肪饮食',
    '软食': '软食',
    '其他': '其他'
  }
  return typeMap[type] || type
}

// 获取饮食类型标签颜色
const getDietTypeColor = (type) => {
  const colorMap = {
    '糖尿病饮食': 'warning',
    '高血压饮食': 'danger',
    '高血脂饮食': 'success',
    '低盐饮食': 'info',
    '低脂肪饮食': 'primary',
    '软食': 'success',
    '其他': 'gray'
  }
  return colorMap[type] || 'default'
}

// 显示添加对话框
const showAddDialog = () => {
  isEditMode.value = false
  dialogTitle.value = '添加膳食需求'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEditMode.value = true
  dialogTitle.value = '编辑膳食需求'
  // 使用深拷贝确保包含完整数据，同时保持dietForm的响应式
  const deepCopiedRow = JSON.parse(JSON.stringify(row))
  Object.keys(dietForm).forEach(key => {
    dietForm[key] = deepCopiedRow[key]
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(dietForm, {
    id: null,
    residentName: '',
    roomNumber: '',
    bedNumber: '',
    dietType: '',
    description: ''
  })
  if (dietFormRef.value) {
    dietFormRef.value.resetFields()
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await dietFormRef.value.validate()
    
    let response
    let successMessage
    let errorMessage
    
    if (isEditMode.value) {
      // 编辑膳食需求
      response = await updateDiet(dietForm)
      successMessage = '编辑成功'
      errorMessage = '编辑失败'
    } else {
      // 添加膳食需求
      response = await addDiet(dietForm)
      successMessage = '添加成功'
      errorMessage = '添加失败'
    }
    
    // 简化响应处理，适配不同的返回格式
    if (response.data) {
      if (response.data.success || response.data) {
        ElMessage.success(successMessage)
        dialogVisible.value = false
        fetchDietList()
      } else {
        ElMessage.error(response.data.message || errorMessage)
      }
    } else {
      ElMessage.success(successMessage)
      dialogVisible.value = false
      fetchDietList()
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error' && error.message === '表单验证失败') {
      // 表单验证失败，不显示额外提示
      return
    }
    ElMessage.error('操作失败，请重试')
  }
}

// 删除记录
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteDiet(id)
      
      // 简化响应处理，适配不同的返回格式
      if (response.data) {
        if (response.data.success || response.data) {
          ElMessage.success('删除成功')
          // 刷新膳食需求列表和老人选择列表
          fetchDietList()
          fetchResidentList()
        } else {
          ElMessage.error(response.data.message || '删除失败')
        }
      } else {
        ElMessage.success('删除成功')
        fetchDietList()
        fetchResidentList()
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }).catch(() => {
    // 取消删除
  })
}
</script>

<style scoped>
/* 基础样式 */
.diet-management-container {
  width: 100%;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
  padding: 32px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #303133;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
  font-weight: 500;
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
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef2ff 100%);
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.add-btn {
  border-radius: 8px;
  font-weight: 600;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.card-body {
  padding: 28px;
}

/* 搜索和筛选 */
.search-filter {
  display: flex;
  gap: 20px;
  margin-bottom: 28px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.search-input {
  width: 350px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.filter-select {
  width: 220px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.filter-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-btn {
  border-radius: 8px;
  font-weight: 600;
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 表格样式 */
.table-container {
  margin-bottom: 28px;
  overflow-x: auto;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.diet-table {
  border-radius: 12px;
  overflow: hidden;
}

.diet-table .el-table__header-wrapper th {
  background: #f8f9ff;
  font-weight: 600;
  color: #303133;
  padding: 18px 12px;
  font-size: 15px;
  border-bottom: 2px solid #e4e7ed;
}

.diet-table .el-table__body-wrapper tr {
  transition: all 0.3s ease;
}

.diet-table .el-table__body-wrapper tr:hover {
  background: #fafafa;
  transform: translateX(2px);
}

.diet-table .el-table__body-wrapper td {
  padding: 16px 12px;
  font-size: 14px;
  color: #606266;
  border-bottom: 1px solid #f0f0f0;
}

/* 表格内容样式 */
.resident-name {
  font-weight: 600;
  color: #303133;
}

.diet-type-tag {
  font-weight: 600;
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 12px;
}

.description-text {
  line-height: 1.5;
  color: #606266;
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.edit-btn {
  border-radius: 6px;
  font-weight: 600;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.delete-btn {
  border-radius: 6px;
  font-weight: 600;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(255, 77, 79, 0.3);
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.pagination-component {
  --el-pagination-item-bg-color: white;
  --el-pagination-item-active-bg-color: #667eea;
  --el-pagination-item-active-border-color: #667eea;
  --el-pagination-item-size: 36px;
}

/* 对话框样式 */
.diet-dialog {
  padding: 0;
}

.diet-dialog .el-dialog__header {
  padding: 24px 24px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.diet-dialog .el-dialog__title {
  color: white;
  font-size: 22px;
  font-weight: 600;
}

.diet-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.diet-dialog .el-dialog__body {
  padding: 28px 24px;
  background: #fafafa;
}

.diet-dialog .el-dialog__footer {
  padding: 16px 24px 24px;
  background: white;
  border-radius: 0 0 12px 12px;
  border-top: 1px solid #f0f0f0;
}

/* 表单样式 */
.diet-form {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.form-item {
  margin-bottom: 24px;
  padding: 10px 0;
}

.form-input, .form-select {
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
  width: 100%;
}

.form-input:focus, .form-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-item-textarea {
  margin-bottom: 16px;
}

.form-textarea {
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
  resize: vertical;
  width: 100%;
}

.form-textarea:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 0;
  margin: 0;
}

.footer-btn {
  padding: 12px 32px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  min-width: 120px;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.primary-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 行样式 */
.even-row {
  background-color: #f8f9ff;
}

.odd-row {
  background-color: white;
}

/* 膳食需求卡片样式 */
.diet-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
  padding: 20px;
}

/* 膳食卡片 */
.diet-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  min-height: 280px;
}

.diet-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

/* 卡片头部 */
.diet-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.diet-id-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.diet-id {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.diet-type-tag {
  font-weight: 600;
  padding: 4px 16px;
}

.diet-dates {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.date-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.date-item .el-icon {
  font-size: 14px;
}

/* 卡片内容 */
.diet-card-content {
  flex: 1;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 住户信息 */
.resident-section {
  text-align: center;
}

.resident-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.room-bed-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.room-bed-info .el-icon {
  color: #667eea;
}

/* 描述部分 */
.description-section {
  background: #f8f9ff;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.description-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 10px;
}

.description-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  max-height: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* 卡片底部 */
.diet-card-footer {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .diet-management-container {
    padding: 16px;
  }
  
  .search-input {
    width: 280px;
  }
  
  .filter-select {
    width: 180px;
  }
  
  .diet-cards-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .card-body {
    padding: 16px;
  }
  
  .search-filter {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    padding: 16px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .diet-cards-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 12px;
  }
  
  .diet-card {
    padding: 16px;
  }
}
</style>