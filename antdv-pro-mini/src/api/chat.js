
// 获取聊天用户列表
export function getChatUserList(params) {
  return useGet('/chat/users', params);
}

// 获取聊天用户信息
export function getChatUserInfo(userId) {
  return useGet(`/user/info/${userId}`);
}

// 获取与指定用户的聊天记录
export function getChatMessages(params) {
  return useGet('/chat/messages', params);
}

// 发送聊天消息
export function sendChatMessage(data) {
  return usePost('/chat/send', data);
}

// 标记消息为已读
export function markMessagesAsRead(data) {
  return usePost('/chat/read', data);
}

// 获取未读消息数量
export function getUnreadMessageCount(userId) {
  return useGet('/chat/unread/count', { userId });
} 