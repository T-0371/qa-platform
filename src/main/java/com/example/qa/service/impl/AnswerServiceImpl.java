package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qa.dto.request.AnswerCreateRequest;
import com.example.qa.entity.Answer;
import com.example.qa.entity.Question;
import com.example.qa.entity.User;
import com.example.qa.entity.PointsConfig;
import com.example.qa.mapper.AnswerMapper;
import com.example.qa.mapper.QuestionMapper;
import com.example.qa.mapper.UserMapper;
import com.example.qa.service.AnswerService;
import com.example.qa.service.NotificationService;
import com.example.qa.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {
    
    @Autowired
    private AnswerMapper answerMapper;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private PointsConfigService pointsConfigService;
    
    /**
     * 获取所有回答
     * @return 所有回答列表
     */
    @Override
    public List<Answer> getAllAnswers() {
        return answerMapper.selectList(null);
    }
    
    /**
     * 创建回答
     * @param questionId 问题ID
     * @param request 回答创建请求
     * @param userId 用户ID
     * @return 创建成功的回答信息
     */
    @Override
    public Answer createAnswer(Long questionId, AnswerCreateRequest request, Long userId) {
        // 检查问题是否存在
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        
        // 创建回答对象
        Answer answer = Answer.builder().build();
        answer.setQuestionId(questionId);
        answer.setUserId(userId);
        answer.setContent(request.getContent());
        answer.setVoteCount(0);
        answer.setIsAccepted(false);
        answer.setCreatedAt(new Date());
        answer.setUpdatedAt(new Date());
        
        // 保存回答
        answerMapper.insert(answer);
        
        // 更新问题的回答数
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionMapper.updateById(question);
        
        // 创建通知
        if (!question.getUserId().equals(userId)) {
            User fromUser = userMapper.selectById(userId);
            String fromUsername = fromUser != null ? fromUser.getUsername() : "用户";
            notificationService.createNotification(
                question.getUserId(),
                questionId,
                answer.getId(),
                "ANSWER",
                "问题有新回答",
                fromUsername + " 回答了你的问题：「" + truncate(question.getTitle(), 30) + "」",
                userId,
                fromUsername
            );
        }
        
        // 回答奖励积分（从配置中读取）
        User answerUser = userMapper.selectById(userId);
        if (answerUser != null) {
            PointsConfig config = pointsConfigService.getCurrentConfig();
            int rewardPoints = config != null && config.getAnswerReward() != null ? config.getAnswerReward() : 5;
            answerUser.setPoints(answerUser.getPoints() + rewardPoints);
            userMapper.updateById(answerUser);
            System.out.println("=== 用户 " + answerUser.getUsername() + " 回答问题获得 " + rewardPoints + " 积分 ===");
        }
        
        return answer;
    }
    
    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen) + "..." : str;
    }
    
    /**
     * 获取问题的回答列表
     * @param questionId 问题ID
     * @param page 页码
     * @param size 每页大小
     * @return 回答列表
     */
    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId, int page, int size) {
        IPage<Answer> answerPage = new Page<>(page, size);
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, questionId);
        wrapper.orderByDesc(Answer::getIsAccepted);
        wrapper.orderByDesc(Answer::getVoteCount);
        wrapper.orderByDesc(Answer::getCreatedAt);
        answerMapper.selectPage(answerPage, wrapper);
        
        List<Answer> answers = answerPage.getRecords();
        for (Answer answer : answers) {
            loadAnswerUserName(answer);
        }
        return answers;
    }
    
    private void loadAnswerUserName(Answer answer) {
        User user = userMapper.selectById(answer.getUserId());
        if (user != null) {
            answer.setUserName(user.getUsername());
            answer.setAvatar(user.getAvatar());
        }
    }
    
    /**
     * 根据ID获取回答详情
     * @param id 回答ID
     * @return 回答详情
     */
    @Override
    public Answer getAnswerById(Long id) {
        return answerMapper.selectById(id);
    }
    
    /**
     * 更新回答
     * @param id 回答ID
     * @param request 回答更新请求
     * @param userId 用户ID
     * @return 更新后的回答信息
     */
    @Override
    public Answer updateAnswer(Long id, AnswerCreateRequest request, Long userId) {
        // 获取回答
        Answer answer = answerMapper.selectById(id);
        if (answer == null) {
            throw new RuntimeException("回答不存在");
        }
        
        // 检查权限
        if (!answer.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此回答");
        }
        
        // 更新回答
        answer.setContent(request.getContent());
        answer.setUpdatedAt(new Date());
        answerMapper.updateById(answer);
        
        return answer;
    }
    
    /**
     * 删除回答
     * @param id 回答ID
     * @param userId 用户ID
     */
    @Override
    public void deleteAnswer(Long id, Long userId) {
        // 获取回答
        Answer answer = answerMapper.selectById(id);
        if (answer == null) {
            throw new RuntimeException("回答不存在");
        }
        
        // 检查权限
        if (!answer.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此回答");
        }
        
        // 获取问题
        Question question = questionMapper.selectById(answer.getQuestionId());
        
        // 删除回答
        answerMapper.deleteById(id);
        
        // 更新问题的回答数
        if (question != null) {
            question.setAnswerCount(Math.max(0, question.getAnswerCount() - 1));
            questionMapper.updateById(question);
        }
    }
    
    /**
     * 采纳回答
     * @param id 回答ID
     * @param questionId 问题ID
     * @param userId 用户ID
     * @return 采纳后的回答信息
     */
    @Override
    public Answer acceptAnswer(Long id, Long questionId, Long userId) {
        // 获取问题
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        
        // 检查权限
        if (!question.getUserId().equals(userId)) {
            throw new RuntimeException("只有问题作者可以采纳回答");
        }
        
        // 获取回答
        Answer answer = answerMapper.selectById(id);
        if (answer == null) {
            throw new RuntimeException("回答不存在");
        }
        
        // 检查回答是否属于该问题
        if (!answer.getQuestionId().equals(questionId)) {
            throw new RuntimeException("回答不属于该问题");
        }
        
        // 取消其他回答的采纳状态
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, questionId);
        wrapper.eq(Answer::getIsAccepted, true);
        List<Answer> acceptedAnswers = answerMapper.selectList(wrapper);
        for (Answer acceptedAnswer : acceptedAnswers) {
            acceptedAnswer.setIsAccepted(false);
            answerMapper.updateById(acceptedAnswer);
        }
        
        // 设置当前回答为采纳状态
        answer.setIsAccepted(true);
        answer.setUpdatedAt(new Date());
        answerMapper.updateById(answer);
        
        // 更新问题状态
        question.setStatus("CLOSED");
        questionMapper.updateById(question);
        
        return answer;
    }
}