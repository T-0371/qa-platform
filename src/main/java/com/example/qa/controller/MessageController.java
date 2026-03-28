package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Message;
import com.example.qa.entity.PointsConfig;
import com.example.qa.entity.User;
import com.example.qa.service.MessageService;
import com.example.qa.service.PointsConfigService;
import com.example.qa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@Tag(name = "消息管理", description = "用户聊天消息相关接口")
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
    @Operation(summary = "获取与指定用户的聊天记录", description = "获取当前用户与指定用户的所有聊天记录")
    public ApiResponse<List<Message>> getConversation(
            @PathVariable Long userId,
            @RequestParam(required = false) Long currentUserId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, currentUserId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        List<Message> messages = messageService.getConversation(id, userId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最近的聊天记录", description = "获取当前用户最近的聊天记录")
    public ApiResponse<List<Message>> getRecentConversations(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        List<Message> messages = messageService.getRecentConversations(id);
        return ApiResponse.success(messages);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取指定用户的未读消息数量", description = "获取指定用户发送给当前用户的未读消息数量")
    public ApiResponse<Long> getUnreadCountFromSender(
            @RequestParam Long userId,
            @RequestParam Long senderId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        Long count = messageService.getUnreadCountFromSender(senderId, id);
        return ApiResponse.success(count);
    }
    
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息数量", description = "获取当前用户的未读消息数量")
    public ApiResponse<Map<String, Long>> getUnreadCount(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        Long count = messageService.getUnreadCount(id);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ApiResponse.success(result);
    }

    @PostMapping("/read/{senderId}")
    @Operation(summary = "标记消息为已读", description = "将指定用户发送给当前用户的消息标记为已读")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long senderId,
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        messageService.markAsRead(senderId, id);
        
        // 发送已读通知给消息发送者
        Message readNotification = new Message();
        readNotification.setContent("__READ__");
        readNotification.setSenderId(id);
        readNotification.setReceiverId(senderId);
        messagingTemplate.convertAndSend("/user/" + senderId + "/queue/messages", readNotification);
        
        return ApiResponse.success(null);
    }
    
    @GetMapping("/check-contact/{userId}")
    @Operation(summary = "检查与用户的联系是否存在", description = "检查当前用户与指定用户是否已有联系")
    public ApiResponse<Map<String, Object>> checkContact(
            @PathVariable Long userId,
            @RequestParam(required = false) Long currentUserId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, currentUserId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        boolean exists = messageService.checkContactExists(id, userId);
        
        // 获取聊天通道的创建者（第一个发送消息的人）
        Long initiatorId = null;
        if (exists) {
            List<Message> messages = messageService.getConversation(id, userId);
            if (messages != null && !messages.isEmpty()) {
                initiatorId = messages.get(0).getSenderId();
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("points", userService.getUserById(id).getPoints());
        result.put("initiatorId", initiatorId);
        return ApiResponse.success(result);
    }
    
    @PostMapping("/create-contact/{userId}")
    @Operation(summary = "创建与用户的联系", description = "创建与指定用户的联系并扣除相应积分")
    public ApiResponse<Map<String, Object>> createContact(
            @PathVariable Long userId,
            @RequestParam(required = false) Long currentUserId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, currentUserId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 从积分配置中获取聊天消耗积分
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int firstChatCost = config != null && config.getFirstChatCost() != null ? config.getFirstChatCost() : 10;
        int repeatChatCost = config != null && config.getRepeatChatCost() != null ? config.getRepeatChatCost() : 5;
        
        boolean exists = messageService.checkContactExists(id, userId);
        int pointsNeeded = exists ? repeatChatCost : firstChatCost;
        
        if (!userService.checkPoints(id, pointsNeeded)) {
            return ApiResponse.error(400, "积分不足，无法创建联系");
        }
        
        boolean success = userService.deductPoints(id, pointsNeeded);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("points", userService.getUserById(id).getPoints());
        result.put("pointsDeducted", pointsNeeded);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
        response.setMessage("联系创建成功，消耗" + pointsNeeded + "积分");
        return response;
    }
    
    @PostMapping("/send-message")
    @Operation(summary = "发送消息并扣除积分", description = "发送消息并扣除积分")
    public ApiResponse<Map<String, Object>> sendMessage(
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long senderId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, senderId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 从积分配置中获取发送消息消耗积分
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int messageCost = config != null && config.getMessageCost() != null ? config.getMessageCost() : 1;
        
        if (!userService.checkPoints(id, messageCost)) {
            return ApiResponse.error(400, "积分不足，无法发送消息");
        }
        
        boolean success = userService.deductPoints(id, messageCost);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("points", userService.getUserById(id).getPoints());
        result.put("pointsDeducted", messageCost);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
        response.setMessage("消息发送成功，消耗" + messageCost + "积分");
        return response;
    }
    
    @GetMapping("/user/points")
    @Operation(summary = "获取用户积分", description = "获取当前用户的积分")
    public ApiResponse<Map<String, Object>> getUserPoints(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        User user = userService.getUserById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("points", user.getPoints());
        result.put("username", user.getUsername());
        return ApiResponse.success(result);
    }
    
    @PostMapping("/initiate-chat/{userId}")
    @Operation(summary = "发起聊天并扣除积分", description = "当用户发起聊天时扣除积分")
    public ApiResponse<Map<String, Object>> initiateChat(
            @PathVariable Long userId,
            @RequestParam Long receiverId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int firstChatCost = config != null && config.getFirstChatCost() != null ? config.getFirstChatCost() : 5;
        int repeatChatCost = config != null && config.getRepeatChatCost() != null ? config.getRepeatChatCost() : 3;
        
        boolean exists = messageService.checkContactExists(id, receiverId);
        int pointsNeeded = exists ? repeatChatCost : firstChatCost;
        
        if (!userService.checkPoints(id, pointsNeeded)) {
            return ApiResponse.error(400, "积分不足，无法发起聊天");
        }
        
        boolean success = userService.deductPoints(id, pointsNeeded);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("points", userService.getUserById(id).getPoints());
        result.put("pointsDeducted", pointsNeeded);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
        response.setMessage("发起聊天成功，消耗" + pointsNeeded + "积分");
        return response;
    }
    
    @PostMapping("/receive-contact/{userId}")
    @Operation(summary = "接收联系并增加积分", description = "当用户接收联系时增加积分")
    public ApiResponse<Map<String, Object>> receiveContact(
            @PathVariable Long userId, // 消息发送者ID
            @RequestParam(required = false) Long currentUserId, // 当前用户ID（接收者）
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, currentUserId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 从积分配置中获取接收联系获得的积分
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int firstContactReward = config != null && config.getFirstContactReward() != null ? config.getFirstContactReward() : 10;
        int repeatContactReward = config != null && config.getRepeatContactReward() != null ? config.getRepeatContactReward() : 5;
        
        boolean exists = messageService.checkContactExists(id, userId);
        int pointsToAdd = exists ? repeatContactReward : firstContactReward;
        
        userService.addPoints(id, pointsToAdd);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("points", userService.getUserById(id).getPoints());
        result.put("pointsAdded", pointsToAdd);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
        response.setMessage("成功接收联系，获得" + pointsToAdd + "积分");
        return response;
    }
    
    @PostMapping("/reply-reward")
    @Operation(summary = "回复消息获得积分", description = "当用户回复消息时获得积分")
    public ApiResponse<Map<String, Object>> replyReward(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long currentUserId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, currentUserId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        // 从积分配置中获取回复消息获得的积分
        PointsConfig config = pointsConfigService.getCurrentConfig();
        int replyReward = config != null && config.getReplyReward() != null ? config.getReplyReward() : 1;
        
        userService.addPoints(id, replyReward);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("points", userService.getUserById(id).getPoints());
        result.put("pointsAdded", replyReward);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
        response.setMessage("回复消息成功，获得" + replyReward + "积分");
        return response;
    }
}