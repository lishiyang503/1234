<template>
  <div class="elder-nursing-records">
    <div class="page-header">
      <h2>护理记录管理</h2>
      <p>查看您的护理服务执行记录</p>
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
              <el-form-item label="护理类型">
                <el-select v-model="filterForm.nursingType" placeholder="选择护理类型" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="生活护理" value="LIFE" />
                  <el-option label="医疗护理" value="MEDICAL" />
                  <el-option label="康复护理" value="REHAB" />
                  <el-option label="心理护理" value="PSYCHO" />
                </el-select>
              </el-form-item>
              <el-form-item label="护理状态">
                <el-select v-model="filterForm.status" placeholder="选择护理状态" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="待执行" value="PENDING" />
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
            <span>护理统计</span>
          </div>
        </template>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ totalRecords }}</div>
            <div class="stat-label">总护理次数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ completedRecords }}</div>
            <div class="stat-label">已完成护理</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ pendingRecords }}</div>
            <div class="stat-label">待执行护理</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ averageDuration }}</div>
            <div class="stat-label">平均时长(分钟)</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 护理记录列表 -->
    <div class="records-section">
      <el-card class="records-card">
        <template #header>
          <div class="card-header">
            <el-icon><Document /></el-icon>
            <span>护理记录列表</span>
          </div>
        </template>
        <div class="records-table">
          <el-table
            :data="filteredRecords"
            style="width: 100%"
            @row-click="viewRecordDetails"
          >
            <el-table-column prop="recordTime" label="护理时间" width="180" />
            <el-table-column prop="contentName" label="护理内容" min-width="150" />
            <el-table-column prop="nurseName" label="护理人员" width="120" />
            <el-table-column prop="duration" label="实际时长(分钟)" width="120" />
            <el-table-column label="护理状态" width="100">
              <template #default="scope">
                <el-tag :type="getTagType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button size="small" @click.stop="viewRecordDetails(scope.row)">
                  <el-icon><View /></el-icon>
                  详情
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
    
    <!-- 护理记录详情 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedRecord ? '护理记录详情' : '护理记录'
"
      width="70%"
    >
      <div v-if="selectedRecord" class="record-details">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">护理内容：</span>
              <span class="detail-value">{{ selectedRecord.contentName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">护理时间：</span>
              <span class="detail-value">{{ selectedRecord.recordTime }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">护理人员：</span>
              <span class="detail-value">{{ selectedRecord.nurseName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">护理状态：</span>
              <span class="detail-value status" :class="selectedRecord.status === 'COMPLETED' ? 'status-completed' : 'status-pending'">
                {{ getStatusText(selectedRecord.status) }}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">预计时长：</span>
              <span class="detail-value">{{ selectedRecord.estimatedDuration }} 分钟</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">实际时长：</span>
              <span class="detail-value">{{ selectedRecord.duration }} 分钟</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedRecord.description">
          <h4>护理描述</h4>
          <p class="detail-description">{{ selectedRecord.description }}</p>
        </div>
        
        <div class="detail-section" v-if="selectedRecord.notes">
          <h4>护理备注</h4>
          <p class="detail-notes">{{ selectedRecord.notes }}</p>
        </div>
        
        <div class="detail-section" v-if="selectedRecord.nurseEvaluation">
          <h4>护理人员评价</h4>
          <div class="evaluation-card">
            <div class="evaluation-header">
              <span class="evaluation-nurse">{{ selectedRecord.nurseName }}</span>
              <span class="evaluation-date">{{ selectedRecord.evaluationDate }}</span>
            </div>
            <div class="evaluation-content">{{ selectedRecord.nurseEvaluation }}</div>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedRecord.serviceRating">
          <h4>服务评价</h4>
          <div class="rating-card">
            <div class="rating-header">
              <span class="rating-title">服务满意度</span>
              <div class="rating-stars">
                <el-rate v-model="selectedRecord.serviceRating" disabled show-score text-color="#ff9900" />
              </div>
            </div>
            <div class="rating-content" v-if="selectedRecord.serviceComment">
              {{ selectedRecord.serviceComment }}
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
import { ElMessage } from 'element-plus'
import {
  Filter,
  DataAnalysis,
  Document,
  Search,
  Refresh,
  View
} from '@element-plus/icons-vue'

const filterForm = ref({
  dateRange: null,
  nursingType: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const selectedRecord = ref(null)

const nursingRecords = ref([
  {
    id: 1,
    contentName: '个人卫生护理',
    recordTime: '2024-01-07 08:30',
    nurseName: '张护士',
    estimatedDuration: 30,
    duration: 35,
    status: 'COMPLETED',
    description: '完成了洗脸、刷牙、梳头、剃须、洗脚等个人卫生护理服务，老人配合度良好。',
    notes: '老人皮肤较为干燥，已使用保湿乳液。',
    nurseEvaluation: '老人状态良好，配合度高，护理过程顺利。',
    evaluationDate: '2024-01-07',
    serviceRating: 5,
    serviceComment: '护理人员服务态度很好，专业技能强，非常满意。'
  },
  {
    id: 2,
    contentName: '康复训练',
    recordTime: '2024-01-07 09:30',
    nurseName: '李康复师',
    estimatedDuration: 60,
    duration: 55,
    status: 'COMPLETED',
    description: '进行了肢体功能训练、关节活动、肌力训练等康复护理服务。',
    notes: '老人左侧肢体活动度有所改善，继续加强训练。',
    nurseEvaluation: '老人训练积极性高，进步明显，建议继续保持。',
    evaluationDate: '2024-01-07',
    serviceRating: 5,
    serviceComment: '康复师专业水平高，训练计划合理，效果明显。'
  },
  {
    id: 3,
    contentName: '药物护理',
    recordTime: '2024-01-07 15:00',
    nurseName: '王护士',
    estimatedDuration: 20,
    duration: 20,
    status: 'COMPLETED',
    description: '协助老人服用下午药物，包括降压药和降糖药。',
    notes: '老人按时服药，无不良反应。',
    nurseEvaluation: '老人服药依从性好，药物管理规范。',
    evaluationDate: '2024-01-07'
  },
  {
    id: 4,
    contentName: '饮食护理',
    recordTime: '2024-01-07 18:00',
    nurseName: '张护士',
    estimatedDuration: 45,
    duration: 40,
    status: 'COMPLETED',
    description: '协助老人晚餐进食、饮水，老人食欲良好。',
    notes: '老人晚餐进食量正常，无不适。',
    nurseEvaluation: '老人饮食情况良好，营养摄入均衡。',
    evaluationDate: '2024-01-07'
  },
  {
    id: 5,
    contentName: '心理疏导',
    recordTime: '2024-01-06 14:00',
    nurseName: '刘心理咨询师',
    estimatedDuration: 40,
    duration: 45,
    status: 'COMPLETED',
    description: '与老人进行心理沟通，情绪安抚，心理支持等心理护理服务。',
    notes: '老人近期因思念家人情绪低落，已进行安抚。',
    nurseEvaluation: '老人情绪有所好转，建议多安排家属探视。',
    evaluationDate: '2024-01-06',
    serviceRating: 4,
    serviceComment: '心理咨询师很专业，耐心倾听，给予了很好的建议。'
  },
  {
    id: 6,
    contentName: '个人卫生护理',
    recordTime: '2024-01-06 08:30',
    nurseName: '张护士',
    estimatedDuration: 30,
    duration: 32,
    status: 'COMPLETED',
    description: '完成了洗脸、刷牙、梳头、剃须、洗脚等个人卫生护理服务。',
    notes: '老人睡眠质量良好，精神状态佳。',
    nurseEvaluation: '老人状态良好，护理过程顺利。',
    evaluationDate: '2024-01-06'
  },
  {
    id: 7,
    contentName: '康复训练',
    recordTime: '2024-01-06 09:30',
    nurseName: '李康复师',
    estimatedDuration: 60,
    duration: 58,
    status: 'COMPLETED',
    description: '进行了肢体功能训练、关节活动、肌力训练等康复护理服务。',
    notes: '老人训练效果良好，建议增加训练强度。',
    nurseEvaluation: '老人训练积极性高，进步明显。',
    evaluationDate: '2024-01-06'
  },
  {
    id: 8,
    contentName: '药物护理',
    recordTime: '2024-01-08 08:00',
    nurseName: '王护士',
    estimatedDuration: 20,
    duration: 0,
    status: 'PENDING',
    description: '协助老人服用早晨药物，包括降压药和降糖药。',
    notes: ''
  }
])

const totalRecords = computed(() => nursingRecords.value.length)
const completedRecords = computed(() => nursingRecords.value.filter(record => record.status === 'COMPLETED').length)
const pendingRecords = computed(() => nursingRecords.value.filter(record => record.status === 'PENDING').length)
const averageDuration = computed(() => {
  const completed = nursingRecords.value.filter(record => record.status === 'COMPLETED')
  if (completed.length === 0) return 0
  const total = completed.reduce((sum, record) => sum + record.duration, 0)
  return Math.round(total / completed.length)
})

const filteredRecords = computed(() => {
  let filtered = nursingRecords.value
  
  // 这里可以根据筛选条件进行过滤
  // 暂时返回所有记录
  
  return filtered
})

const total = computed(() => filteredRecords.value.length)

const handleFilter = () => {
  ElMessage.info('执行查询')
  // 这里可以根据筛选条件调用API获取数据
}

const resetFilter = () => {
  filterForm.value = {
    dateRange: null,
    nursingType: '',
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

const viewRecordDetails = (record) => {
  selectedRecord.value = record
  dialogVisible.value = true
}

const getTagType = (status) => {
  switch (status) {
    case 'COMPLETED':
      return 'success'
    case 'PENDING':
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
    case 'PENDING':
      return '待执行'
    case 'CANCELLED':
      return '已取消'
    default:
      return '未知'
  }
}

onMounted(() => {
  // 初始化加载数据
  console.log('Nursing records page mounted')
})
</script>

<style scoped>
.elder-nursing-records {
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

.pagination {
  margin-top: 20px;
  text-align: right;
}

.record-details {
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

.status-pending {
  background-color: #ecf5ff;
  color: #409eff;
}

.detail-description {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.detail-notes {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.evaluation-card {
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.evaluation-nurse {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.evaluation-date {
  font-size: 12px;
  color: var(--text-secondary);
}

.evaluation-content {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
}

.rating-card {
  padding: 16px;
  background-color: var(--bg-primary);
  border-radius: var(--border-radius);
}

.rating-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.rating-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.rating-content {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .elder-nursing-records {
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
  
  .evaluation-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .rating-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>