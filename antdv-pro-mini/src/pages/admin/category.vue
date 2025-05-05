<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col>
            <a-form-item name="name" label="分类名称">
              <a-input v-model:value="formModel.name" placeholder="请输入分类名称" />
            </a-form-item>
          </a-col>
          <a-col>
            <a-space flex justify-end w-full>
              <a-button :loading="loading" type="primary" @click="onSearch">
                查询
              </a-button>
              <a-button :loading="loading" @click="onReset">
                重置
              </a-button>
              <a-button type="primary" @click="open">
                <template #icon>
                  <PlusOutlined />
                </template>
                新增
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>
    <a-table :columns="columns" :data-source="tableData" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'operation'">
          <a-button style="padding: 0;" type="link" @click="open(record)">编辑</a-button>
          <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
            <a-button type="link" danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>
    
    <!-- 使用组件 -->
    <CategoryEdit ref="categoryEditRef" @saveOk="getList" />
  </div>
</template>

<script setup>
import { PlusOutlined } from '@ant-design/icons-vue';
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { getCategoryPage, deleteCategory } from '~/api/projectCategory';
import CategoryEdit from './components/CategoryEdit.vue';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  name: "",
});

// 使用组件
const categoryEditRef = ref(null);

onMounted(() => {
  getList();
});

const onPageChange = () => {
  getList();
};

const onSearch = () => {
  formModel.value.pageNum = 1;
  getList();
};

const onReset = () => {
  formModel.value = {
    pageNum: 1,
    pageSize: 10,
    name: ""
  };
  getList();
};

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await getCategoryPage(formModel.value);
    total.value = data.total;
    data.records.map((item, i) => { item.index = i + 1 });
    tableData.value = data.records;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const onDelete = async (id) => {
  try {
    await deleteCategory({ id });
    getList();
    message.success('删除成功');
  } catch (error) {
    console.log(error);
    message.error('删除失败');
  }
};

const open = (record = {}) => {
  categoryEditRef.value.open(record);
};

const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 80,
  },
  {
    title: '分类名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 200,
  },
];
</script>

<style lang="less" scoped>
.box {
  height: calc(100vh - 170px);
}

:deep(.ant-form-item) {
  margin-bottom: 0;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>