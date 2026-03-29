package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Message;
import com.example.qa.entity.PointsConfig;
import com.example.qa.entity.User;
import com.example.qa.service.MessageService;
import com.example.qa.service.PointsConfigService;
import com.example.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointsConfigService pointsConfigService;
    
    private Long getUserIdFromSessionOrParam(HttpSession session, Long userId) {
        if (userId != null) {
            return userId;
        }
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    @GetMapping("/conversation/{userId}")
    public ApiResponse getConversation(@PathVariable Long userId, HttpSession session) {
        try {
            Long currentUserId = getUserIdFromSessionOrParam(session, null);
            if (currentUserId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            List<Message> messages = messageService.getMessagesBetweenUsers(currentUserId, userId);
            return ApiResponse.success("获取聊天记录成功", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取聊天记录失败: " + e.getMessage());
        }
    }

    @GetMapping("/contacts")
    public ApiResponse getContacts(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            List<User> contacts = messageService.getUserContacts(user.getId());
            return ApiResponse.success("获取联系人列表成功", contacts);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取联系人列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/send")
    public ApiResponse sendMessage(@RequestParam Long recipientId, @RequestParam String content, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Message message = messageService.sendMessage(user.getId(), recipientId, content);
            
            // 发送WebSocket消息
            Map<String, Object> webSocketMessage = new HashMap<>();
            webSocketMessage.put("type", "newMessage");
            webSocketMessage.put("message", message);
            messagingTemplate.convertAndSend("/topic/user/" + recipientId, webSocketMessage);
            
            return ApiResponse.success("发送消息成功", message);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread/count")
    public ApiResponse getUnreadMessageCount(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            int count = messageService.getUnreadMessageCount(user.getId());
            return ApiResponse.success("获取未读消息数成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取未读消息数失败: " + e.getMessage());
        }
    }

    @PutMapping("/read/{userId}")
    public ApiResponse markAsRead(@PathVariable Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            messageService.markMessagesAsRead(user.getId(), userId);
            return ApiResponse.success("标记已读成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("标记已读失败: " + e.getMessage());
        }
    }

    @GetMapping("/points/config")
    public ApiResponse getPointsConfig() {
        try {
            PointsConfig config = pointsConfigService.getPointsConfig();
            return ApiResponse.success("获取积分配置成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取积分配置失败: " + e.getMessage());
        }
    }

    @PostMapping("/points/check")
    public ApiResponse checkPoints(@RequestParam Long targetUserId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Map<String, Object> result = messageService.checkPoints(user.getId(), targetUserId);
            return ApiResponse.success("检查积分成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("检查积分失败: " + e.getMessage());
        }
    }

    @PostMapping("/points/consume")
    public ApiResponse consumePoints(@RequestParam Long targetUserId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Map<String, Object> result = messageService.consumePoints(user.getId(), targetUserId);
            return ApiResponse.success("消耗积分成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("消耗积分失败: " + e.getMessage());
        }
    }
}