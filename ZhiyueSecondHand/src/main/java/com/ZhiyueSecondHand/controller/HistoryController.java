package com.ZhiyueSecondHand.controller;

import com.ZhiyueSecondHand.domain.dto.BookHistoryDto;
import com.ZhiyueSecondHand.domain.query.PageQuery;
import com.ZhiyueSecondHand.domain.vo.HistoryGoodsVO;
import com.ZhiyueSecondHand.service.IHistoryService;
import com.ZhiyueSecondHand.util.PageDTO;
import com.ZhiyueSecondHand.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 浏览历史 前端控制器
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-11
 */
@Slf4j
@Tag(name = "历史", description = "历史相关接口")
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final IHistoryService historyService;

    /**
     * 添加浏览历史
     * @param dto
     * @return
     */
    @PostMapping("/add")
    public Result addHistory(@RequestBody @Valid BookHistoryDto dto) {
        return historyService.addHistory(dto);
    }

    /**
     * 分页查询浏览历史
     * @return
     */
    @Operation(summary = "查询addHistory")
    @GetMapping("/page")
    public Result<PageDTO<HistoryGoodsVO>> getHistoryPage(PageQuery pageQuery) {
        return historyService.getHistoryPage(pageQuery);
    }
}
