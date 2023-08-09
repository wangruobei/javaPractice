package com.weibo.dao;

import com.weibo.entity.Post;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PostMapper {

    @Select("SELECT * FROM Post WHERE contentId = #{id}")
    Post findContentById(@Param("id") Long id);

    @Insert("INSERT INTO Post (userId, title, content) values(#{userId}, #{title}, #{content})")
    void addPost(@Param("userId") long id, @Param("title") String title, @Param("content") String content);

    //通过SQL语句获得当前用户新闻推送中最近15条推文的ID列表，按照推文创建时间由近到远排序
    @Select("select * from Post where userId in (select userId from Follow where followerId=#{userId} " +
            "union select #{userId}) order by create_time DESC limit #{limit}")
    List<Post> feed(@Param("userId") long userId, @Param("limit") long limit);

    @Update("UPDATE Post set views = views + #{views} where contentId=#{contentId}")
    void updateViews(@Param("views") long views, @Param("contentId") long contentId);

    @Select("select contentId from Post where userId = #{userId} order by create_time DESC limit #{limit}")
    List<Long> getContentIdByUserIdOrderbyCreatetimeWithLimit(@Param("userId") long userId, @Param("limit") long limit);
}
