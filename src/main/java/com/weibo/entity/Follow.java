package com.weibo.entity;

import lombok.Data;

@Data
public class Follow {
    private long id;
    private long userId;
    private long followerId;

    public Follow(long userId, long followerId){
        this.userId = userId;
        this.followerId = followerId;
    }
}