package com.sqn.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqn.seckill.configuration.SeckillRabbitmqConfig;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.SeckillOrderService;
import com.sqn.seckill.service.SeckillService;
import com.sqn.seckill.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Title: MqReceiver
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/20 0020 下午 9:00
 */
@Service
@Slf4j
public class SecKillRabbitmqReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private SeckillService seckillService;

    /**
     * 接收秒杀消息
     *
     * @param msg
     */
    @RabbitListener(queues = SeckillRabbitmqConfig.SECKILL_QUEUE)
    public void receiverSeckillMessage(String msg) {
        SeckillMessage seckillMessage = JSON.parseObject(msg, SeckillMessage.class);
        log.info(SeckillRabbitmqConfig.SECKILL_QUEUE + "接收消息：" + seckillMessage);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();

        //判断库存是否足够
        GoodsVO goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            return;
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            return;
        }

        //秒杀操作：减库存，下订单，写入秒杀订单
        Order order = seckillService.seckill(user, goods);
    }

}
