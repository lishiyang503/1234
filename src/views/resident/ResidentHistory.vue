<template>
  <div class="resident-history-container">
    <div class="page-header">
      <h2 class="page-title">入住历史</h2>
      <p class="page-subtitle">查询老人的入住历史记录</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">入住历史查询</h3>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.name"
              placeholder="按姓名搜索"
              prefix-icon="User"
              clearable
              class="search-input"
            />
            <el-input
              v-model="searchParams.bedNumber"
              placeholder="按床位搜索"
              prefix-icon="Location"
              clearable
              class="search-input"
            />
            <el-date-picker
              v-model="searchParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="date-picker"
            />
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          
          <!-- 入住历史表格 -->
          <div class="table-container">
            <el-table
              :data="residentHistoryList"
              border
              stripe
              style="width: 100%"
              :row-class-name="tableRowClassName"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="residentName" label="老人姓名" width="150" />
              <el-table-column prop="roomNumber" label="房间号" width="120" align="center" />
              <el-table-column prop="bedNumber" label="床位号" width="120" align="center" />
              <el-table-column prop="entryDate" label="入住日期" width="150" align="center" />
              <el-table-column prop="exitDate" label="退房日期" width="150" align="center" />
              <el-table-column prop="duration" label="入住时长" width="120" align="center">
                <template #default="scope">
                  {{ scope.row.duration }}天
                </template>
              </el-table-column>
              <el-table-column prop="reason" label="退房原因" min-width="200" />
              <el-table-column prop="operator" label="操作人" width="120" align="center" />
              <el-table-column prop="createTime" label="记录时间" width="180" align="center" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getResidentHistory } from '@/api/resident'

// 搜索和筛选参数
const searchParams = reactive({
  name: '',
  bedNumber: '',
  dateRange: []
})

// 表格数据
const residentHistoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 生命周期
onMounted(() => {
  fetchResidentHistory()
})

// 获取入住历史列表
const fetchResidentHistory = async () => {
  loading.value = true
  try {
    const response = await getResidentHistory({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.data.success) {
      residentHistoryList.value = response.data.data.list || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取入住历史列表失败')
    }
  } catch (error) {
    console.error('获取入住历史列表失败:', error)
    ElMessage.error('获取入住历史列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResidentHistory()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchResidentHistory()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}
</script>

<style scoped>
.resident-history-container {
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
  align-items: center;
}

.search-input {
  width: 250px;
}

.date-picker {
  width: 350px;
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