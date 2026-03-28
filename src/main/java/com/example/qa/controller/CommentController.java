package com.example.qa.controller;

import com.example.qa.dto.request.CommentCreateRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Comment;
import com.example.qa.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理控制器
 * 实现评论相关的API接口
 */
@RestController
@RequestMapping("/comments")
@Tag(name = "评论管理", description = "评论的创建、查询、更新、删除等接口")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 创建评论
     * @param request 评论创建请求
     * @param userId 用户ID（从认证中获取）
     * @return 创建成功的评论信息
     */
    @PostMapping
    @Operation(summary = "创建评论", description = "为问题或回答创建新的评论")
    public ApiResponse<Comment> createComment(@RequestBody CommentCreateRequest request, @RequestAttribute("userId") Long userId) {
        Comment comment = commentService.createComment(request, userId);
        return ApiResponse.success(comment);
    }
    
    /**
     * 获取评论列表
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 评论列表
     */
    @GetMapping
    @Operation(summary = "获取评论列表", description = "获取指定目标的评论列表")
    public ApiResponse<List<Comment>> getCommentsByTarget(@RequestParam String targetType, @RequestParam Long targetId) {
        List<Comment> comments = commentService.getCommentsByTarget(targetType, targetId);
        return ApiResponse.success(comments);
    }
    
    /**
     * 更新评论
     * @param id 评论ID
     * @param content 评论内容
     * @param userId 用户ID（从认证中获取）
     * @return 更新后的评论信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新评论", description = "更新指定评论的内容")
    public ApiResponse<Comment> updateComment(@PathVariable Long id, @RequestParam String content, @RequestAttribute("userId") Long userId) {
        Comment comment = commentService.updateComment(id, content, userId);
        return ApiResponse.success(comment);
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID（从认证中获取）
     * @return 删除成功的响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除指定的评论")
    public ApiResponse<Void> deleteComment(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        commentService.deleteComment(id, userId);
        return ApiResponse.success(null);
    }
}