package com.example.qa.service;

import com.example.qa.entity.PointsConfig;

/**
 * 积分配置服务接口
 */
public interface PointsConfigService {
    
    /**
     * 获取当前积分配置
     * @return 积分配置
     */
    PointsConfig getCurrentConfig();
    
    /**
     * 更新积分配置
     * @param config 积分配置
     * @return 更新后的积分配置
     */
    PointsConfig updateConfig(PointsConfig config);
    
    /**
     * 初始化默认积分配置
     */
    void initDefaultConfig();
}
