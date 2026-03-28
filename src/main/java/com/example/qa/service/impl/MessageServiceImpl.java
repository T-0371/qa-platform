package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.qa.entity.Message;
import com.example.qa.entity.Question;
import com.example.qa.mapper.MessageMapper;
import com.example.qa.service.MessageService;
import com.example.qa.service.NotificationService;
import com.example.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private QuestionService questionService;

    @Override
    public Message sendMessage(Message message) {
        message.setCreatedAt(new Date());
        message.setIsRead(false);
        messageMapper.insert(message);
        
        Message savedMessage = messageMapper.selectById(message.getId());
        
        String notificationContent;
        Long notificationQuestionId = savedMessage.getQuestionId();
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeStr = sdf.format(message.getCreatedAt());
        
        if (notificationQuestionId != null) {
            Question question = questionService.getQuestionById(notificationQuestionId);
            String questionTitle = question != null ? question.getTitle() : "未知问题";
            if (questionTitle.length() > 20) {
                questionTitle = questionTitle.substring(0, 20) + "...";
            }
            notificationContent = "在[" + questionTitle + "]中，" + message.getSenderUsername() + "于" + timeStr + "发起了聊天：" + message.getContent();
        } else {
            notificationContent = message.getSenderUsername() + "于" + timeStr + "发起了聊天：" + message.getContent();
        }
        
        notificationService.createNotification(
            message.getReceiverId(),
            notificationQuestionId,
            null,
            "CHAT",
            "新消息",
            notificationContent,
            message.getSenderId(),
            message.getSenderUsername()
        );
        
        return savedMessage;
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageMapper.getConversation(userId1, userId2);
    }

    @Override
    public List<Message> getRecentConversations(Long userId) {
        return messageMapper.getRecentConversations(userId);
    }

    @Override
    public void markAsRead(Long senderId, Long receiverId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender_id", senderId);
        queryWrapper.eq("receiver_id", receiverId);
        queryWrapper.eq("is_read", false);
        
        Message message = new Message();
        message.setIsRead(true);
        messageMapper.update(message, queryWrapper);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId);
        queryWrapper.eq("is_read", false);
        return messageMapper.selectCount(queryWrapper);
    }
    
    @Override
    public Long getUnreadCountFromSender(Long senderId, Long receiverId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender_id", senderId);
        queryWrapper.eq("receiver_id", receiverId);
        queryWrapper.eq("is_read", false);
        return messageMapper.selectCount(queryWrapper);
    }
    
    @Override
    public boolean checkContactExists(Long userId1, Long userId2) {
        List<Message> messages = messageMapper.getConversation(userId1, userId2);
        return !messages.isEmpty();
    }
}