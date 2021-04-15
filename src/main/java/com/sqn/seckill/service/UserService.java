package com.sqn.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sqn.seckill.entity.User;
import com.sqn.seckill.vo.LoginVO;
import com.sqn.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 秒杀-秒杀用户 服务类
 * </p>
 *
 * @author sqn
 * @since 2021-04-07
 */
public interface UserService extends IService<User> {
    /**
     * 登录功能
     *
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    public RespBean doLogin(LoginVO loginVO,HttpServletRequest request, HttpServletResponse response);

}
