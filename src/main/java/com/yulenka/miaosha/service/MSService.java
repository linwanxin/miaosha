package com.yulenka.miaosha.service;

import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.redis.MSKey;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.util.MD5Util;
import com.yulenka.miaosha.util.UUIDUtil;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 15:25 2019/7/24
 **/
@Service
public class MSService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    private static char[] ops = new char[]{'+','-','*'};

    @Transactional
    public OrderInfo miaosha(MSUser user, GoodsVo goods){
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if(success){
            //生成秒杀订单
            return orderService.createOrder(user,goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getResult(long userId, long goodsId) {
        MSOrder order = orderService.getMSOrderByUserIdGoodsId(userId,goodsId);
        if(order != null){
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MSKey.isGoodsOver,""+goodsId);
    }

    public void setGoodsOver(Long goodsId) {
        redisService.set(MSKey.isGoodsOver,""+goodsId,true);
    }
//
//    public boolean checkVerifyCode(MSUser msUser, long goodsId, int verifyCode) {
//        if(msUser == null || goodsId  <= 0){
//            return false;
//        }
//        Integer codeOld = redisService.get(MSKey.getMS)
//    }

    /**
     * 创建秒杀路径
     * @param msUser
     * @param goodsId
     * @return
     */
    public String createMSPath(MSUser msUser, long goodsId) {
        if(msUser == null || goodsId <= 0){
            return  null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MSKey.getMisoshaPath,""+msUser.getId()+"_"+ goodsId,str);
        return str;

    }

    /**
     * 检查路径
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(MSUser user, long goodsId, String path) {
        if(user == null || goodsId <= 0 || path == null){
            return false;
        }
        String oldPath = redisService.get(MSKey.getMisoshaPath,user.getId()+"_"+goodsId,String.class);
        return oldPath.equals(path);
    }

    public BufferedImage createVerifyCode(MSUser msUser, long goodsId) {
        if(msUser == null || goodsId <= 0){
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int result = calc(verifyCode);
        redisService.set(MSKey.getMiaoshaVeriyCode,msUser.getId()+","+goodsId,result);
        //输出图片
        return image;
    }
//
//    public static void main(String[] args) {
//        System.out.println(calc("3+4*7"));
//    }
    public static int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" +num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(MSUser msUser, long goodsId, int verifyCode) {
        if(msUser == null || goodsId <= 0){
            return false;
        }
        //这里有时间限制
        Integer codeOld = redisService.get(MSKey.getMiaoshaVeriyCode,msUser.getId()+","+goodsId,Integer.class);
        if(codeOld== null|| codeOld-verifyCode !=0){
            return false;
        }
        redisService.delete(MSKey.getMiaoshaVeriyCode,msUser.getId()+","+goodsId);
        return true;
    }

}
