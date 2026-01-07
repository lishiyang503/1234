<template>
  <div class="service-packages-container">
    <div class="page-header">
      <h2 class="page-title">护理服务管理</h2>
      <p class="page-subtitle">管理护理级别和服务内容</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">护理级别管理</h3>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加护理级别
          </el-button>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.name"
              placeholder="搜索套餐名称"
              prefix-icon="Search"
              clearable
              class="search-input"
            />
            <el-select
              v-model="searchParams.level"
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
          
          <!-- 服务套餐卡片网格 -->
          <div class="table-container">
            <div class="packages-grid">
              <div 
                v-for="pkg in servicePackagesList" 
                :key="pkg.id"
                class="package-card"
              >
                <!-- 卡片头部 -->
                <div class="package-card-header">
                  <div class="package-id-section">
                    <span class="package-id">ID: {{ pkg.id }}</span>
                    <el-tag 
                      :type="getLevelType(pkg.level)" 
                      class="level-tag"
                    >
                      {{ pkg.level }}
                    </el-tag>
                  </div>
                  <div class="package-status">
                    <el-switch
                      v-model="pkg.status"
                      active-value="启用"
                      inactive-value="禁用"
                      @change="handleStatusChange(pkg)"
                    />
                  </div>
                </div>
                
                <!-- 卡片内容 -->
                <div class="package-card-content">
                  <div class="package-name-section">
                    <h3 class="package-name">{{ pkg.name }}</h3>
                    <div class="package-price">
                      <el-icon><Money /></el-icon>
                      <span class="price">¥{{ pkg.price }}/月</span>
                    </div>
                  </div>
                  
                  <div class="description-section">
                    <div class="description-title">
                      <el-icon><Document /></el-icon>
                      <span>套餐描述</span>
                    </div>
                    <div class="description-text">{{ pkg.description }}</div>
                  </div>
                  
                  <div class="features-section">
                    <div class="features-title">
                      <el-icon><List /></el-icon>
                      <span>服务内容</span>
                    </div>
                    <div class="features-content">
                      {{ pkg.features }}
                    </div>
                  </div>
                </div>
                
                <!-- 卡片底部 -->
                <div class="package-card-footer">
                  <div class="action-buttons">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="showEditDialog(pkg)"
                      class="edit-btn"
                    >
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="handleDelete(pkg.id)"
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
            <div v-if="!servicePackagesList.length" class="empty-state">
              <el-empty 
                description="暂无服务套餐"
                :image-size="100"
              >
                <el-button type="primary" @click="showAddDialog">
                  <el-icon><Plus /></el-icon>
                  添加套餐
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
            />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
    >
      <el-form
        ref="serviceFormRef"
        :model="serviceForm"
        :rules="serviceRules"
        label-width="120px"
      >
        <div class="form-row">
          <el-form-item label="套餐名称" prop="name" class="form-item">
            <el-input v-model="serviceForm.name" placeholder="请输入套餐名称" />
          </el-form-item>
          <el-form-item label="套餐级别" prop="level" class="form-item">
            <el-select v-model="serviceForm.level" placeholder="请选择套餐级别">
              <el-option label="基础套餐" value="基础" />
              <el-option label="中级套餐" value="中级" />
              <el-option label="高级套餐" value="高级" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="价格(元/月)" prop="price" class="form-item">
            <el-input v-model="serviceForm.price" type="number" placeholder="请输入价格" />
          </el-form-item>
          <el-form-item label="状态" prop="status" class="form-item">
            <el-select v-model="serviceForm.status" placeholder="请选择状态">
              <el-option label="启用" value="启用" />
              <el-option label="禁用" value="禁用" />
            </el-select>
          </el-form-item>
        </div>
        
        <el-form-item label="套餐描述" prop="description">
          <el-input
            v-model="serviceForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入套餐描述"
          />
        </el-form-item>
        
        <el-form-item label="服务内容" prop="features">
          <el-select
            v-model="serviceForm.features"
            multiple
            placeholder="请选择服务内容"
            collapse-tags
          >
            <el-option label="日常护理" value="日常护理" />
            <el-option label="医疗护理" value="医疗护理" />
            <el-option label="康复训练" value="康复训练" />
            <el-option label="心理咨询" value="心理咨询" />
            <el-option label="文化娱乐" value="文化娱乐" />
            <el-option label="餐饮服务" value="餐饮服务" />
            <el-option label="清洁服务" value="清洁服务" />
            <el-option label="其他服务" value="其他服务" />
          </el-select>
          <div class="form-help">可多选，按住Ctrl键可选择多项</div>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Edit, 
  Delete, 
  Search, 
  Money, 
  Document, 
  List,
  Calendar 
} from '@element-plus/icons-vue'
import { getNursingLevels, addNursingLevel, updateNursingLevel, deleteNursingLevel } from '@/api/nursing'

// 搜索和筛选参数
const searchParams = reactive({
  name: '',
  level: ''
})

// 表格数据
const servicePackagesList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加服务套餐')
const isEditMode = ref(false)

// 表单引用
const serviceFormRef = ref()

// 表单数据
const serviceForm = reactive({
  id: null,
  name: '',
  level: '',
  price: '',
  description: '',
  features: [],
  status: '启用'
})

// 表单验证规则
const serviceRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择套餐级别', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  description: [{ required: true, message: '请输入套餐描述', trigger: 'blur' }],
  features: [{ required: true, message: '请选择服务内容', trigger: 'change' }]
}

// 生命周期
onMounted(() => {
  fetchServicePackages()
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

// 获取护理级别列表
const fetchServicePackages = async () => {
  loading.value = true
  try {
    const response = await getNursingLevels({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.data.success) {
      // 转换护理级别数据为套餐格式
      servicePackagesList.value = (response.data.data.list || []).map(level => ({
        id: level.id,
        name: level.name,
        level: level.level === 1 ? '基础' : level.level === 2 ? '中级' : '高级',
        price: level.baseCost,
        description: level.description || '无描述',
        features: [level.name + '护理服务'],
        status: level.isActive ? '启用' : '禁用'
      }))
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取护理级别列表失败')
    }
  } catch (error) {
    console.error('获取护理级别列表失败:', error)
    ElMessage.error('获取护理级别列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchServicePackages()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchServicePackages()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    const response = await updateNursingLevel({
      id: row.id,
      isActive: row.status === '启用'
    })
    if (response.data.success) {
      ElMessage.success(`护理级别 ${row.name} 状态已更新为 ${row.status}`)
    } else {
      ElMessage.error(response.data.message || '状态更新失败')
      // 恢复原状态
      row.status = row.status === '启用' ? '禁用' : '启用'
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败，请重试')
    // 恢复原状态
    row.status = row.status === '启用' ? '禁用' : '启用'
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEditMode.value = false
  dialogTitle.value = '添加护理级别'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEditMode.value = true
  dialogTitle.value = '编辑护理级别'
  Object.assign(serviceForm, row)
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(serviceForm, {
    id: null,
    name: '',
    level: '',
    price: '',
    description: '',
    features: [],
    status: '启用'
  })
  if (serviceFormRef.value) {
    serviceFormRef.value.resetFields()
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await serviceFormRef.value.validate()
    
    // 转换表单数据为护理级别格式
    const nursingLevelData = {
      id: serviceForm.id,
      name: serviceForm.name,
      level: serviceForm.level === '基础' ? 1 : serviceForm.level === '中级' ? 2 : 3,
      baseCost: serviceForm.price,
      description: serviceForm.description,
      isActive: serviceForm.status === '启用'
    }
    
    if (isEditMode.value) {
      // 编辑护理级别
      const response = await updateNursingLevel(nursingLevelData)
      if (response.data.success) {
        ElMessage.success('编辑成功')
        dialogVisible.value = false
        fetchServicePackages()
      } else {
        ElMessage.error(response.data.message || '编辑失败')
      }
    } else {
      // 添加护理级别
      const response = await addNursingLevel(nursingLevelData)
      if (response.data.success) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchServicePackages()
      } else {
        ElMessage.error(response.data.message || '添加失败')
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name !== 'Error' || error.message !== '表单验证失败') {
      ElMessage.error('操作失败，请重试')
    }
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
      const response = await deleteNursingLevel(id)
      if (response.data.success) {
        ElMessage.success('删除成功')
        fetchServicePackages()
      } else {
        ElMessage.error(response.data.message || '删除失败')
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
.service-packages-container {
  width: 100%;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

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

.content-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

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

.card-body {
  padding: 24px;
}

.search-filter {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.search-input {
  width: 300px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.filter-select {
  width: 200px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.filter-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.table-container {
  margin-bottom: 24px;
}

/* 套餐卡片样式 */
.packages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  padding: 20px;
}

/* 套餐卡片 */
.package-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  min-height: 320px;
}

.package-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

/* 卡片头部 */
.package-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.package-id-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.package-id {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.level-tag {
  font-weight: 600;
  padding: 4px 16px;
}

.package-status {
  display: flex;
  align-items: center;
}

/* 卡片内容 */
.package-card-content {
  flex: 1;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 套餐名称和价格 */
.package-name-section {
  text-align: center;
}

.package-name {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
}

.package-price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #f56c6c;
  font-weight: 600;
  font-size: 24px;
}

.package-price .el-icon {
  font-size: 20px;
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

/* 服务内容部分 */
.features-section {
  background: #f0f9eb;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #67c23a;
}

.features-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #67c23a;
  margin-bottom: 10px;
}

.features-content {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
  font-weight: 500;
  background: white;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  min-height: 120px;
  display: flex;
  align-items: center;
}

/* 卡片底部 */
.package-card-footer {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons {
  display: flex;
  gap: 12px;
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.form-item {
  flex: 1;
  min-width: 300px;
}

.form-help {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.price {
  font-weight: 600;
  color: #f56c6c;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .service-packages-container {
    padding: 16px;
  }
  
  .packages-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;
    padding: 16px;
  }
  
  .search-input {
    width: 280px;
  }
  
  .filter-select {
    width: 180px;
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
  
  .packages-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 12px;
  }
  
  .package-card {
    padding: 16px;
  }
}
</style>