package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.entity.ExpertAssignment;
import com.cen.entity.ProjectApplication;
import com.cen.entity.User;
import com.cen.mapper.ExpertAssignmentMapper;
import com.cen.mapper.ProjectApplicationMapper;
import com.cen.mapper.UserMapper;
import com.cen.service.IExpertAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 专家分配服务实现类
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Service
public class ExpertAssignmentServiceImpl extends ServiceImpl<ExpertAssignmentMapper, ExpertAssignment> implements IExpertAssignmentService {

    @Resource
    private ExpertAssignmentMapper assignmentMapper;
    
    @Resource
    private ProjectApplicationMapper applicationMapper;
    
    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignExperts(Long applicationId, List<Long> expertIds) {
        if (applicationId == null || expertIds == null || expertIds.isEmpty()) {
            return false;
        }
        
        // 检查项目申报是否存在且状态为已通过
        ProjectApplication application = applicationMapper.selectById(applicationId);
        if (application == null || !"1".equals(application.getStatus())) {
            return false;
        }
        
        // 查询已分配的专家
        QueryWrapper<ExpertAssignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        List<ExpertAssignment> existAssignments = assignmentMapper.selectList(queryWrapper);
        List<Long> existExpertIds = existAssignments.stream()
                .map(ExpertAssignment::getExpertId)
                .collect(Collectors.toList());
        
        // 删除不再分配的专家
        existAssignments.forEach(assignment -> {
            if (!expertIds.contains(assignment.getExpertId())) {
                assignmentMapper.deleteById(assignment.getId());
            }
        });
        
        // 添加新分配的专家
        LocalDateTime now = LocalDateTime.now();
        for (Long expertId : expertIds) {
            if (!existExpertIds.contains(expertId)) {
                ExpertAssignment assignment = new ExpertAssignment();
                assignment.setApplicationId(applicationId);
                assignment.setExpertId(expertId);
                assignment.setStatus("0"); // 待评审
                assignment.setAssignTime(now);
                assignmentMapper.insert(assignment);
            }
        }
        
        return true;
    }

    @Override
    public Page<ExpertAssignment> pageAssignment(Integer pageNum, Integer pageSize, Long applicationId, Long expertId, String status) {
        QueryWrapper<ExpertAssignment> queryWrapper = new QueryWrapper<>();
        if (applicationId != null) {
            queryWrapper.eq("application_id", applicationId);
        }
        if (expertId != null) {
            queryWrapper.eq("expert_id", expertId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("assign_time");
        
        return assignmentMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public Page<Map<String, Object>> pageAssignmentWithApplication(Integer pageNum, Integer pageSize, Long applicationId, Long expertId, String status) {
        // 1. 获取分配列表
        Page<ExpertAssignment> assignmentPage = pageAssignment(pageNum, pageSize, applicationId, expertId, status);
        List<ExpertAssignment> assignments = assignmentPage.getRecords();
        
        // 创建结果页对象
        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize, assignmentPage.getTotal());
        List<Map<String, Object>> resultRecords = new ArrayList<>();
        
        if (!assignments.isEmpty()) {
            // 2. 获取所有申请ID
            List<Long> applicationIds = assignments.stream()
                    .map(ExpertAssignment::getApplicationId)
                    .collect(Collectors.toList());
            
            // 3. 批量查询申请信息
            List<ProjectApplication> applications = applicationMapper.selectBatchIds(applicationIds);
            Map<Long, ProjectApplication> applicationMap = applications.stream()
                    .collect(Collectors.toMap(ProjectApplication::getId, app -> app));
            
            // 4. 获取专家ID列表
            List<Long> expertIds = assignments.stream()
                    .map(ExpertAssignment::getExpertId)
                    .collect(Collectors.toList());
            
            // 5. 批量查询专家信息
            List<User> experts = userMapper.selectBatchIds(expertIds);
            Map<Long, User> expertMap = experts.stream()
                    .collect(Collectors.toMap(User::getId, expert -> expert));
            
            // 6. 组装结果
            for (ExpertAssignment assignment : assignments) {
                Map<String, Object> resultRecord = new HashMap<>();
                // 复制分配信息
                resultRecord.put("id", assignment.getId());
                resultRecord.put("applicationId", assignment.getApplicationId());
                resultRecord.put("expertId", assignment.getExpertId());
                resultRecord.put("status", assignment.getStatus());
                resultRecord.put("assignTime", assignment.getAssignTime());
                resultRecord.put("reviewTime", assignment.getReviewTime());
                
                // 关联申请信息
                ProjectApplication app = applicationMap.get(assignment.getApplicationId());
                if (app != null) {
                    resultRecord.put("application", app);
                }
                
                // 关联专家信息
                User expert = expertMap.get(assignment.getExpertId());
                if (expert != null) {
                    resultRecord.put("expertName", expert.getUsername());
                }
                
                resultRecords.add(resultRecord);
            }
        }
        
        resultPage.setRecords(resultRecords);
        return resultPage;
    }

    @Override
    public List<Long> getAssignedExpertIds(Long applicationId) {
        if (applicationId == null) {
            return new ArrayList<>();
        }
        
        QueryWrapper<ExpertAssignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.select("expert_id");
        
        List<ExpertAssignment> assignments = assignmentMapper.selectList(queryWrapper);
        return assignments.stream()
                .map(ExpertAssignment::getExpertId)
                .collect(Collectors.toList());
    }
} 