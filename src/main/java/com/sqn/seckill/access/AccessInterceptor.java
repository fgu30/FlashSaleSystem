package com.sqn.seckill.access;

import com.alibaba.fastjson.JSON;
import com.sqn.seckill.controller.LoginController;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.UserService;
import com.sqn.seckill.utils.CookieUtil;
import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Title: AccessInterceptor
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/30 0030 上午 10:24
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String methodName = method.getName();
            //System.out.println(methodName);
            if ("toLogin".equals(methodName) || "doLogin".equals(methodName)){
                return true;
            }
            //获取cookie中userTicket
            String ticket = CookieUtil.getCookieValue(request, "userTicket");
            if (StringUtils.isEmpty(ticket)) {
                return false;
            }
            //根据cookie中获取的userTicket从redis中获取用户信息
            User user = userService.getUserByCookie(ticket, request, response);
            // 将用户信息存入ThreadLocal
            UserContext.setUser(user);

            //获取注解信息
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, RespBean.error(RespBeanEnum.SESSION_ERROR));
                    return false;
                }
                key = key + "_" + user.getId();
            } else {
                //do  nothing
            }
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (count == null) {
                redisTemplate.opsForValue().set(key, 1, seconds, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                redisTemplate.opsForValue().increment(key);
            } else {
                render(response, RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHEED));
                return false;
            }
        }

        return true;
    }

    /**
     * 返回页面信息
     *
     * @param response
     * @param respBean
     * @throws Exception
     */
    private void render(HttpServletResponse response, RespBean respBean) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(respBean);
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

}
