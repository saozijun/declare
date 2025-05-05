package com.cen.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Result;
import com.cen.entity.ProjectCategory;
import com.cen.entity.Project;
import com.cen.service.IProjectCategoryService;
import com.cen.service.IProjectService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目分类控制器
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@RestController
@RequestMapping("/project/category")
public class ProjectCategoryController {

    @Resource
    private IProjectCategoryService projectCategoryService;

    @Resource
    private IProjectService projectService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(defaultValue = "") String name) {
        QueryWrapper<ProjectCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name), "name", name);
        queryWrapper.orderByDesc("id");
        return Result.success(projectCategoryService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result list() {
        QueryWrapper<ProjectCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(projectCategoryService.list(queryWrapper));
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/getById")
    public Result getById(@RequestParam Long id) {
        return Result.success(projectCategoryService.getById(id));
    }

    /**
     * 保存或更新
     */
    @PostMapping("/save")
    public Result save(@RequestBody ProjectCategory projectCategory) {
        return Result.success(projectCategoryService.saveOrUpdate(projectCategory));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result delete(@RequestBody ProjectCategory projectCategory) {
        return Result.success(projectCategoryService.removeById(projectCategory.getId()));
    }

    /**
     * 批量删除
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(projectCategoryService.removeBatchByIds(ids));
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取总分类数
        long totalCategories = projectCategoryService.count();
        statistics.put("totalCategories", totalCategories);
        
        // 获取每个分类下的项目数
        List<ProjectCategory> categories = projectCategoryService.list();
        Map<Long, Long> projectCounts = new HashMap<>();
        
        for (ProjectCategory category : categories) {
            QueryWrapper<Project> wrapper = new QueryWrapper<>();
            wrapper.eq("category_id", category.getId());
            long count = projectService.count(wrapper);
            projectCounts.put(category.getId(), count);
        }
        
        statistics.put("projectCounts", projectCounts);
        
        return Result.success(statistics);
    }

    /**
     * 获取热门分类
     */
    @GetMapping("/hot")
    public Result getHotCategories(@RequestParam(defaultValue = "5") Integer limit) {
        QueryWrapper<ProjectCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("project_count") // 按项目数量倒序
               .last("LIMIT " + limit); // 限制数量
        return Result.success(projectCategoryService.list(wrapper));
    }

    /**
     * 获取分类下的项目列表
     */
    @GetMapping("/projects")
    public Result getCategoryProjects(@RequestParam Long categoryId,
                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId)
               .eq("status", "1") // 只获取已发布的项目
               .orderByDesc("create_time");
        return Result.success(projectService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 获取所有分类（用于分类页面左侧列表）
     */
    @GetMapping("/all")
    public Result getAllCategories() {
        QueryWrapper<ProjectCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id"); // 按ID升序排序
        return Result.success(projectCategoryService.list(queryWrapper));
    }
} 