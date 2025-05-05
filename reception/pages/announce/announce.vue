<template>
	<view class="container">
		<scroll-view 
			scroll-y="true" 
			class="scroll-view"
			refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
			@refresherpulling="onPulling"
			@refresherrestore="onRestore"
			@refresherabort="onAbort"
		>
			<view class="page-header">
				<view class="title">项目评审公示</view>
				<view class="subtitle">已完成评审的项目按照综合评分排名</view>
			</view>
			
			<!-- 加载提示 -->
			<view class="loading-box" v-if="loading && !refreshing">
				<image src="../../static/icons/loading.gif" mode="aspectFit" class="loading-icon"></image>
				<text class="loading-text">加载中...</text>
			</view>
			
			<!-- 无数据提示 -->
			<view class="no-data" v-else-if="projectList.length === 0">
				<image src="../../static/icons/no-data.png" mode="aspectFit"></image>
				<text>暂无公示项目</text>
			</view>
			
			<!-- 项目列表 -->
			<view class="project-list" v-else>
				<view class="project-card" v-for="(item, index) in projectList" :key="index" @click="viewDetail(item)">
					<view class="rank-badge" :class="{'rank-top': index < 3}">{{index + 1}}</view>
					
					<view class="project-header">
						<text class="project-name">{{item.projectName}}</text>
						<view class="score-tag">
							<text>{{item.score || 0}}分</text>
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
							<text class="label">评审专家：</text>
							<text class="value">{{item.reviewCount || 0}}人</text>
						</view>
					</view>
					
					<!-- 评分详情 -->
				<!-- 	<view class="score-details" v-if="item.showDetails">
						<view class="detail-header">
							<text>评分详情</text>
							<text class="collapse-btn" @click.stop="toggleDetails(index)">收起</text>
						</view>
						
						<view class="score-categories">
							<view class="category-section">
								<view class="category-title">技术评审</view>
								<view class="score-item">
									<text>技术可行性:</text>
									<text class="score-value">{{item.technicalFeasibilityScore || 0}}/20分</text>
								</view>
								<view class="score-item">
									<text>创新性:</text>
									<text class="score-value">{{item.innovationScore || 0}}/15分</text>
								</view>
								<view class="score-item">
									<text>成熟度:</text>
									<text class="score-value">{{item.maturityScore || 0}}/15分</text>
								</view>
							</view>
							
							<view class="category-section">
								<view class="category-title">商务评审</view>
								<view class="score-item">
									<text>预算合理性:</text>
									<text class="score-value">{{item.budgetReasonabilityScore || 0}}/15分</text>
								</view>
								<view class="score-item">
									<text>成本效益:</text>
									<text class="score-value">{{item.costBenefitScore || 0}}/15分</text>
								</view>
								<view class="score-item">
									<text>合同条款:</text>
									<text class="score-value">{{item.contractTermsScore || 0}}/10分</text>
								</view>
							</view>
							
							<view class="category-section">
								<view class="category-title">风险与合规</view>
								<view class="score-item">
									<text>风险识别:</text>
									<text class="score-value">{{item.riskIdentificationScore || 0}}/10分</text>
								</view>
								<view class="score-item">
									<text>合规性:</text>
									<text class="score-value">{{item.complianceScore || 0}}/10分</text>
								</view>
							</view>
						</view>
					</view>
					
					<view class="action-box">
						<view class="action-btn" @click.stop="toggleDetails(index)">
							{{item.showDetails ? '收起详情' : '查看评分详情'}}
						</view>
					</view> -->
					
					<!-- <view class="action-box">
						<view class="action-btn" @click.stop="viewDetail(item)">查看详情</view>
					</view> -->
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
	import {
		ref,
		onMounted
	} from 'vue'
	import { request } from '../../request/index.js'
	
	const loading = ref(true);
	const refreshing = ref(false);
	const projectList = ref([]);
	
	// 获取已评审完成的项目列表
	const fetchPublicProjects = async () => {
		loading.value = true;
		try {
			const res = await request({
				url: '/project/application/announce-list',
				method: 'GET'
			});
			
			if (res.code === 200) {
				// 处理数据
				const projects = res.data || [];
				
				// 为每个项目添加展开/收起状态
				projectList.value = projects.map(project => ({
					...project,
					showDetails: false
				}));
			}
		} catch (error) {
			console.error('获取公示项目失败:', error);
			uni.showToast({
				title: '获取数据失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};
	
	// 下拉刷新处理
	const onRefresh = async () => {
		refreshing.value = true;
		try {
			await fetchPublicProjects();
			uni.showToast({
				title: '刷新成功',
				icon: 'success',
				duration: 1000
			});
		} catch (error) {
			console.error('刷新失败:', error);
		} finally {
			setTimeout(() => {
				refreshing.value = false;
			}, 1000);
		}
	};
	
	// 下拉中
	const onPulling = (e) => {
		// 可以在这里处理下拉过程中的逻辑
	};
	
	// 下拉还原
	const onRestore = () => {
		console.log('下拉刷新还原');
	};
	
	// 下拉中止
	const onAbort = () => {
		console.log('下拉刷新中止');
	};
	
	// 展开/收起详情
	const toggleDetails = (index) => {
		if (!projectList.value[index]) return;
		
		projectList.value[index].showDetails = !projectList.value[index].showDetails;
	};
	
	// 查看项目详情
	const viewDetail = (item) => {
		uni.navigateTo({
			url: `/pages/announce/project-detail?id=${item.id}`
		});
	};

	onMounted(() => {
		fetchPublicProjects();
	});
</script>

<style>
	.container {
		height: 100vh;
		background-color: #f5f6f7;
	}
	
	.scroll-view {
		height: 100%;
	}
	
	.page-header {
		margin: 30rpx;
		margin-bottom: 30rpx;
	}
	
	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
	}
	
	.subtitle {
		font-size: 26rpx;
		color: #888;
		margin-top: 10rpx;
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
		padding: 0 30rpx 30rpx;
	}
	
	.project-card {
		background: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
		position: relative;
	}
	
	.rank-badge {
		position: absolute;
		top: -10rpx;
		left: -10rpx;
		width: 50rpx;
		height: 50rpx;
		background: #ddd;
		color: #fff;
		font-size: 26rpx;
		font-weight: bold;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 2rpx 5rpx rgba(0,0,0,0.1);
	}
	
	.rank-top {
		background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
	}
	
	.project-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-left: 30rpx;
	}
	
	.project-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		flex: 1;
	}
	
	.score-tag {
		padding: 6rpx 16rpx;
		background: #f0f5ff;
		border-radius: 6rpx;
		font-size: 28rpx;
		color: #597ef7;
		font-weight: bold;
	}
	
	.project-info {
		margin-bottom: 20rpx;
	}
	
	.info-item {
		display: flex;
		margin-bottom: 16rpx;
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
	
	.score-details {
		background: #f9f9f9;
		border-radius: 8rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
	}
	
	.detail-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}
	
	.collapse-btn {
		font-size: 24rpx;
		color: #888;
		font-weight: normal;
	}
	
	.score-categories {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}
	
	.category-section {
		margin-bottom: 10rpx;
	}
	
	.category-title {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
		padding-left: 10rpx;
		border-left: 4rpx solid #1890ff;
	}
	
	.score-item {
		display: flex;
		justify-content: space-between;
		font-size: 26rpx;
		color: #333;
		margin-bottom: 8rpx;
		padding-left: 16rpx;
	}
	
	.score-value {
		color: #1890ff;
	}
	
	.action-box {
		display: flex;
		justify-content: center;
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