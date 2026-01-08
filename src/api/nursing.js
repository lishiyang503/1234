import request from '@/utils/request'

// 护理级别管理相关API
// 获取护理级别列表
export const getNursingLevels = (params) => {
  return request({
    url: '/nursing/level/list',
    method: 'get',
    params
  })
}

// 添加护理级别
export const addNursingLevel = (data) => {
  return request({
    url: '/nursing/level/add',
    method: 'post',
    data
  })
}

// 更新护理级别
export const updateNursingLevel = (data) => {
  return request({
    url: '/nursing/level/update',
    method: 'put',
    data
  })
}

// 删除护理级别
export const deleteNursingLevel = (id) => {
  return request({
    url: `/nursing/level/delete/${id}`,
    method: 'delete'
  })
}

// 护理内容管理相关API

// 获取护理内容列表
export const getNursingContents = (params) => {
  return request({
    url: '/nursing/content/list',
    method: 'get',
    params
  })
}

// 添加护理内容
export const addNursingContent = (data) => {
  return request({
    url: '/nursing/content/add',
    method: 'post',
    data
  })
}

// 更新护理内容
export const updateNursingContent = (data) => {
  return request({
    url: '/nursing/content/update',
    method: 'put',
    data
  })
}

// 删除护理内容
export const deleteNursingContent = (id) => {
  return request({
    url: `/nursing/content/delete/${id}`,
    method: 'delete'
  })
}

// 护理记录管理相关API

// 获取护理记录列表
export const getNursingRecords = (params) => {
  return request({
    url: '/nursing/record/list',
    method: 'get',
    params
  })
}

// 添加护理记录
export const addNursingRecord = (data) => {
  return request({
    url: '/nursing/record/add',
    method: 'post',
    data
  })
}

// 更新护理记录
export const updateNursingRecord = (data) => {
  // 【修复】直接发送所有数据，不要手动过滤字段，否则编辑其他信息（如备注、姓名）会不生效
  return request({
    url: '/nursing/record/update',
    method: 'put',
    data: data
  })
}

// 删除护理记录
export const deleteNursingRecord = (id) => {
  return request({
    url: `/nursing/record/delete/${id}`,
    method: 'delete'
  })
}

// 获取护理记录详情
export const getNursingRecordDetail = (id) => {
  return request({
    url: `/nursing/record/detail/${id}`,
    method: 'get'
  })
}
