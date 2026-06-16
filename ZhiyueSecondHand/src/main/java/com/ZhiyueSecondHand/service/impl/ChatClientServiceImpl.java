package com.ZhiyueSecondHand.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ZhiyueSecondHand.agent.Agent;
import com.ZhiyueSecondHand.agent.impl.RouterAgent;
import com.ZhiyueSecondHand.domain.pojo.ChatList;
import com.ZhiyueSecondHand.enums.ChatModelType;
import com.ZhiyueSecondHand.exception.BusinessException;
import com.ZhiyueSecondHand.exception.UnauthorizedException;
import com.ZhiyueSecondHand.factory.impl.RouterAgentFactory;
import com.ZhiyueSecondHand.service.IChatClientService;
import com.ZhiyueSecondHand.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ZhiyueSecondHand.constants.MqConstant.AiList.AI_LIST_DIRECT_EXCHANGE;
import static com.ZhiyueSecondHand.constants.MqConstant.AiList.AI_LIST_ROUTING_KEY;


@Service
@RequiredArgsConstructor
public class ChatClientServiceImpl implements IChatClientService {
    private final RouterAgentFactory routerAgentFactory;
    private final RabbitTemplate rabbitTemplate;
    private final RouterAgent routerAgent;

    @Override
    public String chat(String prompt, String conversationId, String imageUrl) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        String key = userId + ":" + conversationId;
        String newprompt = StrUtil.isNotBlank(imageUrl)
                ? prompt + ",imageUrl : " + imageUrl
                : prompt;
        String content = routerAgent.contentSimple(newprompt, key);
        ChatModelType chatModelType = ChatModelType.fromCode(content);
        Assert.notNull(chatModelType,
                () -> new BusinessException("chatModelType is null" +
                        content));
        Agent agent = routerAgentFactory.getAgent(chatModelType);
        String retContent = agent.contentSimple(newprompt, key);
        saveConList(prompt, conversationId, userId);
        return retContent;

    }

    private void saveConList(String conName, String conversationId, Long userId) {
        ChatList chatList = new ChatList();
        chatList.setConversationId(conversationId);
        chatList.setUserId(userId);
        chatList.setConversationName(conName);
        chatList.setCreateTime(LocalDateTime.now());
        rabbitTemplate.convertAndSend(AI_LIST_DIRECT_EXCHANGE,
                AI_LIST_ROUTING_KEY,
                chatList);
    }
}

