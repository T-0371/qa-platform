package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qa.entity.Answer;
import com.example.qa.entity.Question;
import com.example.qa.entity.User;
import com.example.qa.entity.Vote;
import com.example.qa.mapper.AnswerMapper;
import com.example.qa.mapper.QuestionMapper;
import com.example.qa.mapper.UserMapper;
import com.example.qa.mapper.VoteMapper;
import com.example.qa.service.NotificationService;
import com.example.qa.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class VoteServiceImpl implements VoteService {


    
    @Autowired
    private VoteMapper voteMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private AnswerMapper answerMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public Vote vote(String targetType, Long targetId, Long userId, Integer voteType) {
        if (!"QUESTION".equals(targetType) && !"ANSWER".equals(targetType) && !"COMMENT".equals(targetType)) {
            throw new RuntimeException("无效的目标类型");
        }
        
        if (voteType != 1 && voteType != -1) {
            throw new RuntimeException("无效的点赞类型");
        }
        
        Vote existingVote = voteMapper.findByTargetAndUser(targetType, targetId, userId);
        if (existingVote != null) {
            if (existingVote.getVoteType().equals(voteType)) {
                voteMapper.deleteById(existingVote.getId());
                updateVoteCount(targetType, targetId, -voteType);
                return null;
            } else {
                int delta = voteType * 2;
                existingVote.setVoteType(voteType);
                voteMapper.updateById(existingVote);
                updateVoteCount(targetType, targetId, delta);
                return existingVote;
            }
        }
        
        Vote vote = new Vote();
        vote.setTargetType(targetType);
        vote.setTargetId(targetId);
        vote.setUserId(userId);
        vote.setVoteType(voteType);
        vote.setCreatedAt(new Date());
        
        voteMapper.insert(vote);
        updateVoteCount(targetType, targetId, voteType);
        
        if (voteType == 1) {
            createVoteNotification(targetType, targetId, userId);
        }
        
        return vote;
    }
    
    private void createVoteNotification(String targetType, Long targetId, Long fromUserId) {
        User fromUser = userMapper.selectById(fromUserId);
        String fromUsername = fromUser != null ? fromUser.getUsername() : "用户";
        
        if ("QUESTION".equals(targetType)) {
            Question question = questionMapper.selectById(targetId);
            if (question != null && !question.getUserId().equals(fromUserId)) {
                notificationService.createNotification(
                    question.getUserId(),
                    targetId,
                    null,
                    "LIKE",
                    "问题获得点赞",
                    fromUsername + " 赞了你的问题：「" + truncate(question.getTitle(), 30) + "」",
                    fromUserId,
                    fromUsername
                );
            }
        } else if ("ANSWER".equals(targetType)) {
            Answer answer = answerMapper.selectById(targetId);
            if (answer != null && !answer.getUserId().equals(fromUserId)) {
                Question question = questionMapper.selectById(answer.getQuestionId());
                String questionTitle = question != null ? question.getTitle() : "问题";
                notificationService.createNotification(
                    answer.getUserId(),
                    answer.getQuestionId(),
                    targetId,
                    "LIKE",
                    "回答获得点赞",
                    fromUsername + " 赞了你在「" + truncate(questionTitle, 20) + "」中的回答",
                    fromUserId,
                    fromUsername
                );
            }
        }
    }
    
    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen) + "..." : str;
    }
    
    private void updateVoteCount(String targetType, Long targetId, int delta) {
        if ("QUESTION".equals(targetType)) {
            Question question = questionMapper.selectById(targetId);
            if (question != null) {
                question.setVoteCount((question.getVoteCount() != null ? question.getVoteCount() : 0) + delta);
                questionMapper.updateById(question);
            }
        } else if ("ANSWER".equals(targetType)) {
            Answer answer = answerMapper.selectById(targetId);
            if (answer != null) {
                answer.setVoteCount((answer.getVoteCount() != null ? answer.getVoteCount() : 0) + delta);
                answerMapper.updateById(answer);
            }
        }
    }
    
    @Override
    @Transactional
    public void cancelVote(Long id, Long userId) {
        Vote vote = voteMapper.selectById(id);
        if (vote == null) {
            throw new RuntimeException("点赞记录不存在");
        }
        
        if (!vote.getUserId().equals(userId)) {
            throw new RuntimeException("无权限取消此点赞/踩");
        }
        
        voteMapper.deleteById(id);
        updateVoteCount(vote.getTargetType(), vote.getTargetId(), -1);
    }
    
    @Override
    public com.example.qa.entity.Vote getVoteByTargetAndUser(String targetType, Long targetId, Long userId) {
        return voteMapper.findByTargetAndUser(targetType, targetId, userId);
    }
}