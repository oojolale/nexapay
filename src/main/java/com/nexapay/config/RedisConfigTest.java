package com.nexapay.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis配置测试类
 * 用于验证Redis配置是否正确
 */
@Configuration
@Slf4j
public class RedisConfigTest {

    @Bean
    public CommandLineRunner testRedisConfig(RedisTemplate<String, Object> redisTemplate) {
        return args -> {
            log.info("=== Redis Configuration Test ===");
            log.info("RedisTemplate bean is available: {}", redisTemplate != null);
            if (redisTemplate != null) {
                log.info("RedisTemplate class: {}", redisTemplate.getClass().getName());
                log.info("Redis connection factory: {}", redisTemplate.getConnectionFactory());
                
                try {
                    // 简单的ping测试
                    String pingResult = redisTemplate.getConnectionFactory().getConnection().ping();
                    log.info("Redis ping result: {}", pingResult);
                    log.info("✅ Redis connection successful!");
                } catch (Exception e) {
                    log.warn("⚠️ Redis connection failed: {}", e.getMessage());
                    log.info("Note: Make sure Redis server is running on localhost:6379");
                }
            }
            log.info("=== Redis Test Complete ===");
        };
    }
}