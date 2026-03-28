package com.example.qa.service;

import com.example.qa.entity.HotQuestionConfig;
import java.util.List;

/**
 * 热门问题配置服务接口
 */
public interface HotQuestionConfigService {
    
    /**
     * 创建配置
     * @param config 配置信息
     * @return 创建后的配置
     */
    HotQuestionConfig createConfig(HotQuestionConfig config);
    
    /**
     * 更新配置
     * @param config 配置信息
     * @return 更新后的配置
     */
    HotQuestionConfig updateConfig(HotQuestionConfig config);
    
    /**
     * 删除配置
     * @param id 配置ID
     */
    void deleteConfig(Long id);
    
    /**
     * 获取配置详情
     * @param id 配置ID
     * @return 配置详情
     */
    HotQuestionConfig getConfigById(Long id);
    
    /**
     * 获取所有配置
     * @return 配置列表
     */
    List<HotQuestionConfig> getAllConfigs();
    
    /**
     * 获取当前启用的配置
     * @return 启用的配置
     */
    HotQuestionConfig getEnabledConfig();
    
    /**
     * 启用配置
     * @param id 配置ID
     */
    void enableConfig(Long id);
}
