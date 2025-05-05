package com.cen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cen.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 聊天消息Mapper接口
 * </p>
 *
 * @author volcano
 * @since 2023-05-20
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
} 