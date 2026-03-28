package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Vote;
import com.example.qa.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞管理控制器
 * 实现点赞/踩相关的API接口
 */
@RestController
@RequestMapping("/votes")
@Tag(name = "点赞管理", description = "点赞/踩的创建、取消等接口")
public class VoteController {
    
    @Autowired
    private VoteService voteService;
    
    /**
     * 点赞/踩
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param voteType 点赞类型：1表示点赞，-1表示踩
     * @param userId 用户ID
     * @return 点赞记录
     */
    @PostMapping
    @Operation(summary = "点赞/踩", description = "对问题、回答或评论进行点赞/踩")
    public ApiResponse<Vote> vote(@RequestParam String targetType, @RequestParam Long targetId, @RequestParam Integer voteType, @RequestParam Long userId) {
        Vote vote = voteService.vote(targetType, targetId, userId, voteType);
        return ApiResponse.success(vote);
    }
    
    /**
     * 取消点赞/踩
     * @param id 点赞记录ID
     * @param userId 用户ID
     * @return 取消成功的响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "取消点赞/踩", description = "取消对问题、回答或评论的点赞/踩")
    public ApiResponse<Void> cancelVote(@PathVariable Long id, @RequestParam Long userId) {
        voteService.cancelVote(id, userId);
        return ApiResponse.success(null);
    }
}