<template>
  <a-modal
    v-model:open="visible"
    :title="modalTitle"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form
      ref="formRef"
      :model="categoryForm"
      :rules="rules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 18 }"
    >
      <a-form-item label="分类名称" name="name">
        <a-input v-model:value="categoryForm.name" placeholder="请输入分类名称" />
      </a-form-item>
      <a-form-item label="描述" name="description">
        <a-textarea v-model:value="categoryForm.description" placeholder="请输入描述" :rows="4" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref } from "vue";
import { message } from 'ant-design-vue';
import { saveCategory } from '~/api/projectCategory';

const formRef = ref();
const visible = ref(false);
const confirmLoading = ref(false);
const modalTitle = ref('新增分类');
const emits = defineEmits(["saveOk"]);

const categoryForm = ref({
  id: null,
  name: '',
  description: ''
});

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'change' },
  ],
};

const afterClose = () => {
  formRef.value?.resetFields();
  categoryForm.value = {
    id: null,
    name: '',
    description: ''
  };
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    await saveCategory(categoryForm.value);
    visible.value = false;
    message.success(categoryForm.value.id ? '更新成功' : '新增成功');
    emits('saveOk');
  } catch (error) {
    console.log(error);
  } finally {
    confirmLoading.value = false;
  }
};

const open = (record = {}) => {
  if (record.id) {
    modalTitle.value = '编辑分类';
    categoryForm.value = { ...record };
  } else {
    modalTitle.value = '新增分类';
    categoryForm.value = {
      id: null,
      name: '',
      description: ''
    };
  }
  
  visible.value = true;
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped>
</style>