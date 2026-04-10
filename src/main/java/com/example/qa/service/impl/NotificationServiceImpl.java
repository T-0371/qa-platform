package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.qa.entity.Notification;
import com.example.qa.mapper.NotificationMapper;
import com.example.qa.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `notification` (" +
                "`id` BIGINT NOT NULL AUTO_INCREMENT," +
                "`user_id` BIGINT NOT NULL COMMENT '接收通知的用户ID'," +
                "`question_id` BIGINT NULL COMMENT '相关问题ID'," +
                "`answer_id` BIGINT NULL COMMENT '相关回答ID'," +
                "`type` VARCHAR(20) NOT NULL COMMENT '通知类型: LIKE/ANSWER'," +
                "`title` VARCHAR(200) NULL COMMENT '通知标题'," +
                "`content` VARCHAR(500) NULL COMMENT '通知内容'," +
                "`is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读'," +
                "`from_user_id` BIGINT NULL COMMENT '触发通知的用户ID'," +
                "`from_username` VARCHAR(50) NULL COMMENT '触发通知的用户名'," +
                "`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (`id`)," +
                "INDEX `idx_user_id` (`user_id`)," +
                "INDEX `idx_is_read` (`is_read`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表'");
        } catch (Exception e) {
            // 表已存在，忽略错误
        }
    }
    
    @Override
    public void createNotification(Long userId, Long questionId, Long answerId, String type, String title, String content, Long fromUserId, String fromUsername) {
        Notification notification = Notification.builder()
                .userId(userId)
                .questionId(questionId)
                .answerId(answerId)
                .type(type)
                .title(title)
                .content(content)
                .isRead(false)
                .fromUserId(fromUserId)
                .fromUsername(fromUsername)
                .createdAt(new Date())
                .build();
        notificationMapper.insert(notification);
    }
    
    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false)
                .orderByDesc(Notification::getCreatedAt);
        return notificationMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<Notification> getAllNotifications(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        return notificationMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<Notification> getNotificationsByType(Long userId, String type) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId);
        if (!"ALL".equalsIgnoreCase(type)) {
            queryWrapper.eq(Notification::getType, type.toUpperCase());
        }
        queryWrapper.orderByDesc(Notification::getCreatedAt);
        return notificationMapper.selectList(queryWrapper);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false);
        return Math.toIntExact(notificationMapper.selectCount(queryWrapper));
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }
    
    @Override
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    public void markAsReadBySender(Long userId, Long senderId) {
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getFromUserId, senderId)
                .eq(Notification::getIsRead, false)
                .set(Notification::getIsRead, true);
        notificationMapper.update(null, updateWrapper);
    }
    
    @Override
    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }
    
    @Override
    public void deleteAllNotifications(Long userId) {
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId);
        notificationMapper.delete(queryWrapper);
    }
}
