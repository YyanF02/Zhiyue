package com.ZhiyueSecondHand.controller;

import com.ZhiyueSecondHand.domain.dto.CollectDto;
import com.ZhiyueSecondHand.domain.vo.GoodsVO;
import com.ZhiyueSecondHand.service.ICollectService;
import com.ZhiyueSecondHand.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 用户收藏表 前端控制器
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-09
 */
@Tag(name = "收藏", description = "收藏相关接口")
@RestController
@RequestMapping("/collect")
@RequiredArgsConstructor
public class CollectController {

    private final ICollectService collectService;

    @PostMapping
    public Result setCollectStatus(@Valid @RequestBody CollectDto dto) {
        collectService.addCollect(dto);
        return Result.success();
    }


    @Operation(summary = "查询setCollectStatus")
    @GetMapping
    public Result<List<GoodsVO>> getCollectList() {
        List<GoodsVO> result = collectService.getCollectList();
        return Result.success(result);
    }
}
