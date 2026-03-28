package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.HotQuestionConfig;
import com.example.qa.entity.Question;
import com.example.qa.service.HotQuestionConfigService;
import com.example.qa.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 热门问题配置控制器
 */
@RestController
@RequestMapping("/hot-question-config")
@Tag(name = "热门问题配置", description = "热门问题排行榜配置管理")
public class HotQuestionConfigController {
    
    @Autowired
    private HotQuestionConfigService hotQuestionConfigService;
    
    @Autowired
    private QuestionService questionService;
    
    /**
     * 创建配置
     */
    @PostMapping
    @Operation(summary = "创建热门问题配置", description = "创建新的热门问题排行榜配置")
    public ApiResponse<HotQuestionConfig> createConfig(@RequestBody HotQuestionConfig config) {
        HotQuestionConfig createdConfig = hotQuestionConfigService.createConfig(config);
        return ApiResponse.success(createdConfig);
    }
    
    /**
     * 更新配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新热门问题配置", description = "更新热门问题排行榜配置")
    public ApiResponse<HotQuestionConfig> updateConfig(@PathVariable Long id, @RequestBody HotQuestionConfig config) {
        config.setId(id);
        HotQuestionConfig updatedConfig = hotQuestionConfigService.updateConfig(config);
        return ApiResponse.success(updatedConfig);
    }
    
    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除热门问题配置", description = "删除热门问题排行榜配置")
    public ApiResponse<Void> deleteConfig(@PathVariable Long id) {
        hotQuestionConfigService.deleteConfig(id);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取配置详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取热门问题配置详情", description = "根据ID获取配置详情")
    public ApiResponse<HotQuestionConfig> getConfigById(@PathVariable Long id) {
        HotQuestionConfig config = hotQuestionConfigService.getConfigById(id);
        return ApiResponse.success(config);
    }
    
    /**
     * 获取所有配置
     */
    @GetMapping
    @Operation(summary = "获取所有热门问题配置", description = "获取所有配置列表")
    public ApiResponse<List<HotQuestionConfig>> getAllConfigs() {
        List<HotQuestionConfig> configs = hotQuestionConfigService.getAllConfigs();
        return ApiResponse.success(configs);
    }
    
    /**
     * 启用配置
     */
    @PostMapping("/{id}/enable")
    @Operation(summary = "启用热门问题配置", description = "启用指定的配置")
    public ApiResponse<Void> enableConfig(@PathVariable Long id) {
        hotQuestionConfigService.enableConfig(id);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取当前启用的配置
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取当前启用的配置", description = "获取当前启用的热门问题配置")
    public ApiResponse<HotQuestionConfig> getEnabledConfig() {
        HotQuestionConfig config = hotQuestionConfigService.getEnabledConfig();
        return ApiResponse.success(config);
    }
    
    /**
     * 根据当前配置获取热门问题
     */
    @GetMapping("/hot-questions")
    @Operation(summary = "获取热门问题", description = "根据当前配置获取热门问题排行榜")
    public ApiResponse<List<Question>> getHotQuestionsByConfig() {
        HotQuestionConfig config = hotQuestionConfigService.getEnabledConfig();
        List<Question> questions = questionService.getHotQuestionsByConfig(
            config.getTimeRangeDays(),
            config.getMinViewCount(),
            config.getMinAnswerCount(),
            config.getMinVoteCount(),
            config.getSortBy(),
            config.getDisplayCount()
        );
        return ApiResponse.success(questions);
    }
    
    /**
     * 预览配置效果
     */
    @PostMapping("/preview")
    @Operation(summary = "预览配置效果", description = "预览指定配置的热门问题效果")
    public ApiResponse<Map<String, Object>> previewConfig(@RequestBody HotQuestionConfig config) {
        List<Question> questions = questionService.getHotQuestionsByConfig(
            config.getTimeRangeDays(),
            config.getMinViewCount(),
            config.getMinAnswerCount(),
            config.getMinVoteCount(),
            config.getSortBy(),
            config.getDisplayCount()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("config", config);
        result.put("questions", questions);
        result.put("totalCount", questions.size());
        
        return ApiResponse.success(result);
    }
}
