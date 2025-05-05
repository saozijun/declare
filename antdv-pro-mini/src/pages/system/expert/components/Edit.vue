<template>
  <a-modal
    v-model:open="visible"
    width="800px"
    :title="modelRef.id ? '编辑' : '新增'"
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
        <a-form-item label="选择用户" name="userId" v-if="!modelRef.id">
        <a-select v-model:value="modelRef.userId" placeholder="请选择用户" @change="handleUserChange">
          <a-select-option v-for="user in availableUsers" :key="user.id" :value="user.id">
            {{ user.nickname }} ({{ user.username }})
          </a-select-option>
        </a-select>
      </a-form-item>
      </a-col>
      <a-col :span="!modelRef.id ? 12 : 24">
        <a-form-item label="专家姓名" name="name">
        <a-input v-model:value="modelRef.name" placeholder="请输入专家姓名"/>
      </a-form-item>
      </a-col>
    </a-row>
    <a-row :gutter="16">
      <a-col :span="12">
        <a-form-item label="性别" name="gender">
        <a-select v-model:value="modelRef.gender" placeholder="请选择性别">
          <a-select-option value="男">男</a-select-option>
          <a-select-option value="女">女</a-select-option>
        </a-select>
      </a-form-item>
      </a-col>
      <a-col :span="12">
    
        <a-form-item label="联系方式" name="phone">
        <a-input v-model:value="modelRef.phone" placeholder="请输入联系方式"/>
      </a-form-item>
      </a-col>
    </a-row>
  
      <a-form-item label="电子邮箱" name="email">
        <a-input v-model:value="modelRef.email" type="email" placeholder="请输入电子邮箱"/>
      </a-form-item>
      <a-form-item label="资质证明" name="qualification">
        <a-textarea v-model:value="modelRef.qualification" placeholder="请输入资质证明" :rows="4"/>
      </a-form-item>
      <a-form-item label="专业领域" name="expertise">
        <a-input v-model:value="modelRef.expertise" placeholder="请输入专业领域"/>
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
import { ref, onMounted } from "vue";
import { Form, message } from 'ant-design-vue';
import { save } from '~/api/expert'
import { getAvailableUsers } from '~/api/expert'

const formRef = ref();
const visible = ref(false);
const confirmLoading = ref(false);
const emits = defineEmits(["saveOk"]);
const availableUsers = ref([]);

const modelRef = ref({
  id: null,
  userId: null,
  name: "",
  gender: "",
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
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  name: [{ required: true, message: '请输入专家姓名', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系方式', trigger: 'change' }],
  email: [{ required: true, message: '请输入电子邮箱', trigger: 'change' }],
  expertise: [{ required: true, message: '请输入专业领域', trigger: 'change' }],
};

const afterClose = () => {
  formRef.value?.resetFields();
};

const handleUserChange = (userId) => {
  const selectedUser = availableUsers.value.find(user => user.id === userId);
  if (selectedUser) {
    modelRef.value.name = selectedUser.nickname;
    modelRef.value.email = selectedUser.email;
  }
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    await save(modelRef.value);
    message.success('操作成功');
    emits('saveOk');
    visible.value = false;
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoading.value = false;
  }
};

const open = async (row) => {
  if (!row.id) {
    // 获取可用的用户列表
    const response = await getAvailableUsers();
    availableUsers.value = response.data;
  }
  modelRef.value = JSON.parse(JSON.stringify(row));
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
</style> 