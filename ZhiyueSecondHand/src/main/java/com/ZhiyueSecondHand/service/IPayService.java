package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.pojo.Order;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IPayService {
    void pcPay(HttpServletResponse response, Long orderId);

    @Transactional(rollbackFor = Exception.class)
    boolean checkStock(Order order);

    String notify(HttpServletRequest request);

    @Transactional(rollbackFor = Exception.class)
    void setOrderAndPayStatusToSuccess(String outTradeNo);
}
