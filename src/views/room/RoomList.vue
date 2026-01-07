<template>
  <div class="room-list-container">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-section">
        <el-input
          v-model="searchParams.roomNumber"
          placeholder="搜索房间号..."
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <div class="filter-group">
          <el-select
            v-model="searchParams.roomType"
            placeholder="房间类型"
            class="filter-select"
            @change="handleSearch"
            clearable
          >
            <el-option label="单人间" value="单人间" />
            <el-option label="双人间" value="双人间" />
            <el-option label="三人间" value="三人间" />
          </el-select>
          
          <el-select
            v-model="searchParams.status"
            placeholder="房间状态"
            class="filter-select"
            @change="handleSearch"
            clearable
          >
            <el-option label="空闲" :value="0" />
            <el-option label="已满" :value="1" />
            <el-option label="维修中" :value="2" />
          </el-select>
          
          <el-button @click="handleReset" class="reset-btn">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
      </div>
      
      <el-button 
        type="primary" 
        class="add-btn"
        @click="handleAddRoom"
      >
        <el-icon><Plus /></el-icon>
        添加新房间
      </el-button>
    </div>

    <!-- 房间卡片网格 -->
    <div class="rooms-grid">
      <!-- 添加房间卡片 -->
      <div class="add-room-card" @click="handleAddRoom">
        <div class="add-icon">
          <el-icon><Plus /></el-icon>
        </div>
        <div class="add-text">添加新房间</div>
      </div>

      <!-- 房间卡片 -->
      <div 
        v-for="room in roomList" 
        :key="room.roomId"
        class="room-card"
        :class="getRoomCardClass(room.status)"
        @click="handleCardClick(room.roomId)"
      >
        <div class="card-header">
          <div class="room-number">
            <div class="room-icon">
              <el-icon><House /></el-icon>
            </div>
            <h3>{{ room.roomNumber }}</h3>
          </div>
          <el-tag 
            :type="getStatusTagType(room.status)" 
            class="status-tag"
            :class="getStatusTagClass(room.status)"
          >
            {{ getStatusText(room.status) }}
          </el-tag>
        </div>
        
        <div class="card-content">
          <div class="room-info">
            <div class="info-item">
              <el-icon><Star /></el-icon>
              <span>{{ room.roomType }}</span>
            </div>
            <div class="info-item">
              <el-icon><House /></el-icon>
              <span>{{ room.totalBeds }} 个床位</span>
            </div>
            <div class="info-item" v-if="room.description">
              <el-icon><Document /></el-icon>
              <span>{{ room.description }}</span>
            </div>
          </div>
          
          <!-- 维修信息 -->
          <div v-if="room.status === 2" class="repair-section">
            <div class="repair-header">
              <el-icon><Tools /></el-icon>
              <span>维修信息</span>
            </div>
            <div class="repair-details">
              <p class="repair-reason">{{ room.repairReason }}</p>
              <div class="repair-time">
                <span>开始: {{ formatDate(room.repairStartDate) }}</span>
                <span v-if="room.repairEndDate"> | 预计完成: {{ formatDate(room.repairEndDate) }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card-footer">
          <div class="action-buttons">
            <el-button 
              v-if="room.status === 0"
              class="action-btn repair-btn"
              @click.stop="handleSetRepair(room)"
            >
              <el-icon><Tools /></el-icon>
              设为维修
            </el-button>
            <el-button 
              v-if="room.status === 2"
              class="action-btn complete-btn"
              @click.stop="handleCompleteRepair(room.roomId)"
            >
              <el-icon><Check /></el-icon>
              完成维修
            </el-button>
            
            <el-dropdown @click.stop class="more-dropdown">
              <el-button class="action-btn more-btn">
                <el-icon><More /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click.stop="handleEditRoom(room)">
                    <el-icon><Edit /></el-icon>
                    编辑房间
                  </el-dropdown-item>
                  <el-dropdown-item 
                    @click.stop="handleDeleteRoom(room.roomId)"
                    class="delete-option"
                  >
                    <el-icon><Delete /></el-icon>
                    删除房间
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!roomList.length && !loading" class="empty-state">
      <div class="empty-illustration">
        <div class="empty-icon">🏢</div>
        <h3>暂无房间数据</h3>
        <p>点击"添加新房间"按钮来创建第一个房间</p>
        <el-button type="primary" @click="handleAddRoom" class="empty-btn">
          添加房间
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>正在加载房间数据...</p>
    </div>

    <!-- 添加/编辑房间对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑房间信息' : '添加新房间'"
      width="500px"
      class="room-dialog"
      @close="handleDialogClose"
    >
      <el-form
        ref="roomFormRef"
        :model="roomForm"
        :rules="roomRules"
        label-width="100px"
        class="room-form"
      >
        <el-form-item label="房间号" prop="roomNumber">
          <el-input
            v-model="roomForm.roomNumber"
            placeholder="如：101、A201等"
            class="form-input"
          />
        </el-form-item>
        
        <el-form-item label="房间类型" prop="roomType">
          <el-select
            v-model="roomForm.roomType"
            placeholder="选择房间类型"
            class="form-select"
            @change="handleRoomTypeChange"
          >
            <el-option label="单人间" value="单人间" />
            <el-option label="双人间" value="双人间" />
            <el-option label="三人间" value="三人间" />
            <el-option label="VIP套房" value="VIP套房" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="床位总数" prop="totalBeds">
          <el-input-number
            v-model="roomForm.totalBeds"
            :min="1"
            :max="10"
            controls-position="right"
            class="form-number"
          />
        </el-form-item>
        
        <el-form-item label="初始状态" prop="status">
          <el-select
            v-model="roomForm.status"
            placeholder="选择房间状态"
            class="form-select"
            disabled
          >
            <el-option label="空闲" :value="0" />
            <el-option label="已满" :value="1" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="房间描述" prop="description">
          <el-input
            v-model="roomForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入房间描述信息..."
            class="form-textarea"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit"
            class="submit-btn"
            :loading="submitting"
          >
            {{ isEdit ? '更新房间' : '创建房间' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 设为维修对话框 -->
    <el-dialog
      v-model="repairDialogVisible"
      title="设置房间为维修状态"
      width="500px"
      class="repair-dialog"
      @close="handleRepairDialogClose"
    >
      <el-form
        ref="repairFormRef"
        :model="repairForm"
        :rules="repairRules"
        label-width="100px"
        class="repair-form"
      >
        <el-form-item label="维修原因" prop="repairReason">
          <el-input
            v-model="repairForm.repairReason"
            placeholder="请输入维修原因..."
            class="form-input"
          />
        </el-form-item>
        
        <el-form-item label="预计完成" prop="repairEndDate">
          <el-date-picker
            v-model="repairForm.repairEndDate"
            type="date"
            placeholder="选择预计完成日期"
            class="form-datepicker"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="维修备注" prop="repairNotes">
          <el-input
            v-model="repairForm.repairNotes"
            type="textarea"
            :rows="3"
            placeholder="请输入维修备注..."
            class="form-textarea"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="repairDialogVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleSubmitRepair"
            class="submit-btn"
          >
            确认维修
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  User,
  Tools,
  Setting,
  Search,
  Plus,
  Refresh,
  Star,
  Document,
  Edit,
  Delete,
  More,
  Check,
  OfficeBuilding,
  Warning,
  PieChart
} from '@element-plus/icons-vue'
import * as roomApi from '@/api/room'
import * as bedApi from '@/api/bed'

const router = useRouter()

// 数据
const roomList = ref([])
const loading = ref(false)
const submitting = ref(false)
const searchParams = reactive({
  roomNumber: '',
  roomType: '',
  status: null
})

// 统计信息
const stats = reactive({
  totalRooms: 0,
  availableRooms: 0,
  repairRooms: 0
})

// 表单相关
const dialogVisible = ref(false)
const repairDialogVisible = ref(false)
const isEdit = ref(false)
const currentRoomId = ref(null)
const roomFormRef = ref()
const repairFormRef = ref()

const roomForm = reactive({
  roomNumber: '',
  roomType: '',
  totalBeds: 1,
  status: 0,
  description: ''
})

const repairForm = reactive({
  repairReason: '',
  repairEndDate: '',
  repairNotes: ''
})

// 表单验证规则
const roomRules = {
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' },
    { min: 1, max: 10, message: '房间号长度在1到10个字符', trigger: 'blur' }
  ],
  roomType: [
    { required: true, message: '请选择房间类型', trigger: 'change' }
  ],
  totalBeds: [
    { required: true, message: '请输入床位总数', trigger: 'blur' }
  ]
}

const repairRules = {
  repairReason: [
    { required: true, message: '请输入维修原因', trigger: 'blur' }
  ]
}

// 生命周期
onMounted(() => {
  fetchRooms()
})

const fetchRooms = async () => {
    loading.value = true
    try {
      console.log('正在获取房间列表，搜索条件:', searchParams)
      const response = await roomApi.searchRooms(searchParams)
      console.log('房间列表响应:', response)
      
      if (response.data) {
        // 适配后端返回的数据结构，将id映射为roomId，bedCount映射为totalBeds
        const rooms = response.data.data.map(room => ({
          roomId: room.id,
          roomNumber: room.roomNumber,
          roomType: room.roomType,
          totalBeds: room.bedCount,
          status: room.status,
          description: room.description,
          repairReason: room.repairReason || '',
          repairStartDate: room.repairStartDate || '',
          repairEndDate: room.repairEndDate || ''
        })) || []
        
        // 为每个房间获取床位信息，然后更新房间状态
        const roomsWithUpdatedStatus = await Promise.all(rooms.map(async (room) => {
          // 如果房间状态是维修中，保持不变
          if (room.status === 2) {
            return room;
          }
          
          try {
            // 获取房间的床位列表
            const bedResponse = await bedApi.getBedsByRoomId(room.roomId);
            if (bedResponse.data && bedResponse.data.data) {
              const beds = bedResponse.data.data;
              
              // 计算已占用的床位数，直接使用residentId判断，更准确
              const occupiedBeds = beds.filter(bed => bed.residentId !== null).length;
              
              // 如果已占用床位数等于总床位数，状态设为已满
              // 否则设为空闲
              const actualStatus = occupiedBeds === room.totalBeds ? 1 : 0;
              
              return {
                ...room,
                status: actualStatus
              };
            }
          } catch (bedError) {
            console.error(`获取房间${room.roomNumber}的床位信息失败:`, bedError);
          }
          
          return room;
        }));
        
        roomList.value = roomsWithUpdatedStatus;
        
        // 更新统计信息
        updateStats()
        console.log('获取到的房间数据:', roomList.value)
      } else {
        ElMessage.error('获取房间列表失败')
      }
    } catch (error) {
      console.error('获取房间列表失败:', error)
      ElMessage.error('获取房间列表失败，请检查网络连接')
    } finally {
      loading.value = false
    }
  }

const updateStats = () => {
  stats.totalRooms = roomList.value.length
  stats.availableRooms = roomList.value.filter(room => room.status === 0).length
  stats.repairRooms = roomList.value.filter(room => room.status === 2).length
}

const handleSearch = () => {
  fetchRooms()
}

const handleReset = () => {
  Object.keys(searchParams).forEach(key => {
    searchParams[key] = ''
  })
  fetchRooms()
}

const handleCardClick = (roomId) => {
  console.log('点击房间ID:', roomId, '正在跳转到床位管理页面...')
  router.push({ 
    name: 'BedList', 
    params: { roomId: roomId },
    query: { fromRoom: true }
  })
}

const handleAddRoom = () => {
  isEdit.value = false
  Object.keys(roomForm).forEach(key => {
    if (key === 'totalBeds') {
      roomForm[key] = 1
    } else if (key === 'status') {
      roomForm[key] = 0
    } else {
      roomForm[key] = ''
    }
  })
  dialogVisible.value = true
}

const handleEditRoom = (room) => {
  isEdit.value = true
  currentRoomId.value = room.roomId
  Object.assign(roomForm, room)
  dialogVisible.value = true
}

// 选择房间类型后自动设置床位数量
const handleRoomTypeChange = (roomType) => {
  // 根据房间类型自动设置床位数量
  switch (roomType) {
    case '单人间':
      roomForm.totalBeds = 1
      break
    case '双人间':
      roomForm.totalBeds = 2
      break
    case '三人间':
      roomForm.totalBeds = 3
      break
    case 'VIP套房':
      roomForm.totalBeds = 1
      break
    default:
      roomForm.totalBeds = 1
  }
}

const handleSubmit = async () => {
    try {
      await roomFormRef.value.validate()
      submitting.value = true
      
      // 准备提交数据，适配后端字段名
      const submitData = {
        roomNumber: roomForm.roomNumber,
        roomType: roomForm.roomType,
        bedCount: roomForm.totalBeds,
        status: 0, // 固定为空闲状态
        description: roomForm.description
      }
      
      if (isEdit.value) {
        submitData.id = currentRoomId.value
        const response = await roomApi.updateRoom(submitData)
        if (response.status === 200) {
          ElMessage.success({
            message: '房间信息更新成功',
            type: 'success',
            showClose: true
          })
          dialogVisible.value = false
          fetchRooms()
        } else {
          ElMessage.error('更新失败')
        }
      } else {
        const response = await roomApi.addRoom(submitData)
        if (response.status === 200) {
          ElMessage.success({
            message: '房间创建成功',
            type: 'success',
            showClose: true
          })
          dialogVisible.value = false
          fetchRooms()
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

const handleDeleteRoom = async (roomId) => {
    try {
      await ElMessageBox.confirm('请选择删除方式：', '确认删除', {
        type: 'warning',
        confirmButtonText: '删除信息（保留床位）',
        cancelButtonText: '删除床位',
        distinguishCancelAndClose: true,
        cancelButtonClass: 'confirm-delete-btn'
      })
      
      // 用户点击"删除信息（保留床位）"
      const response = await roomApi.deleteRoomInfo(roomId)
      if (response.status === 200) {
        ElMessage.success({
          message: '房间信息删除成功，床位已保留',
          type: 'success',
          showClose: true
        })
        fetchRooms()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      if (error === 'cancel') {
        // 用户点击"删除床位"
        try {
          await ElMessageBox.confirm('确定要删除这个房间及所有床位吗？此操作不可撤销。', '确认删除', {
            type: 'danger',
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            confirmButtonClass: 'confirm-delete-btn',
            cancelButtonClass: 'cancel-delete-btn'
          })
          
          const response = await roomApi.deleteRoom(roomId)
          if (response.status === 200) {
            ElMessage.success({
              message: '房间删除成功',
              type: 'success',
              showClose: true
            })
            fetchRooms()
          } else {
            ElMessage.error('删除失败')
          }
        } catch (innerError) {
          if (innerError !== 'cancel') {
            console.error('删除失败:', innerError)
          }
        }
      }
    }
}

const handleSetRepair = (room) => {
  currentRoomId.value = room.roomId
  Object.keys(repairForm).forEach(key => {
    repairForm[key] = ''
  })
  repairDialogVisible.value = true
}

const handleSubmitRepair = async () => {
    try {
      await repairFormRef.value.validate()
      
      const response = await roomApi.setRoomRepair(currentRoomId.value, repairForm)
      if (response.status === 200) {
        ElMessage.success({
          message: '房间已设置为维修状态',
          type: 'success',
          showClose: true
        })
        repairDialogVisible.value = false
        fetchRooms()
      } else {
        ElMessage.error('操作失败')
      }
    } catch (error) {
      console.error('设置维修失败:', error)
      ElMessage.error('操作失败，请重试')
    }
}

const handleCompleteRepair = async (roomId) => {
    try {
      await ElMessageBox.confirm('确定要完成维修吗？系统将根据实际入住情况更新房间状态。', '确认完成维修', {
        type: 'warning',
        confirmButtonText: '完成维修',
        cancelButtonText: '取消'
      })
      
      const response = await roomApi.completeRepair(roomId)
      if (response.status === 200) {
        ElMessage.success({
          message: '维修完成，房间状态已根据实际入住情况更新',
          type: 'success',
          showClose: true
        })
        fetchRooms()
      } else {
        ElMessage.error('操作失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('完成维修失败:', error)
      }
    }
}

const handleDialogClose = () => {
  if (roomFormRef.value) {
    roomFormRef.value.resetFields()
  }
}

const handleRepairDialogClose = () => {
  if (repairFormRef.value) {
    repairFormRef.value.resetFields()
  }
}

// 工具方法
const getStatusText = (status) => {
  const map = { 0: '空闲', 1: '已满', 2: '维修中' }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = { 0: 'success', 1: 'info', 2: 'warning' }
  return map[status] || 'info'
}

const getStatusTagClass = (status) => {
  const map = { 0: 'tag-available', 1: 'tag-occupied', 2: 'tag-repair' }
  return map[status] || 'tag-occupied'
}

const getRoomCardClass = (status) => {
  const map = { 0: 'card-available', 1: 'card-occupied', 2: 'card-repair' }
  return map[status] || 'card-occupied'
}

const formatDate = (date) => {
  if (!date) return '未设置'
  try {
    return new Date(date).toLocaleDateString()
  } catch (e) {
    return date
  }
}
</script>

<style scoped>
/* 全局容器样式 */
.room-list-container {
  width: 100%;
  height: 100%;
  background: var(--bg-primary);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 操作栏 */
.action-bar {
  background: white;
  border-radius: var(--border-radius);
  padding: 24px;
  margin-bottom: 30px;
  border: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 300px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
  box-shadow: none;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 1px var(--primary-color);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  width: 140px;
}

.filter-select :deep(.el-select__wrapper) {
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
  box-shadow: none;
}

.reset-btn {
  border-radius: var(--border-radius);
  padding: 10px 20px;
}

.add-btn {
  background: var(--primary-color);
  border: none;
  border-radius: var(--border-radius);
  padding: 12px 28px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background: var(--primary-dark);
  transform: none;
  box-shadow: none;
}

/* 房间网格 */
.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

/* 添加房间卡片 */
.add-room-card {
  background: white;
  border: 2px dashed var(--primary-light);
  border-radius: var(--border-radius);
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 240px;
}

.add-room-card:hover {
  border-color: var(--primary-color);
  transform: none;
  box-shadow: none;
}

.add-icon {
  width: 64px;
  height: 64px;
  background: var(--primary-lighter);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.add-icon .el-icon {
  font-size: 28px;
  color: var(--primary-color);
}

.add-text {
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 16px;
}

/* 房间卡片 */
.room-card {
  background: white;
  border-radius: var(--border-radius);
  padding: 24px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  min-height: 240px;
  position: relative;
  overflow: hidden;
}

.room-card:hover {
  transform: none;
  box-shadow: none;
  border-color: var(--primary-light);
}

.room-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-light);
  border-radius: var(--border-radius) var(--border-radius) 0 0;
}

.card-available::before {
  background: var(--success-color);
}

.card-occupied::before {
  background: var(--warning-color);
}

.card-repair::before {
  background: var(--danger-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.room-number {
  display: flex;
  align-items: center;
  gap: 12px;
}

.room-icon {
  width: 48px;
  height: 48px;
  background: var(--bg-hover);
  border-radius: var(--border-radius);
  display: flex;
  align-items: center;
  justify-content: center;
}

.room-icon .el-icon {
  font-size: 24px;
  color: var(--primary-color);
}

.room-number h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.status-tag {
  border: none !important;
  font-weight: 600 !important;
  border-radius: var(--border-radius) !important;
  padding: 6px 16px !important;
}

.tag-available {
  background: rgba(107, 142, 122, 0.1) !important;
  color: var(--success-color) !important;
}

.tag-occupied {
  background: rgba(194, 168, 107, 0.1) !important;
  color: var(--warning-color) !important;
}

.tag-repair {
  background: rgba(179, 106, 106, 0.1) !important;
  color: var(--danger-color) !important;
}

.card-content {
  flex: 1;
}

.room-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-secondary);
}

.info-item .el-icon {
  font-size: 16px;
  color: var(--primary-color);
}

.repair-section {
  margin-top: 20px;
  padding: 16px;
  background: rgba(179, 106, 106, 0.05);
  border-radius: var(--border-radius);
  border-left: 3px solid var(--danger-color);
}

.repair-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: var(--danger-color);
  font-weight: 600;
}

.repair-details {
  font-size: 13px;
  color: var(--text-secondary);
}

.repair-reason {
  margin: 0 0 8px 0;
  font-weight: 500;
}

.repair-time {
  font-size: 12px;
  color: var(--text-muted);
}

.card-footer {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-btn {
  border-radius: var(--border-radius) !important;
  padding: 8px 16px !important;
  font-weight: 500 !important;
  border: none !important;
}

.repair-btn {
  background: rgba(179, 106, 106, 0.1) !important;
  color: var(--danger-color) !important;
}

.repair-btn:hover {
  background: rgba(179, 106, 106, 0.2) !important;
}

.complete-btn {
  background: rgba(107, 142, 122, 0.1) !important;
  color: var(--success-color) !important;
}

.complete-btn:hover {
  background: rgba(107, 142, 122, 0.2) !important;
}

.more-btn {
  background: rgba(91, 124, 138, 0.1) !important;
  color: var(--primary-color) !important;
  padding: 8px !important;
}

.more-dropdown :deep(.el-dropdown-menu) {
  border-radius: 12px;
  padding: 8px;
}

.delete-option {
  color: var(--danger-color) !important;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
}

.empty-illustration {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 24px;
  color: var(--primary-light);
}

.empty-state h3 {
  margin: 0 0 12px 0;
  color: var(--text-primary);
  font-size: 24px;
  font-weight: 600;
}

.empty-state p {
  color: var(--text-muted);
  margin-bottom: 24px;
  font-size: 16px;
}

.empty-btn {
  border-radius: var(--border-radius);
  padding: 12px 32px;
  font-weight: 600;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid var(--primary-lighter);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  margin: 0 auto 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  color: var(--text-muted);
  font-size: 16px;
}

/* 对话框样式 */
.room-dialog,
.repair-dialog {
  border-radius: var(--border-radius) !important;
}

.room-dialog :deep(.el-dialog__header),
.repair-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 0 !important;
  margin: 0 !important;
}

.room-dialog :deep(.el-dialog__title),
.repair-dialog :deep(.el-dialog__title) {
  font-size: 20px !important;
  font-weight: 600 !important;
  color: var(--text-primary) !important;
}

.room-dialog :deep(.el-dialog__body),
.repair-dialog :deep(.el-dialog__body) {
  padding: 20px 24px !important;
}

.room-form,
.repair-form {
  padding: 0 10px;
}

.form-input :deep(.el-input__wrapper),
.form-select :deep(.el-select__wrapper),
.form-datepicker :deep(.el-input__wrapper) {
  border-radius: var(--border-radius) !important;
  border: 1px solid var(--border-color) !important;
  box-shadow: none !important;
  transition: all 0.3s ease;
}

.form-input :deep(.el-input__wrapper:hover),
.form-select :deep(.el-select__wrapper:hover),
.form-datepicker :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light) !important;
}

.form-input :deep(.el-input__wrapper.is-focus),
.form-select :deep(.el-select__wrapper.is-focus),
.form-datepicker :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 1px var(--primary-color) !important;
}

.form-number :deep(.el-input-number__decrease),
.form-number :deep(.el-input-number__increase) {
  background: var(--bg-hover) !important;
  border: none !important;
  color: var(--primary-color) !important;
}

.form-textarea :deep(.el-textarea__inner) {
  border-radius: var(--border-radius) !important;
  border: 1px solid var(--border-color) !important;
  box-shadow: none !important;
  transition: all 0.3s ease;
  font-family: inherit !important;
}

.form-textarea :deep(.el-textarea__inner:hover) {
  border-color: var(--primary-light) !important;
}

.form-textarea :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 1px var(--primary-color) !important;
}

.dialog-footer {
  padding: 0 24px 24px !important;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  border-radius: var(--border-radius) !important;
  padding: 10px 24px !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-secondary) !important;
  background: white !important;
}

.submit-btn {
  border-radius: var(--border-radius) !important;
  padding: 10px 32px !important;
  background: var(--primary-color) !important;
  border: none !important;
  font-weight: 600 !important;
  transition: all 0.2s ease !important;
}

.submit-btn:hover {
  background: var(--primary-dark) !important;
  transform: none !important;
  box-shadow: none !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .rooms-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
  }
  
  .search-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
  
  .filter-group {
    flex-wrap: wrap;
  }
  
  .rooms-grid {
    grid-template-columns: 1fr;
  }
}
</style>