<template>
  <view class="container">
    <view class="page-header">
      <text class="title">聊天列表</text>
    </view>
    
    <scroll-view scroll-y class="chat-list-scroll">
      <view v-if="chatUsers.length > 0">
        <view 
          class="chat-user-item" 
          v-for="(user, index) in chatUsers" 
          :key="index"
          @click="navigateToChat(user.id)"
        >
          <image class="user-avatar" :src="user.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
          <view class="user-info">
            <view class="user-name-time">
              <text class="user-name">{{user.nickname}}</text>
              <text class="last-time">{{formatTime(user.lastMessageTime)}}</text>
            </view>
            <text class="last-message">{{user.lastMessage || '暂无消息'}}</text>
          </view>
          <view class="unread-badge" v-if="user.unreadCount > 0">
            <text>{{user.unreadCount > 99 ? '99+' : user.unreadCount}}</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-tip">
        <text>暂无聊天记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onShow } from "@dcloudio/uni-app";
import { getChatUsers } from '../../api/my/chat.js';

const chatUsers = ref([]);

// 获取聊天列表
const fetchChatUsers = async () => {
  try {
    const res = await getChatUsers();
    if (res.code === 200) {
      chatUsers.value = res.data || [];
    } else {
      uni.showToast({
        title: res.msg || '获取聊天列表失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取聊天列表失败:', error);
    uni.showToast({
      title: '网络错误',
      icon: 'none'
    });
  }
};

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  
  const now = new Date();
  const msgDate = new Date(timestamp);
  
  // 判断是否是今天
  if (now.toDateString() === msgDate.toDateString()) {
    return `${String(msgDate.getHours()).padStart(2, '0')}:${String(msgDate.getMinutes()).padStart(2, '0')}`;
  }
  
  // 判断是否是昨天
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (yesterday.toDateString() === msgDate.toDateString()) {
    return `昨天 ${String(msgDate.getHours()).padStart(2, '0')}:${String(msgDate.getMinutes()).padStart(2, '0')}`;
  }
  
  // 其他日期显示完整日期
  return `${msgDate.getFullYear()}-${String(msgDate.getMonth() + 1).padStart(2, '0')}-${String(msgDate.getDate()).padStart(2, '0')}`;
};

// 跳转到聊天页面
const navigateToChat = (userId) => {
  uni.navigateTo({
    url: `/pages/my/chat?userId=${userId}`
  });
};

onShow(() => {
  fetchChatUsers();
});

onMounted(() => {
  fetchChatUsers();
});
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f6f7;
}

.page-header {
  background-color: #409EFF;
  padding: 30rpx;
  color: #fff;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
}

.chat-list-scroll {
  flex: 1;
}

.chat-user-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 24rpx;
}

.user-info {
  flex: 1;
  overflow: hidden;
}

.user-name-time {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.user-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.last-time {
  font-size: 24rpx;
  color: #999;
}

.last-message {
  font-size: 28rpx;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 450rpx;
}

.unread-badge {
  background-color: #f56c6c;
  color: #fff;
  border-radius: 30rpx;
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  right: 40rpx;
  bottom: 40rpx;
}

.empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300rpx;
  color: #909399;
  font-size: 28rpx;
}
</style> 