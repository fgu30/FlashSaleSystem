package com.sqn.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.vo.OrderDetailVO;

/**
 * <p>
 * 秒杀-订单 服务类
 * </p>
 *
 * @author sqn
 * @since 2021-04-15
 */
public interface OrderService extends IService<Order> {

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    OrderDetailVO detail(Long orderId);

}
