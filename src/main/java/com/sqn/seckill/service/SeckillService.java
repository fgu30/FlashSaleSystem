package com.sqn.seckill.service;

import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.vo.GoodsVO;

import java.awt.image.BufferedImage;

/**
 * Title: SeckillService
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/29 0029 下午 6:33
 */
public interface SeckillService {

    /**
     * 秒杀 出现超卖问题
     *
     * @param user
     * @param goods
     * @return
     */
    Order seckill1(User user, GoodsVO goods);

    /**
     * 秒杀
     *
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVO goods);

    /**
     * 获取秒杀结果
     *
     * @param userId
     * @param goodsId
     * @return
     */
    long getSeckillResult(Long userId, Long goodsId);

    /**
     * 秒杀重置：方便测试
     *
     * @return
     */
    boolean getSeckillReset();

    /**
     * 验证秒杀接口path合法性：从redis取出，防止脚本直接刷接口
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);

    /**
     * 生成秒杀接口path，存入redis
     *
     * @param user
     * @param goodsId
     * @return
     */
    String createSecKillPath(User user, Long goodsId);

    /**
     * 生成验证码
     *
     * @param user
     * @param goodsId
     * @return
     */
    BufferedImage createVerifyCode(User user, Long goodsId);

    /**
     * 检查验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    boolean checkVerifyCode(User user, Long goodsId, int verifyCode);
}
