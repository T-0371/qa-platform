package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Collect;

import java.util.List;

/**
 * 收藏Mapper接口
 * 用于收藏相关的数据库操作
 */
public interface CollectMapper extends BaseMapper<Collect> {
    /**
     * 根据用户ID获取收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Collect> findByUserId(Long userId);
    
    /**
     * 根据用户ID和问题ID获取收藏记录
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 收藏记录
     */
    Collect findByUserAndQuestion(Long userId, Long questionId);
}