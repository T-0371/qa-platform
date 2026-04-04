package com.example.qa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseMigrationConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        try {
            // 检查 login_token 字段是否存在
            boolean columnExists = checkColumnExists("user", "login_token");
            
            if (!columnExists) {
                System.out.println("正在添加 login_token 和 login_time 字段到 user 表...");
                
                // 添加 login_token 字段
                jdbcTemplate.execute("ALTER TABLE user ADD COLUMN login_token VARCHAR(255) DEFAULT NULL AFTER security_answer");
                
                // 添加 login_time 字段
                jdbcTemplate.execute("ALTER TABLE user ADD COLUMN login_time DATETIME DEFAULT NULL AFTER login_token");
                
                // 创建索引
                jdbcTemplate.execute("CREATE INDEX idx_login_token ON user(login_token)");
                
                System.out.println("数据库字段添加成功！");
            } else {
                System.out.println("login_token 字段已存在，跳过迁移。");
            }
        } catch (Exception e) {
            System.err.println("数据库迁移失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkColumnExists(String tableName, String columnName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                        "WHERE TABLE_NAME = ? AND COLUMN_NAME = ? AND TABLE_SCHEMA = DATABASE()";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName, columnName);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}