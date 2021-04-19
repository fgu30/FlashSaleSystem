package com.sqn.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqn.seckill.entity.Order;
import com.sqn.seckill.entity.SeckillOrder;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.OrderService;
import com.sqn.seckill.service.SeckillOrderService;
import com.sqn.seckill.vo.GoodsVO;
import com.sqn.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class SecKillController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

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
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        //判断用户是否登录
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);

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
}
