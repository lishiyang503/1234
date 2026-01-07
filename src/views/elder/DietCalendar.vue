<template>
  <div class="diet-calendar-container">
    <div class="page-header">
      <h2 class="page-title">膳食日历</h2>
      <p class="page-subtitle">查看月度膳食安排</p>
    </div>
    
    <div class="content-section">
      <div class="card">
        <div class="card-header">
          <div class="month-selector">
            <el-button @click="prevMonth" class="month-btn">
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
            <h3 class="current-month">{{ currentYear }}年{{ currentMonth }}月</h3>
            <el-button @click="nextMonth" class="month-btn">
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
        
        <div class="card-body">
          <!-- 日历头部 -->
          <div class="calendar-header">
            <div class="weekday" v-for="day in weekdays" :key="day">
              {{ day }}
            </div>
          </div>
          
          <!-- 日历主体 -->
          <div class="calendar-body">
            <!-- 上个月的日期 -->
            <div 
              v-for="prevDay in prevMonthDays" 
              :key="'prev-' + prevDay"
              class="calendar-day prev-month"
            >
              <span class="day-number">{{ prevDay }}</span>
            </div>
            
            <!-- 当前月的日期 -->
            <div 
              v-for="day in currentMonthDays" 
              :key="'current-' + day"
              class="calendar-day current-month"
              :class="{ today: isToday(day) }"
            >
              <span class="day-number">{{ day }}</span>
              <div class="diet-info" v-if="getDietInfo(day)">
                <div class="meal-item breakfast">
                  <span class="meal-label">早餐</span>
                  <span class="meal-content">{{ getDietInfo(day).breakfast || '无' }}</span>
                </div>
                <div class="meal-item lunch">
                  <span class="meal-label">午餐</span>
                  <span class="meal-content">{{ getDietInfo(day).lunch || '无' }}</span>
                </div>
                <div class="meal-item dinner">
                  <span class="meal-label">晚餐</span>
                  <span class="meal-content">{{ getDietInfo(day).dinner || '无' }}</span>
                </div>
              </div>
            </div>
            
            <!-- 下个月的日期 -->
            <div 
              v-for="nextDay in nextMonthDays" 
              :key="'next-' + nextDay"
              class="calendar-day next-month"
            >
              <span class="day-number">{{ nextDay }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, Calendar } from '@element-plus/icons-vue'

// 日历数据
const currentDate = ref(new Date())
const dietTemplateList = ref([])
const loading = ref(false)

// 计算属性
const currentYear = computed(() => currentDate.value.getFullYear())
const currentMonth = computed(() => currentDate.value.getMonth() + 1)
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

// 获取当月第一天是星期几
const firstDayOfMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value - 1, 1).getDay()
})

// 获取当月的天数
const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 0).getDate()
})

// 获取上个月需要显示的天数
const prevMonthDays = computed(() => {
  const days = []
  const prevMonthDaysCount = firstDayOfMonth.value
  const prevMonthLastDay = new Date(currentYear.value, currentMonth.value - 1, 0).getDate()
  
  for (let i = prevMonthDaysCount - 1; i >= 0; i--) {
    days.push(prevMonthLastDay - i)
  }
  
  return days
})

// 获取当月的天数数组
const currentMonthDays = computed(() => {
  const days = []
  for (let i = 1; i <= daysInMonth.value; i++) {
    days.push(i)
  }
  return days
})

// 获取下个月需要显示的天数
const nextMonthDays = computed(() => {
  const days = []
  const totalDays = prevMonthDays.value.length + currentMonthDays.value.length
  const remainingDays = 42 - totalDays // 6行7列 = 42个格子
  
  for (let i = 1; i <= remainingDays; i++) {
    days.push(i)
  }
  
  return days
})

// 检查是否是今天
const isToday = (day) => {
  const today = new Date()
  return today.getFullYear() === currentYear.value && 
         today.getMonth() + 1 === currentMonth.value && 
         today.getDate() === day
}

// 切换到上个月
const prevMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value - 2, 1)
  fetchDietTemplate()
}

// 切换到下个月
const nextMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value, 1)
  fetchDietTemplate()
}

// 获取膳食信息
const getDietInfo = (day) => {
  return dietTemplateList.value.find(item => item.day === day)
}

// 获取膳食模板数据
const fetchDietTemplate = () => {
  // 使用静态数据，为整个月的每一天添加详细的膳食信息
  dietTemplateList.value = [
    // 1号
    { 
      day: 1, 
      breakfast: '营养早餐：热牛奶200ml、全麦面包2片、水煮鸡蛋1个、新鲜草莓5颗', 
      lunch: '丰盛午餐：大米饭150g、红烧肉100g、清炒西兰花200g、番茄鸡蛋汤1碗', 
      dinner: '清淡晚餐：鸡蛋面条100g、西红柿鸡蛋卤、凉拌黄瓜100g、紫菜汤1碗'
    },
    // 2号
    { 
      day: 2, 
      breakfast: '传统早餐：豆浆300ml、油条1根、猪肉包子2个、小咸菜1份', 
      lunch: '经典午餐：大米饭150g、鱼香肉丝150g、家常豆腐100g、萝卜排骨汤1碗', 
      dinner: '北方风味：馒头100g、白菜炖粉条200g、炒时蔬100g、小米粥1碗'
    },
    // 3号
    { 
      day: 3, 
      breakfast: '中式早餐：小米粥200ml、咸鸭蛋1个、小笼包3个、榨菜1份', 
      lunch: '川菜风味：大米饭150g、宫保鸡丁150g、清炒黄瓜150g、酸辣汤1碗', 
      dinner: '鲁菜经典：大米饭150g、糖醋排骨120g、红烧茄子150g、菠菜汤1碗'
    },
    // 4号
    { 
      day: 4, 
      breakfast: '西式早餐：热牛奶200ml、燕麦粥100g、煎蛋1个、苹果1个', 
      lunch: '粤菜风味：大米饭150g、梅菜扣肉120g、白灼西兰花150g、老火靓汤1碗', 
      dinner: '面食精选：牛肉拉面150g、卤牛肉50g、凉拌豆芽100g、牛肉汤1碗'
    },
    // 5号
    { 
      day: 5, 
      breakfast: '煎饼早餐：豆浆300ml、杂粮煎饼1个、水煮鸡蛋1个、生菜叶2片', 
      lunch: '家常菜：大米饭150g、可乐鸡翅120g、酸辣土豆丝150g、番茄豆腐汤1碗', 
      dinner: '川菜小炒：馒头100g、青椒肉丝150g、蒜蓉油麦菜150g、紫菜蛋花汤1碗'
    },
    // 6号
    { 
      day: 6, 
      breakfast: '粥品早餐：八宝粥300ml、油条1根、咸菜1份、茶叶蛋1个', 
      lunch: '海鲜风味：大米饭150g、清蒸鲈鱼150g、家常豆腐100g、鱼头豆腐汤1碗', 
      dinner: '经典搭配：大米饭150g、红烧肉100g、清炒青菜150g、番茄鸡蛋汤1碗'
    },
    // 7号
    { 
      day: 7, 
      breakfast: '健康早餐：酸奶200ml、全麦面包2片、香蕉1根、水煮蛋1个', 
      lunch: '川菜经典：大米饭150g、鱼香肉丝150g、清炒时蔬150g、萝卜汤1碗', 
      dinner: '面食风味：西红柿鸡蛋面120g、煎蛋1个、凉拌黄瓜100g、面汤1碗'
    },
    // 8号
    { 
      day: 8, 
      breakfast: '包子早餐：豆浆300ml、香菇菜包2个、肉包1个、茶叶蛋1个', 
      lunch: '宫保系列：大米饭150g、宫保鸡丁150g、清炒黄瓜150g、酸辣汤1碗', 
      dinner: '东北风味：馒头100g、白菜炖粉条200g、炒土豆丝150g、小米粥1碗'
    },
    // 9号
    { 
      day: 9, 
      breakfast: '粥品早餐：南瓜粥300ml、咸鸭蛋1个、小笼包2个、榨菜1份', 
      lunch: '粤菜经典：大米饭150g、梅菜扣肉120g、白灼菜心150g、老火汤1碗', 
      dinner: '鲁菜风味：大米饭150g、糖醋排骨120g、红烧茄子150g、菠菜汤1碗'
    },
    // 10号
    { 
      day: 10, 
      breakfast: '西式早餐：热牛奶200ml、燕麦粥100g、煎蛋1个、橙子1个', 
      lunch: '家常菜：大米饭150g、可乐鸡翅120g、酸辣土豆丝150g、番茄豆腐汤1碗', 
      dinner: '面食精选：牛肉拉面150g、卤牛肉50g、凉拌豆芽100g、牛肉汤1碗'
    },
    // 11号
    { 
      day: 11, 
      breakfast: '煎饼早餐：豆浆300ml、杂粮煎饼1个、水煮鸡蛋1个、生菜叶2片', 
      lunch: '川菜风味：大米饭150g、麻婆豆腐150g、清炒空心菜150g、紫菜汤1碗', 
      dinner: '北方风味：馒头100g、青椒肉丝150g、蒜蓉油麦菜150g、小米粥1碗'
    },
    // 12号
    { 
      day: 12, 
      breakfast: '营养早餐：热牛奶200ml、全麦面包2片、水煮鸡蛋1个、新鲜蓝莓5颗', 
      lunch: '经典午餐：大米饭150g、红烧肉100g、清炒西兰花200g、番茄鸡蛋汤1碗', 
      dinner: '清淡晚餐：鸡蛋面条100g、西红柿鸡蛋卤、凉拌黄瓜100g、紫菜汤1碗'
    },
    // 13号
    { 
      day: 13, 
      breakfast: '传统早餐：豆浆300ml、油条1根、猪肉包子2个、小咸菜1份', 
      lunch: '粤菜风味：大米饭150g、白切鸡150g、清炒芥蓝150g、鸡汤1碗', 
      dinner: '鲁菜经典：大米饭150g、糖醋里脊120g、红烧茄子150g、菠菜汤1碗'
    },
    // 14号
    { 
      day: 14, 
      breakfast: '中式早餐：小米粥200ml、咸鸭蛋1个、小笼包3个、榨菜1份', 
      lunch: '川菜风味：大米饭150g、鱼香肉丝150g、清炒黄瓜150g、酸辣汤1碗', 
      dinner: '北方风味：馒头100g、白菜炖粉条200g、炒时蔬100g、小米粥1碗'
    },
    // 15号
    { 
      day: 15, 
      breakfast: '西式早餐：热牛奶200ml、燕麦粥100g、煎蛋1个、苹果1个', 
      lunch: '家常菜：大米饭150g、可乐鸡翅120g、酸辣土豆丝150g、番茄豆腐汤1碗', 
      dinner: '面食精选：炸酱面150g、黄瓜丝100g、豆芽100g、面汤1碗'
    },
    // 16号
    { 
      day: 16, 
      breakfast: '包子早餐：豆浆300ml、香菇菜包2个、肉包1个、茶叶蛋1个', 
      lunch: '鲁菜风味：大米饭150g、糖醋排骨120g、红烧茄子150g、菠菜汤1碗', 
      dinner: '川菜经典：大米饭150g、麻婆豆腐150g、清炒空心菜150g、紫菜汤1碗'
    },
    // 17号
    { 
      day: 17, 
      breakfast: '粥品早餐：八宝粥300ml、油条1根、咸菜1份、茶叶蛋1个', 
      lunch: '粤菜风味：大米饭150g、白切鸡150g、清炒芥蓝150g、鸡汤1碗', 
      dinner: '北方风味：馒头100g、青椒肉丝150g、蒜蓉油麦菜150g、小米粥1碗'
    },
    // 18号
    { 
      day: 18, 
      breakfast: '健康早餐：酸奶200ml、全麦面包2片、香蕉1根、水煮蛋1个', 
      lunch: '经典午餐：大米饭150g、红烧肉100g、清炒西兰花200g、番茄鸡蛋汤1碗', 
      dinner: '清淡晚餐：鸡蛋面条100g、西红柿鸡蛋卤、凉拌黄瓜100g、紫菜汤1碗'
    },
    // 19号
    { 
      day: 19, 
      breakfast: '煎饼早餐：豆浆300ml、杂粮煎饼1个、水煮鸡蛋1个、生菜叶2片', 
      lunch: '川菜风味：大米饭150g、宫保鸡丁150g、清炒黄瓜150g、酸辣汤1碗', 
      dinner: '面食精选：牛肉拉面150g、卤牛肉50g、凉拌豆芽100g、牛肉汤1碗'
    },
    // 20号
    { 
      day: 20, 
      breakfast: '营养早餐：热牛奶200ml、全麦面包2片、水煮鸡蛋1个、新鲜草莓5颗', 
      lunch: '家常菜：大米饭150g、可乐鸡翅120g、酸辣土豆丝150g、番茄豆腐汤1碗', 
      dinner: '鲁菜经典：大米饭150g、糖醋里脊120g、红烧茄子150g、菠菜汤1碗'
    },
    // 21号
    { 
      day: 21, 
      breakfast: '传统早餐：豆浆300ml、油条1根、猪肉包子2个、小咸菜1份', 
      lunch: '粤菜风味：大米饭150g、梅菜扣肉120g、白灼西兰花150g、老火靓汤1碗', 
      dinner: '北方风味：馒头100g、白菜炖粉条200g、炒时蔬100g、小米粥1碗'
    },
    // 22号
    { 
      day: 22, 
      breakfast: '中式早餐：小米粥200ml、咸鸭蛋1个、小笼包3个、榨菜1份', 
      lunch: '川菜风味：大米饭150g、麻婆豆腐150g、清炒空心菜150g、紫菜汤1碗', 
      dinner: '面食精选：炸酱面150g、黄瓜丝100g、豆芽100g、面汤1碗'
    },
    // 23号
    { 
      day: 23, 
      breakfast: '西式早餐：热牛奶200ml、燕麦粥100g、煎蛋1个、苹果1个', 
      lunch: '经典午餐：大米饭150g、红烧肉100g、清炒西兰花200g、番茄鸡蛋汤1碗', 
      dinner: '清淡晚餐：鸡蛋面条100g、西红柿鸡蛋卤、凉拌黄瓜100g、紫菜汤1碗'
    },
    // 24号
    { 
      day: 24, 
      breakfast: '包子早餐：豆浆300ml、香菇菜包2个、肉包1个、茶叶蛋1个', 
      lunch: '粤菜风味：大米饭150g、白切鸡150g、清炒芥蓝150g、鸡汤1碗', 
      dinner: '鲁菜经典：大米饭150g、糖醋排骨120g、红烧茄子150g、菠菜汤1碗'
    },
    // 25号
    { 
      day: 25, 
      breakfast: '粥品早餐：八宝粥300ml、油条1根、咸菜1份、茶叶蛋1个', 
      lunch: '川菜风味：大米饭150g、鱼香肉丝150g、清炒黄瓜150g、酸辣汤1碗', 
      dinner: '北方风味：馒头100g、青椒肉丝150g、蒜蓉油麦菜150g、小米粥1碗'
    },
    // 26号
    { 
      day: 26, 
      breakfast: '健康早餐：酸奶200ml、全麦面包2片、香蕉1根、水煮蛋1个', 
      lunch: '家常菜：大米饭150g、可乐鸡翅120g、酸辣土豆丝150g、番茄豆腐汤1碗', 
      dinner: '面食精选：牛肉拉面150g、卤牛肉50g、凉拌豆芽100g、牛肉汤1碗'
    },
    // 27号
    { 
      day: 27, 
      breakfast: '煎饼早餐：豆浆300ml、杂粮煎饼1个、水煮鸡蛋1个、生菜叶2片', 
      lunch: '川菜风味：大米饭150g、宫保鸡丁150g、清炒黄瓜150g、酸辣汤1碗', 
      dinner: '鲁菜经典：大米饭150g、糖醋里脊120g、红烧茄子150g、菠菜汤1碗'
    },
    // 28号
    { 
      day: 28, 
      breakfast: '营养早餐：热牛奶200ml、全麦面包2片、水煮鸡蛋1个、新鲜蓝莓5颗', 
      lunch: '粤菜风味：大米饭150g、梅菜扣肉120g、白灼西兰花150g、老火靓汤1碗', 
      dinner: '清淡晚餐：鸡蛋面条100g、西红柿鸡蛋卤、凉拌黄瓜100g、紫菜汤1碗'
    },
    // 29号
    { 
      day: 29, 
      breakfast: '传统早餐：豆浆300ml、油条1根、猪肉包子2个、小咸菜1份', 
      lunch: '经典午餐：大米饭150g、红烧肉100g、清炒西兰花200g、番茄鸡蛋汤1碗', 
      dinner: '北方风味：馒头100g、白菜炖粉条200g、炒时蔬100g、小米粥1碗'
    },
    // 30号
    { 
      day: 30, 
      breakfast: '中式早餐：小米粥200ml、咸鸭蛋1个、小笼包3个、榨菜1份', 
      lunch: '川菜风味：大米饭150g、麻婆豆腐150g、清炒空心菜150g、紫菜汤1碗', 
      dinner: '面食精选：炸酱面150g、黄瓜丝100g、豆芽100g、面汤1碗'
    },
    // 31号
    { 
      day: 31, 
      breakfast: '西式早餐：热牛奶200ml、燕麦粥100g、煎蛋1个、苹果1个', 
      lunch: '粤菜风味：大米饭150g、白切鸡150g、清炒芥蓝150g、鸡汤1碗', 
      dinner: '鲁菜经典：大米饭150g、糖醋排骨120g、红烧茄子150g、菠菜汤1碗'
    }
  ]
}

// 生命周期
onMounted(() => {
  fetchDietTemplate()
})
</script>

<style scoped>
.diet-calendar-container {
  width: 100%;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
  padding: 32px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #303133;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
  font-weight: 500;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef2ff 100%);
  border-bottom: 1px solid #f0f0f0;
}

.month-selector {
  display: flex;
  align-items: center;
  gap: 24px;
}

.month-btn {
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.current-month {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.card-body {
  padding: 24px;
}

/* 日历样式 */
.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.weekday {
  text-align: center;
  font-weight: 600;
  color: #606266;
  padding: 12px;
  background: #f8f9ff;
  border-radius: 8px;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day {
  min-height: 120px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  position: relative;
  transition: all 0.3s ease;
}

.calendar-day:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.calendar-day.prev-month,
.calendar-day.next-month {
  background: #f5f7fa;
  color: #909399;
}

.calendar-day.current-month {
  background: white;
}

.calendar-day.today {
  background: #ecf5ff;
  border-color: #409eff;
}

.day-number {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 8px;
  display: block;
}

/* 膳食信息样式 */
.diet-info {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.4;
}

.meal-item {
  margin-bottom: 4px;
  padding: 4px 8px;
  border-radius: 4px;
}

.meal-item.breakfast {
  background: #f0f9ff;
  color: #0088cc;
}

.meal-item.lunch {
  background: #f0fff4;
  color: #006400;
}

.meal-item.dinner {
  background: #fff7e6;
  color: #cc6600;
}

.meal-label {
  font-weight: 600;
  margin-right: 4px;
}

.meal-content {
  font-size: 11px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .diet-calendar-container {
    padding: 16px;
  }
  
  .calendar-day {
    min-height: 100px;
    padding: 8px;
  }
  
  .diet-info {
    font-size: 10px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .card-body {
    padding: 16px;
  }
  
  .calendar-day {
    min-height: 80px;
    padding: 6px;
  }
  
  .day-number {
    font-size: 14px;
  }
  
  .diet-info {
    font-size: 9px;
  }
  
  .meal-item {
    padding: 2px 4px;
  }
}
</style>