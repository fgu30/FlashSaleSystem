package com.sqn.seckill.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.management.MXBean;

/**
 * Title: RabbitMQConfig
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/20 0020 下午 8:52
 */
@Configuration
public class RabbitMQFanoutConfig {

    //队列 queue
    private static final String QUEUE_FANOUT_01 = "queue_fanout01";
    private static final String QUEUE_FANOUT_02 = "queue_fanout02";
    //交换机 exchange
    private static final String FANOUT_EXCHANGE = "fanoutExchange";

    @Bean
    public Queue queue(){
        return new Queue("queue",true);
    }

    @Bean
    public Queue queueFanout01(){
        return new Queue(QUEUE_FANOUT_01);
    }

    @Bean
    public Queue queueFanout02(){
        return new Queue(QUEUE_FANOUT_02);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding bindingFanout01(){
        return BindingBuilder.bind(queueFanout01()).to(fanoutExchange());
    }

    @Bean
    public Binding bindingFanout02(){
        return BindingBuilder.bind(queueFanout02()).to(fanoutExchange());
    }

}
