<template>
  <div class="screen-container">
    <div class="bg-grid"></div>
    <div class="bg-glow"></div>
    <div class="bg-particles"></div>

    <div class="header">
      <div class="header-decoration-left"></div>
      <div class="header-center">
        <h1 class="title">智慧养老 · 健康监测驾驶舱</h1>
        <div class="subtitle">INTELLIGENT ELDERLY CARE MONITORING SYSTEM</div>
      </div>
      <div class="header-decoration-right"></div>
      <div class="time-display">
        <span class="date">{{ currentDate }}</span>
        <span class="time">{{ currentTime }}</span>
      </div>
    </div>

    <div class="main-layout">
      
      <div class="column left-col">
        <div class="tech-panel profile-panel">
          <div class="panel-corner top-left"></div><div class="panel-corner top-right"></div>
          <div class="panel-corner bottom-left"></div><div class="panel-corner bottom-right"></div>
          
          <div class="panel-title">
            <i class="icon-monitor"></i> 监测对象档案 / TARGET
          </div>
          
          <div class="panel-content">
            <div class="custom-select-wrapper">
              <el-select 
                v-model="currentResidentId" 
                placeholder="请选择监测对象" 
                class="neon-select"
                popper-class="neon-select-dropdown"
                filterable
                @change="handleResidentChange"
              >
                <el-option
                  v-for="item in residentsList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </div>

            <div class="profile-card" v-if="currentProfile">
              <div class="avatar-section">
                <div class="scan-circle" :class="{ 'alert-pulse': alerts.length > 0 }">
                  <div class="avatar-img">{{ currentProfile.name?.charAt(0) }}</div>
                  <div class="scan-line"></div>
                </div>
                <div class="status-badge" :class="alerts.length > 0 ? 'bg-red' : 'bg-green'">
                  {{ alerts.length > 0 ? '异常' : '正常' }}
                </div>
              </div>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">姓名 NAME</span>
                  <span class="val">{{ currentProfile.name }}</span>
                </div>
                <div class="info-item">
                  <span class="label">年龄 AGE</span>
                  <span class="val">{{ currentProfile.age }} <small>岁</small></span>
                </div>
                <div class="info-item">
                  <span class="label">房间 ROOM</span>
                  <span class="val hl-yellow">{{ currentProfile.room_number || '---' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">护理 LEVEL</span>
                  <span class="val hl-cyan">{{ currentProfile.nursing_level || '标准' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="tech-panel alert-panel">
          <div class="panel-corner top-left"></div><div class="panel-corner top-right"></div>
          <div class="panel-corner bottom-left"></div><div class="panel-corner bottom-right"></div>
          
          <div class="panel-title">
            <i class="icon-alert"></i> 实时预警 / ALERTS
          </div>
          <div class="panel-content scroll-box">
            <transition-group name="slide-fade" tag="div" class="alert-list">
              <div 
                v-for="(alert, index) in alerts" 
                :key="index" 
                class="alert-row"
                :class="alert.level || 'warning'"
              >
                <div class="alert-icon">!</div>
                <div class="alert-info">
                  <div class="row-1">
                    <span class="alert-type">{{ alert.title || '健康异常' }}</span>
                    <span class="alert-time">{{ formatTimeOnly(alert.time) }}</span>
                  </div>
                  <div class="row-2">{{ alert.description || alert.content }}</div>
                </div>
              </div>
            </transition-group>
            
            <div v-if="alerts.length === 0" class="no-data">
              <div class="safe-shield">🛡️</div>
              <p>各项体征指标平稳</p>
            </div>
          </div>
        </div>
      </div>

      <div class="column center-col">
        <div class="tech-panel map-panel">
          <div class="map-border-glow"></div>
          <div class="panel-title center-title">
            院内实时追踪 (LBS)
          </div>
          <div class="map-chart-box" ref="mapChartRef"></div>
          <div class="map-stats">
            <div class="stat-item">
              <span class="num">{{ residentsList.length }}</span>
              <span class="txt">在住人数</span>
            </div>
            <div class="stat-item">
              <span class="num text-green">{{ residentsList.length }}</span>
              <span class="txt">定位正常</span>
            </div>
          </div>
        </div>

        <div class="tech-panel trend-panel">
          <div class="panel-corner top-left"></div><div class="panel-corner top-right"></div>
          <div class="panel-corner bottom-left"></div><div class="panel-corner bottom-right"></div>
          
          <div class="panel-title flex-title">
            <span><i class="icon-chart"></i> 72H体征趋势 / TRENDS</span>
            <div class="chart-tabs">
              <span :class="{active: chartType==='bp'}" @click="chartType='bp'">血压</span>
              <span :class="{active: chartType==='bs'}" @click="chartType='bs'">血糖</span>
              <span :class="{active: chartType==='hr'}" @click="chartType='hr'">心率</span>
            </div>
          </div>
          <div class="chart-box" ref="trendChartRef"></div>
        </div>
      </div>

      <div class="column right-col">
        <div class="tech-panel vitals-panel">
          <div class="panel-corner top-left"></div><div class="panel-corner top-right"></div>
          <div class="panel-corner bottom-left"></div><div class="panel-corner bottom-right"></div>
          
          <div class="panel-title">
            <i class="icon-pulse"></i> 实时体征 / VITALS
            <button class="add-btn" @click="dialogVisible=true">+</button>
          </div>
          
          <div class="vitals-grid">
            <div class="vital-card">
              <div class="chart-mini" ref="hrGaugeRef"></div>
              <div class="vital-data">
                <span class="label">心率 (BPM)</span>
                <span class="value text-orange">{{ latestVitals.hr }}</span>
              </div>
            </div>
            <div class="vital-card">
              <div class="chart-mini" ref="spo2GaugeRef"></div>
              <div class="vital-data">
                <span class="label">血氧 (%)</span>
                <span class="value text-cyan">{{ latestVitals.spo2 }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="tech-panel temp-panel">
          <div class="panel-corner top-left"></div><div class="panel-corner top-right"></div>
          <div class="panel-corner bottom-left"></div><div class="panel-corner bottom-right"></div>
          
          <div class="panel-title"><i class="icon-temp"></i> 体温监测 / TEMP</div>
          <div class="temp-layout">
            <div class="temp-big">
              <span class="num" :class="getTempColor(latestVitals.temp)">{{ latestVitals.temp }}</span>
              <span class="unit">°C</span>
            </div>
            <div class="temp-chart-wrapper" ref="tempChartRef"></div>
          </div>
        </div>

        <div class="tech-panel sleep-panel">
          <div class="panel-corner top-left"></div><div class="panel-corner top-right"></div>
          <div class="panel-corner bottom-left"></div><div class="panel-corner bottom-right"></div>
          
          <div class="panel-title"><i class="icon-sleep"></i> 昨夜睡眠 / SLEEP</div>
          <div class="sleep-content" v-if="sleepData">
            <div class="sleep-visual" :style="{'--sleep-progress': sleepScore / 100}">
              <!-- 定义渐变 -->
              <svg width="0" height="0" style="position: absolute;">
                <defs>
                  <linearGradient id="sleepGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" style="stop-color:#43a047;stop-opacity:1" />
                    <stop offset="50%" style="stop-color:#f57c00;stop-opacity:1" />
                    <stop offset="100%" style="stop-color:#8e24aa;stop-opacity:1" />
                  </linearGradient>
                </defs>
              </svg>
              <!-- 睡眠质量圆环图 -->
              <svg viewBox="0 0 80 80" class="circular-chart blue">
                <circle class="circle-bg" cx="40" cy="40" r="40" />
                <circle class="circle" cx="40" cy="40" r="40" />
                <text x="40" y="43" class="percentage">{{ sleepData.sleep_quality }}</text>
              </svg>
            </div>
            <div class="sleep-metrics">
              <div class="metric"><span class="l">时长</span> <span class="v">{{ sleepData.sleep_duration }}h</span></div>
              <div class="metric"><span class="l">深睡</span> <span class="v">{{ sleepData.deepSleepDuration || '-' }}h</span></div>
              <div class="metric"><span class="l">夜醒</span> <span class="v">{{ sleepData.night_wake_count || 0 }}次</span></div>
            </div>
          </div>
          <div class="no-data-text" v-else>暂无数据</div>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="数据录入" width="400px" class="tech-dialog" :modal="true" append-to-body>
      <el-form :model="recordForm" label-position="top" class="dark-form">
        <el-form-item label="类型">
          <el-radio-group v-model="recordType" fill="#6574cd" text-color="#000">
            <el-radio-button label="bloodPressure">血压</el-radio-button>
            <el-radio-button label="bloodSugar">血糖</el-radio-button>
            <el-radio-button label="heartRate">心率</el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <div v-if="recordType === 'bloodPressure'" class="form-row">
          <el-form-item label="收缩压"><el-input-number v-model="recordForm.systolic" :min="0" /></el-form-item>
          <el-form-item label="舒张压"><el-input-number v-model="recordForm.diastolic" :min="0" /></el-form-item>
        </div>
        <div v-if="recordType === 'bloodSugar'">
          <el-form-item label="血糖值"><el-input-number v-model="recordForm.level" :step="0.1" /></el-form-item>
          <el-form-item label="状态"><el-radio-group v-model="recordForm.status"><el-radio label="空腹">空腹</el-radio><el-radio label="餐后">餐后</el-radio></el-radio-group></el-form-item>
        </div>
        <div v-if="recordType === 'heartRate'">
          <el-form-item label="心率"><el-input-number v-model="recordForm.rate" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <button class="tech-btn" @click="submitRecord">确认提交</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, computed } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getAllResidents, getBloodPressureRecords, getBloodSugarRecords, getHeartRateRecords, getHealthAlerts, addBloodPressureRecord, addBloodSugarRecord, addHeartRateRecord } from '@/api/resident'

// --- API (保持原逻辑) ---
const getBloodOxygen = (id) => request({ url: `/health-monitoring/blood-oxygen/${id}`, method: 'get' })
const getBodyTemperature = (id) => request({ url: `/health-monitoring/body-temperature/${id}`, method: 'get' })
const getSleepQuality = (id) => request({ url: `/health-monitoring/sleep-quality/${id}`, method: 'get' })

// --- 状态 ---
const currentDate = ref('')
const currentTime = ref('')
const residentsList = ref([])
const currentResidentId = ref(null)
const currentProfile = ref(null)
const alerts = ref([])
const sleepData = ref(null)
const latestVitals = reactive({ hr: 0, spo2: 0, temp: 36.5 })
const chartType = ref('bp')

// ECharts
const mapChartRef = ref(null), trendChartRef = ref(null), hrGaugeRef = ref(null), spo2GaugeRef = ref(null), tempChartRef = ref(null)
let charts = []

// 弹窗
const dialogVisible = ref(false)
const recordType = ref('bloodPressure')
const recordForm = reactive({ systolic: 120, diastolic: 80, level: 5.6, rate: 75, status: '空腹', notes: '' })

// 计算睡眠分数用于圆环图
const sleepScore = computed(() => {
  if (!sleepData.value) return 0;
  const q = sleepData.value.sleep_quality;
  return q === '优' ? 95 : q === '良' ? 80 : q === '中' ? 60 : 40;
})

onMounted(async () => {
  startClock()
  await initResidents()
  window.addEventListener('resize', handleResize)
  // 初始化仪表盘占位
  initGauges()
})

onUnmounted(() => {
  stopClock()
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
})

// --- 时钟 ---
let timer
const startClock = () => {
  const update = () => {
    const d = new Date()
    currentDate.value = `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')}`
    currentTime.value = `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}:${String(d.getSeconds()).padStart(2,'0')}`
  }
  update()
  timer = setInterval(update, 1000)
}
const stopClock = () => clearInterval(timer)

// --- 数据逻辑 ---
const initResidents = async () => {
  try {
    const res = await getAllResidents()
    let list = res.data?.success ? (res.data.data.list || res.data.data) : (res.data || [])
    // 过滤退房
    residentsList.value = list.filter(r => (r.status || '').indexOf('退') === -1)
    
    if (residentsList.value.length > 0) {
      currentResidentId.value = residentsList.value[0].id
      handleResidentChange(currentResidentId.value)
    }
  } catch(e) { console.error(e) }
}

const handleResidentChange = (id) => {
  currentProfile.value = residentsList.value.find(r => r.id === id) || {}
  loadData(id)
}

const loadData = async (id) => {
  try {
    const [bp, bs, hr, bo, temp, sleep, alert] = await Promise.all([
      getBloodPressureRecords({ residentId: id }),
      getBloodSugarRecords({ residentId: id }),
      getHeartRateRecords({ residentId: id }),
      getBloodOxygen(id),
      getBodyTemperature(id),
      getSleepQuality(id),
      getHealthAlerts()
    ])

    // 1. 预警 (筛选)
    const allAlerts = alert.data?.success ? alert.data.data : []
    alerts.value = allAlerts.filter(a => (a.description || '').includes(currentProfile.value.name))

    // 2. 体温数据处理
    const tempData = temp.data?.success ? temp.data.data : []
    // 处理体温数据，兼容不同字段名
    const processedTempData = tempData.map(item => ({
      temperature: item.temperature || item.temp || 36.5,
      measure_time: item.measure_time || item.measureTime || item.recordTime || new Date().toISOString(),
      measure_part: item.measure_part || item.measurePart || '腋下'
    })).sort((a, b) => new Date(b.measure_time) - new Date(a.measure_time))
    
    latestVitals.temp = processedTempData.length ? processedTempData[0].temperature : 36.5
    renderTempChart(processedTempData)

    // 3. 仪表盘数据处理
    // 心率数据
    const hrData = hr.data?.success ? (hr.data.data || []) : []
    const sortedHrData = hrData.sort((a, b) => new Date(b.record_time || b.recordTime || 0) - new Date(a.record_time || a.recordTime || 0))
    const lastHr = sortedHrData.length ? (sortedHrData[0].rate || sortedHrData[0].heartRate || 75) : 75
    
    // 血氧数据
    const boData = bo.data?.success ? (bo.data.data || []) : []
    const processedBoData = boData.map(item => ({
      oxygen_saturation: item.oxygen_saturation || item.oxygenSaturation || item.spo2 || 98,
      measure_time: item.measure_time || item.measureTime || item.recordTime || new Date().toISOString()
    })).sort((a, b) => new Date(b.measure_time) - new Date(a.measure_time))
    
    const lastSpo2 = processedBoData.length ? processedBoData[0].oxygen_saturation : 98
    
    latestVitals.hr = lastHr
    latestVitals.spo2 = lastSpo2
    renderGauge(hrGaugeRef.value, lastHr, 200, '#f57c00') // Orange
    renderGauge(spo2GaugeRef.value, lastSpo2, 100, '#00b8d4') // Cyan

    // 4. 睡眠数据处理
    const sleepRawData = sleep.data?.success ? sleep.data.data : []
    // 处理睡眠数据，兼容不同字段名
    if (sleepRawData.length > 0) {
      const sleepItem = sleepRawData[0]
      sleepData.value = {
        sleep_duration: sleepItem.sleep_duration || sleepItem.sleepDuration || sleepItem.duration || 0,
        sleep_quality: sleepItem.sleep_quality || sleepItem.sleepQuality || sleepItem.quality || '未知',
        fall_asleep_time: sleepItem.fall_asleep_time || sleepItem.fallAsleepTime || sleepItem.sleepStart || null,
        wake_up_time: sleepItem.wake_up_time || sleepItem.wakeUpTime || sleepItem.sleepEnd || null,
        night_wake_count: sleepItem.night_wake_count || sleepItem.nightWakeCount || sleepItem.wakeCount || 0
      }
    } else {
      sleepData.value = null
    }

    // 5. 趋势图数据处理
    const bpList = bp.data?.success ? (bp.data.data || []) : []
    const bsList = bs.data?.success ? (bs.data.data || []) : []
    renderTrend(bpList, bsList, hrData)

    // 6. 地图
    renderMap(currentProfile.value.room_number)

  } catch(e) { console.error("加载数据失败", e) }
}

// --- 图表渲染 (ECharts 动态效果增强版) ---

// 通用配置增强
const commonGrid = { top: 30, bottom: 20, left: 40, right: 20, containLabel: true }
const darkTooltip = { 
  backgroundColor: 'rgba(0,0,0,0.9)', 
  borderColor: '#6574cd', 
  textStyle: { color: '#fff' },
  padding: 10,
  borderRadius: 6,
  boxShadow: '0 0 15px rgba(101, 116, 205, 0.3)'
}

// 趋势图增强 - 添加动态效果
const renderTrend = (bp, bs, hr) => {
  if(!trendChartRef.value) return
  let chart = getChart(trendChartRef.value)
  
  let x = [], s = [], colors = []
  const fmt = d => { let t = new Date(d.measureTime||d.createTime||d.measure_time||d.record_time); return `${t.getMonth()+1}-${t.getDate()} ${t.getHours()}h` }

  if(chartType.value === 'bp') {
    x = bp.map(i => fmt(i))
    s = [
      { 
        name: '收缩压', 
        data: bp.map(i=>i.systolic), 
        type: 'line', 
        smooth: true, 
        areaStyle: { 
          opacity: 0.3,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(101, 116, 205, 0.5)' },
            { offset: 1, color: 'rgba(101, 116, 205, 0.1)' }
          ])
        },
        lineStyle: { width: 3, color: '#6574cd' },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: { color: '#6574cd', borderColor: '#fff', borderWidth: 2 },
        emphasis: { symbolSize: 8 },
        animation: true,
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      },
      { 
        name: '舒张压', 
        data: bp.map(i=>i.diastolic), 
        type: 'line', 
        smooth: true, 
        areaStyle: { 
          opacity: 0.2,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(142, 36, 170, 0.5)' },
            { offset: 1, color: 'rgba(142, 36, 170, 0.1)' }
          ])
        },
        lineStyle: { width: 3, color: '#8e24aa' },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: { color: '#8e24aa', borderColor: '#fff', borderWidth: 2 },
        emphasis: { symbolSize: 8 },
        animation: true,
        animationDuration: 1500,
        animationEasing: 'cubicOut',
        animationDelay: 300
      }
    ]
    colors = ['#6574cd', '#8e24aa']
  } else if(chartType.value === 'bs') {
    x = bs.map(i => fmt(i))
    s = [{
      name: '血糖', 
      data: bs.map(i=>i.level), 
      type: 'line', 
      smooth: true, 
      areaStyle: { 
        opacity: 0.3,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(67, 160, 71, 0.5)' },
          { offset: 1, color: 'rgba(67, 160, 71, 0.1)' }
        ])
      },
      lineStyle: { width: 3, color: '#43a047' },
      symbol: 'circle',
      symbolSize: 6,
      itemStyle: { color: '#43a047', borderColor: '#fff', borderWidth: 2 },
      emphasis: { symbolSize: 8 },
      animation: true,
      animationDuration: 1500,
      animationEasing: 'cubicOut'
    }]
    colors = ['#43a047']
  } else {
    x = hr.map(i => fmt(i))
    s = [{
      name: '心率', 
      data: hr.map(i=>i.rate || i.heartRate), 
      type: 'line', 
      smooth: true, 
      areaStyle: { 
        opacity: 0.3,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(245, 124, 0, 0.5)' },
          { offset: 1, color: 'rgba(245, 124, 0, 0.1)' }
        ])
      },
      lineStyle: { width: 3, color: '#f57c00' },
      symbol: 'circle',
      symbolSize: 6,
      itemStyle: { color: '#f57c00', borderColor: '#fff', borderWidth: 2 },
      emphasis: { symbolSize: 8 },
      animation: true,
      animationDuration: 1500,
      animationEasing: 'cubicOut'
    }]
    colors = ['#f57c00']
  }

  chart.setOption({
    color: colors,
    tooltip: { trigger: 'axis', ...darkTooltip },
    grid: commonGrid,
    xAxis: { 
      type: 'category', 
      data: x, 
      axisLine: { 
        lineStyle: { 
          color: '#6574cd',
          width: 2
        } 
      }, 
      axisLabel: { 
        color: '#6574cd',
        fontSize: 11,
        fontWeight: 'bold'
      },
      axisTick: {
        show: true,
        lineStyle: {
          color: '#6574cd',
          width: 2
        }
      }
    },
    yAxis: { 
      type: 'value', 
      splitLine: { 
        lineStyle: { 
          type: 'dashed', 
          color: 'rgba(101, 116, 205, 0.2)'
        } 
      }, 
      axisLabel: { 
        color: '#aaa',
        fontSize: 11
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: 'rgba(101, 116, 205, 0.3)'
        }
      }
    },
    series: s,
    animation: true,
    animationDuration: 2000,
    animationEasing: 'elasticOut'
  }, true)
}

// 仪表盘增强 - 添加动态效果和渐变
const renderGauge = (dom, val, max, color) => {
  if(!dom) return
  let chart = getChart(dom)
  
  // 根据数值动态生成渐变色
  const getGradientColor = (value, max, baseColor) => {
    const ratio = value / max;
    if (ratio > 0.8) {
      return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
        { offset: 0, color: baseColor },
        { offset: 1, color: '#8e24aa' }
      ]);
    } else if (ratio > 0.6) {
      return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
        { offset: 0, color: '#43a047' },
        { offset: 1, color: baseColor }
      ]);
    } else {
      return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
        { offset: 0, color: '#43a047' },
        { offset: 1, color: baseColor }
      ]);
    }
  };

  chart.setOption({
    series: [{
      type: 'gauge', 
      startAngle: 180, 
      endAngle: 0, 
      min: 0, 
      max: max,
      splitNumber: 5, 
      itemStyle: { 
        color: getGradientColor(val, max, color), 
        shadowBlur: 20, 
        shadowColor: color,
        shadowOffsetX: 0,
        shadowOffsetY: 10
      },
      progress: { 
        show: true, 
        width: 12,
        roundCap: true,
        itemStyle: {
          color: getGradientColor(val, max, color)
        }
      }, 
      pointer: { 
        show: true, 
        length: '70%',
        width: 4,
        itemStyle: {
          color: color,
          shadowBlur: 10,
          shadowColor: color
        }
      },
      axisLine: { 
        lineStyle: { 
          width: 12, 
          color: [[0.6, 'rgba(67, 160, 71, 0.3)'], [0.8, 'rgba(245, 124, 0, 0.3)'], [1, 'rgba(142, 36, 170, 0.3)']]
        } 
      },
      axisTick: { 
        show: true,
        length: 8,
        lineStyle: {
          color: '#6574cd',
          width: 2
        }
      }, 
      splitLine: { 
        show: true,
        length: 15,
        lineStyle: {
          color: '#6574cd',
          width: 3
        }
      }, 
      axisLabel: { 
        show: true,
        color: '#6574cd',
        fontSize: 12,
        distance: -20
      },
      detail: { 
        show: false 
      }, 
      data: [{ 
        value: val,
        name: '',
        label: {
          show: false
        }
      }],
      animation: true,
      animationDuration: 2000,
      animationEasing: 'elasticOut'
    }]
  })
}

// 体温图增强 - 添加动态效果和渐变
const renderTempChart = (data) => {
  if(!tempChartRef.value) return
  let chart = getChart(tempChartRef.value)
  
  // 排序数据按时间顺序
  const sortedData = [...data].sort((a, b) => new Date(a.measure_time) - new Date(b.measure_time))
  
  const x = sortedData.map(i => { 
    let d = new Date(i.measure_time); 
    return `${d.getHours()}:${d.getMinutes()}` 
  })
  const y = sortedData.map(i => i.temperature)
  
  chart.setOption({
    tooltip: { 
      trigger: 'axis', 
      ...darkTooltip,
      formatter: function(params) {
        return `<div style="color:#6574cd; font-weight:bold; margin-bottom:5px;">${params[0].axisValue}</div>` +
               `<div style="color:#fff;">体温: <span style="color:#6574cd; font-size:16px;">${params[0].value}°C</span></div>`
      }
    },
    grid: { top: 5, bottom: 5, left: 0, right: 0 },
    xAxis: { show: false, data: x },
    yAxis: { show: false, min: 35, max: 40 },
    series: [{
      type: 'line', 
      data: y, 
      smooth: true, 
      showSymbol: false,
      lineStyle: { 
        color: '#6574cd', 
        width: 3,
        shadowBlur: 10,
        shadowColor: 'rgba(101, 116, 205, 0.5)',
        shadowOffsetY: 5
      },
      areaStyle: { 
        color: new echarts.graphic.LinearGradient(0,0,0,1, [
          {offset:0, color:'rgba(101, 116, 205, 0.6)'}, 
          {offset:0.5, color:'rgba(101, 116, 205, 0.2)'}, 
          {offset:1, color:'rgba(101, 116, 205, 0)'}]
        )
      },
      animation: true,
      animationDuration: 2500,
      animationEasing: 'cubicOut',
      animationDelay: 300
    }],
    animation: true,
    animationDuration: 2000,
    animationEasing: 'elasticOut'
  })
}

// 地图增强 - 添加动态效果和更多交互
const renderMap = (room) => {
  if(!mapChartRef.value) return
  let chart = getChart(mapChartRef.value)
  
  // 模拟房间布局增强
  const rooms = [
    {name: '301', value: [20, 70], status: 'occupied'},
    {name: '302', value: [40, 70], status: 'occupied'},
    {name: '303', value: [60, 70], status: 'occupied'},
    {name: '304', value: [80, 70], status: 'vacant'},
    {name: '201', value: [20, 30], status: 'occupied'},
    {name: '202', value: [40, 30], status: 'occupied'},
    {name: '203', value: [60, 30], status: 'occupied'},
    {name: '护士站', value: [85, 30], status: 'staff'}
  ]
  
  const current = rooms.find(r => room && r.name.includes(room.match(/\d+/)?.[0])) || {name: '大厅', value: [50, 50]}

  chart.setOption({
    xAxis: { show: false, min: 0, max: 100 }, 
    yAxis: { show: false, min: 0, max: 100 },
    grid: { top: 0, bottom: 0, left: 0, right: 0 },
    series: [
      { // 背景网格
        type: 'scatter', 
        symbol: 'rect', 
        symbolSize: [90, 45],
        data: [[50, 50]],
        itemStyle: { 
          color: 'rgba(0,229,255,0.05)', 
          borderColor: 'rgba(0,229,255,0.1)', 
          borderWidth: 1 
        },
        label: { show: false }
      },
      { // 房间框增强
        type: 'scatter', 
        symbol: 'rect', 
        symbolSize: [50, 35],
        data: rooms,
        itemStyle: { 
          color: function(params) {
            const room = params.data;
            if (room.status === 'occupied') {
              return 'rgba(101, 116, 205, 0.15)';
            } else if (room.status === 'vacant') {
              return 'rgba(67, 160, 71, 0.1)';
            } else {
              return 'rgba(245, 124, 0, 0.15)';
            }
          },
          borderColor: function(params) {
            const room = params.data;
            if (room.status === 'occupied') {
              return '#6574cd';
            } else if (room.status === 'vacant') {
              return '#43a047';
            } else {
              return '#f57c00';
            }
          },
          borderWidth: 1.5,
          shadowBlur: 10,
          shadowColor: 'rgba(0, 229, 255, 0.2)'
        },
        label: { 
          show: true, 
          formatter: '{b}', 
          color: function(params) {
            const room = params.data;
            if (room.status === 'occupied') {
              return '#6574cd';
            } else if (room.status === 'vacant') {
              return '#43a047';
            } else {
              return '#f57c00';
            }
          }, 
          fontSize: 10,
          fontWeight: 'bold'
        },
        emphasis: {
          itemStyle: {
            borderWidth: 2,
            shadowBlur: 20,
            shadowColor: 'rgba(0, 229, 255, 0.5)'
          },
          label: {
            fontSize: 12,
            color: '#fff'
          }
        }
      },
      { // 人员定位增强
        type: 'effectScatter', 
        coordinateSystem: 'cartesian2d',
        data: [current.value],
        symbolSize: 12, 
        itemStyle: { 
          color: '#ffeb3b', 
          shadowBlur: 20,
          shadowColor: 'rgba(255, 235, 59, 0.8)',
          borderColor: '#fff',
          borderWidth: 2
        },
        rippleEffect: { 
          brushType: 'fill', 
          scale: 6,
          period: 3,
          color: 'rgba(255, 235, 59, 0.6)'
        },
        animation: true,
        animationDuration: 2000,
        animationEasing: 'elasticOut'
      },
      { // 房间连接线条
        type: 'line',
        data: [[20, 70], [40, 70], [60, 70], [80, 70], [85, 30], [60, 30], [40, 30], [20, 30], [20, 70]],
        lineStyle: {
          color: 'rgba(101, 116, 205, 0.15)',
          type: 'dashed',
          width: 1
        },
        symbol: 'none',
        animation: true,
        animationDuration: 3000,
        animationEasing: 'linear',
        animationDelay: 500
      }
    ],
    animation: true,
    animationDuration: 2500,
    animationEasing: 'elasticOut'
  })
}

const getChart = (dom) => {
  let c = echarts.getInstanceByDom(dom)
  if(!c) { c = echarts.init(dom); charts.push(c) }
  return c
}
const handleResize = () => charts.forEach(c => c.resize())
const getTempColor = (t) => t > 37.2 ? 'text-red' : 'text-green'
const formatTimeOnly = (s) => s ? s.substring(11,16) : ''

const submitRecord = async () => {
  const d = { ...recordForm, residentId: currentResidentId.value }
  let r; if(recordType.value==='bloodPressure') r=await addBloodPressureRecord(d); else if(recordType.value==='bloodSugar') r=await addBloodSugarRecord(d); else r=await addHeartRateRecord(d);
  if(r.data?.success) { ElMessage.success('录入成功'); dialogVisible.value=false; loadData(currentResidentId.value); } else ElMessage.error('失败');
}
</script>

<style scoped>
/* 引入数字字体 */
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Rajdhani:wght@500;700&display=swap');

/* 全局变量 - 深度优化深色主题配色方案 */
:root {
  --bg-dark: #00020a;
  --panel-bg: rgba(5, 10, 20, 0.98);
  --neon-cyan: #006064;
  --neon-blue: #283593;
  --neon-red: #4a148c;
  --neon-orange: #e65100;
  --neon-green: #1b5e20;
  --border-color: rgba(40, 53, 147, 0.8);
  /* 深度优化主题色 */
  --primary-accent: #283593;
  --secondary-accent: #4a148c;
  --tertiary-accent: #1b5e20;
  --quaternary-accent: #e65100;
  /* 动态效果变量增强 */
  --glow-intensity: 0.6;
  --transition-speed: 0.3s;
  --card-hover-scale: 1.03;
  --shadow-intensity: 0.8;
  /* 新增深度效果变量 */
  --depth-light: rgba(255, 255, 255, 0.05);
  --depth-medium: rgba(255, 255, 255, 0.1);
  --depth-heavy: rgba(255, 255, 255, 0.15);
}

.screen-container {
  width: 100%;
  height: 100vh;
  background-color: var(--bg-dark);
  color: #fff;
  font-family: 'Rajdhani', sans-serif;
  overflow: hidden;
  position: relative;
}

/* 深度动态背景效果增强 */
.bg-grid {
  position: absolute; 
  width: 100%; 
  height: 100%;
  background-image: 
    linear-gradient(rgba(40, 53, 147, 0.15) 1px, transparent 1px),
    linear-gradient(90deg, rgba(40, 53, 147, 0.15) 1px, transparent 1px);
  background-size: 40px 40px;
  z-index: 0;
  animation: gridMove 25s linear infinite;
  opacity: 0.7;
}

/* 网格移动动画增强 */
@keyframes gridMove {
  0% {
    transform: translate(0, 0);
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translate(40px, 40px);
    opacity: 0.7;
  }
}

.bg-glow {
  position: absolute; 
  width: 100%; 
  height: 100%;
  background: 
    radial-gradient(circle at 20% 30%, rgba(40, 53, 147, 0.4), transparent 65%),
    radial-gradient(circle at 80% 70%, rgba(74, 20, 140, 0.4), transparent 65%),
    radial-gradient(circle at 50% 50%, rgba(27, 94, 32, 0.3), transparent 85%);
  z-index: 0;
  animation: glowPulse 10s ease-in-out infinite;
}

/* 光晕脉冲动画增强 */
@keyframes glowPulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  33% {
    opacity: 0.8;
    transform: scale(1.02);
  }
  66% {
    opacity: 1;
    transform: scale(1.05);
  }
}

/* 多层动态粒子效果 */
.bg-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

/* 前景粒子层 */
.bg-particles::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(40, 53, 147, 0.6) 1px, transparent 1px),
    radial-gradient(circle at 30% 70%, rgba(74, 20, 140, 0.6) 1px, transparent 1px),
    radial-gradient(circle at 70% 40%, rgba(27, 94, 32, 0.6) 1px, transparent 1px),
    radial-gradient(circle at 90% 80%, rgba(230, 81, 0, 0.6) 1px, transparent 1px);
  background-size: 80px 80px;
  animation: particleFloat 12s ease-in-out infinite;
  opacity: 0.6;
  filter: blur(1px);
}

/* 背景粒子层 */
.bg-particles::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 25% 35%, rgba(40, 53, 147, 0.4) 1px, transparent 1px),
    radial-gradient(circle at 65% 85%, rgba(74, 20, 140, 0.4) 1px, transparent 1px),
    radial-gradient(circle at 45% 55%, rgba(230, 81, 0, 0.4) 1px, transparent 1px);
  background-size: 120px 120px;
  animation: particleFloat 18s ease-in-out infinite reverse;
  opacity: 0.4;
  filter: blur(1px);
}

/* 粒子浮动动画增强 */
@keyframes particleFloat {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
    opacity: 0.4;
  }
  25% {
    transform: translate(30px, -30px) rotate(90deg);
    opacity: 0.8;
  }
  50% {
    transform: translate(60px, 0) rotate(180deg);
    opacity: 1;
  }
  75% {
    transform: translate(30px, 30px) rotate(270deg);
    opacity: 0.8;
  }
}

/* Header */
.header {
  height: 80px; position: relative; z-index: 2;
  display: flex; justify-content: space-between; align-items: flex-start;
  padding: 0 20px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.8), transparent);
}
.header-decoration-left, .header-decoration-right {
  flex: 1; height: 2px; background: var(--border-color); margin-top: 35px; position: relative;
}
.header-decoration-left::after { content:''; position: absolute; right: 0; top: -3px; width: 50px; height: 8px; background: var(--neon-cyan); }
.header-decoration-right::after { content:''; position: absolute; left: 0; top: -3px; width: 50px; height: 8px; background: var(--neon-cyan); }

.header-center {
  flex: 0 0 500px; text-align: center;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='60' viewBox='0 0 400 60'%3E%3Cpath d='M0,0 L400,0 L380,50 L20,50 Z' fill='rgba(0, 229, 255, 0.1)' stroke='rgba(0, 229, 255, 0.5)' stroke-width='2'/%3E%3C/svg%3E");
  background-repeat: no-repeat; background-position: center top; background-size: 100% 100%;
  height: 60px; padding-top: 5px;
}
.title { margin: 0; font-size: 28px; color: #fff; text-shadow: 0 0 10px var(--neon-cyan); letter-spacing: 2px; }
.subtitle { font-size: 10px; color: var(--neon-cyan); letter-spacing: 4px; opacity: 0.8; }
.time-display { position: absolute; right: 30px; top: 40px; font-family: 'Share Tech Mono'; color: var(--neon-cyan); font-size: 18px; }
.time-display .date { margin-right: 15px; font-size: 14px; color: #aaa; }

/* 布局 */
.main-layout {
  display: grid; grid-template-columns: 25% 50% 25%; gap: 20px;
  padding: 10px 20px 20px; height: calc(100vh - 80px); position: relative; z-index: 1;
}
.column { display: flex; flex-direction: column; gap: 20px; }

/* 通用面板风格 - 深度增强设计 */
.tech-panel {
  background: linear-gradient(135deg, 
    var(--panel-bg) 0%,
    rgba(40, 53, 147, 0.12) 100%);
  border: 1px solid rgba(40, 53, 147, 0.5);
  border-radius: 14px;
  position: relative; 
  padding: 20px;
  flex: 1; 
  display: flex; 
  flex-direction: column;
  box-shadow: 
    0 10px 35px rgba(0, 0, 0, 0.7),
    inset 0 0 35px rgba(0, 0, 0, 0.8),
    0 0 0 1px rgba(40, 53, 147, 0.2) inset,
    0 0 20px rgba(40, 53, 147, 0.15);
  backdrop-filter: blur(12px);
  opacity: 0;
  transform: translateY(25px) scale(0.97);
  animation: panelEntrance 0.9s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
  transition: all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  overflow: hidden;
}

/* 面板边框动画效果增强 */
.tech-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 14px;
  padding: 1px;
  background: linear-gradient(90deg, 
    rgba(40, 53, 147, 0.6),
    rgba(74, 20, 140, 0.6),
    rgba(27, 94, 32, 0.6),
    rgba(230, 81, 0, 0.6),
    rgba(40, 53, 147, 0.6));
  -webkit-mask: 
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
  animation: borderGlow 3.5s linear infinite;
  z-index: 1;
  box-shadow: inset 0 0 30px rgba(40, 53, 147, 0.2);
}

/* 边框发光动画增强 */
@keyframes borderGlow {
  0% {
    background-position: 0% 0%;
  }
  100% {
    background-position: 400% 0%;
  }
}

/* 面板顶部光效增强 */
.tech-panel::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(40, 53, 147, 1) 50%,
    transparent 100%);
  animation: topGlow 2.8s ease-in-out infinite;
  z-index: 1;
  box-shadow: 0 0 15px rgba(40, 53, 147, 0.6);
  pointer-events: none;
}

/* 顶部光效动画增强 */
@keyframes topGlow {
  0% {
    opacity: 0.8;
    transform: translateX(-100%);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.8;
    transform: translateX(100%);
  }
}

/* 面板底部光效新增 - 使用额外的伪元素 */
.tech-panel .panel-content::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: -20px;
  right: -20px;
  height: 3px;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(74, 20, 140, 1) 50%,
    transparent 100%);
  animation: bottomGlow 3.2s ease-in-out infinite;
  z-index: 1;
  box-shadow: 0 0 15px rgba(74, 20, 140, 0.6);
  pointer-events: none;
}

/* 底部光效动画 */
@keyframes bottomGlow {
  0% {
    opacity: 0.8;
    transform: translateX(100%);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.8;
    transform: translateX(-100%);
  }
}

/* 面板入场动画增强 */
@keyframes panelEntrance {
  0% {
    opacity: 0;
    transform: translateY(25px) scale(0.97);
    box-shadow: 
      0 2px 10px rgba(0, 0, 0, 0.6),
      inset 0 0 20px rgba(0, 0, 0, 0.8),
      0 0 0 1px rgba(40, 53, 147, 0.15) inset;
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    box-shadow: 
      0 10px 35px rgba(0, 0, 0, 0.7),
      inset 0 0 35px rgba(0, 0, 0, 0.8),
      0 0 0 1px rgba(40, 53, 147, 0.2) inset,
      0 0 20px rgba(40, 53, 147, 0.15);
  }
}

/* 面板悬停效果增强 */
.tech-panel:hover {
  box-shadow: 
    0 15px 45px rgba(40, 53, 147, 0.4),
    inset 0 0 35px rgba(0, 0, 0, 0.8),
    0 0 0 1px rgba(40, 53, 147, 0.4) inset,
    0 0 30px rgba(40, 53, 147, 0.3);
  border-color: rgba(40, 53, 147, 0.8);
  transform: translateY(-6px) scale(1.02);
  background: linear-gradient(135deg, 
    var(--panel-bg) 0%,
    rgba(40, 53, 147, 0.18) 100%);
}

/* 四角装饰深度设计 */
.panel-corner { 
  position: absolute; 
  width: 18px; 
  height: 18px; 
  border: 2px solid rgba(40, 53, 147, 0.7); 
  transition: all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  opacity: 0.85;
  border-radius: 3px;
  background: rgba(40, 53, 147, 0.15);
  box-shadow: 0 0 12px rgba(40, 53, 147, 0.4);
  z-index: 2;
}

.top-left { top: -1px; left: -1px; border-right: 0; border-bottom: 0; }
.top-right { top: -1px; right: -1px; border-left: 0; border-bottom: 0; }
.bottom-left { bottom: -1px; left: -1px; border-right: 0; border-top: 0; }
.bottom-right { bottom: -1px; right: -1px; border-left: 0; border-top: 0; }

/* 四角装饰悬停效果增强 */
.tech-panel:hover .panel-corner {
  width: 24px; 
  height: 24px; 
  box-shadow: 0 0 25px rgba(40, 53, 147, 0.7);
  opacity: 1;
  border-width: 2px;
  background: rgba(40, 53, 147, 0.25);
  animation: cornerGlow 1.5s ease-in-out infinite alternate;
}

/* 四角发光动画 */
@keyframes cornerGlow {
  0% {
    box-shadow: 0 0 20px rgba(40, 53, 147, 0.6);
  }
  100% {
    box-shadow: 0 0 30px rgba(40, 53, 147, 0.9);
  }
}

/* 面板内容区域增强 */
.panel-content {
  flex: 1; 
  overflow: hidden; 
  position: relative;
  z-index: 1;
}

/* 面板标题深度设计 */
.panel-title {
  font-size: 16px; 
  color: #fff; 
  border-left: 4px solid var(--primary-accent);
  padding: 12px 18px;
  margin-bottom: 20px; 
  background: linear-gradient(90deg, 
    rgba(40, 53, 147, 0.35) 0%,
    rgba(40, 53, 147, 0.08) 100%);
  border-radius: 0 8px 8px 0;
  height: auto;
  line-height: 1.4;
  display: flex; 
  align-items: center; 
  justify-content: space-between;
  font-weight: 600;
  letter-spacing: 0.5px;
  box-shadow: 
    0 3px 12px rgba(0, 0, 0, 0.5),
    0 0 10px rgba(40, 53, 147, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

/* 面板标题光效 */
.panel-title::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(255, 255, 255, 0.1) 50%,
    transparent 100%);
  transition: all 0.6s ease;
  z-index: 0;
}

.panel-title:hover::after {
  left: 100%;
}

.panel-title:hover {
  transform: translateX(3px);
  box-shadow: 
    0 4px 15px rgba(40, 53, 147, 0.4),
    0 0 15px rgba(40, 53, 147, 0.3);
}

.panel-title i {
  margin-right: 10px;
  color: var(--primary-accent);
  text-shadow: 0 0 15px rgba(40, 53, 147, 0.8);
  font-size: 18px;
  position: relative;
  z-index: 1;
}

.panel-title span {
  position: relative;
  z-index: 1;
}

.panel-content { flex: 1; overflow: hidden; position: relative; z-index: 1; }
.flex-title { display: flex; justify-content: space-between; align-items: center; }

/* 1. 左侧 - 档案面板增强 */
.profile-panel { 
  flex: 0 0 280px; 
}

/* 下拉框现代化设计 */
:deep(.neon-select .el-input__wrapper) {
  background: rgba(0, 0, 0, 0.45) !important;
  box-shadow: 
    0 3px 10px rgba(0, 0, 0, 0.5),
    0 0 0 1px var(--primary-accent) inset !important;
  border-radius: 6px !important;
  transition: all 0.3s ease !important;
}

:deep(.neon-select .el-input__wrapper:hover) {
  box-shadow: 
    0 5px 15px rgba(57, 73, 171, 0.3),
    0 0 0 2px var(--primary-accent) inset !important;
}

:deep(.neon-select .el-input__inner) {
  color: #fff; 
  font-family: 'Share Tech Mono';
  background: transparent !important;
  border: none !important;
}

:deep(.neon-select .el-input__suffix-inner) {
  color: var(--primary-accent) !important;
}

:deep(.neon-select-dropdown .el-select-dropdown__wrap) {
  background: rgba(0, 0, 0, 0.9) !important;
  border: 1px solid var(--primary-accent) !important;
  border-radius: 6px !important;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.5);
}

/* 个人档案卡片增强 */
.profile-card { 
  display: flex; 
  flex-direction: column; 
  gap: 20px; 
  margin-top: 15px; 
}

.avatar-section { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  gap: 15px;
}

/* 扫描头像增强 */
.scan-circle {
  width: 100px; 
  height: 100px; 
  border-radius: 50%; 
  border: 2px solid var(--primary-accent);
  position: relative; 
  overflow: hidden; 
  display: flex; 
  align-items: center; 
  justify-content: center;
  background: radial-gradient(circle at center, 
    rgba(57, 73, 171, 0.2) 0%,
    rgba(0, 0, 0, 0.6) 100%);
  box-shadow: 
    0 0 30px rgba(57, 73, 171, 0.4),
    inset 0 0 20px rgba(0, 0, 0, 0.7),
    0 0 0 1px rgba(57, 73, 171, 0.2) inset;
  transition: all 0.3s ease;
}

.scan-circle:hover {
  box-shadow: 
    0 0 40px rgba(57, 73, 171, 0.6),
    inset 0 0 20px rgba(0, 0, 0, 0.7),
    0 0 0 1px rgba(57, 73, 171, 0.4) inset;
  transform: scale(1.05);
}

.alert-pulse { 
  border-color: var(--secondary-accent); 
  animation: pulse-enhanced 2s ease-in-out infinite;
}

/* 增强的脉冲动画 */
@keyframes pulse-enhanced {
  0% {
    box-shadow: 
      0 0 20px rgba(106, 27, 154, 0.3),
      inset 0 0 20px rgba(0, 0, 0, 0.7);
  }
  50% {
    box-shadow: 
      0 0 40px rgba(106, 27, 154, 0.6),
      inset 0 0 20px rgba(0, 0, 0, 0.7);
  }
  100% {
    box-shadow: 
      0 0 20px rgba(106, 27, 154, 0.3),
      inset 0 0 20px rgba(0, 0, 0, 0.7);
  }
}

.avatar-img { 
  font-size: 42px; 
  color: #fff; 
  text-shadow: 0 0 15px rgba(57, 73, 171, 0.7);
  font-weight: bold;
}

/* 扫描线增强 */
.scan-line {
  position: absolute; 
  width: 100%; 
  height: 3px; 
  background: linear-gradient(90deg, 
    transparent 0%,
    var(--primary-accent) 50%,
    transparent 100%);
  top: 0; 
  box-shadow: 0 0 10px var(--primary-accent); 
  animation: scan 2s linear infinite;
}

/* 优化扫描线动画 */
@keyframes scan {
  0% { 
    top: 0; 
    opacity: 1; 
  }
  100% { 
    top: 100%; 
    opacity: 0; 
  }
}

/* 状态徽章增强 */
.status-badge { 
  padding: 8px 20px; 
  border-radius: 25px; 
  font-weight: bold; 
  font-size: 14px; 
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.4);
  border: 1px solid transparent;
}

.bg-green { 
  background: rgba(46, 125, 50, 0.25); 
  color: var(--tertiary-accent); 
  border-color: var(--tertiary-accent); 
}

.bg-red { 
  background: rgba(106, 27, 154, 0.25); 
  color: var(--secondary-accent); 
  border-color: var(--secondary-accent); 
}

/* 状态徽章悬停效果 */
.status-badge:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
}

/* 信息网格优化 */
.info-grid { 
  display: grid; 
  grid-template-columns: 1fr; 
  gap: 12px;
}

.info-item { 
  background: rgba(255, 255, 255, 0.08); 
  padding: 15px 20px;
  border-radius: 8px;
  border-left: 3px solid var(--primary-accent);
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.4);
}

.info-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
  box-shadow: 0 5px 15px rgba(57, 73, 171, 0.3);
}

.info-item .label { 
  display: block; 
  font-size: 12px; 
  color: #aaa; 
  margin-bottom: 6px; 
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-item .val { 
  font-size: 20px; 
  font-weight: bold; 
  font-family: 'Share Tech Mono'; 
  color: #fff;
  text-shadow: 0 0 10px rgba(57, 73, 171, 0.6);
}

/* 颜色类优化 */
.hl-yellow { color: var(--quaternary-accent); } 
.hl-cyan { color: var(--primary-accent); }

/* 2. 左侧 - 预警面板深度增强 */
.alert-panel {
  flex: 1;
}

/* 滚动区域优化 */
.scroll-box {
  overflow-y: auto;
  padding-right: 12px;
  flex: 1;
  position: relative;
}

/* 现代化滚动条深度设计 */
.scroll-box::-webkit-scrollbar {
  width: 10px;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 5px;
  border: 1px solid rgba(40, 53, 147, 0.2);
}

.scroll-box::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, 
    var(--primary-accent) 0%,
    var(--secondary-accent) 100%);
  border-radius: 5px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 
    inset 0 2px 5px rgba(0, 0, 0, 0.6),
    0 0 8px rgba(40, 53, 147, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.scroll-box::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, 
    var(--secondary-accent) 0%,
    var(--primary-accent) 100%);
  box-shadow: 
    inset 0 2px 5px rgba(0, 0, 0, 0.7),
    0 0 15px rgba(40, 53, 147, 0.6);
  transform: scaleX(1.1);
}

.scroll-box::-webkit-scrollbar-track {
  background: linear-gradient(90deg, 
    rgba(0, 0, 0, 0.3) 0%,
    rgba(0, 0, 0, 0.5) 100%);
  border-radius: 5px;
  box-shadow: inset 0 0 8px rgba(0, 0, 0, 0.5);
}

/* 预警项深度增强 */
.alert-row {
  display: flex; 
  margin-bottom: 15px; 
  background: linear-gradient(135deg, 
    rgba(74, 20, 140, 0.2) 0%,
    rgba(74, 20, 140, 0.08) 100%);
  border-left: 4px solid var(--secondary-accent); 
  padding: 18px 20px;
  border-radius: 0 10px 10px 0;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.5),
    0 0 10px rgba(74, 20, 140, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(74, 20, 140, 0.15);
}

/* 预警项背景光效增强 */
.alert-row::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, 
    transparent 0%,
    var(--secondary-accent) 40%,
    var(--secondary-accent) 60%,
    transparent 100%);
  animation: alertGlow 2.5s ease-in-out infinite;
  opacity: 0.8;
}

/* 预警项边框光效 */
.alert-row::after {
  content: '';
  position: absolute;
  left: -4px;
  top: 0;
  right: 0;
  bottom: 0;
  border: 1px solid rgba(74, 20, 140, 0.2);
  border-left: 0;
  border-radius: 0 10px 10px 0;
  pointer-events: none;
  opacity: 0;
  transition: all 0.3s ease;
}

.alert-row:hover::after {
  opacity: 1;
}

@keyframes alertGlow {
  0%, 100% {
    opacity: 0.8;
    box-shadow: 0 0 10px rgba(74, 20, 140, 0.5);
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 20px rgba(74, 20, 140, 0.8);
  }
}

/* 预警项悬停效果增强 */
.alert-row:hover {
  transform: translateX(10px) translateY(-3px) scale(1.02);
  background: linear-gradient(135deg, 
    rgba(74, 20, 140, 0.3) 0%,
    rgba(74, 20, 140, 0.15) 100%);
  box-shadow: 
    0 6px 20px rgba(74, 20, 140, 0.4),
    0 0 15px rgba(74, 20, 140, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  border-color: rgba(74, 20, 140, 0.3);
}

/* 预警项警告样式 */
.alert-row.warning {
  background: linear-gradient(135deg, 
    rgba(230, 81, 0, 0.2) 0%,
    rgba(230, 81, 0, 0.08) 100%);
  border-left-color: var(--quaternary-accent);
  border-color: rgba(230, 81, 0, 0.15);
}

.alert-row.warning::before {
  background: linear-gradient(180deg, 
    transparent 0%,
    var(--quaternary-accent) 40%,
    var(--quaternary-accent) 60%,
    transparent 100%);
}

.alert-row.warning::after {
  border-color: rgba(230, 81, 0, 0.2);
}

.alert-row.warning:hover {
  background: linear-gradient(135deg, 
    rgba(230, 81, 0, 0.3) 0%,
    rgba(230, 81, 0, 0.15) 100%);
  box-shadow: 
    0 6px 20px rgba(230, 81, 0, 0.4),
    0 0 15px rgba(230, 81, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  border-color: rgba(230, 81, 0, 0.3);
}

/* 预警图标深度增强 */
.alert-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, 
    var(--secondary-accent) 0%,
    rgba(74, 20, 140, 0.8) 100%);
  color: #000;
  text-align: center;
  line-height: 36px;
  border-radius: 50%;
  font-weight: bold;
  margin-right: 18px;
  flex-shrink: 0;
  box-shadow: 
    0 4px 15px rgba(74, 20, 140, 0.6),
    inset 0 2px 5px rgba(255, 255, 255, 0.2),
    inset 0 -2px 5px rgba(0, 0, 0, 0.3);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: iconPulse 2s ease-in-out infinite alternate;
}

/* 图标脉冲动画 */
@keyframes iconPulse {
  0% {
    box-shadow: 
      0 4px 15px rgba(74, 20, 140, 0.6),
      inset 0 2px 5px rgba(255, 255, 255, 0.2);
  }
  100% {
    box-shadow: 
      0 6px 25px rgba(74, 20, 140, 0.9),
      inset 0 2px 5px rgba(255, 255, 255, 0.25);
  }
}

.alert-row.warning .alert-icon {
  background: linear-gradient(135deg, 
    var(--quaternary-accent) 0%,
    rgba(230, 81, 0, 0.8) 100%);
  box-shadow: 
    0 4px 15px rgba(230, 81, 0, 0.6),
    inset 0 2px 5px rgba(255, 255, 255, 0.2),
    inset 0 -2px 5px rgba(0, 0, 0, 0.3);
  animation: warningIconPulse 2s ease-in-out infinite alternate;
}

/* 警告图标脉冲动画 */
@keyframes warningIconPulse {
  0% {
    box-shadow: 
      0 4px 15px rgba(230, 81, 0, 0.6),
      inset 0 2px 5px rgba(255, 255, 255, 0.2);
  }
  100% {
    box-shadow: 
      0 6px 25px rgba(230, 81, 0, 0.9),
      inset 0 2px 5px rgba(255, 255, 255, 0.25);
  }
}

.alert-row:hover .alert-icon {
  transform: scale(1.2) rotate(5deg);
}

/* 预警信息布局增强 */
.alert-info {
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.row-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.alert-type {
  color: var(--secondary-accent);
  font-weight: bold;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 0 10px rgba(74, 20, 140, 0.5);
  transition: all 0.3s ease;
}

.alert-row:hover .alert-type {
  text-shadow: 0 0 15px rgba(74, 20, 140, 0.8);
  transform: translateX(2px);
}

.alert-row.warning .alert-type {
  color: var(--quaternary-accent);
  text-shadow: 0 0 10px rgba(230, 81, 0, 0.5);
}

.alert-row.warning:hover .alert-type {
  text-shadow: 0 0 15px rgba(230, 81, 0, 0.8);
}

.alert-time {
  font-family: 'Share Tech Mono';
  color: #aaa;
  font-size: 12px;
  white-space: nowrap;
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.5) 0%,
    rgba(0, 0, 0, 0.3) 100%);
  padding: 4px 10px;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  transition: all 0.3s ease;
}

.alert-row:hover .alert-time {
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.6) 0%,
    rgba(0, 0, 0, 0.4) 100%);
  transform: translateY(-1px);
}

.row-2 {
  font-size: 14px;
  color: #e0e0e0;
  line-height: 1.6;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.3s ease;
  opacity: 0.9;
}

.alert-row:hover .row-2 {
  opacity: 1;
  transform: translateX(2px);
}

/* 空状态深度优化 */
.no-data {
  text-align: center;
  color: var(--tertiary-accent);
  padding: 60px 20px;
  background: linear-gradient(135deg, 
    rgba(27, 94, 32, 0.15) 0%,
    rgba(27, 94, 32, 0.08) 100%);
  border-radius: 12px;
  margin: 20px 0;
  box-shadow: 
    0 5px 20px rgba(0, 0, 0, 0.5),
    0 0 15px rgba(27, 94, 32, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(27, 94, 32, 0.25);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.no-data:hover {
  box-shadow: 
    0 7px 25px rgba(27, 94, 32, 0.3),
    0 0 20px rgba(27, 94, 32, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.safe-shield {
  font-size: 70px;
  margin-bottom: 25px;
  animation: safePulse 2.5s ease-in-out infinite;
  display: block;
  filter: drop-shadow(0 0 20px rgba(27, 94, 32, 0.5));
}

@keyframes safePulse {
  0%, 100% {
    transform: scale(1) rotate(0deg);
    opacity: 0.8;
  }
  25% {
    transform: scale(1.1) rotate(-5deg);
    opacity: 0.9;
  }
  50% {
    transform: scale(1.2) rotate(0deg);
    opacity: 1;
  }
  75% {
    transform: scale(1.1) rotate(5deg);
    opacity: 0.9;
  }
}

.no-data p {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 0 0 15px rgba(27, 94, 32, 0.6);
  background: linear-gradient(90deg, 
    rgba(27, 94, 32, 0.8) 0%,
    rgba(27, 94, 32, 0.6) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 3. 中间 - 地图面板深度增强 */
.map-panel {
  flex: 2;
  background: linear-gradient(135deg, 
    var(--panel-bg) 0%,
    rgba(40, 53, 147, 0.12) 100%);
  border: 1px solid rgba(40, 53, 147, 0.5);
  box-shadow: 
    0 10px 35px rgba(0, 0, 0, 0.7),
    inset 0 0 35px rgba(0, 0, 0, 0.8),
    0 0 0 1px rgba(40, 53, 147, 0.2) inset,
    0 0 25px rgba(40, 53, 147, 0.15);
  position: relative;
  overflow: hidden;
  border-radius: 14px;
}

/* 地图边框光效增强 */
.map-border-glow {
  position: absolute;
  inset: 0;
  box-shadow: 
    inset 0 0 50px rgba(40, 53, 147, 0.2),
    0 0 40px rgba(40, 53, 147, 0.25);
  pointer-events: none;
  border-radius: 14px;
  animation: mapGlow 4.5s ease-in-out infinite;
}

/* 地图边框光效动画 */
@keyframes mapGlow {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
    box-shadow: 
      inset 0 0 40px rgba(40, 53, 147, 0.15),
      0 0 30px rgba(40, 53, 147, 0.2);
  }
  50% {
    opacity: 1;
    transform: scale(1.01);
    box-shadow: 
      inset 0 0 50px rgba(40, 53, 147, 0.25),
      0 0 45px rgba(40, 53, 147, 0.35);
  }
}

/* 地图标题深度优化 */
.center-title {
  text-align: center;
  border: none;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(40, 53, 147, 0.35) 40%,
    rgba(40, 53, 147, 0.35) 60%,
    transparent 100%);
  justify-content: center;
  font-size: 19px;
  letter-spacing: 2px;
  padding: 14px;
  margin: 0 -20px 20px;
  position: relative;
  z-index: 1;
  border-bottom: 1px solid rgba(40, 53, 147, 0.4);
  text-shadow: 0 0 15px rgba(40, 53, 147, 0.7);
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.5),
    0 0 15px rgba(40, 53, 147, 0.2);
  animation: titleGlow 3s ease-in-out infinite alternate;
}

/* 标题发光动画 */
@keyframes titleGlow {
  0% {
    text-shadow: 0 0 15px rgba(40, 53, 147, 0.7);
  }
  100% {
    text-shadow: 0 0 25px rgba(40, 53, 147, 1);
  }
}

/* 地图图表容器深度设计 */
.map-chart-box {
  width: 100%; 
  height: calc(100% - 80px); /* 减去标题和统计信息高度 */
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 
    inset 0 0 25px rgba(0, 0, 0, 0.7),
    inset 0 0 50px rgba(40, 53, 147, 0.1);
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.3) 0%,
    rgba(40, 53, 147, 0.05) 100%);
  margin: 10px 0;
}

/* 地图统计信息深度增强 */
.map-stats {
  position: absolute; 
  bottom: 25px; 
  left: 25px; 
  right: 25px;
  display: flex; 
  justify-content: space-around;
  gap: 25px;
  z-index: 2;
  flex-wrap: wrap;
}

/* 统计项深度设计 */
.stat-item {
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.7) 0%,
    rgba(0, 0, 0, 0.4) 100%);
  padding: 15px 30px;
  border: 1px solid rgba(40, 53, 147, 0.3);
  border-radius: 12px;
  box-shadow: 
    0 5px 18px rgba(0, 0, 0, 0.6),
    0 0 15px rgba(40, 53, 147, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  min-width: 130px;
  flex: 1;
  max-width: 220px;
  position: relative;
  overflow: hidden;
}

/* 统计项光效 */
.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%,
    var(--primary-accent) 50%,
    transparent 100%);
  animation: statBorderGlow 3s ease-in-out infinite;
  opacity: 0.7;
  pointer-events: none;
}

/* 统计项边框发光动画 */
@keyframes statBorderGlow {
  0%, 100% {
    opacity: 0.7;
    transform: translateX(-100%);
  }
  50% {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 统计项悬停效果增强 */
.stat-item:hover {
  transform: translateY(-5px) scale(1.03);
  box-shadow: 
    0 8px 25px rgba(40, 53, 147, 0.45),
    0 0 20px rgba(40, 53, 147, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  border-color: rgba(40, 53, 147, 0.6);
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.75) 0%,
    rgba(0, 0, 0, 0.45) 100%);
}

.stat-item .num {
  font-size: 32px; 
  font-family: 'Share Tech Mono'; 
  font-weight: bold;
  color: var(--primary-accent);
  text-shadow: 
    0 0 20px rgba(40, 53, 147, 0.8),
    0 0 30px rgba(40, 53, 147, 0.5);
  line-height: 1;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.stat-item:hover .num {
  transform: scale(1.1);
  text-shadow: 
    0 0 25px rgba(40, 53, 147, 1),
    0 0 40px rgba(40, 53, 147, 0.7);
}

.stat-item .txt {
  font-size: 12px; 
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-top: 8px;
  text-align: center;
  transition: all 0.3s ease;
  opacity: 0.8;
  font-weight: 500;
}

.stat-item:hover .txt {
  opacity: 1;
  color: #fff;
  text-shadow: 0 0 10px rgba(40, 53, 147, 0.6);
}

.text-green {
  color: var(--tertiary-accent);
  text-shadow: 
    0 0 20px rgba(27, 94, 32, 0.8),
    0 0 30px rgba(27, 94, 32, 0.5);
}

.stat-item:hover .text-green {
  text-shadow: 
    0 0 25px rgba(27, 94, 32, 1),
    0 0 40px rgba(27, 94, 32, 0.7);
}

/* 4. 中间 - 趋势面板增强 */
.trend-panel {
  flex: 1;
}

/* 图表标签增强 */
.chart-tabs {
  display: flex;
  gap: 10px;
  margin-left: auto;
  position: relative;
  z-index: 1;
}

.chart-tabs span {
  cursor: pointer;
  padding: 8px 20px;
  font-size: 14px;
  color: #aaa;
  border: 1px solid rgba(57, 73, 171, 0.3);
  border-radius: 25px;
  background: rgba(0, 0, 0, 0.4);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  font-weight: 500;
  backdrop-filter: blur(5px);
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.4);
}

.chart-tabs span:hover {
  color: var(--primary-accent);
  border-color: var(--primary-accent);
  background: rgba(57, 73, 171, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(57, 73, 171, 0.3);
}

.chart-tabs span.active {
  color: #fff;
  border-color: var(--primary-accent);
  background: rgba(57, 73, 171, 0.35);
  box-shadow: 
    0 4px 15px rgba(57, 73, 171, 0.4),
    inset 0 0 20px rgba(57, 73, 171, 0.15);
  transform: translateY(-2px);
  border-width: 1px;
}

/* 图表容器增强 */
.chart-box {
  width: 100%; 
  height: calc(100% - 50px); /* 减去标题高度 */
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.3) 0%,
    rgba(57, 73, 171, 0.05) 100%);
  box-shadow: 
    inset 0 0 30px rgba(0, 0, 0, 0.7),
    0 0 0 1px rgba(57, 73, 171, 0.15) inset;
}

/* 图表容器顶部光效 */
.chart-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(57, 73, 171, 0.9) 50%,
    transparent 100%);
  animation: chartGlow 4s ease-in-out infinite;
}

/* 图表顶部光效动画 */
@keyframes chartGlow {
  0%, 100% {
    opacity: 0.7;
    transform: translateX(-100%);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.7;
    transform: translateX(100%);
  }
}

/* 5. 右侧 - 体征面板增强 */
.vitals-panel {
  flex: 0 0 250px;
}

/* 添加按钮增强 */
.add-btn {
  background: rgba(57, 73, 171, 0.25);
  border: 1px solid var(--primary-accent);
  color: var(--primary-accent);
  cursor: pointer;
  width: 28px;
  height: 28px;
  line-height: 26px;
  text-align: center;
  border-radius: 50%;
  font-size: 18px;
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.4);
}

.add-btn:hover {
  background: var(--primary-accent);
  color: #000;
  transform: scale(1.15);
  box-shadow: 0 5px 15px rgba(57, 73, 171, 0.5);
}

/* 体征网格布局增强 */
.vitals-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  height: calc(100% - 50px);
  align-items: stretch;
}

/* 体征卡片增强 */
.vital-card {
  text-align: center;
  position: relative;
  background: linear-gradient(135deg, 
    rgba(0, 0, 0, 0.4) 0%,
    rgba(57, 73, 171, 0.08) 100%);
  border-radius: 10px;
  padding: 20px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.5),
    inset 0 0 20px rgba(0, 0, 0, 0.6),
    0 0 0 1px rgba(57, 73, 171, 0.15) inset;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  backdrop-filter: blur(5px);
}

.vital-card:hover {
  transform: translateY(-3px);
  box-shadow: 
    0 6px 20px rgba(57, 73, 171, 0.35),
    inset 0 0 20px rgba(0, 0, 0, 0.6),
    0 0 0 1px rgba(57, 73, 171, 0.3) inset;
}

/* 迷你图表容器 */
.chart-mini {
  height: 120px;
  width: 100%;
  margin-bottom: 15px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
  box-shadow: inset 0 0 15px rgba(0, 0, 0, 0.6);
}

/* 体征数据样式 */
.vital-data {
  position: relative;
  width: 100%;
  text-align: center;
  padding: 12px 0;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
}

.vital-data .label {
  font-size: 12px;
  color: #aaa;
  display: block;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}

.vital-data .value {
  font-size: 32px;
  font-family: 'Share Tech Mono';
  font-weight: bold;
  text-shadow: 0 0 15px rgba(57, 73, 171, 0.6);
}

/* 颜色类优化 */
.text-orange {
  color: var(--quaternary-accent);
}

.text-cyan {
  color: var(--primary-accent);
}

/* 6. 右侧 - 体温 */
.temp-panel { flex: 0 0 150px; }
.temp-layout { display: flex; align-items: center; height: 100%; }
.temp-big { font-family: 'Share Tech Mono'; margin-right: 10px; }
.temp-big .num { font-size: 40px; font-weight: bold; }
.temp-big .unit { font-size: 16px; color: #aaa; }
.temp-chart-wrapper { flex: 1; height: 100px; }

/* 7. 右侧 - 睡眠增强 */
.sleep-panel { flex: 1; }
.sleep-content { display: flex; align-items: center; height: 100%; padding: 0 10px; }
.sleep-visual { 
  width: 100px; 
  height: 100px; 
  margin-right: 20px;
  position: relative;
  animation: sleepVisualEntrance 1s ease-out forwards;
}

/* 睡眠可视化入场动画 */
@keyframes sleepVisualEntrance {
  0% {
    opacity: 0;
    transform: scale(0.8) rotate(-180deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
}

.circular-chart { 
  display: block; 
  margin: 0 auto; 
  max-width: 100%; 
  max-height: 250px; 
  transform: rotate(-90deg); /* 旋转圆环图 */
}
.circle-bg { 
  fill: none; 
  stroke: rgba(255, 255, 255, 0.1); 
  stroke-width: 4; 
  stroke-dasharray: 251.2; /* 2πr, r=40 */
  stroke-dashoffset: 0;
}
.circle { 
  fill: none; 
  stroke-width: 3.5; 
  stroke-linecap: round; 
  stroke: url(#sleepGradient); /* 使用渐变 */
  stroke-dasharray: 251.2; /* 圆环总长 */
  stroke-dashoffset: 251.2; /* 初始隐藏 */
  animation: sleepProgress 2s ease-in-out forwards;
  filter: drop-shadow(0 0 10px rgba(126, 87, 194, 0.5));
}

/* 睡眠进度动画 */
@keyframes sleepProgress {
  0% {
    stroke-dashoffset: 251.2;
  }
  100% {
    stroke-dashoffset: calc(251.2 * (1 - var(--sleep-progress, 0)));
  }
}

/* 睡眠质量渐变定义 */
.sleep-visual::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: conic-gradient(
    from -90deg,
    #8e24aa 0deg 180deg,
    #f57c00 180deg 270deg,
    #43a047 270deg 360deg
  );
  border-radius: 50%;
  opacity: 0.1;
  transform: scale(1.2);
  animation: conicRotate 8s linear infinite;
}

/* 圆锥旋转动画 */
@keyframes conicRotate {
  0% {
    transform: scale(1.2) rotate(0deg);
  }
  100% {
    transform: scale(1.2) rotate(360deg);
  }
}

.percentage { 
  fill: #fff; 
  font-family: 'Share Tech Mono', sans-serif; 
  font-weight: bold; 
  font-size: 0.6em; 
  text-anchor: middle; 
  transform: rotate(90deg); /* 旋转文字回正 */
  text-shadow: 0 0 10px rgba(0, 229, 255, 0.5);
}

.sleep-metrics { 
  flex: 1; 
  display: grid; 
  grid-template-columns: 1fr; 
  gap: 10px; 
  font-size: 13px;
  animation: metricsEntrance 1s ease-out forwards;
  animation-delay: 0.5s;
  opacity: 0;
}

/* 指标入场动画 */
@keyframes metricsEntrance {
  0% {
    opacity: 0;
    transform: translateX(-10px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

.metric { 
  display: flex; 
  justify-content: space-between; 
  padding: 5px 0;
  border-bottom: 1px dashed rgba(0, 229, 255, 0.2);
  transition: all 0.3s ease;
}

/* 指标悬停效果 */
.metric:hover {
  border-color: var(--neon-cyan);
  transform: translateX(5px);
}

.metric .l { color: rgba(255, 255, 255, 0.7); } 
.metric .v { 
  color: var(--neon-cyan); 
  font-family: 'Share Tech Mono'; 
  font-weight: bold;
  text-shadow: 0 0 5px rgba(0, 229, 255, 0.5);
}

/* 动画 */
@keyframes scan { 0% { top: 0; opacity: 1; } 100% { top: 100%; opacity: 0; } }
@keyframes pulse-red { 0% { box-shadow: 0 0 0 0 rgba(255, 71, 87, 0.4); } 70% { box-shadow: 0 0 0 10px rgba(255, 71, 87, 0); } 100% { box-shadow: 0 0 0 0 rgba(255, 71, 87, 0); } }

/* 弹窗样式覆盖 */
:deep(.tech-dialog) { background: rgba(10, 20, 40, 0.95); border: 1px solid var(--neon-cyan); box-shadow: 0 0 20px rgba(0,0,0,0.8); }
:deep(.tech-dialog .el-dialog__title) { color: var(--neon-cyan); }
:deep(.tech-dialog .el-dialog__body) { color: #fff; }
:deep(.dark-form .el-form-item__label) { color: #aaa; }
:deep(.el-input__wrapper), :deep(.el-input-number__decrease), :deep(.el-input-number__increase) { background: #111; box-shadow: none; border: 1px solid #444; color: #fff; }
:deep(.el-input__inner) { color: #fff; }
.tech-btn { width: 100%; padding: 10px; background: var(--neon-cyan); border: none; font-weight: bold; cursor: pointer; color: #000; }
.tech-btn:hover { background: #fff; }
</style>