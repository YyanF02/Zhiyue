package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.dto.SetPasswordDto;
import com.ZhiyueSecondHand.domain.pojo.User;
import com.ZhiyueSecondHand.domain.vo.ChatMessageVO;
import com.ZhiyueSecondHand.domain.vo.UserLoginVO;
import com.ZhiyueSecondHand.domain.vo.UserVO;
import com.ZhiyueSecondHand.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  用户服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-07
 */
public interface IUserService extends IService<User> {


    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @param msg
     * @param type
     * @return
     */
    Result<UserLoginVO> LoginCheck(String phone, String msg, Integer type);

    Result<UserLoginVO> QRCodeLogin(String code, String session);

    void loginCheck(HttpServletResponse response);

    Result<String> check(String loginId);

    Result<UserLoginVO> confirmLogin(String loginId);

    Result forgetPassword(SetPasswordDto dto);

    UserVO getUserInfoById(Long id);


    Result updateUserSimpleById(User user);

    Result updatePassword(SetPasswordDto dto);

    Result<Double> getUserBalance();

    void setUserInfoInSession(ChatMessageVO chatMessageVO);
}
