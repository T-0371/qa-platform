package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.HotQuestionConfig;
import com.example.qa.service.HotQuestionConfigService;
import com.example.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hot-question-config")
public class HotQuestionConfigController {

    @Autowired
    private HotQuestionConfigService hotQuestionConfigService;

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ApiResponse getConfigs() {
        try {
            List<HotQuestionConfig> configs = hotQuestionConfigService.getAllConfigs();
            return ApiResponse.success("获取成功", configs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse createConfig(@RequestBody HotQuestionConfig config) {
        try {
            HotQuestionConfig createdConfig = hotQuestionConfigService.createConfig(config);
            return ApiResponse.success("创建成功", createdConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateConfig(@PathVariable Long id, @RequestBody HotQuestionConfig config) {
        try {
            config.setId(id);
            HotQuestionConfig updatedConfig = hotQuestionConfigService.updateConfig(config);
            return ApiResponse.success("更新成功", updatedConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteConfig(@PathVariable Long id) {
        try {
            hotQuestionConfigService.deleteConfig(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse getConfig(@PathVariable Long id) {
        try {
            HotQuestionConfig config = hotQuestionConfigService.getConfigById(id);
            if (config == null) {
                return ApiResponse.error("配置不存在");
            }
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/enable")
    public ApiResponse enableConfig(@PathVariable Long id) {
        try {
            hotQuestionConfigService.enableConfig(id);
            return ApiResponse.success("启用成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("启用失败: " + e.getMessage());
        }
    }

    @GetMapping("/enabled")
    public ApiResponse getEnabledConfig() {
        try {
            HotQuestionConfig config = hotQuestionConfigService.getEnabledConfig();
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/hot-questions")
    public ApiResponse getHotQuestions() {
        try {
            HotQuestionConfig config = hotQuestionConfigService.getEnabledConfig();
            if (config == null) {
                return ApiResponse.error("未找到启用的配置");
            }
            List hotQuestions = questionService.getHotQuestionsByConfig(
                    config.getTimeRangeDays(),
                    config.getMinViewCount(),
                    config.getMinAnswerCount(),
                    config.getMinVoteCount(),
                    config.getSortBy(),
                    config.getDisplayCount()
            );
            return ApiResponse.success("获取成功", hotQuestions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/preview")
    public ApiResponse previewHotConfig(@RequestBody HotQuestionConfig config) {
        try {
            List hotQuestions = questionService.getHotQuestionsByConfig(
                    config.getTimeRangeDays(),
                    config.getMinViewCount(),
                    config.getMinAnswerCount(),
                    config.getMinVoteCount(),
                    config.getSortBy(),
                    config.getDisplayCount()
            );
            return ApiResponse.success("预览成功", hotQuestions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("预览失败: " + e.getMessage());
        }
    }
}
