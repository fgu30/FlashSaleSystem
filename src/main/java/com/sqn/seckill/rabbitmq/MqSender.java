package com.sqn.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Title: MqSender
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/20 0020 下午 8:57
 */
@Service
@Slf4j
public class MqSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 默认
     *
     * @param msg
     */
    public void send(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("queue", msg);
    }

    /**
     * fanout模式
     *
     * @param msg
     */
    public void sendFanout(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    /**
     * direct模式
     *
     * @param msg
     */
    public void sendDirectRed(Object msg) {
        log.info("发送red消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    /**
     * direct模式
     *
     * @param msg
     */
    public void sendDirectGreen(Object msg) {
        log.info("发送green消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }

    /**
     * /topic模式
     *
     * @param msg
     */
    public void sendTopic01(Object msg) {
        log.info("发送Topic01消息：" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
    }

    /**
     * /topic模式
     *
     * @param msg
     */
    public void sendTopic02(Object msg) {
        log.info("发送Topic02消息：" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "message.queue.green.abc", msg);
    }
}
