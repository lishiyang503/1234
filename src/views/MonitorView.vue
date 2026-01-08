<template>
  <div class="monitor-container">
    <div class="header-bar">
      <div class="decoration-line left"></div>
      <div class="title-box">
        <h1>智慧健康监测中心</h1>
        <p>Intelligent Health Monitoring Platform</p>
      </div>
      <div class="decoration-line right"></div>
      <div class="time-display">{{ currentTime }}</div>
    </div>

    <div class="main-grid">
      
      <div class="side-panel left-panel">
        <div class="panel-box profile-card">
          <div class="box-header">监测对象</div>
          <div class="profile-content">
            <div class="avatar-ring">
              <div class="avatar">李</div>
              <div class="status-badge normal">正常</div>
            </div>
            <div class="info-rows">
              <div class="info-row">
                <span class="label">姓名:</span>
                <span class="value">李秀英</span>
              </div>
              <div class="info-row">
                <span class="label">年龄:</span>
                <span class="value">76 岁</span>
              </div>
              <div class="info-row">
                <span class="label">风险等级:</span>
                <span class="value text-low">低风险</span>
              </div>
              <div class="info-row">
                <span class="label">设备电量:</span>
                <span class="value text-mid">82%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="panel-box control-box">
          <div class="box-header">数据筛选</div>
          <div class="form-item">
            <label>数据类型</label>
            <select v-model="dataType" class="glass-select">
              <option value="all">综合视图</option>
              <option value="bp">血压趋势</option>
              <option value="hr">心率监测</option>
            </select>
          </div>
          <div class="form-item">
            <label>时间范围</label>
            <div class="date-range-mock">
              <span>2024-01-01</span>
              <span class="arrow">→</span>
              <span>2024-01-07</span>
            </div>
          </div>
          <button class="btn-query" @click="refreshData">
            <span class="icon">🔍</span> 立即查询
          </button>
        </div>

        <div class="panel-box log-box">
          <div class="box-header">实时告警日志</div>
          <ul class="log-list">
            <li v-for="(log, i) in logs" :key="i" class="log-item">
              <span class="log-time">{{ log.time }}</span>
              <span class="log-content" :class="log.type">{{ log.text }}</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="center-panel">
        <div class="map-container">
          <div class="map-header">
            <h3>📍 实时位置与活动轨迹 (LBS)</h3>
            <div class="map-tags">
              <span class="tag active">安全围栏</span>
              <span class="tag">历史轨迹</span>
            </div>
          </div>
          <div ref="mapChart" class="echarts-map"></div>
          <div class="corner t-l"></div><div class="corner t-r"></div>
          <div class="corner b-l"></div><div class="corner b-r"></div>
        </div>

        <div class="trend-container">
          <div class="trend-header">
            <h3>📈 72小时 生理体征多维分析</h3>
          </div>
          <div ref="trendChart" class="echarts-trend"></div>
        </div>
      </div>

      <div class="side-panel right-panel">
        <div class="panel-box gauge-box">
          <div class="box-header">实时体征快照</div>
          
          <div class="gauge-grid">
            <div class="gauge-item">
              <div ref="hrGauge" class="mini-chart"></div>
              <div class="gauge-label">心率 (BPM)</div>
            </div>
            <div class="gauge-item">
              <div ref="spo2Gauge" class="mini-chart"></div>
              <div class="gauge-label">血氧 (%)</div>
            </div>
          </div>
        </div>

        <div class="panel-box sleep-box">
          <div class="box-header">睡眠质量分析</div>
          <div class="sleep-stats">
            <div class="stat-big">
              7.5<small>h</small>
              <span>总睡眠</span>
            </div>
            <div class="stat-bar-group">
              <div class="bar-label">深睡 (25%)</div>
              <div class="progress"><div class="fill deep" style="width: 25%"></div></div>
              <div class="bar-label">浅睡 (55%)</div>
              <div class="progress"><div class="fill light" style="width: 55%"></div></div>
            </div>
          </div>
        </div>
        
        <div class="panel-box advice-box">
          <div class="box-header">AI 护理建议</div>
          <div class="advice-content">
            <p>💡 血压在晨间略有波动，建议保持清淡饮食。今日运动量达标，请继续保持。</p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';

// 状态
const currentTime = ref(new Date().toLocaleString());
const dataType = ref('all');
const logs = ref([
  { time: '10:23', text: '心率稍快 (105 bpm)', type: 'warn' },
  { time: '08:00', text: '服药提醒: 降压药', type: 'info' },
  { time: '07:30', text: '起床活动', type: 'success' },
  { time: '06:15', text: '睡眠监测结束', type: 'info' },
]);

// DOM Refs
const mapChart = ref(null);
const trendChart = ref(null);
const hrGauge = ref(null);
const spo2Gauge = ref(null);

let charts = [];
let timer = null;

// --- 1. 初始化 GIS 轨迹图 (模拟 LBS) ---
const initMapChart = () => {
  if (!mapChart.value) return;
  const chart = echarts.init(mapChart.value);
  charts.push(chart);

  // 模拟 GPS 坐标点 (x, y)
  const traceData = Array.from({length: 20}, (_, i) => [
    50 + Math.cos(i/3) * 20 + Math.random() * 5, 
    50 + Math.sin(i/3) * 20 + Math.random() * 5
  ]);

  const option = {
    backgroundColor: 'transparent',
    grid: { top: 30, bottom: 30, left: 30, right: 30 },
    xAxis: { show: false, min: 0, max: 100 },
    yAxis: { show: false, min: 0, max: 100 },
    series: [
      {
        type: 'scatter',
        symbolSize: 10,
        data: traceData,
        itemStyle: { color: 'rgba(59, 130, 246, 0.6)' }
      },
      {
        name: '当前位置',
        type: 'effectScatter',
        coordinateSystem: 'cartesian2d',
        data: [traceData[traceData.length - 1]], // 最后一个点
        symbolSize: 20,
        rippleEffect: { brushType: 'stroke', scale: 4 },
        itemStyle: { color: '#4ade80' },
        label: { show: true, formatter: '当前位置', position: 'right', color: '#fff' }
      },
      {
        type: 'line',
        smooth: true,
        data: traceData,
        lineStyle: { color: '#3b82f6', width: 2, type: 'dashed' }
      },
      // 安全围栏 (圆形)
      {
        type: 'pie',
        radius: ['0%', '0%'], // 占位，实际应该用 markArea 或 graphic，这里为了简单用 pie 占位或忽略
        markArea: {
          silent: true,
          itemStyle: { color: 'rgba(16, 185, 129, 0.1)', borderWidth: 1, borderColor: 'rgba(16, 185, 129, 0.3)', borderType: 'dashed' },
          data: [[{ coord: [20, 20] }, { coord: [80, 80] }]]
        }
      }
    ]
  };
  chart.setOption(option);
};

// --- 2. 初始化趋势图 (Rich Data) ---
const initTrendChart = () => {
  if (!trendChart.value) return;
  const chart = echarts.init(trendChart.value);
  charts.push(chart);

  const dates = ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00'];
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(17, 24, 39, 0.9)', borderColor: '#374151', textStyle: { color: '#fff' } },
    legend: { data: ['收缩压', '舒张压', '体温'], textStyle: { color: '#9ca3af' }, top: 0 },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: dates, axisLine: { lineStyle: { color: '#374151' } } },
    yAxis: [
      { type: 'value', name: 'mmHg', position: 'left', splitLine: { lineStyle: { color: 'rgba(255,255,255,0.05)' } } },
      { type: 'value', name: '°C', position: 'right', min: 35, max: 39, splitLine: { show: false } }
    ],
    series: [
      {
        name: '收缩压', type: 'line', smooth: true,
        data: [120, 125, 122, 130, 128, 124, 121],
        itemStyle: { color: '#f87171' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0,0,0,1, [{offset:0, color:'rgba(248,113,113,0.3)'}, {offset:1, color:'rgba(0,0,0,0)'}]) }
      },
      {
        name: '舒张压', type: 'line', smooth: true,
        data: [80, 82, 79, 85, 83, 80, 78],
        itemStyle: { color: '#fbbf24' }
      },
      {
        name: '体温', type: 'bar', yAxisIndex: 1, barWidth: 10,
        data: [36.5, 36.6, 36.8, 37.0, 36.7, 36.6, 36.5],
        itemStyle: { color: '#3b82f6', borderRadius: [5,5,0,0] }
      }
    ]
  };
  chart.setOption(option);
};

// --- 3. 初始化仪表盘 ---
const initGauge = (dom, title, value, color) => {
  if (!dom) return;
  const chart = echarts.init(dom);
  charts.push(chart);
  
  const option = {
    series: [{
      type: 'gauge',
      startAngle: 180, endAngle: 0,
      min: 0, max: title === '心率' ? 150 : 100,
      splitNumber: 5,
      itemStyle: { color: color, shadowColor: color, shadowBlur: 10 },
      progress: { show: true, roundCap: true, width: 8 },
      pointer: { icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z', length: '12%', width: 10, offsetCenter: [0, '-60%'], itemStyle: { color: 'auto' } },
      axisLine: { roundCap: true, lineStyle: { width: 8, color: [[1, 'rgba(255,255,255,0.1)']] } },
      axisTick: { show: false }, splitLine: { show: false }, axisLabel: { show: false },
      title: { show: false },
      detail: { 
        backgroundColor: 'rgba(255,255,255,0.05)', borderRadius: 8,
        offsetCenter: [0, '20%'], valueAnimation: true, 
        formatter: '{value}', color: '#fff', fontSize: 20 
      },
      data: [{ value: value }]
    }]
  };
  chart.setOption(option);
};

const refreshData = () => {
  // 模拟数据刷新动画
  initTrendChart();
  logs.value.unshift({ time: new Date().toLocaleTimeString('en-GB').slice(0,5), text: '数据手动更新成功', type: 'success' });
};

const handleResize = () => charts.forEach(c => c.resize());

onMounted(() => {
  setTimeout(() => {
    initMapChart();
    initTrendChart();
    initGauge(hrGauge.value, '心率', 78, '#34d399');
    initGauge(spo2Gauge.value, '血氧', 98, '#3b82f6');
  }, 100);
  
  window.addEventListener('resize', handleResize);
  timer = setInterval(() => currentTime.value = new Date().toLocaleString(), 1000);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  clearInterval(timer);
  charts.forEach(c => c.dispose());
});
</script>

<style scoped>
/* --- 全局布局 --- */
.monitor-container {
  min-height: 100vh;
  background-color: #0b1120; /* 科技黑底 */
  background-image: radial-gradient(circle at 50% 50%, rgba(30, 58, 138, 0.15), transparent 60%);
  color: #fff;
  font-family: 'Inter', sans-serif;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* --- 顶部标题栏 --- */
.header-bar {
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  background: linear-gradient(to bottom, rgba(15,23,42,0.9), rgba(15,23,42,0));
  border-bottom: 1px solid rgba(59, 130, 246, 0.2);
}
.title-box { text-align: center; }
.title-box h1 { margin: 0; font-size: 26px; font-weight: 700; letter-spacing: 2px; text-shadow: 0 0 10px rgba(59, 130, 246, 0.5); }
.title-box p { margin: 2px 0 0; font-size: 10px; color: #64748b; text-transform: uppercase; letter-spacing: 4px; }
.time-display { position: absolute; right: 24px; font-family: 'Courier New', monospace; color: #3b82f6; font-size: 16px; font-weight: bold; }
.decoration-line { height: 2px; width: 100px; background: linear-gradient(90deg, transparent, #3b82f6, transparent); position: absolute; bottom: 0; }
.left { left: 20%; } .right { right: 20%; }

/* --- 主网格 --- */
.main-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 280px 1fr 280px;
  gap: 20px;
  padding: 20px;
}

/* --- 通用面板样式 (DataV 风格) --- */
.panel-box {
  background: rgba(17, 24, 39, 0.6);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 20px;
  position: relative;
  box-shadow: 0 0 15px rgba(0,0,0,0.3) inset;
}
/* DataV 角标装饰 */
.panel-box::before { content: ''; position: absolute; top: -1px; left: -1px; width: 10px; height: 10px; border-top: 2px solid #3b82f6; border-left: 2px solid #3b82f6; }
.panel-box::after { content: ''; position: absolute; bottom: -1px; right: -1px; width: 10px; height: 10px; border-bottom: 2px solid #3b82f6; border-right: 2px solid #3b82f6; }

.box-header {
  color: #93c5fd; font-size: 14px; font-weight: 600; margin-bottom: 12px; padding-left: 8px; border-left: 3px solid #3b82f6;
}

/* --- 左侧面板 --- */
.profile-content { display: flex; flex-direction: column; align-items: center; gap: 16px; }
.avatar-ring { 
  width: 70px; height: 70px; border-radius: 50%; border: 2px solid #3b82f6; 
  display: flex; align-items: center; justify-content: center; position: relative;
  box-shadow: 0 0 15px rgba(59, 130, 246, 0.4);
}
.avatar { font-size: 24px; font-weight: bold; }
.status-badge { 
  position: absolute; bottom: -5px; background: #10b981; font-size: 10px; padding: 2px 6px; 
  border-radius: 10px; border: 2px solid #0b1120;
}
.info-rows { width: 100%; }
.info-row { display: flex; justify-content: space-between; font-size: 13px; padding: 6px 0; border-bottom: 1px dashed rgba(255,255,255,0.1); }
.info-row .label { color: #94a3b8; }
.text-mid { color: #facc15; } .text-low { color: #10b981; }

.control-box .form-item { margin-bottom: 12px; }
.control-box label { display: block; font-size: 12px; color: #64748b; margin-bottom: 4px; }
.glass-select { 
  width: 100%; background: rgba(0,0,0,0.3); border: 1px solid rgba(255,255,255,0.1); 
  color: #fff; padding: 8px; border-radius: 4px; outline: none; 
}
.date-range-mock { 
  display: flex; justify-content: space-between; align-items: center; background: rgba(0,0,0,0.3); 
  padding: 8px; border-radius: 4px; border: 1px solid rgba(255,255,255,0.1); font-size: 12px;
}
.btn-query { 
  width: 100%; background: linear-gradient(90deg, #2563eb, #1d4ed8); border: none; padding: 10px; 
  color: #fff; border-radius: 4px; cursor: pointer; transition: 0.3s; margin-top: 8px;
}
.btn-query:hover { box-shadow: 0 0 10px rgba(37, 99, 235, 0.5); }

.log-list { list-style: none; padding: 0; margin: 0; max-height: 150px; overflow-y: auto; }
.log-item { display: flex; gap: 10px; font-size: 12px; padding: 6px 0; }
.log-time { color: #64748b; font-family: monospace; }
.warn { color: #f87171; } .info { color: #93c5fd; } .success { color: #34d399; }

/* --- 中间面板 --- */
.center-panel { display: flex; flex-direction: column; gap: 20px; }
.map-container { 
  flex: 1.5; background: rgba(17, 24, 39, 0.4); border: 1px solid rgba(255,255,255,0.05); 
  position: relative; border-radius: 8px; overflow: hidden; display: flex; flex-direction: column;
}
.map-header { 
  position: absolute; top: 16px; left: 16px; z-index: 10; display: flex; flex-direction: column; gap: 8px;
}
.map-header h3 { margin: 0; font-size: 16px; text-shadow: 0 2px 4px rgba(0,0,0,0.8); }
.map-tags .tag { 
  font-size: 10px; padding: 2px 8px; background: rgba(0,0,0,0.5); 
  border: 1px solid rgba(255,255,255,0.2); border-radius: 10px; margin-right: 6px; 
}
.tag.active { background: #3b82f6; border-color: #3b82f6; }
.echarts-map { flex: 1; width: 100%; min-height: 300px; }

/* 地图装饰角 */
.corner { position: absolute; width: 20px; height: 20px; border: 2px solid rgba(59, 130, 246, 0.5); }
.t-l { top: 10px; left: 10px; border-right: none; border-bottom: none; }
.t-r { top: 10px; right: 10px; border-left: none; border-bottom: none; }
.b-l { bottom: 10px; left: 10px; border-right: none; border-top: none; }
.b-r { bottom: 10px; right: 10px; border-left: none; border-top: none; }

.trend-container { flex: 1; background: rgba(17, 24, 39, 0.4); border-radius: 8px; border: 1px solid rgba(255,255,255,0.05); padding: 16px; display: flex; flex-direction: column; }
.trend-header h3 { margin: 0 0 10px 0; font-size: 15px; color: #e2e8f0; }
.echarts-trend { flex: 1; min-height: 200px; }

/* --- 右侧面板 --- */
.gauge-grid { display: flex; justify-content: space-between; }
.gauge-item { width: 48%; text-align: center; }
.mini-chart { height: 120px; width: 100%; }
.gauge-label { font-size: 12px; color: #94a3b8; margin-top: -10px; }

.sleep-stats { padding: 8px 0; }
.stat-big { font-size: 32px; font-weight: bold; color: #a78bfa; text-align: center; margin-bottom: 12px; }
.stat-big small { font-size: 14px; color: #64748b; margin-left: 4px; }
.stat-big span { display: block; font-size: 12px; color: #94a3b8; font-weight: normal; margin-top: -4px; }
.stat-bar-group { display: flex; flex-direction: column; gap: 6px; }
.bar-label { font-size: 10px; color: #94a3b8; }
.progress { height: 6px; background: rgba(255,255,255,0.1); border-radius: 3px; overflow: hidden; }
.fill { height: 100%; border-radius: 3px; }
.fill.deep { background: #8b5cf6; } .fill.light { background: #60a5fa; }

.advice-content { font-size: 12px; color: #cbd5e1; line-height: 1.5; background: rgba(59, 130, 246, 0.1); padding: 10px; border-radius: 4px; border-left: 3px solid #facc15; }

/* 响应式 */
@media (max-width: 1200px) {
  .main-grid { grid-template-columns: 1fr; }
  .side-panel { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
  .map-container { min-height: 400px; }
}
</style>