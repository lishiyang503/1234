<template>
  <div class="nursing-records-container">
    <div class="page-header">
      <h2 class="page-title">护理记录管理</h2>
      <p class="page-subtitle">记录护理执行情况及效果评估</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">护理记录列表</h3>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加护理记录
          </el-button>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.residentName"
              placeholder="搜索老人姓名"
              prefix-icon="Search"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <el-select
              v-model="searchParams.completed"
              placeholder="筛选完成状态"
              clearable
              class="filter-select"
            >
              <el-option label="全部" value="" />
              <el-option label="已完成" value="1" />
              <el-option label="未完成" value="0" />
            </el-select>
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><RefreshRight /></el-icon>
              重置
            </el-button>
          </div>
          
          <!-- 护理记录列表 -->
          <div class="table-container">
            <el-table
              :data="nursingRecordsList"
              border
              stripe
              style="width: 100%"
              v-loading="loading"
              element-loading-text="加载中..."
              element-loading-background="rgba(255, 255, 255, 0.8)"
              empty-text="暂无护理记录数据"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="residentName" label="老人姓名" width="150" sortable />
              <el-table-column prop="nursingContent" label="护理内容" width="200" />
              <el-table-column prop="executor" label="执行人" width="120" align="center" />
              <el-table-column prop="executeTime" label="执行时间" width="180" align="center" sortable>
                <template #default="scope">
                  {{ formatDate(scope.row.executeTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="completed" label="是否完成" width="120" align="center">
                <template #default="scope">
                  <el-switch
                    v-model="scope.row.completed"
                    active-value="1"
                    inactive-value="0"
                    @change="handleStatusChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="完成状态" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.completed === '1' ? 'success' : 'warning'">
                    {{ scope.row.completed === '1' ? '已完成' : '未完成' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="evaluation" label="效果评估" width="120" align="center">
                <template #default="scope">
                  <el-rate
                    v-model="scope.row.evaluation"
                    :texts="['差', '中', '好']"
                    :max="3"
                    show-score
                    @change="handleEvaluationChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="200" />
              <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable>
                <template #default="scope">
                  {{ formatDate(scope.row.createTime) }}
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
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="recordFormRef"
        :model="recordForm"
        :rules="recordRules"
        label-width="120px"
      >
        <el-form-item label="老人姓名" prop="residentName">
          <el-input v-model="recordForm.residentName" placeholder="请输入老人姓名" />
        </el-form-item>
        
        <el-form-item label="护理内容" prop="nursingContent">
          <el-input v-model="recordForm.nursingContent" placeholder="请输入护理内容" />
        </el-form-item>
        
        <el-form-item label="执行人" prop="executor">
          <el-input v-model="recordForm.executor" placeholder="请输入执行人姓名" />
        </el-form-item>
        
        <el-form-item label="是否完成" prop="completed">
          <el-switch
            v-model="recordForm.completed"
            active-value="1"
            inactive-value="0"
            @change="handleDialogStatusChange"
          />
        </el-form-item>
        
        <el-form-item label="效果评估" prop="evaluation">
          <el-rate
            v-model="recordForm.evaluation"
            :texts="['差', '中', '好']"
            :max="3"
            show-score
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="recordForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
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
import { getNursingRecords, addNursingRecord, updateNursingRecord, deleteNursingRecord } from '@/api/nursing'

// 搜索参数
const searchParams = reactive({
  residentName: '',
  completed: ''
})

// 表格数据
const nursingRecordsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加护理记录')

// 表单引用
const recordFormRef = ref()

// 表单数据
const recordForm = reactive({
  id: null,
  residentName: '',
  nursingContent: '',
  executeTime: '',
  executor: '',
  completed: '0',
  evaluation: 2, // 默认中
  remark: ''
})

// 表单验证规则
const recordRules = {
  residentName: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  nursingContent: [{ required: true, message: '请输入护理内容', trigger: 'blur' }],
  executor: [{ required: true, message: '请输入执行人姓名', trigger: 'blur' }],
  evaluation: [{ required: true, message: '请选择效果评估', trigger: 'change' }]
}

// 生命周期
onMounted(() => {
  fetchNursingRecords()
})

// 获取护理记录列表
const fetchNursingRecords = async () => {
  loading.value = true
  try {
    // 处理日期范围
    const params = {
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 转换日期范围格式
    if (searchParams.dateRange && searchParams.dateRange.length === 2) {
      params.startDate = searchParams.dateRange[0]
      params.endDate = searchParams.dateRange[1]
    }
    
    const response = await getNursingRecords(params)
    
    console.log('护理记录列表响应:', response)
    
    // 增强数据格式兼容性
    if (response && response.data) {
      const data = response.data
      if (data.success) {
        // 假设后端返回的数据在 data.data.list 或 data.data.records 中
        const list = data.data.list || data.data.records || []
        
        nursingRecordsList.value = list.map(record => ({
          ...record,
          // 【关键修复】强制转为字符串，确保与 el-switch 的 active-value="1" 匹配
          // 这样切换页面或刷新后，状态依然是"已完成"
          completed: String(record.completed !== null ? record.completed : '0'),
          
          // 确保 evaluation 是数字 (防止评分组件报错)
          evaluation: Number(record.evaluation || 0)
        }))
        
        total.value = data.data.total || 0
      } else {
        ElMessage.error(data.message || '获取护理记录列表失败')
      }
    } else if (Array.isArray(response.data)) {
      nursingRecordsList.value = response.data.map(record => ({
        ...record,
        // 【关键修复】强制转为字符串，确保与 el-switch 的 active-value="1" 匹配
        completed: String(record.completed !== null ? record.completed : '0'),
        
        // 确保 evaluation 是数字 (防止评分组件报错)
        evaluation: Number(record.evaluation || 0)
      }))
      total.value = response.data.length
    } else {
      ElMessage.error('获取护理记录列表失败: 数据格式不正确')
    }
  } catch (error) {
    console.error('获取护理记录列表失败:', error)
    ElMessage.error('获取护理记录列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchNursingRecords()
}

// 重置搜索
const resetSearch = () => {
  searchParams.residentName = ''
  searchParams.dateRange = null
  searchParams.completed = ''
  currentPage.value = 1
  fetchNursingRecords()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchNursingRecords()
}

// 重置表单
const resetForm = () => {
  if (recordFormRef.value) {
    recordFormRef.value.resetFields()
  }
  Object.assign(recordForm, {
    id: null,
    residentName: '',
    nursingContent: '',
    executor: '',
    completed: '0',
    evaluation: 2,
    remark: ''
  })
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加护理记录'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  dialogTitle.value = '编辑护理记录'
  Object.assign(recordForm, row)
  // 确保completed为字符串格式，evaluation为数字格式
  recordForm.completed = String(row.completed || '0')
  recordForm.evaluation = Number(row.evaluation || 2)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await recordFormRef.value.validate()
    
    // 准备提交数据
    const submitData = {
      ...recordForm,
      // 1. 【关键】确保 completed 转为数字类型 (数据库通常存 0/1，前端 switch 可能是 "0"/"1")
      completed: parseInt(recordForm.completed),
      // 2. 确保 evaluation 是数字
      evaluation: Number(recordForm.evaluation),
      // 3. 如果是添加操作，且状态为已完成，自动设置执行时间为当前时间
      // 如果是编辑操作，保留原执行时间
      executeTime: recordForm.executeTime || (recordForm.completed === '1' ? getNowFormatDate() : null)
    }
    
    // 移除可能导致后端报错的多余字段 (如果后端实体类很严格)
    // 通常 createTime/updateTime 不应该由前端更新，建议删除
    delete submitData.createTime
    delete submitData.updateTime
    
    console.log('提交的数据:', submitData)

    let response
    let successMessage
    let errorMessage
    
    if (recordForm.id) {
      // 更新护理记录
      response = await updateNursingRecord(submitData)
      successMessage = '护理记录更新成功'
      errorMessage = '护理记录更新失败'
    } else {
      // 添加护理记录
      response = await addNursingRecord(submitData)
      successMessage = '护理记录添加成功'
      errorMessage = '护理记录添加失败'
    }
    
    if (response.data.success) {
      ElMessage.success(successMessage)
      dialogVisible.value = false
      fetchNursingRecords()
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

// 删除护理记录
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该护理记录吗？', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteNursingRecord(id)
      if (response.data.success) {
        ElMessage.success('护理记录删除成功')
        fetchNursingRecords()
      } else {
        ElMessage.error(response.data.message || '护理记录删除失败')
      }
    } catch (error) {
      console.error('删除护理记录失败:', error)
      ElMessage.error('护理记录删除失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 处理状态变更
const handleStatusChange = async (row) => {
    // 1. 获取变更后的新状态
    const newStatus = row.completed
    
    // 备份旧状态和旧时间（用于请求失败时回滚）
    const oldStatus = newStatus === '1' ? '0' : '1'
    const oldExecuteTime = row.executeTime

    try {
        // 2. 准备提交给后端的参数
        const updateData = {
            id: row.id,
            // 确保转换为数字类型 (根据数据库 tinyint)
            completed: parseInt(newStatus)
        }

        // 3. 【核心逻辑】如果点击了"已完成" (值为'1')
        let newTimeStr = null
        if (newStatus === '1') {
            newTimeStr = getNowFormatDate() // 生成当前时间
            updateData.executeTime = newTimeStr // 放入参数中发送给后端
        }

        console.log('正在更新:', updateData)

        // 4. 调用后端 API
        const res = await updateNursingRecord(updateData)

        // 5. 检查响应，适配后端返回格式
        if (res && (res.code === 200 || res.data?.success)) {
            ElMessage.success('操作成功')
            
            // 6. 【前端实时更新】界面上立即显示新时间，无需刷新
            if (newTimeStr) {
                row.executeTime = newTimeStr
            }
        } else {
            throw new Error(res.msg || res.data?.message || '更新失败')
        }
    } catch (error) {
        console.error('更新失败:', error)
        // 7. 失败回滚：恢复到之前的状态和时间
        row.completed = oldStatus
        row.executeTime = oldExecuteTime
        ElMessage.error('操作失败，已恢复')
    }
}

// 【新增】处理对话框内的状态变更
const handleDialogStatusChange = (val) => {
  // 如果切换到了"已完成" (active-value="1")
  if (val === '1') {
    ElMessage.success('状态已置为完成')
  }
}

// 处理评估变更
const handleEvaluationChange = async (row) => {
  try {
    // 保存当前评估值，用于恢复
    const originalEvaluation = row.evaluation
    
    // 2. 准备提交参数，只传递需要更新的字段
    const updateData = {
      id: row.id,
      evaluation: Number(row.evaluation)
    }
    
    console.log('更新评估数据:', updateData)
    const response = await updateNursingRecord(updateData)
    
    console.log('评估更新响应:', response)
    
    // 简化响应处理
    if (response && response.data && response.data.success) {
      // 更新成功，确保本地数据类型正确
      row.evaluation = updateData.evaluation
      ElMessage.success('评估更新成功')
    } else {
      // 更新失败，恢复原评估值
      row.evaluation = originalEvaluation
      ElMessage.error(response.data?.message || '评估更新失败')
    }
  } catch (error) {
    console.error('评估更新失败:', error)
    // 恢复原评估值
    row.evaluation = originalEvaluation
    ElMessage.error('评估更新失败，已恢复原评估')
  }
}

// 获取当前格式化时间字符串 (YYYY-MM-DD HH:mm:ss)
const getNowFormatDate = () => {
  const date = new Date()
  const pad = (n) => n.toString().padStart(2, '0')
  const year = date.getFullYear()
  const month = pad(date.getMonth() + 1)
  const day = pad(date.getDate())
  const hour = pad(date.getHours())
  const minute = pad(date.getMinutes())
  const second = pad(date.getSeconds())
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
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
.nursing-records-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: var(--bg-primary);
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
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
  background: white;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.card:hover {
  box-shadow: none;
  transform: none;
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
  font-size: 16px;
  font-weight: 500;
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

.search-input,
.filter-select {
  width: 200px;
  transition: all 0.3s ease;
}

.date-filter {
  width: 300px;
  transition: all 0.3s ease;
}

.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
  background: white;
  border-radius: var(--border-radius);
}

/* 表格样式优化 */
:deep(.el-table th) {
  font-size: 16px !important;
  font-weight: 500 !important;
  padding: 12px 0 !important;
  background-color: var(--bg-primary) !important;
}

:deep(.el-table td) {
  font-size: 16px !important;
  padding: 12px 0 !important;
}

:deep(.el-table tr:hover > td) {
  background-color: var(--bg-hover) !important;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nursing-records-container {
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
  .filter-select,
  .date-filter {
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