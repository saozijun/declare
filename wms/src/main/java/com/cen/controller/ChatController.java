package com.cen.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cen.common.Result;
import com.cen.entity.ChatMessage;
import com.cen.entity.User;
import com.cen.service.IChatMessageService;
import com.cen.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 聊天消息控制器
 * </p>
 *
 * @author volcano
 * @since 2023-05-20
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private IChatMessageService chatMessageService;
    
    @Resource
    private IUserService userService;

    /**
     * 获取聊天用户列表
     */
    @GetMapping("/users")
    public Result getChatUsers(@RequestParam Long currentUserId) {
        return Result.success(chatMessageService.getChatUsers(currentUserId));
    }

    /**
     * 获取与指定用户的聊天记录
     */
    @GetMapping("/messages")
    public Result getChatMessages(
            @RequestParam Long userId,
            @RequestParam Long currentUserId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        // 将消息标记为已读
        chatMessageService.markMessagesAsRead(userId, currentUserId);
        
        Page<ChatMessage> page = chatMessageService.getChatMessages(currentUserId, userId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 发送聊天消息
     */
    @PostMapping("/send")
    public Result sendMessage(@RequestBody Map<String, Object> params) {
        Long receiverId = Long.parseLong(params.get("receiverId").toString());
        String content = params.get("content").toString();
        Long senderId = Long.parseLong(params.get("senderId").toString());
        
        // 检查接收者是否存在
        User receiver = userService.getById(receiverId);
        if (receiver == null) {
            return Result.error(400, "接收者不存在");
        }
        
        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setIsRead(false);
        
        boolean success = chatMessageService.save(message);
        if (success) {
            return Result.success(message);
        } else {
            return Result.error(500, "发送消息失败");
        }
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/read")
    public Result markMessagesAsRead(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        Long currentUserId = Long.parseLong(params.get("currentUserId").toString());
        
        boolean success = chatMessageService.markMessagesAsRead(userId, currentUserId);
        if (success) {
            return Result.success();
        } else {
            return Result.error(500, "标记消息状态失败");
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public Result getUnreadCount(@RequestParam Long userId) {
        int count = chatMessageService.getUnreadCount(userId);
        return Result.success(count);
    }
} 