package com.sqn.seckill.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sqn
 * @date 2020/3/20 0020 上午 11:55
 * @description mybatis-plus 分页插件
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.sqn.seckill.mapper")
public class MybatisPlusConfig {

}
