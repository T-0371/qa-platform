package com.example.qa.service;

import com.example.qa.dto.request.CommentCreateRequest;
import com.example.qa.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 定义评论相关的业务方法
 */
public interface CommentService {
    /**
     * 创建评论
     * @param request 评论创建请求
     * @param userId 用户ID
     * @return 创建成功的评论信息
     */
    Comment createComment(CommentCreateRequest request, Long userId);
    
    /**
     * 根据目标获取评论列表
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 评论列表
     */
    List<Comment> getCommentsByTarget(String targetType, Long targetId);
    
    /**
     * 根据ID获取评论详情
     * @param id 评论ID
     * @return 评论详情
     */
    Comment getCommentById(Long id);
    
    /**
     * 更新评论
     * @param id 评论ID
     * @param content 评论内容
     * @param userId 用户ID
     * @return 更新后的评论信息
     */
    Comment updateComment(Long id, String content, Long userId);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID
     */
    void deleteComment(Long id, Long userId);
}