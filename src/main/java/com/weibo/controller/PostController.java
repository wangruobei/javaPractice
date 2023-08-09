package com.weibo.controller;

import com.weibo.dao.PostMapper;
import com.weibo.entity.Post;
import com.weibo.service.PostService;
import com.weibo.util.SensitiveWordsFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController()
@Api(tags = "Post")
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private SensitiveWordsFilter sensitiveFilter;

    @Autowired
    private PostService postService;

    /**
     * 通过微博内容ID获取微博内容
     *
     * @param contentId
     * @return
     */
    @GetMapping("/{contentId}")
    @ApiOperation("通过内容ID获取微博内容")
    public Post getContentById(@PathVariable Long contentId) {
        return postMapper.findContentById(contentId);
    }

    /**
     * 发微博
     * 过滤敏感关键字
     *
     * @param post
     * @return
     */
    @PostMapping("/")
    @ApiOperation("发送微博")
    public String postContent(@RequestBody Post post) {
        String content = sensitiveFilter.filter(post.getContent());
        postMapper.addPost(post.getUserId(), post.getTitle(), content);
        return "success";
    }

    /**
     * 更新微博访问数
     *
     * @param contentId
     * @return
     */
    @PutMapping("/views/{contentId}")
    @ApiOperation("更新微博访问数")
    public String updateViews(@PathVariable Long contentId){
        postService.updateViews(contentId);
        return "success";
    }

    /**
     * 从缓存中获取当日热点文章
     * redis缓存中热点文章会使用定时器Quartz每十分钟刷新一次
     *
     * @return
     */
    @GetMapping("/getHotContent")
    @ApiOperation("从缓存中获取当日热点文章")
    public List<Object> getHotContent(){
        return postService.getHotContentFromCache();
    }

    /**
     * 记录用户访问的历史推文
     *
     * @param userId
     * @param contentId
     * @return
     */
    @GetMapping("/visitContent/{userId}/{contentId}")
    @ApiOperation("记录用户访问的历史推文")
    public String visitContent(@PathVariable Long userId, @PathVariable Long contentId){
        postService.updateVisitHistoryWithLRU(userId, contentId, 100);
        return "success";
    }

    /**
     * 检索当前用户新闻推送中最近 15 条推文的 ID
     *
     * @param id
     * @return
     */
    @GetMapping("/feed/{id}")
    @ApiOperation("检索当前用户新闻推送中最近 15 条推文的 ID")
    public List<Post> getFeed(@PathVariable Long id){
        return postService.getFeed(id, 15);
    }

}
