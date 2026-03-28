package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Answer;

import java.util.List;

public interface AnswerMapper extends BaseMapper<Answer> {
    List<Answer> findAnswersByQuestionId(Long questionId);
    List<Answer> findAcceptedAnswersByQuestionId(Long questionId);
}