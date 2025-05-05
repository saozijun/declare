<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col>
            <a-form-item name="status" label="状态">
              <a-select v-model:value="formModel.status" style="width: 200px" placeholder="请选择状态" allowClear>
                <a-select-option value="0">待评审</a-select-option>
                <a-select-option value="1">已评审</a-select-option>
              </a-select>
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
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>
    
    <!-- 显示加载状态 -->
    <div v-if="loading" style="text-align: center; margin: 20px 0;">
      <a-spin />
    </div>
    
    <!-- 显示数据为空的提示 -->
    <a-empty v-else-if="!tableData.length" description="暂无数据" />
    
    <a-table v-else :columns="columns" :data-source="tableData" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'assignTime'">
          {{ record.assignTime ? new Date(record.assignTime).toLocaleString() : '-' }}
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-button style="padding: 0;" type="link" @click="openDetail(record)">查看详情</a-button>
          <a-button 
            type="link" 
            @click="handleReview(record)" 
            v-if="record.status === '0'"
          >
            评审
          </a-button>
          <a-button 
            type="link" 
            @click="viewReview(record)" 
            v-if="record.status === '1'"
          >
            查看评审
          </a-button>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>

    <!-- 项目详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="项目详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions bordered v-if="detailData">
        <a-descriptions-item label="项目名称" :span="3">{{ detailData.projectName }}</a-descriptions-item>
        <a-descriptions-item label="申报单位" :span="3">{{ detailData.organization }}</a-descriptions-item>
        <a-descriptions-item label="项目负责人" :span="3">{{ detailData.leader }}</a-descriptions-item>
        <a-descriptions-item label="项目周期" :span="3">{{ detailData.period }}</a-descriptions-item>
        <a-descriptions-item label="预算总额" :span="3">{{ detailData.budget }} 元</a-descriptions-item>
        <a-descriptions-item label="申报用户" :span="3">
          <div class="user-info-container">
            <div class="user-details">
              <div v-if="detailData.userDetail">
                <a-avatar v-if="detailData.userDetail.avatarUrl" :src="detailData.userDetail.avatarUrl" />
                <a-avatar v-else>{{ detailData.userDetail.nickname ? detailData.userDetail.nickname.substring(0, 1) : 'U' }}</a-avatar>
                <span class="user-name">{{ detailData.userDetail.nickname || '未知用户' }}</span>
                <span class="user-email" v-if="detailData.userDetail.email">{{ detailData.userDetail.email }}</span>
              </div>
              <div v-else>
                <a-avatar>U</a-avatar>
                <span class="user-name">{{ detailData.userName || '未知用户' }}</span>
              </div>
            </div>
            <a-button type="primary" size="small" @click="openChatDialog(detailData)">
              <template #icon><message-outlined /></template>
              与申报人沟通
            </a-button>
          </div>
        </a-descriptions-item>
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
      </a-descriptions>
    </a-modal>

    <!-- 评审表单弹窗 -->
    <a-modal
      v-model:open="reviewFormVisible"
      title="专家评审"
      width="800px"
    >
      <div class="review-form">
        <div class="review-section">
          <div class="section-title">技术评审</div>
          <div class="form-item">
            <div class="label">技术可行性 (20分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.technicalFeasibilityScore" 
                :min="0" 
                :max="20" 
                :step="1" 
                :marks="{ 0: '0', 10: '10', 20: '20' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">创新性 (15分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.innovationScore" 
                :min="0" 
                :max="15" 
                :step="1" 
                :marks="{ 0: '0', 8: '8', 15: '15' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">成熟度 (15分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.maturityScore" 
                :min="0" 
                :max="15" 
                :step="1" 
                :marks="{ 0: '0', 8: '8', 15: '15' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">备注</div>
            <div class="content">
              <a-textarea 
                v-model:value="reviewForm.technicalRemarks" 
                placeholder="请输入技术评审备注" 
                :auto-size="{ minRows: 2, maxRows: 6 }" 
              />
            </div>
          </div>
        </div>

        <div class="review-section">
          <div class="section-title">商务评审</div>
          <div class="form-item">
            <div class="label">预算合理性 (15分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.budgetReasonabilityScore" 
                :min="0" 
                :max="15" 
                :step="1" 
                :marks="{ 0: '0', 8: '8', 15: '15' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">成本效益 (15分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.costBenefitScore" 
                :min="0" 
                :max="15" 
                :step="1" 
                :marks="{ 0: '0', 8: '8', 15: '15' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">合同条款 (10分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.contractTermsScore" 
                :min="0" 
                :max="10" 
                :step="1" 
                :marks="{ 0: '0', 5: '5', 10: '10' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">备注</div>
            <div class="content">
              <a-textarea 
                v-model:value="reviewForm.businessRemarks" 
                placeholder="请输入商务评审备注" 
                :auto-size="{ minRows: 2, maxRows: 6 }" 
              />
            </div>
          </div>
        </div>

        <div class="review-section">
          <div class="section-title">风险与合规评审</div>
          <div class="form-item">
            <div class="label">风险识别 (10分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.riskIdentificationScore" 
                :min="0" 
                :max="10" 
                :step="1" 
                :marks="{ 0: '0', 5: '5', 10: '10' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">合规性 (10分)</div>
            <div class="content">
              <a-slider 
                v-model:value="reviewForm.complianceScore" 
                :min="0" 
                :max="10" 
                :step="1" 
                :marks="{ 0: '0', 5: '5', 10: '10' }" 
              />
            </div>
          </div>
          <div class="form-item">
            <div class="label">备注</div>
            <div class="content">
              <a-textarea 
                v-model:value="reviewForm.riskComplianceRemarks" 
                placeholder="请输入风险与合规评审备注" 
                :auto-size="{ minRows: 2, maxRows: 6 }" 
              />
            </div>
          </div>
        </div>

        <div class="total-score-section">
          <div class="form-item">
            <div class="label">总分</div>
            <div class="content">
              <a-tag color="blue" class="total-score">{{ calculateTotalScore() }}分</a-tag>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <a-button key="back" @click="saveAsDraft">保存草稿</a-button>
        <a-button key="submit" type="primary" @click="submitReviewForm">提交评审</a-button>
      </template>
    </a-modal>

    <!-- 查看评审结果弹窗 -->
    <a-modal
      v-model:open="reviewDetailVisible"
      title="评审结果"
      width="800px"
      :footer="null"
    >
      <a-descriptions bordered v-if="detailData && reviewDetail">
        <a-descriptions-item label="项目名称" :span="3">{{ detailData.projectName }}</a-descriptions-item>
        <a-descriptions-item label="申报单位" :span="3">{{ detailData.organization }}</a-descriptions-item>

        <a-descriptions-item label="总分" :span="3">
          <a-tag color="blue">{{ reviewDetail.totalScore }}分</a-tag>
        </a-descriptions-item>

        <a-descriptions-item label="技术可行性" :span="1">{{ reviewDetail.technicalFeasibilityScore }}分/20分</a-descriptions-item>
        <a-descriptions-item label="创新性" :span="1">{{ reviewDetail.innovationScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="成熟度" :span="1">{{ reviewDetail.maturityScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="技术评审备注" :span="3">{{ reviewDetail.technicalRemarks }}</a-descriptions-item>

        <a-descriptions-item label="预算合理性" :span="1">{{ reviewDetail.budgetReasonabilityScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="成本效益" :span="1">{{ reviewDetail.costBenefitScore }}分/15分</a-descriptions-item>
        <a-descriptions-item label="合同条款" :span="1">{{ reviewDetail.contractTermsScore }}分/10分</a-descriptions-item>
        <a-descriptions-item label="商务评审备注" :span="3">{{ reviewDetail.businessRemarks }}</a-descriptions-item>

        <a-descriptions-item label="风险识别" :span="1">{{ reviewDetail.riskIdentificationScore }}分/10分</a-descriptions-item>
        <a-descriptions-item label="合规性" :span="1">{{ reviewDetail.complianceScore }}分/10分</a-descriptions-item>
        <a-descriptions-item label="风险与合规评审备注" :span="3">{{ reviewDetail.riskComplianceRemarks }}</a-descriptions-item>

        <a-descriptions-item label="评审时间" :span="3">{{ reviewDetail.reviewTime }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <!-- 聊天弹窗 -->
    <a-modal
      v-model:open="chatDialogVisible"
      :title="'与 ' + (currentChatUser ? currentChatUser : '申报人') + ' 沟通'"
      width="700px"
      :footer="null"
      @cancel="closeChatDialog"
    >
      <chat-box 
        v-if="chatUserId"
        :userId="chatUserId" 
        :userInfo="userStore.userInfo"
        ref="chatBoxRef"
      />
      <div v-else class="empty-chat-state">
        <div class="empty-icon">💬</div>
        <p>无法启动聊天，用户信息不完整</p>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { MessageOutlined } from '@ant-design/icons-vue';
import { getAssignmentList } from '~/api/expertAssignment';
import { submitReview, saveReviewDraft, getReviewByAssignment } from '~/api/expertReview';
import { getInfo } from '~/api/user';
import { useUserStore } from "~@/stores/user";
import ChatBox from '~/components/ChatBox.vue';

const userStore = useUserStore();
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const detailVisible = ref(false);
const detailData = ref(null);

const reviewFormVisible = ref(false);
const reviewDetailVisible = ref(false);
const currentAssignment = ref(null);
const reviewDetail = ref({});

const chatDialogVisible = ref(false);
const chatUserId = ref(null);
const chatBoxRef = ref(null);
const currentChatUser = ref(null);

const formModel = ref({
  pageNum: 1,
  pageSize: 10,
  projectName: "",
  organization: "",
  status: undefined,
  expertId: null // 当前登录的专家ID
});

const reviewForm = ref({
  assignmentId: null,
  applicationId: null,
  expertId: null,
  // 技术评审
  technicalFeasibilityScore: 0,
  innovationScore: 0,
  maturityScore: 0,
  technicalRemarks: '',
  // 商务评审
  budgetReasonabilityScore: 0,
  costBenefitScore: 0,
  contractTermsScore: 0,
  businessRemarks: '',
  // 风险与合规评审
  riskIdentificationScore: 0,
  complianceScore: 0,
  riskComplianceRemarks: '',
  // 状态
  status: '0'
});

onMounted(() => {
  console.log("页面加载，用户信息:", userStore.userInfo);
  if (userStore.userInfo && userStore.userInfo.id) {
    formModel.value.expertId = userStore.userInfo.id;
    getList();
  } else {
    message.error('未获取到用户信息，请重新登录');
  }
});

// 监听用户信息变化
watch(() => userStore.userInfo, (newVal) => {
  if (newVal && newVal.id) {
    formModel.value.expertId = newVal.id;
    getList();
  }
}, { deep: true });

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
    status: undefined,
    expertId: userStore.userInfo ? userStore.userInfo.id : null
  };
  getList();
};

const getList = async () => {
  if (!formModel.value.expertId) {
    message.error('未获取到专家ID，无法加载数据');
    return;
  }
  
  loading.value = true;
  try {
    console.log("查询参数:", formModel.value);
    const { data } = await getAssignmentList(formModel.value);
    console.log("获取数据结果:", data);
    
    if (data && data.records) {
      total.value = data.total || 0;
      tableData.value = data.records || [];
      tableData.value.forEach((item, index) => {
        item.index = index + 1;
      });
      // 调试输出
      if (tableData.value.length === 0) {
        console.log("没有获取到数据");
      } else {
        console.log(`获取到${tableData.value.length}条数据`);
      }
    } else {
      total.value = 0;
      tableData.value = [];
      console.log("返回的数据格式不正确:", data);
    }
  } catch (error) {
    console.error("获取数据失败:", error);
    message.error('获取数据失败');
    total.value = 0;
    tableData.value = [];
  } finally {
    loading.value = false;
  }
};

const openDetail = (record) => {
  console.log("查看详情:", record);
  if (record && record.application) {
    detailData.value = record.application;
    // 获取申报用户的详细信息
    if (detailData.value.userId) {
      fetchUserInfo(detailData.value.userId);
    }
    
    detailVisible.value = true;
  } else {
    message.error('项目详情数据不完整');
  }
};

const fetchUserInfo = async (userId) => {
  try {
    const res = await getInfo(userId);
    if (res.code == 200 && res.data) {
      detailData.value.userDetail = res.data;
      console.log("获取到用户详情:", res.data);
    }
  } catch (error) {
    console.error("获取用户信息请求失败:", error);
  }
};

const handleReview = async (record) => {
  console.log("评审项目:", record);
  if (!record || !record.id) {
    message.error('评审数据不完整');
    return;
  }
  
  currentAssignment.value = record;
  if (record.application) {
    detailData.value = record.application;
  }
  
  resetReviewForm();

  reviewForm.value.assignmentId = record.id;
  reviewForm.value.applicationId = record.applicationId;
  reviewForm.value.expertId = formModel.value.expertId;

  // 检查是否有草稿
  try {
    const { data } = await getReviewByAssignment(record.id);
    console.log("获取评审草稿:", data);
    if (data) reviewForm.value = data;
  } catch (error) {
    console.error("获取评审草稿失败:", error);
  }

  reviewFormVisible.value = true;
};

const viewReview = async (record) => {
  console.log("查看评审结果:", record);
  if (!record || !record.id) {
    message.error('评审数据不完整');
    return;
  }
  
  currentAssignment.value = record;
  if (record.application) {
    detailData.value = record.application;
  }

  try {
    const { data } = await getReviewByAssignment(record.id);
    console.log("获取评审结果:", data);
    if (data) {
      reviewDetail.value = data;
      reviewDetailVisible.value = true;
    } else {
      message.error('找不到评审记录');
    }
  } catch (error) {
    console.error("获取评审记录失败:", error);
    message.error('获取评审记录失败');
  }
};

const resetReviewForm = () => {
  reviewForm.value = {
    assignmentId: null,
    applicationId: null,
    expertId: formModel.value.expertId,
    technicalFeasibilityScore: 0,
    innovationScore: 0,
    maturityScore: 0,
    technicalRemarks: '',
    budgetReasonabilityScore: 0,
    costBenefitScore: 0,
    contractTermsScore: 0,
    businessRemarks: '',
    riskIdentificationScore: 0,
    complianceScore: 0,
    riskComplianceRemarks: '',
    status: '0'
  };
};

const calculateTotalScore = () => {
  return (
    (reviewForm.value.technicalFeasibilityScore || 0) +
    (reviewForm.value.innovationScore || 0) +
    (reviewForm.value.maturityScore || 0) +
    (reviewForm.value.budgetReasonabilityScore || 0) +
    (reviewForm.value.costBenefitScore || 0) +
    (reviewForm.value.contractTermsScore || 0) +
    (reviewForm.value.riskIdentificationScore || 0) +
    (reviewForm.value.complianceScore || 0)
  );
};

const saveAsDraft = async () => {
  if (!reviewForm.value.assignmentId) {
    message.error('缺少关键数据，无法保存');
    return;
  }
  
  try {
    reviewForm.value.status = '0'; // 草稿状态
    reviewForm.value.totalScore = calculateTotalScore();
    console.log("保存评审草稿:", reviewForm.value);
    await saveReviewDraft(reviewForm.value);
    message.success('保存草稿成功');
  } catch (error) {
    console.error("保存草稿失败:", error);
    message.error('保存草稿失败');
  }
};

const submitReviewForm = async () => {
  if (!reviewForm.value.assignmentId) {
    message.error('缺少关键数据，无法提交');
    return;
  }
  
  const totalScore = calculateTotalScore();
  
  // 添加确认对话框
  Modal.confirm({
    title: '确认提交评审',
    content: `您的评审得分为${totalScore}分，确认提交该评审结果吗？提交后将无法修改！`,
    okText: '确认提交',
    cancelText: '取消',
    onOk: async () => {
      try {
        reviewForm.value.status = '1'; // 已提交状态
        reviewForm.value.totalScore = totalScore;
        console.log("提交评审:", reviewForm.value);
        await submitReview(reviewForm.value);
        message.success('提交评审成功');
        reviewFormVisible.value = false;
        getList(); // 刷新列表
      } catch (error) {
        console.error("提交评审失败:", error);
        message.error('提交评审失败');
      }
    }
  });
};

const getStatusText = (status) => {
  switch (status) {
    case '0': return '待评审';
    case '1': return '已评审';
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

const openChatDialog = (userInfo) => {
  let userId = null;
  
  // 优先使用userDetail中的id
  if (userInfo.userDetail && userInfo.userDetail.id) {
    userId = userInfo.userDetail.id;
    currentChatUser.value = userInfo.userDetail.nickname || '用户';
  } else if (userInfo.userId) {
    userId = userInfo.userId;
    currentChatUser.value = userInfo.userName || '用户';
  }
  
  if (userId) {
    chatUserId.value = userId;
    chatDialogVisible.value = true;
    
    // Using nextTick to make sure the component is mounted before operations
    nextTick(() => {
      if (chatBoxRef.value) {
        chatBoxRef.value.fetchChatMessages(true);
        chatBoxRef.value.scrollToBottom();
      }
    });
  } else {
    message.error('无法获取申报用户ID，无法启动聊天');
  }
};

const closeChatDialog = () => {
  chatUserId.value = null;
  currentChatUser.value = null;
  chatDialogVisible.value = false;
};

const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 80,
  },
  {
    title: '项目名称',
    dataIndex: ['application', 'projectName'],
    key: 'projectName',
  },
  {
    title: '申报单位',
    dataIndex: ['application', 'organization'],
    key: 'organization',
  },
  {
    title: '项目负责人',
    dataIndex: ['application', 'leader'],
    key: 'leader',
  },
  {
    title: '项目周期',
    dataIndex: ['application', 'period'],
    key: 'period',
  },
  {
    title: '预算总额',
    dataIndex: ['application', 'budget'],
    key: 'budget',
  },
  {
    title: '分配时间',
    dataIndex: 'assignTime',
    key: 'assignTime',
    
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
    width: 200,
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

.review-form {
  max-height: 70vh;
  overflow-y: auto;
  padding: 0 10px;
  margin-bottom: 60px;

}

.review-section {
  background-color: #fafafa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e8e8e8;
  color: #1890ff;
}

.form-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.form-item .label {
  width: 130px;
  text-align: right;
  padding-right: 15px;
  line-height: 32px;
  font-weight: 500;
  color: #333;
}

.form-item .content {
  width: 75%;
}

.total-score-section {
  padding: 15px;
  position: absolute;
  bottom: 5px;
  right: 50px;
  width: 100%;
}
.total-score-section .form-item {
  margin-bottom: 0;
}

.total-score {
  font-size: 16px;
  font-weight: bold;
  padding: 5px 15px;
}

.user-info-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  span {
    margin-right: 10px;
  }
}

.user-details {
  display: flex;
  align-items: center;
  
  :deep(.ant-avatar) {
    margin-right: 12px;
  }
  
  .user-name {
    font-weight: 500;
    margin: 0 8px;
    color: #333;
  }
  
  .user-email {
    color: #666;
    font-size: 13px;
  }
}

.empty-chat-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  height: 300px;

  .empty-icon {
    font-size: 48px;
    color: #ccc;
    margin-bottom: 15px;
  }

  p {
    color: #666;
    margin: 0;
    font-size: 14px;
  }
}

/* Chat dialog sizing */
:deep(.ant-modal-body) {
  max-height: 500px;
  overflow-y: auto;
  padding: 0;
}
</style> 