package com.example.qa.service;

import com.example.qa.entity.Notification;
import java.util.List;

public interface NotificationService {
    void createNotification(Long userId, Long questionId, Long answerId, String type, String title, String content, Long fromUserId, String fromUsername);
    List<Notification> getUnreadNotifications(Long userId);
    List<Notification> getAllNotifications(Long userId);
    List<Notification> getNotificationsByType(Long userId, String type);
    int getUnreadCount(Long userId);
    void markAllAsRead(Long userId);
    void markAsRead(Long id);
    void deleteNotification(Long id);
    void deleteAllNotifications(Long userId);
}
