<template>
	<view class="container">
		<view class="form-container">
			<view class="form-title">项目申报表</view>
			
			<view class="form-item">
				<text class="label">项目名称<text class="required">*</text></text>
				<input class="input" v-model="formData.projectName" placeholder="请输入项目名称" />
			</view>
			
			<view class="form-item">
				<text class="label">申报单位<text class="required">*</text></text>
				<input class="input" v-model="formData.organization" placeholder="请输入申报单位" />
			</view>
			
			<view class="form-item">
				<text class="label">项目负责人<text class="required">*</text></text>
				<input class="input" v-model="formData.leader" placeholder="请输入项目负责人" />
			</view>
			
			<view class="form-item">
				<text class="label">项目周期<text class="required">*</text></text>
				<input class="input" v-model="formData.period" placeholder="请输入项目周期" />
			</view>
			
			<view class="form-item">
				<text class="label">预算总额<text class="required">*</text></text>
				<input class="input" type="digit" v-model="formData.budget" placeholder="请输入预算总额" />
			</view>
			
			<view class="form-item">
				<text class="label">技术方案<text class="required">*</text></text>
				<textarea class="textarea" v-model="formData.technicalSolution" placeholder="请输入技术方案" />
			</view>
			
			<view class="form-item">
				<text class="label">预期成果<text class="required">*</text></text>
				<textarea class="textarea" v-model="formData.expectedResults" placeholder="请输入预期成果" />
			</view>
			
			<view class="form-item">
				<text class="label">风险识别<text class="required">*</text></text>
				<textarea class="textarea" v-model="formData.riskIdentification" placeholder="请输入风险识别" />
			</view>
			
			<view class="form-item">
				<text class="label">经济效益<text class="required">*</text></text>
				<textarea class="textarea" v-model="formData.economicBenefits" placeholder="请输入经济效益" />
			</view>
			
			<view class="submit-btn" @click="submitApplication">提交申报</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { submitProjectApplication } from '../../api/type/type.js'
import { onLoad } from "@dcloudio/uni-app";

const userInfo = ref(uni.getStorageSync('userInfo') || null)
const formData = ref({
	projectId: '',
	userId: userInfo.value?.id || '',
	projectName: '',
	organization: '',
	leader: '',
	period: '',
	budget: '',
	technicalSolution: '',
	expectedResults: '',
	riskIdentification: '',
	economicBenefits: ''
})

onLoad((option) => {
	formData.value.projectId = option.id
})

const submitApplication = async () => {
	// 表单验证
	if (!formData.value.projectName) {
		uni.showToast({
			title: '请输入项目名称',
			icon: 'none'
		})
		return
	}
	if (!formData.value.organization) {
		uni.showToast({
			title: '请输入申报单位',
			icon: 'none'
		})
		return
	}
	if (!formData.value.leader) {
		uni.showToast({
			title: '请输入项目负责人',
			icon: 'none'
		})
		return
	}
	if (!formData.value.period) {
		uni.showToast({
			title: '请输入项目周期',
			icon: 'none'
		})
		return
	}
	if (!formData.value.budget) {
		uni.showToast({
			title: '请输入预算总额',
			icon: 'none'
		})
		return
	}
	if (!formData.value.technicalSolution) {
		uni.showToast({
			title: '请输入技术方案',
			icon: 'none'
		})
		return
	}
	if (!formData.value.expectedResults) {
		uni.showToast({
			title: '请输入预期成果',
			icon: 'none'
		})
		return
	}
	if (!formData.value.riskIdentification) {
		uni.showToast({
			title: '请输入风险识别',
			icon: 'none'
		})
		return
	}
	if (!formData.value.economicBenefits) {
		uni.showToast({
			title: '请输入经济效益',
			icon: 'none'
		})
		return
	}

	try {
		const res = await submitProjectApplication(formData.value)
		if (res.code === 200) {
			uni.showToast({
				title: '申报成功',
				icon: 'success'
			})
			setTimeout(() => {
				uni.navigateBack()
			}, 1500)
		} else {
			uni.showToast({
				title: res.msg || '申报失败',
				icon: 'none'
			})
		}
	} catch (e) {
		console.error('提交申报失败', e)
		uni.showToast({
			title: '申报失败',
			icon: 'none'
		})
	}
}
</script>

<style>
.container {
	padding: 20rpx;
	background-color: #F8F8F8;
	min-height: 100vh;
}

.form-container {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx;
}

.form-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	text-align: center;
	margin-bottom: 40rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.label {
	font-size: 28rpx;
	color: #333;
	margin-bottom: 10rpx;
	display: block;
}

.required {
	color: #ff4d4f;
	margin-left: 4rpx;
}

.input {
	width: 100%;
	height: 80rpx;
	background-color: #F8F8F8;
	border-radius: 8rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}

.textarea {
	width: 100%;
	height: 200rpx;
	background-color: #F8F8F8;
	border-radius: 8rpx;
	padding: 20rpx;
	font-size: 28rpx;
}

.submit-btn {
	width: 100%;
	height: 80rpx;
	line-height: 80rpx;
	background-color: #007AFF;
	color: #ffffff;
	border-radius: 40rpx;
	font-size: 32rpx;
	text-align: center;
	margin-top: 40rpx;
}
</style> 