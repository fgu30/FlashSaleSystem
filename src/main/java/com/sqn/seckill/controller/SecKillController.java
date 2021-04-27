package com.sqn.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
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

import java.util.List;

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

    /**
     * 秒杀
     * 1000*1*10
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
     * 1000*1*10
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
     * 1000*1*10
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
        Order order = orderService.seckill(user, goods);
        return RespBean.success(order);
    }

    /**
     * 秒杀 静态化 解决超卖问题  redis预减库存
     * 1000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
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
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //数据库操作
        GoodsVO goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存是否足够
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //生成订单
        Order order = orderService.seckill(user, goods);

        return RespBean.success(order);
    }


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
        });
    }
}
