package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qa.dto.request.CommentCreateRequest;
import com.example.qa.entity.Comment;
import com.example.qa.mapper.CommentMapper;
import com.example.qa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 评论服务实现类
 * 实现评论相关的业务逻辑
 */
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    /**
     * 创建评论
     * @param request 评论创建请求
     * @param userId 用户ID
     * @return 创建成功的评论信息
     */
    @Override
    public Comment createComment(CommentCreateRequest request, Long userId) {
        // 检查目标类型是否合法
        if (!"QUESTION".equals(request.getTargetType()) && !"ANSWER".equals(request.getTargetType())) {
            throw new RuntimeException("无效的目标类型");
        }
        
        // 创建评论对象
        Comment comment = new Comment();
        comment.setTargetType(request.getTargetType());
        comment.setTargetId(request.getTargetId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());
        
        // 保存评论
        commentMapper.insert(comment);
        
        return comment;
    }
    
    /**
     * 根据目标获取评论列表
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 评论列表
     */
    @Override
    public List<Comment> getCommentsByTarget(String targetType, Long targetId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTargetType, targetType);
        wrapper.eq(Comment::getTargetId, targetId);
        wrapper.orderByDesc(Comment::getCreatedAt);
        return commentMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID获取评论详情
     * @param id 评论ID
     * @return 评论详情
     */
    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }
    
    /**
     * 更新评论
     * @param id 评论ID
     * @param content 评论内容
     * @param userId 用户ID
     * @return 更新后的评论信息
     */
    @Override
    public Comment updateComment(Long id, String content, Long userId) {
        // 获取评论
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查权限
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此评论");
        }
        
        // 更新评论
        comment.setContent(content);
        comment.setUpdatedAt(new Date());
        commentMapper.updateById(comment);
        
        return comment;
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID
     */
    @Override
    public void deleteComment(Long id, Long userId) {
        // 获取评论
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查权限
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此评论");
        }
        
        // 删除评论
        commentMapper.deleteById(id);
    }
}