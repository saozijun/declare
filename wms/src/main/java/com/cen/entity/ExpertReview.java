package com.cen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 专家评审表
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Data
@TableName("expert_review")
public class ExpertReview {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 专家分配ID
     */
    private Long assignmentId;

    /**
     * 项目申报ID
     */
    private Long applicationId;

    /**
     * 专家用户ID
     */
    private Long expertId;

    /**
     * 技术可行性得分
     */
    private Integer technicalFeasibilityScore;

    /**
     * 创新性得分
     */
    private Integer innovationScore;

    /**
     * 成熟度得分
     */
    private Integer maturityScore;

    /**
     * 技术评审备注
     */
    private String technicalRemarks;

    /**
     * 预算合理性得分
     */
    private Integer budgetReasonabilityScore;

    /**
     * 成本效益得分
     */
    private Integer costBenefitScore;

    /**
     * 合同条款得分
     */
    private Integer contractTermsScore;

    /**
     * 商务评审备注
     */
    private String businessRemarks;

    /**
     * 风险识别得分
     */
    private Integer riskIdentificationScore;

    /**
     * 合规性得分
     */
    private Integer complianceScore;

    /**
     * 风险与合规评审备注
     */
    private String riskComplianceRemarks;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 评审时间
     */
    private LocalDateTime reviewTime;

    /**
     * 评审状态：0-草稿 1-已提交
     */
    private String status;
} 