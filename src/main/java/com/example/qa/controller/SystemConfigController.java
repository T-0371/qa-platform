package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.SystemConfig;
import com.example.qa.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system-config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public ApiResponse getSystemConfig() {
        try {
            SystemConfig config = systemConfigService.getConfig();
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping
    public ApiResponse updateSystemConfig(@RequestBody SystemConfig config) {
        try {
            SystemConfig updatedConfig = systemConfigService.updateConfig(config);
            return ApiResponse.success("更新成功", updatedConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    public ApiResponse saveSystemConfig(@RequestBody SystemConfig config) {
        try {
            SystemConfig updatedConfig = systemConfigService.updateConfig(config);
            return ApiResponse.success("保存成功", updatedConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("保存失败: " + e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ApiResponse resetSystemConfig() {
        try {
            SystemConfig defaultConfig = systemConfigService.resetToDefault();
            return ApiResponse.success("重置成功", defaultConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("重置失败: " + e.getMessage());
        }
    }
}