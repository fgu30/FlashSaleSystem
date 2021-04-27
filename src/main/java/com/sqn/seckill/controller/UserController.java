package com.sqn.seckill.controller;


import com.sqn.seckill.entity.User;
import com.sqn.seckill.rabbitmq.MqSender;
import com.sqn.seckill.service.UserService;
import com.sqn.seckill.utils.CookieUtil;
import com.sqn.seckill.utils.Md5Util;
import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 秒杀-秒杀用户 前端控制器
 * </p>
 *
 * @author sqn
 * @since 2021-04-07
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MqSender mqSender;

    @Autowired
    private UserService userService;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

    @RequestMapping("/toUpdatePassword")
    public String toUpdate() {
        return "userUpdatePassword";
    }

    @RequestMapping("/updatePassword")
    @ResponseBody
    public RespBean updatePassword(Long mobile, String password, HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(userTicket)) {
            User user = userService.getById(mobile);
            if (user != null) {
                user.setPassword(Md5Util.formPassToDbPass(password,user.getSalt()));
                userService.updateById(user);
                return RespBean.success();
            }
            return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        return userService.updatePassword(userTicket, password, request, response);
    }

    /**
     * 测试发送rabbitMQ消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }

    /**
     * 测试发送rabbitMQ消息 fanout
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void fanout(){
        mqSender.sendFanout("fanout");
    }

    /**
     * 测试发送rabbitMQ消息 direct
     */
    @RequestMapping("/mq/directRed")
    @ResponseBody
    public void directRed(){
        mqSender.sendDirectRed("direct : red");
    }

    /**
     * 测试发送rabbitMQ消息 direct
     */
    @RequestMapping("/mq/directGreen")
    @ResponseBody
    public void directGreen(){
        mqSender.sendDirectGreen("direct : green");
    }

    /**
     * 测试发送rabbitMQ消息 topic
     */
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void topic01(){
        mqSender.sendTopic01("Hello,Red!");
    }

    /**
     * 测试发送rabbitMQ消息 topic
     */
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void topic02(){
        mqSender.sendTopic02("Hello,Green!");
    }

    /**
     * 测试发送rabbitMQ消息 headers
     */
    @RequestMapping("/mq/headers01")
    @ResponseBody
    public void headers01(){
        mqSender.sendHeaders01("Hello,Headers01!");
    }

    /**
     * 测试发送rabbitMQ消息 headers
     */
    @RequestMapping("/mq/headers02")
    @ResponseBody
    public void headers02(){
        mqSender.sendHeaders02("Hello,Headers02!");
    }

}

