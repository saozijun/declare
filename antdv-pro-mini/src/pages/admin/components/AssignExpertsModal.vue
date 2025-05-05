<template>
  <a-modal
    v-model:open="visible"
    title="分配专家"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form :model="assignForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="项目" name="projectName">
        <span>{{ assignForm.projectName }}</span>
      </a-form-item>
      <a-form-item label="专家" name="expertIds" :rules="[{ required: true, message: '请选择专家' }]">
        <a-select
          v-model:value="assignForm.expertIds"
          mode="multiple"
          placeholder="请选择专家"
          :options="expertList.map(item => ({ value: item.id, label: item.nickname }))"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref } from "vue";
import { message } from 'ant-design-vue';
import { assignExperts } from '~/api/project';
import { getExpertList } from '~/api/user';

const visible = ref(false);
const confirmLoading = ref(false);
const expertList = ref([]);
const emits = defineEmits(["saveOk"]);

const assignForm = ref({
  projectId: null,
  projectName: '',
  expertIds: []
});

const afterClose = () => {
  assignForm.value = {
    projectId: null,
    projectName: '',
    expertIds: []
  };
};

const handleOk = async () => {
  if (!assignForm.value.expertIds || assignForm.value.expertIds.length === 0) {
    message.error('请选择专家');
    return;
  }
  
  confirmLoading.value = true;
  try {
    await assignExperts(assignForm.value.projectId, assignForm.value.expertIds);
    visible.value = false;
    message.success('分配成功');
    emits('saveOk');
  } catch (error) {
    console.log(error);
    message.error('分配失败');
  } finally {
    confirmLoading.value = false;
  }
};

const getExperts = async () => {
  try {
    const { data } = await getExpertList();
    expertList.value = data;
  } catch (error) {
    console.log(error);
  }
};

const open = async (record) => {
  await getExperts();
  
  assignForm.value = {
    projectId: record.id,
    projectName: record.name,
    expertIds: []
  };
  
  visible.value = true;
};

defineExpose({
  open,
});
</script>

<style lang="less" scoped>
</style> 