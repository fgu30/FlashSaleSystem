package com.sqn.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillGoods;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.OrderService;
import com.sqn.seckill.service.SeckillGoodsService;
import com.sqn.seckill.service.SeckillOrderService;
import com.sqn.seckill.service.SeckillService;
import com.sqn.seckill.utils.Md5Util;
import com.sqn.seckill.utils.UUIDUtil;
import com.sqn.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Title: SeckillServiceImpl
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/29 0029 下午 6:34
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 秒杀 出现超卖问题
     *
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order seckill1(User user, GoodsVO goods) {
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.updateById(seckillGoods);

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderService.save(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        return order;
    }

    /**
     * 秒杀 解决超卖问题
     *
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order seckill(User user, GoodsVO goods) {
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=" + "stock_count-1").eq("goods_id", seckillGoods.getId()).gt("stock_count", 0));
        if (!result) {
            //减库存失败，商品卖完了
            setGoodsOver(goods.getId());
            return null;
        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderService.save(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        //秒杀成功存入redis中
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);

        return order;
    }

    /**
     * 获取秒杀结果
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public long getSeckillResult(Long userId, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            //秒杀成功
            return seckillOrder.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                //秒杀失败，库存不足，商品已经卖完
                return -1;
            } else {
                //排队中
                return 0;
            }
        }
    }

    /**
     * 秒杀重置：方便测试
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean getSeckillReset() {
        orderService.remove(null);
        seckillOrderService.remove(null);
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=" + 10));
        return update;
    }

    /**
     * 验证秒杀接口path合法性：从redis取出，防止脚本直接刷接口
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathStr = redisTemplate.opsForValue().get("path:" + user.getId() + ":" + goodsId).toString();
        if (!path.equals(pathStr)) {
            return false;
        }
        return true;
    }

    /**
     * 生成秒杀接口path，存入redis
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createSecKillPath(User user, Long goodsId) {
        String path = Md5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("path:" + user.getId() + ":" + goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }

    /**
     * 生成验证码图片
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public BufferedImage createVerifyCode(User user, Long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }

        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        //set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        //draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        //create a random instance to generate the codes
        Random random = new Random();
        //make some confusion
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            //生成点
            g.drawOval(x, y, 0, 0);
        }
        //generate a random code
        String verifyCode = generateVerifyCode(random);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisTemplate.opsForValue().set("verifyCode:" + user.getId() + ":" + goodsId, rnd, 300, TimeUnit.SECONDS);
        //输出图片
        return image;
    }

    /**
     * 检查验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @Override
    public boolean CheckVerifyCode(User user, Long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer code = (Integer) redisTemplate.opsForValue().get("verifyCode:" + user.getId() + ":" + goodsId);
        if (code == null || code - verifyCode != 0) {
            return false;
        }
        redisTemplate.delete("verifyCode:" + user.getId() + ":" + goodsId);
        return true;
    }

    /**
     * 计算验证码结果
     *
     * @param exp
     * @return
     */
    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+', '-', '*'};

    /**
     * 生成验证码
     * + - *
     *
     * @param random
     * @return
     */
    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 设置redis key  isGoodsOver 秒杀商品库存不足
     *
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisTemplate.opsForValue().set("isGoodsOver", goodsId);
    }

    /**
     * 获取redis key  isGoodsOver 秒杀商品库存不足
     *
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(Long goodsId) {
        return redisTemplate.hasKey("isGoodsOver");
    }
}
