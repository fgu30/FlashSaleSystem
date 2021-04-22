package com.sqn.seckill.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title: RabbitmqDirectConfig
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/21 0021 下午 12:05
 */
@Configuration
public class RabbitmqDirectConfig {

    /**
     * 队列 queue
     */
    private static final String QUEUE_DIRECT_01 = "queue_direct01";
    private static final String QUEUE_DIRECT_02 = "queue_direct02";

    /**
     * 交换机 exchange
     */
    private static final String DIRECT_EXCHANGE = "directExchange";

    /**
     * 路由键 routingkey #表示0或多个单词，*表示一个单词
     */
    private static final String QUEUE_RED = "queue.red";
    private static final String QUEUE_GREEN = "queue.green";

    @Bean
    public Queue queueDirect01() {
        return new Queue(QUEUE_DIRECT_01);
    }

    @Bean
    public Queue queueDirect02() {
        return new Queue(QUEUE_DIRECT_02);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding bindingDirectRed() {
        return BindingBuilder.bind(queueDirect01()).to(directExchange()).with(QUEUE_RED);
    }

    @Bean
    public Binding bindingDirectGreen() {
        return BindingBuilder.bind(queueDirect02()).to(directExchange()).with(QUEUE_GREEN);
    }

}
