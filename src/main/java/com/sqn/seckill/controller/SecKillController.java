package com.sqn.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.rabbitmq.SecKillRabbitmqSender;
import com.sqn.seckill.rabbitmq.SeckillMessage;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.OrderService;
import com.sqn.seckill.service.SeckillOrderService;
import com.sqn.seckill.vo.GoodsVO;
import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Title: SecKillController
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/16 0016 下午 3:27
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SecKillRabbitmqSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化，把商品库存数量加载到redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> list = goodsService.findGoodsVO();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVO -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVO.getId(), goodsVO.getStockCount());
            localOverMap.put(goodsVO.getId(), false);
        });
    }

    /**
     * 秒杀
     * 10000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVO goods = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存是否足够
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    /**
     * 秒杀 静态化
     * 10000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill3", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill3(User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVO goods = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存是否足够
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.seckill(user, goods);
        return RespBean.success(order);
    }

    /**
     * 秒杀 静态化 解决超卖问题
     * 10000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill4", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill4(User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVO goods = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存是否足够
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断是否重复抢购,从redis中读取
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //秒杀操作：减库存，下订单，写入秒杀订单
        Order order = orderService.seckill(user, goods);

        return RespBean.success(order);
    }

    /**
     * 秒杀 静态化   解决超卖问题    redis预减库存     rabbitmq消息入队
     * 10000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
     * <p>
     * windows优化后QPS:1115.4/sec
     * linux优化后QPS:94.4/sec
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //redis操作
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购,从redis中读取
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //redis预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //入队
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        sender.sendSeckillMessage(seckillMessage);

        return RespBean.success();
    }

    /**
     * 秒杀结果：
     * orderId：成功
     * -1：秒杀失败
     * 0：排队中
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean seckillResult(User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        long result = orderService.getSeckillResult(user.getId(), goodsId);
        return RespBean.success(result);
    }

    /**
     * 秒杀重置：方便测试
     *
     * @return
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public RespBean seckillReset() {
        List<GoodsVO> list = goodsService.findGoodsVO();
        list.forEach(goodsVO -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVO.getId(), 10);
            localOverMap.put(goodsVO.getId(), false);
        });
        Set<String> keys = redisTemplate.keys("order:" + "*");
        redisTemplate.delete(keys);
        redisTemplate.delete("isGoodsOver");
        boolean result = orderService.getSeckillReset();
        if (!result) {
            RespBean.error(RespBeanEnum.ERROR);
        }
        return RespBean.success(result);
    }

}
