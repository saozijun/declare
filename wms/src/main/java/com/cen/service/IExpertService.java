package com.cen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.Expert;
import com.cen.common.Result;
import com.cen.controller.dto.ExpertRegisterDTO;

public interface IExpertService extends IService<Expert> {
    Result saveExpert(Expert expert);
    Result updateExpert(Expert expert);
    Result getExpertByUserId(Integer userId);
    Result getExpertList();
    Result updateExpertStatus(Integer id, String status);
    
    /**
     * 注册专家信息
     * @param expertRegisterDTO 专家注册信息
     * @return 注册结果
     */
    Result registerExpert(ExpertRegisterDTO expertRegisterDTO);
} 