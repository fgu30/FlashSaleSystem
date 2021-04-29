package com.sqn.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.sqn.seckill.configuration.SeckillRabbitmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Title: SecKillRabbitmqSender
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/27 0027 下午 3:40
 */
@Service
@Slf4j
public class SecKillRabbitmqSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀消息
     *
     * @param seckillMessage
     */
    public void sendSeckillMessage(SeckillMessage seckillMessage) {
        log.info("发送seckill消息：" + seckillMessage);
        //消息队列可以发送 字符串、字节数组、序列化对象
        String msg = JSON.toJSONString(seckillMessage);
        rabbitTemplate.convertAndSend(SeckillRabbitmqConfig.SECKILL_EXCHANGE, SeckillRabbitmqConfig.SECKILL_ROUTINGKEY, msg);
    }

}
