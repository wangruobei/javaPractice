package com.weibo.config;

import com.weibo.quartz.PersistViewsToDatabaseJob;
import com.weibo.quartz.RefreshHotContentsJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.quartz.TriggerBuilder.newTrigger;

@Component
public class QuartzConfig implements ApplicationRunner {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // jobDetail
        JobDetail jobDetail = JobBuilder
                .newJob(PersistViewsToDatabaseJob.class)
                .withIdentity("jobName", "jobGroup")
                .build();

        // 每天凌晨4点执行定时任务，将redis中微博访问数据持久化到mysql数据库中。
        CronTrigger trigger = (CronTrigger)TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 4 * * ?"))
                .build();

        // jobDetail
        JobDetail jobDetail1 = JobBuilder
                .newJob(RefreshHotContentsJob.class)
                .withIdentity("jobName1", "jobGroup1")
                .build();

        // 每天凌晨4点执行定时任务，将redis中微博访问数据持久化到mysql数据库中。
        CronTrigger trigger1 = (CronTrigger)TriggerBuilder.newTrigger()
                .withIdentity("trigger2", "group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 /1 * * * ?"))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.scheduleJob(jobDetail1, trigger1);
        scheduler.start();
    }
}
