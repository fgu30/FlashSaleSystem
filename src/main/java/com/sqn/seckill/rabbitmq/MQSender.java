package com.sqn.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Title: MQSender
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/20 0020 下午 8:57
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("queue",msg);
    }

    //fanout
    public void sendFanout(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
    }

    //direct
    public void sendDirectRed(Object msg) {
        log.info("发送red消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
    }

    public void sendDirectGreen(Object msg) {
        log.info("发送green消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
    }

    //topic
    public void sendTopic01(Object msg){
        log.info("发送Topic01消息：" + msg);
        rabbitTemplate.convertAndSend("topicExchange","queue.red.message",msg);
    }

    public void sendTopic02(Object msg){
        log.info("发送Topic02消息：" + msg);
        rabbitTemplate.convertAndSend("topicExchange","message.queue.green.abc",msg);
    }
}
