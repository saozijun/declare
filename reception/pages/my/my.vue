<template>
	<view class="container">
		<!-- 用户信息卡片 -->
		<view class="user-card" v-if="userInfo">
			<view class="user-info">
				<image class="avatar" :src="userInfo.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
				<view class="info-right">
					<text class="nickname">{{userInfo.nickname}}</text>
					<view class="user-tag">{{userInfo.email}}</view>
				</view>
				<view class="edit-btn" @click="editUserInfo">
					<image src="../../static/icons/edit.png" mode="aspectFit"></image>
					<text>编辑</text>
				</view>
			</view>
		</view>
		<!-- 功能菜单 -->
		<view class="menu-list">
			<view class="menu-group">
				<view class="menu-item" @click="navigateTo('/pages/my/project-applications')">
					<view class="menu-left">
						<image src="../../static/icons/i1.png" mode="aspectFit"></image>
						<text>项目信息</text>
					</view>
					<image src="../../static/icons/arrow-right.png" mode="aspectFit" class="arrow"></image>
				</view>
	<!-- 			<view class="menu-item" @click="navigateTo('/pages/my/my-collection')">
					<view class="menu-left">
						<image src="../../static/icons/collection.png" mode="aspectFit"></image>
						<text>项目分配</text>
					</view>
					<image src="../../static/icons/arrow-right.png" mode="aspectFit" class="arrow"></image>
				</view> -->
				<view class="menu-item" @click="navigateTo('/pages/my/chat-list')">
					<view class="menu-left">
						<image src="../../static/icons/i2.png" mode="aspectFit"></image>
						<text>沟通交流</text>
					</view>
					<view class="badge" v-if="unreadCount > 0">{{unreadCount > 99 ? '99+' : unreadCount}}</view>
					<image src="../../static/icons/arrow-right.png" mode="aspectFit" class="arrow"></image>
				</view>
				<view class="menu-item" @click="navigateTo('/pages/my/review-results')">
					<view class="menu-left">
						<image src="../../static/icons/i3.png" mode="aspectFit"></image>
						<text>评审结果</text>
					</view>
					<image src="../../static/icons/arrow-right.png" mode="aspectFit" class="arrow"></image>
				</view>
			</view>
		</view>
		<view class="logout" @click="logout">
			退出登录
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { baseURL } from '../../request/index.js'
import { onShow } from "@dcloudio/uni-app";
import { getUnreadMessageCount } from '../../api/my/chat.js';
// 用户信息
const userInfo = ref(uni.getStorageSync('userInfo') || null)

const unreadCount = ref(0);

// 获取未读消息数量
const fetchUnreadCount = async () => {
	try {
		const res = await getUnreadMessageCount();
		if (res.code === 200) {
			unreadCount.value = res.data || 0;
		}
	} catch (error) {
		console.error('获取未读消息数量失败:', error);
	}
};

onShow(async ()=>{
	userInfo.value = uni.getStorageSync('userInfo') || null;
	fetchUnreadCount();
})
// 编辑用户信息
const editUserInfo = () => {
	uni.navigateTo({
		url: '/pages/my/edit-profile'
	})
}

// 页面跳转
const navigateTo = (url) => {
	uni.navigateTo({ url })
}

const navigateToChat = (userId) => {
	uni.navigateTo({
		url: `/pages/my/chat?userId=${userId}`
	});
};

const logout = ()=>{
	uni.showModal({
		title: '提示',
		content: '是否确认退出登录',
		success: function (res) {
			if (res.confirm) {
				uni.clearStorageSync();
				uni.reLaunch({
					url: '/pages/login/login'
				});
			} 
		}
	});
}
</script>

<style>
.container {
	min-height: 100vh;
	background-color: #f5f6f7;
	padding: 30rpx;
}

/* 用户信息卡片 */
.user-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
}

.user-info {
	display: flex;
	align-items: center;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	margin-right: 24rpx;
}

.info-right {
	flex: 1;
}

.nickname {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 12rpx;
	display: block;
}

.user-tag {
	display: inline-block;
	font-size: 24rpx;
	color: #409eff;
	background: #ecf5ff;
	padding: 4rpx 16rpx;
	border-radius: 6rpx;
}

.edit-btn {
	display: flex;
	align-items: center;
	padding: 12rpx 24rpx;
	background: #f5f6f7;
	border-radius: 30rpx;
	margin-left: 20rpx;
}

.edit-btn image {
	width: 28rpx;
	height: 28rpx;
	margin-right: 8rpx;
}

.edit-btn text {
	font-size: 24rpx;
	color: #666;
}

/* 用户统计 */
.user-stats {
	display: flex;
	justify-content: space-around;
	margin-top: 30rpx;
	padding-top: 30rpx;
	border-top: 1rpx solid #f5f6f7;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.stat-item .num {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

.stat-item .label {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

/* 功能菜单 */
.menu-list {
	margin-bottom: 30rpx;
}

.menu-group {
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
}

.menu-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 40rpx 30rpx;
	border-bottom: 1rpx solid #f5f6f7;
	position: relative;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-left {
	display: flex;
	align-items: center;
	gap: 20rpx;
}
.logout{
	text-align: center;
	font-size: 28rpx;
	color: #999;
	margin: 50rpx;
}

.menu-left image {
	width: 40rpx;
	height: 40rpx;
}

.menu-left text {
	font-size: 32rpx;
	color: #333;
}

.arrow {
	width: 32rpx;
	height: 32rpx;
}

/* 游览打卡样式 */
.visit-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.add-visit {
	width: 130rpx;
	display: flex;
	align-items: center;
}

.add-visit image {
	width: 100%;
	margin-right: 8rpx;
}

.add-visit text {
	font-size: 24rpx;
	color: #409eff;
}

.visit-scroll {
	width: 100%;
}

.visit-list {
	display: flex;
	gap: 20rpx;
	padding: 10rpx 0;
}

.visit-item {
	position: relative;
	flex-shrink: 0;
	width: 240rpx;
	background: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
}

.spot-img {
	width: 100%;
	height: 160rpx;
	object-fit: cover;
}

.visit-info {
	padding: 16rpx;
}

.spot-name {
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}

.visit-time {
	font-size: 24rpx;
	color: #999;
}

.visit-tag {
	position: absolute;
	top: 16rpx;
	right: 16rpx;
	padding: 4rpx 12rpx;
	background: rgba(64, 158, 255, 0.9);
	border-radius: 6rpx;
	font-size: 22rpx;
	color: #fff;
}

.badge {
	background-color: #f56c6c;
	color: #fff;
	border-radius: 20rpx;
	min-width: 32rpx;
	height: 32rpx;
	padding: 0 8rpx;
	font-size: 22rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 10rpx;
	position: absolute;
	right: 60rpx;
}
</style> 