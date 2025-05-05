<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col>
            <a-form-item name="name" label="专家姓名">
              <a-input v-model="formModel.name" placeholder="请输入专家姓名" />
            </a-form-item>
          </a-col>
          <a-col>
            <a-form-item name="expertise" label="专业领域">
              <a-input v-model="formModel.expertise" placeholder="请输入专业领域" />
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
        <template v-if="column.key === 'status'">
          <span>
            <a-tag :color="getStatusColor(record.status)">
              {{ record.status }}
            </a-tag>
          </span>
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-button style="padding: 0;" type="link" @click="open(record)">编辑</a-button>
          <a-button type="link" @click="handleStatusUpdate(record.id, '已通过')" v-if="record.status === '审核中'">通过</a-button>
          <a-button type="link" style="padding: 0;" @click="handleStatusUpdate(record.id, '已拒绝')" v-if="record.status === '审核中'">拒绝</a-button>
          <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
            <a-button  type="link" danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
    <Edit ref="editRef" @saveOk="getList"></Edit>
  </div>
</template>

<script setup>
import { PlusOutlined } from '@ant-design/icons-vue';
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { list, del, updateStatus } from '~/api/expert'
import Edit from './components/Edit.vue';

const editRef = ref(null)
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formModel = ref({
    name: "",
    expertise: ""
})

onMounted(() => {
  getList()
})

const onPageChange = (page) => {
  getList()
}

const onSearch = () => {
  getList()
}

const onReset = () => {
  formModel.value = {
    name: "",
    expertise: ""
  }
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await list(formModel.value)
    data.map((item,i) => {item.index = i + 1})
    tableData.value = data
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}

const onDelete = async (id) => {
  try {
    await del({id})
    getList()
    message.success('删除成功');
  } catch (error) {
    console.log(error)
  }
}

const handleStatusUpdate = async (id, status) => {
  try {
    await updateStatus(id, status)
    getList()
    message.success('状态更新成功');
  } catch (error) {
    console.log(error)
  }
}

const getStatusColor = (status) => {
  switch(status) {
    case '审核中': return 'orange';
    case '已通过': return 'green';
    case '已拒绝': return 'red';
    default: return 'blue';
  }
}

const open = (record = {}) => {
  editRef.value.open(record)
}

const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 80,
  },
  {
    title: '专家姓名',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
  },
  {
    title: '联系方式',
    dataIndex: 'phone',
    key: 'phone',
  },
  {
    title: '电子邮箱',
    dataIndex: 'email',
    key: 'email',
  },
  {
    title: '专业领域',
    dataIndex: 'expertise',
    key: 'expertise',
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
    width: 300,
  },
];
</script>

<style lang="less" scoped>
.box {
  height: calc(100vh - 170px);
}

:deep( .ant-form-item ) {
  margin-bottom: 0;
}
.pagination{
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style> 