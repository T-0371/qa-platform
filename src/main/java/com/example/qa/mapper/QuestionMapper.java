package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {
    List<Question> findHotQuestions(int limit);
    List<Question> findLatestQuestions(int limit);
    List<Question> findQuestionsByTagId(Long tagId);
    
    /**
     * 根据配置获取热门问题
     */
    List<Question> findHotQuestionsByConfig(@Param("timeRangeDays") Integer timeRangeDays,
                                             @Param("minViewCount") Integer minViewCount,
                                             @Param("minAnswerCount") Integer minAnswerCount,
                                             @Param("minVoteCount") Integer minVoteCount,
                                             @Param("sortBy") String sortBy,
                                             @Param("limit") Integer limit);
    
    /**
     * 搜索问题
     */
    List<Question> searchQuestions(@Param("keyword") String keyword);
}