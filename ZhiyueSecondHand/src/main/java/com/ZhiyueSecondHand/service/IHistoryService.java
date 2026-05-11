package com.ZhiyueSecondHand.service;

import com.ZhiyueSecondHand.domain.dto.BookHistoryDto;
import com.ZhiyueSecondHand.domain.query.PageQuery;
import com.ZhiyueSecondHand.domain.vo.HistoryGoodsVO;
import com.ZhiyueSecondHand.util.PageDTO;
import com.ZhiyueSecondHand.util.Result;

/**
 * <p>
 * 浏览历史服务类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-11
 */
public interface IHistoryService {

    /**
     * 添加浏览历史
     *
     * @param dto 浏览历史 DTO
     * @return 结果
     */
    Result addHistory(BookHistoryDto dto);


    /**
     * 分页查询浏览历史
     *
     * @return 分页结果
     */
    Result<PageDTO<HistoryGoodsVO>> getHistoryPage(PageQuery pageQuery);
}
