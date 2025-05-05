package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.ExpertReview;

/**
 * <p>
 * 专家评审服务接口
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
public interface IExpertReviewService extends IService<ExpertReview> {

    /**
     * 提交专家评审
     * @param review 评审对象
     * @return 是否成功
     */
    boolean submitReview(ExpertReview review);

    /**
     * 保存评审草稿
     * @param review 评审对象
     * @return 是否成功
     */
    boolean saveReviewDraft(ExpertReview review);

    /**
     * 查询专家评审列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param applicationId 项目申报ID
     * @param expertId 专家ID
     * @param status 状态
     * @return 分页对象
     */
    Page<ExpertReview> pageReview(Integer pageNum, Integer pageSize, Long applicationId, Long expertId, String status);

    /**
     * 查询评审详情
     * @param id 评审ID
     * @return 评审对象
     */
    ExpertReview getReviewDetail(Long id);

    /**
     * 根据分配ID查询评审
     * @param assignmentId 分配ID
     * @return 评审对象
     */
    ExpertReview getReviewByAssignmentId(Long assignmentId);
    
    /**
     * 获取项目申报的评审结果
     * @param applicationId 项目申报ID
     * @return 评审结果对象
     */
    Object getReviewResultByApplicationId(Long applicationId);
} 