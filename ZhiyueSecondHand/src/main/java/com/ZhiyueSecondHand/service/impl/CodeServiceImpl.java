package com.ZhiyueSecondHand.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.ZhiyueSecondHand.constants.RedisConstant;
import com.ZhiyueSecondHand.domain.dto.CodeSendDto;
import com.ZhiyueSecondHand.enums.CodeType;
import com.ZhiyueSecondHand.exception.BusinessException;
import com.ZhiyueSecondHand.service.ICodeService;
import com.ZhiyueSecondHand.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 验证码服务实现类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-07
 */
@Service
@RequiredArgsConstructor
public class CodeServiceImpl  implements ICodeService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public Result<String> generateCode(String phone) {
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(
                RedisConstant.LOGIN_CODE_KEY + phone,
                code,
                10,
                TimeUnit.MINUTES
        );
        return Result.success(code);
    }

    @Override
    public Result sendCode(CodeSendDto dto) {
        String phone = dto.getPhone();
        CodeType type = dto.getType();
        
        if (type == null) {
            throw new BusinessException("验证码类型不能为空");
        }
        
        String code = RandomUtil.randomNumbers(6);
        String key = RedisConstant.CODE_SEND_KEY + phone + ":" + type.getCode();
        
        redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);
        
        return Result.success();
    }
}
