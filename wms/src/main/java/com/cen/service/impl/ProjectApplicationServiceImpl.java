package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.entity.Project;
import com.cen.entity.ProjectApplication;
import com.cen.entity.User;
import com.cen.mapper.ProjectApplicationMapper;
import com.cen.mapper.ProjectMapper;
import com.cen.mapper.UserMapper;
import com.cen.service.IProjectApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目申报服务实现类
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Service
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication> implements IProjectApplicationService {

    @Resource
    private ProjectApplicationMapper applicationMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<ProjectApplication> pageApplication(Integer pageNum, Integer pageSize, Long projectId, Long userId, String status, String projectName, String organization) {
        QueryWrapper<ProjectApplication> queryWrapper = new QueryWrapper<>();
        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (projectName != null && !projectName.isEmpty()) {
            queryWrapper.like("project_name", projectName);
        }
        if (organization != null && !organization.isEmpty()) {
            queryWrapper.like("organization", organization);
        }
        queryWrapper.orderByDesc("create_time");

        Page<ProjectApplication> page = applicationMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        List<ProjectApplication> records = page.getRecords();

        if (!records.isEmpty()) {
            // 查询用户信息
            List<Long> userIds = records.stream().map(ProjectApplication::getUserId).collect(Collectors.toList());
            List<User> users = userMapper.selectBatchIds(userIds);
            Map<Long, String> userMap = users.stream().collect(
                    Collectors.toMap(User::getId, User::getUsername));

            // 设置用户名称
            for (ProjectApplication application : records) {
                application.setUserName(userMap.getOrDefault(application.getUserId(), ""));
            }
        }

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApplication(ProjectApplication application) {
        // 检查项目是否存在且已发布
        Project project = projectMapper.selectById(application.getProjectId());
        if (project == null || !"1".equals(project.getStatus())) {
            return false;
        }

        // 设置项目名称
        application.setProjectName(project.getName());
        
        application.setStatus("0"); // 待审核
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        return applicationMapper.insert(application) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewApplication(Long id, String status) {
        ProjectApplication application = applicationMapper.selectById(id);
        if (application == null) {
            return false;
        }

        application.setStatus(status);
        application.setUpdateTime(LocalDateTime.now());
        return applicationMapper.updateById(application) > 0;
    }

    /**
     * 获取可分配的专家列表
     * @return 专家列表
     */
    @Override
    public List<User> getAvailableExperts() {
        // 查询所有角色为专家的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "expert"); // 假设角色ID 3为专家角色
        return userMapper.selectList(queryWrapper);
    }
} 