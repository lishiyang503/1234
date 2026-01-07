<template>
  <div class="elder-purchase-records">
    <div class="page-header">
      <h2>购买记录管理</h2>
      <p>查看您的服务购买记录</p>
    </div>
    
    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-card class="filter-card">
        <template #header>
          <div class="card-header">
            <el-icon><Filter /></el-icon>
            <span>筛选条件</span>
          </div>
        </template>
        <div class="filter-content">
          <div class="filter-row">
            <el-form :inline="true" :model="filterForm" class="filter-form">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="filterForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  style="width: 240px"
                />
              </el-form-item>
              <el-form-item label="服务类型">
                <el-select v-model="filterForm.serviceType" placeholder="选择服务类型" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="护理服务" value="NURSING" />
                  <el-option label="生活服务" value="LIFE" />
                  <el-option label="医疗服务" value="MEDICAL" />
                  <el-option label="康复服务" value="REHAB" />
                </el-select>
              </el-form-item>
              <el-form-item label="订单状态">
                <el-select v-model="filterForm.status" placeholder="选择订单状态" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="处理中" value="PROCESSING" />
                  <el-option label="已取消" value="CANCELLED" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleFilter">
                  <el-icon><Search /></el-icon>
                  查询
                </el-button>
                <el-button @click="resetFilter">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 统计信息 -->
    <div class="stats-section">
      <el-card class="stats-card">
        <template #header>
          <div class="card-header">
            <el-icon><DataAnalysis /></el-icon>
            <span>购买统计</span>
          </div>
        </template>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ totalOrders }}</div>
            <div class="stat-label">总订单数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ totalAmount }}</div>
            <div class="stat-label">总消费金额</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ completedOrders }}</div>
            <div class="stat-label">已完成订单</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ averageOrderAmount }}</div>
            <div class="stat-label">平均订单金额</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 购买记录列表 -->
    <div class="records-section">
      <el-card class="records-card">
        <template #header>
          <div class="card-header">
            <el-icon><Document /></el-icon>
            <span>购买记录列表</span>
          </div>
        </template>
        <div class="records-table">
          <el-table
            :data="filteredOrders"
            style="width: 100%"
            @row-click="viewOrderDetails"
          >
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="serviceName" label="服务名称" min-width="150" />
            <el-table-column prop="amount" label="订单金额" width="120">
              <template #default="scope">
                <span class="amount">¥{{ scope.row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="orderDate" label="下单日期" width="150" />
            <el-table-column prop="status" label="订单状态" width="100">
              <template #default="scope">
                <el-tag :type="getTagType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button size="small" @click.stop="viewOrderDetails(scope.row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button size="small" type="danger" @click.stop="cancelOrder(scope.row)" v-if="scope.row.status === 'PROCESSING'">
                  <el-icon><Close /></el-icon>
                  取消
                </el-button>
                <el-button size="small" type="info" @click.stop="printOrder(scope.row)" v-if="scope.row.status === 'COMPLETED'">
                  <el-icon><Printer /></el-icon>
                  打印
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 订单详情 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedOrder ? '订单详情' : '订单信息'"
      width="70%"
    >
      <div v-if="selectedOrder" class="order-details">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">订单号：</span>
              <span class="detail-value">{{ selectedOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务名称：</span>
              <span class="detail-value">{{ selectedOrder.serviceName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单金额：</span>
              <span class="detail-value amount">¥{{ selectedOrder.amount }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">下单日期：</span>
              <span class="detail-value">{{ selectedOrder.orderDate }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">支付日期：</span>
              <span class="detail-value">{{ selectedOrder.paymentDate || '未支付' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态：</span>
              <span class="detail-value status" :class="selectedOrder.status === 'COMPLETED' ? 'status-completed' : selectedOrder.status === 'PROCESSING' ? 'status-processing' : 'status-cancelled'">
                {{ getStatusText(selectedOrder.status) }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">支付方式：</span>
              <span class="detail-value">{{ selectedOrder.paymentMethod || '未支付' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">服务周期：</span>
              <span class="detail-value">{{ selectedOrder.serviceStartDate }} 至 {{ selectedOrder.serviceEndDate }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedOrder.serviceDetails">
          <h4>服务详情</h4>
          <div class="service-details">
            <p>{{ selectedOrder.serviceDetails }}</p>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedOrder.paymentDetails">
          <h4>支付详情</h4>
          <div class="payment-details">
            <div class="payment-item" v-for="(item, index) in selectedOrder.paymentDetails" :key="index">
              <span class="payment-label">{{ item.label }}：</span>
              <span class="payment-value">{{ item.value }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Filter,
  DataAnalysis,
  Document,
  Search,
  Refresh,
  View,
  Close,
  Printer
} from '@element-plus/icons-vue'

const filterForm = ref({
  dateRange: null,
  serviceType: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const selectedOrder = ref(null)

const purchaseOrders = ref([
  {
    id: 1,
    orderNo: 'ORD-20240101-001',
    serviceName: '基础生活照料套餐',
    serviceType: 'LIFE',
    amount: 800,
    orderDate: '2023-12-30',
    paymentDate: '2023-12-30',
    status: 'COMPLETED',
    paymentMethod: '支付宝',
    serviceStartDate: '2024-01-01',
    serviceEndDate: '2024-01-31',
    serviceDetails: '为老人提供基础的生活照料服务，包括日常起居、个人卫生、饮食协助等。',
    paymentDetails: [
      { label: '支付方式', value: '支付宝' },
      { label: '支付时间', value: '2023-12-30 10:30:00' },
      { label: '交易号', value: '2023123010300001' },
      { label: '支付状态', value: '支付成功' }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD-20231201-001',
    serviceName: '精神慰藉套餐',
    serviceType: 'PSYCHO',
    amount: 600,
    orderDate: '2023-11-28',
    paymentDate: '2023-11-28',
    status: 'COMPLETED',
    paymentMethod: '微信支付',
    serviceStartDate: '2023-12-01',
    serviceEndDate: '2023-12-31',
    serviceDetails: '为老人提供精神慰藉和心理支持服务，包括聊天陪伴、心理疏导等。',
    paymentDetails: [
      { label: '支付方式', value: '微信支付' },
      { label: '支付时间', value: '2023-11-28 14:20:00' },
      { label: '交易号', value: '2023112814200001' },
      { label: '支付状态', value: '支付成功' }
    ]
  },
  {
    id: 3,
    orderNo: 'ORD-20240107-001',
    serviceName: '专业医疗护理套餐',
    serviceType: 'MEDICAL',
    amount: 1500,
    orderDate: '2024-01-07',
    paymentDate: null,
    status: 'PROCESSING',
    paymentMethod: null,
    serviceStartDate: '2024-01-08',
    serviceEndDate: '2024-02-07',
    serviceDetails: '为需要医疗护理的老人提供专业的医疗护理服务，包括药物管理、伤口护理等。'
  },
  {
    id: 4,
    orderNo: 'ORD-20231101-001',
    serviceName: '康复理疗套餐',
    serviceType: 'REHAB',
    amount: 1200,
    orderDate: '2023-10-28',
    paymentDate: '2023-10-28',
    status: 'COMPLETED',
    paymentMethod: '银行转账',
    serviceStartDate: '2023-11-01',
    serviceEndDate: '2023-11-30',
    serviceDetails: '为需要康复理疗的老人提供专业的康复训练和理疗服务，帮助恢复身体功能。',
    paymentDetails: [
      { label: '支付方式', value: '银行转账' },
      { label: '支付时间', value: '2023-10-28 09:15:00' },
      { label: '交易号', value: '2023102809150001' },
      { label: '支付状态', value: '支付成功' }
    ]
  },
  {
    id: 5,
    orderNo: 'ORD-20231001-001',
    serviceName: '综合护理套餐',
    serviceType: 'NURSING',
    amount: 2500,
    orderDate: '2023-09-28',
    paymentDate: '2023-09-28',
    status: 'COMPLETED',
    paymentMethod: '支付宝',
    serviceStartDate: '2023-10-01',
    serviceEndDate: '2023-10-31',
    serviceDetails: '为老人提供全方位的综合护理服务，包括生活照料、医疗护理、康复理疗和精神慰藉。',
    paymentDetails: [
      { label: '支付方式', value: '支付宝' },
      { label: '支付时间', value: '2023-09-28 16:45:00' },
      { label: '交易号', value: '2023092816450001' },
      { label: '支付状态', value: '支付成功' }
    ]
  }
])

const totalOrders = computed(() => purchaseOrders.value.length)
const totalAmount = computed(() => {
  return purchaseOrders.value.reduce((sum, order) => sum + order.amount, 0)
})
const completedOrders = computed(() => {
  return purchaseOrders.value.filter(order => order.status === 'COMPLETED').length
})
const averageOrderAmount = computed(() => {
  if (purchaseOrders.value.length === 0) return 0
  return Math.round(totalAmount.value / purchaseOrders.value.length)
})

const filteredOrders = computed(() => {
  let filtered = purchaseOrders.value
  
  // 这里可以根据筛选条件进行过滤
  // 暂时返回所有订单
  
  return filtered
})

const total = computed(() => filteredOrders.value.length)

const handleFilter = () => {
  ElMessage.info('执行查询')
  // 这里可以根据筛选条件调用API获取数据
}

const resetFilter = () => {
  filterForm.value = {
    dateRange: null,
    serviceType: '',
    status: ''
  }
  ElMessage.info('筛选条件已重置')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (current) => {
  currentPage.value = current
}

const viewOrderDetails = (order) => {
  selectedOrder.value = order
  dialogVisible.value = true
}

const cancelOrder = (order) => {
  ElMessageBox.confirm(
    `确定要取消订单 ${order.orderNo} 吗？`,
    '取消订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('订单已取消')
    // 这里将来会调用API取消订单
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const printOrder = (order) => {
  ElMessage.info('打印订单功能开发中')
  // 这里将来会实现订单打印功能
}

const getTagType = (status) => {
  switch (status) {
    case 'COMPLETED':
      return 'success'
    case 'PROCESSING':
      return 'primary'
    case 'CANCELLED':
      return 'danger'
    default:
      return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'COMPLETED':
      return '已完成'
    case 'PROCESSING':
      return '处理中'
    case 'CANCELLED':
      return '已取消'
    default:
      return '未知状态'
  }
}

onMounted(() => {
  // 初始化加载数据
  console.log('Purchase records page mounted')
})
</script>

<style scoped>
.elder-purchase-records {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.filter-section {
  margin-bottom: 30px;
}

.filter-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.filter-form {
  width: 100%;
}

.stats-section {
  margin-bottom: 30px;
}

.stats-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.records-section {
  margin-bottom: 30px;
}

.records-card {
  border-radius: var(--border-radius);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.records-table {
  margin: 20px 0;
}

.amount {
  font-weight: 600;
  color: var(--primary-color);
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.order-details {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.detail-value {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.detail-value.amount {
  color: var(--primary-color);
}

.detail-value.status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
}

.status-completed {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-processing {
  background-color: #ecf5ff;
  color: #409eff;
}

.status-cancelled {
  background-color: #fef0f0;
  color: #f56c6c;
}

.service-details {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.payment-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.payment-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.payment-label {
  font-size: 14px;
  color: var(--text-secondary);
  min-width: 100px;
}

.payment-value {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

@media (max-width: 768px) {
  .elder-purchase-records {
    padding: 10px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-form .el-form-item {
    margin-bottom: 12px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .payment-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>