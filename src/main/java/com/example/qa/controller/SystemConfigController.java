package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.SystemConfig;
import com.example.qa.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system-config")
@Tag(name = "系统配置", description = "系统配置管理")
public class SystemConfigController {
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @GetMapping
    @Operation(summary = "获取系统配置", description = "获取当前系统配置")
    public ApiResponse<SystemConfig> getConfig() {
        SystemConfig config = systemConfigService.getConfig();
        return ApiResponse.success(config);
    }
    
    @PostMapping
    @Operation(summary = "更新系统配置", description = "更新系统配置")
    public ApiResponse<SystemConfig> updateConfig(@RequestBody SystemConfig config) {
        SystemConfig updatedConfig = systemConfigService.updateConfig(config);
        return ApiResponse.success(updatedConfig);
    }
    
    @PostMapping("/reset")
    @Operation(summary = "重置系统配置", description = "重置为默认配置")
    public ApiResponse<SystemConfig> resetToDefault() {
        SystemConfig config = systemConfigService.resetToDefault();
        return ApiResponse.success(config);
    }
}
