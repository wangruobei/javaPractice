package com.weibo;

import com.weibo.util.SensitiveWordsFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = WeiboApplication.class)
@SpringBootTest
public class SensitiveWordsTests {

    @Autowired
    private SensitiveWordsFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text1 = "这里可以赌博、可以嫖娼、可以吸毒，哈哈！";
        System.out.println(sensitiveFilter.filter(text1));

        String text2 = "这里可以☆赌☆☆博、可以☆☆嫖☆娼☆☆、可以☆吸☆☆毒，哈哈！";
        System.out.println(sensitiveFilter.filter(text2));
    }

}
