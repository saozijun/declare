import { request } from '../../request/index.js';

// 获取分类下的项目列表
export const getCategoryProjects = (params) => {
	return request({
		url: `/project/category/projects`,
		method: 'GET',
		data: params
	})
}

// 获取分类详情
export const getCategoryDetail = (id) => {
	return request({
		url: `/project/category/getById`,
		method: 'GET',
		data: { id }
	})
} 

export function getCategoryList() {
	return request({
	  url: '/project/category/all',
	  method: 'get'
	})
  }

// 提交项目申报
export const submitProjectApplication = (data) => {
	return request({
		url: `/project/application/submit`,
		method: 'POST',
		data
	})
}

// 获取项目详情
export const getProjectDetail = (id) => {
	return request({
		url: `/project/getById`,
		method: 'GET',
		data: { id }
	})
}