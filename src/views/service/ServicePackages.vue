<template>
  <div class="service-packages-container">
    <div class="page-header">
      <h2 class="page-title">服务管理</h2>
      <p class="page-subtitle">管理服务套餐信息</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">服务套餐管理</h3>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加套餐
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
          
          <!-- 服务套餐列表 -->
          <div class="table-container">
            <el-table
              :data="servicePackagesList"
              border
              stripe
              style="width: 100%"
              :row-class-name="tableRowClassName"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="name" label="套餐名称" width="180" />
              <el-table-column prop="level" label="套餐级别" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="getLevelType(scope.row.level)">
                    {{ scope.row.level }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="价格(元/月)" width="150" align="center">
                <template #default="scope">
                  <span class="price">{{ scope.row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="套餐描述" min-width="300">
                <template #default="scope">
                  <div class="description">{{ scope.row.description }}</div>
                </template>
              </el-table-column>
              <el-table-column prop="features" label="服务内容" min-width="300">
                <template #default="scope">
                  <div class="features-list">
                    <el-tag
                      v-for="(feature, index) in scope.row.features"
                      :key="index"
                      size="small"
                      effect="light"
                      class="feature-tag"
                    >
                      {{ feature }}
                    </el-tag>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="scope">
                  <el-switch
                    v-model="scope.row.status"
                    active-value="启用"
                    inactive-value="禁用"
                    @change="handleStatusChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" align="center">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="showEditDialog(scope.row)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                    <el-icon><Delete /></el-icon>
                    删除
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
import { getServicePackages, addServicePackage, updateServicePackage, deleteServicePackage, updateServicePackageStatus } from '@/api/service'

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

// 获取服务套餐列表
const fetchServicePackages = async () => {
  loading.value = true
  try {
    const response = await getServicePackages({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.data.success) {
      servicePackagesList.value = response.data.data.list || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取服务套餐列表失败')
    }
  } catch (error) {
    console.error('获取服务套餐列表失败:', error)
    ElMessage.error('获取服务套餐列表失败，请重试')
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
    const response = await updateServicePackageStatus(row.id, row.status)
    if (response.data.success) {
      ElMessage.success(`套餐 ${row.name} 状态已更新为 ${row.status}`)
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
  dialogTitle.value = '添加服务套餐'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEditMode.value = true
  dialogTitle.value = '编辑服务套餐'
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
    
    if (isEditMode.value) {
      // 编辑服务套餐
      const response = await updateServicePackage(serviceForm)
      if (response.data.success) {
        ElMessage.success('编辑成功')
        dialogVisible.value = false
        fetchServicePackages()
      } else {
        ElMessage.error(response.data.message || '编辑失败')
      }
    } else {
      // 添加服务套餐
      const response = await addServicePackage(serviceForm)
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
      const response = await deleteServicePackage(id)
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
  height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
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
  background: var(--gradient-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
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
}

.search-input {
  width: 300px;
}

.filter-select {
  width: 200px;
}

.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
}

.price {
  font-weight: 600;
  color: var(--danger-color);
}

.description {
  max-height: 60px;
  overflow-y: auto;
  text-overflow: ellipsis;
}

.features-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.feature-tag {
  margin: 2px 0;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.even-row {
  background-color: var(--bg-primary);
}

.odd-row {
  background-color: var(--bg-secondary);
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
  color: var(--text-muted);
  font-size: 12px;
  margin-top: 8px;
}
</style>