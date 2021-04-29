package com.sqn.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.exception.GlobalException;
import com.sqn.seckill.mapper.OrderMapper;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.OrderService;
import com.sqn.seckill.vo.GoodsVO;
import com.sqn.seckill.vo.OrderDetailVO;
import com.sqn.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private OrderMapper orderMapper;

    @Autowired
    private GoodsService goodsService;

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
