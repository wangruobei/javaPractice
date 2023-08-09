package com.weibo.quartz;

import com.weibo.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时每十分钟生成一次每日热点文章id列表，并存在redis中
 *
 */
public class RefreshHotContentsJob implements Job {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void execute(JobExecutionContext context) {
        // 获取定时任务名
        String name = context.getJobDetail().getKey().getName();
        // 创建线程池
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(() -> {

            // 根据当天时间生成redis key值
            String todayKey = RedisKeyUtil.getCurrentDateRedisKey();

            // 在redis中根据zset排序获取当日热门前15条微博的contentId
            Set<Object> res = redisTemplate.opsForZSet().reverseRangeByScore(todayKey, 0, Double.MAX_VALUE, 0, 15);

            // 将排序结果存储在redis中
            String hotContentsKey = RedisKeyUtil.getHotContentsKey();

            if(redisTemplate.hasKey(hotContentsKey)){
                redisTemplate.delete(hotContentsKey);
            }

            for(Object contentId: res) {
                redisTemplate.opsForList().leftPush(hotContentsKey, contentId);
            }

            executorService.shutdown();
        }, 0, TimeUnit.SECONDS);
    }
}
