import request from '@/utils/request'

// 获取所有房间
export function getAllRooms() {
  return request({
    url: '/rooms/all',
    method: 'get'
  })
}

// 根据ID获取房间
export function getRoomById(roomId) {
  return request({
    url: `/rooms/${roomId}`,
    method: 'get'
  })
}

// 获取可用房间
export function getAvailableRooms() {
  return request({
    url: '/rooms/available',
    method: 'get'
  })
}

// 获取维修中的房间
export function getRepairRooms() {
  return request({
    url: '/rooms/repair',
    method: 'get'
  })
}

// 条件查询房间
export function searchRooms(params) {
  return request({
    url: '/rooms/search',
    method: 'get',
    params
  })
}

// 添加房间
export function addRoom(data) {
  return request({
    url: '/rooms',
    method: 'post',
    data
  })
}

// 更新房间
export function updateRoom(data) {
  return request({
    url: '/rooms',
    method: 'put',
    data
  })
}

// 删除房间
export function deleteRoom(roomId) {
  return request({
    url: `/rooms/${roomId}`,
    method: 'delete'
  })
}

// 设置维修状态
export function setRoomRepair(roomId, data) {
  return request({
    url: `/rooms/repair/${roomId}`,
    method: 'post',
    data
  })
}

// 完成维修
export function completeRepair(roomId) {
  return request({
    url: `/rooms/repair/complete/${roomId}`,
    method: 'put'
  })
}

// 更新房间状态
export function updateRoomStatus(roomId, status) {
  return request({
    url: `/rooms/status/${roomId}`,
    method: 'put',
    params: { status }
  })
}