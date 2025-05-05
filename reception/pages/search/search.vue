<template>
	<view class="container">
		<!-- 搜索框 -->
		<view class="search-header">
			<view class="search-input">
				<view class="back-icon" @click="goBack">
					<uni-icons type="left" size="20"></uni-icons>
				</view>
				<input 
					type="text" 
					v-model="keyword" 
					placeholder="搜索项目名称、描述或负责人" 
					focus 
					confirm-type="search"
					@confirm="search"
				/>
				<view class="search-icon" @click="search">
					<uni-icons type="search" size="20" color="#007AFF"></uni-icons>
				</view>
			</view>
		</view>
		
		<!-- 搜索历史 -->
		<view class="search-history" v-if="historyKeywords.length > 0 && !keyword && !hasSearched">
			<view class="history-header">
				<text class="section-title">搜索历史</text>
				<text class="clear-btn" @click="clearHistory">清除</text>
			</view>
			<view class="history-tags">
				<view 
					class="history-tag" 
					v-for="(item, index) in historyKeywords" 
					:key="index"
					@click="useHistoryKeyword(item)"
				>
					{{ item }}
				</view>
			</view>
		</view>
		
		<!-- 热门搜索 -->
		<view class="hot-search" v-if="!keyword && !hasSearched">
			<view class="section-title">热门搜索</view>
			<view class="hot-tags">
				<view 
					class="hot-tag" 
					v-for="(item, index) in hotKeywords" 
					:key="index"
					@click="useHistoryKeyword(item)"
				>
					{{ item }}
				</view>
			</view>
		</view>
		
		<!-- 加载中 -->
		<view class="loading-box" v-if="loading">
			<image src="../../static/icons/loading.gif" mode="aspectFit" class="loading-icon"></image>
			<text class="loading-text">加载中...</text>
		</view>
		
		<!-- 搜索结果为空 -->
		<view class="no-result" v-else-if="hasSearched && projectList.length === 0">
			<image src="../../static/icons/no-data.png" mode="aspectFit"></image>
			<text>未找到相关项目</text>
		</view>
		
		<!-- 搜索结果 -->
		<view class="search-results" v-else-if="hasSearched">
			<view class="result-header">
				<text class="result-count">找到 {{ total }} 个相关项目</text>
			</view>
			
			<view class="project-list">
				<view class="project-item" v-for="(item, index) in projectList" :key="index" @click="toProjectDetail(item)">
					<image src="../../static/project.png" mode="aspectFill"></image>
					<view class="project-info">
						<view class="project-name">{{ item.name }}</view>
						<view class="project-desc">{{ item.description || '暂无描述' }}</view>
						<view class="project-meta">
							<text class="leader-name">负责人: {{ item.leader || '未指定' }}</text>
							<text class="date">{{ formatDate(item.createTime) }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 加载更多 -->
			<view class="load-more" v-if="hasMore" @click="loadMore">
				<text>加载更多</text>
			</view>
			<view class="no-more" v-else-if="projectList.length > 0">
				<text>已显示全部项目</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed, onMounted } from 'vue'
	import { searchProjects } from '../../api/index/index.js'
	
	const keyword = ref('')
	const projectList = ref([])
	const loading = ref(false)
	const hasSearched = ref(false)
	const pageNum = ref(1)
	const pageSize = ref(10)
	const total = ref(0)
	const hasMore = computed(() => projectList.value.length < total.value)
	
	// 搜索历史
	const historyKeywords = ref([])
	// 热门搜索词
	const hotKeywords = ref(['人工智能', '大数据', '区块链', '智慧医疗', '绿色能源'])
	
	// 格式化日期
	const formatDate = (dateString) => {
		if (!dateString) return '';
		const date = new Date(dateString);
		return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
	}
	
	// 返回上一页
	const goBack = () => {
		uni.navigateBack();
	}
	
	// 清除搜索历史
	const clearHistory = () => {
		historyKeywords.value = [];
		uni.setStorageSync('searchHistory', JSON.stringify([]));
	}
	
	// 使用历史关键词
	const useHistoryKeyword = (word) => {
		keyword.value = word;
		search();
	}
	
	// 保存搜索历史
	const saveSearchHistory = (word) => {
		if (!word.trim()) return;
		
		// 移除重复项
		const index = historyKeywords.value.findIndex(k => k === word);
		if (index !== -1) {
			historyKeywords.value.splice(index, 1);
		}
		
		// 添加到开头
		historyKeywords.value.unshift(word);
		
		// 最多保存10条
		if (historyKeywords.value.length > 10) {
			historyKeywords.value = historyKeywords.value.slice(0, 10);
		}
		
		// 保存到本地
		uni.setStorageSync('searchHistory', JSON.stringify(historyKeywords.value));
	}
	
	// 搜索
	const search = async () => {
		if (!keyword.value.trim() && hasSearched.value) return;
		
		// 重置分页
		pageNum.value = 1;
		projectList.value = [];
		
		await performSearch();
		
		// 保存搜索历史
		if (keyword.value.trim()) {
			saveSearchHistory(keyword.value);
		}
	}
	
	// 执行搜索
	const performSearch = async () => {
		if (!keyword.value.trim()) return;
		
		loading.value = true;
		hasSearched.value = true;
		
		try {
			const res = await searchProjects(keyword.value, pageNum.value, pageSize.value);
			
			if (res.code == 200) {
				const { records, total: totalCount } = res.data;
				console.log(res.data);
				if (pageNum.value === 1) {
					projectList.value = records || [];
				} else {
					projectList.value = [...projectList.value, ...(records || [])];
				}
				
				total.value = totalCount || 0;
			}
		} catch (error) {
			console.error('搜索失败:', error);
			uni.showToast({
				title: '搜索失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
		}
	}
	
	// 加载更多
	const loadMore = () => {
		if (loading.value || !hasMore.value) return;
		
		pageNum.value++;
		performSearch();
	}
	
	// 查看项目详情
	const toProjectDetail = (project) => {
		uni.navigateTo({
			url: `/pages/project/detail?id=${project.id}`
		});
	}
	
	onMounted(() => {
		// 加载搜索历史
		const history = uni.getStorageSync('searchHistory');
		if (history) {
			try {
				historyKeywords.value = JSON.parse(history);
			} catch (e) {
				historyKeywords.value = [];
			}
		}
	});
</script>

<style>
	.container {
		min-height: 100vh;
		background-color: #F8F8F8;
		padding-bottom: 30rpx;
	}
	
	.search-header {
		padding: 20rpx 30rpx;
		background-color: #FFFFFF;
	}
	
	.search-input {
		display: flex;
		align-items: center;
		background-color: #F2F2F2;
		border-radius: 36rpx;
		padding: 10rpx 20rpx;
	}
	
	.back-icon {
		padding: 0 10rpx;
	}
	
	.search-input input {
		flex: 1;
		height: 60rpx;
		padding: 0 20rpx;
		font-size: 28rpx;
	}
	
	.search-icon {
		padding: 0 10rpx;
	}
	
	.search-history, .hot-search {
		margin: 30rpx;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
	}
	
	.history-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}
	
	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}
	
	.clear-btn {
		font-size: 26rpx;
		color: #007AFF;
	}
	
	.history-tags, .hot-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		margin-top: 20rpx;
	}
	
	.history-tag, .hot-tag {
		padding: 10rpx 24rpx;
		background-color: #F2F2F2;
		border-radius: 30rpx;
		font-size: 26rpx;
		color: #333;
	}
	
	.hot-tag {
		background-color: #EFF6FF;
		color: #2979FF;
	}
	
	.loading-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 300rpx;
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
	
	.no-result {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}
	
	.no-result image {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 20rpx;
	}
	
	.no-result text {
		color: #999;
		font-size: 28rpx;
	}
	
	.search-results {
		padding: 0 30rpx;
	}
	
	.result-header {
		margin-bottom: 20rpx;
	}
	
	.result-count {
		font-size: 28rpx;
		color: #666;
	}
	
	.project-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}
	
	.project-item {
		display: flex;
		padding: 20rpx;
		background-color: #FFFFFF;
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
	}
	
	.project-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}
	
	.project-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}
	
	.project-meta {
		display: flex;
		justify-content: space-between;
		font-size: 24rpx;
		color: #999;
	}
	
	.leader-name {
		color: #666;
	}
	
	.load-more, .no-more {
		text-align: center;
		padding: 30rpx;
		font-size: 28rpx;
		color: #666;
	}
	
	.load-more {
		color: #007AFF;
	}
</style> 