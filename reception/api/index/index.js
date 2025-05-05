import { request } from '../../request/index.js';

// 获取轮播图
export const getBanner = () => {
	return request({
		url: '/banner/list',
		method: 'GET'
	})
}

// 获取分类列表
export const getCategories = () => {
	return request({
		url: '/project/category/list',
		method: 'GET'
	})
}

// 获取项目列表
export const getProjects = (params) => {
	return request({
		url: `/project/page`,
		method: 'GET',
		data: params
	})
}

// 搜索项目
export const searchProjects = (keyword, pageNum = 1, pageSize = 10) => {
	return request({
		url: `/project/search`,
		method: 'GET',
		data: { 
			keyword,
			pageNum,
			pageSize
		}
	})
}

// 获取推荐项目
export const getRecommendations = (limit = 5) => {
	return request({
		url: `/project/recommendations`,
		method: 'GET',
		data: { limit }
	})
}

// 更新用户信息
export const updateUser = (data) => {
	return request({
		url: `/user/update/profile`,
		method: 'POST',
		data
	})
}

