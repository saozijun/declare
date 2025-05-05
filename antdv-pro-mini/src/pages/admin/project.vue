<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col :span="6">
            <a-form-item name="name" label="项目名称">
              <a-input v-model:value="formModel.name" placeholder="请输入项目名称" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item name="categoryId" label="项目分类">
              <a-select v-model:value="formModel.categoryId" placeholder="请选择项目分类" allowClear>
                <a-select-option v-for="item in categoryList" :key="item.id" :value="item.id">
                  {{ item.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item name="status" label="状态">
              <a-select v-model:value="formModel.status" placeholder="请选择状态" allowClear>
                <a-select-option value="0">未发布</a-select-option>
                <a-select-option value="1">已发布</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
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
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-button 
          style="padding: 0;" 
            type="link" 
            @click="handlePublish(record)" 
            v-if="record.status === '0'"
          >
            发布
          </a-button>
          <a-button 
          style="padding: 0;" 
            type="link" 
            @click="handleUnpublish(record)" 
            v-if="record.status === '1'"
          >
            取消发布
          </a-button>
          <a-button type="link" @click="open(record)">编辑</a-button>

          <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
            <a-button  style="padding: 0;"  type="link" danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>

    <!-- 使用组件 -->
    <ProjectEdit ref="projectEditRef" @saveOk="getList" />
  </div>
</template>

<script setup>
import { PlusOutlined } from '@ant-design/icons-vue';
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { getProjectPage, deleteProject, publishProject, unpublishProject } from '~/api/project';
import { getCategoryList } from '~/api/projectCategory';
import ProjectEdit from './components/ProjectEdit.vue';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const categoryList = ref([]);
const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  name: "",
  categoryId: undefined,
  status: undefined
});

// 使用组件
const projectEditRef = ref(null);

onMounted(async () => {
  try {
    const { data } = await getCategoryList();
    categoryList.value = data;
  } catch (error) {
    console.log(error);
  }
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
    name: "",
    categoryId: undefined,
    status: undefined
  };
  getList();
};

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await getProjectPage(formModel.value);
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
    await deleteProject({ id });
    getList();
    message.success('删除成功');
  } catch (error) {
    console.log(error);
    message.error('删除失败');
  }
};

const open = (record = {}) => {
  projectEditRef.value.open(record);
};

const handlePublish = async (record) => {
  try {
    await publishProject(record.id);
    message.success('发布成功');
    getList();
  } catch (error) {
    console.log(error);
    message.error('发布失败');
  }
};

const handleUnpublish = async (record) => {
  try {
    await unpublishProject(record.id);
    message.success('取消发布成功');
    getList();
  } catch (error) {
    console.log(error);
    message.error('取消发布失败');
  }
};

const getStatusText = (status) => {
  switch (status) {
    case '0': return '未发布';
    case '1': return '已发布';
    default: return '未知状态';
  }
};

const getStatusColor = (status) => {
  switch (status) {
    case '0': return 'orange';
    case '1': return 'green';
    default: return 'default';
  }
};

const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 60,
  },
  {
    title: '项目名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '项目分类',
    dataIndex: 'categoryName',
    key: 'categoryName',
  },
  {
    title: '负责人',
    dataIndex: 'leader',
    key: 'leader',
    width: 80,
  },
  {
    title: '联系方式',
    dataIndex: 'contact',
    key: 'contact',
  },
  {
    title: '开始日期',
    dataIndex: 'startDate',
    key: 'startDate',
  }, 
  {
    title: '结束日期',
    dataIndex: 'endDate',
    key: 'endDate',
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 180,
  },
];
</script>

<style lang="less" scoped>
.box {
  height: calc(100vh - 170px);
}


.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>