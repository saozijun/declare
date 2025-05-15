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
        
        // 商务评审
        totalScore += review.getEnterpriseQualificationScore() != null ? review.getEnterpriseQualificationScore() : 0; // 最高10分
        totalScore += review.getFinancialStatusScore() != null ? review.getFinancialStatusScore() : 0; // 最高5分
        totalScore += review.getPerformanceCaseScore() != null ? review.getPerformanceCaseScore() : 0; // 最高10分
        totalScore += review.getPerformanceCapabilityScore() != null ? review.getPerformanceCapabilityScore() : 0; // 最高5分
        
        // 技术评审
        totalScore += review.getTechnicalResponseScore() != null ? review.getTechnicalResponseScore() : 0; // 最高10分
        totalScore += review.getImplementationPlanScore() != null ? review.getImplementationPlanScore() : 0; // 最高15分
        totalScore += review.getQualityAssuranceScore() != null ? review.getQualityAssuranceScore() : 0; // 最高10分
        totalScore += review.getAfterSaleServiceScore() != null ? review.getAfterSaleServiceScore() : 0; // 最高5分
        
        // 价格评分
        totalScore += review.getPriceScore() != null ? review.getPriceScore() : 0; // 最高30分
        
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
        // 商务评审
        int enterpriseQualificationScoreSum = 0;
        int financialStatusScoreSum = 0;
        int performanceCaseScoreSum = 0;
        int performanceCapabilityScoreSum = 0;
        // 技术评审
        int technicalResponseScoreSum = 0;
        int implementationPlanScoreSum = 0;
        int qualityAssuranceScoreSum = 0;
        int afterSaleServiceScoreSum = 0;
        // 价格评分
        int priceScoreSum = 0;
        
        // 收集评审意见
        StringBuilder technicalCommentBuilder = new StringBuilder();
        StringBuilder businessCommentBuilder = new StringBuilder();
        StringBuilder priceCommentBuilder = new StringBuilder();
        
        for (ExpertReview review : reviews) {
            // 累加各项分数
            totalScore += review.getTotalScore() != null ? review.getTotalScore() : 0;
            
            // 商务评审
            enterpriseQualificationScoreSum += review.getEnterpriseQualificationScore() != null ? review.getEnterpriseQualificationScore() : 0;
            financialStatusScoreSum += review.getFinancialStatusScore() != null ? review.getFinancialStatusScore() : 0;
            performanceCaseScoreSum += review.getPerformanceCaseScore() != null ? review.getPerformanceCaseScore() : 0;
            performanceCapabilityScoreSum += review.getPerformanceCapabilityScore() != null ? review.getPerformanceCapabilityScore() : 0;
            
            // 技术评审
            technicalResponseScoreSum += review.getTechnicalResponseScore() != null ? review.getTechnicalResponseScore() : 0;
            implementationPlanScoreSum += review.getImplementationPlanScore() != null ? review.getImplementationPlanScore() : 0;
            qualityAssuranceScoreSum += review.getQualityAssuranceScore() != null ? review.getQualityAssuranceScore() : 0;
            afterSaleServiceScoreSum += review.getAfterSaleServiceScore() != null ? review.getAfterSaleServiceScore() : 0;
            
            // 价格评分
            priceScoreSum += review.getPriceScore() != null ? review.getPriceScore() : 0;
            
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
            
            if (review.getPriceRemarks() != null && !review.getPriceRemarks().isEmpty()) {
                if (priceCommentBuilder.length() > 0) {
                    priceCommentBuilder.append("\n");
                }
                priceCommentBuilder.append(review.getPriceRemarks());
            }
        }
        
        // 计算平均分
        int reviewCount = reviews.size();
        double averageScore = reviewCount > 0 ? (double) totalScore / reviewCount : 0;
        
        // 计算各项评分的平均值
        // 商务评审
        int enterpriseQualificationScore = reviewCount > 0 ? Math.round((float) enterpriseQualificationScoreSum / reviewCount) : 0;
        int financialStatusScore = reviewCount > 0 ? Math.round((float) financialStatusScoreSum / reviewCount) : 0;
        int performanceCaseScore = reviewCount > 0 ? Math.round((float) performanceCaseScoreSum / reviewCount) : 0;
        int performanceCapabilityScore = reviewCount > 0 ? Math.round((float) performanceCapabilityScoreSum / reviewCount) : 0;
        // 技术评审
        int technicalResponseScore = reviewCount > 0 ? Math.round((float) technicalResponseScoreSum / reviewCount) : 0;
        int implementationPlanScore = reviewCount > 0 ? Math.round((float) implementationPlanScoreSum / reviewCount) : 0;
        int qualityAssuranceScore = reviewCount > 0 ? Math.round((float) qualityAssuranceScoreSum / reviewCount) : 0;
        int afterSaleServiceScore = reviewCount > 0 ? Math.round((float) afterSaleServiceScoreSum / reviewCount) : 0;
        // 价格评分
        int priceScore = reviewCount > 0 ? Math.round((float) priceScoreSum / reviewCount) : 0;
        
        // 评审备注
        String technicalComment = technicalCommentBuilder.toString();
        String businessComment = businessCommentBuilder.toString();
        String priceComment = priceCommentBuilder.toString();
        
        // 构建结果对象
        Map<String, Object> result = new HashMap<>();
        result.put("score", Math.round(averageScore * 10) / 10.0); // 保留一位小数
        result.put("reviewCount", reviewCount);
        
        // 添加各项评分
        // 商务评审
        result.put("enterpriseQualificationScore", enterpriseQualificationScore);
        result.put("financialStatusScore", financialStatusScore);
        result.put("performanceCaseScore", performanceCaseScore);
        result.put("performanceCapabilityScore", performanceCapabilityScore);
        // 技术评审
        result.put("technicalResponseScore", technicalResponseScore);
        result.put("implementationPlanScore", implementationPlanScore);
        result.put("qualityAssuranceScore", qualityAssuranceScore);
        result.put("afterSaleServiceScore", afterSaleServiceScore);
        // 价格评分
        result.put("priceScore", priceScore);
        
        // 添加评审备注
        result.put("technicalComment", technicalComment);
        result.put("businessComment", businessComment);
        result.put("priceComment", priceComment);
        
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