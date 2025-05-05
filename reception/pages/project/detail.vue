<template>
	<view class="container">
		<view class="project-detail">
			<!-- 项目标题区域 -->
			<view class="project-header">
				<view class="project-name">{{projectDetail.name}}</view>
				<view class="project-status" :class="projectDetail.status == 1 ? 'published' : 'unpublished'">
					{{projectDetail.status == 1 ? '申报中' : '已截止'}}
				</view>
			</view>
			
			<!-- 项目基本信息 -->
			<view class="info-section">
				<view class="section-title">基本信息</view>
				<view class="info-grid">
					<view class="info-item">
						<text class="label">项目负责人</text>
						<text class="value">{{projectDetail.leader}}</text>
					</view>
					<view class="info-item">
						<text class="label">联系方式</text>
						<text class="value">{{projectDetail.contact}}</text>
					</view>
					<view class="info-item">
						<text class="label">开始时间</text>
						<text class="value">{{projectDetail.startDate}}</text>
					</view>
					<view class="info-item">
						<text class="label">结束时间</text>
						<text class="value">{{projectDetail.endDate}}</text>
					</view>
				</view>
			</view>

			<!-- 项目描述 -->
			<view class="info-section">
				<view class="section-title">项目描述</view>
				<view class="description-content">
					{{projectDetail.description}}
				</view>
			</view>

			<!-- 申报要求 -->
			<view class="info-section">
				<view class="section-title">申报要求</view>
				<view class="requirements-content">
					<view class="requirement-item" v-for="(item, index) in requirementsList" :key="index">
						<text class="requirement-dot">•</text>
						<text class="requirement-text">{{item}}</text>
					</view>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="action-area">
				<button class="apply-btn" @click="toApplication" v-if="projectDetail.status == 1">立即申报</button>
				<button class="disabled-btn" v-else>已截止申报</button>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProjectDetail } from '../../api/type/type.js'
import { onLoad } from "@dcloudio/uni-app";

const projectDetail = ref({})
const requirementsList = ref([
	'申报单位需具备相关资质',
	'项目负责人需具备相关经验',
	'需提供详细的技术方案',
	'需提供预算明细',
	'需提供预期成果说明'
])

onLoad(async (option)=>{
	if (option.id) getProjectInfo(option.id)
})

const getProjectInfo = async (id) => {
	try {
		const res = await getProjectDetail(id)
		if (res.code === 200) {
			projectDetail.value = res.data
		}
	} catch (e) {
		console.error('获取项目详情失败', e)
	}
}

const toApplication = () => {
	uni.navigateTo({
		url: `/pages/project/application?id=${projectDetail.value.id}`
	})
}
</script>

<style>
.container {
	padding: 20rpx;
	background-color: #F8F8F8;
	min-height: 100vh;
}

.project-detail {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx;
}

.project-header {
	display: flex;
	justify-content: space-between;
	gap: 20rpx;
	align-items: flex-start;
	margin-bottom: 40rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #EEEEEE;
}

.project-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

.project-status {
	font-size: 24rpx;
	padding: 8rpx 20rpx;
	border-radius: 50rpx;
	margin-top: 4rpx;
	min-width: fit-content;
}

.project-status.published {
	background-color: #e8f5e9;
	color: #4caf50;
}

.project-status.unpublished {
	background-color: #fff3e0;
	color: #ff9800;
}

.info-section {
	margin-bottom: 40rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
	position: relative;
	padding-left: 20rpx;
}

.section-title::before {
	content: '';
	position: absolute;
	left: 0;
	top: 50%;
	transform: translateY(-50%);
	width: 8rpx;
	height: 32rpx;
	background-color: #007AFF;
	border-radius: 4rpx;
}

.info-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
	background-color: #F8F8F8;
	padding: 20rpx;
	border-radius: 12rpx;
}

.info-item {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.label {
	font-size: 26rpx;
	color: #666;
}

.value {
	font-size: 28rpx;
	color: #333;
}

.description-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	background-color: #F8F8F8;
	padding: 20rpx;
	border-radius: 12rpx;
}

.requirements-content {
	background-color: #F8F8F8;
	padding: 20rpx;
	border-radius: 12rpx;
}

.requirement-item {
	display: flex;
	align-items: flex-start;
	margin-bottom: 16rpx;
}

.requirement-dot {
	color: #007AFF;
	margin-right: 10rpx;
	font-size: 32rpx;
}

.requirement-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	flex: 1;
}

.action-area {
	margin-top: 40rpx;
	display: flex;
	justify-content: center;
}

.apply-btn {
	width: 80%;
	height: 80rpx;
	line-height: 80rpx;
	background-color: #007AFF;
	color: #ffffff;
	border-radius: 40rpx;
	font-size: 32rpx;
}

.disabled-btn {
	width: 80%;
	height: 80rpx;
	line-height: 80rpx;
	background-color: #cccccc;
	color: #ffffff;
	border-radius: 40rpx;
	font-size: 32rpx;
}
</style> 