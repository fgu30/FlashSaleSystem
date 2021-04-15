package com.sqn.seckill.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 秒杀-秒杀用户
 * </p>
 *
 * @author sqn
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
@ApiModel(value="User对象", description="秒杀-秒杀用户")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID,手机号码")
    @TableId(value = "id")
    private Long id;

    @ApiModelProperty(value = "昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "MD5(MD5(pass明文+固定salt) + salt)")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "盐")
    @TableField("salt")
    private String salt;

    @ApiModelProperty(value = "头像，云存储的ID")
    @TableField("head")
    private String head;

    @ApiModelProperty(value = "注册时间")
    @TableField("register_date")
    private Date registerDate;

    @ApiModelProperty(value = "最后一次登录时间")
    @TableField("last_login_date")
    private Date lastLoginDate;

    @ApiModelProperty(value = "登录次数")
    @TableField("login_count")
    private Integer loginCount;

}
