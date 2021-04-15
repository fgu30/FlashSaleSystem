package com.sqn.seckill.controller;

import com.sqn.seckill.service.UserService;
import com.sqn.seckill.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Title: LoginController
 * Description: 登录
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/7 0007 下午 1:20
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 跳转登录页面
     *
     * @return 登录页面
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    /**
     * 登录功能
     *
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public Object doLogin(@Valid LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        return userService.doLogin(loginVO, request, response);
    }
}
