package com.weibo.controller;
import com.weibo.dao.FollowMapper;
import com.weibo.dao.UserMapper;
import com.weibo.entity.User;
import com.weibo.service.PostService;
import com.weibo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "User")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     *
     * @return
     */
    @GetMapping("/")
    @ApiOperation("获取所有用户列表")
    public List<User> getUserList() {
        return userService.getUserList();
    }

    /**
     * 创建用户
     *
     * @param user
     * @return
     */
    @PostMapping("/")
    @ApiOperation("创建用户")
    public String createUser(@RequestBody User user) {
        userService.createUser(user);
        return "success";
    }

    /**
     * 关注用户
     *
     * @param followeeId
     * @param followerId
     * @return
     */
    @PostMapping("/follow/{followeeId}/{followerId}")
    @ApiOperation("关注用户")
    public String follow(@PathVariable long followeeId, @PathVariable long followerId){
        userService.follow(followeeId, followerId);
        return "success";
    }

    /**
     * 取消关注
     *
     * @param followeeId
     * @param followerId
     * @return
     */
    @DeleteMapping("/follow/{followeeId}/{followerId}")
    @ApiOperation("取消关注")
    public String unfollow(@PathVariable long followeeId, @PathVariable long followerId){
        userService.unfollow(followeeId, followerId);
        return "success";
    }
}
