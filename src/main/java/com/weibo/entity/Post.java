package com.weibo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(value="微博")
public class Post {
    private long contentId;
    private long userId;
    private String title;
    private String content;
    private LocalDate create_time;

    public Post(long userId, String title, String content){
        this.userId = userId;
        this.content = content;
        this.title = title;
    }
}
