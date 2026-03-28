package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.qa.entity.SystemConfig;
import com.example.qa.mapper.SystemConfigMapper;
import com.example.qa.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Cacheable(value = "systemConfig", key = "'config'")
    public SystemConfig getConfig() {
        try {
            QueryWrapper<SystemConfig> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("id");
            wrapper.last("LIMIT 1");
            List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
            if (configs.isEmpty()) {
                return getDefaultConfig();
            }
            return configs.get(0);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("doesn't exist")) {
                createTableAndInsertDefault();
                return getDefaultConfig();
            }
            throw e;
        }
    }
    
    @Override
    @CacheEvict(value = "systemConfig", key = "'config'")
    public SystemConfig updateConfig(SystemConfig config) {
        try {
            SystemConfig existingConfig = getConfigWithoutCache();
            if (existingConfig == null) {
                config.setId(null);
                config.setCreatedAt(new Date());
                config.setUpdatedAt(new Date());
                systemConfigMapper.insert(config);
                return config;
            } else {
                config.setId(existingConfig.getId());
                config.setCreatedAt(existingConfig.getCreatedAt());
                config.setUpdatedAt(new Date());
                systemConfigMapper.updateById(config);
                return systemConfigMapper.selectById(config.getId());
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("doesn't exist")) {
                createTableAndInsertDefault();
                return updateConfig(config);
            }
            throw e;
        }
    }
    
    private SystemConfig getConfigWithoutCache() {
        QueryWrapper<SystemConfig> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        wrapper.last("LIMIT 1");
        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        if (configs.isEmpty()) {
            return null;
        }
        return configs.get(0);
    }
    
    @Override
    @CacheEvict(value = "systemConfig", key = "'config'")
    public SystemConfig resetToDefault() {
        SystemConfig defaultConfig = getDefaultConfig();
        return updateConfig(defaultConfig);
    }
    
    private void createTableAndInsertDefault() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS system_config (" +
                "id BIGINT NOT NULL AUTO_INCREMENT, " +
                "site_name VARCHAR(100) NOT NULL DEFAULT 'TechQA', " +
                "site_description VARCHAR(500) DEFAULT NULL, " +
                "background_type VARCHAR(20) NOT NULL DEFAULT 'gradient', " +
                "background_value TEXT DEFAULT NULL, " +
                "layout_type VARCHAR(20) NOT NULL DEFAULT 'default', " +
                "logo_url VARCHAR(500) DEFAULT NULL, " +
                "favicon_url VARCHAR(500) DEFAULT NULL, " +
                "primary_color VARCHAR(20) NOT NULL DEFAULT '#ec4899', " +
                "secondary_color VARCHAR(20) NOT NULL DEFAULT '#8b5cf6', " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System Config'";
        
        jdbcTemplate.execute(createTableSql);
        
        String insertDefaultSql = "INSERT INTO system_config (site_name, site_description, background_type, background_value, layout_type, primary_color, secondary_color) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertDefaultSql, 
                "TechQA", 
                "探索技术前沿 · 分享编程智慧 · 共同成长进步",
                "gradient",
                "linear-gradient(145deg, #0f0f1e 0%, #1a1a2e 25%, #16213e 50%, #0d0d1a 75%, #0a0a14 100%)",
                "default",
                "#ec4899",
                "#8b5cf6"
        );
    }
    
    private SystemConfig getDefaultConfig() {
        return SystemConfig.builder()
                .siteName("TechQA")
                .siteDescription("探索技术前沿 · 分享编程智慧 · 共同成长进步")
                .backgroundType("gradient")
                .backgroundValue("linear-gradient(145deg, #0f0f1e 0%, #1a1a2e 25%, #16213e 50%, #0d0d1a 75%, #0a0a14 100%)")
                .layoutType("default")
                .primaryColor("#ec4899")
                .secondaryColor("#8b5cf6")
                .build();
    }
}
