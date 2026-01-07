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
              <el-table-column label="ID" width="80" align="center">
                <template #default="scope">
                  {{ scope.$index + 1 }}
                </template>
              </el-table-column>
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
              <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                  <el-dropdown>
                    <el-button type="primary">
                      操作
                      <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="showEditDialog(scope.row)">
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-dropdown-item>
                        <el-dropdown-item @click="handleDelete(scope.row.actualId)" type="danger">
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                        <el-dropdown-item @click="handleCheckout(scope.row.actualId)" type="warning">
                          <el-icon><ArrowRight /></el-icon>
                          退房
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
                <el-input v-model="residentForm.age" type="number" placeholder="根据身份证号自动计算" readonly />
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
                <el-select v-model="residentForm.roomNumber" placeholder="请选择房间号" @change="handleRoomChange">
                  <el-option
                    v-for="room in roomList"
                    :key="room.roomNumber"
                    :label="room.roomNumber"
                    :value="room.roomNumber"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="床位号" prop="bedNumber" class="form-item">
                <el-select v-model="residentForm.bedNumber" placeholder="请选择床位号">
                  <el-option
                    v-for="bed in availableBeds"
                    :key="bed.bedNumber"
                    :label="bed.bedNumber"
                    :value="bed.bedNumber"
                  />
                </el-select>
              </el-form-item>
            </div>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="健康信息" name="health">
          <el-form
            :model="residentForm.healthRecord"
            label-width="120px"
            class="health-form"
          >
            <div class="form-row">
              <el-form-item label="身高(cm)" class="form-item">
                <el-input 
                  v-model="residentForm.healthRecord.height" 
                  type="number" 
                  placeholder="请输入身高，如：170.5" 
                  min="50" 
                  max="250" 
                  step="0.1"
                  clearable
                />
              </el-form-item>
              <el-form-item label="体重(kg)" class="form-item">
                <el-input 
                  v-model="residentForm.healthRecord.weight" 
                  type="number" 
                  placeholder="请输入体重，如：65.5" 
                  min="20" 
                  max="200" 
                  step="0.1"
                  clearable
                />
              </el-form-item>
              <el-form-item label="血型" class="form-item">
                <el-select 
                  v-model="residentForm.healthRecord.bloodType" 
                  placeholder="请选择血型"
                  clearable
                >
                  <el-option label="A型" value="A型" />
                  <el-option label="B型" value="B型" />
                  <el-option label="AB型" value="AB型" />
                  <el-option label="O型" value="O型" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </div>
            <el-form-item label="既往病史" class="form-item">
              <el-input
                v-model="residentForm.healthRecord.medicalHistory"
                type="textarea"
                :rows="4"
                placeholder="请详细描述老人的既往病史，如：高血压、糖尿病等"
                resize="vertical"
              />
              <div class="form-hint">提示：请包括疾病名称、确诊时间、治疗情况等信息</div>
            </el-form-item>
            <el-form-item label="过敏史" class="form-item">
              <el-input
                v-model="residentForm.healthRecord.allergyHistory"
                type="textarea"
                :rows="4"
                placeholder="请详细描述老人的过敏史，如：青霉素过敏、海鲜过敏等"
                resize="vertical"
              />
              <div class="form-hint">提示：请包括过敏原、过敏反应症状等信息</div>
            </el-form-item>
            <el-form-item label="健康状况" class="form-item">
              <el-input
                v-model="residentForm.healthRecord.healthNotes"
                type="textarea"
                :rows="4"
                placeholder="请详细描述老人的当前健康状况"
                resize="vertical"
              />
              <div class="form-hint">提示：请包括身体状况、活动能力、特殊需求等信息</div>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResidentList, addResident, updateResident, deleteResident, checkoutResident } from '@/api/resident'
import { getAllRooms } from '@/api/room'
import { getAllBeds } from '@/api/bed'

// 根据身份证号计算年龄
const calculateAgeFromIdCard = (idCard) => {
  if (!idCard || idCard.length !== 18) {
    return ''
  }
  try {
    // 从身份证号中提取出生年月（第7-14位）
    const birthDateStr = idCard.substring(6, 14)
    const birthYear = parseInt(birthDateStr.substring(0, 4))
    const birthMonth = parseInt(birthDateStr.substring(4, 6))
    const birthDay = parseInt(birthDateStr.substring(6, 8))
    
    // 获取当前日期
    const now = new Date()
    const currentYear = now.getFullYear()
    const currentMonth = now.getMonth() + 1 // 月份从0开始
    const currentDay = now.getDate()
    
    // 计算年龄
    let age = currentYear - birthYear
    
    // 调整年龄（如果生日还没过）
    if (currentMonth < birthMonth || (currentMonth === birthMonth && currentDay < birthDay)) {
      age--
    }
    
    return age
  } catch (error) {
    return ''
  }
}

// 监听身份证号变化，自动计算年龄
watch(
  () => residentForm.idCard,
  (newIdCard) => {
    if (newIdCard) {
      residentForm.age = calculateAgeFromIdCard(newIdCard)
    } else {
      residentForm.age = ''
    }
  }
)

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

// 房间和床位数据
const roomList = ref([])
const bedList = ref([])
const availableBeds = ref([])
const loadingRooms = ref(false)
const loadingBeds = ref(false)

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
  status: '入住',
  healthRecord: {
    height: '',
    weight: '',
    bloodType: '',
    medicalHistory: '',
    allergyHistory: '',
    healthNotes: ''
  }
})

// 表单验证规则
const residentRules = {
  name: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  emergencyContact: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }],
  emergencyPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  entryDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请选择房间号', trigger: 'change' }],
  bedNumber: [{ required: true, message: '请选择床位号', trigger: 'change' }]
}

// 获取房间列表
const fetchRooms = async () => {
  loadingRooms.value = true
  try {
    const response = await getAllRooms()
    console.log('房间列表响应:', response)
    
    // 兼容不同的响应格式
    if (response.data && response.data.success) {
      roomList.value = response.data.data || []
    } else if (response.data) {
      // 如果直接返回数据数组
      roomList.value = response.data
    } else {
      // 其他情况
      roomList.value = []
    }
    
    // 过滤出空闲的房间（状态为0表示空闲）
    roomList.value = roomList.value.filter(room => room.status === 0)
    
    console.log('处理后的roomList:', roomList.value)
    ElMessage.success('获取空闲房间列表成功')
  } catch (error) {
    console.error('获取房间列表失败:', error)
    ElMessage.error('获取房间列表失败: ' + (error.message || '未知错误'))
    roomList.value = [] // 确保roomList始终是数组
  } finally {
    loadingRooms.value = false
  }
}

// 获取床位列表
const fetchBeds = async () => {
  loadingBeds.value = true
  try {
    const response = await getAllBeds()
    console.log('床位列表响应:', response)
    // 兼容不同的响应格式
    if (response.data && response.data.success) {
      bedList.value = response.data.data || []
    } else if (response.data) {
      // 如果直接返回数据数组
      bedList.value = response.data
    } else {
      // 其他情况
      bedList.value = []
    }
    // 默认过滤可用床位
    updateAvailableBeds()
    ElMessage.success('获取床位列表成功')
  } catch (error) {
    console.error('获取床位列表失败:', error)
    ElMessage.error('获取床位列表失败: ' + (error.message || '未知错误'))
  } finally {
    loadingBeds.value = false
  }
}

// 更新可用床位
const updateAvailableBeds = () => {
  // 确保bedList.value是数组
  const beds = Array.isArray(bedList.value) ? bedList.value : []
  console.log('当前bedList:', beds)
  console.log('当前roomNumber:', residentForm.roomNumber)
  
  if (residentForm.roomNumber) {
    // 筛选对应房间的可用床位（0表示空闲）
    // 床位号格式为"房间号-床位"，如"101-A"，所以可以通过床位号前缀匹配房间号
    availableBeds.value = beds.filter(bed => {
      // 检查bed是否有效
      if (!bed || !bed.bedNumber) return false
      
      // 获取床位号的房间号部分（如"101-A" -> "101"）
      const bedRoomNumber = bed.bedNumber.split('-')[0]
      
      console.log('检查床位:', bed.bedNumber, '房间号:', bedRoomNumber, '状态:', bed.status)
      
      return bedRoomNumber === residentForm.roomNumber && bed.status === 0
    })
  } else {
    // 没有选择房间时，显示所有可用床位
    availableBeds.value = beds.filter(bed => bed && bed.status === 0)
  }
  
  console.log('可用床位:', availableBeds.value)
}

// 处理房间变化
const handleRoomChange = () => {
  // 清空当前选择的床位
  residentForm.bedNumber = ''
  // 更新可用床位
  updateAvailableBeds()
}

// 生命周期
onMounted(() => {
  fetchResidentList()
  fetchRooms()
  fetchBeds()
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
  // 重新获取最新的房间和床位数据
  fetchRooms()
  fetchBeds()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEditMode.value = true
  dialogTitle.value = '编辑入住登记'
  // 重置表单，确保healthRecord对象存在
  resetForm()
  // 填充基础信息
  Object.assign(residentForm, {
    id: row.actualId || row.id,
    name: row.name,
    idCard: row.idCard,
    gender: row.gender,
    age: row.age,
    phone: row.phone,
    emergencyContact: row.emergencyContact,
    emergencyPhone: row.emergencyPhone,
    entryDate: row.entryDate,
    roomNumber: row.roomNumber,
    bedNumber: row.bedNumber,
    status: row.status
  })
  // 填充健康信息
  if (row.healthRecord) {
    Object.assign(residentForm.healthRecord, row.healthRecord)
  }
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
    status: '入住',
    healthRecord: {
      height: '',
      weight: '',
      bloodType: '',
      medicalHistory: '',
      allergyHistory: '',
      healthNotes: ''
    }
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

/* 健康表单样式 */
.health-form {
  background-color: var(--bg-secondary);
  padding: 20px;
  border-radius: var(--border-radius);
}

/* 表单提示信息 */
.form-hint {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 8px;
  padding-left: 12px;
  border-left: 3px solid var(--border-color);
}

/* 文本域样式优化 */
:deep(.el-textarea__inner) {
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
}

/* 输入框样式优化 */
:deep(.el-input__wrapper) {
  border-radius: var(--border-radius);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
}

/* 选择框样式优化 */
:deep(.el-select__wrapper) {
  border-radius: var(--border-radius);
}

:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.2);
}

/* 标签页样式优化 */
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.el-tabs__item.is-active) {
  color: var(--primary-color);
  font-weight: 600;
}

:deep(.el-tabs__active-bar) {
  background-color: var(--primary-color);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-item {
    min-width: 100%;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .health-form {
    padding: 16px;
  }
}
</style>