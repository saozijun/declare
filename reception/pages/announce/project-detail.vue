 <template>
 	<view class="container">
 		<view class="page-header">
 			<view class="title">项目详情</view>
 		</view>
 		
 		<!-- 加载提示 -->
 		<view class="loading-box" v-if="loading">
 			<image src="../../static/icons/loading.gif" mode="aspectFit" class="loading-icon"></image>
 			<text class="loading-text">加载中...</text>
 		</view>
 		
 		<!-- 项目信息 -->
 		<view class="project-card" v-if="projectInfo && !loading">
 			<view class="project-header">
 				<text class="project-name">{{projectInfo.projectName}}</text>
 				<view class="project-status" :class="getScoreClass(projectInfo.score)">
 					{{projectInfo.score || 0}}分
 				</view>
 			</view>
 			
 			<view class="project-info">
 				<view class="info-item">
 					<text class="label" style="letter-spacing: 6rpx;">申报单位：</text>
 					<text class="value">{{projectInfo.organization}}</text>
 				</view>
 				<view class="info-item">
 					<text class="label">项目负责人：</text>
 					<text class="value">{{projectInfo.leader}}</text>
 				</view>
 				<view class="info-item">
 					<text class="label" style="letter-spacing: 6rpx;">项目周期：</text>
 					<text class="value">{{projectInfo.period}}</text>
 				</view>
 				<view class="info-item">
 					<text class="label" style="letter-spacing: 6rpx;">预算总额：</text>
 					<text class="value">¥{{projectInfo.budget ? projectInfo.budget.toLocaleString() : '--'}}</text>
 				</view>
 				<view class="info-item">
 					<text class="label" style="letter-spacing: 6rpx;">评审专家：</text>
 					<text class="value">{{projectInfo.reviewCount || 0}}人</text>
 				</view>
 			</view>
 		</view>
 		
 		<!-- 评审结果 -->
 		<view class="section-title">评审详情</view>
 		<view class="review-card" v-if="!loading">
 			<view class="no-data" v-if="!projectInfo">
 				<text>暂无评审结果</text>
 			</view>
 			<view v-else>
 				<!-- 总分 -->
 				<view class="score-box">
 					<text class="score-label">综合评分：</text>
 					<text class="score-value">{{projectInfo.score || 0}}</text>
 					<text class="score-max">/100</text>
 				</view>
 				
 				<!-- 评分详情 -->
 				<view class="review-categories">
 					<view class="category-section">
 						<view class="category-title">技术评审</view>
 						
 						<view class="score-section">
 							<text class="section-name">技术可行性</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.technicalFeasibilityScore / 20) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.technicalFeasibilityScore || 0}}分/20分</text>
 						</view>
 						
 						<view class="score-section">
 							<text class="section-name">创新性</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.innovationScore / 15) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.innovationScore || 0}}分/15分</text>
 						</view>
 						
 						<view class="score-section">
 							<text class="section-name">成熟度</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.maturityScore / 15) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.maturityScore || 0}}分/15分</text>
 						</view>
 					</view>
 					
 					<view class="category-section">
 						<view class="category-title">商务评审</view>
 						
 						<view class="score-section">
 							<text class="section-name">预算合理性</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.budgetReasonabilityScore / 15) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.budgetReasonabilityScore || 0}}分/15分</text>
 						</view>
 						
 						<view class="score-section">
 							<text class="section-name">成本效益</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.costBenefitScore / 15) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.costBenefitScore || 0}}分/15分</text>
 						</view>
 						
 						<view class="score-section">
 							<text class="section-name">合同条款</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.contractTermsScore / 10) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.contractTermsScore || 0}}分/10分</text>
 						</view>
 					</view>
 					
 					<view class="category-section">
 						<view class="category-title">风险与合规评审</view>
 						
 						<view class="score-section">
 							<text class="section-name">风险识别</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.riskIdentificationScore / 10) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.riskIdentificationScore || 0}}分/10分</text>
 						</view>
 						
 						<view class="score-section">
 							<text class="section-name">合规性</text>
 							<view class="progress-bar">
 								<view class="progress-inner" :style="{width: `${(projectInfo.complianceScore / 10) * 100}%`}"></view>
 							</view>
 							<text class="section-score">{{projectInfo.complianceScore || 0}}分/10分</text>
 						</view>
 					</view>
 				</view>
 				
 				<!-- 评审结论 -->
 				<view class="review-conclusion" v-if="projectInfo.conclusion">
 					<view class="conclusion-title">评审结论</view>
 					<view class="conclusion-content">
 						<text>{{projectInfo.conclusion}}</text>
 					</view>
 				</view>
 			</view>
 		</view>
 		
 		<!-- 项目详情 -->
 		<view class="section-title">项目详情</view>
 		<view class="detail-card" v-if="projectInfo && !loading">
 			<view class="detail-section" v-if="projectInfo.technicalSolution">
 				<view class="detail-title">技术方案</view>
 				<view class="detail-content">{{projectInfo.technicalSolution}}</view>
 			</view>
 			
 			<view class="detail-section" v-if="projectInfo.expectedResults">
 				<view class="detail-title">预期成果</view>
 				<view class="detail-content">{{projectInfo.expectedResults}}</view>
 			</view>
 			
 			<view class="detail-section" v-if="projectInfo.riskIdentification">
 				<view class="detail-title">风险识别</view>
 				<view class="detail-content">{{projectInfo.riskIdentification}}</view>
 			</view>
 			
 			<view class="detail-section" v-if="projectInfo.economicBenefits">
 				<view class="detail-title">经济效益</view>
 				<view class="detail-content">{{projectInfo.economicBenefits}}</view>
 			</view>
 		</view>
 		
 		<!-- 返回按钮 -->
 		<view class="back-btn" @click="goBack">返回列表</view>
 	</view>
 </template>
 
 <script setup>
 	import { ref, onMounted } from 'vue';
 	import { request } from '../../request/index.js';
 	import { onLoad } from "@dcloudio/uni-app";
 	
 	const loading = ref(true);
 	const projectInfo = ref(null);
 	
 	// 获取URL参数
 	const applicationId = ref('');
 	
 	onLoad((options) => {
 		applicationId.value = options.id;
 		
 		if (applicationId.value) {
 			fetchProjectDetail(applicationId.value);
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
 		loading.value = true;
 		try {
 			// 获取项目基本信息
 			const projectRes = await request({
 				url: '/project/application/getById',
 				method: 'GET',
 				data: { id }
 			});
 			
 			if (projectRes.code === 200 && projectRes.data) {
 				// 获取评审结果
 				const reviewRes = await request({
 					url: '/expert/review/result',
 					method: 'GET',
 					data: { applicationId: id }
 				});
 				
 				if (reviewRes.code === 200 && reviewRes.data && reviewRes.data.summary) {
 					// 合并项目信息和评审结果
 					projectInfo.value = {
 						...projectRes.data,
 						...reviewRes.data.summary
 					};
 				} else {
 					projectInfo.value = projectRes.data;
 				}
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
 	
 	// 返回列表
 	const goBack = () => {
 		uni.navigateBack();
 	};
 	
 	// 根据分数获取样式类
 	const getScoreClass = (score) => {
 		if (!score) return '';
 		
 		if (score >= 80) {
 			return 'score-high';
 		} else if (score >= 60) {
 			return 'score-medium';
 		} else {
 			return 'score-low';
 		}
 	};
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
 	
 	.project-card, .review-card, .detail-card {
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
 	
 	.score-high {
 		background: #f6ffed;
 		color: #52c41a;
 	}
 	
 	.score-medium {
 		background: #e6f7ff;
 		color: #1890ff;
 	}
 	
 	.score-low {
 		background: #fff2f0;
 		color: #ff4d4f;
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
 		min-width: 150rpx;
 	}
 	
 	.value {
 		color: #333;
 		font-size: 26rpx;
 		flex: 1;
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
 	
 	.review-conclusion {
 		margin-top: 30rpx;
 	}
 	
 	.conclusion-title {
 		font-size: 28rpx;
 		font-weight: bold;
 		color: #333;
 		margin-bottom: 20rpx;
		position: relative;
		z-index: 11;
 	}
 	
 	.conclusion-content {
 		font-size: 26rpx;
 		color: #333;
 		line-height: 1.6;
 	}
 	
 	.detail-section {
 		margin-bottom: 30rpx;
 	}
 	
 	.detail-title {
 		font-size: 28rpx;
 		font-weight: bold;
 		color: #333;
 		margin-bottom: 16rpx;
 		position: relative;
		z-index: 11;
 	}
 	
 	.detail-content {
 		font-size: 26rpx;
 		color: #333;
 		line-height: 1.6;
 		background: #f9f9f9;
 		padding: 20rpx;
 		border-radius: 8rpx;
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
 	}
	.conclusion-title::before,.category-title::before,.detail-title::before{
		content: '';
		position: absolute;
		left: -10rpx;
		bottom: 0;
		height: 20rpx;
		width: 60rpx;
		border-radius: 3rpx;
		z-index: -1;
		background: linear-gradient(-90deg, #92dfff, #ffffff);
	}
	
 </style>