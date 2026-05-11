package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.pojo.ChatList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * ai聊天列表
 服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-05-06
 */
public interface IChatListService extends IService<ChatList> {

    List<ChatList> getUserConversationList();
}
