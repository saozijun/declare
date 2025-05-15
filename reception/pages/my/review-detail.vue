<template>
	<view class="container">
		<view class="section-title">项目信息</view>
		<!-- 加载提示 -->
		<view class="loading-box" v-if="loading">
			<image src="../../static/icons/loading.gif" mode="aspectFit" class="loading-icon"></image>
			<text class="loading-text">加载中...</text>
		</view>
		
		<!-- 项目信息 -->
		<view class="project-card" v-if="projectInfo && !loading">
			<view class="project-header">
				<text class="project-name">{{projectInfo.projectName}}</text>
				<view class="project-status" :class="getStatusClass(projectInfo.status)">
					{{getStatusText(projectInfo.status)}}
				</view>
			</view>
			
			<view class="project-info">
				<view class="info-item">
					<text class="label">申报单位：</text>
					<text class="value">{{projectInfo.organization}}</text>
				</view>
				<view class="info-item">
					<text class="label">项目负责人：</text>
					<text class="value">{{projectInfo.leader}}</text>
				</view>
				<view class="info-item">
					<text class="label">项目周期：</text>
					<text class="value">{{projectInfo.period}}</text>
				</view>
				<view class="info-item">
					<text class="label">预算总额：</text>
					<text class="value">¥{{projectInfo.budget ? projectInfo.budget.toLocaleString() : '--'}}</text>
				</view>
				<view class="info-item">
					<text class="label">提交时间：</text>
					<text class="value">{{formatDate(projectInfo.createTime)}}</text>
				</view>
			</view>
		</view>
		
		<!-- 评审专家 -->
		<view class="section-title">评审专家</view>
		<view class="experts-scroll-container" v-if="!loading">
			<scroll-view scroll-x="true" class="experts-scroll">
				<view class="expert-items">
					<view 
						class="expert-card" 
						v-for="(expert, index) in expertReviews" :key="index"
						:class="{ active: selectedExpertIndex === index }"
						@click="selectExpert(index)"
					>
						<image :src="expert.avatarUrl || '../../static/avatar.png'" class="expert-avatar"></image>
						<view class="expert-info">
							<text class="expert-name">{{expert.expertName || '专家' + (index+1)}}</text>
							<text class="expert-score">{{expert.totalScore || 0}}分</text>
						</view>
					</view>
				</view>
			</scroll-view>
			
			<view class="no-data" v-if="expertReviews.length === 0">
				<text>暂无评审专家</text>
			</view>
		</view>
		
		<!-- 评审结果 -->
		<view class="section-title">评审详情</view>
		<view class="review-card" v-if="!loading">
			<view class="no-data" v-if="!reviewResult && expertReviews.length === 0">
				<text>暂无评审结果</text>
			</view>
			
			<!-- 评审内容 -->
			<view v-else>
				<view class="tab-selector">
					<view 
						class="tab-item" 
						:class="{ active: selectedTab === 'expertDetail' }"
						@click="selectedTab = 'expertDetail'"
					>
						专家评分明细
					</view>
					<view 
						class="tab-item" 
						:class="{ active: selectedTab === 'summary' }"
						@click="selectedTab = 'summary'"
					>
						综合评分
					</view>
				</view>
				
				<!-- 专家评分明细 -->
				<view v-if="selectedTab === 'expertDetail' && selectedExpert">
					<!-- 总分 -->
					<view class="score-box">
						<text class="score-label">评审总分：</text>
						<text class="score-value">{{selectedExpert.totalScore || 0}}</text>
						<text class="score-max">/100</text>
					</view>
					
					<!-- 评分详情 -->
					<view class="review-categories">
						<view class="category-section">
							<view class="category-title">商务评审</view>
							
							<view class="score-section">
								<text class="section-name">企业资质</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.enterpriseQualificationScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.enterpriseQualificationScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">财务情况</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.financialStatusScore / 5) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.financialStatusScore || 0}}分/5分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">业绩案例</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.performanceCaseScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.performanceCaseScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">履约能力</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.performanceCapabilityScore / 5) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.performanceCapabilityScore || 0}}分/5分</text>
							</view>
							
							<view class="category-comment" v-if="selectedExpert.businessRemarks">
								<text class="comment-label">商务评审备注：</text>
								<text class="comment-content">{{selectedExpert.businessRemarks}}</text>
							</view>
						</view>
						
						<view class="category-section">
							<view class="category-title">技术评审</view>
							
							<view class="score-section">
								<text class="section-name">技术方案响应性</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.technicalResponseScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.technicalResponseScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">实施方案</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.implementationPlanScore / 15) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.implementationPlanScore || 0}}分/15分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">质量保障措施</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.qualityAssuranceScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.qualityAssuranceScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">售后服务</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.afterSaleServiceScore / 5) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.afterSaleServiceScore || 0}}分/5分</text>
							</view>
							
							<view class="category-comment" v-if="selectedExpert.technicalRemarks">
								<text class="comment-label">技术评审备注：</text>
								<text class="comment-content">{{selectedExpert.technicalRemarks}}</text>
							</view>
						</view>
						
						<view class="category-section">
							<view class="category-title">价格评审</view>
							
							<view class="score-section">
								<text class="section-name">价格评分</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(selectedExpert.priceScore / 30) * 100}%`}"></view>
								</view>
								<text class="section-score">{{selectedExpert.priceScore || 0}}分/30分</text>
							</view>
							
							<view class="category-comment" v-if="selectedExpert.priceRemarks">
								<text class="comment-label">价格评审备注：</text>
								<text class="comment-content">{{selectedExpert.priceRemarks}}</text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 综合评分 -->
				<view v-if="selectedTab === 'summary' && summaryResult">
					<!-- 总分 -->
					<view class="score-box">
						<text class="score-label">平均评分：</text>
						<text class="score-value">{{summaryResult.score || 0}}</text>
						<text class="score-max">/100</text>
					</view>
					
					<!-- 评分详情 -->
					<view class="review-categories">
						<view class="category-section">
							<view class="category-title">商务评审</view>
							
							<view class="score-section">
								<text class="section-name">企业资质</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.enterpriseQualificationScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.enterpriseQualificationScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">财务情况</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.financialStatusScore / 5) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.financialStatusScore || 0}}分/5分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">业绩案例</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.performanceCaseScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.performanceCaseScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">履约能力</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.performanceCapabilityScore / 5) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.performanceCapabilityScore || 0}}分/5分</text>
							</view>
						</view>
						
						<view class="category-section">
							<view class="category-title">技术评审</view>
							
							<view class="score-section">
								<text class="section-name">技术方案响应性</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.technicalResponseScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.technicalResponseScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">实施方案</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.implementationPlanScore / 15) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.implementationPlanScore || 0}}分/15分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">质量保障措施</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.qualityAssuranceScore / 10) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.qualityAssuranceScore || 0}}分/10分</text>
							</view>
							
							<view class="score-section">
								<text class="section-name">售后服务</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.afterSaleServiceScore / 5) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.afterSaleServiceScore || 0}}分/5分</text>
							</view>
						</view>
						
						<view class="category-section">
							<view class="category-title">价格评审</view>
							
							<view class="score-section">
								<text class="section-name">价格评分</text>
								<view class="progress-bar">
									<view class="progress-inner" :style="{width: `${(summaryResult.priceScore / 30) * 100}%`}"></view>
								</view>
								<text class="section-score">{{summaryResult.priceScore || 0}}分/30分</text>
							</view>
						</view>
					</view>
					
					<!-- 评审结论 -->
					<view class="review-conclusion" v-if="summaryResult.conclusion">
						<view class="conclusion-title">评审结论</view>
						<view class="conclusion-content">
							<text>{{summaryResult.conclusion}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 返回按钮 -->
		<view class="back-btn" @click="goBack">返回列表</view>
	</view>
</template>

<script setup>
	import { ref, onMounted, computed } from 'vue';
	import { getProjectApplicationDetail, getApplicationReviewResult, getApplicationExperts } from '../../api/my/review.js';
	import { onLoad } from "@dcloudio/uni-app";
	
	const loading = ref(true);
	const projectInfo = ref(null);
	const reviewResult = ref(null);
	const expertReviews = ref([]);
	const selectedExpertIndex = ref(0);
	const selectedTab = ref('expertDetail');
	
	// 获取URL参数
	const applicationId = ref('');
	
	// 计算属性：当前选中的专家
	const selectedExpert = computed(() => {
		if (expertReviews.value.length > 0 && selectedExpertIndex.value >= 0) {
			return expertReviews.value[selectedExpertIndex.value];
		}
		return null;
	});
	
	// 计算属性：汇总结果
	const summaryResult = computed(() => {
		if (reviewResult.value) {
			return reviewResult.value.summary || null;
		}
		return null;
	});
	
	onLoad((options) => {
		applicationId.value = options.id;
		
		if (applicationId.value) {
			fetchProjectDetail(applicationId.value);
			fetchReviewResult(applicationId.value);
		} else {
			loading.value = false;
			uni.showToast({
				title: '项目ID不存在',
				icon: 'none'
			});
		}
	});
	
	// 获取项目详情
	const fetchProjectDetail = async (id) => {
		try {
			const res = await getProjectApplicationDetail(id);
			if (res.code === 200) {
				projectInfo.value = res.data;
			}
		} catch (error) {
			console.error('获取项目详情失败', error);
			uni.showToast({
				title: '获取项目详情失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	};
	
	// 获取评审结果
	const fetchReviewResult = async (id) => {
		try {
			const res = await getApplicationReviewResult(id);
			if (res.code === 200) {
				// 原始数据
				reviewResult.value = res.data;
				
				// 处理专家评审列表
				if (res.data.reviews && res.data.reviews.length > 0) {
					expertReviews.value = res.data.reviews.map((review, index) => {
						// 获取专家名称
						const expertName = review.expertName || `专家${index + 1}`;
						
						// 计算总分
						const totalScore = (
							// 商务评审
							(review.enterpriseQualificationScore || 0) +
							(review.financialStatusScore || 0) +
							(review.performanceCaseScore || 0) +
							(review.performanceCapabilityScore || 0) +
							// 技术评审
							(review.technicalResponseScore || 0) +
							(review.implementationPlanScore || 0) +
							(review.qualityAssuranceScore || 0) +
							(review.afterSaleServiceScore || 0) +
							// 价格分
							(review.priceScore || 0)
						);
						
						return {
							...review,
							expertName: expertName,
							totalScore: totalScore,
							avatarUrl: review.avatarUrl || null
						};
					});
					
					// 默认选中第一个专家
					selectedExpertIndex.value = 0;
				} else {
					// 如果没有专家评审，则默认显示汇总数据
					selectedTab.value = 'summary';
				}
			}
		} catch (error) {
			console.error('获取评审结果失败', error);
		}
	};
	
	// 选择专家
	const selectExpert = (index) => {
		selectedExpertIndex.value = index;
		selectedTab.value = 'expertDetail';
	};
	
	// 返回列表
	const goBack = () => {
		uni.navigateBack();
	};
	
	// 状态文本转换
	const getStatusText = (status) => {
		const statusMap = {
			'0': '待审核',
			'1': '已通过',
			'2': '已拒绝',
			'3': '已分配',
			'4': '已评审'
		};
		return statusMap[status] || '未知状态';
	};
	
	// 状态样式类
	const getStatusClass = (status) => {
		const classMap = {
			'0': 'status-pending',
			'1': 'status-pass',
			'2': 'status-reject',
			'3': 'status-reviewed',
			'4': 'status-reviewed'
		};
		return classMap[status] || '';
	};
	
	// 日期格式化
	const formatDate = (dateStr) => {
		if (!dateStr) return '--';
		const date = new Date(dateStr);
		return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
	};
</script>

<style>
	.container {
		min-height: 100vh;
		background-color: #f5f6f7;
		padding: 30rpx;
	}
	
	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin: 30rpx 0 20rpx;
	}
	
	.loading-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 200rpx;
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
		justify-content: center;
		padding: 50rpx 0;
	}
	
	.no-data text {
		color: #999;
		font-size: 28rpx;
	}
	
	.project-card, .experts-scroll-container, .review-card {
		background: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
		margin-bottom: 30rpx;
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
	
	/* 水平滚动专家列表样式 */
	.experts-scroll {
		width: 100%;
		white-space: nowrap;
	}
	
	.expert-items {
		display: flex;
		padding: 10rpx 0;
	}
	
	.expert-card {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 20rpx;
		border: 1rpx solid transparent;
		border-radius: 8rpx;
		margin-right: 20rpx;
		width: 160rpx;
		transition: all 0.3s;
	}
	
	.expert-card:last-child {
		margin-right: 0;
	}
	
	.expert-card.active {
		border-color: #1890ff;
		background-color: rgba(24, 144, 255, 0.05);
	}
	
	.expert-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-bottom: 10rpx;
	}
	
	.expert-info {
		text-align: center;
		width: 100%;
	}
	
	.expert-name {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 6rpx;
		display: block;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.expert-score {
		font-size: 24rpx;
		color: #1890ff;
		font-weight: bold;
	}
	
	.score-box {
		display: flex;
		align-items: baseline;
		margin-bottom: 30rpx;
	}
	
	.score-label {
		font-size: 28rpx;
		color: #333;
	}
	
	.score-value {
		font-size: 60rpx;
		font-weight: bold;
		color: #1890ff;
		margin: 0 10rpx;
	}
	
	.score-max {
		font-size: 28rpx;
		color: #888;
	}
	
	.tab-selector {
		display: flex;
		margin-bottom: 30rpx;
		background-color: #f9f9f9;
		border-radius: 8rpx;
		overflow: hidden;
	}
	
	.tab-item {
		flex: 1;
		padding: 16rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #666;
		transition: all 0.3s;
	}
	
	.tab-item.active {
		background-color: #1890ff;
		color: #fff;
	}
	
	.review-categories {
		margin-bottom: 30rpx;
	}
	
	.category-section {
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx dashed #eee;
	}
	
	.category-section:last-child {
		border-bottom: none;
	}
	
	.category-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		position: relative;
		z-index: 11;
	}
	
	.category-title::before{
		content: '';
		position: absolute;
		left: 0;
		bottom: 0;
		height: 20rpx;
		width: 80rpx;
		/* background-color: #409EFF; */
		border-radius: 3rpx;
		z-index: -1;
		background: linear-gradient(-90deg, #92dfff, #ffffff);
	}
	
	.score-section {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}
	
	.section-name {
		width: 200rpx;
		font-size: 26rpx;
		color: #333;
	}
	
	.progress-bar {
		flex: 1;
		height: 20rpx;
		background: #f5f6f7;
		border-radius: 10rpx;
		margin: 0 20rpx;
		overflow: hidden;
	}
	
	.progress-inner {
		height: 100%;
		background: #1890ff;
		border-radius: 10rpx;
	}
	
	.section-score {
		font-size: 26rpx;
		color: #1890ff;
		min-width: 120rpx;
		text-align: right;
	}
	
	.category-comment {
		background: #f9f9f9;
		padding: 16rpx;
		border-radius: 8rpx;
		margin-top: 10rpx;
		margin-bottom: 20rpx;
	}
	
	.comment-label {
		font-size: 26rpx;
		color: #666;
		font-weight: bold;
		display: block;
		margin-bottom: 8rpx;
	}
	
	.comment-content {
		font-size: 26rpx;
		color: #333;
		line-height: 1.6;
	}
	
	.review-conclusion {
		margin-top: 30rpx;
	}
	
	.conclusion-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.conclusion-content {
		font-size: 26rpx;
		color: #333;
		line-height: 1.6;
	}
	
	.back-btn {
		width: 100%;
		height: 80rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		background: #1890ff;
		color: #fff;
		font-size: 28rpx;
		border-radius: 40rpx;
		margin-top: 40rpx;
		margin-bottom: 20rpx;
	}
</style> 