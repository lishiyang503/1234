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

// 办理退房
export const checkoutResident = (id) => {
  return request({
    url: `/resident/checkout/${id}`,
    method: 'put'
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
    url: '/health/add',
    method: 'post',
    data
  })
}

// 更新健康档案
export const updateHealthRecord = (data) => {
  return request({
    url: '/health/update',
    method: 'put',
    data
  })
}

// 获取健康档案详情
export const getHealthRecordDetail = (id) => {
  return request({
    url: `/health/detail/${id}`,
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