package com.sqn.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillGoods;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.exception.GlobalException;
import com.sqn.seckill.mapper.OrderMapper;
import com.sqn.seckill.mapper.SeckillGoodsMapper;
import com.sqn.seckill.mapper.SeckillOrderMapper;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.OrderService;
import com.sqn.seckill.service.SeckillGoodsService;
import com.sqn.seckill.service.SeckillOrderService;
import com.sqn.seckill.vo.GoodsVO;
import com.sqn.seckill.vo.OrderDetailVO;
import com.sqn.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 秒杀-订单 服务实现类
 * </p>
 *
 * @author sqn
 * @since 2021-04-15
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀 出现超卖问题
     *
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order seckill1(User user, GoodsVO goods) {
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.updateById(seckillGoods);

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        return order;
    }

    /**
     * 秒杀 解决超卖问题
     *
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order seckill(User user, GoodsVO goods) {
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=" + "stock_count-1").eq("goods_id", seckillGoods.getId()).gt("stock_count", 0));
        if (!result) {
            //减库存失败，商品卖完了
            setGoodsOver(goods.getId());
            return null;
        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        //秒杀成功存入redis中
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);

        return order;
    }

    /**
     * 获取秒杀结果
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public long getSeckillResult(Long userId, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            //秒杀成功
            return seckillOrder.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                //秒杀失败，库存不足，商品已经卖完
                return -1;
            } else {
                //排队中
                return 0;
            }
        }
    }

    /**
     * 秒杀重置：方便测试
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean getSeckillReset() {
        orderMapper.delete(null);
        seckillOrderMapper.delete(null);
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=" + 10));
        return update;
    }

    /**
     * 设置redis key  isGoodsOver 秒杀商品库存不足
     *
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisTemplate.opsForValue().set("isGoodsOver", goodsId);
    }

    /**
     * 获取redis key  isGoodsOver 秒杀商品库存不足
     *
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(Long goodsId) {
        return redisTemplate.hasKey("isGoodsOver");
    }

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVO detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVO goodsVO = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVO detail = new OrderDetailVO();
        detail.setOrder(order);
        detail.setGoodsVO(goodsVO);
        return detail;
    }

}
