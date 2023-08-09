package com.weibo.service;

import com.weibo.dao.FollowMapper;
import com.weibo.dao.UserMapper;
import com.weibo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    /**
     * 获取所有用户列表
     *
     * @return
     */
    public List<User> getUserList() {
        return userMapper.findAll();
    }

    /**
     * 创建用户
     *
     * @param user
     * @return
     */
    public void createUser(User user) {
        // 密码仅存储md5值
        String pwd_md5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        userMapper.addUser(user.getName(), pwd_md5);
    }

    /**
     * 关注用户
     *
     * @param followeeId
     * @param followerId
     * @return
     */
    public void follow(long followeeId, long followerId){
        followMapper.follow(followeeId, followerId);
    }

    /**
     * 取消关注
     *
     * @param followeeId
     * @param followerId
     * @return
     */
    public void unfollow(long followeeId, long followerId){
        followMapper.unfollow(followeeId, followerId);
    }
}
