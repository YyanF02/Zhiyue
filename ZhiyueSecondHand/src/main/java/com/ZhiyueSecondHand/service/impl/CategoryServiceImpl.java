package com.ZhiyueSecondHand.service.impl;

import com.ZhiyueSecondHand.domain.dto.CategoryDto;
import com.ZhiyueSecondHand.domain.query.PageQuery;
import com.ZhiyueSecondHand.domain.vo.CategoryVO;
import com.ZhiyueSecondHand.service.ICategoryService;
import com.ZhiyueSecondHand.util.CollUtils;
import com.ZhiyueSecondHand.util.PageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ZhiyueSecondHand.domain.pojo.Category;
import com.ZhiyueSecondHand.mapper.CategoryMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 书籍分类 服务实现类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-09
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public void addCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        categoryMapper.insert(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public void updateCategory(CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        categoryMapper.updateById(category);
    }

    @Override
    public PageDTO<CategoryVO> getCategoryList(PageQuery query) {
        Page<Category> page = lambdaQuery().page(query.toMpPage("sort", true));
        List<Category> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        return PageDTO.of(page, CategoryVO.class);
    }
}
