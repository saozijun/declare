<template>
	<view class="container">
		<!-- 左侧分类列表 -->
		<view class="category-sidebar">
			<view class="category-item" 
				v-for="(category, index) in categoryList" 
				:key="index"
				:class="{ active: currentCategoryId === category.id }"
				@click="switchCategory(category)">
				{{category.name}}
			</view>
		</view>

		<!-- 右侧项目列表 -->
		<view class="project-content">
			<!-- 分类标题 -->
			<view class="category-header">
				<view class="category-name">{{currentCategoryName}}</view>
			</view>

			<!-- 项目列表 -->
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

			<!-- 加载更多 -->
			<view class="load-more" v-if="hasMore">
				<text v-if="loading">加载中...</text>
				<text v-else @click="loadMore">加载更多</text>
			</view>
			<view class="no-more" v-else>没有更多了</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		onMounted
	} from 'vue'
	import {
		getCategoryProjects,
		getCategoryList
	} from '../../api/type/type.js'
	
	const userInfo = ref(uni.getStorageSync('userInfo') || null)
	const categoryList = ref([])
	const currentCategoryId = ref('')
	const currentCategoryName = ref('')
	const projectList = ref([])
	const pageNum = ref(1)
	const pageSize = ref(10)
	const hasMore = ref(true)
	const loading = ref(false)

	onMounted(() => {
		getCategories()
	})

	const getCategories = async () => {
		try {
			const res = await getCategoryList()
			if (res.code === 200) {
				categoryList.value = res.data
				if (categoryList.value.length > 0) {
					switchCategory(categoryList.value[0])
				}
			}
		} catch (e) {
			console.error('获取分类列表失败', e)
		}
	}

	const switchCategory = (category) => {
		currentCategoryId.value = category.id
		currentCategoryName.value = category.name
		pageNum.value = 1
		projectList.value = []
		getProjects()
	}

	const getProjects = async () => {
		if (loading.value) return
		loading.value = true
		try {
			const res = await getCategoryProjects({
				categoryId: currentCategoryId.value,
				pageNum: pageNum.value,
				pageSize: pageSize.value
			})
			if (res.code === 200) {
				const { records, total } = res.data
				if (pageNum.value === 1) {
					projectList.value = records
				} else {
					projectList.value = [...projectList.value, ...records]
				}
				hasMore.value = projectList.value.length < total
			}
		} catch (e) {
			console.error('获取项目列表失败', e)
		} finally {
			loading.value = false
		}
	}

	const loadMore = () => {
		if (!hasMore.value || loading.value) return
		pageNum.value++
		getProjects()
	}

	const toProjectDetail = (project) => {
		uni.navigateTo({
			url: `/pages/project/detail?id=${project.id}`
		})
	}
</script>

<style>
	.container {
		display: flex;
		min-height: 100vh;
		background-color: #F8F8F8;
	}

	.category-sidebar {
		width: 200rpx;
		background-color: #ffffff;
/* 		padding: 20rpx 0; */
	}

	.category-item {
		padding: 36rpx 30rpx;
		font-size: 28rpx;
		color: #333;
		border-left: 4rpx solid transparent;
	}

	.category-item.active {
		background-color: #f5f5f5;
		border-left-color: #007AFF;
		color: #007AFF;
	}

	.project-content {
		flex: 1;
		padding: 20rpx;
	}

	.category-header {
		padding: 20rpx;
		/* background-color: #ffffff; */
		border-radius: 16rpx;
		margin-bottom: 20rpx;
	}

	.category-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.project-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.project-item {
		display: flex;
		padding: 20rpx;
		background-color: #ffffff;
		border-radius: 12rpx;
	}

	.project-item image {
		width: 200rpx;
		height: 150rpx;
		border-radius: 8rpx;
		margin-right: 20rpx;
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
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 1;
		overflow: hidden;
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
	
	.project-date{
		font-size: 24rpx;
		color: #666;
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

	.load-more, .no-more {
		text-align: center;
		padding: 20rpx;
		color: #999;
		font-size: 24rpx;
	}

	.load-more text {
		color: #007AFF;
	}
</style>