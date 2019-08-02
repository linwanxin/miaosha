package com.yulenka.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.redis.AccessKey;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.MSUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 拦截器
 * Author:林万新 lwx
 * Date:  2019/8/1
 * Time: 21:04
 */
@Service
public class AccessInterceptor implements HandlerInterceptor{

    @Autowired
    MSUserService msUserService;
    @Autowired
    RedisService redisService;

    public  boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MSUser msUser = getUser(request,response);
            UserContext.set(msUser);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin =  accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
                if(msUser == null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_"+msUser.getId();
            }
            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(accessKey,key,Integer.class);
            if(count == null){
                redisService.set(accessKey,key,1);
            }else if(count < maxCount){
                redisService.incr(accessKey,key);
            }else {
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String jsonStr = JSON.toJSONString(Result.error(cm));
        out.write(jsonStr.getBytes());
        out.flush();
        out.close();
    }

    private MSUser getUser(HttpServletRequest request, HttpServletResponse response) {

        String paramToken = request.getParameter(msUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request,msUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return msUserService.getUserByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request, String cookiNameToken) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookiNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
