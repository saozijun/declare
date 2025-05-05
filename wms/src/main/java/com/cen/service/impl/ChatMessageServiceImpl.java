package com.cen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cen.entity.ChatMessage;
import com.cen.entity.User;
import com.cen.entity.vo.ChatUserVO;
import com.cen.mapper.ChatMessageMapper;
import com.cen.mapper.UserMapper;
import com.cen.service.IChatMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 聊天消息服务实现类
 * </p>
 *
 * @author volcano
 * @since 2023-05-20
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<ChatUserVO> getChatUsers(Long userId) {
        // 查询与当前用户有关的所有消息
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender_id", userId).or().eq("receiver_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<ChatMessage> messages = this.list(queryWrapper);

        // 收集与当前用户有聊天记录的用户ID
        Set<Long> userIds = new HashSet<>();
        for (ChatMessage message : messages) {
            if (message.getSenderId().equals(userId)) {
                userIds.add(message.getReceiverId());
            } else {
                userIds.add(message.getSenderId());
            }
        }

        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询这些用户的信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        // 构建聊天用户列表
        Map<Long, ChatUserVO> chatUserMap = new HashMap<>();
        
        for (ChatMessage message : messages) {
            Long otherUserId = message.getSenderId().equals(userId) ? message.getReceiverId() : message.getSenderId();
            
            if (!chatUserMap.containsKey(otherUserId)) {
                User user = userMap.get(otherUserId);
                if (user == null) continue;
                
                ChatUserVO chatUserVO = new ChatUserVO();
                chatUserVO.setId(user.getId());
                chatUserVO.setNickname(user.getNickname());
                chatUserVO.setEmail(user.getEmail());
                chatUserVO.setAvatarUrl(user.getAvatarUrl());
                chatUserVO.setLastMessage(message.getContent());
                chatUserVO.setLastMessageTime(message.getCreateTime());
                
                // 计算未读消息数
                QueryWrapper<ChatMessage> unreadQuery = new QueryWrapper<>();
                unreadQuery.eq("sender_id", otherUserId)
                          .eq("receiver_id", userId)
                          .eq("is_read", false);
                int unreadCount = (int) this.count(unreadQuery);
                chatUserVO.setUnreadCount(unreadCount);
                
                chatUserMap.put(otherUserId, chatUserVO);
            }
        }
        
        // 按最后消息时间排序
        return chatUserMap.values().stream()
                .sorted(Comparator.comparing(ChatUserVO::getLastMessageTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChatMessage> getChatMessages(Long currentUserId, Long targetUserId, Integer pageNum, Integer pageSize) {
        Page<ChatMessage> page = new Page<>(pageNum, pageSize);
        // 修复 setDesc 方法不存在的问题
        // page.setDesc("create_time");  // 按时间倒序
        
        // 查询双方之间的消息
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.nested(i -> i.eq("sender_id", currentUserId).eq("receiver_id", targetUserId))
                    .or(i -> i.eq("sender_id", targetUserId).eq("receiver_id", currentUserId));
        queryWrapper.orderByDesc("create_time");  // 在查询条件中设置倒序
        
        Page<ChatMessage> messagePage = this.page(page, queryWrapper);
        
        // 填充发送者和接收者信息
        if (messagePage.getRecords() != null && !messagePage.getRecords().isEmpty()) {
            User currentUser = userMapper.selectById(currentUserId);
            User targetUser = userMapper.selectById(targetUserId);
            
            for (ChatMessage message : messagePage.getRecords()) {
                if (message.getSenderId().equals(currentUserId)) {
                    message.setSender(currentUser);
                    message.setReceiver(targetUser);
                } else {
                    message.setSender(targetUser);
                    message.setReceiver(currentUser);
                }
            }
        }
        
        return messagePage;
    }

    @Override
    public boolean markMessagesAsRead(Long senderId, Long receiverId) {
        UpdateWrapper<ChatMessage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("sender_id", senderId)
                    .eq("receiver_id", receiverId)
                    .eq("is_read", false)
                    .set("is_read", true);
        
        return this.update(updateWrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId).eq("is_read", false);
        return (int) this.count(queryWrapper);
    }
} 