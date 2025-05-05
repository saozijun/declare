<template>
  <div class="chat-list-page">
    <div class="side-panel">
      <div class="panel-header">
        <h2>消息列表</h2>
      </div>
      <div v-if="chatUsers.length > 0" class="user-list">
        <div 
          v-for="user in chatUsers" 
          :key="user.id" 
          class="user-item"
          :class="{ 'active': selectedUserId === user.id }"
          @click="selectUser(user)"
        >
          <div class="avatar-container">
            <div class="avatar">
              {{ user.nickname ? user.nickname.substring(0, 1) : 'U' }}
            </div>
            <span v-if="user.unreadCount" class="badge">{{ user.unreadCount > 99 ? '99+' : user.unreadCount }}</span>
          </div>
          <div class="user-info">
            <div class="name-time">
              <div class="user-name">{{ user.nickname || '用户' }}</div>
              <div class="last-time">{{ formatTime(user.lastMessageTime) }}</div>
            </div>
            <div class="last-message">{{ user.lastMessage || '暂无消息' }}</div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon">📩</div>
        <p>暂无聊天记录</p>
      </div>
    </div>
    
    <div class="chat-panel" v-if="selectedUserId">
      <div class="panel-header">
        <h2>{{ selectedUser?.nickname || '用户' }}</h2>
      </div>
      <ChatBox 
        :key="selectedUserId"
        :userId="selectedUserId" 
        :userInfo="userInfo"
        @message-sent="handleMessageSent"
        ref="chatBoxRef"
      />
    </div>
    
    <div class="empty-chat" v-else>
      <div class="empty-icon">💬</div>
      <p>选择一个联系人开始聊天</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import ChatBox from '~/components/ChatBox.vue';
import { getChatUserList } from '~/api/chat';
import { useUserStore } from '~/stores/user';

const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo);
const loading = ref(false);
const chatUsers = ref([]);
const selectedUserId = ref(null);
const selectedUser = ref(null);
const chatBoxRef = ref(null);

// 获取聊天用户列表
const fetchChatUsers = async () => {
  try {
    loading.value = true;
    const { data } = await getChatUserList({ 
      currentUserId: userInfo.value.id 
    });
    console.log(data,'data');
    
    if (data) {
      chatUsers.value = data;
      
      // 自动选择有未读消息的第一个用户
      const userWithUnread = chatUsers.value.find(user => user.unreadCount > 0);
      if (userWithUnread) {
        selectUser(userWithUnread);
      }
    }
  } catch (error) {
    console.error('获取聊天列表失败:', error);
    message.error('获取聊天列表失败');
  } finally {
    loading.value = false;
  }
};

// 选择用户
const selectUser = (user) => {
  selectedUserId.value = user.id;
  selectedUser.value = user;
  
  // 使用nextTick等待DOM更新，确保chatBoxRef已经挂载
  nextTick(() => {
    // 如果已经加载了聊天组件，手动刷新消息
    if (chatBoxRef.value) {
      chatBoxRef.value.fetchChatMessages(true);
      chatBoxRef.value.scrollToBottom();
    }
  });
};

// 处理发送消息事件
const handleMessageSent = (msgData) => {
  // 更新聊天列表中的最新消息
  const userIndex = chatUsers.value.findIndex(u => u.id === selectedUserId.value);
  if (userIndex !== -1) {
    chatUsers.value[userIndex].lastMessage = msgData.message.content;
    chatUsers.value[userIndex].lastMessageTime = msgData.message.createTime;
    
    // 将这个用户移到列表顶部
    const user = chatUsers.value.splice(userIndex, 1)[0];
    chatUsers.value.unshift(user);
  }
};

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  
  const date = new Date(timestamp);
  const now = new Date();
  const isToday = now.toDateString() === date.toDateString();
  
  if (isToday) {
    return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  }
  
  // 昨天
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (yesterday.toDateString() === date.toDateString()) {
    return `昨天 ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  }
  
  // 其他日期
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

onMounted(() => {
  if (userInfo.value && userInfo.value.id) {
    fetchChatUsers();
  } else {
    message.error('获取用户信息失败，请重新登录');
  }
});
</script>

<style lang="less" scoped>
.chat-list-page {
  display: flex;
  height: calc(100vh - 170px);
  gap: 24px;
  background-color: #f9f9fb;
}

.side-panel {
  width: 320px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f5;
  
  h2 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #111;
  }
}

.user-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.12);
    border-radius: 4px;
  }
}

.user-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 6px;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f4f6f8;
  }
  
  &.active {
    background-color: #f0f7ff;
  }
}

.avatar-container {
  position: relative;
  margin-right: 14px;
}

.avatar {
  width: 46px;
  height: 46px;
  background-color: #4c6ef5;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 10px;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}

.user-info {
  flex: 1;
  overflow: hidden;
}

.name-time {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.user-name {
  font-weight: 500;
  font-size: 15px;
  color: #303133;
}

.last-time {
  color: #909399;
  font-size: 12px;
}

.last-message {
  color: #606266;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 220px;
}

.chat-panel {
  flex: 1;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.empty-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 16px;
  color: #c0c4cc;
}

.empty-state p,
.empty-chat p {
  color: #909399;
  font-size: 14px;
  text-align: center;
}
</style> 