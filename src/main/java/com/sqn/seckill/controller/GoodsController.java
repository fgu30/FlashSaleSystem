package com.sqn.seckill.controller;

import com.sqn.seckill.entity.User;
import com.sqn.seckill.service.GoodsService;
import com.sqn.seckill.service.UserService;
import com.sqn.seckill.vo.GoodsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Title: GoodsController
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/14 0014 上午 11:25
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转到商品列表页面
     * user存入session中  后改为Spring Session----redis
     *
     * @param session
     * @param model
     * @param ticket
     * @return
     */
//    @RequestMapping("/toList")
//    public String toList(HttpSession session, Model model, @CookieValue("userTicket") String ticket){
//        if(StringUtils.isEmpty(ticket)){
//            return "login";
//        }
//        User user = (User) session.getAttribute(ticket);
//        if (null == user){
//            return "login";
//        }
//        model.addAttribute("user",user);
//        return "goodsList";
//    }

    /**
     * 跳转到商品列表页面
     * cookie存放userTicket，user存入redis中
     *
     * @param request
     * @param response
     * @param model
     * @param ticket
     * @return
     */
//    @RequestMapping("/toList")
//    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String ticket) {
//        if (StringUtils.isEmpty(ticket)) {
//            return "login";
//        }
//        User user = userService.getUserByCookie(ticket, request, response);
//        if (null == user) {
//            return "login";
//        }
//        model.addAttribute("user", user);
//        return "goodsList";
//    }

    /**
     * 跳转到商品列表页面
     * user参数解析
     *
     * @param model
     * @param user
     * @return
     */
//    @RequestMapping("/toList")
//    public String toList(Model model, User user) {
//        model.addAttribute("user", user);
//        model.addAttribute("goodsList", goodsService.findGoodsVO());
//        return "goodsList";
//    }

    /**
     * 跳转到商品列表页面 （页面缓存 存入Redis）
     *
     * @param model
     * @param user
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        // Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVO());
        // 如果为空,手动渲染，存入Redis并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);

        }
        return html;
    }

    /**
     * 跳转到商品详情页面
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
//    @RequestMapping("/toDetail/{goodsId}")
//    public String toDetail(Model model, User user, @PathVariable Long goodsId) {
//        model.addAttribute("user", user);
//        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
//        model.addAttribute("goods", goodsVO);
//
//        Date startDate = goodsVO.getStartDate();
//        Date endDate = goodsVO.getEndDate();
//        Date nowDate = new Date();
//
//        //秒杀状态
//        int secKillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//        //秒杀结束倒计时
//        int endSeconds = endSeconds = (int) ((endDate.getTime() - nowDate.getTime()) / 1000);
//        //秒杀还未开始
//        if (nowDate.before(startDate)) {
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            //秒杀已结束
//            secKillStatus = 2;
//            remainSeconds = -1;
//            endSeconds = 0;
//        } else {
//            //秒杀进行中
//            secKillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("endSeconds", endSeconds);
//        return "goodsDetail";
//    }

    /**
     * 跳转到商品详情页面 （页面缓存 存入Redis）
     *
     * @param model
     * @param user
     * @param goodsId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        // Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail" + goodsId);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        model.addAttribute("goods", goodsVO);

        Date startDate = goodsVO.getStartDate();
        Date endDate = goodsVO.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀结束倒计时
        int endSeconds = endSeconds = (int) ((endDate.getTime() - nowDate.getTime()) / 1000);
        //秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;
            endSeconds = 0;
        } else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("endSeconds", endSeconds);
        // 如果为空,手动渲染，存入Redis并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsDetail"+goodsId, html, 60, TimeUnit.SECONDS);

        }
        return html;
    }
}
