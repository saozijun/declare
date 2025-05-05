<template>
	<view class="container">
		<!-- 顶部标题 -->
		<view class="header">
			<text class="title">{{ isLogin ? '登录' : '注册' }}</text>
			<text class="subtitle">欢迎使用项目评审系统</text>
		</view>

		<!-- 登录/注册表单 -->
		<view class="form-box" >
			<view class="input-item" v-if="!isLogin">
				<text class="label">昵称</text>
				<input type="text" 
					v-model="formData.nickname" 
					placeholder="请输入昵称" 
					maxlength="20"
					placeholder-class="placeholder"/>
			</view>
			<!-- 账号输入 -->
			<view class="input-item">
				<text class="label">账号</text>
				<input type="text" 
					v-model="formData.username" 
					placeholder="请输入账号" 
					maxlength="20"
					placeholder-class="placeholder"/>
			</view>

			<!-- 密码输入 -->
			<view class="input-item">
				<text class="label">密码</text>
				<view class="password-input">
					<input :type="showPassword ? 'text' : 'password'"
						v-model="formData.password" 
						placeholder="请输入密码" 
						maxlength="20"
						placeholder-class="placeholder"/>
					<text class="eye-btn" @click="togglePassword">
						{{showPassword ? '隐藏' : '显示'}}
					</text>
				</view>
			</view>

			<!-- 登录/注册按钮 -->
			<button class="login-btn" 
				:class="{ active: isFormValid }"
				@click="handleSubmit">
				<text>{{ isLogin ? '登录' : '注册' }}</text>
			</button>

			<!-- 切换登录/注册 -->
			<view class="switch-mode" @click="switchMode">
				<text>{{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { login, register, roleList } from '../../api/login/index.js'

// 是否为登录模式
const isLogin = ref(true)
const roles = ref([])
const selectedRoleIndex = ref(0)

// 表单数据
const formData = ref({
	username: '',
	password: '',
	nickname: ''
})

// 是否显示密码
const showPassword = ref(false)

// 计算属性
const isFormValid = computed(() => {
	if (isLogin.value) {
		return formData.value.username && formData.value.password
	}
	return formData.value.username && 
		formData.value.password 
})

// 切换密码显示
const togglePassword = () => {
	showPassword.value = !showPassword.value
}

// 切换登录/注册模式
const switchMode = () => {
	isLogin.value = !isLogin.value
	formData.value = {
		username: '',
		password: '',
		nickname: ''
	}
}


// 提交处理
const handleSubmit = async () => {
	if (!isFormValid.value) {
		uni.showToast({
			title: '请填写完整信息',
			icon: 'none'
		})
		return
	}

	uni.showLoading({
		title: isLogin.value ? '登录中...' : '注册中...'
	})

	if (isLogin.value) {
		const res = await login(formData.value)
		uni.setStorageSync('userInfo', res.data)
		uni.setStorageSync('token', res.data.token)
		uni.switchTab({
			url: '/pages/index/index'
		})
	} else {
		let temp = JSON.parse(JSON.stringify(formData.value))
		await register(temp)
		uni.showToast({
			title: '注册成功',
			icon: 'success'
		})
		isLogin.value = true
	}
	uni.hideLoading()
}

onMounted(() => {
	
})
</script>

<style>
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 30%);
	padding: 0 40rpx;
}

/* 顶部标题 */
.header {
	padding: 220rpx 0 80rpx;
	
}

.title {
	font-size: 56rpx;
	font-weight: bold;
	background: linear-gradient(45deg, #2b5876, #4e4376);
	-webkit-background-clip: text;
	color: transparent;
	margin-bottom: 16rpx;
	display: block;
}

.subtitle {
	font-size: 28rpx;
	color: #666;
	letter-spacing: 2rpx;
}

/* 表单区域 */
.form-box {
	padding: 40rpx 0;
}

.input-item {
	margin-bottom: 40rpx;
}

.label {
	font-size: 28rpx;
	color: #2b5876;
	margin-bottom: 20rpx;
	display: block;
	font-weight: 500;
}

.input-item input {
	width: 100%;
	height: 88rpx;
	background: rgba(255, 255, 255, 0.8);
	border: 2rpx solid #e6f0fb;
	border-radius: 12rpx;
	padding: 0 30rpx;
	font-size: 30rpx;
	box-sizing: border-box;
	transition: all 0.3s;
}

.input-item input:focus {
	border-color: #2b5876;
	background: #ffffff;
	box-shadow: 0 4rpx 12rpx rgba(43, 88, 118, 0.1);
}

.password-input {
	position: relative;
}

.password-input input {
	padding-right: 120rpx;
}

.eye-btn {
	position: absolute;
	right: 20rpx;
	top: 50%;
	transform: translateY(-50%);
	font-size: 28rpx;
	color: #2b5876;
	padding: 20rpx;
}

.placeholder {
	color: #bbb;
}

/* 登录按钮 */
.login-btn {
	width: 100%;
	height: 88rpx;
	line-height: 88rpx;
	text-align: center;
	background: #f0f0f0;
	font-size: 32rpx;
	border-radius: 44rpx;
	margin: 60rpx 0;
	transition: all 0.3s;
	position: relative;
	overflow: hidden;
}

.login-btn::after {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: linear-gradient(45deg, #2b5876, #4e4376);
	opacity: 0;
	transition: opacity 0.3s;
}

.login-btn.active::after {
	opacity: 1;
}

.login-btn text {
	position: relative;
	z-index: 1;
	color: #999;
}

.login-btn.active text {
	color: #fff;
}

/* 按钮点击效果 */
.login-btn.active:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 8rpx rgba(43, 88, 118, 0.2);
}

.eye-btn:active {
	opacity: 0.7;
}

.picker-view {
	width: 100%;
	height: 88rpx;
	background: rgba(255, 255, 255, 0.8);
	border: 2rpx solid #e6f0fb;
	border-radius: 12rpx;
	padding: 0 30rpx;
	font-size: 30rpx;
	line-height: 88rpx;
	box-sizing: border-box;
}

.switch-mode {
	text-align: center;
	font-size: 28rpx;
	color: #2b5876;
	padding: 20rpx;
}

.switch-mode:active {
	opacity: 0.7;
}
</style> 