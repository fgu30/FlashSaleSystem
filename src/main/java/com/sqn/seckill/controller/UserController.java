package com.sqn.seckill.controller;


import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.UserService;
import com.sqn.seckill.utils.CookieUtil;
import com.sqn.seckill.utils.MD5Util;
import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
                user.setPassword(MD5Util.formPassToDBPass(password,user.getSalt()));
                userService.updateById(user);
                return RespBean.success();
            }
            return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        return userService.updatePassword(userTicket, password, request, response);
    }

}

