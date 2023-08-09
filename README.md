# 项目介绍
这是一个简易的前后端分离的微博后端系统，实现了RESTful接口。
项目基于目前主流 Java Web 技术栈（SpringBoot + MyBatis + MySQL + Redis + Spring Quartz），
实现了让用户发送推文，关注/取消关注其他用户，能够看见关注人（包括自己）的最近15条推文，和“热门微博”等功能。
## 核心技术栈
- Spring
- SpringBoot 2.7.12
- ORM：MyBatis 2.1.3
- 数据库：MySQL 8.0
- 分布式缓存：Redis
- 分布式定时任务：Spring Quartz
- 日志：SLF4J（日志接口） + Logback（日志实现）
- 接口文档：Swagger


# 功能介绍
运行后，访问`http://localhost:8080/swagger-ui/index.html`查看接口文档。

## 发送推文功能
在PostController中实现了postContent方法，接收userId、title、content参数，生成一个微博文章。

```java
public String postContent(@RequestBody Post post) {
    String content = sensitiveFilter.filter(post.getContent());
    postMapper.addPost(post.getUserId(), post.getTitle(), content);
    return "success";
}
```

## 关注与取关
在UserController中实现了follow与unfollow方法，接收followerId与followeeId作为参数。

```java
public String follow(@PathVariable long followeeId, @PathVariable long followerId){
    userService.follow(followeeId, followerId);
    return "success";
}

public String unfollow(@PathVariable long followeeId, @PathVariable long followerId){
    userService.unfollow(followeeId, followerId);
    return "success";
}
```

## 主Feed信息流功能
在PostController中实现了getFeed方法，接收userId参数，生成推文列表，即当前用户新闻推送中最近 15 条推文。
新闻推送中的每一项都是由用户关注的人或者是用户自己发布的推文。
推文按照时间顺序由最近到最远排序。
```java
public List<Post> getFeed(@PathVariable Long id){
    return postService.getFeed(id, 15);
}
```

## 热门微博功能
在PostController中实现了getHotContent方法，检索微博系统中所有微博内容，返回当日访问量最高的前15条微博内容ID。
从缓存中获取当日热点文章
```java
public List<Object> getHotContent(){
    return postService.getHotContentFromCache();
}
```

并实现了updateViews用于更新每篇文章的访问数量。
```java
public String updateViews(@PathVariable Long contentId){
    postService.updateViews(contentId);
    return "success";
}
```
redis缓存中热点文章会使用定时器Quartz每十分钟刷新一次


## 推文更新机制
在PostController中实现了visitContent方法，记录用户访问的历史推文。

```java
public String visitContent(@PathVariable Long userId, @PathVariable Long contentId){
    postService.updateVisitHistoryWithLRU(userId, contentId, 100);
    return "success";
}
```

通过LRU算法淘汰最旧的访问记录
```java
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
```


# 高并发与性能优化
为实现高QPS的场景，我们实现了两点高并发的方案

## 闲时持久化文章访问数据
文章每日访问量数据将会实时存储在redis缓存中，并于每日凌晨四点通过定时器持久化到mysql数据库中。

## 缓存热点文章
热点文章需要对当日所有被访问文章按访问量排序，如果每个请求都排序一次将导致巨大的性能压力。
本项目将使用定时器每隔十分钟将当前redis缓存中所有被访问文章排序并将结果存入redis `hotContents`键中，所有用户对于热点文章请求直接从redis缓存中获取，因此十分钟内仅需排序一次。


# 项目运行与测试

## 运行
运行项目仅需配置mysql与redis
在application.properties中配置mysql与redis的连接，使用java11运行。

数据表结构信息存储在`/sql/*.sql`中。

## 测试
本项目有详细的单元测试

对于用户、文章等功能测试，在UserTest、和PostTests中均有`initEnvironments`方法用于初始化测试代码，向数据库or缓存中插入测试用户、文章、访问数据等。
在进行测试前，务必先插入测试环境。


git使用测试