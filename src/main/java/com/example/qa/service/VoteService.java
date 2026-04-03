package com.example.qa.service;

import com.example.qa.entity.Vote;

/**
 * 点赞服务接口
 * 定义点赞/踩相关的业务方法
 */
public interface VoteService {
    /**
     * 点赞/踩
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param userId 用户ID
     * @param voteType 点赞类型：1表示点赞，-1表示踩
     * @return 点赞记录
     */
    Vote vote(String targetType, Long targetId, Long userId, Integer voteType);
    
    /**
     * 取消点赞/踩
     * @param id 点赞记录ID
     * @param userId 用户ID
     */
    void cancelVote(Long id, Long userId);
    
    /**
     * 根据目标类型、目标ID和用户ID获取点赞记录
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    Vote getVoteByTargetAndUser(String targetType, Long targetId, Long userId);
}