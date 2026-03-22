package com.nexapay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * NexaPay SaaS Platform 启动类
 * 企业级多租户金融运营平台
 */
@SpringBootApplication
@MapperScan({"com.nexapay.crm.mapper", "com.nexapay.erp.mapper", "com.nexapay.payment.mapper", "com.nexapay.risk.mapper", "com.nexapay.scheduler.mapper", "com.nexapay.system.mapper"})
@EnableTransactionManagement
public class NexaPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexaPayApplication.class, args);
    }
}
