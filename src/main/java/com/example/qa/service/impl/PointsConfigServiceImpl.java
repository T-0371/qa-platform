package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.qa.entity.PointsConfig;
import com.example.qa.mapper.PointsConfigMapper;
import com.example.qa.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 积分配置服务实现类
 */
@Service
public class PointsConfigServiceImpl implements PointsConfigService {
    
    @Autowired
    private PointsConfigMapper pointsConfigMapper;
    
    @Override
    public PointsConfig getCurrentConfig() {
        try {
            PointsConfig config = pointsConfigMapper.selectOne(new QueryWrapper<>());
            if (config == null) {
                initDefaultConfig();
                config = pointsConfigMapper.selectOne(new QueryWrapper<>());
            }
            return config;
        } catch (Exception e) {
            // 如果表不存在，创建表并初始化配置
            try {
                createPointsConfigTable();
                initDefaultConfig();
                return pointsConfigMapper.selectOne(new QueryWrapper<>());
            } catch (Exception ex) {
                // 如果创建表失败，返回默认配置
                PointsConfig defaultConfig = new PointsConfig();
                defaultConfig.setFirstChatCost(10);
                defaultConfig.setRepeatChatCost(5);
                defaultConfig.setMessageCost(1);
                defaultConfig.setQuestionReward(10);
                defaultConfig.setAnswerReward(5);
                defaultConfig.setFirstContactReward(10);
                defaultConfig.setRepeatContactReward(5);
                defaultConfig.setReplyReward(1);
                return defaultConfig;
            }
        }
    }
    
    @Override
    public PointsConfig updateConfig(PointsConfig config) {
        try {
            PointsConfig existingConfig = pointsConfigMapper.selectOne(new QueryWrapper<>());
            if (existingConfig == null) {
                config.setCreatedAt(new Date());
                config.setUpdatedAt(new Date());
                pointsConfigMapper.insert(config);
            } else {
                config.setId(existingConfig.getId());
                config.setCreatedAt(existingConfig.getCreatedAt());
                config.setUpdatedAt(new Date());
                pointsConfigMapper.updateById(config);
            }
            return config;
        } catch (Exception e) {
            // 如果表不存在，创建表并插入配置
            try {
                createPointsConfigTable();
                config.setCreatedAt(new Date());
                config.setUpdatedAt(new Date());
                pointsConfigMapper.insert(config);
                return config;
            } catch (Exception ex) {
                throw new RuntimeException("保存积分配置失败: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public void initDefaultConfig() {
        PointsConfig config = new PointsConfig();
        config.setFirstChatCost(10);
        config.setRepeatChatCost(5);
        config.setMessageCost(1);
        config.setQuestionReward(10);
        config.setAnswerReward(5);
        config.setFirstContactReward(10);
        config.setRepeatContactReward(5);
        config.setReplyReward(1);
        config.setCreatedAt(new Date());
        config.setUpdatedAt(new Date());
        pointsConfigMapper.insert(config);
    }
    
    /**
     * 创建积分配置表
     */
    private void createPointsConfigTable() {
        // 这里可以添加创建表的SQL语句
        // 由于我们使用的是MyBatis Plus，这里只是一个占位符
        // 实际项目中，应该使用数据库迁移工具来管理表结构
    }
}
