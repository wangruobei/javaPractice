package com.weibo.dao;

import com.weibo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM User WHERE name = #{name}")
    User findByName(@Param("name") String name);

    @Select("SELECT * FROM User WHERE id = #{id}")
    User findById(@Param("id") String id);

    @Select("SELECT * FROM User")
    ArrayList<User> findAll();

    @Insert("INSERT INTO User (name, password) values(#{name}, #{password})")
    void addUser(@Param("name") String name, @Param("password") String password);

}
