package com.heima.schedule.test;

import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/19
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private CacheService cacheService;

    @Test
    public void testList() {
        cacheService.lLeftPush("list_001", "hello, redis");
    }
}
