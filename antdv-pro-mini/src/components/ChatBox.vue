<template>
  <div class="chat-box" :class="{ 'popup-mode': isPopup }">
    <!-- 聊天头部 -->
    <div class="chat-header" v-if="isPopup">
      <div class="close-btn" @click="$emit('close')">
        <span class="close-icon">×</span>
      </div>
      <div class="user-info">
        <div class="avatar">
          {{ chatUser.nickname ? chatUser.nickname.substring(0, 1) : 'U' }}
        </div>
        <div class="user-details">
          <div class="user-name">{{ chatUser.nickname || '用户' }}</div>
          <div class="user-role">{{ chatUser.role === 'expert' ? '专家' : '申报人' }}</div>
        </div>
      </div>
    </div>
    
    <!-- 聊天内容区域 -->
    <div class="chat-content" ref="chatContentRef" @scroll="handleScroll">
      <div class="loading-more" v-if="isLoading">
        <div class="loading-spinner"></div>
        <span>加载更多消息...</span>
      </div>
      
      <template v-if="messages.length > 0">
        <div v-for="(dateGroup, dateKey) in groupedMessages" :key="dateKey" class="date-group">
          <!-- 日期分割线 -->
          <div class="date-divider">
            <span>{{ dateKey }}</span>
          </div>
          
          <!-- 消息列表 -->
          <div 
            v-for="(msg, msgIndex) in dateGroup" 
            :key="msg.id"
            :id="`msg-${msg.id}`"
            class="message-row"
            :class="{ 'message-self': msg.senderId === currentUserId }"
          >
            <!-- 时间戳 -->
            <div class="time-stamp" v-if="shouldShowTime(dateGroup, msgIndex)">
              <span>{{ formatTime(msg.createTime) }}</span>
            </div>
            
            <!-- 接收的消息 -->
            <div v-if="msg.senderId !== currentUserId" class="message-received">
              <div class="avatar">
                {{ chatUser.nickname ? chatUser.nickname.substring(0, 1) : 'U' }}
              </div>
              <div class="message-content">
                <div class="sender-name">{{ chatUser.nickname || '用户' }}</div>
                <div class="bubble">
                  <div class="text">{{ msg.content }}</div>
                </div>
              </div>
            </div>
            
            <!-- 发送的消息 -->
            <div v-else class="message-sent">
              <div class="message-content">
                <div class="bubble">
                  <div class="text">{{ msg.content }}</div>
                </div>
                <div class="message-status">{{ msg.isRead ? '已读' : '已发送' }}</div>
              </div>
              <div class="avatar">
                {{ userInfo.nickname ? userInfo.nickname.substring(0, 1) : 'E' }}
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <!-- 无消息提示 -->
      <div class="empty-messages" v-if="messages.length === 0 && !isLoading">
        <div class="empty-icon">💬</div>
        <p>暂无消息，发送第一条消息开始聊天吧</p>
      </div>
    </div>
    
    <!-- 输入框区域 -->
    <div class="chat-input">
      <div class="input-container">
        <textarea 
          v-model="messageText" 
          placeholder="输入消息..." 
          rows="1"
          @input="autoResizeTextarea"
          @keydown.enter.prevent="sendMessage"
          ref="textareaRef"
        ></textarea>
      </div>
      <button 
        :class="['send-button', { 'active': messageText.trim() }]"
        :disabled="!messageText.trim()"
        @click="sendMessage"
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20">
          <path d="M3.4 20.4l17.45-7.48a1 1 0 000-1.84L3.4 3.6a.993.993 0 00-1.39.91L2 9.12c0 .5.37.93.87.99L17 12 2.87 13.88c-.5.07-.87.5-.87 1l.01 4.61c0 .71.73 1.2 1.39.91z" fill="currentColor"></path>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue';
import { message } from 'ant-design-vue';
import { getChatMessages, sendChatMessage, getChatUserInfo } from '~/api/chat';

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  },
  isPopup: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['close', 'message-sent']);

const chatUser = ref({});
const messages = ref([]);
const messageText = ref('');
const isLoading = ref(false);
const currentPage = ref(1);
const pageSize = ref(20);
const hasMore = ref(true);
const chatContentRef = ref(null);
const textareaRef = ref(null);
const currentUserId = computed(() => props.userInfo.id);

// 自动调整文本区域高度
const autoResizeTextarea = () => {
  const textarea = textareaRef.value;
  if (!textarea) return;
  
  textarea.style.height = 'auto';
  const newHeight = Math.min(Math.max(textarea.scrollHeight, 24), 120);
  textarea.style.height = `${newHeight}px`;
};

// 滚动处理，用于检测滚动到顶部时加载更多
const handleScroll = (e) => {
  if (e.target.scrollTop < 50 && hasMore.value && !isLoading.value) {
    loadMoreMessages();
  }
};

// 按日期分组消息
const groupedMessages = computed(() => {
  const groups = {};
  
  messages.value.forEach(msg => {
    const date = new Date(msg.createTime);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    
    let dateKey;
    if (date.toDateString() === today.toDateString()) {
      dateKey = '今天';
    } else if (date.toDateString() === yesterday.toDateString()) {
      dateKey = '昨天';
    } else {
      dateKey = `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
    }
    
    if (!groups[dateKey]) {
      groups[dateKey] = [];
    }
    
    groups[dateKey].push(msg);
  });
  
  // 确保每个日期组内消息按时间顺序排列
  for (const dateKey in groups) {
    groups[dateKey].sort((a, b) => new Date(a.createTime) - new Date(b.createTime));
  }
  
  return groups;
});

// 获取聊天对象信息
const fetchChatUserInfo = async () => {
  try {
    isLoading.value = true;
    const { data } = await getChatUserInfo(props.userId);
    if (data) {
      chatUser.value = data;
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    message.error('获取用户信息失败');
  } finally {
    isLoading.value = false;
  }
};

// 获取聊天消息
const fetchChatMessages = async (reset = false) => {
  try {
    if (isLoading.value) return;
    isLoading.value = true;
    
    if (reset) {
      currentPage.value = 1;
      hasMore.value = true;
      messages.value = [];
    }
    
    if (!hasMore.value) {
      isLoading.value = false;
      return;
    }
    
    const { data } = await getChatMessages({
      userId: props.userId,
      currentUserId: currentUserId.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    });
    
    if (data && data.records) {
      const newMessages = data.records || [];
      
      if (reset) {
        messages.value = newMessages;
      } else {
        // 保存旧滚动高度和位置
        const scrollElement = chatContentRef.value;
        // 确保scrollElement存在
        if (scrollElement) {
          const oldScrollHeight = scrollElement.scrollHeight;
          const oldScrollTop = scrollElement.scrollTop;
          
          messages.value = [...newMessages, ...messages.value];
          
          // 在DOM更新后恢复滚动位置
          nextTick(() => {
            if (scrollElement) {
              const newScrollHeight = scrollElement.scrollHeight;
              scrollElement.scrollTop = oldScrollTop + (newScrollHeight - oldScrollHeight);
            }
          });
        } else {
          // 如果scrollElement不存在，只更新消息
          messages.value = [...newMessages, ...messages.value];
        }
      }
      
      hasMore.value = newMessages.length === pageSize.value;
      currentPage.value++;
      
      if (reset) {
        nextTick(() => {
          scrollToBottom();
        });
      }
    }
  } catch (error) {
    console.error('获取消息失败:', error);
    message.error('获取消息失败');
  } finally {
    isLoading.value = false;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!messageText.value.trim()) return;
  
  const content = messageText.value.trim();
  messageText.value = '';
  
  // 重置输入框高度
  if (textareaRef.value) {
    textareaRef.value.style.height = 'auto';
  }
  
  const tempMessage = {
    id: Date.now(),
    senderId: currentUserId.value,
    receiverId: props.userId,
    content: content,
    createTime: new Date().toISOString(),
    isRead: false
  };
  
  // 先添加到本地消息列表
  messages.value.push(tempMessage);
  
  // 滚动到底部
  nextTick(() => {
    scrollToBottom();
  });
  
  try {
    // 发送消息到服务器
    const { data } = await sendChatMessage({
      receiverId: props.userId,
      senderId: currentUserId.value,
      content: content
    });
    
    // 发送成功后通知父组件
    emit('message-sent', {
      message: tempMessage,
      response: data
    });
  } catch (error) {
    console.error('发送消息失败:', error);
    message.error('发送消息失败');
  }
};

// 加载更多消息
const loadMoreMessages = () => {
  if (hasMore.value && !isLoading.value) {
    fetchChatMessages(false);
  }
};

// 滚动到底部
const scrollToBottom = () => {
  // 确保DOM更新后再滚动
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight;
      
      // 双重保险：如果第一次滚动不到位，再试一次
      setTimeout(() => {
        if (chatContentRef.value) {
          chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight;
        }
      }, 100);
    }
  });
};

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  
  const date = new Date(timestamp);
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 是否显示时间戳
const shouldShowTime = (messages, index) => {
  // 第一条消息总是显示时间
  if (index === 0) return true;
  
  const currentMsg = messages[index];
  const prevMsg = messages[index - 1];
  const currentTime = new Date(currentMsg.createTime);
  const prevTime = new Date(prevMsg.createTime);
  
  // 如果两条消息间隔超过5分钟，显示时间
  return (currentTime - prevTime) > 5 * 60 * 1000;
};

// 监听用户ID变化，重新加载消息
watch(() => props.userId, (newId) => {
  if (newId) {
    fetchChatUserInfo();
    // 使用延时确保组件完全渲染
    setTimeout(() => {
      fetchChatMessages(true);
    }, 50);
  }
}, { immediate: true });

// 导出方法供父组件调用
defineExpose({
  fetchChatMessages,
  scrollToBottom
});

onMounted(() => {
  if (props.userId) {
    fetchChatUserInfo();
    // 使用延时确保DOM完全渲染
    setTimeout(() => {
      fetchChatMessages(true);
    }, 50);
  }
});
</script>

<style lang="less" scoped>
.chat-box {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f9fafc;
  overflow: hidden;
  
  &.popup-mode {
    height: 70vh;
    width: 100%;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f5;
}

.close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  margin-right: 12px;
  cursor: pointer;
  border-radius: 50%;
  
  &:hover {
    background-color: #f5f5f5;
  }
  
  .close-icon {
    font-size: 20px;
    line-height: 1;
    color: #909399;
  }
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.avatar {
  width: 40px;
  height: 40px;
  background-color: #4c6ef5;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 500;
}

.user-details {
  margin-left: 12px;
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.12);
    border-radius: 4px;
  }
}

.loading-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
  
  .loading-spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgba(0, 0, 0, 0.1);
    border-left-color: #4c6ef5;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
  
  span {
    margin-left: 8px;
    color: #909399;
    font-size: 13px;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.date-divider {
  text-align: center;
  margin: 20px 0;
  
  span {
    padding: 4px 16px;
    background-color: rgba(0, 0, 0, 0.04);
    color: #909399;
    font-size: 12px;
    border-radius: 16px;
  }
}

.message-row {
  margin-bottom: 16px;
}

.time-stamp {
  text-align: center;
  margin: 10px 0;
  
  span {
    font-size: 12px;
    padding: 2px 10px;
    color: #909399;
    background: rgba(0, 0, 0, 0.03);
    border-radius: 10px;
  }
}

.message-received {
  display: flex;
  align-items: flex-start;
  margin-right: 60px;
  
  .avatar {
    width: 36px;
    height: 36px;
    flex-shrink: 0;
  }
  
  .message-content {
    margin-left: 12px;
    max-width: calc(100% - 48px);
  }
  
  .sender-name {
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
    margin-left: 12px;
  }
  
  .bubble {
    background-color: #fff;
    padding: 12px 16px;
    border-radius: 18px 18px 18px 4px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    position: relative;
  }
}

.message-sent {
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  margin-left: 60px;
  
  .avatar {
    width: 36px;
    height: 36px;
    flex-shrink: 0;
    margin-left: 12px;
  }
  
  .message-content {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    max-width: calc(100% - 48px);
  }
  
  .bubble {
    background-color: #4c6ef5;
    color: #fff;
    padding: 12px 16px;
    border-radius: 18px 18px 4px 18px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    position: relative;
  }
  
  .message-status {
    font-size: 12px;
    color: #909399;
    margin: 4px 8px;
  }
}

.text {
  word-break: break-word;
  line-height: 1.5;
  font-size: 14px;
}

.empty-messages {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 50%;
  
  .empty-icon {
    font-size: 40px;
    margin-bottom: 16px;
    color: #c0c4cc;
  }
  
  p {
    color: #909399;
    font-size: 14px;
    text-align: center;
  }
}

.chat-input {
  padding: 16px;
  background-color: #fff;
  border-top: 1px solid #f0f0f5;
  display: flex;
  align-items: flex-end;
}

.input-container {
  flex: 1;
  margin-right: 12px;
  
  textarea {
    width: 100%;
    background-color: #f5f7fa;
    border: none;
    border-radius: 20px;
    padding: 10px 16px;
    resize: none;
    outline: none;
    font-size: 14px;
    line-height: 1.5;
    min-height: 40px;
    max-height: 120px;
    transition: all 0.2s;
    
    &:focus {
      background-color: #f0f2f5;
      box-shadow: 0 0 0 2px rgba(76, 110, 245, 0.1);
    }
    
    &::placeholder {
      color: #909399;
    }
  }
}

.send-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f2f5;
  border: none;
  color: #c0c4cc;
  cursor: pointer;
  transition: all 0.2s;
  
  &.active {
    background-color: #4c6ef5;
    color: white;
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
</style> 