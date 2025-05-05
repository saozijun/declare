package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.Project;

import java.util.List;

/**
 * <p>
 * 项目服务接口
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
public interface IProjectService extends IService<Project> {

    /**
     * 分页查询项目
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 项目名称
     * @param categoryId 分类ID
     * @param status 状态
     * @return 分页结果
     */
    Page<Project> pageProject(int pageNum, int pageSize, String name, Long categoryId, String status);
    
    /**
     * 分配专家
     * @param projectId 项目ID
     * @param expertIds 专家ID列表
     * @return 是否成功
     */
    boolean assignExperts(Long projectId, List<Long> expertIds);
    
    /**
     * 更新项目状态
     * @param id 项目ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);
} 