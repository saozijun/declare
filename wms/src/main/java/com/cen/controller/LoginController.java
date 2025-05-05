package com.cen.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.cen.common.Constants;
import com.cen.common.Result;
import com.cen.common.lang.Const;
import com.cen.controller.dto.ExpertRegisterDTO;
import com.cen.controller.dto.UserDTO;
import com.cen.service.IExpertService;
import com.cen.service.IUserService;
import com.cen.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@RestController
public class LoginController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IUserService userService;
    @Autowired
    private IExpertService expertService;

    //获取验证码
    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response) throws IOException {
        String key = UUID.randomUUID().toString();
        // 利用 hutool 工具，生成验证码图片资源
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 5);
        // 获得生成的验证码字符
        String code = captcha.getCode();
        // 利用 redis 来存储验证码
        redisUtil.set(Const.CAPTCHA_KEY,code);
        // 将验证码图片的二进制数据写入【响应体 response 】
        captcha.write(response.getOutputStream());
    }
    /*登录接口*/
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        return Result.success(userService.login(userDTO));
    }
    /*注册接口*/
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        return Result.success(userService.register(userDTO));
    }

    /*专家注册接口*/
    @PostMapping("/expert/register")
    public Result expertRegister(@RequestBody ExpertRegisterDTO expertRegisterDTO) {
        String username = expertRegisterDTO.getUsername();
        String password = expertRegisterDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        // 注册用户并获取用户ID
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(expertRegisterDTO.getUsername());
        userDTO.setPassword(expertRegisterDTO.getPassword());
        userDTO.setNickname(expertRegisterDTO.getName());
        userDTO.setEmail(expertRegisterDTO.getEmail());
        
        Long userId = userService.expertRegister(userDTO);
        
        // 注册专家信息
        expertRegisterDTO.setUserId(userId);
        return Result.success(expertService.registerExpert(expertRegisterDTO));
    }

    /*注册接口*/
    @PostMapping("/user/register")
    public Result userRegister(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        return Result.success(userService.userRegister(userDTO));
    }

    /*用户登录接口 - 仅允许user角色登录*/
    @PostMapping("/user/login")
    public Result userLogin(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        return Result.success(userService.userLogin(userDTO));
    }
}
