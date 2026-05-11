package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.dto.CartNumDto;
import com.ZhiyueSecondHand.domain.dto.ShoppingCartDto;
import com.ZhiyueSecondHand.domain.pojo.ShoppingCart;
import com.ZhiyueSecondHand.domain.vo.ShoppingCartVO;
import com.ZhiyueSecondHand.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-09
 */
public interface IShoppingCartService extends IService<ShoppingCart> {

    Result addShoppingCart(ShoppingCartDto dto);

    Result<ShoppingCartVO> getUserShoppingCart();

    Result clearShoppingCart();

    Result updateCartNum(CartNumDto dto);

    Result deleteCartById(Long cartId);
}
