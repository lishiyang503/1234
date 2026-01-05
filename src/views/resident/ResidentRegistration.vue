<template>
  <div class="resident-registration-container">
    <div class="page-header">
      <h2 class="page-title">入住管理</h2>
      <p class="page-subtitle">管理老人的入住登记信息</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">入住登记</h3>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增登记
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
            />
            <el-input
              v-model="searchParams.idCard"
              placeholder="搜索身份证号"
              prefix-icon="Document"
              clearable
              class="search-input"
            />
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 入住登记表格 -->
          <div class="table-container">
            <el-table
              :data="residentList"
              border
              stripe
              style="width: 100%"
              :row-class-name="tableRowClassName"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="name" label="老人姓名" width="150" />
              <el-table-column prop="idCard" label="身份证号" width="200" />
              <el-table-column prop="gender" label="性别" width="80" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.gender === '男' ? 'info' : 'success'">
                    {{ scope.row.gender }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="age" label="年龄" width="80" align="center" />
              <el-table-column prop="roomNumber" label="房间号" width="120" align="center" />
              <el-table-column prop="bedNumber" label="床位号" width="120" align="center" />
              <el-table-column prop="entryDate" label="入住日期" width="150" align="center" />
              <el-table-column prop="status" label="状态" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '入住' ? 'success' : 'warning'">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="showEditDialog(scope.row)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                  <el-button type="warning" size="small" @click="handleCheckout(scope.row.id)">
                    <el-icon><ArrowRight /></el-icon>
                    退房
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
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="基础信息" name="basic">
          <el-form
            ref="residentFormRef"
            :model="residentForm"
            :rules="residentRules"
            label-width="120px"
          >
            <div class="form-row">
              <el-form-item label="老人姓名" prop="name" class="form-item">
                <el-input v-model="residentForm.name" placeholder="请输入老人姓名" />
              </el-form-item>
              <el-form-item label="身份证号" prop="idCard" class="form-item">
                <el-input v-model="residentForm.idCard" placeholder="请输入身份证号" />
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="性别" prop="gender" class="form-item">
                <el-select v-model="residentForm.gender" placeholder="请选择性别">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                </el-select>
              </el-form-item>
              <el-form-item label="年龄" prop="age" class="form-item">
                <el-input v-model="residentForm.age" type="number" placeholder="请输入年龄" />
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="联系电话" prop="phone" class="form-item">
                <el-input v-model="residentForm.phone" placeholder="请输入联系电话" />
              </el-form-item>
              <el-form-item label="紧急联系人" prop="emergencyContact" class="form-item">
                <el-input v-model="residentForm.emergencyContact" placeholder="请输入紧急联系人" />
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="紧急联系电话" prop="emergencyPhone" class="form-item">
                <el-input v-model="residentForm.emergencyPhone" placeholder="请输入紧急联系电话" />
              </el-form-item>
              <el-form-item label="入住日期" prop="entryDate" class="form-item">
                <el-date-picker
                  v-model="residentForm.entryDate"
                  type="date"
                  placeholder="选择入住日期"
                  style="width: 100%"
                />
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="房间号" prop="roomNumber" class="form-item">
                <el-input v-model="residentForm.roomNumber" placeholder="请输入房间号" />
              </el-form-item>
              <el-form-item label="床位号" prop="bedNumber" class="form-item">
                <el-input v-model="residentForm.bedNumber" placeholder="请输入床位号" />
              </el-form-item>
            </div>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="健康信息" name="health">
          <el-form
            :model="residentForm"
            label-width="120px"
          >
            <el-form-item label="既往病史">
              <el-input
                v-model="residentForm.medicalHistory"
                type="textarea"
                :rows="4"
                placeholder="请输入既往病史"
              />
            </el-form-item>
            <el-form-item label="过敏史">
              <el-input
                v-model="residentForm.allergyHistory"
                type="textarea"
                :rows="4"
                placeholder="请输入过敏史"
              />
            </el-form-item>
            <el-form-item label="健康状况">
              <el-input
                v-model="residentForm.healthStatus"
                type="textarea"
                :rows="4"
                placeholder="请输入健康状况"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
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
import { getResidentList, addResident, updateResident, deleteResident, checkoutResident } from '@/api/resident'

// 搜索和筛选参数
const searchParams = reactive({
  name: '',
  idCard: ''
})

// 表格数据
const residentList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增入住登记')
const isEditMode = ref(false)
const activeTab = ref('basic')

// 表单引用
const residentFormRef = ref()

// 表单数据
const residentForm = reactive({
  id: null,
  name: '',
  idCard: '',
  gender: '',
  age: '',
  phone: '',
  emergencyContact: '',
  emergencyPhone: '',
  entryDate: '',
  roomNumber: '',
  bedNumber: '',
  medicalHistory: '',
  allergyHistory: '',
  healthStatus: '',
  status: '入住'
})

// 表单验证规则
const residentRules = {
  name: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  emergencyContact: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }],
  emergencyPhone: [{ required: true, message: '请输入紧急联系电话', trigger: 'blur' }],
  entryDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }]
}

// 生命周期
onMounted(() => {
  fetchResidentList()
})

// 获取入住登记列表
const fetchResidentList = async () => {
  loading.value = true
  try {
    const response = await getResidentList({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    console.log('入住登记列表响应:', response)
    
    // 增强数据格式兼容性
    if (response.data) {
      if (response.data.success) {
        residentList.value = response.data.data.list || []
        total.value = response.data.data.total || 0
      } else {
        ElMessage.error(response.data.message || '获取入住登记列表失败')
      }
    } else if (Array.isArray(response.data)) {
      // 兼容直接返回数组的情况
      residentList.value = response.data
      total.value = response.data.length
    } else {
      ElMessage.error('获取入住登记列表失败: 数据格式不正确')
    }
  } catch (error) {
    console.error('获取入住登记列表失败:', error)
    // 改进错误处理
    if (error.response) {
      // 服务器返回了错误响应
      ElMessage.error(`获取入住登记列表失败: ${error.response.status} ${error.response.statusText}`)
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error('获取入住登记列表失败: 服务器无响应')
    } else {
      // 请求配置错误
      ElMessage.error(`获取入住登记列表失败: ${error.message}`)
    }
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResidentList()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchResidentList()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 显示添加对话框
const showAddDialog = () => {
  isEditMode.value = false
  dialogTitle.value = '新增入住登记'
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEditMode.value = true
  dialogTitle.value = '编辑入住登记'
  Object.assign(residentForm, row)
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(residentForm, {
    id: null,
    name: '',
    idCard: '',
    gender: '',
    age: '',
    phone: '',
    emergencyContact: '',
    emergencyPhone: '',
    entryDate: '',
    roomNumber: '',
    bedNumber: '',
    medicalHistory: '',
    allergyHistory: '',
    healthStatus: '',
    status: '入住'
  })
  activeTab.value = 'basic'
  if (residentFormRef.value) {
    residentFormRef.value.resetFields()
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    await residentFormRef.value.validate()
    
    let response
    let successMessage
    let errorMessage
    
    if (isEditMode.value) {
      // 编辑入住登记
      response = await updateResident(residentForm)
      successMessage = '编辑成功'
      errorMessage = '编辑失败'
    } else {
      // 添加入住登记
      response = await addResident(residentForm)
      successMessage = '新增成功'
      errorMessage = '新增失败'
    }
    
    if (response.data.success) {
      ElMessage.success(successMessage)
      dialogVisible.value = false
      fetchResidentList()
    } else {
      ElMessage.error(response.data.message || errorMessage)
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error' && error.message === '表单验证失败') {
      // 表单验证失败，不显示额外提示
      return
    }
    
    if (error.response) {
      ElMessage.error(`${errorMessage}: ${error.response.status} ${error.response.statusText}`)
    } else if (error.request) {
      ElMessage.error(`${errorMessage}: 服务器无响应`)
    } else {
      ElMessage.error(`${errorMessage}: ${error.message}`)
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
      const response = await deleteResident(id)
      if (response.data.success) {
        ElMessage.success('删除成功')
        fetchResidentList()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      if (error.response) {
        ElMessage.error(`删除失败: ${error.response.status} ${error.response.statusText}`)
      } else if (error.request) {
        ElMessage.error('删除失败: 服务器无响应')
      } else {
        ElMessage.error(`删除失败: ${error.message}`)
      }
    }
  }).catch(() => {
    // 取消删除
  })
}

// 办理退房
const handleCheckout = (id) => {
  ElMessageBox.confirm('确定要办理退房吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await checkoutResident(id)
      if (response.data.success) {
        ElMessage.success('退房成功')
        fetchResidentList()
      } else {
        ElMessage.error(response.data.message || '退房失败')
      }
    } catch (error) {
      console.error('退房失败:', error)
      if (error.response) {
        ElMessage.error(`退房失败: ${error.response.status} ${error.response.statusText}`)
      } else if (error.request) {
        ElMessage.error('退房失败: 服务器无响应')
      } else {
        ElMessage.error(`退房失败: ${error.message}`)
      }
    }
  }).catch(() => {
    // 取消退房
  })
}
</script>

<style scoped>
.resident-registration-container {
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

.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
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
</style>