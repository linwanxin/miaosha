package com.yulenka.miaosha.dao;

import com.yulenka.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 18:44 2019/7/21
 **/
@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") int id);

}
