package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.User;
import com.example.qa.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/system")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/config")
    public ApiResponse getSystemConfig() {
        try {
            return ApiResponse.success("获取系统配置成功", systemConfigService.getSystemConfig());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取系统配置失败: " + e.getMessage());
        }
    }

    @PutMapping("/config")
    public ApiResponse updateSystemConfig(@RequestParam String key, @RequestParam String value, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            
            systemConfigService.updateSystemConfig(key, value);
            return ApiResponse.success("更新系统配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新系统配置失败: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ApiResponse refreshSystemConfig(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            
            systemConfigService.refreshSystemConfig();
            return ApiResponse.success("刷新系统配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("刷新系统配置失败: " + e.getMessage());
        }
    }
}