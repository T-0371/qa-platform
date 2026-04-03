package com.example.qa.service;

import com.example.qa.dto.request.AnswerCreateRequest;
import com.example.qa.entity.Answer;

import java.util.List;

/**
 * 回答服务接口
 * 定义回答相关的业务方法
 */
public interface AnswerService {
    /**
     * 获取所有回答
     * @return 所有回答列表
     */
    List<Answer> getAllAnswers();
    
    /**
     * 创建回答
     * @param questionId 问题ID
     * @param request 回答创建请求
     * @param userId 用户ID
     * @return 创建成功的回答信息
     */
    Answer createAnswer(Long questionId, AnswerCreateRequest request, Long userId);
    
    /**
     * 获取问题的回答列表
     * @param questionId 问题ID
     * @param page 页码
     * @param size 每页大小
     * @return 回答列表
     */
    List<Answer> getAnswersByQuestionId(Long questionId, int page, int size);
    
    /**
     * 根据ID获取回答详情
     * @param id 回答ID
     * @return 回答详情
     */
    Answer getAnswerById(Long id);
    
    /**
     * 更新回答
     * @param id 回答ID
     * @param request 回答更新请求
     * @param userId 用户ID
     * @return 更新后的回答信息
     */
    Answer updateAnswer(Long id, AnswerCreateRequest request, Long userId);
    
    /**
     * 删除回答
     * @param id 回答ID
     * @param userId 用户ID
     */
    void deleteAnswer(Long id, Long userId);
    
    /**
     * 采纳回答
     * @param id 回答ID
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 采纳后的回答信息
     */
    Answer acceptAnswer(Long id, Long questionId, Long userId);
}