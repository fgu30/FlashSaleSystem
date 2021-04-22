package com.sqn.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
public class MqReceiver {

    /**
     * 默认
     *
     * @param msg
     */
    @RabbitListener(queues = "queue")
    public void receiver(Object msg) {
        log.info("接收消息：" + msg);
    }

    /**
     * fanout模式
     *
     * @param msg
     */
    @RabbitListener(queues = "queue_fanout01")
    public void receiverFanout01(Object msg) {
        log.info("queue_fanout01接收消息：" + msg);
    }

    /**
     * fanout模式
     *
     * @param msg
     */
    @RabbitListener(queues = "queue_fanout02")
    public void receiverFanout02(Object msg) {
        log.info("queue_fanout02接收消息：" + msg);
    }

    /**
     * direct模式
     *
     * @param msg
     */
    @RabbitListener(queues = "queue_direct01")
    public void receiverDirectRed(Object msg) {
        log.info("queue_direct01接收消息：" + msg);
    }

    /**
     * direct模式
     *
     * @param msg
     */
    @RabbitListener(queues = "queue_direct02")
    public void receiverDirectGreen(Object msg) {
        log.info("queue_direct02接收消息：" + msg);
    }

    /**
     * /topic模式
     *
     * @param msg
     */
    @RabbitListener(queues = "queue_topic01")
    public void receiverTopic01(Object msg) {
        log.info("queue_topic01接收消息：" + msg);
    }

    /**
     * /topic模式
     *
     * @param msg
     */
    @RabbitListener(queues = "queue_topic02")
    public void receiverTopic02(Object msg) {
        log.info("queue_topic02接收消息：" + msg);
    }

}
