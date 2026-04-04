package com.example.qa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class DatabaseMigrationConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    @PostConstruct
    public void migrate() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }

        try {
            boolean columnExists = checkColumnExists("user", "login_token");

            if (!columnExists) {
                System.out.println("正在添加 login_token 和 login_time 字段到 user 表...");

                jdbcTemplate.execute("ALTER TABLE user ADD COLUMN login_token VARCHAR(255) DEFAULT NULL AFTER security_answer");
                jdbcTemplate.execute("ALTER TABLE user ADD COLUMN login_time DATETIME DEFAULT NULL AFTER login_token");
                jdbcTemplate.execute("CREATE INDEX idx_login_token ON user(login_token)");

                System.out.println("数据库字段添加成功！");
            } else {
                System.out.println("login_token 字段已存在，跳过迁移。");
            }

            boolean pointsConfigExists = checkTableExists("points_config");

            if (!pointsConfigExists) {
                System.out.println("正在创建 points_config 表...");

                jdbcTemplate.execute("CREATE TABLE points_config (id BIGINT AUTO_INCREMENT PRIMARY KEY, first_chat_cost INT DEFAULT 0, repeat_chat_cost INT DEFAULT 0, message_cost INT DEFAULT 0, question_reward INT DEFAULT 5, answer_reward INT DEFAULT 2, first_contact_reward INT DEFAULT 10, repeat_contact_reward INT DEFAULT 2, reply_reward INT DEFAULT 1, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");

                jdbcTemplate.execute("INSERT INTO points_config (first_chat_cost, repeat_chat_cost, message_cost, question_reward, answer_reward, first_contact_reward, repeat_contact_reward, reply_reward) VALUES (2, 1, 1, 5, 2, 10, 2, 1)");

                System.out.println("points_config 表创建成功！");
            } else {
                System.out.println("points_config 表已存在，跳过创建。");
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