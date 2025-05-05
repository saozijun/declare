package com.cen.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Constants;
import com.cen.common.lang.Const;
import com.cen.controller.dto.UserDTO;
import com.cen.entity.FileD;
import com.cen.mapper.UserMapper;
import com.cen.utils.RedisUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.cen.service.IUserService;
import com.cen.entity.User;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-03-20
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${files.upload.path}")
    private  String fileUploadPath;
    @Resource
    private IUserService userService;
    @Resource
    UserMapper userMapper;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
//        return Result.success(userService.saveOrUpdate(user));
        return Result.success(userService.saveUser(user));
    }
    //修改密码
    @PostMapping("/edit/pow")
    public Result editPow(@RequestBody User user) {
        return Result.success(userService.editPow(user));
    }
    //上传头像
    @PostMapping("/upload/avatar")
    public Result uploadAvatar(@RequestBody User user) throws IOException {

        return Result.success(userService.saveOrUpdate(user));
    }
    //删除
    @PostMapping("/delete")
    public Result userDelete(@RequestBody User user){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(userService.removeById(user.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result batch(@RequestBody List<Integer> ids){
        return Result.success(userService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@RequestParam Long id) {
        return Result.success(userService.getById(id));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String nickname,
                           @RequestParam(defaultValue = "") String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(nickname),"nickname",nickname);
        queryWrapper.like(Strings.isNotEmpty(email),"email",email);
        queryWrapper.orderByDesc("id"); //设置id倒序
        return Result.success(userService.page(new Page<>(pageNum, pageSize),queryWrapper));
    }
    private User getFlieMd5(String md5){
        // 查询文件的md5是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<User> fileslist = userMapper.selectList(queryWrapper);
        return fileslist.size()==0?null:fileslist.get(0);
    }
    // 查询学生列表
    @GetMapping("/student/list")
    public Result getStudentList() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "student");  // 根据role字段筛选学生
        queryWrapper.orderByDesc("id");
        return Result.success(userService.list(queryWrapper));
    }
    
    // 查询专家列表
    @GetMapping("/expert/list")
    public Result getExpertList() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "expert");  // 根据role字段筛选专家
        queryWrapper.orderByDesc("id");
        return Result.success(userService.list(queryWrapper));
    }
    
    // 获取用户信息（用于聊天）
    @GetMapping("/info/{userId}")
    public Result getUserInfo(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        
        // 移除敏感信息
        user.setPassword(null);
        
        return Result.success(user);
    }
    
    // 更新用户资料（头像和昵称）
    @PostMapping("/update/profile")
    public Result updateUserProfile(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error(400, "用户ID不能为空");
        }
        
        User existingUser = userService.getById(user.getId());
        if (existingUser == null) {
            return Result.error(404, "用户不存在");
        }
        
        // 更新用户资料（头像、昵称和邮箱）
        existingUser.setNickname(user.getNickname());
        if (StrUtil.isNotBlank(user.getAvatarUrl())) {
            existingUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (StrUtil.isNotBlank(user.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }
        
        boolean success = userService.updateById(existingUser);
        if (success) {
            return Result.success();
        } else {
            return Result.error(500, "更新用户信息失败");
        }
    }
    
    // 更新用户状态
    @PostMapping("/updateStatus")
    public Result updateUserStatus(@RequestBody User user) {
        if (user.getId() == null || user.getStatus() == null) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        
        User existingUser = userService.getById(user.getId());
        if (existingUser == null) {
            return Result.error(Constants.CODE_400, "用户不存在");
        }
        
        // 不允许修改管理员状态
        if (existingUser.getId() == 1) {
            return Result.error(Constants.CODE_400, "不能修改管理员状态");
        }
        
        existingUser.setStatus(user.getStatus());
        boolean success = userService.updateById(existingUser);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error(Constants.CODE_500, "更新状态失败");
        }
    }
}

