package com.sqn.seckill.controller;


import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.OrderService;
import com.sqn.seckill.vo.OrderDetailVO;
import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 秒杀-订单 前端控制器
 * </p>
 *
 * @author sqn
 * @since 2021-04-15
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单详情
     *
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("detail")
    @ResponseBody
    public RespBean detail(User user,Long orderId){
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVO detail = orderService.detail(orderId);

        return RespBean.success(detail);
    }


}

