<template>
	<view class="container">
		<view class="page-header">
			<view class="title">评审结果</view>
		</view>
		
		<!-- 加载提示 -->
		<view class="loading-box" v-if="loading">
			<image src="../../static/icons/loading.gif" mode="aspectFit" class="loading-icon"></image>
			<text class="loading-text">加载中...</text>
		</view>
		
		<!-- 无数据提示 -->
		<view class="no-data" v-else-if="projectList.length === 0">
			<image src="../../static/icons/no-data.png" mode="aspectFit"></image>
			<text>暂无评审项目</text>
		</view>
		
		<!-- 项目列表 -->
		<view class="project-list" v-else>
			<view class="project-card" v-for="(item, index) in projectList" :key="index" @click="viewDetail(item)">
				<view class="project-header">
					<text class="project-name">{{item.projectName}}</text>
					<view class="project-status" :class="getStatusClass(item.status)">
						{{getStatusText(item.status)}}
					</view>
				</view>
				
				<view class="project-info">
					<view class="info-item">
						<text class="label">申报单位：</text>
						<text class="value">{{item.organization}}</text>
					</view>
					<view class="info-item">
						<text class="label">项目负责人：</text>
						<text class="value">{{item.leader}}</text>
					</view>
					<view class="info-item">
						<text class="label">提交时间：</text>
						<text class="value">{{formatDate(item.createTime)}}</text>
					</view>
				</view>
				
				<view class="action-box">
					<view class="action-btn" @click.stop="viewDetail(item)">查看评审详情</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, onMounted } from 'vue';
	import { getMyCompletedReviews } from '../../api/my/review.js';
	
	const loading = ref(true);
	const projectList = ref([]);
	
	// 获取项目列表
	const fetchProjects = async () => {
		loading.value = true;
		try {
			const res = await getMyCompletedReviews();
			if (res.code === 200) {
				// 获取已完成评审的项目申报
				projectList.value = res.data || [];
			}
		} catch (error) {
			console.error('获取项目列表失败', error);
			uni.showToast({
				title: '获取数据失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};
	
	// 查看项目详情
	const viewDetail = (item) => {
		uni.navigateTo({
			url: `/pages/my/review-detail?id=${item.id}`
		});
	};
	
	// 状态文本转换
	const getStatusText = (status) => {
		return '评审完成';
	};
	
	// 状态样式类
	const getStatusClass = (status) => {
		return 'status-reviewed';
	};
	
	// 日期格式化
	const formatDate = (dateStr) => {
		if (!dateStr) return '--';
		const date = new Date(dateStr);
		return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
	};
	
	onMounted(() => {
		fetchProjects();
	});
</script>

<style>
	.container {
		min-height: 100vh;
		background-color: #f5f6f7;
		padding: 30rpx;
	}
	
	.page-header {
		margin-bottom: 30rpx;
	}
	
	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
	}
	
	.loading-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 400rpx;
	}
	
	.loading-icon {
		width: 80rpx;
		height: 80rpx;
		margin-bottom: 20rpx;
	}
	
	.loading-text {
		color: #999;
		font-size: 28rpx;
	}
	
	.no-data {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}
	
	.no-data image {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 20rpx;
	}
	
	.no-data text {
		color: #999;
		font-size: 28rpx;
	}
	
	.project-list {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}
	
	.project-card {
		background: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
	}
	
	.project-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}
	
	.project-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.project-status {
		padding: 4rpx 16rpx;
		border-radius: 6rpx;
		font-size: 24rpx;
	}
	
	.status-pending {
		background: #e6f7ff;
		color: #1890ff;
	}
	
	.status-pass {
		background: #f6ffed;
		color: #52c41a;
	}
	
	.status-reject {
		background: #fff2f0;
		color: #ff4d4f;
	}
	
	.status-reviewed {
		background: #f0f5ff;
		color: #597ef7;
	}
	
	.project-info {
		margin-bottom: 20rpx;
	}
	
	.info-item {
		display: flex;
		margin-bottom: 20rpx;
	}
	
	.label {
		color: #888;
		font-size: 26rpx;
		min-width: 180rpx;
	}
	
	.value {
		color: #333;
		font-size: 26rpx;
		flex: 1;
	}
	
	.action-box {
		display: flex;
		justify-content: flex-end;
		margin-top: 20rpx;
	}
	
	.action-btn {
		background: #1890ff;
		color: #fff;
		padding: 10rpx 30rpx;
		border-radius: 30rpx;
		font-size: 26rpx;
	}
</style> 