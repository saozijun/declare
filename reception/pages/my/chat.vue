<template>
  <view class="container">
    <!-- 聊天头部 -->
    <view class="chat-header">
      <view class="user-info">
        <image class="user-avatar" :src="chatUser.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
        <view class="user-details">
          <text class="user-name">{{chatUser.nickname || '聊天'}}</text>
        </view>
      </view>
    </view>
    
    <scroll-view 
      scroll-y 
      class="message-list" 
      :scroll-top="scrollTop"
      :scroll-into-view="latestMessageId"
      @scrolltoupper="loadMoreMessages"
      upper-threshold="50"
      ref="messageScroll"
    >
      <!-- 加载提示 -->
      <view class="loading-more" v-if="isLoading">
        <view class="loading-indicator"></view>
        <text>加载更多消息...</text>
      </view>
      
      <view class="messages-container">
        <!-- 消息列表 -->
        <block v-if="messages.length > 0">
          <!-- 日期分组 -->
          <view v-for="(dateGroup, dateKey) in groupedMessages" :key="dateKey" class="date-group">
            <!-- 日期标签 -->
            <view class="date-divider">
              <text>{{dateKey}}</text>
            </view>
            
            <!-- 该日期下的消息 -->
            <view 
              v-for="(msg, msgIndex) in dateGroup" 
              :key="msg.id"
              :id="'msg-'+msg.id"
              class="message-row"
              :class="{ 'message-self': msg.senderId === currentUserId }"
            >
              <!-- 消息时间 -->
              <view class="time-stamp" v-if="shouldShowTime(dateGroup, msgIndex)">
                <text>{{formatTime(msg.createTime)}}</text>
              </view>
              
              <!-- 接收的消息 -->
              <view v-if="msg.senderId !== currentUserId" class="message-received">
                <image class="avatar" :src="chatUser.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
                <view class="message-content">
                  <text class="sender-name">{{chatUser.nickname || '用户'}}</text>
                  <view class="bubble">
                    <text>{{msg.content}}</text>
                  </view>
                </view>
              </view>
              
              <!-- 发送的消息 -->
              <view v-else class="message-sent">
                <view class="message-content">
                  <view class="bubble">
                    <text>{{msg.content}}</text>
                  </view>
                  <text class="message-status">{{msg.isRead ? '已读' : '已发送'}}</text>
                </view>
                <image class="avatar" :src="userInfo.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
              </view>
            </view>
          </view>
        </block>
        
        <!-- 无消息提示 -->
        <view class="empty-messages" v-if="messages.length === 0 && !isLoading">
          <text>暂无消息，发送第一条消息开始聊天吧</text>
        </view>
      </view>
    </scroll-view>
    
    <!-- 输入区域 -->
    <view class="message-input-container">
      <view class="message-input">
        <input 
          type="text" 
          v-model="messageText" 
          placeholder="输入消息..." 
          confirm-type="send"
          @confirm="sendMessage"
          @focus="scrollToBottom"
        />
        <button class="send-btn" @click="sendMessage" :disabled="!messageText">发送</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue';
import { onLoad, onShow, onUnload } from "@dcloudio/uni-app";
import { getChatMessages, sendChatMessage, getChatUserInfo } from '../../api/my/chat.js';

const userId = ref(null);
const chatUser = ref({});
const messages = ref([]);
const messageText = ref('');
const scrollTop = ref(0);
const latestMessageId = ref('');
const isLoading = ref(false);
const currentPage = ref(1);
const pageSize = ref(20);
const hasMore = ref(true);
const userInfo = ref(uni.getStorageSync('userInfo') || {});
const currentUserId = computed(() => userInfo.value?.id);

// 将消息按日期分组
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

let socketTask = null;

// 获取聊天对象信息
const fetchChatUserInfo = async () => {
  try {
    if (!userId.value) return;
    
    const res = await getChatUserInfo(userId.value);
    if (res.code === 200) {
      chatUser.value = res.data || {};
      // 设置页面标题
      uni.setNavigationBarTitle({
        title: chatUser.value.nickname || '聊天'
      });
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

// 获取聊天消息
const fetchChatMessages = async (reset = false) => {
  try {
    if (!userId.value) return;
    if (isLoading.value) return;
    
    isLoading.value = true;
    
    if (reset) {
      currentPage.value = 1;
      hasMore.value = true;
    }
    
    if (!hasMore.value) {
      isLoading.value = false;
      return;
    }
    
    const res = await getChatMessages(userId.value, currentPage.value, pageSize.value);
    if (res.code === 200) {
      const newMessages = res.data.records || [];
      
      if (reset) {
        messages.value = newMessages;
      } else {
        messages.value = [...newMessages, ...messages.value];
      }
      
      hasMore.value = newMessages.length === pageSize.value;
      
      if (newMessages.length > 0 && reset) {
        latestMessageId.value = 'msg-' + newMessages[newMessages.length - 1].id;
        nextTick(() => {
          scrollToBottom();
        });
      }
      
      currentPage.value++;
    }
  } catch (error) {
    console.error('获取消息失败:', error);
  } finally {
    isLoading.value = false;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!messageText.value.trim()) return;
  
  const tempMessage = {
    id: Date.now(),
    senderId: currentUserId.value,
    receiverId: userId.value,
    content: messageText.value.trim(),
    createTime: new Date().toISOString(),
    isRead: false
  };
  
  messages.value.push(tempMessage);
  latestMessageId.value = 'msg-' + tempMessage.id;
  
  nextTick(() => {
    scrollToBottom();
    messageText.value = '';
  });
  
  try {
    const res = await sendChatMessage({
      receiverId: userId.value,
      content: tempMessage.content
    });
    
    if (res.code !== 200) {
      uni.showToast({
        title: '消息发送失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    uni.showToast({
      title: '网络错误',
      icon: 'none'
    });
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
  const query = uni.createSelectorQuery().in(this);
  query.select('.message-list').boundingClientRect();
  query.exec(function(res) {
    if (res && res[0]) {
      scrollTop.value = res[0].height * 2; // 确保滚动到底部
    }
  });
};

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  
  const date = new Date(timestamp);
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 决定是否显示时间
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

// 初始化WebSocket
const initWebSocket = () => {
  if (!userId.value || !currentUserId.value) return;
  
  const token = uni.getStorageSync('token');
  socketTask = uni.connectSocket({
    url: `ws://your-websocket-url/chat?userId=${currentUserId.value}&token=${token}`,
    success: () => {
      console.log('WebSocket连接成功');
    }
  });
  
  socketTask.onOpen(() => {
    console.log('WebSocket已打开');
  });
  
  socketTask.onMessage((res) => {
    const message = JSON.parse(res.data);
    if (message.senderId == userId.value) {
      messages.value.push(message);
      latestMessageId.value = 'msg-' + message.id;
      nextTick(() => {
        scrollToBottom();
      });
    }
  });
  
  socketTask.onClose(() => {
    console.log('WebSocket已关闭');
  });
  
  socketTask.onError((error) => {
    console.error('WebSocket错误:', error);
  });
};

// 关闭WebSocket
const closeWebSocket = () => {
  if (socketTask) {
    socketTask.close();
    socketTask = null;
  }
};

// 返回按钮
const goBack = () => {
  uni.navigateBack();
};

onLoad((options) => {
  if (options && options.userId) {
    userId.value = options.userId;
    fetchChatUserInfo();
    fetchChatMessages(true);
    // initWebSocket(); // 取消注释以启用WebSocket
  }
});

onShow(() => {
  userInfo.value = uni.getStorageSync('userInfo') || {};
});

onUnload(() => {
  closeWebSocket();
});
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f0f2f5;
}

/* 头部样式 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #409EFF;
  color: #fff;
  box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
  z-index: 10;
}

.back-btn {
  padding: 10rpx 20rpx;
  margin-right: 10rpx;
}

.icon {
  font-size: 40rpx;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  border: 2rpx solid rgba(255,255,255,0.5);
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 6rpx;
}

.user-status {
  font-size: 24rpx;
  opacity: 0.8;
}

/* 消息列表样式 */
.message-list {
  flex: 1;
  background-color: #f5f7fa;
  padding: 0 20rpx;
  position: relative;
  box-sizing: border-box;
}

.messages-container {
  padding: 20rpx 0;
  display: flex;
  flex-direction: column;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
  color: #999;
  font-size: 24rpx;
}

.loading-indicator {
  width: 30rpx;
  height: 30rpx;
  border: 3rpx solid #409EFF;
  border-radius: 50%;
  border-top-color: transparent;
  margin-right: 10rpx;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 日期分组样式 */
.date-group {
  margin-bottom: 30rpx;
}

.date-divider {
  text-align: center;
  margin: 30rpx 0 20rpx;
}

.date-divider text {
  background-color: rgba(0, 0, 0, 0.05);
  color: #666;
  font-size: 24rpx;
  padding: 8rpx 30rpx;
  border-radius: 20rpx;
}

/* 消息行样式 */
.message-row {
  margin-bottom: 20rpx;
  width: 100%;
}

.time-stamp {
  text-align: center;
  margin: 16rpx 0;
}

.time-stamp text {
  background-color: rgba(0, 0, 0, 0.05);
  color: #666;
  font-size: 22rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

/* 接收的消息样式 */
.message-received {
  display: flex;
  align-items: flex-start;
  margin-right: 60rpx;
}

.message-received .avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.message-received .message-content {
  display: flex;
  flex-direction: column;
  max-width: calc(100% - 100rpx);
}

.message-received .sender-name {
  font-size: 24rpx;
  color: #606266;
  margin-bottom: 6rpx;
  padding-left: 10rpx;
}

.message-received .bubble {
  background-color: #ffffff;
  border-radius: 18rpx;
  border-top-left-radius: 4rpx;
  padding: 20rpx 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
  position: relative;
  align-self: flex-start;
}

/* 发送的消息样式 */
.message-sent {
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  margin-left: 60rpx;
}

.message-sent .avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-left: 16rpx;
  flex-shrink: 0;
}

.message-sent .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  max-width: calc(100% - 100rpx);
}

.message-sent .bubble {
  background-color: #95ec69;
  border-radius: 18rpx;
  border-top-right-radius: 4rpx;
  padding: 20rpx 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
  position: relative;
  align-self: flex-end;
}

.message-sent .message-status {
  font-size: 22rpx;
  color: #909399;
  margin-top: 8rpx;
  margin-right: 10rpx;
}

.bubble text {
  font-size: 28rpx;
  line-height: 1.5;
  word-break: break-word;
}

/* 无消息提示样式 */
.empty-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60%;
  color: #909399;
  font-size: 28rpx;
  text-align: center;
}

/* 消息输入区域样式 */
.message-input-container {
  padding: 20rpx;
  background-color: #fff;
  border-top: 1rpx solid #e0e0e0;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.message-input {
  display: flex;
  align-items: center;
}

.message-input input {
  flex: 1;
  height: 80rpx;
  padding: 0 40rpx;
  background-color: #f5f5f5;
  border-radius: 150rpx;
  font-size: 28rpx;
}

.send-btn {
  margin-left: 20rpx;
  background-color: #409EFF;
  color: #fff;
  font-size: 28rpx;
  padding: 0 40rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 150rpx;
}

.send-btn[disabled] {
  background-color: #cccccc;
  color: #ffffff;
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .container {
    background-color: #1a1a1a;
  }
  
  .chat-header {
    background-color: #333;
  }
  
  .message-received .bubble {
    background-color: #333;
    color: #fff;
  }
  
  .message-sent .bubble {
    background-color: #076e00;
  }
  
  .date-divider text, .time-stamp text {
    background-color: rgba(255, 255, 255, 0.1);
    color: #ccc;
  }
  
  .message-input-container, .message-input {
    background-color: #222;
  }
  
  .message-input input {
    background-color: #333;
    color: #fff;
  }
}
</style> 