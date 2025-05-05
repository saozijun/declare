package com.cen.mapper;

import com.cen.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author volcano
 * @since 2025-03-20
 */
public interface UserMapper extends BaseMapper<User> {
    List<User> getStudentsByCourseId(Long courseId);
}
