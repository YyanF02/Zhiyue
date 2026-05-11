package com.ZhiyueSecondHand.agent;

import com.ZhiyueSecondHand.constants.ChatClientSkills;
import com.ZhiyueSecondHand.enums.ChatModelType;
import com.ZhiyueSecondHand.tool.ToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BuyAgent extends AbstarctAgent {

    private final ChatMemory redisChatMemory;
    private final ToolService toolService;
    private final ChatClient vectorChatClient;


    @Override
    public String contentSimple(String prompt, String conversationId) {
        return super.contentSimple(prompt, conversationId);
    }

    @Override
    public ChatModelType getChatModelType() {
        return ChatModelType.BUY;
    }

    @Override
    public String getSystemPrompt() {
        return ChatClientSkills.BUY_PROMPT;
    }

    @Override
    public Object[] tools() {
        return new Object[]{
                toolService
        };
    }

    @Override
    public ChatClient getchatClient() {
        return vectorChatClient;
    }

    @Override
    public Map<String, Object> adviorsParams(String conversationId, String requestId) {
        return Map.of(ChatMemory.CONVERSATION_ID, conversationId);
    }

    @Override
    public List<Advisor> getAdvisor() {
        return List.of(
                MessageChatMemoryAdvisor
                        .builder(redisChatMemory)
                        .build()
        );
    }
}
