package com.ZhiyueSecondHand.agent;

import com.ZhiyueSecondHand.constants.ChatClientSkills;
import com.ZhiyueSecondHand.enums.ChatModelType;
import com.ZhiyueSecondHand.tool.ToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SellAgent extends AbstarctAgent {
    private final ToolService toolService;
    private final ChatMemory redisChatMemory;


    @Override
    public ChatModelType getChatModelType() {
        return ChatModelType.SELL;
    }

    @Override
    public String getSystemPrompt() {
        return ChatClientSkills.SELL_PROMPT;
    }


    @Override
    public Object[] tools() {
        return new Object[]{
                toolService
        };
    }


    @Override
    public Map<String, Object> adviorsParams(String conversationId, String requestId) {
        return Map.of(ChatMemory.CONVERSATION_ID, conversationId);
    }

    @Override
    public List<Advisor> getAdvisor() {
        return List.of(MessageChatMemoryAdvisor.builder(redisChatMemory).build());
    }
}
