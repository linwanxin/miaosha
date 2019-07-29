package com.yulenka.miaosha.service;

import com.yulenka.miaosha.dao.MSUserDao;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.User;
import com.yulenka.miaosha.exception.GlobalException;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.redis.UserKey;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.util.MD5Util;
import com.yulenka.miaosha.util.UUIDUtil;
import com.yulenka.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 23:18 2019/7/22
 **/
@Service
public class MSUserService {

    @Autowired
    MSUserDao msUserDao;
    @Autowired
    RedisService redisService;

    public static final String COOKI_NAME_TOKEN = "token";

    public String login(HttpServletResponse response,LoginVo loginVo){
        if(loginVo == null){
           throw  new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //验证手机号
        MSUser msUser = getUserById(Long.parseLong(mobile));
        if(msUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = msUser.getPassword();
        String saltDB = msUser.getSalt();
        String calcPass =  MD5Util.formPassToDbPass(password,saltDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,msUser);

        return token;
    }

    /**
     *  其他页面根据token获取到User登陆信息
     */
    public MSUser getUserByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MSUser msUser =  redisService.get(UserKey.token,token,MSUser.class);
        //延长有效期:这里不是采用增加时间方式，因为cookie+，redis也要+；而 是采用重新生成cookie的方式，但是token不变，还是原来的
        if(msUser != null){
            addCookie(response,token,msUser);
        }
        return msUser;

    }

    //存放在redis中，和redis的过期时间一致
    private void addCookie(HttpServletResponse response, String token, MSUser msUser) {
        redisService.set(UserKey.token,token,msUser);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(UserKey.token.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    //加入对象缓存
    public MSUser getUserById(long id){
        //取缓存
        MSUser msUser = redisService.get(UserKey.getById,""+id,MSUser.class);
        if(msUser!= null){
            return msUser;
        }
        //取数据库
        msUser =  msUserDao.getUserById(id);
        if(msUser != null){
            redisService.set(UserKey.getById,""+id,MSUser.class);
        }
        return msUser;
    }

    //其他渠道修改对象则删除原缓存数据;注意顺序！
    public boolean updatePassword(String token,long id,String formPass){
        //取出对象
        MSUser msUser =  getUserById(id);
        if(msUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MSUser updateUser = new MSUser();
        updateUser.setPassword(MD5Util.formPassToDbPass(formPass,msUser.getSalt()));
        updateUser.setId(id);
        msUserDao.updateUser(updateUser);
        //处理缓存
        redisService.delete(UserKey.getById,"" + id);
        msUser.setPassword(updateUser.getPassword());
        redisService.set(UserKey.token,token,msUser);
        return true;
    }
}
