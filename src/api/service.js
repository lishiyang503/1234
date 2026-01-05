import request from '@/utils/request'

// 服务套餐相关API
// 获取服务套餐列表
export const getServicePackages = (params) => {
  return request({
    url: '/service/packages',
    method: 'get',
    params
  })
}

// 添加服务套餐
export const addServicePackage = (data) => {
  return request({
    url: '/service/package/add',
    method: 'post',
    data
  })
}

// 编辑服务套餐
export const updateServicePackage = (data) => {
  return request({
    url: '/service/package/update',
    method: 'put',
    data
  })
}

// 删除服务套餐
export const deleteServicePackage = (id) => {
  return request({
    url: `/service/package/delete/${id}`,
    method: 'delete'
  })
}

// 更新服务套餐状态
export const updateServicePackageStatus = (id, status) => {
  return request({
    url: `/service/package/status/${id}`,
    method: 'put',
    params: { status }
  })
}

// 服务购买记录相关API
// 获取服务购买记录列表
export const getPurchaseRecords = (params) => {
  return request({
    url: '/service/purchase/records',
    method: 'get',
    params
  })
}