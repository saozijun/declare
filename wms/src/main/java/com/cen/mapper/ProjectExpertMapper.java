package com.cen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cen.entity.ProjectExpert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目专家关联 Mapper 接口
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Mapper
public interface ProjectExpertMapper extends BaseMapper<ProjectExpert> {
    
    /**
     * 批量插入项目专家关联数据
     * @param projectExperts 项目专家关联列表
     * @return 是否成功
     */
    boolean insertBatchProjectExperts(@Param("list") List<ProjectExpert> projectExperts);
} 