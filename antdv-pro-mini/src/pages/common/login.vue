<script setup>
import { h } from 'vue'
import { AlipayCircleFilled, LockOutlined, MobileOutlined, TaobaoCircleFilled, UserOutlined, WeiboCircleFilled, SwapOutlined, SmileOutlined } from '@ant-design/icons-vue'
import GlobalLayoutFooter from '~/layouts/components/global-footer/index.vue'
import { login } from '~/api/common/login'
import { useAuthorization } from '~/composables/authorization'
import { useUserStore } from '~@/stores/user'
import { notification } from 'ant-design-vue';
import RegisterModal from './components/RegisterModal.vue'

const token = useAuthorization()
const captchaImg = ref( import.meta.env.VITE_APP_BASE_URL + '/captcha');
const appStore = useAppStore()
const { layoutSetting } = storeToRefs(appStore)
const router = useRouter()
const loginModel = reactive({
  username: undefined,
  password: undefined,
  code: undefined
})
const role = ref(null)
const formRef = shallowRef()
const codeLoading = shallowRef(false)
const resetCounter = 60
const submitLoading = shallowRef(false)
const registerModalRef = shallowRef()

const setRole = (val) => {
  if (role.value != val) {
    formRef.value?.resetFields()
    loginModel.username = undefined
    loginModel.password = undefined
    loginModel.code = undefined
  }
  role.value = val
}
const changeSrc = async () => {
  captchaImg.value = captchaImg.value + '?d='+Date.now()
};

const { counter, pause, reset, resume, isActive } = useInterval(1000, {
  controls: true,
  immediate: false,
  callback(count) {
    if (count) {
      if (count === resetCounter)
        pause()
    }
  },
})

const openRegisterModal = () => {
  registerModalRef.value?.open()
}

const handleRegisterSuccess = () => {
  notification.success({
    message: '注册成功',
    description: '请使用新注册的账号登录',
    duration: 2
  })
}

const submit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    let loginData = {
      username: loginModel.username,
      password: loginModel.password,
      code: loginModel.code,
    }
    const { data } = await login(loginData)
    
    // 设置用户信息
    const userStore = useUserStore()
    userStore.userInfo = data
    
    // 清除之前的路由数据
    userStore.routerData = null
    
    // 生成动态路由
    const currentRoute = await userStore.generateDynamicRoutes()
    
    // 添加新路由
    await router.addRoute(currentRoute)
    
    // 设置 token
    token.value = data.token

    notification.success({
      message: '登录成功',
      description: '欢迎回来',
      duration: 2
    })

    // 在登录成功后添加延时
    setTimeout(() => {
      router.replace('/')
    }, 100)
  }
  catch (e) {
    changeSrc()
    console.warn(e)
  }
  finally {
    submitLoading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-main">
        <!-- 左侧装饰区域 -->
        <div class="login-left">
          <div class="left-content">
            <div class="decoration-image">
              <img src="/src/assets/ys.png" alt="">
            </div>
          </div>
        </div>

        <!-- 右侧登录区域 -->
        <div class="login-right">
          <div class="login-form-wrapper">
            <div class="login-header">
              <h4>🚔️ - 欢迎登录 - 🚔️</h4>
              <h1>项目评审系统</h1>
            </div>
            <a-form ref="formRef" :model="loginModel" class="login-form">
              <a-form-item name="username" :rules="[{ required: true, message: '请输入用户名' }]">
                <a-input v-model:value="loginModel.username" size="large" placeholder="请输入用户名">
                  <template #prefix>
                    <UserOutlined />
                  </template>
                </a-input>
              </a-form-item>

              <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
                <a-input-password v-model:value="loginModel.password" size="large" placeholder="请输入密码">
                  <template #prefix>
                    <LockOutlined />
                  </template>
                </a-input-password>
              </a-form-item>

              <a-form-item name="code" :rules="[{ required: true, message: '请输入验证码' }]" v-if="false">
                <div class="captcha-wrapper">
                  <a-input
                    v-model:value="loginModel.code"
                    size="large"
                    placeholder="请输入验证码"
                    style="width: 60%"
                  />
                  <img
                    :src="captchaImg"
                    alt="验证码"
                    class="captcha-image"
                    @click="changeSrc"
                  />
                </div>
              </a-form-item>

              <a-form-item>
                <a-button type="primary" size="large" :loading="submitLoading" class="submit-btn" @click="submit">
                  登录
                </a-button>
              </a-form-item>
              
              <div class="register-link">
                <span>没有账号？</span>
                <a @click="openRegisterModal">注册专家账号</a>
              </div>
            </a-form>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <div class="login-footer">
      <GlobalLayoutFooter :copyright="layoutSetting.copyright">
        <template #renderFooterLinks>
          <footer-links />
        </template>
      </GlobalLayoutFooter>
    </div>
    
    <!-- 注册弹窗 -->
    <RegisterModal ref="registerModalRef" @register-success="handleRegisterSuccess" />
  </div>
</template>

<style lang="less" scoped>
.login-container {
  min-height: 100vh;
  background-image: url('/src/assets/bg.png');
  background-size: cover;
  background-repeat: no-repeat;
  position: relative;
  display: flex;
  flex-direction: column;
}

.theme-switch {
  position: absolute;
  top: 24px;
  right: 24px;
  cursor: pointer;
  z-index: 10;

  .icon {
    font-size: 20px;
    color: var(--text-color);
  }
}

.login-wrapper {
  flex: 1;
  padding: 40px 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-main{
  max-width: 900px;
  height: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border-radius: 10px;
  background: #ffffff94;
  backdrop-filter: blur(10px);
  position: relative;
}

.login-left {
  display: none;
  width: 600px;
  height: 100%;
  .left-content {
    height: 100%;
    .decoration-image {
      width: 100%;
      height: 100%;
      // background-image: url('/src/assets/ys.png');
      // background-size: cover;
      // background-repeat: no-repeat;
      // background-position: center;
      display: flex;
      align-items: center;
      justify-content: center;
      img{
        width: 400px;
      }
    }
  }
}

.login-right {
  width: 100%;
  min-width: 400px;
  padding: 40px;

  .login-form-wrapper {
    padding: 25px;
    border-radius: 8px;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
  font-family: "宋体";
  h3{
    color: #252525;
    margin-bottom: 20px;
  }
  .logo {
    width: 48px;
    height: 48px;
    margin-bottom: 16px;
  }

  h1 {
    font-size: 24px;
    color: var(--text-color);
    font-weight: 600;
  }
}

.login-form {
  margin-top: 10px;
  .submit-btn {
    width: 100%;
  }
}

.login-footer {
  padding: 20px;
  text-align: center;
}

@media (min-width: 1024px) {
  .login-left {
    display: block;
  }

  .login-right {
    width: 400px;
  }
}

.captcha-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.captcha-image {
  width: 35%;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
}

.register-link {
  text-align: center;
  margin-top: 10px;
  a {
    color: #1890ff;
    cursor: pointer;
    margin-left: 5px;
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
