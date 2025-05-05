<template>
	<view class="container">
		<view class="form-item">
			<text class="label">头像</text>
			<view class="avatar-wrapper" @click="chooseImage">
				<image class="avatar" :src="formData.avatarUrl || '../../static/avatar.png'" mode="aspectFill"></image>
				<view class="change-text">点击更换</view>
			</view>
		</view>

		<view class="form-item">
			<text class="label">昵称</text>
			<input class="input" v-model="formData.nickname" placeholder="请输入昵称" maxlength="20" />
		</view>

		<view class="form-item">
			<text class="label">邮箱</text>
			<input class="input" v-model="formData.email" placeholder="请输入邮箱" type="email" />
		</view>

		<button class="submit-btn" @click="saveProfile">保存</button>
	</view>
</template>

<script setup>
	import {ref, onMounted} from 'vue'
	import { baseURL } from '../../request/index.js'
	import { updateUser } from '../../api/index/index.js'
	const isUpImg = ref(false)
	const formData = ref({
		avatarUrl: '',
		nickname: '',
		email: ''
	})

	onMounted(() => {
		const userInfo = uni.getStorageSync('userInfo')
		if (userInfo) {
			formData.value = {
				id: userInfo.id,
				avatarUrl: userInfo.avatarUrl,
				nickname: userInfo.nickname,
				email: userInfo.email || ''
			}
		} else {
			uni.reLaunch({
				url: '/pages/login/login'
			});
		}
	})

	// 选择图片
	const chooseImage = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['compressed'],
			sourceType: ['album', 'camera'],
			success: (res) => {
				formData.value.avatarUrl = res.tempFilePaths[0]
				isUpImg.value = true
			}
		})
	}

	// 保存资料
	const saveProfile = async () => {
		if (!formData.value.nickname.trim()) {
			uni.showToast({
				title: '请输入昵称',
				icon: 'none'
			})
			return
		}
		
		if (formData.value.email && !validateEmail(formData.value.email)) {
			uni.showToast({
				title: '邮箱格式不正确',
				icon: 'none'
			})
			return
		}
		
		try {
			// 如果头像已更改，则先上传图片
			if (isUpImg.value) {
				await new Promise((resolve, reject) => {
					uni.uploadFile({
						url: baseURL + '/file/upload',
						filePath: formData.value.avatarUrl,
						name: 'file',
						success: (uploadRes) => {
							formData.value.avatarUrl = uploadRes.data
							isUpImg.value = false
							resolve()
						},
						fail: (err) => {
							reject(err)
						}
					})
				})
			}
			// 更新用户信息
			await updateUser(formData.value)
			
			// 更新本地存储
			const userInfo = uni.getStorageSync('userInfo')
			userInfo.nickname = formData.value.nickname
			userInfo.avatarUrl = formData.value.avatarUrl
			userInfo.email = formData.value.email
			uni.setStorageSync('userInfo', userInfo)

			uni.showToast({
				title: '保存成功',
				icon: 'success',
				duration: 2000,
				success: () => {
					setTimeout(() => {
						uni.navigateBack()
					}, 2000)
				}
			})
		} catch (error) {
			uni.showToast({
				title: '保存失败',
				icon: 'none'
			})
			console.error('保存失败:', error)
		}
	}
	
	// 验证邮箱格式
	const validateEmail = (email) => {
		const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
		return regex.test(email)
	}
</script>

<style>
	.container {
		padding: 30rpx;
		background-color: #f5f6f7;
		min-height: 100vh;
	}

	.form-item {
		background-color: #fff;
		padding: 30rpx;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
	}

	.label {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.avatar-wrapper {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 20rpx 0;
	}

	.avatar {
		width: 160rpx;
		height: 160rpx;
		border-radius: 50%;
		margin-bottom: 20rpx;
	}

	.change-text {
		font-size: 24rpx;
		color: #409eff;
	}

	.input {
		width: 100%;
		height: 80rpx;
		font-size: 28rpx;
		color: #333;
	}

	.submit-btn {
		margin-top: 60rpx;
		background-color: #409eff;
		color: #fff;
		border-radius: 12rpx;
		font-size: 32rpx;
	}

	.submit-btn:active {
		opacity: 0.8;
	}
</style>