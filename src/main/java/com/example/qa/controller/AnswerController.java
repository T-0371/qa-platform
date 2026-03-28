package com.example.qa.controller;

import com.example.qa.dto.request.AnswerCreateRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Answer;
import com.example.qa.entity.PointsConfig;
import com.example.qa.service.AnswerService;
import com.example.qa.service.PointsConfigService;
import com.example.qa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回答管理控制器
 * 实现回答相关的API接口
 */
@RestController
@RequestMapping("/answers")
@Tag(name = "回答管理", description = "回答的创建、查询、更新、删除和采纳等接口")
public class AnswerController {
    
    @Autowired
    private AnswerService answerService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointsConfigService pointsConfigService;
    
    /**
     * 获取所有回答
     * @return 所有回答列表
     */
    @GetMapping
    @Operation(summary = "获取所有回答", description = "获取系统中的所有回答列表")
    public ApiResponse<List<Answer>> getAllAnswers() {
        List<Answer> answers = answerService.getAllAnswers();
        return ApiResponse.success(answers);
    }
    
    /**
     * 创建回答
     * @param questionId 问题ID
     * @param request 回答创建请求
     * @return 创建成功的回答信息
     */
    @PostMapping("/questions/{questionId}")
    @Operation(summary = "创建回答", description = "为指定问题创建新的回答")
    public ApiResponse<Answer> createAnswer(@PathVariable Long questionId, @RequestBody AnswerCreateRequest request) {
        Long userId = request.getUserId();
        Answer answer = answerService.createAnswer(questionId, request, userId);
        
        // 从积分配置中获取回答问题获得的积分
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int answerReward = config != null && config.getAnswerReward() != null ? config.getAnswerReward() : 5;
        
        // 回答问题增加积分
        userService.addPoints(userId, answerReward);
        ApiResponse<Answer> response = ApiResponse.success(answer);
        response.setMessage("回答创建成功，获得" + answerReward + "积分");
        return response;
    }
    
    /**
     * 获取问题的回答列表
     * @param questionId 问题ID
     * @param page 页码
     * @param size 每页大小
     * @return 回答列表
     */
    @GetMapping("/questions/{questionId}")
    @Operation(summary = "获取问题的回答列表", description = "分页获取指定问题的回答列表")
    public ApiResponse<List<Answer>> getAnswersByQuestionId(@PathVariable Long questionId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<Answer> answers = answerService.getAnswersByQuestionId(questionId, page, size);
        return ApiResponse.success(answers);
    }
    
    /**
     * 更新回答
     * @param id 回答ID
     * @param request 回答更新请求
     * @param userId 用户ID（从认证中获取）
     * @return 更新后的回答信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新回答", description = "更新指定回答的内容")
    public ApiResponse<Answer> updateAnswer(@PathVariable Long id, @RequestBody AnswerCreateRequest request, @RequestAttribute("userId") Long userId) {
        Answer answer = answerService.updateAnswer(id, request, userId);
        return ApiResponse.success(answer);
    }
    
    /**
     * 删除回答
     * @param id 回答ID
     * @param userId 用户ID（从认证中获取）
     * @return 删除成功的响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除回答", description = "删除指定的回答")
    public ApiResponse<Void> deleteAnswer(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        answerService.deleteAnswer(id, userId);
        return ApiResponse.success(null);
    }
    
    /**
     * 采纳回答
     * @param id 回答ID
     * @param questionId 问题ID
     * @param userId 用户ID（从认证中获取）
     * @return 采纳后的回答信息
     */
    @PutMapping("/{id}/accept")
    @Operation(summary = "采纳回答", description = "采纳指定的回答为问题的最佳答案")
    public ApiResponse<Answer> acceptAnswer(@PathVariable Long id, @RequestParam Long questionId, @RequestAttribute("userId") Long userId) {
        Answer answer = answerService.acceptAnswer(id, questionId, userId);
        return ApiResponse.success(answer);
    }
}