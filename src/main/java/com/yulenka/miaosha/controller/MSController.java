package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.access.AccessLimit;
import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.rabbitmq.MQSender;
import com.yulenka.miaosha.rabbitmq.MSMessage;
import com.yulenka.miaosha.redis.AccessKey;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
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
     * 实现初始化：加载商品库存
     * @throws Exception
     */
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
    /**
     * 秒杀接口
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public  Result<Integer> miaosha(MSUser user,
                                     @RequestParam("goodsId") long goodsId,
                                    @PathVariable("path")String path)
    {
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check  = msService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记
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

    /**
     * 获取秒杀结果
     * @param msUser
     * @param goodsId
     * @return
     */
    @GetMapping("/result")
    @ResponseBody
    public Result<Long> result(MSUser msUser,@RequestParam("goodsId") long goodsId){
        if(msUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = msService.getResult(msUser.getId(),goodsId);
        return Result.success(result);
    }

    /**
     * 获取秒杀接口
     * @param msUser
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public Result<String> getMSPath(HttpServletRequest request ,MSUser msUser, @RequestParam("goodsId")long goodsId,
                                    @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode){
        if(msUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean check = msService.checkVerifyCode(msUser,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = msService.createMSPath(msUser,goodsId);
        return Result.success(path);
    }
    /**
     *获取验证码
     */
    @GetMapping("/verifyCode")
    @ResponseBody
    public Result<String> getMSVerifyCode(HttpServletResponse response,MSUser msUser,
                                          @RequestParam("goodsId") long goodsId){
        if(msUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try{
            BufferedImage image = msService.createVerifyCode(msUser,goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
