package com.cen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Result;
import com.cen.entity.ProjectApplication;
import com.cen.service.IExpertAssignmentService;
import com.cen.service.IExpertReviewService;
import com.cen.service.IProjectApplicationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用项目控制器 - 用于兼容前端路径
 * </p>
 *
 * @author cen
 * @since 2024-07-10
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Resource
    private IProjectApplicationService applicationService;
    
    @Resource
    private IExpertReviewService reviewService;
    
    @Resource
    private IExpertAssignmentService assignmentService;
    
    /**
     * 分页查询项目申报
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                      @RequestParam(defaultValue = "10") Integer pageSize,
                      @RequestParam(required = false) Long projectId,
                      @RequestParam(required = false) Long userId,
                      @RequestParam(required = false) String status,
                      @RequestParam(required = false) String projectName,
                      @RequestParam(required = false) String organization) {
        return Result.success(applicationService.pageApplication(pageNum, pageSize, projectId, userId, status, projectName, organization));
    }
    
    /**
     * 获取用户已评审的项目申报列表
     */
    @GetMapping("/reviewed")
    public Result getReviewedApplications(@RequestParam Long userId) {
        // 1. 获取用户所有的项目申报
        Page<ProjectApplication> page = applicationService.pageApplication(1, 100, null, userId, null, null, null);
        List<ProjectApplication> allApplications = page.getRecords();
        
        // 2. 筛选出已评审的项目
        List<ProjectApplication> reviewedApplications = new ArrayList<>();
        
        for (ProjectApplication app : allApplications) {
            // 获取项目的评审结果
            Object reviewResult = reviewService.getReviewResultByApplicationId(app.getId());
            
            // 如果有评审结果，表示已评审
            if (reviewResult != null) {
                reviewedApplications.add(app);
            }
        }
        
        // 3. 构建分页结果
        Page<ProjectApplication> resultPage = new Page<>(1, reviewedApplications.size());
        resultPage.setRecords(reviewedApplications);
        resultPage.setTotal(reviewedApplications.size());
        
        return Result.success(resultPage);
    }
} 