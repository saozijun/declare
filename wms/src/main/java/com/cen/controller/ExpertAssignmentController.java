package com.cen.controller;

import com.cen.common.Result;
import com.cen.entity.User;
import com.cen.service.IExpertAssignmentService;
import com.cen.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 专家分配控制器
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@RestController
@RequestMapping("/expert/assignment")
public class ExpertAssignmentController {

    @Resource
    private IExpertAssignmentService assignmentService;
    
    @Resource
    private IUserService userService;

    /**
     * 分配专家
     */
    @PostMapping("/assign")
    public Result assign(@RequestParam Long applicationId, @RequestBody List<Long> expertIds) {
        if (assignmentService.assignExperts(applicationId, expertIds)) {
            return Result.success();
        }
        return Result.error(400,"分配失败");
    }

    /**
     * 查询项目的专家分配列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) Long applicationId,
                       @RequestParam(required = false) Long expertId,
                       @RequestParam(required = false) String status) {
        return Result.success(assignmentService.pageAssignmentWithApplication(pageNum, pageSize, applicationId, expertId, status));
    }

    /**
     * 获取项目已分配的专家ID列表
     */
    @GetMapping("/assigned-experts")
    public Result getAssignedExperts(@RequestParam Long applicationId) {
        return Result.success(assignmentService.getAssignedExpertIds(applicationId));
    }
    
    /**
     * 获取项目已分配的专家详细信息
     */
    @GetMapping("/assigned-experts-detail")
    public Result getAssignedExpertsDetail(@RequestParam Long applicationId) {
        List<Long> expertIds = assignmentService.getAssignedExpertIds(applicationId);
        if (expertIds == null || expertIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        
        List<User> experts = userService.listByIds(expertIds);
        // 只返回专家的基本信息
        List<User> simplifiedExperts = experts.stream()
            .map(expert -> {
                User simpleUser = new User();
                simpleUser.setId(expert.getId());
                simpleUser.setNickname(expert.getNickname());
                simpleUser.setAvatarUrl(expert.getAvatarUrl());
                simpleUser.setEmail(expert.getEmail());
                return simpleUser;
            })
            .collect(Collectors.toList());
        
        return Result.success(simplifiedExperts);
    }
    
    /**
     * 获取申报项目的专家分配信息（匹配前端API路径）
     */
    @GetMapping("/application-experts")
    public Result getApplicationExperts(@RequestParam Long applicationId) {
        List<Long> expertIds = assignmentService.getAssignedExpertIds(applicationId);
        if (expertIds == null || expertIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        
        List<User> experts = userService.listByIds(expertIds);
        // 只返回专家的基本信息
        List<User> simplifiedExperts = experts.stream()
            .map(expert -> {
                User simpleUser = new User();
                simpleUser.setId(expert.getId());
                simpleUser.setNickname(expert.getNickname());
                simpleUser.setAvatarUrl(expert.getAvatarUrl());
                simpleUser.setEmail(expert.getEmail());
                return simpleUser;
            })
            .collect(Collectors.toList());
        
        return Result.success(simplifiedExperts);
    }
} 