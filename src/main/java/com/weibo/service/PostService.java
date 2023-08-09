package com.weibo.service;

import com.weibo.dao.PostMapper;
import com.weibo.entity.Post;
import com.weibo.util.RedisKeyUtil;
import com.weibo.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SensitiveWordsFilter sensitiveFilter;


    /**
     * 更新微博访问数
     * 当日访问数存储在redis中
     * 使用Spring Quartz定时器，每日4时持久化前一日的访问数到MySQL数据库中
     *
     * @param contentId
     */
    public void updateViews(long contentId){
        String todayKey = RedisKeyUtil.getCurrentDateRedisKey();
        redisTemplate.opsForZSet().incrementScore(todayKey, contentId, 1);
    }

    /**
     * 在缓存中获取热点文章列表
     *
     * @return
     */
    public List<Object> getHotContentFromCache(){
        String hotContentsKey = RedisKeyUtil.getHotContentsKey();

        // 在redis缓存中直接获取热点文章列表
        List<Object> res = redisTemplate.opsForList().range(hotContentsKey, 0, -1);
        return new ArrayList<>(res);
    }

    /**
     * 检索微博系统中所有微博内容，返回当日访问量最高的前count条微博内容ID
     *
     * @return
     */
    public List<Object> getHotContent(int count){
        // 根据当天时间生成redis key值
        String todayKey = RedisKeyUtil.getCurrentDateRedisKey();

        // 在redis中根据zset排序获取当日热门前15条微博的contentId
        Set<Object> res = redisTemplate.opsForZSet().reverseRangeByScore(todayKey, 0, Double.MAX_VALUE, 0, count);
        return new ArrayList<>(res);
    }

    /**
     * 记录用户访问的历史推文
     * 历史推文个数最大为MAX_VISIT_SIZE, 采用LRU算法淘汰
     *
     * @param userId
     * @param contentId
     */
    public void updateVisitHistoryWithLRU(Long userId, Long contentId, int visit_size){
        String userVisKey = RedisKeyUtil.getUserVisitedKey(userId);

        // 如果该用户未访问任何文章，在redis中新建key并插入当前contentId
        if(!redisTemplate.hasKey(userVisKey)){
            redisTemplate.opsForList().rightPush(userVisKey, contentId);
            return;
        }

        // 如果当前用户访问记录中已存在该文章，则删除以前对该文章的访问记录
        if(redisTemplate.opsForList().indexOf(userVisKey, contentId) != null){
            redisTemplate.opsForList().remove(userVisKey, 1, contentId);
        }

        // 将对该文章的访问记录插入为最新访问记录
        redisTemplate.opsForList().rightPush(userVisKey, contentId);

        // 如果访问记录缓存页大小超过visit_size（默认为100），则删除最旧一条记录
        if(redisTemplate.opsForList().size(userVisKey) > visit_size){
            redisTemplate.opsForList().leftPop(userVisKey);
        }
    }

    public List<Integer> getVisitHistory(Long userId){
        String userVisKey = RedisKeyUtil.getUserVisitedKey(userId);

        if(!redisTemplate.hasKey(userVisKey)){
            return new ArrayList<>();
        }

        // 将对该文章的访问记录插入为最新访问记录
        List<Object> contentIds = redisTemplate.opsForList().range(userVisKey, 0, -1);

        return contentIds.stream()
                .map(obj -> (Integer) obj)
                .collect(Collectors.toList());
    }

    /**
     * 检索当前用户新闻推送中最近 15 条推文的 id
     * 推文为用户和起关注用户推文列表，按照时间排序
     * 通过sql语句实现
     *
     * @param id
     * @return
     */
    public List<Post> getFeed(Long id, int limit){
        return postMapper.feed(id, limit);
    }





}
