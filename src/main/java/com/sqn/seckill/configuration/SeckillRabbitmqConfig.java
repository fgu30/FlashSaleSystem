package com.sqn.seckill.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title: SeckillRabbitmqConfig
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/27 0027 下午 3:32
 */
@Configuration
public class SeckillRabbitmqConfig {

    /**
     * 队列 queue
     */
    public static final String SECKILL_QUEUE = "seckill.queue";

    /**
     * 交换机 exchange
     */
    public static final String SECKILL_EXCHANGE = "seckillExchange";

    /**
     * 路由键 routingkey #表示0或多个单词，*表示一个单词
     */
    public static final String SECKILL_ROUTINGKEY = "seckill.routingkey";

    @Bean
    public Queue secKillQueue() {
        return new Queue(SECKILL_QUEUE);
    }

    @Bean
    public DirectExchange secKillExchange() {
        return new DirectExchange(SECKILL_EXCHANGE);
    }

    @Bean
    public Binding bindingSecKill() {
        return BindingBuilder.bind(secKillQueue()).to(secKillExchange()).with(SECKILL_ROUTINGKEY);
    }

}
