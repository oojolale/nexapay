package com.nexapay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.nexapay.crm.mapper.ContactMapper;

/**
 * MyBatis配置检查类
 * 用于验证MyBatis Plus配置是否正确
 */
@Configuration
@Slf4j
public class MyBatisConfigCheck {

    @Bean
    public CommandLineRunner checkMyBatisConfig(ContactMapper contactMapper) {
        return args -> {
            log.info("=== MyBatis Plus Configuration Check ===");
            log.info("ContactMapper bean is available: {}", contactMapper != null);
            log.info("ContactMapper class: {}", contactMapper.getClass().getName());
            log.info("=== Configuration Check Complete ===");
        };
    }
}