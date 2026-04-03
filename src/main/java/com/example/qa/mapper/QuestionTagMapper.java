package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.QuestionTag;

import java.util.List;

/**
 * 问题标签关联Mapper接口
 * 用于问题标签关联相关的数据库操作
 */
public interface QuestionTagMapper extends BaseMapper<QuestionTag> {
    /**
     * 根据问题ID获取标签ID列表
     * @param questionId 问题ID
     * @return 标签ID列表
     */
    List<Long> findTagIdsByQuestionId(Long questionId);
    
    /**
     * 根据标签ID获取问题ID列表
     * @param tagId 标签ID
     * @return 问题ID列表
     */
    List<Long> findQuestionIdsByTagId(Long tagId);
}