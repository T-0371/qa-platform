package com.example.qa.service;

import com.example.qa.entity.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getConversation(Long userId1, Long userId2);
    List<Message> getRecentConversations(Long userId);
    void markAsRead(Long senderId, Long receiverId);
    Long getUnreadCount(Long userId);
    Long getUnreadCountFromSender(Long senderId, Long receiverId);
    boolean checkContactExists(Long userId1, Long userId2);
}