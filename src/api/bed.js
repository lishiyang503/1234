import request from '@/utils/request'

// 根据房间ID获取床位列表
export function getBedsByRoomId(roomId) {
  return request({
    url: `/beds/${roomId}`,
    method: 'get'
  })
}

// 获取所有床位
export function getAllBeds() {
  return request({
    url: '/beds/all',
    method: 'get'
  })
}

// 根据ID获取床位
// 已修改为通过resident_id查询，为保持向后兼容保留原方法名
export function getBedById(residentId) {
  return request({
    url: '/beds/by-resident',
    method: 'get',
    params: { residentId }
  })
}

// 根据老人ID获取床位（新方法名，推荐使用）
export function getBedByResidentId(residentId) {
  return request({
    url: '/beds/by-resident',
    method: 'get',
    params: { residentId }
  })
}

// 添加床位
export function addBed(data) {
  return request({
    url: '/beds',
    method: 'post',
    data
  })
}

// 更新床位
export function updateBed(data) {
  return request({
    url: '/beds',
    method: 'put',
    data
  })
}

// 删除床位
export function deleteBed(bedId) {
  return request({
    url: '/beds',
    method: 'delete',
    params: { id: bedId }
  })
}

// 修复重复的resident_id问题
export function fixDuplicateResident() {
  return request({
    url: '/beds/fix-duplicate-resident',
    method: 'post'
  })
}