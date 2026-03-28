package com.example.qa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/status")
    public void sendStatus(@Payload Map<String, Object> statusMessage) {
        // 更新用户状态到全局缓存
        if (statusMessage.containsKey("userId") && statusMessage.containsKey("status")) {
            Long userId = Long.parseLong(statusMessage.get("userId").toString());
            String status = statusMessage.get("status").toString();
            Long chatWithUserId = null;
            if (statusMessage.containsKey("chatWithUserId") && statusMessage.get("chatWithUserId") != null) {
                chatWithUserId = Long.parseLong(statusMessage.get("chatWithUserId").toString());
            }
            UserStatusController.updateUserStatus(userId, status, chatWithUserId);
        }
        
        // 广播状态消息给所有用户
        messagingTemplate.convertAndSend("/topic/status", statusMessage);
    }
}