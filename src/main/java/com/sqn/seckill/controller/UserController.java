package com.sqn.seckill.controller;


import com.sqn.seckill.entity.User;
import com.sqn.seckill.vo.RespBean;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 秒杀-秒杀用户 前端控制器
 * </p>
 *
 * @author sqn
 * @since 2021-04-07
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

}

