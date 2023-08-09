package com.weibo.quartz;

import com.weibo.dao.PostMapper;
import com.weibo.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PersistViewsToDatabaseJob implements Job {
    private Integer runSecond;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setRunSecond(Integer runSecond) {
        this.runSecond = runSecond;
    }

    @Override
    public void execute(JobExecutionContext context) {
        // 获取定时任务名
        String name = context.getJobDetail().getKey().getName();
        // 创建线程池
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(() -> {

            // 将前日redis中微博文访问量持久化数据库中
            String lastDayKey = RedisKeyUtil.getLastDateRedisKey();
            Set<ZSetOperations.TypedTuple<Object>> views =  redisTemplate.opsForZSet().rangeWithScores(lastDayKey, 0, -1);
            for(ZSetOperations.TypedTuple<Object> tuple : views){
                long contentId = (long) tuple.getValue();
                double view = tuple.getScore();
                postMapper.updateViews((long)view, contentId);
            }

            executorService.shutdown();
        }, 0, TimeUnit.SECONDS);
    }
}
