package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Vote;

/**
 * 点赞Mapper接口
 * 用于点赞相关的数据库操作
 */
public interface VoteMapper extends BaseMapper<Vote> {
    /**
     * 根据目标类型、目标ID和用户ID获取点赞记录
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    Vote findByTargetAndUser(String targetType, Long targetId, Long userId);
}