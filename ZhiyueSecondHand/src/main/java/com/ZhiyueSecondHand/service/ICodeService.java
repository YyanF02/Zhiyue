package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.dto.CodeSendDto;
import com.ZhiyueSecondHand.util.Result;

/**
 * <p>
 * 验证码服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-07
 */
public interface ICodeService {

    /**
     * 生成短信验证码
     *
     * @param phone 手机号
     * @return 验证码
     */
    Result<String> generateCode(String phone);

    /**
     * 发送验证码
     *
     * @param dto 验证码发送 DTO
     * @return 结果
     */
    Result sendCode(CodeSendDto dto);
}
