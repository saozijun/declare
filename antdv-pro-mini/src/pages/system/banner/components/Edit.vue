<template>
  <a-modal
    v-model:open="visible"
    :title="modelRef.id ? '编辑' : '新增'"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form
      ref="formRef"
      :model="modelRef"
      :rules="rules"
      :label-col="{style:{width: '60px'}}"
    >
      <a-form-item label="名称" name="name">
        <a-input v-model:value="modelRef.name" placeholder="请输入"/>
      </a-form-item>
      <a-form-item label="图片" name="url">
        <a-upload
            name="file"
              list-type="picture-card"
            :action="baseUrl + '/file/upload'"
            :show-upload-list="false"
            @change="handleChange"
          >
            <img class="banner-img" v-if="modelRef.url" :src="modelRef.url" alt="" />
            <div v-else style="font-size: 35px; color: #c1c1c1;"> + </div>
          </a-upload>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, computed } from "vue";
import { message } from 'ant-design-vue';
import { UploadOutlined } from "@ant-design/icons-vue";
import { save } from '~/api/banner'

const formRef = ref();
const visible = ref(false);
const confirmLoading = ref(false);
const baseUrl = import.meta.env.VITE_APP_BASE_URL;
const emits = defineEmits(["saveOk"]);

const modelRef = ref({
  name: "",
  url: "",
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'change' }],
  url: [{ required: true, message: '请上传图片', trigger: 'change' }]
};

const afterClose = () => {
  formRef.value?.resetFields();
};

const handleChange = (info) => {
  if (info.file.status === "done") {
    message.success(`上传成功`);
    modelRef.value.url = info.file.response;
  } else if (info.file.status === "error") {
    message.error(`上传失败`);
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
    console.log(error);
  } finally {
    confirmLoading.value = false;
  }
};

const open = async (row) => {
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
.banner-img{
  width: 100%;
  height: 100%;
  // object-fit: cover;
  border-radius: 4px;
}
</style>