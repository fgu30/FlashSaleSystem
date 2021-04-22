package com.sqn.seckill.service.impl;

import com.sqn.seckill.SeckillApplicationTests;
import com.sqn.seckill.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * UserServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>04/22/2021</pre>
 */
public class UserServiceImplTest extends SeckillApplicationTests {

    @Autowired
    private UserService userServiceImpl;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response)
     */
    @Test
    public void testDoLogin() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response)
     */
    @Test
    public void testGetUserByCookie() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response)
     */
    @Test
    public void testUpdatePassword() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: testJunitGenerator()
     */
    @Test
    public void testTestJunitGenerator() throws Exception {
        userServiceImpl.testJunitGenerator();
    }


} 
