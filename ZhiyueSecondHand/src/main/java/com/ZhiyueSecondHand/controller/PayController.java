package com.ZhiyueSecondHand.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ZhiyueSecondHand.service.IPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "支付", description = "支付相关接口")
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {



    private final IPayService payService;

    /**
     * 电脑网站支付接口
     */
    @GetMapping("/pc")
    public void pcPay(HttpServletResponse response,
                      Long orderId) {
        payService.pcPay(response, orderId);
    }

    @Operation(summary = "创建pcPay")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        return payService.notify(request);
    }



}
