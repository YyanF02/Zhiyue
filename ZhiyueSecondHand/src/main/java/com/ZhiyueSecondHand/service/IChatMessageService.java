package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.query.ChatMessageQuery;
import com.ZhiyueSecondHand.domain.vo.ChatMessageVO;
import com.ZhiyueSecondHand.domain.pojo.ChatMessage;
import com.ZhiyueSecondHand.util.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-18
 */
public interface IChatMessageService extends IService<ChatMessage> {


    void updateStatus(Long sessionId);


    void saveAndSetLastMsg(ChatMessage chatMessage);

    /**
     * 分页查询聊天消息
     * @param query 查询参数
     * @return 分页结果
     */
    PageDTO<ChatMessageVO> getChatMessages(ChatMessageQuery query);
}
