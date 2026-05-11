package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.pojo.ChatMemory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-21
 */
public interface IChatMemoryService extends IService<ChatMemory> {

    /**
     * 获取用户的所有会话列表（最新一条消息）
     */
    List<ChatMemory> getUserConversationList(Long userId);

    /**
     * 获取会话的所有消息（按时间排序）
     */
    List<ChatMemory> getConversationMessages(String conversationId);

    /**
     * 删除会话
     */
    void deleteConversation(String conversationId);

    /**
     * 保存AI回复
     */
    void saveAiMessage(String conversationId, Long userId, String content);

}
