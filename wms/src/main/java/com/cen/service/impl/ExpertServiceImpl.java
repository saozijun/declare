package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.common.Constants;
import com.cen.common.Result;
import com.cen.controller.dto.ExpertRegisterDTO;
import com.cen.entity.Expert;
import com.cen.entity.User;
import com.cen.exception.ServiceException;
import com.cen.mapper.ExpertMapper;
import com.cen.service.IExpertService;
import com.cen.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertMapper, Expert> implements IExpertService {

    @Resource
    private IUserService userService;

    @Override
    @Transactional
    public Result saveExpert(Expert expert) {
        // 检查用户是否存在
        User user = userService.getById(expert.getUserId());
        if (user == null) {
            throw new ServiceException(Constants.CODE_500, "用户不存在");
        }
        
        // 检查用户角色是否为普通用户
        if (!"user".equals(user.getRole())) {
            throw new ServiceException(Constants.CODE_500, "只能选择普通用户作为专家");
        }

        // 检查是否已存在专家信息
        QueryWrapper<Expert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", expert.getUserId());
        Expert existingExpert = getOne(queryWrapper);
        if (existingExpert != null) {
            throw new ServiceException(Constants.CODE_500, "该用户已存在专家信息");
        }

        // 设置初始状态为审核中
        expert.setStatus("审核中");
        save(expert);
        
        // 更新用户角色为专家
        user.setRole("expert");
        user.setRoleId(3);
        userService.updateById(user);
        
        return Result.success(expert);
    }

    @Override
    public Result updateExpert(Expert expert) {
        Expert existingExpert = getById(expert.getId());
        if (existingExpert == null) {
            throw new ServiceException(Constants.CODE_500, "专家信息不存在");
        }
        updateById(expert);
        return Result.success(expert);
    }

    @Override
    public Result getExpertByUserId(Integer userId) {
        QueryWrapper<Expert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Expert expert = getOne(queryWrapper);
        if (expert == null) {
            throw new ServiceException(Constants.CODE_500, "未找到专家信息");
        }
        return Result.success(expert);
    }

    @Override
    public Result getExpertList() {
        return Result.success(list());
    }

    @Override
    @Transactional
    public Result updateExpertStatus(Integer id, String status) {
        Expert expert = getById(id);
        if (expert == null) {
            throw new ServiceException(Constants.CODE_500, "专家不存在");
        }
        
        expert.setStatus(status);
        updateById(expert);
        
        // 如果状态为已拒绝，将用户角色改回普通用户
        if ("已拒绝".equals(status)) {
            User user = userService.getById(expert.getUserId());
            if (user != null) {
                user.setRole("user");
                user.setRoleId(2);
                userService.updateById(user);
            }
        }
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result registerExpert(ExpertRegisterDTO expertRegisterDTO) {
        // 验证用户ID是否存在
        if (expertRegisterDTO.getUserId() == null) {
            throw new ServiceException(Constants.CODE_500, "用户注册失败");
        }
        
        // 检查是否已存在专家信息
        QueryWrapper<Expert> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", expertRegisterDTO.getUserId());
        Expert existingExpert = getOne(queryWrapper);
        if (existingExpert != null) {
            throw new ServiceException(Constants.CODE_500, "该用户已存在专家信息");
        }
        
        // 创建新的专家信息
        Expert expert = new Expert();
        // 从DTO复制属性到实体
        BeanUtils.copyProperties(expertRegisterDTO, expert);
        
        // 设置初始状态为审核中
        expert.setStatus("审核中");
        
        // 保存专家信息
        save(expert);
        
        return Result.success(expert);
    }
} 