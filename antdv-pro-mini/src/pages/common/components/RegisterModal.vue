<template>
  <a-modal
    v-model:open="visible"
    width="800px"
    title="专家注册"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form
      ref="formRef"
      :model="modelRef"
      :rules="rules"
      :label-col="{style:{width: '80px'}}"
    >
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="用户名" name="username">
            <a-input v-model:value="modelRef.username" placeholder="请输入用户名"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="密码" name="password">
            <a-input-password v-model:value="modelRef.password" placeholder="请输入密码"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="专家姓名" name="name">
            <a-input v-model:value="modelRef.name" placeholder="请输入专家姓名"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="性别" name="gender">
            <a-select v-model:value="modelRef.gender" placeholder="请选择性别">
              <a-select-option value="男">男</a-select-option>
              <a-select-option value="女">女</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="联系方式" name="phone">
            <a-input v-model:value="modelRef.phone" placeholder="请输入联系方式"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="电子邮箱" name="email">
            <a-input v-model:value="modelRef.email" type="email" placeholder="请输入电子邮箱"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item label="专业领域" name="expertise">
        <a-input v-model:value="modelRef.expertise" placeholder="请输入专业领域"/>
      </a-form-item>
      <a-form-item label="资质证明" name="qualification">
        <a-textarea v-model:value="modelRef.qualification" placeholder="请输入资质证明" :rows="4"/>
      </a-form-item>
      <a-form-item label="教育背景" name="education">
        <a-textarea v-model:value="modelRef.education" placeholder="请输入教育背景" :rows="4"/>
      </a-form-item>
      <a-form-item label="工作经验" name="workExperience">
        <a-textarea v-model:value="modelRef.workExperience" placeholder="请输入工作经验" :rows="4"/>
      </a-form-item>
      <a-form-item label="主要成就" name="achievements">
        <a-textarea v-model:value="modelRef.achievements" placeholder="请输入主要成就" :rows="4"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref } from "vue";
import { message } from 'ant-design-vue';
import { expertRegister } from '~/api/common/login'

const formRef = ref();
const visible = ref(false);
const confirmLoading = ref(false);
const emits = defineEmits(["registerSuccess"]);

const modelRef = ref({
  username: "",
  password: "",
  name: "",
  gender: null,
  phone: "",
  email: "",
  qualification: "",
  expertise: "",
  education: "",
  workExperience: "",
  achievements: "",
  status: "审核中"
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入专家姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  email: [{ required: true, message: '请输入电子邮箱', trigger: 'blur' }],
  expertise: [{ required: true, message: '请输入专业领域', trigger: 'blur' }],
  qualification: [{ required: true, message: '请输入资质证明', trigger: 'blur' }],
  education: [{ required: true, message: '请输入教育背景', trigger: 'blur' }],
  workExperience: [{ required: true, message: '请输入工作经验', trigger: 'blur' }],
  achievements: [{ required: true, message: '请输入主要成就', trigger: 'blur' }],
};

const afterClose = () => {
  formRef.value?.resetFields();
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    await expertRegister(modelRef.value);
    message.success('注册成功，请登录');
    emits('registerSuccess');
    visible.value = false;
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoading.value = false;
  }
};

const open = () => {
  visible.value = true;
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped>
.ant-form{
  margin-top: 20px;
}
:deep(textarea){
  height: 60px;
}
</style> 