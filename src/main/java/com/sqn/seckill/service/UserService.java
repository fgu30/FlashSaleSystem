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
    public RespBean doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     *
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     *
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);

    /**
     * 测试 JunitGenerator2.0
     */
    void testJunitGenerator();
}
