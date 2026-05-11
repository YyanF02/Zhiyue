package com.ZhiyueSecondHand.factory.impl;

import com.ZhiyueSecondHand.agent.Agent;
import com.ZhiyueSecondHand.agent.BuyAgent;
import com.ZhiyueSecondHand.agent.NormalAgent;
import com.ZhiyueSecondHand.agent.RouterAgent;
import com.ZhiyueSecondHand.agent.SellAgent;
import com.ZhiyueSecondHand.enums.ChatModelType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RouterAgentFactory {

    private final NormalAgent normalAgent;
    private final BuyAgent buyAgent;
    private final SellAgent sellAgent;
    private final RouterAgent routerAgent;


    private final Map<ChatModelType, Agent> agentMap = new EnumMap<>(ChatModelType.class);

    @PostConstruct
    private void init() {
        agentMap.put(ChatModelType.ROUTER, routerAgent);
        agentMap.put(ChatModelType.NORMAL, normalAgent);
        agentMap.put(ChatModelType.BUY, buyAgent);
        agentMap.put(ChatModelType.SELL, sellAgent);
    }

    /**
     * 获取对应的agent,如果没有对应的agent，则返回normalAgent
     * @param chatModelType
     * @return
     */
    public Agent getAgent(ChatModelType chatModelType) {
        return agentMap.getOrDefault(chatModelType , normalAgent);
    }
}
