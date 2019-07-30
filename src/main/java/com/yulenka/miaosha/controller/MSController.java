package com.yulenka.miaosha.controller;

import com.sun.org.apache.bcel.internal.classfile.Code;
import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.rabbitmq.MQSender;
import com.yulenka.miaosha.rabbitmq.MSMessage;
import com.yulenka.miaosha.redis.GoodsKey;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.MSService;
import com.yulenka.miaosha.service.OrderService;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 14:24 2019/7/24
 **/
@Controller
@RequestMapping("/miaosha")
public class MSController implements InitializingBean{

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MSService msService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    //增加内存缓存，减少redis网络访问
    private Map<Long,Boolean> localOverMap = new HashMap<>();
/**
    @PostMapping("/do_miaosha2")
    public String miaosha2(Model model, MSUser user,
                          @RequestParam("goodsId") long goodsId){
        model.addAttribute("user",user);

        if(user == null){
            return "login";
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        //先判断库存
        int stock = goods.getStockCount();
        if(stock <= 0){
            model.addAttribute("errmsg",CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //是否已经秒杀过
        MSOrder msOrder =  orderService.getMSOrderByUserIdGoodsId(user.getId(),goodsId);
        if(msOrder != null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //进行秒杀业务逻辑
        OrderInfo orderInfo = msService.miaosha(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
        
    }
    */
    @PostMapping("/do_miaosha")
    @ResponseBody
    public  Result<Integer> miaosha(MSUser user,
                                     @RequestParam("goodsId") long goodsId){

        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean over  = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getMSGoodsStock,""+ goodsId);
        if(stock <  0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀过了
        MSOrder order = orderService.getMSOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MSMessage message =  new MSMessage();
        message.setGoodsId(goodsId);
        message.setMsUser(user);
        sender.sendMSMessage(message);
        return Result.success(0);
        /*
        //先判断库存 :+上syn会出现11件！！
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //是否已经秒杀过
        MSOrder msOrder =  orderService.getMSOrderByUserIdGoodsId(user.getId(),goodsId);
        if(msOrder != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //进行秒杀业务逻辑
        OrderInfo orderInfo = msService.miaosha(user,goods);
        return Result.success(orderInfo);
        */
    }

    //实现初始化：加载商品库存
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos =  goodsService.getAllGoodsVo();
        if(goodsVos == null){
            return;
        }
        for(GoodsVo goodsVo : goodsVos){
            redisService.set(GoodsKey.getMSGoodsStock,""+ goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    @GetMapping("/result")
    @ResponseBody
    public Result<Long> result(MSUser msUser,@RequestParam("goodsId") long goodsId){
        if(msUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = msService.getResult(msUser.getId(),goodsId);
        return Result.success(result);
    }


}
