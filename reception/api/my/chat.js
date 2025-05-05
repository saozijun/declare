import { request } from '../../request/index.js';

// 获取聊天用户列表
export function getChatUsers() {
  const userInfo = uni.getStorageSync('userInfo') || {};
  return request({
    url: '/chat/users',
    method: 'get',
    data: {
      currentUserId: userInfo.id
    }
  });
}

// 获取聊天用户信息
export function getChatUserInfo(userId) {
  return request({
    url: `/user/info/${userId}`,
    method: 'get'
  });
}

// 获取与指定用户的聊天记录
export function getChatMessages(userId, pageNum = 1, pageSize = 20) {
  const userInfo = uni.getStorageSync('userInfo') || {};
  return request({
    url: '/chat/messages',
    method: 'get',
    data: {
      userId,
      currentUserId: userInfo.id,
      pageNum,
      pageSize
    }
  });
}

// 发送聊天消息
export function sendChatMessage(data) {
  const userInfo = uni.getStorageSync('userInfo') || {};
  return request({
    url: '/chat/send',
    method: 'post',
    data: {
      ...data,
      senderId: userInfo.id
    }
  });
}

// 标记消息为已读
export function markMessagesAsRead(userId) {
  const userInfo = uni.getStorageSync('userInfo') || {};
  return request({
    url: '/chat/read',
    method: 'post',
    data: {
      userId,
      currentUserId: userInfo.id
    }
  });
}

// 获取未读消息数量
export function getUnreadMessageCount() {
  const userInfo = uni.getStorageSync('userInfo') || {};
  return request({
    url: '/chat/unread/count',
    method: 'get',
    data: {
      userId: userInfo.id
    }
  });
} 