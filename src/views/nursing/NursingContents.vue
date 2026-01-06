<template>
  <div class="nursing-contents-container">
    <div class="page-header">
      <h2 class="page-title">护理内容管理</h2>
      <p class="page-subtitle">定义不同类型的护理项目和服务内容</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">护理内容列表</h3>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加护理内容
          </el-button>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.name"
              placeholder="搜索护理内容名称"
              prefix-icon="Search"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <el-select
              v-model="searchParams.category"
              placeholder="选择护理类型"
              clearable
              class="filter-select"
              @change="handleSearch"
            >
              <el-option label="日常护理" value="1" />
              <el-option label="医疗护理" value="2" />
              <el-option label="心理护理" value="3" />
            </el-select>
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 护理内容列表 -->
          <div class="table-container">
            <el-table
              :data="nursingContentsList"
              border
              stripe
              style="width: 100%"
              v-loading="loading"
              element-loading-text="加载中..."
              element-loading-background="rgba(255, 255, 255, 0.8)"
              empty-text="暂无护理内容数据"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="name" label="护理内容名称" min-width="180" sortable />
              <el-table-column label="护理类型" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="getCategoryTagType(scope.row.category)">
                    {{ getCategoryText(scope.row.category) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="护理内容描述" min-width="250" />
              <el-table-column label="适用护理级别" min-width="180" align="center">
                <template #default="scope">
                  <div class="level-tags">
                    <el-tag 
                      v-for="level in scope.row.applicableLevels" 
                      :key="level"
                      size="small"
                      :type="getLevelTagType(level)"
                    >
                      {{ getLevelText(level) }}
                    </el-tag>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="预计时长(分钟)" width="150" align="center" sortable />
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
                        <el-dropdown-item @click="handleDelete(scope.row.id)" type="danger">
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
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="contentFormRef"
        :model="contentForm"
        :rules="contentRules"
        label-width="120px"
      >
        <el-form-item label="护理内容名称" prop="name">
          <el-input v-model="contentForm.name" placeholder="请输入护理内容名称" />
        </el-form-item>
        
        <el-form-item label="护理类型" prop="category">
          <el-select v-model="contentForm.category" placeholder="请选择护理类型" clearable>
            <el-option label="日常护理" value="1" />
            <el-option label="医疗护理" value="2" />
            <el-option label="心理护理" value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="护理内容描述" prop="description">
          <el-input
            v-model="contentForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入护理内容描述"
          />
        </el-form-item>
        
        <el-form-item label="适用护理级别" prop="applicableLevels">
          <el-select 
            v-model="contentForm.applicableLevels" 
            placeholder="请选择适用护理级别"
            multiple
            style="width: 100%"
          >
            <el-option label="基础护理" value="1" />
            <el-option label="中级护理" value="2" />
            <el-option label="高级护理" value="3" />
            <el-option label="VIP" value="4" />
            <el-option label="SVIP" value="5" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="预计时长(分钟)" prop="duration">
          <el-input-number
            v-model="contentForm.duration"
            :min="1"
            :max="360"
            :step="5"
            placeholder="请输入预计时长"
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
import { getNursingContents, addNursingContent, updateNursingContent, deleteNursingContent } from '@/api/nursing'

// 搜索和筛选参数
const searchParams = reactive({
  name: '',
  category: ''
})

// 表格数据
const nursingContentsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加护理内容')

// 表单引用
const contentFormRef = ref()

// 表单数据
const contentForm = reactive({
  id: null,
  name: '',
  category: '',
  description: '',
  applicableLevels: [],
  duration: 30
})

// 表单验证规则
const contentRules = {
  name: [{ required: true, message: '请输入护理内容名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择护理类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入护理内容描述', trigger: 'blur' }],
  applicableLevels: [{ required: true, message: '请选择适用护理级别', trigger: 'change' }],
  duration: [
    { required: true, message: '请输入预计时长', trigger: 'blur' },
    { type: 'number', min: 1, message: '预计时长不能少于1分钟', trigger: 'blur,change' }
  ]
}

// 生命周期
onMounted(() => {
  fetchNursingContents()
})

// 获取护理内容列表
const fetchNursingContents = async () => {
  loading.value = true
  try {
    const response = await getNursingContents({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    console.log('护理内容列表响应:', response)
    
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
            ElMessage.error(data.message || '获取护理内容列表失败')
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
          ElMessage.error('获取护理内容列表失败: 数据格式不正确')
        }
      } else {
        ElMessage.error('获取护理内容列表失败: 数据格式不正确')
      }
    } else if (Array.isArray(response)) {
      // 直接返回数组的情况
      processedData.list = response
      processedData.total = response.length
    } else {
      ElMessage.error('获取护理内容列表失败: 数据格式不正确')
    }
    
    // 确保数据类型正确
    nursingContentsList.value = Array.isArray(processedData.list) ? processedData.list : []
    total.value = typeof processedData.total === 'number' ? processedData.total : nursingContentsList.value.length
    
    // 为每条记录添加默认值，防止表格显示异常
    nursingContentsList.value = nursingContentsList.value.map(record => ({
      ...record,
      id: record.id || '',
      name: record.name || '',
      category: record.category || '',
      description: record.description || '',
      applicableLevels: record.applicableLevels || [],
      duration: record.duration || 0
    }))
    
  } catch (error) {
    console.error('获取护理内容列表失败:', error)
    // 区分不同类型的错误
    if (error.response) {
      // 服务器返回了错误响应
      const errorMsg = error.response.data?.message || error.response.statusText || '获取护理内容列表失败'
      ElMessage.error(`获取护理内容列表失败: ${errorMsg}`)
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error('获取护理内容列表失败: 服务器无响应，请检查网络连接或服务器状态')
    } else if (error.message) {
      // 请求配置错误
      ElMessage.error(`获取护理内容列表失败: ${error.message}`)
    } else {
      // 其他错误
      ElMessage.error('获取护理内容列表失败: 发生未知错误')
    }
    // 确保列表为空，避免表格显示异常
    nursingContentsList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchNursingContents()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchNursingContents()
}

// 获取护理类型标签类型
const getCategoryTagType = (category) => {
  const map = {
    1: 'success', // 日常护理
    2: 'warning', // 医疗护理
    3: 'primary'  // 心理护理
  }
  return map[category] || 'info'
}

// 获取护理类型文本
const getCategoryText = (category) => {
  const map = {
    1: '日常护理',
    2: '医疗护理',
    3: '心理护理'
  }
  return map[category] || category
}

// 获取护理级别标签类型
const getLevelTagType = (level) => {
  const map = {
    1: 'success', // 基础护理
    2: 'warning', // 中级护理
    3: 'danger',  // 高级护理
    4: 'primary', // VIP
    5: 'info'     // SVIP
  }
  return map[level] || 'info'
}

// 获取护理级别文本
const getLevelText = (level) => {
  const map = {
    1: '基础护理',
    2: '中级护理',
    3: '高级护理',
    4: 'VIP',
    5: 'SVIP'
  }
  return map[level] || level
}



// 重置表单
const resetForm = () => {
  if (contentFormRef.value) {
    contentFormRef.value.resetFields()
  }
  Object.assign(contentForm, {
    id: null,
    name: '',
    category: '',
    description: '',
    applicableLevels: [],
    duration: 30
  })
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加护理内容'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑护理内容'
  Object.assign(contentForm, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await contentFormRef.value.validate()
    
    let response
    let successMessage
    let errorMessage
    
    if (contentForm.id) {
      // 更新护理内容
      response = await updateNursingContent(contentForm)
      successMessage = '护理内容更新成功'
      errorMessage = '护理内容更新失败'
    } else {
      // 添加护理内容
      response = await addNursingContent(contentForm)
      successMessage = '护理内容添加成功'
      errorMessage = '护理内容添加失败'
    }
    
    console.log('提交响应:', response)
    
    // 增强响应处理兼容性
    if (response && response.data) {
      const data = response.data
      if (data.success !== undefined) {
        if (data.success) {
          ElMessage.success(successMessage)
          dialogVisible.value = false
          fetchNursingContents()
        } else {
          ElMessage.error(data.message || errorMessage)
        }
      } else {
        // 兼容不同响应格式
        ElMessage.success(successMessage)
        dialogVisible.value = false
        fetchNursingContents()
      }
    } else {
      // 兼容直接返回成功的情况
      ElMessage.success(successMessage)
      dialogVisible.value = false
      fetchNursingContents()
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
      ElMessage.error(`${contentForm.id ? '更新' : '添加'}护理内容失败: ${errorMsg}`)
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error(`${contentForm.id ? '更新' : '添加'}护理内容失败: 服务器无响应`)
    } else {
      // 请求配置错误
      ElMessage.error(`${contentForm.id ? '更新' : '添加'}护理内容失败: ${error.message}`)
    }
  }
}

// 删除护理内容
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该护理内容吗？', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteNursingContent(id)
      if (response.data.success) {
        ElMessage.success('护理内容删除成功')
        fetchNursingContents()
      } else {
        ElMessage.error(response.data.message || '护理内容删除失败')
      }
    } catch (error) {
      console.error('删除护理内容失败:', error)
      ElMessage.error('护理内容删除失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}
</script>

<style scoped>
.nursing-contents-container {
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

.filter-select {
  width: 200px;
  transition: all 0.3s ease;
}

.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  background: white;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.level-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nursing-contents-container {
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
  
  .search-input,
  .filter-select {
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