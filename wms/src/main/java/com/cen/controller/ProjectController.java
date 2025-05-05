package com.cen.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import com.cen.entity.Project;
import com.cen.entity.ProjectExpert;
import com.cen.mapper.ProjectExpertMapper;
import com.cen.service.IProjectService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目控制器
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Resource
    private IProjectService projectService;
    
    @Resource
    private ProjectExpertMapper projectExpertMapper;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(defaultValue = "") String name,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String status) {
        return Result.success(projectService.pageProject(pageNum, pageSize, name, categoryId, status));
    }

    /**
     * 搜索项目
     */
    @GetMapping("/search")
    public Result search(@RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         @RequestParam(required = false) String keyword) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        // 只搜索已发布的项目
        queryWrapper.eq("status", "1");
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                      .like("name", keyword)
                      .or()
                      .like("description", keyword)
                      .or()
                      .like("leader", keyword));
        }
        
        queryWrapper.orderByDesc("create_time");
        
        return Result.success(projectService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/getById")
    public Result getById(@RequestParam Long id) {
        return Result.success(projectService.getById(id));
    }

    /**
     * 保存或更新
     */
    @PostMapping("/save")
    public Result save(@RequestBody Project project) {
        // 新增时设置状态为待审核
        if (project.getId() == null) {
            project.setStatus("0"); // 待审核
            project.setCreateTime(LocalDateTime.now());
        }
        project.setUpdateTime(LocalDateTime.now());
        return Result.success(projectService.saveOrUpdate(project));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result delete(@RequestBody Project project) {
        return Result.success(projectService.removeById(project.getId()));
    }

    /**
     * 批量删除
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(projectService.removeBatchByIds(ids));
    }

    /**
     * 分配专家
     */
    @PostMapping("/assign")
    public Result assignExperts(@RequestParam Long projectId, @RequestBody List<Long> expertIds) {
        return Result.success(projectService.assignExperts(projectId, expertIds));
    }

    /**
     * 更新项目状态
     */
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestParam Long id, @RequestParam String status) {
        return Result.success(projectService.updateStatus(id, status));
    }

    /**
     * 发布项目
     */
    @PostMapping("/publish")
    public Result publishProject(@RequestParam Long id) {
        return Result.success(projectService.updateStatus(id, "1")); // 1表示已发布
    }

    /**
     * 取消发布项目
     */
    @PostMapping("/unpublish")
    public Result unpublishProject(@RequestParam Long id) {
        return Result.success(projectService.updateStatus(id, "0")); // 0表示未发布
    }

    /**
     * 获取项目评审记录
     */
    @GetMapping("/reviews")
    public Result getProjectReviews(@RequestParam Long projectId) {
        QueryWrapper<ProjectExpert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        List<ProjectExpert> reviews = projectExpertMapper.selectList(queryWrapper);
        return Result.success(reviews);
    }

    /**
     * 获取项目统计信息
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取总项目数
        long totalProjects = projectService.count();
        statistics.put("totalProjects", totalProjects);
        
        // 获取已发布项目数
        QueryWrapper<Project> publishedWrapper = new QueryWrapper<>();
        publishedWrapper.eq("status", "1");
        long publishedProjects = projectService.count(publishedWrapper);
        statistics.put("publishedProjects", publishedProjects);
        
        // 获取待审核项目数
        QueryWrapper<Project> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "0");
        long pendingProjects = projectService.count(pendingWrapper);
        statistics.put("pendingProjects", pendingProjects);
        
        return Result.success(statistics);
    }

    /**
     * 获取推荐项目
     */
    @GetMapping("/recommendations")
    public Result getRecommendations(@RequestParam(defaultValue = "5") Integer limit) {
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "1") // 已发布
               .orderByDesc("create_time") // 按创建时间倒序
               .last("LIMIT " + limit); // 限制数量
        return Result.success(projectService.list(wrapper));
    }
} 