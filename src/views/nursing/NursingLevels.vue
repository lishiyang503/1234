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
          
          <!-- 护理级别卡片列表 -->
          <div class="cards-container" v-loading="loading" element-loading-text="加载中..." element-loading-background="rgba(255, 255, 255, 0.8)">
            <div v-if="nursingLevelsList.length === 0" class="empty-state">
              <el-empty description="暂无护理级别数据" />
            </div>
            <el-card
              v-for="item in nursingLevelsList"
              :key="item.id"
              class="level-card"
              shadow="hover"
              :body-style="{ padding: '0' }"
            >
              <!-- 卡片头部 - 带渐变背景 -->
              <div class="card-header-gradient" :class="`level-bg-${item.level}`">
                <div class="card-header-content">
                  <!-- 护士头像 -->
                  <div class="nurse-avatar-wrapper">
                    <div class="nurse-avatar">
                      <el-icon class="nurse-icon"><UserFilled /></el-icon>
                    </div>
                  </div>
                  
                  <div class="level-info">
                    <h4 class="level-name">{{ item.name }}</h4>
                    <el-tag :type="getLevelTagType(item.level)" class="level-tag">
                      {{ getLevelText(item.level) }}
                    </el-tag>
                  </div>
                  
                  <el-dropdown>
                    <el-button type="primary" size="small" circle class="action-btn">
                      <el-icon><Setting /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="showEditDialog(item)" class="dropdown-item">
                          <el-icon class="dropdown-icon"><Edit /></el-icon>
                          编辑
                        </el-dropdown-item>
                        <el-dropdown-item 
                          @click="handleDelete(item.id)" 
                          class="dropdown-item dropdown-item-danger"
                          :disabled="item.id <= 3"
                        >
                          <el-icon class="dropdown-icon"><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
              
              <!-- 卡片主体内容 -->
              <div class="card-body-content">
                <!-- 描述部分 -->
                <div class="description-section">
                  <div class="section-header">
                    <el-icon class="section-icon"><Document /></el-icon>
                    <h5 class="section-title">级别描述</h5>
                  </div>
                  <p class="description-text">{{ item.description }}</p>
                </div>
                
                <!-- 费用卡片 -->
                <div class="cost-card">
                  <div class="cost-icon-wrapper">
                    <el-icon class="cost-icon"><Money /></el-icon>
                  </div>
                  <div class="cost-info">
                    <span class="cost-label">基础费用</span>
                    <span class="cost-value">¥{{ item.baseCost }}<span class="cost-unit">/月</span></span>
                  </div>
                </div>
                
                <!-- 时间信息 -->
                <div class="time-section">
                  <div class="time-item">
                    <div class="time-icon-wrapper">
                      <el-icon class="time-icon"><Calendar /></el-icon>
                    </div>
                    <div class="time-info">
                      <span class="time-label">创建时间</span>
                      <span class="time-value">{{ formatDate(item.createTime) }}</span>
                    </div>
                  </div>
                  <div class="time-item">
                    <div class="time-icon-wrapper">
                      <el-icon class="time-icon"><EditPen /></el-icon>
                    </div>
                    <div class="time-info">
                      <span class="time-label">更新时间</span>
                      <span class="time-value">{{ formatDate(item.updateTime) }}</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 卡片底部 -->
              <div class="card-footer">
                <div class="footer-content">
                  <div class="id-info">
                    <span class="id-label">ID:</span>
                    <span class="id-value">{{ item.id }}</span>
                  </div>
                  <el-progress
                    :percentage="getProgressValue(item.level)"
                    :color="getLevelColor(item.level)"
                    :show-text="false"
                    class="level-progress"
                  />
                </div>
              </div>
            </el-card>
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
            <el-option label="VIP" value="4" />
            <el-option label="SVIP" value="5" />
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
import { Setting, Edit, Delete, Calendar, EditPen, Document, Money, UserFilled, Plus } from '@element-plus/icons-vue'

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
    3: 'danger',  // 高级
    4: 'primary', // VIP
    5: 'info'     // SVIP
  }
  return map[level] || 'info'
}

// 获取级别文本
const getLevelText = (level) => {
  const map = {
    1: '基础',
    2: '中级',
    3: '高级',
    4: 'VIP',
    5: 'SVIP'
  }
  return map[level] || level
}

// 获取级别进度值
const getProgressValue = (level) => {
  const map = {
    1: 33,
    2: 66,
    3: 100,
    4: 133,
    5: 166
  }
  return map[level] || 0
}

// 获取级别颜色
const getLevelColor = (level) => {
  const map = {
    1: '#67C23A', // success - 基础
    2: '#E6A23C', // warning - 中级
    3: '#F56C6C', // danger - 高级
    4: '#409EFF', // primary - VIP
    5: '#909399'  // info - SVIP
  }
  return map[level] || '#909399' // info
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

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 28px;
  margin-bottom: 24px;
  transition: all 0.3s ease;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 2px dashed #e0e0e0;
}

/* 卡片基础样式 */
.level-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  background: white;
  border: 1px solid #f0f0f0;
}

.level-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
}

/* 卡片头部渐变背景 */
.card-header-gradient {
  padding: 28px 24px;
  position: relative;
  overflow: hidden;
}

.card-header-gradient::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.1;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><circle cx="50" cy="50" r="2" fill="white" opacity="0.3"/></svg>');
  background-size: 20px 20px;
}

/* 不同级别背景色 */
.level-bg-1 {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.level-bg-2 {
  background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);
}

.level-bg-3 {
  background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);
}

.level-bg-4 {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
}

.level-bg-5 {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
}

/* 卡片头部内容 */
.card-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
  gap: 16px;
}

/* 护士头像 */
.nurse-avatar-wrapper {
  position: relative;
}

.nurse-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.nurse-avatar:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.4);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.nurse-icon {
  font-size: 24px;
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.15));
}

.level-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  flex: 1;
}

.level-name {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
}

.level-tag {
  font-size: 13px;
  padding: 6px 14px;
  border-radius: 20px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 操作按钮 */
.action-btn {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 下拉菜单样式 */
.el-dropdown-menu {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  transition: all 0.2s ease;
  font-size: 14px;
}

.dropdown-item:hover {
  background: #f8f9fa;
  color: #409eff;
}

.dropdown-item-danger {
  color: #f56c6c;
}

.dropdown-item-danger:hover {
  background: #fff1f0;
  color: #f56c6c;
}

.dropdown-icon {
  font-size: 14px;
}

/* 卡片主体内容 */
.card-body-content {
  padding: 24px;
}

/* 描述部分 */
.description-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.section-icon {
  font-size: 16px;
  color: #409eff;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.description-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.7;
  margin: 0;
  word-break: break-word;
  background: #fafafa;
  padding: 16px;
  border-radius: 12px;
  border-left: 4px solid #409eff;
}

/* 费用卡片 */
.cost-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  margin-bottom: 24px;
  transition: all 0.3s ease;
  border: 1px solid #bae6fd;
}

.cost-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.15);
}

.cost-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.cost-icon {
  font-size: 24px;
  color: #3b82f6;
}

.cost-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cost-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.cost-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e40af;
  line-height: 1;
}

.cost-unit {
  font-size: 14px;
  font-weight: normal;
  color: #64748b;
  margin-left: 6px;
}

/* 时间信息 */
.time-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.time-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.time-icon-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.time-icon {
  font-size: 16px;
  color: #64748b;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.time-label {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.time-value {
  color: #334155;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  background: #f8fafc;
  padding: 4px 8px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

/* 卡片底部 */
.card-footer {
  padding: 0 24px 24px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.id-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #94a3b8;
  font-family: 'Courier New', monospace;
}

.id-label {
  font-weight: 600;
}

.id-value {
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 12px;
  color: #64748b;
}

/* 进度条 */
.level-progress {
  height: 8px;
  border-radius: 4px;
  flex: 1;
  background: #f1f5f9;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .cards-container {
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .nursing-levels-container {
    padding: 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .card-body-content {
    padding: 20px;
  }
  
  .cards-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .level-name {
    font-size: 20px;
  }
  
  .cost-value {
    font-size: 24px;
  }
  
  .card-header-gradient {
    padding: 24px 20px;
  }
  
  .footer-content {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .card-body-content {
    padding: 16px;
  }
  
  .level-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .card-header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .cost-card {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .cards-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 全局过渡效果 */
* {
  transition: color 0.3s ease, background-color 0.3s ease, border-color 0.3s ease;
}
</style>