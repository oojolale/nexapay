package com.nexapay.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * 数据库初始化器
 * 检查数据库连接并创建必要的表
 */
@Component
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("=== Database Initialization Check ===");
        
        try {
            // 检查数据库连接
            testDatabaseConnection();
            
            // 检查表是否存在
            checkAndCreateTables();
            
            log.info("✅ Database initialization check completed successfully!");
        } catch (Exception e) {
            log.error("❌ Database initialization failed: {}", e.getMessage());
            log.info("Please ensure:");
            log.info("1. PostgreSQL is running on localhost:5432");
            log.info("2. Database 'nexapay' exists");
            log.info("3. Username 'postgres' and password 'postgres' are correct");
            log.info("4. Or update application.yml with your database credentials");
        }
    }

    private void testDatabaseConnection() throws SQLException {
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            log.info("✅ Database connection successful!");
            log.info("Database: {}", conn.getMetaData().getDatabaseProductName());
            log.info("Version: {}", conn.getMetaData().getDatabaseProductVersion());
        }
    }

    private void checkAndCreateTables() {
        // 检查tenant表是否存在
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tenant", Integer.class);
            log.info("✅ Table 'tenant' exists");
        } catch (Exception e) {
            log.warn("Table 'tenant' does not exist. Attempting to create tables...");
            createTablesFromSqlFile();
        }
        
        // 检查其他关键表
        String[] tables = {"user", "orders", "inventory", "contact", "transaction", "risk_rule", "scheduled_task"};
        for (String table : tables) {
            try {
                jdbcTemplate.execute("SELECT 1 FROM " + table + " LIMIT 1");
                log.info("✅ Table '{}' exists", table);
            } catch (Exception e) {
                log.warn("Table '{}' does not exist", table);
            }
        }
    }

    private void createTablesFromSqlFile() {
        try {
            log.info("Reading SQL script...");
            ClassPathResource resource = new ClassPathResource("sql/nexapay.sql");
            String sql = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
            
            // 分割SQL语句并执行
            String[] statements = sql.split(";");
            int createdCount = 0;
            
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmed + ";");
                        createdCount++;
                    } catch (Exception e) {
                        log.warn("Failed to execute SQL: {}", trimmed.substring(0, Math.min(50, trimmed.length())));
                    }
                }
            }
            
            log.info("Created {} tables/statements", createdCount);
        } catch (Exception e) {
            log.error("Failed to create tables from SQL file: {}", e.getMessage());
            log.info("You can manually execute the SQL script at: sql/nexapay.sql");
        }
    }
}