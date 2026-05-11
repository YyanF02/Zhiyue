package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.dto.OrderAIDTO;
import com.ZhiyueSecondHand.domain.dto.OrderDTO;
import com.ZhiyueSecondHand.domain.dto.OrderStatusUpdateDto;
import com.ZhiyueSecondHand.domain.pojo.ChatMemory;
import com.ZhiyueSecondHand.domain.query.OrderPageQuery;
import com.ZhiyueSecondHand.domain.pojo.Order;
import com.ZhiyueSecondHand.domain.vo.OrderDetailVO;
import com.ZhiyueSecondHand.domain.vo.OrderVO;
import com.ZhiyueSecondHand.util.PageDTO;
import com.ZhiyueSecondHand.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-09
 */
public interface IOrderService extends IService<Order> {

    Result<Long> createOrder(OrderDTO dto);

    Result<PageDTO<OrderVO>> pageOrders(OrderPageQuery dto);

    Result<Long> countUnpaidOrders();

    Result<Void> deleteOrder(Long orderId);

    Result<Void> updateOrderStatus(OrderStatusUpdateDto dto);

    Result<OrderDetailVO> getOrderDetail(Long orderId);

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void updateStatus(Order order, OrderStatusUpdateDto dto);

    Result<Void> updateAddress(Long orderId, Long addressId);

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void setDefaultAddressFromAI(OrderAIDTO orderAIDTO);

    @Transactional(rollbackFor = Exception.class)
    void updateAIOrderInfo(String conversationId, Order order);

    @Transactional(rollbackFor = Exception.class)
    void updateAIOrderInfo(ChatMemory chatMemory, Order order);

    @Transactional(rollbackFor = Exception.class)
    void updateAIOrderInfo(String conversationId, OrderDetailVO orderDetailVO);
}
