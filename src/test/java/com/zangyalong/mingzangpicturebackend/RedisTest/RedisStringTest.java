//package com.zangyalong.mingzangpicturebackend.RedisTest;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Properties;
//
//import static org.springframework.test.util.AssertionErrors.*;
//
//@SpringBootTest
//@ActiveProfiles("local")
//public class RedisStringTest {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    public void testRedisStringOperations(){
//        // 获取操作对象
//        ValueOperations<String, String> valueOps  = stringRedisTemplate.opsForValue();
//
//        // key 和 value
//        String key = "testKey";
//        String value = "testValue";
//
//        // 1.测试新增或者更新操作
//        valueOps.set(key, value);
//        String storedValue = valueOps.get(key);
//        assertEquals( "存储的值与预期不一致",value, storedValue);
//
//        // 2.测试修改操作
//        String upDatedValue = "upDatedValue";
//        valueOps.set(key, upDatedValue);
//        storedValue = valueOps.get(key);
//        assertEquals("更新后的值与预期的不一致",upDatedValue, storedValue);
//
//        // 3.测试查询操作
//        storedValue = valueOps.get(key);
//        assertNotNull("查询的值非空",storedValue);
//        System.out.println(key + " " +storedValue);
//
////        // 4.测试删除操作
////        stringRedisTemplate.delete(key);
////        storedValue = valueOps.get(key);
////        assertNotNull(storedValue, "删除后的值不为空");
//    }
//}
