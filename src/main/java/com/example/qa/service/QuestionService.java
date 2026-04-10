package com.example.qa.service;

import com.example.qa.dto.request.QuestionCreateRequest;
import com.example.qa.entity.Question;

import java.util.List;

/**
 * 问题服务接口
 * 定义问题相关的业务方法
 */
public interface QuestionService {
    /**
     * 创建问题
     * @param request 问题创建请求
     * @param userId 用户ID
     * @return 创建成功的问题信息
     */
    Question createQuestion(QuestionCreateRequest request, Long userId);
    
    /**
     * 获取问题列表
     * @param page 页码
     * @param size 每页大小
     * @return 问题列表
     */
    List<Question> getQuestionList(int page, int size);

    java.util.Map<String, Object> getQuestionListWithTotal(int page, int size);
    
    /**
     * 根据ID获取问题详情
     * @param id 问题ID
     * @return 问题详情
     */
    Question getQuestionById(Long id);
    
    /**
     * 根据ID获取问题详情（不增加浏览量）
     * @param id 问题ID
     * @return 问题详情
     */
    Question getQuestionByIdWithoutViewCount(Long id);
    
    /**
     * 更新问题
     * @param id 问题ID
     * @param request 问题更新请求
     * @param userId 用户ID
     * @return 更新后的问题信息
     */
    Question updateQuestion(Long id, QuestionCreateRequest request, Long userId);
    
    /**
     * 删除问题
     * @param id 问题ID
     * @param userId 用户ID
     */
    void deleteQuestion(Long id, Long userId);
    
    /**
     * 获取热门问题
     * @param limit 数量限制
     * @return 热门问题列表
     */
    List<Question> getHotQuestions(int limit);
    
    /**
     * 获取最新问题
     * @param limit 数量限制
     * @return 最新问题列表
     */
    List<Question> getLatestQuestions(int limit);
    
    /**
     * 根据标签获取问题
     * @param tagId 标签ID
     * @param page 页码
     * @param size 每页大小
     * @return 问题列表
     */
    List<Question> getQuestionsByTagId(Long tagId, int page, int size);
    
    /**
     * 根据配置获取热门问题
     * @param timeRangeDays 时间范围（天）
     * @param minViewCount 最小浏览量
     * @param minAnswerCount 最小回答数
     * @param minVoteCount 最小投票数
     * @param sortBy 排序方式
     * @param limit 数量限制
     * @return 热门问题列表
     */
    List<Question> getHotQuestionsByConfig(Integer timeRangeDays, Integer minViewCount, 
                                            Integer minAnswerCount, Integer minVoteCount, 
                                            String sortBy, Integer limit);
    
    /**
     * 搜索问题
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 问题列表
     */
    List<Question> searchQuestions(String keyword, int page, int size);
}