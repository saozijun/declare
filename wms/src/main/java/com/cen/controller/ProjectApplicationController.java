package com.cen.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Result;
import com.cen.entity.ExpertReview;
import com.cen.entity.ProjectApplication;
import com.cen.service.IExpertAssignmentService;
import com.cen.service.IExpertReviewService;
import com.cen.service.IProjectApplicationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目申报控制器
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@RestController
@RequestMapping("/project/application")
public class ProjectApplicationController {

    @Resource
    private IProjectApplicationService applicationService;
    
    @Resource
    private IExpertAssignmentService expertAssignmentService;
    
    @Resource
    private IExpertReviewService expertReviewService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum,
                      @RequestParam(defaultValue = "10") Integer pageSize,
                      @RequestParam(required = false) Long projectId,
                      @RequestParam(required = false) Long userId,
                      @RequestParam(required = false) String status,
                      @RequestParam(required = false) String projectName,
                      @RequestParam(required = false) String organization) {
        return Result.success(applicationService.pageApplication(pageNum, pageSize, projectId, userId, status, projectName, organization));
    }
    
    /**
     * 获取当前用户已完成评审的项目申报列表
     */
    @GetMapping("/my-completed-reviews")
    public Result getMyCompletedReviews(@RequestParam Long userId, @RequestParam String status) {
        // 查询状态为4（已评审完成）且用户ID匹配的项目申报
        QueryWrapper<ProjectApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", status);
        queryWrapper.orderByDesc("update_time");
        
        List<ProjectApplication> applications = applicationService.list(queryWrapper);
        return Result.success(applications);
    }
    
    /**
     * 获取所有已完成评审的项目列表，按评分排序（用于公示）
     */
    @GetMapping("/announce-list")
    public Result getAnnounceList() {
        // 查询状态为4（已评审完成）的项目申报
        QueryWrapper<ProjectApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "4");
        
        List<ProjectApplication> applications = applicationService.list(queryWrapper);
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        // 获取每个项目的评审结果和评分，并按评分高低排序
        for (ProjectApplication application : applications) {
            Object reviewResult = expertReviewService.getReviewResultByApplicationId(application.getId());
            
            if (reviewResult instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) reviewResult;
                
                // 合并项目信息和评审结果
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("id", application.getId());
                projectMap.put("projectName", application.getProjectName());
                projectMap.put("organization", application.getOrganization());
                projectMap.put("leader", application.getLeader());
                projectMap.put("createTime", application.getCreateTime());
                
                // 添加评审结果
                projectMap.putAll(resultMap);
                
                resultList.add(projectMap);
            }
        }
        
        // 按评分排序（降序）
        resultList.sort((a, b) -> {
            double scoreA = a.containsKey("score") ? Double.parseDouble(a.get("score").toString()) : 0;
            double scoreB = b.containsKey("score") ? Double.parseDouble(b.get("score").toString()) : 0;
            return Double.compare(scoreB, scoreA); // 降序排列
        });
        
        return Result.success(resultList);
    }
    
    /**
     * 根据ID查询项目申报详情
     */
    @GetMapping("/getById")
    public Result getById(@RequestParam Long id) {
        return Result.success(applicationService.getById(id));
    }

    /**
     * 提交申报
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody ProjectApplication application) {
        if (applicationService.submitApplication(application)) {
            return Result.success();
        }
        return Result.error(400,"提交失败，请检查项目是否已发布");
    }

    /**
     * 审核申报
     */
    @PostMapping("/review")
    public Result review(@RequestParam Long id, @RequestParam String status) {
        if (applicationService.reviewApplication(id, status)) {
            return Result.success();
        }
        return Result.error(400,"审核失败");
    }
    
    /**
     * 获取可分配的专家列表
     */
    @GetMapping("/experts")
    public Result getExperts() {
        return Result.success(applicationService.getAvailableExperts());
    }
    
    /**
     * 分配专家
     */
    @PostMapping("/assign-experts")
    public Result assignExperts(@RequestParam Long applicationId, @RequestBody List<Long> expertIds) {
        // 检查申报状态是否为"已通过"，只有已通过的才能分配专家
        ProjectApplication application = applicationService.getById(applicationId);
        if (application == null) {
            return Result.error(400, "申报不存在");
        }
        
        if (!"1".equals(application.getStatus())) {
            return Result.error(400, "只有已通过审核的申报才能分配专家");
        }
        
        if ("3".equals(application.getStatus())) {
            return Result.error(400, "该申报已分配过专家，不能重复分配");
        }
        
        if (expertAssignmentService.assignExperts(applicationId, expertIds)) {
            // 更新申报状态为"已分配"
            application.setStatus("3");
            applicationService.updateById(application);
            return Result.success();
        }
        return Result.error(400,"分配失败");
    }
} 