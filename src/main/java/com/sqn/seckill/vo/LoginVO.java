package com.sqn.seckill.vo;

import com.sqn.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Title: LoginVO
 * Description:登录参数接收对象
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/7 0007 下午 5:56
 */
@Data
public class LoginVO {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 6,max = 32)
    private String password;
}
