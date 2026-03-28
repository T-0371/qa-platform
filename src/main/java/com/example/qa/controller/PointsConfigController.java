package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.PointsConfig;
import com.example.qa.service.PointsConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 积分配置控制器
 * 处理积分配置相关的API接口
 */
@RestController
@RequestMapping("/points-config")
@Tag(name = "积分配置", description = "积分配置的查询和更新接口")
public class PointsConfigController {
    
    @Autowired
    private PointsConfigService pointsConfigService;
    
    /**
     * 获取当前积分配置
     * @return 当前积分配置
     */
    @GetMapping
    @Operation(summary = "获取当前积分配置", description = "获取系统当前的积分配置")
    public ApiResponse<PointsConfig> getCurrentConfig() {
        PointsConfig config = pointsConfigService.getCurrentConfig();
        return ApiResponse.success(config);
    }
    
    /**
     * 更新积分配置
     * @param config 积分配置
     * @return 更新后的积分配置
     */
    @PutMapping
    @Operation(summary = "更新积分配置", description = "更新系统的积分配置")
    public ApiResponse<PointsConfig> updateConfig(@RequestBody PointsConfig config) {
        PointsConfig updatedConfig = pointsConfigService.updateConfig(config);
        return ApiResponse.success(updatedConfig);
    }
}
