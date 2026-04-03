package com.example.qa.dto.request;

import lombok.Data;

/**
 * 评论创建请求DTO
 * 用于接收创建评论的请求参数
 */
@Data
public class CommentCreateRequest {
    /**
     * 目标类型，如QUESTION或ANSWER
     */
    private String targetType;
    
    /**
     * 目标ID，如问题ID或回答ID
     */
    private Long targetId;
    
    /**
     * 评论内容
     */
    private String content;
}