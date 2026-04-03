package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于检查应用程序和数据库连接状态
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 健康检查接口
     */
    @GetMapping
    public ApiResponse health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "QA Platform");
        health.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(health);
    }

    /**
     * 数据库连接检查
     */
    @GetMapping("/database")
    public ApiResponse checkDatabase() {
        try {
            Connection connection = dataSource.getConnection();
            boolean valid = connection.isValid(5);
            connection.close();

            Map<String, Object> dbInfo = new HashMap<>();
            dbInfo.put("status", valid ? "UP" : "DOWN");
            dbInfo.put("url", dataSource.getConnection().getMetaData().getURL());
            dbInfo.put("username", dataSource.getConnection().getMetaData().getUserName());

            return ApiResponse.success(dbInfo);
        } catch (Exception e) {
            System.err.println("数据库连接检查失败：");
            e.printStackTrace();
            return ApiResponse.error(500, "数据库连接失败：" + e.getMessage());
        }
    }

    /**
     * 测试数据库查询
     */
    @GetMapping("/database/test")
    public ApiResponse testDatabaseQuery() {
        try {
            String sql = "SELECT COUNT(*) FROM user";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "SUCCESS");
            result.put("userCount", count);
            result.put("message", "数据库查询成功");

            return ApiResponse.success(result);
        } catch (Exception e) {
            System.err.println("数据库查询测试失败：");
            e.printStackTrace();
            return ApiResponse.error(500, "数据库查询失败：" + e.getMessage());
        }
    }

    /**
     * 测试密码加密
     */
    @GetMapping("/password/test")
    public ApiResponse testPassword(@RequestParam String password) {
        try {
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

            Map<String, Object> result = new HashMap<>();
            result.put("originalPassword", password);
            result.put("md5Password", md5Password);

            return ApiResponse.success(result);
        } catch (Exception e) {
            System.err.println("密码加密测试失败：");
            e.printStackTrace();
            return ApiResponse.error(500, "密码加密失败：" + e.getMessage());
        }
    }
}