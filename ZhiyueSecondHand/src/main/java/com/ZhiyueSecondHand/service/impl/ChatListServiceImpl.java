package com.ZhiyueSecondHand.service.impl;

import com.ZhiyueSecondHand.domain.pojo.ChatList;
import com.ZhiyueSecondHand.mapper.ChatListMapper;
import com.ZhiyueSecondHand.service.IChatListService;
import com.ZhiyueSecondHand.util.UserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * ai聊天列表
 服务实现类
 * </p>
 *
 * @author 岩哥
 * @since 2026-05-06
 */
@Service
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList> implements IChatListService {

    @Override
    public List<ChatList> getUserConversationList() {
        Long userId = UserContext.getUserId();
        if(userId == null){
            return Collections.emptyList();
        }
        List<ChatList> list = lambdaQuery()
                .eq(ChatList::getUserId, userId)
                .orderByDesc(ChatList::getCreateTime)
                .list();
        if(list.isEmpty()){
            return Collections.emptyList();
        }
        return list;
    }
}
