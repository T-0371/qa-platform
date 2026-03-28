package com.example.qa.controller;

import com.example.qa.dto.request.QuestionCreateRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.HotQuestionConfig;
import com.example.qa.entity.PointsConfig;
import com.example.qa.entity.Question;
import com.example.qa.service.HotQuestionConfigService;
import com.example.qa.service.PointsConfigService;
import com.example.qa.service.QuestionService;
import com.example.qa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问题管理控制器
 * 实现问题相关的API接口
 */
@RestController
@RequestMapping("/questions")
@Tag(name = "问题管理", description = "问题的创建、查询、更新、删除等接口")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private HotQuestionConfigService hotQuestionConfigService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointsConfigService pointsConfigService;
    
    /**
     * 创建问题
     * @param request 问题创建请求
     * @return 创建成功的问题信息
     */
    @PostMapping
    @Operation(summary = "创建问题", description = "创建新的问题")
    public ApiResponse<Question> createQuestion(@RequestBody QuestionCreateRequest request) {
        Long userId = request.getUserId();
        Question question = questionService.createQuestion(request, userId);
        
        // 从积分配置中获取发布问题获得的积分
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int questionReward = config != null && config.getQuestionReward() != null ? config.getQuestionReward() : 10;
        
        // 发布问题增加积分
        userService.addPoints(userId, questionReward);
        ApiResponse<Question> response = ApiResponse.success(question);
        response.setMessage("问题创建成功，获得" + questionReward + "积分");
        return response;
    }
    
    /**
     * 获取问题列表
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词（可选）
     * @return 问题列表
     */
    @GetMapping
    @Operation(summary = "获取问题列表", description = "分页获取问题列表，支持关键词搜索")
    public ApiResponse<List<Question>> getQuestionList(@RequestParam(defaultValue = "1") int page, 
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String keyword) {
        List<Question> questions;
        if (keyword != null && !keyword.trim().isEmpty()) {
            questions = questionService.searchQuestions(keyword.trim(), page, size);
        } else {
            questions = questionService.getQuestionList(page, size);
        }
        return ApiResponse.success(questions);
    }
    
    /**
     * 获取问题详情
     * @param id 问题ID
     * @return 问题详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取问题详情", description = "根据问题ID获取问题详细信息")
    public ApiResponse<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ApiResponse.success(question);
    }
    
    /**
     * 获取问题详情（不增加浏览量）
     * @param id 问题ID
     * @return 问题详情
     */
    @GetMapping("/{id}/no-view")
    @Operation(summary = "获取问题详情（不增加浏览量）", description = "根据问题ID获取问题详细信息，不增加浏览量")
    public ApiResponse<Question> getQuestionByIdWithoutViewCount(@PathVariable Long id) {
        Question question = questionService.getQuestionByIdWithoutViewCount(id);
        return ApiResponse.success(question);
    }
    
    /**
     * 更新问题
     * @param id 问题ID
     * @param request 问题更新请求
     * @param userId 用户ID（从请求参数或认证中获取，可选）
     * @return 更新后的问题信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新问题", description = "更新问题的标题和内容")
    public ApiResponse<Question> updateQuestion(@PathVariable Long id, @RequestBody QuestionCreateRequest request, 
                                                 @RequestParam(value = "userId", required = false) Long paramUserId,
                                                 @RequestAttribute(value = "userId", required = false) Long attrUserId) {
        Long userId = paramUserId != null ? paramUserId : attrUserId;
        Question question = questionService.updateQuestion(id, request, userId);
        return ApiResponse.success(question);
    }
    
    /**
     * 删除问题
     * @param id 问题ID
     * @param userId 用户ID（从请求参数或认证中获取，可选）
     * @return 删除成功的响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除问题", description = "删除指定的问题")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id, 
                                            @RequestParam(value = "userId", required = false) Long paramUserId,
                                            @RequestAttribute(value = "userId", required = false) Long attrUserId) {
        Long userId = paramUserId != null ? paramUserId : attrUserId;
        questionService.deleteQuestion(id, userId);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取热门问题
     * @param limit 数量限制
     * @return 热门问题列表
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门问题", description = "获取浏览量较高的热门问题")
    public ApiResponse<List<Question>> getHotQuestions(@RequestParam(defaultValue = "10") int limit) {
        HotQuestionConfig config = hotQuestionConfigService.getEnabledConfig();
        List<Question> questions = questionService.getHotQuestionsByConfig(
            config.getTimeRangeDays(),
            config.getMinViewCount(),
            config.getMinAnswerCount(),
            config.getMinVoteCount(),
            config.getSortBy(),
            Math.min(limit, config.getDisplayCount())
        );
        return ApiResponse.success(questions);
    }
    
    /**
     * 获取最新问题
     * @param limit 数量限制
     * @return 最新问题列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新问题", description = "获取最新发布的问题")
    public ApiResponse<List<Question>> getLatestQuestions(@RequestParam(defaultValue = "10") int limit) {
        List<Question> questions = questionService.getLatestQuestions(limit);
        return ApiResponse.success(questions);
    }
    
    /**
     * 搜索问题
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索问题", description = "根据关键词搜索问题")
    public ApiResponse<List<Question>> searchQuestions(@RequestParam String keyword,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        List<Question> questions = questionService.searchQuestions(keyword, page, size);
        return ApiResponse.success(questions);
    }
}