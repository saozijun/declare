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
      :model="projectForm"
      :rules="rules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 18 }"
    >
      <a-form-item label="项目名称" name="name">
        <a-input v-model:value="projectForm.name" placeholder="请输入项目名称" />
      </a-form-item>
      <a-form-item label="项目分类" name="categoryId">
        <a-select v-model:value="projectForm.categoryId" placeholder="请选择项目分类">
          <a-select-option v-for="item in categoryList" :key="item.id" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="负责人" name="leader">
        <a-input v-model:value="projectForm.leader" placeholder="请输入负责人" />
      </a-form-item>
      <a-form-item label="联系方式" name="contact">
        <a-input v-model:value="projectForm.contact" placeholder="请输入联系方式" />
      </a-form-item>
      <a-form-item label="项目日期" name="date">
        <a-range-picker v-model:value="projectForm.date"  format="YYYY-MM-DD" :disabled-date="disabledDate"  @change="handleDateChange"/>
      </a-form-item>
      <a-form-item label="描述" name="description">
        <a-textarea v-model:value="projectForm.description" placeholder="请输入描述" :rows="4" />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-select v-model:value="projectForm.status" placeholder="请选择状态">
          <a-select-option value="0">未发布</a-select-option>
          <a-select-option value="1">已发布</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, watch } from "vue";
import { message } from 'ant-design-vue';
import { saveProject } from '~/api/project';
import { getCategoryList } from '~/api/projectCategory';
import dayjs, { Dayjs } from 'dayjs';

const formRef = ref();
const visible = ref(false);
const confirmLoading = ref(false);
const categoryList = ref([]);
const modalTitle = ref('新增项目');
const emits = defineEmits(["saveOk"]);

const projectForm = ref({
  id: null,
  name: '',
  categoryId: undefined,
  date: [],
  leader: '',
  contact: '',
  startDate: null,
  endDate: null,
  budget: null,
  description: '',
  status: '0'
});

const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'change' },
  ],
  categoryId: [
    { required: true, message: '请选择项目分类', trigger: 'change' },
  ],
  leader: [
    { required: true, message: '请输入负责人', trigger: 'change' },
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'change' },
  ],
  date: [
    { required: true, message: '请选择项目日期', trigger: 'change', type: 'array' },
  ],
};

const disabledDate = (current) => {
  return current && current < dayjs().endOf('day');
};

const afterClose = () => {
  formRef.value?.resetFields();
  projectForm.value = {
    id: null,
    name: '',
    categoryId: undefined,
    leader: '',
    contact: '',
    date: [],
    startDate: null,
    endDate: null,
    budget: null,
    description: '',
    status: '0'
  };
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    await saveProject(projectForm.value);
    visible.value = false;
    message.success(projectForm.value.id ? '更新成功' : '新增成功');
    emits('saveOk');
  } catch (error) {
    console.log(error);
  } finally {
    confirmLoading.value = false;
  }
};

const handleDateChange = (date, dateString) => {
  if (date && date.length === 2) {
    projectForm.value.startDate = dateString[0];
    projectForm.value.endDate = dateString[1];
  } else {
    projectForm.value.startDate = null;
    projectForm.value.endDate = null;
  }
};

const getCategories = async () => {
  try {
    const { data } = await getCategoryList();
    categoryList.value = data;
  } catch (error) {
    console.log(error);
  }
};

const open = async (record = {}) => {
  await getCategories();
  
  if (record.id) {
    modalTitle.value = '编辑项目';
    projectForm.value = { ...record };
    if (record.startDate && record.endDate) {
      projectForm.value.date = [
        dayjs(record.startDate, 'YYYY-MM-DD'),
        dayjs(record.endDate, 'YYYY-MM-DD')
      ];
    }
   
  } else {
    modalTitle.value = '新增项目';
    projectForm.value = {
      id: null,
      name: '',
      categoryId: undefined,
      leader: '',
      contact: '',
      startDate: null,
      endDate: null,
      budget: null,
      description: '',
      status: '0'
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