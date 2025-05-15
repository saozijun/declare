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
     * 企业资质得分
     */
    private Integer enterpriseQualificationScore;

    /**
     * 财务情况得分
     */
    private Integer financialStatusScore;

    /**
     * 业绩案例得分
     */
    private Integer performanceCaseScore;

    /**
     * 履约能力得分
     */
    private Integer performanceCapabilityScore;

    /**
     * 商务评审备注
     */
    private String businessRemarks;

    /**
     * 技术方案响应性得分
     */
    private Integer technicalResponseScore;

    /**
     * 实施方案得分
     */
    private Integer implementationPlanScore;

    /**
     * 质量保障措施得分
     */
    private Integer qualityAssuranceScore;

    /**
     * 售后服务得分
     */
    private Integer afterSaleServiceScore;

    /**
     * 技术评审备注
     */
    private String technicalRemarks;

    /**
     * 价格评分
     */
    private Integer priceScore;

    /**
     * 价格评审备注
     */
    private String priceRemarks;

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