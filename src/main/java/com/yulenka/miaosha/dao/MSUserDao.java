package com.yulenka.miaosha.dao;

import com.yulenka.miaosha.domain.MSUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 19:17 2019/7/22
 **/
@Mapper
public interface MSUserDao {

    @Select("select * from ms_user where id = #{id}")
    public MSUser getUserById(@Param("id") long id);

    @Update("update ms_user set password = #{password} where id = #{id}")
    void updateUser(MSUser updateUser);
}
