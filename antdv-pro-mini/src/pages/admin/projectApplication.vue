<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col :span="6">
            <a-form-item name="projectName" label="项目名称">
              <a-input v-model:value="formModel.projectName" placeholder="请输入项目名称" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item name="organization" label="申报单位">
              <a-input v-model:value="formModel.organization" placeholder="请输入申报单位" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item name="status" label="状态">
              <a-select v-model:value="formModel.status" placeholder="请选择状态" allowClear>
                <a-select-option value="0">待审核</a-select-option>
                <a-select-option value="1">已通过</a-select-option>
                <a-select-option value="2">已拒绝</a-select-option>
                <a-select-option value="3">已分配</a-select-option>
                <a-select-option value="4">已评审</a-select-option>
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
          <a-button style="padding: 0;" type="link" @click="openDetail(record)">查看详情</a-button>
          <a-button 

            type="link" 
            @click="handleApprove(record)" 
            v-if="record.status === '0'"
          >
            通过
          </a-button>
          <a-button 
            style="padding: 0;" 
            type="link" 
            @click="handleReject(record)" 
            v-if="record.status === '0'"
          >
            拒绝
          </a-button>
          <a-button 
            type="link" 
            @click="handleAssignExperts(record)" 
            v-if="record.status === '1'"
          >
            分配专家
          </a-button>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>

    <!-- 申报详情弹窗 -->
    <a-modal
      v-model:visible="detailVisible"
      title="申报详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions bordered>
        <a-descriptions-item label="项目名称" :span="3">{{ detailData.projectName }}</a-descriptions-item>
        <a-descriptions-item label="申报单位" :span="3">{{ detailData.organization }}</a-descriptions-item>
        <a-descriptions-item label="项目负责人" :span="3">{{ detailData.leader }}</a-descriptions-item>
        <a-descriptions-item label="项目周期" :span="3">{{ detailData.period }}</a-descriptions-item>
        <a-descriptions-item label="预算总额" :span="3">{{ detailData.budget }} 元</a-descriptions-item>
        <a-descriptions-item label="技术方案" :span="3">
          <div style="white-space: pre-wrap;">{{ detailData.technicalSolution }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="预期成果" :span="3">
          <div style="white-space: pre-wrap;">{{ detailData.expectedResults }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="风险识别" :span="3">
          <div style="white-space: pre-wrap;">{{ detailData.riskIdentification }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="经济效益" :span="3">
          <div style="white-space: pre-wrap;">{{ detailData.economicBenefits }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="申报时间" :span="3">{{ detailData.createTime }}</a-descriptions-item>
        <a-descriptions-item label="状态" :span="3">
          <a-tag :color="getStatusColor(detailData.status)">{{ getStatusText(detailData.status) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item v-if="detailData.status === '3'" label="已分配专家" :span="3">
          <div v-if="assignedExperts.length > 0">
            <a-card v-for="expert in assignedExperts" :key="expert.id" style="margin-bottom: 10px;">
              <a-row>
                <a-col :span="8">
                  <strong>姓名:</strong> {{ expert.nickname }}
                </a-col>
                <a-col :span="8">
                  <strong>邮箱:</strong> {{ expert.email || '未设置' }}
                </a-col>
              </a-row>
            </a-card>
          </div>
          <span v-else>暂无分配专家</span>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <!-- 分配专家弹窗 -->
    <a-modal
      v-model:visible="assignExpertsVisible"
      title="分配专家"
      width="600px"
      @ok="confirmAssignExperts"
    >
      <a-form :model="assignForm">
        <a-form-item label="选择专家" name="expertIds">
          <a-select
            v-model:value="assignForm.expertIds"
            mode="multiple"
            placeholder="请选择专家"
            style="width: 100%"
          >
            <a-select-option v-for="expert in expertList" :key="expert.id" :value="expert.id">
              {{ expert.nickname }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { getApplicationPage, approveApplication, rejectApplication, getExperts, assignExperts } from '~/api/projectApplication';
import { getAssignedExpertsDetail } from '~/api/expertAssignment';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const detailVisible = ref(false);
const detailData = ref({});
const assignedExperts = ref([]);

// 专家分配相关
const assignExpertsVisible = ref(false);
const expertList = ref([]);
const assignForm = ref({
  applicationId: null,
  expertIds: []
});

const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  projectName: "",
  organization: "",
  status: undefined
});

onMounted(() => {
  getList();
});

const loadExperts = async () => {
  try {
    const { data } = await getExperts();
    expertList.value = data;
  } catch (error) {
    console.log(error);
    message.error('获取专家列表失败');
  }
};

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
    projectName: "",
    organization: "",
    status: undefined
  };
  getList();
};

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await getApplicationPage(formModel.value);
    total.value = data.total;
    data.records.map((item, i) => { item.index = i + 1 });
    tableData.value = data.records;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const openDetail = async (record) => {
  detailData.value = record;
  detailVisible.value = true;
  
  // 如果是已分配状态，获取已分配的专家信息
  if (record.status === '3') {
    try {
      const { data } = await getAssignedExpertsDetail(record.id);
      assignedExperts.value = data || [];
    } catch (error) {
      console.log(error);
      message.error('获取分配专家信息失败');
      assignedExperts.value = [];
    }
  } else {
    assignedExperts.value = [];
  }
};

const handleApprove = async (record) => {
  try {
    await approveApplication(record.id);
    message.success('审核通过');
    getList();
  } catch (error) {
    console.log(error);
    message.error('操作失败');
  }
};

const handleReject = async (record) => {
  try {
    await rejectApplication(record.id);
    message.success('已拒绝');
    getList();
  } catch (error) {
    console.log(error);
    message.error('操作失败');
  }
};

const handleAssignExperts = async (record) => {
  assignForm.value.applicationId = record.id;
  assignForm.value.expertIds = [];
  loadExperts()
  try {
    // 获取已分配的专家列表
    const { data } = await getAssignedExpertsDetail(record.id);
    // 转换为专家ID数组
    assignForm.value.expertIds = data.map(expert => expert.id);
  } catch (error) {
    console.log(error);
  }
  
  assignExpertsVisible.value = true;
};

const confirmAssignExperts = async () => {
  if (!assignForm.value.expertIds || assignForm.value.expertIds.length === 0) {
    message.warning('请至少选择一位专家');
    return;
  }
  
  try {
    await assignExperts(assignForm.value.applicationId, assignForm.value.expertIds);
    message.success('分配专家成功');
    assignExpertsVisible.value = false;
    getList();
  } catch (error) {
    console.log(error);
    message.error('分配专家失败');
  }
};

const getStatusText = (status) => {
  switch (status) {
    case '0': return '待审核';
    case '1': return '已通过';
    case '2': return '已拒绝';
    case '3': return '已分配';
    case '4': return '已评审';
    default: return '未知状态';
  }
};

const getStatusColor = (status) => {
  switch (status) {
    case '0': return 'orange';
    case '1': return 'green';
    case '2': return 'red';
    case '3': return 'blue';
    case '4': return 'purple';
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
    dataIndex: 'projectName',
    key: 'projectName',
  },
  {
    title: '申报单位',
    dataIndex: 'organization',
    key: 'organization',
  },
  {
    title: '项目负责人',
    dataIndex: 'leader',
    key: 'leader',
  },
  {
    title: '项目周期',
    dataIndex: 'period',
    key: 'period',
  },
  {
    title: '预算总额',
    dataIndex: 'budget',
    key: 'budget',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '申报时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 260,
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