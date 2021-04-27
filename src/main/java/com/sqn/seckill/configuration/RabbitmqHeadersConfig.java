package com.sqn.seckill.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: RabbitmqHeadersConfig
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/21 0021 下午 12:52
 */
@Configuration
public class RabbitmqHeadersConfig {

    /**
     * 队列 queue
     */
    private static final String QUEUE_HEADERS_01 = "queue_headers01";
    private static final String QUEUE_HEADERS_02 = "queue_headers02";

    /**
     * 交换机 exchange
     */
    private static final String HEADERS_EXCHANGE = "headersExchange";

    @Bean
    public Queue queueHeaders01() {
        return new Queue(QUEUE_HEADERS_01);
    }

    @Bean
    public Queue queueHeaders02() {
        return new Queue(QUEUE_HEADERS_02);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Binding bindingHeaders01() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("color", "red");
        map.put("speed", "low");
        return BindingBuilder.bind(queueHeaders01()).to(headersExchange()).whereAny(map).match();
    }

    @Bean
    public Binding bindingHeaders02() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("color", "red");
        map.put("speed", "fast");
        return BindingBuilder.bind(queueHeaders02()).to(headersExchange()).whereAll(map).match();
    }

}
