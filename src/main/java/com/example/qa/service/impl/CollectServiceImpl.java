package com.example.qa.service.impl;

import com.example.qa.entity.Collect;
import com.example.qa.mapper.CollectMapper;
import com.example.qa.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 收藏服务实现类
 * 实现收藏相关的业务逻辑
 */
@Service
public class CollectServiceImpl implements CollectService {
    
    @Autowired
    private CollectMapper collectMapper;
    
    /**
     * 收藏问题
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    @Override
    public Collect collectQuestion(Long questionId, Long userId) {
        // 检查是否已经收藏
        Collect existingCollect = collectMapper.findByUserAndQuestion(userId, questionId);
        if (existingCollect != null) {
            throw new RuntimeException("已经收藏过此问题");
        }
        
        // 创建收藏记录
        Collect collectRecord = new Collect();
        collectRecord.setUserId(userId);
        collectRecord.setQuestionId(questionId);
        collectRecord.setCreatedAt(new Date());
        
        // 保存收藏记录
        collectMapper.insert(collectRecord);
        
        return collectRecord;
    }
    
    /**
     * 取消收藏
     * @param id 收藏记录ID
     * @param userId 用户ID
     */
    @Override
    public void cancelCollect(Long id, Long userId) {
        // 获取收藏记录
        Collect collectRecord = collectMapper.selectById(id);
        if (collectRecord == null) {
            throw new RuntimeException("收藏记录不存在");
        }
        
        // 检查权限
        if (!collectRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权限取消此收藏");
        }
        
        // 删除收藏记录
        collectMapper.deleteById(id);
    }
    
    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    @Override
    public List<Collect> getUserCollects(Long userId) {
        return collectMapper.findByUserId(userId);
    }
    
    /**
     * 检查用户是否收藏了问题
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 是否收藏
     */
    @Override
    public boolean isCollected(Long questionId, Long userId) {
        Collect collectRecord = collectMapper.findByUserAndQuestion(userId, questionId);
        return collectRecord != null;
    }
}