package com.sqn.seckill.access;

import com.sqn.seckill.entity.User;

/**
 * Title: UserContext
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/30 0030 上午 10:42
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

    
}
