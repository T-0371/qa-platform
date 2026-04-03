package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.PointsConfig;
import com.example.qa.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points-config")
public class PointsConfigController {

    @Autowired
    private PointsConfigService pointsConfigService;

    @GetMapping
    public ApiResponse getPointsConfig() {
        try {
            PointsConfig config = pointsConfigService.getCurrentConfig();
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping
    public ApiResponse updatePointsConfig(@RequestBody PointsConfig config) {
        try {
            PointsConfig updatedConfig = pointsConfigService.updateConfig(config);
            return ApiResponse.success("更新成功", updatedConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/init")
    public ApiResponse initDefaultConfig() {
        try {
            pointsConfigService.initDefaultConfig();
            return ApiResponse.success("初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("初始化失败: " + e.getMessage());
        }
    }
}