package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Collect;
import com.example.qa.service.CollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏管理控制器
 * 实现收藏相关的API接口
 */
@RestController
@RequestMapping("/collects")
@Tag(name = "收藏管理", description = "问题收藏的创建、取消、查询等接口")
public class CollectController {
    
    @Autowired
    private CollectService collectService;
    
    /**
     * 收藏问题
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    @PostMapping
    @Operation(summary = "收藏问题", description = "收藏指定的问题")
    public ApiResponse<Collect> collectQuestion(@RequestParam Long questionId, @RequestParam Long userId) {
        Collect collect = collectService.collectQuestion(questionId, userId);
        return ApiResponse.success(collect);
    }
    
    /**
     * 取消收藏
     * @param id 收藏记录ID
     * @param userId 用户ID
     * @return 取消成功的响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "取消收藏", description = "取消对指定问题的收藏")
    public ApiResponse<Void> cancelCollect(@PathVariable Long id, @RequestParam Long userId) {
        collectService.cancelCollect(id, userId);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取当前用户的收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户的收藏列表", description = "获取登录用户的收藏问题列表")
    public ApiResponse<List<Collect>> getUserCollects(@RequestParam Long userId) {
        List<Collect> collects = collectService.getUserCollects(userId);
        return ApiResponse.success(collects);
    }
}