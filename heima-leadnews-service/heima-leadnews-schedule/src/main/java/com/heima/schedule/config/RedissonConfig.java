package com.heima.schedule.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/22
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        // 单节点
        config.useSingleServer().setAddress("redis://124.223.106.233:6379");
        return Redisson.create(config);
    }
}
