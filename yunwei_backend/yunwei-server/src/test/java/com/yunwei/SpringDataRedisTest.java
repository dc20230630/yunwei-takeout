package com.yunwei;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SpringDataRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试 RedisTemplate 提供的各种数据类型操作对象
     */
    @Test
    public void testRedisTemplate() {
        System.out.println(redisTemplate);

        ValueOperations valueOperations = redisTemplate.opsForValue();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        SetOperations setOperations = redisTemplate.opsForSet();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        System.out.println(valueOperations);
        System.out.println(hashOperations);
        System.out.println(listOperations);
        System.out.println(setOperations);
        System.out.println(zSetOperations);
    }

    /**
     * 测试 String 类型
     */
    @Test
    public void testString() {
        redisTemplate.opsForValue().set("city", "北京");

        String city = (String) redisTemplate.opsForValue().get("city");
        System.out.println(city);

        // 保存验证码，并设置 3 分钟过期
        redisTemplate.opsForValue().set(
                "code",
                "1234",
                3,
                TimeUnit.MINUTES
        );

        // key 不存在时才能设置成功
        Boolean firstResult =
                redisTemplate.opsForValue().setIfAbsent("lock", "1");

        Boolean secondResult =
                redisTemplate.opsForValue().setIfAbsent("lock", "2");

        System.out.println("第一次设置结果：" + firstResult);
        System.out.println("第二次设置结果：" + secondResult);
    }

    /**
     * 测试 Hash 类型
     */
    @Test
    public void testHash() {
        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("100", "name", "dazhou");
        hashOperations.put("100", "age", "20");

        String name = (String) hashOperations.get("100", "name");
        System.out.println(name);

        Set keys = hashOperations.keys("100");
        System.out.println(keys);

        List values = hashOperations.values("100");
        System.out.println(values);

        hashOperations.delete("100", "age");
    }

    /**
     * 测试 List 类型
     */
    @Test
    public void testList() {
        ListOperations listOperations = redisTemplate.opsForList();

        // 先删除旧数据，避免重复执行测试时数据不断增加
        redisTemplate.delete("test:list");

        // 从左侧添加元素
        listOperations.leftPush("test:list", "北京");
        listOperations.leftPush("test:list", "上海");

        // 从右侧添加元素
        listOperations.rightPush("test:list", "广州");
        listOperations.rightPush("test:list", "深圳");

        // 查询全部元素，0 到 -1 表示查询全部
        List list = listOperations.range("test:list", 0, -1);
        System.out.println("全部元素：" + list);

        // 获取指定下标的元素
        Object value = listOperations.index("test:list", 0);
        System.out.println("第一个元素：" + value);

        // 获取列表长度
        Long size = listOperations.size("test:list");
        System.out.println("列表长度：" + size);

        // 从左侧弹出一个元素
        Object leftValue = listOperations.leftPop("test:list");
        System.out.println("左侧弹出的元素：" + leftValue);

        // 从右侧弹出一个元素
        Object rightValue = listOperations.rightPop("test:list");
        System.out.println("右侧弹出的元素：" + rightValue);

        List result = listOperations.range("test:list", 0, -1);
        System.out.println("弹出后的列表：" + result);
    }

    /**
     * 测试 Set 类型
     */
    @Test
    public void testSet() {
        SetOperations setOperations = redisTemplate.opsForSet();

        // 先删除旧数据
        redisTemplate.delete("test:set");

        // 添加元素，Set 中的元素不能重复
        setOperations.add(
                "test:set",
                "Java",
                "Spring Boot",
                "Redis",
                "Java"
        );

        // 获取全部元素
        Set members = setOperations.members("test:set");
        System.out.println("全部元素：" + members);

        // 判断元素是否存在
        Boolean result =
                setOperations.isMember("test:set", "Redis");

        System.out.println("是否包含 Redis：" + result);

        // 获取元素数量
        Long size = setOperations.size("test:set");
        System.out.println("元素数量：" + size);

        // 随机获取一个元素，但不会删除
        Object randomMember =
                setOperations.randomMember("test:set");

        System.out.println("随机元素：" + randomMember);

        // 删除指定元素
        setOperations.remove("test:set", "Redis");

        Set newMembers = setOperations.members("test:set");
        System.out.println("删除后的元素：" + newMembers);
    }

    /**
     * 测试两个 Set 的交集、并集和差集
     */
    @Test
    public void testSetOperation() {
        SetOperations setOperations = redisTemplate.opsForSet();

        redisTemplate.delete("test:set:one");
        redisTemplate.delete("test:set:two");

        setOperations.add(
                "test:set:one",
                "Java",
                "Redis",
                "MySQL"
        );

        setOperations.add(
                "test:set:two",
                "Redis",
                "Spring Boot",
                "MySQL"
        );

        // 交集：两个集合中都有的数据
        Set intersect =
                setOperations.intersect(
                        "test:set:one",
                        "test:set:two"
                );

        System.out.println("交集：" + intersect);

        // 并集：合并两个集合并去重
        Set union =
                setOperations.union(
                        "test:set:one",
                        "test:set:two"
                );

        System.out.println("并集：" + union);

        // 差集：第一个集合有，第二个集合没有的数据
        Set difference =
                setOperations.difference(
                        "test:set:one",
                        "test:set:two"
                );

        System.out.println("差集：" + difference);
    }

    /**
     * 测试 ZSet 类型
     */
    @Test
    public void testZSet() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        // 先删除旧数据
        redisTemplate.delete("test:zset");

        // 添加元素和对应分数
        zSetOperations.add("test:zset", "张三", 80);
        zSetOperations.add("test:zset", "李四", 90);
        zSetOperations.add("test:zset", "王五", 70);
        zSetOperations.add("test:zset", "赵六", 95);

        // 按分数从低到高查询
        Set range =
                zSetOperations.range("test:zset", 0, -1);

        System.out.println("从低到高：" + range);

        // 按分数从高到低查询
        Set reverseRange =
                zSetOperations.reverseRange("test:zset", 0, -1);

        System.out.println("从高到低：" + reverseRange);

        // 查询指定成员的分数
        Double score =
                zSetOperations.score("test:zset", "李四");

        System.out.println("李四的分数：" + score);

        // 给指定成员增加分数
        Double newScore =
                zSetOperations.incrementScore(
                        "test:zset",
                        "王五",
                        15
                );

        System.out.println("王五修改后的分数：" + newScore);

        // 获取成员排名，从 0 开始
        Long rank =
                zSetOperations.rank("test:zset", "李四");

        System.out.println("李四从低到高的排名：" + rank);

        // 删除指定成员
        zSetOperations.remove("test:zset", "张三");

        Set newRange =
                zSetOperations.range("test:zset", 0, -1);

        System.out.println("删除后的数据：" + newRange);
    }

    /**
     * 测试 ZSet 查询元素和分数
     */
    @Test
    public void testZSetWithScores() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        redisTemplate.delete("test:ranking");

        zSetOperations.add("test:ranking", "Java", 90);
        zSetOperations.add("test:ranking", "Spring Boot", 85);
        zSetOperations.add("test:ranking", "Redis", 95);
        zSetOperations.add("test:ranking", "MySQL", 80);

        // 同时查询元素和对应的分数
        Set<ZSetOperations.TypedTuple> tuples =
                zSetOperations.reverseRangeWithScores(
                        "test:ranking",
                        0,
                        -1
                );

        if (tuples != null) {
            for (ZSetOperations.TypedTuple tuple : tuples) {
                System.out.println(
                        "成员：" + tuple.getValue()
                                + "，分数：" + tuple.getScore()
                );
            }
        }
    }

    /**
     * 测试 Redis 通用操作
     */
    @Test
    public void testCommon() {
        redisTemplate.opsForValue().set(
                "test:common",
                "测试数据"
        );

        // 判断 key 是否存在
        Boolean exists =
                redisTemplate.hasKey("test:common");

        System.out.println("key 是否存在：" + exists);

        // 设置过期时间
        Boolean expireResult =
                redisTemplate.expire(
                        "test:common",
                        5,
                        TimeUnit.MINUTES
                );

        System.out.println("设置过期时间结果：" + expireResult);

        // 查询剩余过期时间
        Long expire =
                redisTemplate.getExpire(
                        "test:common",
                        TimeUnit.SECONDS
                );

        System.out.println("剩余过期时间：" + expire + " 秒");

        // 获取 key 的数据类型
        DataType type =
                redisTemplate.type("test:common");

        System.out.println("数据类型：" + type);

        // 删除 key
        Boolean deleteResult =
                redisTemplate.delete("test:common");

        System.out.println("删除结果：" + deleteResult);
    }
}