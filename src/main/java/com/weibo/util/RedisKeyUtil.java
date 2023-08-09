package com.weibo.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_POST_VIEWS = "post:views";
    private static final String PREFIX_POST_VISITED = "post:visited";

    /**
     * 获取当前日期，根据当前日期生成redis的key
     * @return
     */
    public static String getCurrentDateRedisKey() {
        return PREFIX_POST_VIEWS + SPLIT + DateTimeFormatter.ofPattern("MM_dd_yyyy")
                .format(LocalDateTime.now());
    }

    /**
     * 获取昨天的日期，根据当前日期生成redis的key
     * @return
     */
    public static String getLastDateRedisKey() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return PREFIX_POST_VIEWS + SPLIT + format.format(cal.getTime());
    }

    public static String getUserVisitedKey(long userId){
        return PREFIX_POST_VISITED + SPLIT + userId;
    }

    public static String getHotContentsKey(){
        return "hotContents";
    }
}