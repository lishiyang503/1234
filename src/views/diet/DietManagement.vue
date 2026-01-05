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
          <el-button type="primary" @click="showAddDialog">
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
            />
            <el-select
              v-model="searchParams.dietType"
              placeholder="筛选饮食类型"
              clearable
              class="filter-select"
            >
              <el-option label="糖尿病饮食" value="diabetes" />
              <el-option label="高血压饮食" value="hypertension" />
              <el-option label="高血脂饮食" value="hyperlipidemia" />
              <el-option label="低盐饮食" value="lowSalt" />
              <el-option label="低脂肪饮食" value="lowFat" />
              <el-option label="其他" value="other" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 膳食需求表格 -->
          <div class="table-container">
            <el-table
              :data="dietList"
              border
              stripe
              style="width: 100%"
              :row-class-name="tableRowClassName"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="residentName" label="老人姓名" width="150" />
              <el-table-column prop="roomNumber" label="房间号" width="120" align="center" />
              <el-table-column prop="bedNumber" label="床位号" width="120" align="center" />
              <el-table-column prop="dietType" label="饮食类型" width="150" />
              <el-table-column prop="description" label="详细说明" min-width="200" />
              <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
              <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
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
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="dietFormRef"
        :model="dietForm"
        :rules="dietRules"
        label-width="120px"
      >
        <el-form-item label="老人姓名" prop="residentName">
          <el-input v-model="dietForm.residentName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="dietForm.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-input v-model="dietForm.bedNumber" placeholder="请输入床位号" />
        </el-form-item>
        <el-form-item label="饮食类型" prop="dietType">
          <el-select v-model="dietForm.dietType" placeholder="请选择饮食类型">
            <el-option label="糖尿病饮食" value="diabetes" />
            <el-option label="高血压饮食" value="hypertension" />
            <el-option label="高血脂饮食" value="hyperlipidemia" />
            <el-option label="低盐饮食" value="lowSalt" />
            <el-option label="低脂肪饮食" value="lowFat" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" prop="description">
          <el-input
            v-model="dietForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入详细说明"
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
import { getDietList, addDiet, updateDiet, deleteDiet } from '@/api/diet'

// 搜索和筛选参数
const searchParams = reactive({
  name: '',
  dietType: ''
})

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
})

// 获取膳食需求列表
const fetchDietList = async () => {
  loading.value = true
  try {
    const response = await getDietList({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.data.success) {
      dietList.value = response.data.data.list || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取膳食需求列表失败')
    }
  } catch (error) {
    console.error('获取膳食需求列表失败:', error)
    ElMessage.error('获取膳食需求列表失败，请重试')
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
  Object.assign(dietForm, row)
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
    
    if (isEditMode.value) {
      // 编辑膳食需求
      const response = await updateDiet(dietForm)
      if (response.data.success) {
        ElMessage.success('编辑成功')
        dialogVisible.value = false
        fetchDietList()
      } else {
        ElMessage.error(response.data.message || '编辑失败')
      }
    } else {
      // 添加膳食需求
      const response = await addDiet(dietForm)
      if (response.data.success) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchDietList()
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
      const response = await deleteDiet(id)
      if (response.data.success) {
        ElMessage.success('删除成功')
        fetchDietList()
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
.diet-management-container {
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
</style>