<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col :span="6">
            <a-form-item name="projectName" label="项目名称">
              <a-input v-model="formModel.projectName" placeholder="请输入项目名称" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item name="organization" label="申报单位">
              <a-input v-model="formModel.organization" placeholder="请输入申报单位" />
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
        <template v-if="column.key === 'totalScore'">
          <a-tag color="blue">{{ record.totalScore || '暂无' }}</a-tag>
        </template>
        <template v-else-if="column.key === 'assignedExperts'">
          <a-tag v-for="expert in record.experts" :key="expert.id" style="margin-right: 4px">
            {{ expert.name }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-button style="padding: 0;" type="link" @click="viewReviewDetails(record)">查看评审详情</a-button>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>

    <!-- 评审详情弹窗 -->
    <a-modal
      v-model="reviewDetailVisible"
      title="评审详情"
      width="1000px"
      :footer="null"
    >
      <a-descriptions bordered>
        <a-descriptions-item label="项目名称" :span="3">{{ currentApplication.projectName }}</a-descriptions-item>
        <a-descriptions-item label="申报单位" :span="3">{{ currentApplication.organization }}</a-descriptions-item>
        <a-descriptions-item label="项目负责人" :span="3">{{ currentApplication.leader }}</a-descriptions-item>
        <a-descriptions-item label="项目周期" :span="1">{{ currentApplication.period }}</a-descriptions-item>
        <a-descriptions-item label="预算总额" :span="1">{{ currentApplication.budget }} 元</a-descriptions-item>
        <a-descriptions-item label="评审专家数" :span="1">{{ reviewDetails.length }}人</a-descriptions-item>
        <a-descriptions-item label="平均得分" :span="3">
          <a-tag color="blue">{{ calculateAverageScore() }}分</a-tag>
        </a-descriptions-item>
      </a-descriptions>

      <a-divider>专家评审详情</a-divider>
      
      <a-table :columns="reviewDetailColumns" :data-source="reviewDetails" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'expertName'">
            {{ record.expertName }}
          </template>
          <template v-else-if="column.key === 'totalScore'">
            <a-tag color="blue">{{ record.totalScore }}分</a-tag>
          </template>
          <template v-else-if="column.key === 'technicalScores'">
            <div>技术可行性: {{ record.technicalFeasibilityScore }}/20</div>
            <div>创新性: {{ record.innovationScore }}/15</div>
            <div>成熟度: {{ record.maturityScore }}/15</div>
          </template>
          <template v-else-if="column.key === 'businessScores'">
            <div>预算合理性: {{ record.budgetReasonabilityScore }}/15</div>
            <div>成本效益: {{ record.costBenefitScore }}/15</div>
            <div>合同条款: {{ record.contractTermsScore }}/10</div>
          </template>
          <template v-else-if="column.key === 'riskScores'">
            <div>风险识别: {{ record.riskIdentificationScore }}/10</div>
            <div>合规性: {{ record.complianceScore }}/10</div>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-button style="padding: 0;" type="link" @click="viewExpertDetail(record)">查看详情</a-button>
          </template>
        </template>
      </a-table>
    </a-modal>

    <!-- 单个专家评审详情弹窗 -->
    <a-modal
      v-model="expertDetailVisible"
      title="专家评审详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions bordered>
        <a-descriptions-item label="专家姓名" :span="3">{{ currentExpertReview.expertName }}</a-descriptions-item>
        <a-descriptions-item label="评审时间" :span="3">{{ currentExpertReview.reviewTime }}</a-descriptions-item>
        <a-descriptions-item label="总分" :span="3">
          <a-tag color="blue">{{ currentExpertReview.totalScore }}分</a-tag>
        </a-descriptions-item>

        <a-descriptions-item label="技术可行性" :span="1">{{ currentExpertReview.technicalFeasibilityScore }}分/20分</a-descriptions-item>
        <a-descriptions-item label="创新性" :span="1">{{ currentExpertReview.innovationScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="成熟度" :span="1">{{ currentExpertReview.maturityScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="技术评审备注" :span="3">{{ currentExpertReview.technicalRemarks }}</a-descriptions-item>

        <a-descriptions-item label="预算合理性" :span="1">{{ currentExpertReview.budgetReasonabilityScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="成本效益" :span="1">{{ currentExpertReview.costBenefitScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="合同条款" :span="1">{{ currentExpertReview.contractTermsScore }}分/10分</a-descriptions-item>
        <a-descriptions-item label="商务评审备注" :span="3">{{ currentExpertReview.businessRemarks }}</a-descriptions-item>

        <a-descriptions-item label="风险识别" :span="1">{{ currentExpertReview.riskIdentificationScore }}分/10分</a-descriptions-item>
        <a-descriptions-item label="合规性" :span="1">{{ currentExpertReview.complianceScore }}分/10分</a-descriptions-item>
        <a-descriptions-item label="风险与合规评审备注" :span="3">{{ currentExpertReview.riskComplianceRemarks }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { getApplicationPage } from '~/api/projectApplication';
import { getReviewList } from '~/api/expertReview';
import { getAssignmentList } from '~/api/expertAssignment';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const currentApplication = ref({});
const reviewDetails = ref([]);
const reviewDetailVisible = ref(false);
const expertDetailVisible = ref(false);
const currentExpertReview = ref({});

const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  projectName: "",
  organization: "",
  status: "1" // 只查询已通过的项目
});

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
    projectName: "",
    organization: "",
    status: "1"
  };
  getList();
};

const getList = async () => {
  loading.value = true;
  try {
    const { data } = await getApplicationPage(formModel.value);
    total.value = data.total;
    
    // 处理每个项目，添加专家和评分信息
    const projects = [];
    for (const item of data.records) {
      const projectData = { ...item, experts: [], totalScore: 0 };
      
      try {
        // 获取项目分配的专家
        const assignResult = await getAssignmentList({
          applicationId: item.id,
          pageSize: 100
        });
        
        if (assignResult.data && assignResult.data.records) {
          projectData.experts = assignResult.data.records.map(assignment => {
            return {
              id: assignment.expertId,
              name: assignment.expertName || `专家${assignment.expertId}`
            };
          });
          
          // 获取项目的评审
          const reviewResult = await getReviewList({
            applicationId: item.id,
            status: "1",
            pageSize: 100
          });
          
          if (reviewResult.data && reviewResult.data.records && reviewResult.data.records.length > 0) {
            // 计算平均分
            let totalScore = 0;
            reviewResult.data.records.forEach(review => {
              totalScore += review.totalScore;
            });
            projectData.totalScore = (totalScore / reviewResult.data.records.length).toFixed(1);
          }
        }
      } catch (error) {
        console.log(error);
      }
      
      projects.push(projectData);
    }
    
    tableData.value = projects;
  } catch (error) {
    console.log(error);
  } finally {
    loading.value = false;
  }
};

const viewReviewDetails = async (record) => {
  currentApplication.value = record;
  reviewDetails.value = [];
  
  try {
    // 获取项目的所有评审
    const { data } = await getReviewList({
      applicationId: record.id,
      status: "1",
      pageSize: 100
    });
    
    if (data && data.records) {
      reviewDetails.value = data.records;
    }
    
    reviewDetailVisible.value = true;
  } catch (error) {
    console.log(error);
    message.error('获取评审详情失败');
  }
};

const viewExpertDetail = (record) => {
  currentExpertReview.value = record;
  expertDetailVisible.value = true;
};

const calculateAverageScore = () => {
  if (!reviewDetails.value.length) return '暂无';
  
  let total = 0;
  reviewDetails.value.forEach(item => {
    total += item.totalScore;
  });
  
  return (total / reviewDetails.value.length).toFixed(1);
};

const columns = [
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
    title: '评审专家',
    key: 'assignedExperts',
  },
  {
    title: '评审均分',
    dataIndex: 'totalScore',
    key: 'totalScore',
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 150,
  },
];

const reviewDetailColumns = [
  {
    title: '专家姓名',
    key: 'expertName',
  },
  {
    title: '总分',
    key: 'totalScore',
  },
  {
    title: '技术评审',
    key: 'technicalScores',
  },
  {
    title: '商务评审',
    key: 'businessScores',
  },
  {
    title: '风险评审',
    key: 'riskScores',
  },
  {
    title: '评审时间',
    dataIndex: 'reviewTime',
    key: 'reviewTime',
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 120,
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