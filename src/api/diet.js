import request from '@/utils/request'

// 获取膳食需求列表
export const getDietList = (params) => {
  return request({
    url: '/diet/list',
    method: 'get',
    params
  })
}

// 添加膳食需求
export const addDiet = (data) => {
  return request({
    url: '/diet/add',
    method: 'post',
    data
  })
}

// 编辑膳食需求
export const updateDiet = (data) => {
  return request({
    url: '/diet/update',
    method: 'put',
    data
  })
}

// 删除膳食需求
export const deleteDiet = (id) => {
  return request({
    url: `/diet/delete/${id}`,
    method: 'delete'
  })
}