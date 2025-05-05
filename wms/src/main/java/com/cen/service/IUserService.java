package com.cen.service;

import com.cen.controller.dto.UserDTO;
import com.cen.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.common.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author volcano
 * @since 2025-03-20
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    Object register(UserDTO userDTO);

    Object userRegister(UserDTO userDTO);
    
    Long expertRegister(UserDTO userDTO);

    Result saveUser(User user);

    Result getAvailableUsersForExpert();

    Object editPow(User user);

    UserDTO userLogin(UserDTO userDTO);
}
