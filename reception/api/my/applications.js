import { request } from '../../request/index.js';

// 获取用户的项目申报列表
export const getProjectApplications = (userId, pageSize = 100) => {
	return request({
		url: `/project/application/page`,
		method: 'GET',
		data: {
			userId,
			pageSize
		}
	})
}

// 获取项目申报详情
export const getProjectApplicationDetail = (id) => {
	return request({
		url: `/project/application/getById`,
		method: 'GET',
		data: { id }
	})
}

// 获取申报项目的专家分配信息
export const getApplicationExperts = (applicationId) => {
	return request({
		url: `/expert/assignment/assigned-experts-detail`,
		method: 'GET',
		data: { applicationId }
	})
}

// 获取申报项目的评审结果
export const getApplicationReviewResult = (applicationId) => {
	return request({
		url: `/review/result`,
		method: 'GET',
		data: { applicationId }
	})
} 