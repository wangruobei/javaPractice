package com.weibo.dao;

import com.weibo.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {

    @Delete("DELETE FROM Follow WHERE userId = #{userId} AND followerId = #{followerId}")
    void unfollow(@Param("userId") long userId, @Param("followerId") long followerId);

    // followerId 用户 关注 userId 用户， 插入Follow表中
    @Insert("INSERT INTO Follow (userId, followerId) values(#{userId}, #{followerId})")
    void follow(@Param("userId") long userId, @Param("followerId") long followerId);

    @Select("SELECT followerId from Follow where userId = #{userId}")
    List<Long> getFollowers(@Param("userId") long userId);
}
