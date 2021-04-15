package com.sqn.seckill.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Title: SwaggerConfig
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2020/3/20 0020 上午 11:55
 * <p>
 * extends WebMvcConfigurationSupport
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
    /**
     * 配置Swagger的Docket的bean实例
     */
    @Bean
    public Docket docket(Environment environment) {
        //设置要显示的swagger环境
        Profiles profiles = Profiles.of("dev", "test");
        //通过environment. acceptsProfiles判断是否处在自己设定的环境当中
        boolean flag = environment.acceptsProfiles(profiles);
        //System.out.println("是否处在自己设定的环境当中:" + flag);

        /**
         * .enable(true) 是否启动Swagger
         *
         * .apis(RequestHandlerselectors.XXX)
         *   RequestHandlerselectors,配置要扫描接口的方式
         *      basePackage:指定要扫描的包
         *      any():扫描全部
         *      none():不扫描
         *      withclassAnnotation:扫描类上的注解,参数是一个注解的反射对象
         *      withMethodAnnotation:扫描方法上的注解
         */
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                .paths(PathSelectors.any())
                .build()
                .groupName("研发组");
    }

    /**
     * 配置Swagger信息apiInfo
     */
    private ApiInfo apiInfo() {
        //作者信息
        Contact contact = new Contact("sqn", "https://gitee.com/ShiQingning", "1334264094@qq.com");
        return new ApiInfo(
                "Sqn的SwaggerAPI文档",
                "即使再小的帆也能远航",
                "v1.0",
                "urn:tos",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList()
        );
    }

//    /**
//     * 防止@EnableMvc把默认的静态资源路径覆盖了，手动设置的方式
//     * extends WebMvcConfigurationSupport
//     * @param registry
//     */
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//    }

}
