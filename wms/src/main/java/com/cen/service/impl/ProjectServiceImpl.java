package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.entity.Project;
import com.cen.entity.ProjectCategory;
import com.cen.entity.ProjectExpert;
import com.cen.mapper.ProjectCategoryMapper;
import com.cen.mapper.ProjectExpertMapper;
import com.cen.mapper.ProjectMapper;
import com.cen.service.IProjectService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目服务实现类
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Resource
    private ProjectMapper projectMapper;
    
    @Resource
    private ProjectCategoryMapper categoryMapper;
    
    @Resource
    private ProjectExpertMapper projectExpertMapper;

    @Override
    public Page<Project> pageProject(int pageNum, int pageSize, String name, Long categoryId, String status) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name), "name", name);
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (Strings.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        
        Page<Project> page = projectMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        List<Project> records = page.getRecords();
        
        if (!records.isEmpty()) {
            // 查询分类信息
            List<Long> categoryIds = records.stream().map(Project::getCategoryId).collect(Collectors.toList());
            List<ProjectCategory> categories = categoryMapper.selectBatchIds(categoryIds);
            Map<Long, String> categoryMap = categories.stream().collect(
                    Collectors.toMap(ProjectCategory::getId, ProjectCategory::getName));
            
            // 设置分类名称
            for (Project project : records) {
                project.setCategoryName(categoryMap.getOrDefault(project.getCategoryId(), ""));
            }
        }
        
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignExperts(Long projectId, List<Long> expertIds) {
        // 先删除之前的分配
        QueryWrapper<ProjectExpert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        projectExpertMapper.delete(queryWrapper);
        
        // 批量插入新的分配
        List<ProjectExpert> projectExperts = new ArrayList<>();
        for (Long expertId : expertIds) {
            ProjectExpert projectExpert = new ProjectExpert();
            projectExpert.setProjectId(projectId);
            projectExpert.setExpertId(expertId);
            projectExpert.setCreateTime(LocalDateTime.now());
            projectExperts.add(projectExpert);
        }
        
        // 更新项目状态为已发布
        Project project = projectMapper.selectById(projectId);
        if (project != null) {
            project.setStatus("1"); // 已发布
            projectMapper.updateById(project);
        }
        
        return projectExpertMapper.insertBatchProjectExperts(projectExperts);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Project project = projectMapper.selectById(id);
        if (project != null) {
            project.setStatus(status);
            return projectMapper.updateById(project) > 0;
        }
        return false;
    }
} 