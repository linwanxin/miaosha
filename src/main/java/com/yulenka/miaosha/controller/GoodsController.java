package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.redis.GoodsKey;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.MSUserService;
import com.yulenka.miaosha.vo.GoodsDetailVo;
import com.yulenka.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 12:50 2019/7/23
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;

    //页面缓存:
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, MSUser msUser){
        model.addAttribute("user", msUser);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        List<GoodsVo> goodsList = goodsService.getAllGoodsVo();
        model.addAttribute("goodsList",goodsList);
        //return "goods_list";
        //手动渲染的目的是为了将缓存好的数据加入缓存
        IWebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    //url缓存  : 多一个参数
    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response,Model model, MSUser msUser, @PathVariable("goodsId") long goodsId){
        model.addAttribute("user",msUser);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        //时间
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();

        int msStatus = 0;//ms状态
        int remainSeconds = 0;//剩余时间
        if(nowTime < startTime){
            msStatus = 0;
            remainSeconds = (int)((startTime-nowTime)/1000);
        }else if(nowTime > endTime){
            msStatus = 2;
            remainSeconds = -1;
        }else{
            msStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", msStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        IWebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;

        //return "goods_detail";
    }


    //url缓存  : 多一个参数
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, MSUser msUser, @PathVariable("goodsId") long goodsId){
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        //时间
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();

        int msStatus = 0;//ms状态
        int remainSeconds = 0;//剩余时间
        if(nowTime < startTime){
            msStatus = 0;
            remainSeconds = (int)((startTime-nowTime)/1000);
        }else if(nowTime > endTime){
            msStatus = 2;
            remainSeconds = -1;
        }else{
            msStatus = 1;
            remainSeconds = 0;
        }
       GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setMiaoshaStatus(msStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setUser(msUser);
        return Result.success(goodsDetailVo);


    }
}
