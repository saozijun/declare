package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.ExpertAssignment;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专家分配服务接口
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
public interface IExpertAssignmentService extends IService<ExpertAssignment> {

    /**
     * 分配专家
     * @param applicationId 项目申报ID
     * @param expertIds 专家ID列表
     * @return 是否成功
     */
    boolean assignExperts(Long applicationId, List<Long> expertIds);

    /**
     * 查询专家分配列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param applicationId 项目申报ID
     * @param expertId 专家ID
     * @param status 状态
     * @return 分页对象
     */
    Page<ExpertAssignment> pageAssignment(Integer pageNum, Integer pageSize, Long applicationId, Long expertId, String status);
    
    /**
     * 查询专家分配列表，并关联项目申报信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param applicationId 项目申报ID
     * @param expertId 专家ID
     * @param status 状态
     * @return 分页对象，包含application信息
     */
    Page<Map<String, Object>> pageAssignmentWithApplication(Integer pageNum, Integer pageSize, Long applicationId, Long expertId, String status);

    /**
     * 查询项目分配的专家ID列表
     * @param applicationId 项目申报ID
     * @return 专家ID列表
     */
    List<Long> getAssignedExpertIds(Long applicationId);
} 