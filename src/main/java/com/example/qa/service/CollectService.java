package com.example.qa.service;

import com.example.qa.entity.Collect;

import java.util.List;

/**
 * 收藏服务接口
 * 定义收藏相关的业务方法
 */
public interface CollectService {
    /**
     * 收藏问题
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    Collect collectQuestion(Long questionId, Long userId);
    
    /**
     * 取消收藏
     * @param id 收藏记录ID
     * @param userId 用户ID
     */
    void cancelCollect(Long id, Long userId);
    
    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Collect> getUserCollects(Long userId);
    
    /**
     * 检查用户是否收藏了问题
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 是否收藏
     */
    boolean isCollected(Long questionId, Long userId);
}