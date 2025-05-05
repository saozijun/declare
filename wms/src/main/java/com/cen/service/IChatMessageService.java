package com.cen.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cen.entity.ChatMessage;
import com.cen.entity.vo.ChatUserVO;

import java.util.List;

/**
 * <p>
 * 聊天消息服务接口
 * </p>
 *
 * @author volcano
 * @since 2023-05-20
 */
public interface IChatMessageService extends IService<ChatMessage> {

    /**
     * 获取聊天用户列表
     *
     * @param userId 当前用户ID
     * @return 聊天用户列表
     */
    List<ChatUserVO> getChatUsers(Long userId);

    /**
     * 获取与指定用户的聊天记录
     *
     * @param currentUserId 当前用户ID
     * @param targetUserId  目标用户ID
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return 聊天记录分页
     */
    Page<ChatMessage> getChatMessages(Long currentUserId, Long targetUserId, Integer pageNum, Integer pageSize);

    /**
     * 标记消息为已读
     *
     * @param senderId    发送者ID
     * @param receiverId  接收者ID
     * @return 是否成功
     */
    boolean markMessagesAsRead(Long senderId, Long receiverId);

    /**
     * 获取未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadCount(Long userId);
} 