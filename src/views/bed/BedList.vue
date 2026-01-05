<template>
  <div class="bed-management-container">
    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 页面标题 -->
      <div class="page-title-section">
        <el-button type="primary" plain @click="handleBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回房间列表
        </el-button>
        <h1>{{ roomInfo.roomNumber }} - 床位管理</h1>
      </div>

      <!-- 房间状态信息 -->
      <div class="room-status-section">
        <el-card shadow="hover" class="room-status-card">
          <div class="room-status-content">
            <div class="room-basic-info">
              <div class="room-type">{{ roomInfo.roomType }}</div>
              <el-tag :type="getRoomStatusTag(roomInfo.status)" size="large" class="room-status-tag">
                {{ getRoomStatusText(roomInfo.status) }}
              </el-tag>
            </div>
            <div class="room-details-info">
              <div class="info-item">
                <el-icon><House /></el-icon>
                <span>床位总数: {{ roomInfo.totalBeds }}</span>
              </div>
              <div class="info-item">
                <el-icon><CircleCheck /></el-icon>
                <span>空闲床位: {{ availableBedsCount }}</span>
              </div>
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span>已占用: {{ occupiedBedsCount }}</span>
              </div>
              <div class="info-item">
                <el-icon><TrendCharts /></el-icon>
                <span>占用率: {{ occupancyRate }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 操作栏 -->
      <div class="action-section">
        <el-card shadow="hover" class="action-card">
          <div class="action-content">
            <div class="action-left">
              <el-button type="primary" @click="handleAddBed" size="large" class="add-bed-btn">
                <el-icon><Plus /></el-icon>
                添加床位
              </el-button>
            </div>
            <div class="action-right">
              <el-input
                v-model="searchName"
                placeholder="搜索入住人姓名..."
                class="search-input"
                @keyup.enter="handleSearch"
                clearable
                size="large"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 床位表格 -->
      <div class="table-section">
        <el-card shadow="hover" class="table-card">
          <template #header>
            <div class="table-header">
              <h2>床位列表</h2>
              <div class="table-actions">
                <el-button 
                  :disabled="occupiedBedsCount === 0"
                  @click="handleBatchCheckOut"
                  type="warning"
                  size="large"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  批量出院
                </el-button>
                <el-button size="large" @click="handleExportData">
                  <el-icon><Download /></el-icon>
                  导出数据
                </el-button>
                <el-button size="large" @click="refreshData">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
          </template>

          <el-table
            :data="filteredBeds"
            style="width: 100%"
            stripe
            class="bed-table"
            empty-text="暂无床位数据"
            :default-sort="{ prop: 'bedNumber', order: 'ascending' }"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            
            <el-table-column prop="bedNumber" label="床位号" width="120" sortable>
              <template #default="{ row }">
                <div class="bed-number-cell">
                  <div class="bed-number">{{ row.bedNumber }}</div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag 
                  :type="row.status === '空闲' ? 'success' : 'info'" 
                  size="small"
                >
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="入住人姓名" width="150">
              <template #default="{ row }">
                <div class="resident-name-cell">
                  <el-avatar v-if="row.residentName" :size="32" class="resident-avatar">
                    {{ row.residentName.charAt(0) }}
                  </el-avatar>
                  <span v-if="row.residentName" class="resident-name">{{ row.residentName }}</span>
                  <span v-else class="no-resident">暂无住户</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="联系电话" width="150">
              <template #default="{ row }">
                <div class="phone-cell">
                  <span v-if="row.residentPhone">{{ row.residentPhone }}</span>
                  <span v-else class="no-data">-</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="入住时间" width="200">
              <template #default="{ row }">
                <div class="date-cell">
                  <span v-if="row.checkInDate">{{ formatDate(row.checkInDate) }}</span>
                  <span v-else class="no-data">-</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="离院时间" width="200">
              <template #default="{ row }">
                <div class="date-cell">
                  <span v-if="row.checkOutDate">{{ formatDate(row.checkOutDate) }}</span>
                  <span v-else class="no-data">-</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="notes" label="备注" min-width="200">
              <template #default="{ row }">
                <div class="notes-cell">{{ row.notes || '无备注' }}</div>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button
                    size="small"
                    type="primary"
                    @click="handleEditBed(row)"
                    class="edit-btn"
                  >
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  
                  <el-button
                    v-if="row.status === '已占用'"
                    size="small"
                    type="success"
                    @click="handleCheckOut(row)"
                    class="checkout-btn"
                  >
                    <el-icon><Check /></el-icon>
                    出院
                  </el-button>
                  
                  <el-popconfirm
                    title="确定要删除这个床位吗？"
                    @confirm="handleDeleteBed(row.bedId)"
                    confirm-button-text="确定"
                    cancel-button-text="取消"
                  >
                    <template #reference>
                      <el-button
                        size="small"
                        type="danger"
                        class="delete-btn"
                      >
                        <el-icon><Delete /></el-icon>
                        删除
                      </el-button>
                    </template>
                  </el-popconfirm>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 批量操作栏 -->
          <div v-if="selectedBeds.length > 0" class="batch-action-bar">
            <div class="batch-info">
              <el-icon><Warning /></el-icon>
              <span>已选择 {{ selectedBeds.length }} 个床位</span>
            </div>
            <div class="batch-buttons">
              <el-button
                type="warning"
                @click="handleBatchCheckOut"
              >
                <el-icon><ShoppingCart /></el-icon>
                批量出院
              </el-button>
              <el-button
                @click="clearSelection"
              >
                取消选择
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <div v-if="!bedList.length && !loading" class="empty-state">
        <el-empty
          description="暂无床位数据"
          :image-size="120"
          class="empty-container"
        >
          <el-button type="primary" @click="handleAddBed" size="large">
            <el-icon><Plus /></el-icon>
            添加床位
          </el-button>
        </el-empty>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="10" animated class="loading-skeleton" />
      </div>
    </div>

    <!-- 添加/编辑床位对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑床位信息' : '添加新床位'"
      width="800px"
      center
      destroy-on-close
    >
      <el-form
        ref="bedFormRef"
        :model="bedForm"
        :rules="bedRules"
        label-width="150px"
        size="large"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNumber">
              <el-input
                v-model="bedForm.bedNumber"
                placeholder="如：1、A、VIP-1等"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位状态" prop="status">
              <el-radio-group v-model="bedForm.status">
                <el-radio label="空闲" value="空闲">空闲</el-radio>
                <el-radio label="已占用" value="已占用">已占用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider>住户信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="住户姓名" prop="residentName">
              <el-input
                v-model="bedForm.residentName"
                placeholder="请输入住户姓名"
                :disabled="bedForm.status === '空闲'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="residentPhone">
              <el-input
                v-model="bedForm.residentPhone"
                placeholder="请输入联系电话"
                :disabled="bedForm.status === '空闲'"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住时间" prop="checkInDate">
              <el-date-picker
                v-model="bedForm.checkInDate"
                type="datetime"
                placeholder="选择入住日期和时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled="bedForm.status === '空闲'"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划离院时间" prop="checkOutDate">
              <el-date-picker
                v-model="bedForm.checkOutDate"
                type="datetime"
                placeholder="选择计划离院时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled="bedForm.status === '空闲'"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="bedForm.notes"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" size="large">
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit"
            size="large"
            :loading="submitting"
          >
            {{ isEdit ? '更新床位' : '添加床位' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 出院确认对话框 -->
    <el-dialog
      v-model="checkoutDialogVisible"
      title="确认出院"
      width="600px"
      center
      destroy-on-close
    >
      <div class="checkout-dialog-content">
        <el-alert
          title="出院操作将清空住户信息并将床位状态设为空闲"
          type="warning"
          :closable="false"
          class="checkout-alert"
        />
        
        <div v-if="checkoutBed" class="resident-info-card">
          <h4>住户信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ checkoutBed.residentName }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ checkoutBed.residentPhone || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="床位号">{{ checkoutBed.bedNumber }}</el-descriptions-item>
            <el-descriptions-item label="入住时间">{{ formatDate(checkoutBed.checkInDate) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div v-if="batchCheckoutMode && selectedBeds.length > 0" class="batch-checkout-info">
          <h4>批量出院</h4>
          <p>将清空 {{ selectedBeds.length }} 个床位的住户信息：</p>
          <el-scrollbar height="200px" class="batch-beds-list">
            <el-tag v-for="bed in selectedBeds" :key="bed.bedId" closable @close="removeFromSelection(bed)" class="batch-bed-tag">
              {{ bed.bedNumber }} - {{ bed.residentName || '未知' }}
            </el-tag>
          </el-scrollbar>
        </div>
        
        <div class="checkout-notes">
          <el-form :model="checkoutForm" label-width="100px">
            <el-form-item label="出院备注">
              <el-input
                v-model="checkoutForm.notes"
                type="textarea"
                :rows="3"
                placeholder="请输入出院备注（可选）"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="checkoutDialogVisible = false" size="large">
            取消
          </el-button>
          <el-button 
            type="success" 
            @click="confirmCheckOut"
            size="large"
            :loading="checkoutLoading"
          >
            确认出院
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Plus,
  Search,
  House,
  CircleCheck,
  User,
  Download,
  Refresh,
  Calendar,
  Edit,
  Delete,
  Check,
  Phone,
  ShoppingCart,
  InfoFilled,
  Tools,
  PieChart,
  Warning,
  TrendCharts
} from '@element-plus/icons-vue'
import * as roomApi from '@/api/room'
import * as bedApi from '@/api/bed'

const route = useRoute()
const router = useRouter()

const roomId = route.params.roomId
const roomInfo = ref({})
const bedList = ref([])
const selectedBeds = ref([])
const searchName = ref('')
const loading = ref(false)
const submitting = ref(false)
const checkoutLoading = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const checkoutDialogVisible = ref(false)
const isEdit = ref(false)
const batchCheckoutMode = ref(false)

// 当前操作床位
const currentBedId = ref(null)
const checkoutBed = ref(null)
const bedFormRef = ref()

// 表单数据
const bedForm = reactive({
  roomId: Number(roomId),
  bedNumber: '',
  status: '空闲',
  residentName: '',
  residentPhone: '',
  checkInDate: '',
  checkOutDate: '',
  notes: ''
})

const checkoutForm = reactive({
  notes: ''
})

// 表单验证规则
const bedRules = {
  bedNumber: [
    { required: true, message: '请输入床位号', trigger: 'blur' },
    { min: 1, max: 10, message: '床位号长度在1到10个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  residentName: [
    { 
      required: true, 
      message: '请输入住户姓名', 
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (bedForm.status === '已占用' && !value) {
          callback(new Error('住户姓名不能为空'))
        } else {
          callback()
        }
      }
    }
  ]
}

// 计算属性
const availableBedsCount = computed(() => {
  return bedList.value.filter(bed => bed.status === '空闲').length
})

const occupiedBedsCount = computed(() => {
  return bedList.value.filter(bed => bed.status === '已占用').length
})

const filteredBeds = computed(() => {
  if (!searchName.value) return bedList.value
  
  return bedList.value.filter(bed => 
    bed.residentName && bed.residentName.toLowerCase().includes(searchName.value.toLowerCase())
  )
})

const availablePercentage = computed(() => {
  const total = bedList.value.length
  return total ? Math.round((availableBedsCount.value / total) * 100) : 0
})

const occupiedPercentage = computed(() => {
  const total = bedList.value.length
  return total ? Math.round((occupiedBedsCount.value / total) * 100) : 0
})

const occupancyRate = computed(() => {
  const total = bedList.value.length
  return total ? Math.round((occupiedBedsCount.value / total) * 100) : 0
})

// 监听床位状态变化
watch(() => bedForm.status, (newStatus) => {
  if (newStatus === '空闲') {
    // 状态变为空闲时，清空住户信息
    bedForm.residentName = ''
    bedForm.residentPhone = ''
    bedForm.checkInDate = ''
    bedForm.checkOutDate = ''
  }
})

// 生命周期
onMounted(() => {
  fetchRoomInfo()
  fetchBeds()
})

// 方法
const fetchRoomInfo = async () => {
    try {
      const response = await roomApi.getRoomById(roomId)
      if (response.data) {
        // 适配后端返回的房间信息格式
        roomInfo.value = {
          roomId: response.data.data.id,
          roomNumber: response.data.data.roomNumber,
          roomType: response.data.data.roomType,
          totalBeds: response.data.data.bedCount,
          status: response.data.data.status,
          description: response.data.data.description
        }
      } else {
        ElMessage.error('获取房间信息失败')
      }
    } catch (error) {
      console.error('获取房间信息失败:', error)
      ElMessage.error('获取房间信息失败')
    }
  }

  const fetchBeds = async () => {
    loading.value = true
    try {
      const response = await bedApi.getAllBeds()
      if (response.data && Array.isArray(response.data)) {
        // 适配后端返回的床位数据格式
        bedList.value = response.data.map(bed => ({
          bedId: bed.id,
          roomId: bed.roomId,
          bedNumber: bed.bedNumber,
          status: bed.status === 0 ? '空闲' : '已占用',
          residentName: bed.residentName || '',
          residentPhone: bed.residentPhone || '',
          checkInDate: bed.checkInDate || '',
          checkOutDate: bed.checkOutDate || '',
          notes: bed.notes || ''
        })).filter(bed => bed.roomId === Number(roomId))
        console.log('获取到的床位数据:', bedList.value)
      } else {
        ElMessage.error('获取床位列表失败')
      }
    } catch (error) {
      console.error('获取床位列表失败:', error)
      ElMessage.error('获取床位列表失败')
    } finally {
      loading.value = false
    }
  }

const refreshData = () => {
  fetchRoomInfo()
  fetchBeds()
  ElMessage.success('数据已刷新')
}

const handleSearch = () => {
  // 搜索逻辑已经在计算属性中处理
}

const handleBack = () => {
  router.push('/rooms')
}

const handleAddBed = () => {
  isEdit.value = false
  Object.keys(bedForm).forEach(key => {
    if (key !== 'roomId') {
      bedForm[key] = ''
    }
  })
  bedForm.status = '空闲'
  dialogVisible.value = true
}

const handleEditBed = (bed) => {
  isEdit.value = true
  currentBedId.value = bed.bedId
  Object.assign(bedForm, bed)
  // 转换日期格式
  if (bedForm.checkInDate) {
    bedForm.checkInDate = formatDateTimeForInput(bedForm.checkInDate)
  }
  if (bedForm.checkOutDate) {
    bedForm.checkOutDate = formatDateTimeForInput(bedForm.checkOutDate)
  }
  dialogVisible.value = true
}

const handleCheckOut = (bed) => {
  checkoutBed.value = bed
  batchCheckoutMode.value = false
  checkoutForm.notes = ''
  checkoutDialogVisible.value = true
}

const handleBatchCheckOut = () => {
  if (selectedBeds.value.length === 0) {
    ElMessage.warning('请先选择需要出院的床位')
    return
  }
  
  checkoutBed.value = null
  batchCheckoutMode.value = true
  checkoutForm.notes = ''
  checkoutDialogVisible.value = true
}

const confirmCheckOut = async () => {
  checkoutLoading.value = true
  try {
    if (batchCheckoutMode.value) {
      // 批量出院
      const promises = selectedBeds.value.map(bed => {
        const updatedBed = {
          ...bed,
          status: '空闲',
          residentName: '',
          residentPhone: '',
          checkInDate: '',
          checkOutDate: '',
          notes: checkoutForm.notes || bed.notes
        }
        return bedApi.updateBed(updatedBed)
      })
      
      await Promise.all(promises)
      ElMessage.success(`成功为 ${selectedBeds.value.length} 个床位办理出院`)
      selectedBeds.value = []
    } else {
      // 单个床位出院
      const updatedBed = {
        ...checkoutBed.value,
        status: '空闲',
        residentName: '',
        residentPhone: '',
        checkInDate: '',
        checkOutDate: '',
        notes: checkoutForm.notes || checkoutBed.value.notes
      }
      
      await bedApi.updateBed(updatedBed)
      ElMessage.success('出院成功')
    }
    
    checkoutDialogVisible.value = false
    fetchBeds()
    
  } catch (error) {
    console.error('出院操作失败:', error)
    ElMessage.error('出院操作失败')
  } finally {
    checkoutLoading.value = false
  }
}

const handleSubmit = async () => {
    try {
      await bedFormRef.value.validate()
      submitting.value = true
      
      // 准备提交数据，适配后端字段名
      const submitData = {
        roomId: Number(roomId),
        bedNumber: bedForm.bedNumber,
        status: bedForm.status === '空闲' ? 0 : 1,
        residentName: bedForm.status === '空闲' ? '' : bedForm.residentName,
        residentPhone: bedForm.status === '空闲' ? '' : bedForm.residentPhone,
        checkInDate: bedForm.status === '空闲' ? null : bedForm.checkInDate,
        checkOutDate: bedForm.status === '空闲' ? null : bedForm.checkOutDate,
        notes: bedForm.notes || ''
      }
      
      if (isEdit.value) {
        submitData.id = currentBedId.value
        const response = await bedApi.updateBed(submitData)
        if (response.status === 200) {
          ElMessage.success({
            message: '床位信息更新成功',
            type: 'success',
            showClose: true
          })
          dialogVisible.value = false
          fetchBeds()
        } else {
          ElMessage.error('更新失败')
        }
      } else {
        const response = await bedApi.addBed(submitData)
        if (response.status === 200) {
          ElMessage.success({
            message: '床位添加成功',
            type: 'success',
            showClose: true
          })
          dialogVisible.value = false
          fetchBeds()
        } else {
          ElMessage.error('添加失败')
        }
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败，请重试')
    } finally {
      submitting.value = false
    }
  }

const handleDeleteBed = async (bedId) => {
    try {
      const response = await bedApi.deleteBed(bedId)
      if (response.status === 200) {
        ElMessage.success({
          message: '床位删除成功',
          type: 'success',
          showClose: true
        })
        fetchBeds()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }

  const handleEditRoom = () => {
    router.push('/rooms')
    setTimeout(() => {
      // 这里可以触发房间编辑对话框的打开
      // 需要在 RoomList.vue 中添加相应的方法
    }, 100)
  }

  const handleSetRepair = async () => {
    try {
      await ElMessageBox.confirm('确定要将此房间设为维修状态吗？', '设为维修', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      
      const response = await roomApi.setRoomRepair(roomId, {
        repairReason: '设备故障',
        repairNotes: '需要维修'
      })
      
      if (response.status === 200) {
        ElMessage.success('房间已设为维修状态')
        fetchRoomInfo()
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('设为维修失败:', error)
        ElMessage.error('操作失败')
      }
    }
  }

  const handleCompleteRepair = async () => {
    try {
      await ElMessageBox.confirm('确定要完成维修吗？', '完成维修', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      
      const response = await roomApi.completeRepair(roomId)
      
      if (response.status === 200) {
        ElMessage.success('维修完成，房间已恢复为空闲状态')
        fetchRoomInfo()
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('完成维修失败:', error)
        ElMessage.error('操作失败')
      }
    }
  }

const handleExportData = () => {
  // 导出数据逻辑
  const dataStr = JSON.stringify(bedList.value, null, 2)
  const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr)
  
  const exportFileDefaultName = `${roomInfo.value.roomNumber}_床位数据_${new Date().toISOString().slice(0,10)}.json`
  
  const linkElement = document.createElement('a')
  linkElement.setAttribute('href', dataUri)
  linkElement.setAttribute('download', exportFileDefaultName)
  linkElement.click()
  
  ElMessage.success('数据导出成功')
}

const handleSelectionChange = (selection) => {
  selectedBeds.value = selection
}

const clearSelection = () => {
  selectedBeds.value = []
}

const removeFromSelection = (bed) => {
  const index = selectedBeds.value.findIndex(item => item.bedId === bed.bedId)
  if (index > -1) {
    selectedBeds.value.splice(index, 1)
  }
}

const handleDialogClose = () => {
  if (bedFormRef.value) {
    bedFormRef.value.resetFields()
  }
}

// 工具方法
const getRoomStatusText = (status) => {
  const map = { 0: '空闲', 1: '已满', 2: '维修中' }
  return map[status] || '未知'
}

const getRoomStatusTag = (status) => {
  const map = { 0: 'success', 1: 'info', 2: 'warning' }
  return map[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '未设置'
  try {
    const dateObj = new Date(date)
    return dateObj.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return date
  }
}

const formatDateTimeForInput = (date) => {
  if (!date) return ''
  try {
    const d = new Date(date)
    return d.toISOString().slice(0, 19).replace('T', ' ')
  } catch (e) {
    return date
  }
}
</script>

<style scoped>
/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.bed-management-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 主内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面标题区域 */
.page-title-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.page-title-section h1 {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}

.back-btn {
  font-weight: 500;
}

/* 房间状态区域 */
.room-status-section {
  margin-bottom: 24px;
}

.room-status-card {
  border-radius: 12px;
  overflow: hidden;
}

.room-status-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.room-basic-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.room-type {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
}

.room-status-tag {
  font-size: 16px;
  font-weight: 600;
}

.room-details-info {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #606266;
}

.info-item .el-icon {
  color: #667eea;
  font-size: 20px;
}

/* 操作区域 */
.action-section {
  margin-bottom: 24px;
}

.action-card {
  border-radius: 12px;
  overflow: hidden;
}

.action-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.action-left {
  display: flex;
  gap: 16px;
}

.action-right {
  flex: 1;
  max-width: 500px;
}

.add-bed-btn {
  border-radius: 8px;
  font-weight: 600;
}

.search-input {
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 表格区域 */
.table-section {
  margin-bottom: 24px;
}

.table-card {
  border-radius: 12px;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
}

.table-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.bed-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 表格单元格样式 */
.bed-number-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bed-number {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.resident-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resident-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-weight: 600;
  font-size: 16px;
}

.resident-name {
  font-weight: 600;
  color: #303133;
}

.no-resident {
  color: #909399;
  font-style: italic;
}

.no-data {
  color: #909399;
}

.phone-cell, .date-cell, .notes-cell {
  color: #606266;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 批量操作栏 */
.batch-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(255, 183, 77, 0.1);
  border-radius: 8px;
  margin-top: 16px;
  border: 1px solid rgba(255, 183, 77, 0.2);
}

.batch-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #e6a23c;
  font-weight: 600;
}

.batch-buttons {
  display: flex;
  gap: 12px;
}

/* 空状态 */
.empty-state {
  margin: 60px 0;
  text-align: center;
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

/* 加载状态 */
.loading-state {
  margin: 20px 0;
}

.loading-skeleton {
  border-radius: 8px;
}

/* 对话框样式 */
.checkout-dialog-content {
  padding: 20px 0;
}

.checkout-alert {
  margin-bottom: 20px;
}

.resident-info-card {
  margin-bottom: 20px;
}

.resident-info-card h4 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.batch-checkout-info h4 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.batch-checkout-info p {
  margin-bottom: 16px;
  color: #606266;
}

.batch-beds-list {
  margin-bottom: 20px;
}

.batch-bed-tag {
  margin: 4px;
}

.checkout-notes {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .bed-management-container {
    padding: 16px;
  }
  
  .room-status-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .room-details-info {
    width: 100%;
  }
  
  .action-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .action-right {
    max-width: 100%;
  }
  
  .table-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .table-actions {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .page-title-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .page-title-section h1 {
    font-size: 24px;
  }
  
  .room-basic-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .room-details-info {
    gap: 16px;
  }
  
  .info-item {
    font-size: 14px;
  }
  
  .action-left {
    flex-direction: column;
  }
  
  .table-actions {
    flex-direction: column;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .batch-action-bar {
    flex-direction: column;
    gap: 12px;
  }
}
</style>