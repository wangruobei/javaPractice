package com.weibo;

import com.weibo.dao.FollowMapper;
import com.weibo.dao.UserMapper;
import com.weibo.entity.User;
import com.weibo.util.SensitiveWordsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@ContextConfiguration(classes = WeiboApplication.class)
@SpringBootTest
public class UserTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    @Test
    public void initEnvironments() {
        for (int i = 0; i < 10; i++) {
            User user = userMapper.findByName("username" + i);
            if(user == null){
                userMapper.addUser("username" + i, "" + i);
            }
        }
    }

    @Test
    public void testFollowAndUnFollow(){
        User user1 = userMapper.findByName("username1");
        User user2 = userMapper.findByName("username2");

        // user2 关注 user1
        followMapper.follow(user1.getId(), user2.getId());

        List<Long> user1Followers = followMapper.getFollowers(user1.getId());
        Assertions.assertTrue(user1Followers.contains(user2.getId()));

        // user2 取关 user1
        followMapper.unfollow(user1.getId(), user2.getId());

        user1Followers = followMapper.getFollowers(user1.getId());
        Assertions.assertFalse(user1Followers.contains(user2.getId()));
    }

}