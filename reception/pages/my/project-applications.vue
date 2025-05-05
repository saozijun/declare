<template>
	<view class="container">
		<!-- 页面头部 -->
		<view class="page-header">
			<view class="header-content">
				<text class="title">项目申报记录</text>
			</view>
		</view>
		
		<!-- 主内容区域 -->
		<scroll-view scroll-y class="content-scroll">
			<view class="application-list-container">
				<!-- 项目列表 -->
				<view class="project-list" v-if="projects.length > 0">
					<view class="project-card" v-for="(item, index) in projects" :key="index" @click="viewDetail(item)">
						<view class="project-header">
							<text class="project-name">{{item.projectName}}</text>
							<view :class="['status-tag', getStatusClass(item.status)]">{{getStatusText(item.status)}}</view>
						</view>
						<view class="project-divider"></view>
						<view class="project-info">
							<view class="info-row">
								<text class="info-icon cuIcon-company"></text>
								<text class="info-label">申报单位:</text>
								<text class="info-value">{{item.organization}}</text>
							</view>
							<view class="info-row">
								<text class="info-icon cuIcon-friendfill"></text>
								<text class="info-label">负责人:</text>
								<text class="info-value">{{item.leader}}</text>
							</view>
							<view class="info-row">
								<text class="info-icon cuIcon-moneybag"></text>
								<text class="info-label">预算:</text>
								<text class="info-value">{{item.budget || '-'}} 元</text>
							</view>
							<view class="info-row">
								<text class="info-icon cuIcon-calendar"></text>
								<text class="info-label">项目周期:</text>
								<text class="info-value">{{item.period || '-'}}</text>
							</view>
							<view class="info-row">
								<text class="info-icon cuIcon-time"></text>
								<text class="info-label">申报时间:</text>
								<text class="info-value">{{formatDate(item.createTime)}}</text>
							</view>
							<view class="info-row" v-if="item.updateTime">
								<text class="info-icon cuIcon-check"></text>
								<text class="info-label">审核时间:</text>
								<text class="info-value">{{formatDate(item.updateTime)}}</text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 空状态 -->
				<view class="empty-state" v-else>
					<image src="../../static/icons/no-data.png" mode="aspectFit" class="empty-icon"></image>
					<text class="empty-text">暂无申报记录</text>
					<text class="empty-tips">您可以前往"项目申报"页面提交新的项目</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getProjectApplications } from '../../api/my/applications.js';

const projects = ref([]);
const userInfo = ref(uni.getStorageSync('userInfo') || null);

// 获取项目列表
const fetchProjects = async () => {
	try {
		const userId = userInfo.value.id;
		if (!userId) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			});
			return;
		}
		
		const res = await getProjectApplications(userId);
		
		if (res.code == 200) {
			projects.value = res.data.records || [];
		} else {
			uni.showToast({
				title: res.msg || '获取项目列表失败',
				icon: 'none'
			});
		}
	} catch (error) {
		console.error(error);
		uni.showToast({
			title: '网络错误',
			icon: 'none'
		});
	}
};

// 格式化日期
const formatDate = (dateStr) => {
	if (!dateStr) return '-';
	const date = new Date(dateStr);
	return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 获取状态文本
const getStatusText = (status) => {
	switch (status) {
		case '0': return '待审核';
		case '1': return '已通过';
		case '2': return '已拒绝';
		case '3': return '已分配';
		case '4': return '已评审';
		default: return '未知状态';
	}
};

// 获取状态样式类
const getStatusClass = (status) => {
	switch (status) {
		case '0': return 'status-pending';
		case '1': return 'status-approved';
		case '2': return 'status-rejected';
		case '3': return 'status-assigned';
		case '4': return 'status-reviewed';
		default: return '';
	}
};

// 查看详情
const viewDetail = (item) => {
	uni.navigateTo({
		url: `/pages/my/project-detail?id=${item.id}`
	});
};

onMounted(() => {
	fetchProjects();
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
	padding: 40rpx 30rpx 50rpx;
	color: #fff;
	position: relative;
	z-index: 1;
}

.header-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.title {
	font-size: 38rpx;
	font-weight: bold;
	color: #fff;
}

.content-scroll {
	flex: 1;
}

.application-list-container {
	padding: 30rpx;
	padding-bottom: 50rpx;
}

.project-list {
	display: flex;
	flex-direction: column;
	gap: 24rpx;
}

.project-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 0;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.project-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx 30rpx;
}

.project-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	flex: 1;
}

.project-divider {
	height: 1px;
	background-color: #f0f0f0;
}

.project-info {
	padding: 20rpx 30rpx;
	padding-bottom: 40rpx;
}

.info-row {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.info-row:last-child {
	margin-bottom: 0;
}

.info-icon {
	font-size: 28rpx;
	color: #409EFF;
	margin-right: 10rpx;
}

.info-label {
	font-size: 26rpx;
	color: #909399;
	width: 140rpx;
}

.info-value {
	font-size: 26rpx;
	color: #606266;
	flex: 1;
}

.project-footer {
	padding: 20rpx 30rpx;
	display: flex;
	justify-content: flex-end;
}

.view-detail-btn {
	color: #409EFF;
	font-size: 26rpx;
	display: flex;
	align-items: center;
}

.arrow-icon {
	margin-left: 6rpx;
}

.status-tag {
	padding: 6rpx 20rpx;
	border-radius: 30rpx;
	font-size: 24rpx;
	color: #fff;
	margin-left: 20rpx;
	white-space: nowrap;
}

.status-pending {
	background-color: rgba(144, 147, 153, 0.9);
}

.status-approved {
	background-color: rgba(103, 194, 58, 0.9);
}

.status-rejected {
	background-color: rgba(245, 108, 108, 0.9);
}

.status-assigned {
	background-color: rgba(64, 158, 255, 0.7);
}

.status-reviewed {
	background-color: rgba(230, 162, 60, 0.9);
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 32rpx;
	color: #909399;
	margin-bottom: 16rpx;
}

.empty-tips {
	font-size: 26rpx;
	color: #c0c4cc;
}
</style> 