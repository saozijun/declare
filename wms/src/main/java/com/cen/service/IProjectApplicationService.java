package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.ProjectApplication;
import com.cen.entity.User;

import java.util.List;

/**
 * <p>
 * 项目申报服务接口
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
public interface IProjectApplicationService extends IService<ProjectApplication> {

    /**
     * 分页查询项目申报
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param projectId 项目ID
     * @param userId 用户ID
     * @param status 状态
     * @param projectName 项目名称
     * @param organization 申报单位
     * @return 分页对象
     */
    Page<ProjectApplication> pageApplication(Integer pageNum, Integer pageSize, Long projectId, Long userId, String status, String projectName, String organization);

    /**
     * 提交申报
     * @param application 申报对象
     * @return 是否成功
     */
    boolean submitApplication(ProjectApplication application);

    /**
     * 审核申报
     * @param id 申报ID
     * @param status 状态
     * @return 是否成功
     */
    boolean reviewApplication(Long id, String status);
    
    /**
     * 获取可分配的专家列表
     * @return 专家列表
     */
    List<User> getAvailableExperts();
} 