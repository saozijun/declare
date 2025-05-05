<template>
	<view class="container">
		<!-- 项目标题和状态区域 -->
		<view class="page-header">
			<view class="header-content" v-if="projectDetail">
				<view class="title-container">
					<text class="title">{{projectDetail.projectName}}</text>
					<view :class="['status-tag', getStatusClass(projectDetail.status)]">{{getStatusText(projectDetail.status)}}</view>
				</view>
				<view class="sub-info">
					<text class="organization">{{projectDetail.organization}}</text>
					<text class="date">申报于 {{formatDate(projectDetail.createTime)}}</text>
				</view>
			</view>
		</view>
		
		<scroll-view scroll-y class="content-scroll">
			<view class="detail-content" v-if="projectDetail">
				<!-- 项目基本信息卡片 -->
				<view class="detail-card">
					<view class="card-header">
						<text class="section-title">基本信息</text>
					</view>
					<view class="card-body">
						<view class="info-grid">
							<view class="info-item">
								<text class="label">项目负责人</text>
								<text class="value">{{projectDetail.leader}}</text>
							</view>
							<view class="info-item">
								<text class="label">项目预算</text>
								<text class="value">{{projectDetail.budget}} 万元</text>
							</view>
							<view class="info-item">
								<text class="label">实施周期</text>
								<text class="value">{{projectDetail.period}}</text>
							</view>
							<view class="info-item">
								<text class="label">审核时间</text>
								<text class="value">{{projectDetail.updateTime ? formatDate(projectDetail.updateTime) : '-'}}</text>
							</view>
						</view>
					</view>
				</view>
				
				
				<!-- 技术方案卡片 -->
				<view class="detail-card" v-if="projectDetail.technicalSolution">
					<view class="card-header">
						<text class="section-title">技术方案</text>
					</view>
					<view class="card-body">
						<text class="content-text">{{projectDetail.technicalSolution}}</text>
					</view>
				</view>
				
				<!-- a预期成果卡片 -->
				<view class="detail-card" v-if="projectDetail.expectedResults">
					<view class="card-header">
						<text class="section-title">预期成果</text>
					</view>
					<view class="card-body">
						<text class="content-text">{{projectDetail.expectedResults}}</text>
					</view>
				</view>
				
				<!-- 经济效益卡片 -->
				<view class="detail-card" v-if="projectDetail.economicBenefits">
					<view class="card-header">
						<text class="section-title">经济效益</text>
					</view>
					<view class="card-body">
						<text class="content-text">{{projectDetail.economicBenefits}}</text>
					</view>
				</view>
				
				<!-- 风险识别卡片 -->
				<view class="detail-card" v-if="projectDetail.riskIdentification">
					<view class="card-header">
						<text class="section-title">风险识别</text>
					</view>
					<view class="card-body">
						<text class="content-text">{{projectDetail.riskIdentification}}</text>
					</view>
				</view>
				
				<!-- 专家分配卡片 -->
				<view class="detail-card" v-if="projectDetail.status === '3' || projectDetail.status === '4'">
					<view class="card-header">
						<text class="section-title">专家分配</text>
					</view>
					<view class="card-body">
						<view class="expert-list" v-if="experts.length > 0">
							<view class="expert-item" v-for="(expert, index) in experts" :key="index">
								<image class="expert-avatar" :src="expert.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
								<view class="expert-info">
									<text class="expert-name">{{expert.nickname}}</text>
									<text class="expert-email">{{expert.email}}</text>
								</view>
								<button class="chat-btn" @click.stop="navigateToChat(expert.id)">沟通</button>
							</view>
						</view>
						<view class="empty-tip" v-else>暂无专家分配信息</view>
					</view>
				</view>
				
				<!-- 评审结果卡片 -->
				<view class="detail-card" v-if="projectDetail.status === '4'">
					<view class="card-header">
						<text class="section-title">评审结果</text>
					</view>
					<view class="card-body">
						<view class="review-content" v-if="reviewResult">
							<view class="score-container">
								<text class="score-label">综合评分</text>
								<text class="score-value">{{reviewResult.score || '-'}}</text>
							</view>
							<view class="review-divider"></view>
						</view>
						<view class="empty-tip" v-else>暂无评审结果</view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部操作栏 -->
		<view class="action-bar">
			<button class="back-btn" @click="goBack">返回列表</button>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onLoad } from "@dcloudio/uni-app";
import { 
	getProjectApplicationDetail, 
	getApplicationExperts, 
	getApplicationReviewResult 
} from '../../api/my/applications.js';

const projectId = ref(null);
const projectDetail = ref(null);
const experts = ref([]);
const reviewResult = ref(null);

// 格式化日期
const formatDate = (dateStr) => {
	if (!dateStr) return '-';
	const date = new Date(dateStr);
	return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 获取状态文本
const getStatusText = (status) => {
	console.log(status);
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

// 获取项目详情
const fetchProjectDetail = async () => {
	try {
		if (!projectId.value) return;
		
		const res = await getProjectApplicationDetail(projectId.value);
		
		if (res.code == 200) {
			projectDetail.value = res.data;
			// 如果已分配专家或已评审，获取相关信息
			if (res.data.status === '3' || res.data.status === '4') {
				fetchExpertInfo();
			}
			
			if (res.data.status === '4') {
				fetchReviewResult();
			}
		} else {
			uni.showToast({
				title: res.msg || '获取项目详情失败',
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

// 获取专家信息
const fetchExpertInfo = async () => {
	try {
		const res = await getApplicationExperts(projectId.value);
		
		if (res.code == 200) {
			experts.value = res.data || [];
		}
	} catch (error) {
		console.error(error);
	}
};

// 获取评审结果
const fetchReviewResult = async () => {
	try {
		const res = await getApplicationReviewResult(projectId.value);
		
		if (res.code == 200) {
			reviewResult.value = res.data;
		}
	} catch (error) {
		console.error(error);
	}
};

// 返回列表
const goBack = () => {
	uni.navigateBack();
};

// 跳转到聊天页面
const navigateToChat = (userId) => {
	uni.navigateTo({
		url: `/pages/my/chat?userId=${userId}`
	});
};

onLoad((options) => {
	if (options && options.id) {
		projectId.value = options.id;
		fetchProjectDetail();
	} else {
		uni.showToast({
			title: '缺少项目ID参数',
			icon: 'none'
		});
		setTimeout(() => {
			goBack();
		}, 1500);
	}
});
</script>

<style>
.container {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	padding-bottom: 80rpx;
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
	margin-bottom: 10rpx;
}

.title-container {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 20rpx;
}

.title {
	font-size: 38rpx;
	font-weight: bold;
	color: #fff;
	flex: 1;
}

.sub-info {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.organization {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
}

.date {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.7);
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

.content-scroll {
	flex: 1;
	padding-bottom: 120rpx;
}

.detail-content {
	padding: 30rpx;
}

.detail-card {
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 24rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.card-header {
	padding: 24rpx 30rpx;
	border-bottom: 1px solid #f0f0f0;
}

.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	position: relative;
	padding-left: 20rpx;
}

.section-title::before {
	content: '';
	position: absolute;
	left: 0;
	top: 6rpx;
	height: 28rpx;
	width: 6rpx;
	background-color: #409EFF;
	border-radius: 3rpx;
}

.card-body {
	padding: 30rpx;
}

.info-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 30rpx 20rpx;
}

.info-item {
	display: flex;
	flex-direction: column;
	gap: 10rpx;
}

.label {
	font-size: 26rpx;
	color: #909399;
}

.value {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.content-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
}

.expert-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.expert-item {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
}

.expert-avatar {
	width: 88rpx;
	height: 88rpx;
	border-radius: 50%;
	margin-right: 20rpx;
	border: 2rpx solid #eee;
}

.expert-info {
	flex: 1;
}

.expert-name {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
}

.expert-email {
	font-size: 24rpx;
	color: #909399;
	display: block;
}

.chat-btn {
	background-color: #409EFF;
	color: #fff;
	font-size: 24rpx;
	padding: 8rpx 20rpx;
	border-radius: 30rpx;
	margin-left: 20rpx;
	line-height: 1.5;
	min-width: 100rpx;
	text-align: center;
}

.review-content {
	background: #f9f9f9;
	border-radius: 12rpx;
	overflow: hidden;
}

.score-container {
	padding: 24rpx 30rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.score-label {
	font-size: 28rpx;
	color: #606266;
}

.score-value {
	font-size: 38rpx;
	color: #409EFF;
	font-weight: bold;
}

.review-divider {
	height: 1px;
	background-color: #eee;
}

.comment-container {
	padding: 24rpx 30rpx;
}

.comment-label {
	font-size: 28rpx;
	color: #606266;
	margin-bottom: 16rpx;
	display: block;
}

.comment-value {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
}

.empty-tip {
	font-size: 28rpx;
	color: #909399;
	text-align: center;
	padding: 30rpx 0;
}

.action-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 24rpx 30rpx;
	background: #fff;
	display: flex;
	justify-content: center;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	z-index: 10;
	padding-bottom: env(safe-area-inset-bottom);
}

.back-btn {
	background-color: #409EFF;
	color: #fff;
	padding: 4rpx 100rpx;
	border-radius: 150rpx;
	font-size: 30rpx;
	font-weight: 500;
}
</style> 