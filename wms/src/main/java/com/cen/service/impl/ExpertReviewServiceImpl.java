package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.entity.ExpertAssignment;
import com.cen.entity.ExpertReview;
import com.cen.mapper.ExpertAssignmentMapper;
import com.cen.mapper.ExpertReviewMapper;
import com.cen.service.IExpertReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专家评审服务实现类
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Service
public class ExpertReviewServiceImpl extends ServiceImpl<ExpertReviewMapper, ExpertReview> implements IExpertReviewService {

    @Resource
    private ExpertReviewMapper reviewMapper;
    
    @Resource
    private ExpertAssignmentMapper assignmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReview(ExpertReview review) {
        if (review == null || review.getAssignmentId() == null) {
            return false;
        }
        
        // 查询分配记录
        ExpertAssignment assignment = assignmentMapper.selectById(review.getAssignmentId());
        if (assignment == null) {
            return false;
        }
        
        // 设置评审属性
        review.setApplicationId(assignment.getApplicationId());
        review.setExpertId(assignment.getExpertId());
        review.setStatus("1"); // 已提交
        review.setReviewTime(LocalDateTime.now());
        
        // 计算总分
        int totalScore = calculateTotalScore(review);
        review.setTotalScore(totalScore);
        
        // 保存评审
        QueryWrapper<ExpertReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignment_id", review.getAssignmentId());
        ExpertReview existReview = reviewMapper.selectOne(queryWrapper);
        
        boolean result;
        if (existReview != null) {
            review.setId(existReview.getId());
            result = reviewMapper.updateById(review) > 0;
        } else {
            result = reviewMapper.insert(review) > 0;
        }
        
        if (result) {
            // 更新分配状态
            assignment.setStatus("1"); // 已评审
            assignment.setReviewTime(LocalDateTime.now());
            assignmentMapper.updateById(assignment);
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveReviewDraft(ExpertReview review) {
        if (review == null || review.getAssignmentId() == null) {
            return false;
        }
        
        // 查询分配记录
        ExpertAssignment assignment = assignmentMapper.selectById(review.getAssignmentId());
        if (assignment == null) {
            return false;
        }
        
        // 设置评审属性
        review.setApplicationId(assignment.getApplicationId());
        review.setExpertId(assignment.getExpertId());
        review.setStatus("0"); // 草稿
        
        // 计算总分
        int totalScore = calculateTotalScore(review);
        review.setTotalScore(totalScore);
        
        // 保存评审
        QueryWrapper<ExpertReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignment_id", review.getAssignmentId());
        ExpertReview existReview = reviewMapper.selectOne(queryWrapper);
        
        if (existReview != null) {
            review.setId(existReview.getId());
            return reviewMapper.updateById(review) > 0;
        } else {
            return reviewMapper.insert(review) > 0;
        }
    }

    @Override
    public Page<ExpertReview> pageReview(Integer pageNum, Integer pageSize, Long applicationId, Long expertId, String status) {
        QueryWrapper<ExpertReview> queryWrapper = new QueryWrapper<>();
        if (applicationId != null) {
            queryWrapper.eq("application_id", applicationId);
        }
        if (expertId != null) {
            queryWrapper.eq("expert_id", expertId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("review_time");
        
        return reviewMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public ExpertReview getReviewDetail(Long id) {
        return reviewMapper.selectById(id);
    }

    @Override
    public ExpertReview getReviewByAssignmentId(Long assignmentId) {
        if (assignmentId == null) {
            return null;
        }
        
        QueryWrapper<ExpertReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assignment_id", assignmentId);
        
        return reviewMapper.selectOne(queryWrapper);
    }
    
    /**
     * 计算评审总分
     * @param review 评审对象
     * @return 总分
     */
    private int calculateTotalScore(ExpertReview review) {
        int totalScore = 0;
        
        // 技术评审
        totalScore += review.getTechnicalFeasibilityScore() != null ? review.getTechnicalFeasibilityScore() : 0; // 最高20分
        totalScore += review.getInnovationScore() != null ? review.getInnovationScore() : 0; // 最高15分
        totalScore += review.getMaturityScore() != null ? review.getMaturityScore() : 0; // 最高15分
        
        // 商务评审
        totalScore += review.getBudgetReasonabilityScore() != null ? review.getBudgetReasonabilityScore() : 0; // 最高15分
        totalScore += review.getCostBenefitScore() != null ? review.getCostBenefitScore() : 0; // 最高15分
        totalScore += review.getContractTermsScore() != null ? review.getContractTermsScore() : 0; // 最高10分
        
        // 风险与合规评审
        totalScore += review.getRiskIdentificationScore() != null ? review.getRiskIdentificationScore() : 0; // 最高10分
        totalScore += review.getComplianceScore() != null ? review.getComplianceScore() : 0; // 最高10分
        
        return totalScore; // 总分100分
    }
    
    @Override
    public Object getReviewResultByApplicationId(Long applicationId) {
        if (applicationId == null) {
            return null;
        }
        
        // 查询该项目所有已提交的评审
        QueryWrapper<ExpertReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.eq("status", "1"); // 已提交的评审
        List<ExpertReview> reviews = reviewMapper.selectList(queryWrapper);
        
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }
        
        // 计算总评分和各项评分的平均值
        int totalScore = 0;
        int technicalFeasibilityScoreSum = 0;
        int innovationScoreSum = 0;
        int maturityScoreSum = 0;
        int budgetReasonabilityScoreSum = 0;
        int costBenefitScoreSum = 0;
        int contractTermsScoreSum = 0;
        int riskIdentificationScoreSum = 0;
        int complianceScoreSum = 0;
        
        // 收集评审意见
        StringBuilder technicalCommentBuilder = new StringBuilder();
        StringBuilder businessCommentBuilder = new StringBuilder();
        StringBuilder riskCommentBuilder = new StringBuilder();
        
        for (ExpertReview review : reviews) {
            // 累加各项分数
            totalScore += review.getTotalScore() != null ? review.getTotalScore() : 0;
            
            // 技术评审
            technicalFeasibilityScoreSum += review.getTechnicalFeasibilityScore() != null ? review.getTechnicalFeasibilityScore() : 0;
            innovationScoreSum += review.getInnovationScore() != null ? review.getInnovationScore() : 0;
            maturityScoreSum += review.getMaturityScore() != null ? review.getMaturityScore() : 0;
            
            // 商务评审
            budgetReasonabilityScoreSum += review.getBudgetReasonabilityScore() != null ? review.getBudgetReasonabilityScore() : 0;
            costBenefitScoreSum += review.getCostBenefitScore() != null ? review.getCostBenefitScore() : 0;
            contractTermsScoreSum += review.getContractTermsScore() != null ? review.getContractTermsScore() : 0;
            
            // 风险与合规评审
            riskIdentificationScoreSum += review.getRiskIdentificationScore() != null ? review.getRiskIdentificationScore() : 0;
            complianceScoreSum += review.getComplianceScore() != null ? review.getComplianceScore() : 0;
            
            // 收集评审备注
            if (review.getTechnicalRemarks() != null && !review.getTechnicalRemarks().isEmpty()) {
                if (technicalCommentBuilder.length() > 0) {
                    technicalCommentBuilder.append("\n");
                }
                technicalCommentBuilder.append(review.getTechnicalRemarks());
            }
            
            if (review.getBusinessRemarks() != null && !review.getBusinessRemarks().isEmpty()) {
                if (businessCommentBuilder.length() > 0) {
                    businessCommentBuilder.append("\n");
                }
                businessCommentBuilder.append(review.getBusinessRemarks());
            }
            
            if (review.getRiskComplianceRemarks() != null && !review.getRiskComplianceRemarks().isEmpty()) {
                if (riskCommentBuilder.length() > 0) {
                    riskCommentBuilder.append("\n");
                }
                riskCommentBuilder.append(review.getRiskComplianceRemarks());
            }
        }
        
        // 计算平均分
        int reviewCount = reviews.size();
        double averageScore = reviewCount > 0 ? (double) totalScore / reviewCount : 0;
        
        // 计算各项评分的平均值
        int technicalFeasibilityScore = reviewCount > 0 ? Math.round((float) technicalFeasibilityScoreSum / reviewCount) : 0;
        int innovationScore = reviewCount > 0 ? Math.round((float) innovationScoreSum / reviewCount) : 0;
        int maturityScore = reviewCount > 0 ? Math.round((float) maturityScoreSum / reviewCount) : 0;
        int budgetReasonabilityScore = reviewCount > 0 ? Math.round((float) budgetReasonabilityScoreSum / reviewCount) : 0;
        int costBenefitScore = reviewCount > 0 ? Math.round((float) costBenefitScoreSum / reviewCount) : 0;
        int contractTermsScore = reviewCount > 0 ? Math.round((float) contractTermsScoreSum / reviewCount) : 0;
        int riskIdentificationScore = reviewCount > 0 ? Math.round((float) riskIdentificationScoreSum / reviewCount) : 0;
        int complianceScore = reviewCount > 0 ? Math.round((float) complianceScoreSum / reviewCount) : 0;
        
        // 评审备注
        String technicalComment = technicalCommentBuilder.toString();
        String businessComment = businessCommentBuilder.toString();
        String riskComment = riskCommentBuilder.toString();
        
        // 构建结果对象
        Map<String, Object> result = new HashMap<>();
        result.put("score", Math.round(averageScore * 10) / 10.0); // 保留一位小数
        result.put("reviewCount", reviewCount);
        
        // 添加各项评分
        result.put("technicalFeasibilityScore", technicalFeasibilityScore);
        result.put("innovationScore", innovationScore);
        result.put("maturityScore", maturityScore);
        result.put("budgetReasonabilityScore", budgetReasonabilityScore);
        result.put("costBenefitScore", costBenefitScore);
        result.put("contractTermsScore", contractTermsScore);
        result.put("riskIdentificationScore", riskIdentificationScore);
        result.put("complianceScore", complianceScore);
        
        // 添加评审备注
        result.put("technicalComment", technicalComment);
        result.put("businessComment", businessComment);
        result.put("riskComment", riskComment);
        
        // 项目评审结论
        // 根据总分判断评审结论
        String conclusion;
        if (averageScore >= 80) {
            conclusion = "项目评审通过，建议立即实施";
        } else if (averageScore >= 60) {
            conclusion = "项目评审有条件通过，需完善后实施";
        } else {
            conclusion = "项目评审不通过，不建议实施";
        }
        result.put("conclusion", conclusion);
        
        return result;
    }
} 