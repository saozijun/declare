package com.cen.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import com.cen.entity.ExpertAssignment;
import com.cen.entity.ExpertReview;
import com.cen.entity.ProjectApplication;
import com.cen.mapper.ExpertAssignmentMapper;
import com.cen.mapper.ProjectApplicationMapper;
import com.cen.service.IExpertReviewService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专家评审控制器
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@RestController
@RequestMapping("/expert/review")
public class ExpertReviewController {

    @Resource
    private IExpertReviewService reviewService;
    
    @Resource
    private ExpertAssignmentMapper assignmentMapper;
    
    @Resource
    private ProjectApplicationMapper projectApplicationMapper;

    /**
     * 提交评审
     */
    @PostMapping("/submit")
    public Result submitReview(@RequestBody ExpertReview review) {
        if (reviewService.submitReview(review)) {
            // 获取项目申报ID
            Long applicationId = review.getApplicationId();
            
            // 检查是否所有专家都已完成评审
            if (applicationId != null) {
                checkAllExpertsReviewed(applicationId);
            }
            
            return Result.success();
        }
        return Result.error(400,"提交失败");
    }

    /**
     * 检查是否所有专家都已完成评审，如果是，则更新项目申报状态
     * @param applicationId 项目申报ID
     */
    private void checkAllExpertsReviewed(Long applicationId) {
        // 查询该项目所有专家分配记录
        QueryWrapper<ExpertAssignment> assignmentQueryWrapper = new QueryWrapper<>();
        assignmentQueryWrapper.eq("application_id", applicationId);
        List<ExpertAssignment> assignments = assignmentMapper.selectList(assignmentQueryWrapper);
        
        if (assignments == null || assignments.isEmpty()) {
            return;
        }
        
        // 检查是否所有专家都已完成评审
        boolean allReviewed = true;
        for (ExpertAssignment assignment : assignments) {
            // 如果有专家尚未评审，则标记为未全部完成
            if (!"1".equals(assignment.getStatus())) {
                allReviewed = false;
                break;
            }
        }
        
        // 如果所有专家都已评审，则更新项目申报状态为"已评审完成(4)"
        if (allReviewed) {
            ProjectApplication application = projectApplicationMapper.selectById(applicationId);
            if (application != null) {
                application.setStatus("4"); // 设置状态为"已评审完成"
                projectApplicationMapper.updateById(application);
            }
        }
    }

    /**
     * 保存评审草稿
     */
    @PostMapping("/save-draft")
    public Result saveDraft(@RequestBody ExpertReview review) {
        if (reviewService.saveReviewDraft(review)) {
            return Result.success();
        }
        return Result.error(400,"保存失败");
    }

    /**
     * 查询评审列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) Long applicationId,
                       @RequestParam(required = false) Long expertId,
                       @RequestParam(required = false) String status) {
        return Result.success(reviewService.pageReview(pageNum, pageSize, applicationId, expertId, status));
    }

    /**
     * 查询评审详情
     */
    @GetMapping("/detail")
    public Result detail(@RequestParam Long id) {
        return Result.success(reviewService.getReviewDetail(id));
    }

    /**
     * 根据分配ID查询评审
     */
    @GetMapping("/by-assignment")
    public Result getByAssignment(@RequestParam Long assignmentId) {
        return Result.success(reviewService.getReviewByAssignmentId(assignmentId));
    }
    
    /**
     * 获取项目申报的评审结果
     */
    @GetMapping("/result")
    public Result getReviewResult(@RequestParam Long applicationId) {
        // 原有的汇总数据
        Object summaryResult = reviewService.getReviewResultByApplicationId(applicationId);
        
        // 获取所有评审详情
        QueryWrapper<ExpertReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.eq("status", "1"); // 已提交的评审
        List<ExpertReview> reviews = reviewService.list(queryWrapper);
        
        // 构造结果对象
        Map<String, Object> result = new HashMap<>();
        
        // 添加汇总数据
        if (summaryResult != null) {
            result.put("summary", summaryResult);
        }
        
        // 添加详细评审列表
        result.put("reviews", reviews);
        
        return Result.success(result);
    }
} 