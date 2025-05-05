import { request } from '../../request/index.js';

// 登录
export function login(data) {
	return request({
		url: '/user/login',
		method: 'post',
		data
	})
}

// 注册
export function register(data) {
	return request({
		url: '/user/register',
		method: 'post',
		data
	})
}

// 获取角色
export function roleList(params) {
	return request({
		url: '/role',
		method: 'GET',
		params
	})
}


// 修改用户信息
export function updateUser(data) {
	return request({
		url: '/user/update',
		method: 'post',
		data
	})
}