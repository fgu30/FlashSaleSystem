package com.sqn.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.rabbitmq.SecKillRabbitmqSender;
import com.sqn.seckill.rabbitmq.SeckillMessage;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.SeckillOrderService;
import com.sqn.seckill.service.SeckillService;
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
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
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
    private SeckillService seckillService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SecKillRabbitmqSender sender;

    /**
     * 内存标记秒杀商品库存不足
     */
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
    public String doSecKill2(Model model, User user, Long goodsId) {
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
        Order order = seckillService.seckill(user, goods);
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
    public RespBean doSecKill3(User user, Long goodsId) {
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
        Order order = seckillService.seckill(user, goods);
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
    public RespBean doSecKill4(User user, Long goodsId) {
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
        Order order = seckillService.seckill(user, goods);

        return RespBean.success(order);
    }

    /**
     * 秒杀 静态化   解决超卖问题    redis预减库存 rabbitmq消息入队
     * 10000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
     * <p>
     * windows优化后QPS:1315.4/sec
     * linux优化后QPS:94.4/sec
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(User user, @RequestParam("goodsId") Long goodsId) {
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
     * 秒杀 静态化   解决超卖问题    redis预减库存 rabbitmq消息入队    隐藏秒杀接口地址    数学公式验证码
     * 10000*1*10
     * windows优化前QPS:467.5/sec
     * linux优化前QPS:94.4/sec
     * <p>
     * windows优化后QPS:1315.4/sec
     * linux优化后QPS:94.4/sec
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKillPath(User user, @RequestParam("goodsId") Long goodsId, @PathVariable("path") String path) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //隐藏接口，验证path
        boolean check = seckillService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
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
    public RespBean secKillResult(User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return RespBean.success(result);
    }

    /**
     * 秒杀重置：方便测试
     *
     * @return
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public RespBean secKillReset() {
        List<GoodsVO> list = goodsService.findGoodsVO();
        list.forEach(goodsVO -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVO.getId(), 10);
            localOverMap.put(goodsVO.getId(), false);
        });
        Set<String> orders = redisTemplate.keys("order:" + "*");
        redisTemplate.delete(orders);
        redisTemplate.delete("isGoodsOver");
        boolean result = seckillService.getSeckillReset();
        if (!result) {
            RespBean.error(RespBeanEnum.ERROR);
        }
        return RespBean.success(result);
    }

    /**
     * 隐藏秒杀接口地址，获取秒杀接口地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getSecKillPath(User user, @RequestParam("goodsId") Long goodsId, @RequestParam("verifyCode") int verifyCode) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //检查验证码
        boolean check = seckillService.CheckVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return RespBean.error(RespBeanEnum.VERIFYCODE_ERROR);
        }

        //生成秒杀接口path，存入redis
        String path = seckillService.createSecKillPath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * 验证码
     *
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getVerifyCode(HttpServletResponse response, User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //生成验证码
        BufferedImage image = seckillService.createVerifyCode(user, goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return RespBean.error(RespBeanEnum.VERIFYCODE_GENERATE_ERROR, e.getMessage());
        }
        return RespBean.success();
    }

}
