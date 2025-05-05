<template>
	<view class="container">
		<!-- 轮播图 -->
		<swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000"
			indicator-color="rgba(255, 255, 255, 0.6)" indicator-active-color="#ffffff">
			<swiper-item v-for="(item, index) in bannerList" :key="index">
				<image :src="item.url" mode="aspectFill"></image>
			</swiper-item>
		</swiper>

		<!-- 搜索框 -->
		<view class="search-box">
			<view class="search-input" @click="toSearch">
				<image src="../../static/search.png" mode="widthFix"></image>
				<input type="text" placeholder="搜索项目" />
			</view>
		</view>
		
		<!-- 项目列表 -->
		<view class="project-section">
			<view class="section-title">推荐项目</view>
			<view class="project-list">
				<view class="project-item" v-for="(item, index) in projectList" :key="index" @click="toProjectDetail(item)">
					<image src="../../static/project.png" mode="aspectFill"></image>
					<view class="project-info">
						<view class="project-name">{{item.name}}</view>
						<view class="project-desc">{{item.description}}</view>
						<view class="project-date">截止日期：{{item.endDate}}</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		onMounted
	} from 'vue'
	import {
		getBanner,
		getRecommendations
	} from '../../api/index/index.js'
	
	const userInfo = ref(uni.getStorageSync('userInfo') || null)
	const bannerList = ref([])
	const projectList = ref([])

	onMounted(() => {
		getBannerFn()
		getRecommendationsFn()
	})

	const getBannerFn = async () => {
		let res = await getBanner()
		if (res.code === 200) {
			bannerList.value = res.data
		}
	}

	const getRecommendationsFn = async () => {
		let res = await getRecommendations(5)
		if (res.code === 200) {
			projectList.value = res.data
		}
	}

	const toSearch = () => {
		uni.navigateTo({
			url: '/pages/search/search'
		})
	}

	const toCategory = (category) => {
		uni.navigateTo({
			url: `/pages/type/type?id=${category.id}&name=${category.name}`
		})
	}

	const toProjectDetail = (project) => {
		uni.navigateTo({
			url: `/pages/project/detail?id=${project.id}`
		})
	}
</script>

<style>
	.container {
		padding: 20rpx;
		min-height: 100vh;
		background-color: #F8F8F8;
	}

	/* 轮播图样式 */
	.banner-swiper {
		height: 320rpx;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
	}

	.banner-swiper image {
		width: 100%;
		height: 100%;
	}

	/* 搜索框样式 */
	.search-box {
		z-index: 100;
		background-color: #f8f8f8;
		padding: 20rpx 0;
	}

	.search-input {
		display: flex;
		align-items: center;
		background-color: #ffffff;
		padding: 16rpx 24rpx;
		border-radius: 40rpx;
	}

	.search-input image {
		width: 32rpx;
		height: 32rpx;
	}

	.search-input input {
		flex: 1;
		margin-left: 16rpx;
		font-size: 28rpx;
	}

	/* 分类导航样式 */
	.category-section {
		margin-top: 20rpx;
		background-color: #ffffff;
		padding: 20rpx;
		border-radius: 16rpx;
	}

	.category-title {
		font-size: 32rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	.category-scroll {
		white-space: nowrap;
	}

	.category-list {
		display: flex;
		padding: 10rpx 0;
	}

	.category-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-right: 40rpx;
	}

	.category-item image {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		margin-bottom: 10rpx;
	}

	.category-item text {
		font-size: 24rpx;
		color: #333;
	}

	/* 项目列表样式 */
	.project-section {
		margin-top: 20rpx;
		background-color: #ffffff;
		padding: 20rpx;
		border-radius: 16rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	.project-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.project-item {
		display: flex;
		padding: 20rpx;
		background-color: #f8f8f8;
		border-radius: 12rpx;
	}

	.project-item image {
		width: 200rpx;
		height: 150rpx;
		border-radius: 8rpx;
		margin-right: 20rpx;
	}
	
	
	.project-date{
		font-size: 24rpx;
		color: #666;
	}
	

	.project-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.project-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.project-desc {
		font-size: 24rpx;
		color: #666;
		margin: 10rpx 0;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}

	.project-status {
		font-size: 24rpx;
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
		display: inline-block;
	}

	.project-status.published {
		background-color: #e8f5e9;
		color: #4caf50;
	}

	.project-status.unpublished {
		background-color: #fff3e0;
		color: #ff9800;
	}
</style>