package com.sqn.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.exception.GlobalException;
import com.sqn.seckill.mapper.UserMapper;
import com.sqn.seckill.service.UserService;
import com.sqn.seckill.utils.CookieUtil;
import com.sqn.seckill.utils.Md5Util;
import com.sqn.seckill.utils.UUIDUtil;
import com.sqn.seckill.utils.ValidatorUtil;
import com.sqn.seckill.vo.LoginVO;
import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 秒杀-秒杀用户 服务实现类
 * </p>
 *
 * @author sqn
 * @since 2021-04-07
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录功能 user存入session中
     *
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin1(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        //根据手机号码获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if (!Md5Util.formPassToDbPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成Cookie
        String ticket = UUIDUtil.uuid();
        request.getSession().setAttribute(ticket, user);
        //将用户信息存入redis中
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    /**
     * 登录功能  user存入redis
     *
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        //根据手机号码获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if (!Md5Util.formPassToDbPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成Cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     *
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    /**
     * 更新密码
     *
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(Md5Util.formPassToDbPass(password,user.getSalt()));
        int result = userMapper.updateById(user);
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:"+userTicket);
            CookieUtil.deleteCookie(request,response,"userTicket");
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }

    /**
     *  测试 JunitGenerator2.0
     */
    @Override
    public void testJunitGenerator(){
        log.debug("debug");
        log.error("error");
        log.trace("trace");
        log.warn("warn");
        log.info("info");
    }

}
