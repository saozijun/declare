export let baseURL = "http://localhost:9091"; // 访问的地址 
export const request = (options) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: baseURL + options.url,
			method: options.method || 'GET', 
			data: options.data || {},
			header: {
				'Authorization': uni.getStorageSync('token'), //自定义请求头信息 
			},
			success: (res) => {
				if (res.data.code == 200) {
					resolve(res.data)
				}else if(res.data.code == 401){
					uni.reLaunch({
						url: '/pages/login/login'
					});
				}else {
					reject(res)
					return uni.showToast({
						title: res.data.msg,
						icon: 'none'
					})
				}
			},
			fail: (err) => {
				console.log(err)
				reject(err)
			}
		})
	})
}
