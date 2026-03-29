package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.PointsConfig;
import com.example.qa.entity.User;
import com.example.qa.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/points/config")
public class PointsConfigController {

    @Autowired
    private PointsConfigService pointsConfigService;

    @GetMapping
    public ApiResponse getPointsConfig() {
        try {
            PointsConfig config = pointsConfigService.getPointsConfig();
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping
    public ApiResponse updatePointsConfig(@RequestBody PointsConfig config, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            pointsConfigService.updatePointsConfig(config);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }
}