package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Comment;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> findCommentsByTarget(String targetType, Long targetId);
}