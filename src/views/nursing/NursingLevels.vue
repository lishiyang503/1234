<template>
  <div class="nursing-levels-container">
    <div class="page-header">
      <h2 class="page-title">护理级别管理</h2>
      <p class="page-subtitle">定义护理等级及对应基础费用</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">护理级别列表</h3>
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
              placeholder="搜索护理级别名称"
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
          
          <!-- 护理级别列表 -->
          <div class="table-container">
            <el-table
              :data="nursingLevelsList"
              border
              stripe
              style="width: 100%"
              v-loading="loading"
              element-loading-text="加载中..."
              element-loading-background="rgba(255, 255, 255, 0.8)"
              empty-text="暂无护理级别数据"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="name" label="级别名称" width="150" sortable />
              <el-table-column prop="level" label="级别" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="getLevelTagType(scope.row.level)">
                    {{ getLevelText(scope.row.level) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="级别描述" min-width="200" />
              <el-table-column prop="baseCost" label="基础费用(元/月)" width="150" align="center" sortable />
              <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable>
                <template #default="scope">
                  {{ formatDate(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="updateTime" label="更新时间" width="180" align="center" sortable>
                <template #default="scope">
                  {{ formatDate(scope.row.updateTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-dropdown>
                    <el-button type="primary" size="small">
                      操作
                      <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="showEditDialog(scope.row)">
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-dropdown-item>
                        <el-dropdown-item 
                          @click="handleDelete(scope.row.id)" 
                          type="danger"
                          :disabled="scope.row.id <= 3"
                        >
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
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
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="levelFormRef"
        :model="levelForm"
        :rules="levelRules"
        label-width="120px"
      >
        <el-form-item label="级别名称" prop="name">
          <el-input v-model="levelForm.name" placeholder="请输入级别名称" />
        </el-form-item>
        
        <el-form-item label="级别" prop="level">
          <el-select v-model="levelForm.level" placeholder="请选择级别" clearable>
            <el-option label="基础" value="1" />
            <el-option label="中级" value="2" />
            <el-option label="高级" value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="级别描述" prop="description">
          <el-input
            v-model="levelForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入级别描述"
          />
        </el-form-item>
        
        <el-form-item label="基础费用(元/月)" prop="baseCost">
          <el-input-number
            v-model="levelForm.baseCost"
            :min="0"
            :step="100"
            placeholder="请输入基础费用"
            style="width: 100%"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNursingLevels, addNursingLevel, updateNursingLevel, deleteNursingLevel } from '@/api/nursing'

// 搜索和筛选参数
const searchParams = reactive({
  name: ''
})

// 表格数据
const nursingLevelsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加护理级别')

// 表单引用
const levelFormRef = ref()

// 表单数据
const levelForm = reactive({
  id: null,
  name: '',
  level: '',
  description: '',
  baseCost: 0
})

// 表单验证规则
const levelRules = {
  name: [{ required: true, message: '请输入级别名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }],
  description: [{ required: true, message: '请输入级别描述', trigger: 'blur' }],
  baseCost: [{ required: true, message: '请输入基础费用', trigger: 'blur' }, { type: 'number', min: 0, message: '基础费用不能为负数', trigger: 'blur,change' }]
}

// 生命周期
onMounted(() => {
  fetchNursingLevels()
})

// 获取护理级别列表
const fetchNursingLevels = async () => {
  loading.value = true
  try {
    const response = await getNursingLevels({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    console.log('护理级别列表响应:', response)
    
    // 增强数据格式兼容性
    if (response && response.data) {
      const data = response.data
      if (data.success) {
        nursingLevelsList.value = data.data.list || data.data.records || []
        total.value = data.data.total || data.data.totalCount || 0
      } else {
        ElMessage.error(data.message || '获取护理级别列表失败')
      }
    } else if (Array.isArray(response.data)) {
      nursingLevelsList.value = response.data
      total.value = response.data.length
    } else {
      ElMessage.error('获取护理级别列表失败: 数据格式不正确')
    }
  } catch (error) {
    console.error('获取护理级别列表失败:', error)
    ElMessage.error('获取护理级别列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchNursingLevels()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchNursingLevels()
}

// 重置表单
const resetForm = () => {
  if (levelFormRef.value) {
    levelFormRef.value.resetFields()
  }
  Object.assign(levelForm, {
    id: null,
    name: '',
    level: '',
    description: '',
    baseCost: 0
  })
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加护理级别'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑护理级别'
  Object.assign(levelForm, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await levelFormRef.value.validate()
    
    let response
    let successMessage
    let errorMessage
    
    if (levelForm.id) {
      // 更新护理级别
      response = await updateNursingLevel(levelForm)
      successMessage = '护理级别更新成功'
      errorMessage = '护理级别更新失败'
    } else {
      // 添加护理级别
      response = await addNursingLevel(levelForm)
      successMessage = '护理级别添加成功'
      errorMessage = '护理级别添加失败'
    }
    
    if (response.data.success) {
      ElMessage.success(successMessage)
      dialogVisible.value = false
      fetchNursingLevels()
    } else {
      ElMessage.error(response.data.message || errorMessage)
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name !== 'Error' || !error.message.includes('表单验证')) {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 删除护理级别
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该护理级别吗？', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteNursingLevel(id)
      if (response.data.success) {
        ElMessage.success('护理级别删除成功')
        fetchNursingLevels()
      } else {
        ElMessage.error(response.data.message || '护理级别删除失败')
      }
    } catch (error) {
      console.error('删除护理级别失败:', error)
      ElMessage.error('护理级别删除失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 获取级别标签类型
const getLevelTagType = (level) => {
  const map = {
    1: 'success', // 基础
    2: 'warning', // 中级
    3: 'danger'   // 高级
  }
  return map[level] || 'info'
}

// 获取级别文本
const getLevelText = (level) => {
  const map = {
    1: '基础',
    2: '中级',
    3: '高级'
  }
  return map[level] || level
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
</script>

<style scoped>
.nursing-levels-container {
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
  transition: all 0.3s ease;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
  transition: all 0.3s ease;
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

.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  background: white;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nursing-levels-container {
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
</style>