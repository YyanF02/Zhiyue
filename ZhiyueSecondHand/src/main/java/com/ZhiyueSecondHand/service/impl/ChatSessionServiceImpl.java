package com.ZhiyueSecondHand.service.impl;

import com.ZhiyueSecondHand.domain.pojo.ChatMessage;
import com.ZhiyueSecondHand.domain.pojo.ChatSession;
import com.ZhiyueSecondHand.domain.pojo.Goods;
import com.ZhiyueSecondHand.domain.pojo.User;
import com.ZhiyueSecondHand.domain.query.PageQuery;
import com.ZhiyueSecondHand.domain.vo.ChatSessionVO;
import com.ZhiyueSecondHand.exception.UnauthorizedException;
import com.ZhiyueSecondHand.mapper.ChatSessionMapper;
import com.ZhiyueSecondHand.mapper.GoodsMapper;
import com.ZhiyueSecondHand.mapper.UserMapper;
import com.ZhiyueSecondHand.service.IChatMessageService;
import com.ZhiyueSecondHand.service.IChatSessionService;
import com.ZhiyueSecondHand.util.BeanUtils;
import com.ZhiyueSecondHand.util.CollUtils;
import com.ZhiyueSecondHand.util.PageDTO;
import com.ZhiyueSecondHand.util.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-18
 */
@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements IChatSessionService {

    private final IChatMessageService chatMessageService;
    private final UserMapper userMapper;
    private final GoodsMapper goodsMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void saveAndUpdateChatMessageStatus(ChatSession chatSession, Long fromId, Long toId) {
        save(chatSession);
        chatMessageService.updateStatus(chatSession.getId());
    }

    @Override
    public PageDTO<ChatSessionVO> getChatSessions(PageQuery query) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }

        Page<ChatSession> page = query.toMpPageDefaultSortByCreatedTimeDesc();
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getBuyerId, userId)
                .or()
                .eq(ChatSession::getSellerId, userId);

        Page<ChatSession> resultPage = page(page, wrapper);
        List<ChatSession> records = resultPage.getRecords();
        if (records.isEmpty()) {
            return PageDTO.empty(resultPage);
        }

        // 收集所有用户ID
        Set<Long> userIds = new HashSet<>();
        // 收集所有商品ID
        Set<Long> productIds = new HashSet<>();
        for (ChatSession session : records) {
            if (!session.getBuyerId().equals(userId)) {
                userIds.add(session.getBuyerId());
            }
            if (!session.getSellerId().equals(userId)) {
                userIds.add(session.getSellerId());
            }
            if (session.getProductId() != null) {
                productIds.add(session.getProductId());
            }
        }

        // 批量查询用户信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 批量查询商品信息
        List<Goods> goodsList = new ArrayList<>();
        if (!productIds.isEmpty()) {
            goodsList = goodsMapper.selectBatchIds(productIds);
        }
        Map<Long, Goods> goodsMap = goodsList.stream()
                .collect(Collectors.toMap(Goods::getId, g -> g));

        Map<Long, Long> sessionIdToNotReadCountMap = getMyNotReadSession(userId);


        // 组装VO
        List<ChatSessionVO> voList = records.stream().map(session -> {
            ChatSessionVO vo = BeanUtils.copyBean(session, ChatSessionVO.class);

            // 确定对方用户ID
            Long otherUserId = session.getBuyerId().equals(userId)
                    ? session.getSellerId()
                    : session.getBuyerId();
            vo.setUserId(otherUserId);

            // 获取对方用户信息
            User otherUser = userMap.get(otherUserId);
            if (otherUser != null) {
                vo.setName(otherUser.getNickName());
                vo.setAvator(otherUser.getAvatar());
            } else {
                vo.setName("未知用户");
                vo.setAvator("");
            }

            // 计算未读消息数量
            Long notReadCount = sessionIdToNotReadCountMap
                    .getOrDefault(session.getId(), 0L);

            vo.setNotReadNum(notReadCount.intValue());

            // 设置商品信息
            Goods goods = goodsMap.get(session.getProductId());
            if (goods != null) {
                vo.setProductName(goods.getBookName());
                vo.setProductImage(goods.getBookImg());
                vo.setPrice(goods.getPrice() != null ? goods.getPrice().toString() : "0");
            } else {
                vo.setProductName("");
                vo.setProductImage("");
                vo.setPrice("0");
            }

            return vo;
        }).collect(Collectors.toList());

        return PageDTO.of(resultPage, voList);
    }

    @NotNull
    private Map<Long, Long> getMyNotReadSession(Long userId) {
        //获取所有发给我的未读消息
        List<ChatMessage> chatMessages = chatMessageService.lambdaQuery()
                .eq(ChatMessage::getToId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .select(ChatMessage::getSessionId)
                .list();

        Map<Long, Long> sessionIdToNotReadCountMap = new HashMap<>();
        //获取每个sessionId的未读消息数量
        if (CollUtils.isNotEmpty(chatMessages)) {
            sessionIdToNotReadCountMap = chatMessages.stream()
                    .collect(Collectors.groupingBy(
                            ChatMessage::getSessionId,
                            Collectors.counting()));
        }
        return sessionIdToNotReadCountMap;
    }
}
