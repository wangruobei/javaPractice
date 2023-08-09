package com.weibo;


import com.weibo.dao.FollowMapper;
import com.weibo.dao.PostMapper;
import com.weibo.dao.UserMapper;
import com.weibo.entity.Post;
import com.weibo.entity.User;
import com.weibo.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = WeiboApplication.class)
@SpringBootTest
public class PostTests {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowMapper followMapper;

    /**
     * 初始化测试环境
     */
    @Test
    public void initEnvironments() {
        // 创建用户
        for (int i = 0; i < 10; i++) {
            User user = userMapper.findByName("username" + i);
            if(user == null){
                userMapper.addUser("username" + i, "" + i);
            }
        }

        ArrayList<User> users = new ArrayList<>();

        // 创建微博
        for (int i = 0; i < 10; i++) {
            User user = userMapper.findByName("username" + i);
            users.add(i, user);
            for(int j = 0; j < 3; j++){

                postMapper.addPost(user.getId(), "" + i, "I am user" + i +
                        ", this is my " + (j+1) + "th weibo. ");
                try {
                    Thread.sleep(1000); // 暂停1秒钟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 创建follow关系
        followMapper.follow(users.get(1).getId(), users.get(0).getId());
        followMapper.follow(users.get(2).getId(), users.get(0).getId());
        followMapper.follow(users.get(3).getId(), users.get(0).getId());
        followMapper.follow(users.get(4).getId(), users.get(0).getId());
    }

    /**
     * 测试Feed功能，测试"检索当前用户新闻推送中最近 5 条推文ID"
     */
    @Test
    public void testFeed(){
        User user0 = userMapper.findByName("username0");
        List<Post> posts =  postService.getFeed(user0.getId(), 5);
        long[] content_ids = new long[5];
        for(int i=0; i < posts.size(); i++){
            content_ids[i] = posts.get(i).getContentId();
        }

        User user4 = userMapper.findByName("username4");
        User user3 = userMapper.findByName("username3");

        List<Long> posts4 = postMapper.getContentIdByUserIdOrderbyCreatetimeWithLimit(user4.getId(), 3);
        List<Long> posts3 = postMapper.getContentIdByUserIdOrderbyCreatetimeWithLimit(user3.getId(), 2);

        Assertions.assertArrayEquals(content_ids, new long[]{posts4.get(0), posts4.get(1)
                , posts4.get(2), posts3.get(0), posts3.get(1)});
    }

    /**
     * 测试getHotContents功能
     * 仅测试获取前3条热点文章
     */
    @Test
    public void testGetHotContents(){
        long testContentId1 = 10000000000L;
        long testContentId2 = 10000000001L;
        long testContentId3 = 10000000002L;
        long testContentId4 = 10000000003L;

        postService.updateViews(testContentId1);
        postService.updateViews(testContentId1);

        postService.updateViews(testContentId2);

        postService.updateViews(testContentId3);
        postService.updateViews(testContentId3);
        postService.updateViews(testContentId3);

        postService.updateViews(testContentId4);
        postService.updateViews(testContentId4);

        List<Object> contentIds = postService.getHotContent(3);
        Assertions.assertArrayEquals(contentIds.toArray()
                , new Object[]{testContentId3, testContentId4, testContentId1});

    }

    /**
     * 测试使用LRU算法的推文更新机制，设置用户的存储限制为 3
     */
    @Test
    public void testVisitContent(){

        long testUserId = 999999999;

        postService.updateVisitHistoryWithLRU(testUserId, 1L, 3);
        postService.updateVisitHistoryWithLRU(testUserId, 2L, 3);
        postService.updateVisitHistoryWithLRU(testUserId, 3L, 3);

        List<Integer> history = postService.getVisitHistory(testUserId);
        //按时间由远到近的访问记录应为[1,2,3]
        Assertions.assertArrayEquals(history.toArray(), new Integer[]{1,2,3});

        postService.updateVisitHistoryWithLRU(testUserId, 1L, 3);
        history = postService.getVisitHistory(testUserId);
        //用户访问content 1，此时content 1成为最近被访问的内容，按时间由远到近的访问记录应为[2,3,1]
        Assertions.assertArrayEquals(history.toArray(), new Integer[]{2,3,1});

        postService.updateVisitHistoryWithLRU(testUserId, 4L, 3);
        history = postService.getVisitHistory(testUserId);
        //用户访问content 4，此时淘汰content2，content 4成为最近被访问的内容，按时间由远到近的访问记录应为[3,1,4]
        Assertions.assertArrayEquals(history.toArray(), new Integer[]{3,1,4});

        postService.updateVisitHistoryWithLRU(testUserId, 1L, 3);
        history = postService.getVisitHistory(testUserId);
        //用户访问content 1，此时content 1成为最近被访问的内容，按时间由远到近的访问记录应为[3,4,1]
        Assertions.assertArrayEquals(history.toArray(), new Integer[]{3,4,1});
    }
}
