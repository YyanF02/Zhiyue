package com.ZhiyueSecondHand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.ZhiyueSecondHand.constants.RedisConstant;
import com.ZhiyueSecondHand.domain.dto.GoodsUploadDto;
import com.ZhiyueSecondHand.domain.pojo.Category;
import com.ZhiyueSecondHand.domain.pojo.Order;
import com.ZhiyueSecondHand.domain.pojo.OrderItem;
import com.ZhiyueSecondHand.domain.query.GoodsQuery;
import com.ZhiyueSecondHand.domain.vo.GoodsSellerStatsVO;
import com.ZhiyueSecondHand.domain.vo.GoodsToVectorVO;
import com.ZhiyueSecondHand.domain.vo.GoodsVO;
import com.ZhiyueSecondHand.enums.VectorOperateType;
import com.ZhiyueSecondHand.exception.BusinessException;
import com.ZhiyueSecondHand.exception.UnauthorizedException;
import com.ZhiyueSecondHand.mapper.OrderMapper;
import com.ZhiyueSecondHand.service.ICategoryService;
import com.ZhiyueSecondHand.service.IGoodsService;
import com.ZhiyueSecondHand.service.IOrderItemService;
import com.ZhiyueSecondHand.util.BeanUtils;
import com.ZhiyueSecondHand.util.CollUtils;
import com.ZhiyueSecondHand.util.PageDTO;
import com.ZhiyueSecondHand.util.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ZhiyueSecondHand.domain.pojo.Goods;
import com.ZhiyueSecondHand.mapper.GoodsMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表（二手书） 服务实现类
 * </p>
 *
 * @author 岩哥
 * @since 2026-04-09
 */
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    private final GoodsMapper goodsMapper;
    private final StringRedisTemplate redisTemplate;
    private final IOrderItemService orderItemService;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ICategoryService categoryService;

    private final Map<Long, String> categoryMap = new HashMap<>();

    @PostConstruct
    public void initCategoryMap() {
        List<Category> list = categoryService.list();
        categoryMap.putAll(
                list.stream().collect(Collectors.toMap(
                        Category::getId,
                        Category::getName,
                        (oldV, newV) -> oldV
                ))
        );
    }

    @Override
    public PageDTO<GoodsVO> getGoodsListWithLikeStatus(GoodsQuery query) {
        if (BooleanUtil.isTrue(query.getIsViewSellerStore()) && query.getSellerId() == null) {
            throw new BusinessException("参数错误");
        }
        // 分页查询货物信息
        Page<Goods> page = query.toMpPageDefaultSortByCreatedTimeDesc();
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getBookName() != null, Goods::getBookName, query.getBookName());
        if (query.getCategoryId() != null) {
            wrapper.eq(Goods::getCategoryId, query.getCategoryId());
        } else if (CollUtils.isNotEmpty(query.getCategoryIds())) {
            wrapper.in(Goods::getCategoryId, query.getCategoryIds());
        }
        wrapper.eq(query.getSellerId() != null, Goods::getUserId, query.getSellerId());
        wrapper.like(query.getAuthor() != null, Goods::getAuthor, query.getAuthor());
        wrapper.eq(query.getDegree() != null, Goods::getDegree, query.getDegree());
        if (BooleanUtil.isTrue(query.getIsViewSellerStore())) {
            //需要指定状态查询,如果不传递默认查1
            wrapper.eq(query.getStatus() != null, Goods::getStatus, query.getStatus());
        } else {
            //不需要指定状态查询,首页内容
            wrapper.eq(Goods::getStatus, 1);
        }
        wrapper.eq(Goods::getIsDeleted, 0);
/*        if (query.getDegree() != null) {
            wrapper.eq(Goods::getDegree, query.getDegree());
        }*/
        Page<Goods> goodsPage = goodsMapper.selectPage(page, wrapper);
        List<Goods> records = goodsPage.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(goodsPage);
        }

        List<GoodsVO> voList = BeanUtils.copyList(records, GoodsVO.class);

        Long userId = UserContext.getUserId();

        if (userId == null) {
            return PageDTO.of(goodsPage, voList);
        }

        String key = RedisConstant.COLLECT_USER_KEY + userId;
        Set<String> collectSet = redisTemplate.opsForSet().members(key);

        if (CollUtils.isEmpty(collectSet)) {
            return PageDTO.of(goodsPage, voList);
        }
        Set<Long> collectGoodsIds = collectSet.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());


        voList.forEach(vo -> vo.setIsLike(collectGoodsIds.contains(vo.getId())));


        return PageDTO.of(goodsPage, voList);
    }

    @Override
    public GoodsVO getGoodsByIdWithLikeStatus(Long id) {
        Goods goods = lambdaQuery()
                .eq(Goods::getId, id)
                .one();
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }

        GoodsVO vo = BeanUtil.copyProperties(goods, GoodsVO.class);
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return vo;
        }
        String key = RedisConstant.COLLECT_USER_KEY + userId;
        Boolean isMember = redisTemplate.opsForSet().isMember(key, id.toString());
        vo.setIsLike(BooleanUtil.isTrue(isMember));
        return vo;
    }

    @Override
    public void updateGoodsStatus(Long id, Integer status) {
        Goods goods = lambdaQuery().eq(Goods::getId, id).one();
        // 1. 商品为空
        Assert.notNull(goods, () -> new BusinessException("商品不存在"));
        // 2. 商品已删除
        Assert.notEquals(goods.getIsDeleted(), 1,
                () -> new BusinessException("商品已删除，无法修改状态"));
        // 3. 商品无库存
        if (goods.getStock() != null && goods.getStock() <= 0) {
            throw new BusinessException("商品已售空，无法修改状态");
        }
        // 4. 状态合法判断
        boolean canUpdate =
                (goods.getStatus() == 2 && status == 1) ||
                        (goods.getStatus() == 1 && status == 2) ||
                        (goods.getStatus() == 1 && status == 3);
        if (!canUpdate) {
            throw new BusinessException("状态修改不合法");
        }
        goods.setStatus(status);
        lambdaUpdate().eq(Goods::getId, id)
                .set(Goods::getStatus, goods.getStatus())
                .update();
        sendToMqSaveOrUpdateVectorStore(goods, false);
    }

    @Override
    public void sendToMqSaveOrUpdateVectorStore(Goods goods, boolean isSave) {
        if (ObjectUtil.notEqual(goods.getStatus(), 1)) {
            log.debug("商品状态不是上架状态，不保存向量存储");
            return;
        }
        GoodsToVectorVO goodsToVectorVO = BeanUtils.copyBean(goods, GoodsToVectorVO.class);
        goodsToVectorVO.setCategoryName(categoryMap.get(goods.getCategoryId()));
//        goodsToVectorVO.setStatus(Objects.requireNonNull(Goods.StatusEnum.getByCode(goods.getStatus())).getDesc());
        goodsToVectorVO.setDegree(Objects.requireNonNull(Goods.DegreeEnum.getByCode(goods.getDegree())).getDesc());
        if (isSave) {
            VectorOperateType.SAVE.sendMessageToMq(JSONUtil.toJsonStr(goodsToVectorVO), rabbitTemplate);
        } else {
            VectorOperateType.UPDATE.sendMessageToMq(JSONUtil.toJsonStr(goodsToVectorVO), rabbitTemplate);

        }
    }

    @Override
    public void deleteGoods(Long id) {
        Optional.ofNullable(id)
                .map(i -> lambdaQuery().eq(Goods::getId, i).one())
                .ifPresentOrElse(g -> {
                            Assert.notEquals(g.getStatus(), 1,
                                    () -> new BusinessException("上架中的商品无法删除，请先下架")
                            );
                            removeById(id);
                            VectorOperateType.DELETE.sendMessageToMq(id.toString(),
                                    rabbitTemplate);
                        },
                        () -> {
                            throw new BusinessException("商品不存在");
                        });
    }

    @Override
    public void uploadGoods(GoodsUploadDto dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录");
        }
        Goods goods = BeanUtils.copyBean(dto, Goods.class);
        goods.setUserId(userId);
        goods.setTotalNumber(dto.getStock());
        save(goods);
        // 保存到向量存储中
        sendToMqSaveOrUpdateVectorStore(goods, true);
    }

    @Override
    public void updateGoods(GoodsUploadDto dto) {
        Long userId = UserContext.getUserId();
        Assert.notNull(userId, () -> new UnauthorizedException("未登录"));
        Assert.notNull(dto.getId(), () -> new BusinessException("商品ID不能为空"));
        Goods exist = lambdaQuery().eq(Goods::getId, dto.getId()).one();
        Assert.notNull(exist, () -> new BusinessException("商品不存在"));
        Assert.notEquals(exist.getUserId(), userId, () -> new BusinessException("上架中的商品无法修改"));
        Goods goods = BeanUtils.copyBean(dto, Goods.class);
        goods.setUserId(exist.getUserId());
        goods.setTotalNumber(exist.getTotalNumber());
        goods.setStatus(exist.getStatus());
        lambdaUpdate().eq(Goods::getId, dto.getId()).update(goods);
        sendToMqSaveOrUpdateVectorStore(goods, false);
    }


    /**
     * 统计商家信息
     * @return
     */
    @Override
    public GoodsSellerStatsVO getGoodsSellerStats(Long sellerId) {
        GoodsSellerStatsVO goodsSellerStatsVO = new GoodsSellerStatsVO(0, 0, 0);
        //根据用户信息查询
        List<Goods> list = lambdaQuery()
                .eq(Goods::getUserId, sellerId)
                .select(Goods::getTotalNumber, Goods::getStock, Goods::getId)
                .list();
        if (CollUtils.isEmpty(list)) {
            return goodsSellerStatsVO;
        }
        Integer totalProducts = list.stream()
                .map(Goods::getTotalNumber)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        Integer totalSales = list.stream()
                .map(Goods::getStock)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        //1商品总数量
        goodsSellerStatsVO.setTotalProducts(totalSales);
        //2商品总销量
        goodsSellerStatsVO.setTotalSales(totalProducts - totalSales);
        //3订单总数量
        Set<Long> goodsIds = list.stream()
                .map(Goods::getId)
                .collect(Collectors.toSet());
        List<OrderItem> orderItems = orderItemService
                .lambdaQuery()
                .in(OrderItem::getGoodsId, goodsIds)
                .select(OrderItem::getOrderId)
                .list();
        if (CollUtils.isEmpty(orderItems)) {
            return goodsSellerStatsVO;
        }
        Set<Long> orderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .collect(Collectors.toSet());
        Long count = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .in(Order::getId, orderIds)
                        .eq(Order::getStatus, 4)
        );
        goodsSellerStatsVO.setTotalOrders(count.intValue());
        return goodsSellerStatsVO;
    }
}
