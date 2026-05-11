package com.ZhiyueSecondHand.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.ZhiyueSecondHand.advisor.VectorPreHandleAdvisor;
import com.ZhiyueSecondHand.constants.ChatClientSkills;
import com.ZhiyueSecondHand.repository.MyLettuceRedisChatMemoryRepository;
import com.ZhiyueSecondHand.tool.ToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(
            DashScopeChatModel dashScopeChatModel,
            ChatMemory redisChatMemory,
            ToolService toolService) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem(ChatClientSkills.CHAT_CLIENT_SKILLS_PROMPT)
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .model("qwen3.5-plus")
                                .temperature(0.3)
                                .maxToken(1024)
                                .multiModel(true)
                                .build()
                )
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
//                        MessageChatMemoryAdvisor.builder(redisChatMemory).build()
//                        new SafeGuardAdvisor(List.of("岩弟") , "你不能讨论根岩弟有关的任何事情" , 0),
                )
//                .defaultTools(toolService)
                .build();
    }

        @Bean
    public ChatClient vectorChatClient(
            DashScopeChatModel dashScopeChatModel,
            ChatMemory redisVectorChatMemory,
            VectorStore vectorStore) {
        return ChatClient.builder(dashScopeChatModel)
//                .defaultSystem("You are a helpful assistant.请用温柔的语气和用户对话")
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .model("qwen3.5-plus")
                                .temperature(1.4)
                                .maxToken(1024)
                                .multiModel(true)
                                .build()
                )
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
//                        MessageChatMemoryAdvisor.builder(redisVectorChatMemory).build(),
//                        QuestionAnswerAdvisor 已移除，现在通过 searchByRag 工具进行向量检索
                )
                .build();
    }

    /**
     * 自定义redis存储聊天记录
     * @param redisTemplate
     * @return
     */

    @Bean
    public MyLettuceRedisChatMemoryRepository redisChatMemoryRepository(
            RedisTemplate<String, String> redisTemplate,
            RabbitTemplate rabbitTemplate
    ) {
        return new MyLettuceRedisChatMemoryRepository(redisTemplate, "chat:memory:", rabbitTemplate);
    }



    /**
     * 自定义redis存储路由聊天记录
     * @param redisTemplate
     * @return
     */

    @Bean
    public MyLettuceRedisChatMemoryRepository redisChatRouterMemoryRepository(
            RedisTemplate<String, String> redisTemplate,
            RabbitTemplate rabbitTemplate
    ) {
        return new MyLettuceRedisChatMemoryRepository(redisTemplate, "chat:router:", rabbitTemplate);
    }

    @Bean
    public MyLettuceRedisChatMemoryRepository redisVectorChatMemoryRepository(
            RedisTemplate<String, String> redisTemplate,
            RabbitTemplate rabbitTemplate
    ) {
        return new MyLettuceRedisChatMemoryRepository(redisTemplate, "chat:vector:" , rabbitTemplate);
    }


    /**
     * redis存储聊天记录
     * @param redisChatMemoryRepository
     * @return
     */
    @Bean
    public MessageWindowChatMemory redisChatMemory(MyLettuceRedisChatMemoryRepository redisChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(redisChatMemoryRepository)
                .build();
    }

    /**
     * redis存储聊天记录
     * @param redisChatRouterMemoryRepository
     * @return
     */
    @Bean
    public MessageWindowChatMemory redisRouterChatMemory(MyLettuceRedisChatMemoryRepository redisChatRouterMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(redisChatRouterMemoryRepository)
                .build();
    }

    @Bean
    public MessageWindowChatMemory redisVectorChatMemory(MyLettuceRedisChatMemoryRepository redisVectorChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(redisVectorChatMemoryRepository)
                .build();
    }

    /**
     * springai自带分割文本
     * @return
     */
    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return TokenTextSplitter.builder()
                .withChunkSize(200)
                .withMinChunkSizeChars(80)
                .build();
    }




    /**
     * 向量检索前置处理(自定义拦截)
     * @return
     */
    @Bean
    public VectorPreHandleAdvisor vectorPreHandleAdvisor() {
        return new VectorPreHandleAdvisor();
    }
}
