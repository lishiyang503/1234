import request from '@/utils/request'

// 入住登记相关API
// 获取入住登记列表
export const getResidentList = (params) => {
  return request({
    url: '/resident/list',
    method: 'get',
    params
  })
}

// 添加入住登记
export const addResident = (data) => {
  return request({
    url: '/resident/add',
    method: 'post',
    data
  })
}

// 编辑入住登记
export const updateResident = (data) => {
  return request({
    url: '/resident/update',
    method: 'put',
    data
  })
}

// 删除入住登记
export const deleteResident = (id) => {
  return request({
    url: `/resident/delete/${id}`,
    method: 'delete'
  })
}

// 办理退房（通过更新入住登记的状态实现）
export const checkoutResident = (id) => {
  return request({
    url: `/resident/update`,
    method: 'put',
    data: {
      id: id,
      status: '退房',
      exitDate: new Date()
    }
  })
}

// 健康档案相关API
// 获取健康档案列表
export const getHealthRecords = (params) => {
  return request({
    url: '/health/list',
    method: 'get',
    params
  })
}

// 添加健康档案
export const addHealthRecord = (data) => {
  return request({
    url: '/health',
    method: 'post',
    data
  })
}

// 更新健康档案
export const updateHealthRecord = (data) => {
  return request({
    url: '/health',
    method: 'put',
    data
  })
}

// 获取健康档案详情
export const getHealthRecordDetail = (id) => {
  return request({
    url: `/health/${id}`,
    method: 'get'
  })
}

// 入住历史相关API
// 获取入住历史列表
export const getResidentHistory = (params) => {
  return request({
    url: '/resident/history',
    method: 'get',
    params
  })
}

// 获取所有已登记的老人列表
export const getAllResidents = () => {
  return request({
    url: '/resident/all',
    method: 'get'
  })
}

// 老人出院操作
export const dischargeResident = (residentId, reason, operator) => {
  return request({
    url: '/resident/discharge',
    method: 'post',
    params: {
      residentId,
      reason,
      operator
    }
  })
}

// 健康监测相关API
// 血压记录API
// 获取血压记录列表
export const getBloodPressureRecords = (params) => {
  return request({
    url: '/health-monitoring/blood-pressure',
    method: 'get',
    params
  })
}

// 添加血压记录
export const addBloodPressureRecord = (data) => {
  return request({
    url: '/health-monitoring/blood-pressure',
    method: 'post',
    data
  })
}

// 血糖记录API
// 获取血糖记录列表
export const getBloodSugarRecords = (params) => {
  return request({
    url: '/health-monitoring/blood-sugar',
    method: 'get',
    params
  })
}

// 添加血糖记录
export const addBloodSugarRecord = (data) => {
  return request({
    url: '/health-monitoring/blood-sugar',
    method: 'post',
    data
  })
}

// 心率记录API
// 获取心率记录列表
export const getHeartRateRecords = (params) => {
  return request({
    url: '/health-monitoring/heart-rate',
    method: 'get',
    params
  })
}

// 添加心率记录
export const addHeartRateRecord = (data) => {
  return request({
    url: '/health-monitoring/heart-rate',
    method: 'post',
    data
  })
}

// 获取健康预警
export const getHealthAlerts = (params) => {
  return request({
    url: '/health-monitoring/alerts',
    method: 'get',
    params
  })
}