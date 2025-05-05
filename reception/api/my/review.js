import { request } from '../../request/index.js';

// 获取当前用户的已评审项目申报列表
export const getMyProjectApplications = () => {
	const userInfo = uni.getStorageSync('userInfo') || {};
	return request({
		url: `/application/reviewed`,
		method: 'GET',
		data: {
			userId: userInfo.id
		}
	});
}

// 获取当前用户的已完成评审项目申报列表（status = 4）
export const getMyCompletedReviews = () => {
	const userInfo = uni.getStorageSync('userInfo') || {};
	return request({
		url: `/project/application/my-completed-reviews`,
		method: 'GET',
		data: {
			userId: userInfo.id,
			status: '4'
		}
	});
}

// 获取项目申报详情
export const getProjectApplicationDetail = (id) => {
	return request({
		url: `/project/application/getById`,
		method: 'GET',
		data: { id }
	})
}

// 获取申报项目的评审结果
export const getApplicationReviewResult = (applicationId) => {
	return request({
		url: `/expert/review/result`,
		method: 'GET',
		data: { applicationId }
	})
}

// 获取申报项目的专家详情
export const getApplicationExperts = (applicationId) => {
	return request({
		url: `/expert/assignment/application-experts`,
		method: 'GET',
		data: { applicationId }
	})
} 