package com.sqn.seckill.service;

import com.sqn.seckill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.vo.GoodsVO;

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
     * 秒杀
     *
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVO goods);
}