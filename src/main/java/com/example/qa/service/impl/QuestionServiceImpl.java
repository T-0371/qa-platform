package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qa.dto.request.QuestionCreateRequest;
import com.example.qa.entity.Question;
import com.example.qa.entity.QuestionTag;
import com.example.qa.entity.User;
import com.example.qa.mapper.QuestionMapper;
import com.example.qa.mapper.QuestionTagMapper;
import com.example.qa.mapper.AnswerMapper;
import com.example.qa.mapper.CommentMapper;
import com.example.qa.mapper.VoteMapper;
import com.example.qa.mapper.CollectMapper;
import com.example.qa.mapper.UserMapper;
import com.example.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 问题服务实现类
 * 实现问题相关的业务逻辑
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private QuestionTagMapper questionTagMapper;
    
    @Autowired
    private AnswerMapper answerMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private VoteMapper voteMapper;
    
    @Autowired
    private CollectMapper collectMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 创建问题
     * @param request 问题创建请求
     * @param userId 用户ID
     * @return 创建成功的问题信息
     */
    @Override
    @Transactional
    public Question createQuestion(QuestionCreateRequest request, Long userId) {
        // 创建问题对象
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setUserId(userId);
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setVoteCount(0);
        question.setStatus("OPEN");
        question.setCreatedAt(new Date());
        question.setUpdatedAt(new Date());
        
        // 保存问题
        questionMapper.insert(question);
        
        // 处理标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(question.getId());
                questionTag.setTagId(tagId);
                questionTagMapper.insert(questionTag);
            }
        }
        
        return question;
    }
    
    /**
     * 获取问题列表
     * @param page 页码
     * @param size 每页大小
     * @return 问题列表
     */
    @Override
    public List<Question> getQuestionList(int page, int size) {
        IPage<Question> questionPage = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Question::getCreatedAt);
        questionMapper.selectPage(questionPage, wrapper);
        
        List<Question> questions = questionPage.getRecords();
        for (Question question : questions) {
            loadQuestionTags(question);
            loadQuestionUserName(question);
        }
        return questions;
    }
    
    private void loadQuestionUserName(Question question) {
        User user = userMapper.selectById(question.getUserId());
        if (user != null) {
            question.setUserName(user.getUsername());
            question.setAvatar(user.getAvatar());
        }
    }
    
    private void loadQuestionTags(Question question) {
        LambdaQueryWrapper<QuestionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionTag::getQuestionId, question.getId());
        List<QuestionTag> questionTags = questionTagMapper.selectList(wrapper);
        
        List<Long> tagIds = new java.util.ArrayList<>();
        for (QuestionTag qt : questionTags) {
            tagIds.add(qt.getTagId());
        }
        question.setTagIds(tagIds);
    }
    
    /**
     * 根据ID获取问题详情
     * @param id 问题ID
     * @return 问题详情
     */
    @Override
    public Question getQuestionById(Long id) {
        // 获取问题
        Question question = questionMapper.selectById(id);
        if (question != null) {
            // 增加浏览量
            question.setViewCount(question.getViewCount() + 1);
            questionMapper.updateById(question);
            // 加载用户名
            loadQuestionUserName(question);
        }
        return question;
    }
    
    /**
     * 根据ID获取问题详情（不增加浏览量）
     * @param id 问题ID
     * @return 问题详情
     */
    @Override
    public Question getQuestionByIdWithoutViewCount(Long id) {
        // 获取问题（不增加浏览量）
        return questionMapper.selectById(id);
    }
    
    /**
     * 更新问题
     * @param id 问题ID
     * @param request 问题更新请求
     * @param userId 用户ID
     * @return 更新后的问题信息
     */
    @Override
    @Transactional
    public Question updateQuestion(Long id, QuestionCreateRequest request, Long userId) {
        // 获取问题
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        
        // 检查权限（管理员可以修改所有问题）
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null && !user.getRole().equals("ADMIN") && !question.getUserId().equals(userId)) {
                throw new RuntimeException("无权限修改此问题");
            }
        }
        
        // 更新问题
        if (request.getTitle() != null) {
            question.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }
        if (request.getStatus() != null) {
            question.setStatus(request.getStatus());
        }
        question.setUpdatedAt(new Date());
        questionMapper.updateById(question);
        
        // 更新标签关联
        if (request.getTagIds() != null) {
            // 删除旧的标签关联
            LambdaQueryWrapper<QuestionTag> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(QuestionTag::getQuestionId, id);
            questionTagMapper.delete(deleteWrapper);
            
            // 添加新的标签关联
            for (Long tagId : request.getTagIds()) {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(id);
                questionTag.setTagId(tagId);
                questionTagMapper.insert(questionTag);
            }
        }
        
        return question;
    }
    
    /**
     * 删除问题
     * @param id 问题ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        // 获取问题
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        
        // 检查权限（管理员可以删除所有问题）
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null && !user.getRole().equals("ADMIN") && !question.getUserId().equals(userId)) {
                throw new RuntimeException("无权限删除此问题");
            }
        }
        
        // 删除问题标签关联
        LambdaQueryWrapper<QuestionTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(QuestionTag::getQuestionId, id);
        questionTagMapper.delete(tagWrapper);
        
        // 删除相关的回答
        LambdaQueryWrapper<com.example.qa.entity.Answer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(com.example.qa.entity.Answer::getQuestionId, id);
        List<com.example.qa.entity.Answer> answers = answerMapper.selectList(answerWrapper);
        for (com.example.qa.entity.Answer answer : answers) {
            // 删除回答的评论
            LambdaQueryWrapper<com.example.qa.entity.Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.eq(com.example.qa.entity.Comment::getTargetType, "ANSWER");
            commentWrapper.eq(com.example.qa.entity.Comment::getTargetId, answer.getId());
            commentMapper.delete(commentWrapper);
            
            // 删除回答的点赞
            LambdaQueryWrapper<com.example.qa.entity.Vote> voteWrapper = new LambdaQueryWrapper<>();
            voteWrapper.eq(com.example.qa.entity.Vote::getTargetType, "ANSWER");
            voteWrapper.eq(com.example.qa.entity.Vote::getTargetId, answer.getId());
            voteMapper.delete(voteWrapper);
        }
        
        // 删除回答
        answerMapper.delete(answerWrapper);
        
        // 删除问题的点赞
        LambdaQueryWrapper<com.example.qa.entity.Vote> questionVoteWrapper = new LambdaQueryWrapper<>();
        questionVoteWrapper.eq(com.example.qa.entity.Vote::getTargetType, "QUESTION");
        questionVoteWrapper.eq(com.example.qa.entity.Vote::getTargetId, id);
        voteMapper.delete(questionVoteWrapper);
        
        // 删除问题的收藏
        LambdaQueryWrapper<com.example.qa.entity.Collect> collectWrapper = new LambdaQueryWrapper<>();
        collectWrapper.eq(com.example.qa.entity.Collect::getQuestionId, id);
        collectMapper.delete(collectWrapper);
        
        // 删除问题
        questionMapper.deleteById(id);
    }
    
    /**
     * 获取热门问题
     * @param limit 数量限制
     * @return 热门问题列表
     */
    @Override
    public List<Question> getHotQuestions(int limit) {
        return questionMapper.findHotQuestions(limit);
    }
    
    /**
     * 获取最新问题
     * @param limit 数量限制
     * @return 最新问题列表
     */
    @Override
    public List<Question> getLatestQuestions(int limit) {
        List<Question> questions = questionMapper.findLatestQuestions(limit);
        for (Question question : questions) {
            loadQuestionUserName(question);
        }
        return questions;
    }
    
    /**
     * 根据标签获取问题
     * @param tagId 标签ID
     * @param page 页码
     * @param size 每页大小
     * @return 问题列表
     */
    @Override
    public List<Question> getQuestionsByTagId(Long tagId, int page, int size) {
        List<Long> questionIds = questionTagMapper.findQuestionIdsByTagId(tagId);
        if (questionIds == null || questionIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        IPage<Question> questionPage = new Page<>(page, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Question::getId, questionIds);
        wrapper.orderByDesc(Question::getCreatedAt);
        questionMapper.selectPage(questionPage, wrapper);
        return questionPage.getRecords();
    }
    
    /**
     * 根据配置获取热门问题
     */
    @Override
    public List<Question> getHotQuestionsByConfig(Integer timeRangeDays, Integer minViewCount, 
                                                   Integer minAnswerCount, Integer minVoteCount, 
                                                   String sortBy, Integer limit) {
        List<Question> questions = questionMapper.findHotQuestionsByConfig(
            timeRangeDays, minViewCount, minAnswerCount, minVoteCount, sortBy, limit);
        for (Question question : questions) {
            loadQuestionTags(question);
            loadQuestionUserName(question);
        }
        return questions;
    }
    
    /**
     * 搜索问题
     */
    @Override
    public List<Question> searchQuestions(String keyword, int page, int size) {
        List<Question> questions = questionMapper.searchQuestions(keyword);
        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, questions.size());
        if (start >= questions.size()) {
            return new java.util.ArrayList<>();
        }
        List<Question> pageQuestions = questions.subList(start, end);
        for (Question question : pageQuestions) {
            loadQuestionTags(question);
            loadQuestionUserName(question);
        }
        return pageQuestions;
    }
}