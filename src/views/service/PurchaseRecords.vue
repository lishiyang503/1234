<template>
  <div class="purchase-records-container">
    <div class="page-header">
      <h2 class="page-title">服务管理</h2>
      <p class="page-subtitle">查看服务购买记录</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">服务购买记录</h3>
        </div>
        
        <div class="card-body">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-input
              v-model="searchParams.residentName"
              placeholder="搜索老人姓名"
              prefix-icon="User"
              clearable
              class="search-input"
            />
            <el-select
              v-model="searchParams.serviceLevel"
              placeholder="筛选套餐级别"
              clearable
              class="filter-select"
            >
              <el-option label="基础套餐" value="基础" />
              <el-option label="中级套餐" value="中级" />
              <el-option label="高级套餐" value="高级" />
            </el-select>
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
          
          <!-- 购买记录表格 -->
          <div class="table-container">
            <el-table
              :data="purchaseRecordsList"
              border
              stripe
              style="width: 100%"
              :row-class-name="tableRowClassName"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="residentName" label="老人姓名" width="150" />
              <el-table-column prop="roomNumber" label="房间号" width="120" align="center" />
              <el-table-column prop="bedNumber" label="床位号" width="120" align="center" />
              <el-table-column prop="serviceName" label="服务套餐" width="200" />
              <el-table-column prop="serviceLevel" label="套餐级别" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="getLevelType(scope.row.serviceLevel)">
                    {{ scope.row.serviceLevel }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="购买金额(元)" width="150" align="center">
                <template #default="scope">
                  <span class="price">{{ scope.row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="purchaseDate" label="购买日期" width="150" align="center" />
              <el-table-column prop="expireDate" label="到期日期" width="150" align="center" />
              <el-table-column prop="operator" label="操作人" width="120" align="center" />
              <el-table-column prop="status" label="状态" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '有效' ? 'success' : 'warning'">
                    {{ scope.row.status }}
                  </el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import { getPurchaseRecords } from '@/api/service'

// 搜索和筛选参数
const searchParams = reactive({
  residentName: '',
  serviceLevel: '',
  dateRange: []
})

// 表格数据
const purchaseRecordsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 生命周期
onMounted(() => {
  fetchPurchaseRecords()
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

// 获取购买记录列表
const fetchPurchaseRecords = async () => {
  loading.value = true
  try {
    const response = await getPurchaseRecords({
      ...searchParams,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.data.success) {
      purchaseRecordsList.value = response.data.data.list || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取服务购买记录列表失败')
    }
  } catch (error) {
    console.error('获取服务购买记录列表失败:', error)
    ElMessage.error('获取服务购买记录列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchPurchaseRecords()
}

// 分页
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchPurchaseRecords()
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}
</script>

<style scoped>
.purchase-records-container {
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

.filter-select {
  width: 200px;
}

.date-picker {
  width: 350px;
}

.table-container {
  margin-bottom: 24px;
  overflow-x: auto;
}

.price {
  font-weight: 600;
  color: var(--danger-color);
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