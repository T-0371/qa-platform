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
            
            // 检查 points_config 表是否存在
            boolean pointsConfigExists = checkTableExists("points_config");
            
            if (!pointsConfigExists) {
                System.out.println("正在创建 points_config 表...");
                
                // 创建 points_config 表
                jdbcTemplate.execute("CREATE TABLE points_config (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    first_chat_cost INT DEFAULT 0,
                    repeat_chat_cost INT DEFAULT 0,
                    message_cost INT DEFAULT 0,
                    question_reward INT DEFAULT 5,
                    answer_reward INT DEFAULT 2,
                    first_contact_reward INT DEFAULT 10,
                    repeat_contact_reward INT DEFAULT 2,
                    reply_reward INT DEFAULT 1,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )");
                
                // 插入默认配置
                jdbcTemplate.execute("INSERT INTO points_config (first_chat_cost, repeat_chat_cost, message_cost, question_reward, answer_reward, first_contact_reward, repeat_contact_reward, reply_reward) VALUES (2, 1, 1, 5, 2, 10, 2, 1)");
                
                System.out.println("points_config 表创建成功！");
            } else {
                System.out.println("points_config 表已存在，跳过创建。");
            }
            
            // 应用启动时清空所有用户的登录令牌（测试期间）
            System.out.println("清空所有用户的登录令牌...");
            jdbcTemplate.execute("UPDATE user SET login_token = NULL, login_time = NULL");
            System.out.println("登录令牌已清空！");
            
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
    
    private boolean checkTableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.TABLES " +
                        "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = DATABASE()";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}