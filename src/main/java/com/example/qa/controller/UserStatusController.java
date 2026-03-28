package com.example.qa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class UserStatusController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // 存储用户聊天状态的全局缓存
    private static final Map<Long, Long> userChatStatus = new ConcurrentHashMap<>();
    
    // 存储用户在线状态的全局缓存
    private static final Map<Long, String> userOnlineStatus = new ConcurrentHashMap<>();

    @GetMapping("/api/user/status")
    public Map<String, Object> getUserStatus(@RequestParam Long userId) {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("online", userOnlineStatus.getOrDefault(userId, "OFFLINE"));
        status.put("chatWithUserId", userChatStatus.get(userId));
        return status;
    }
    
    @GetMapping("/api/user/chat-status")
    public Map<String, Object> getChatStatus(@RequestParam Long userId, @RequestParam Long targetUserId) {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("isChattingWithMe", userChatStatus.getOrDefault(targetUserId, 0L).equals(userId));
        status.put("targetOnlineStatus", userOnlineStatus.getOrDefault(targetUserId, "OFFLINE"));
        return status;
    }
    
    // 提供给WebSocketController调用的方法
    public static void updateUserStatus(Long userId, String status, Long chatWithUserId) {
        userOnlineStatus.put(userId, status);
        if (chatWithUserId != null) {
            userChatStatus.put(userId, chatWithUserId);
        } else {
            userChatStatus.remove(userId);
        }
    }
}