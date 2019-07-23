package com.yulenka.miaosha.service;

import com.yulenka.miaosha.dao.UserDao;
import com.yulenka.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 18:48 2019/7/21
 **/
@Service
public class UserService {

    @Autowired
    UserDao userDao;
    public User getUserById(int id){
        return userDao.getUserById(id);
    }


}
