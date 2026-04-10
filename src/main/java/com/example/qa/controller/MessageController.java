package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Message;
import com.example.qa.entity.PointsConfig;
import com.example.qa.entity.User;
import com.example.qa.service.MessageService;
import com.example.qa.service.PointsConfigService;
import com.example.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private PointsConfigService pointsConfigService;

    @GetMapping("/conversation")
    public ApiResponse getMessagesBetweenUsers(@RequestParam Long otherUserId, @RequestParam(required = false) Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                if (userId == null) {
                    return ApiResponse.error("用户未登录");
                }
                user = new User();
                user.setId(userId);
            }
            List<Message> messages = messageService.getConversation(user.getId(), otherUserId);
            return ApiResponse.success("获取成功", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/contacts")
    public ApiResponse getUserContacts(@RequestParam(required = false) Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                if (userId == null) {
                    return ApiResponse.error("用户未登录");
                }
                user = new User();
                user.setId(userId);
            }
            List<Message> contacts = messageService.getRecentConversations(user.getId());
            return ApiResponse.success("获取成功", contacts);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse sendMessage(@RequestParam Long receiverId, @RequestParam String content, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Message message = new Message();
            message.setSenderId(user.getId());
            message.setReceiverId(receiverId);
            message.setContent(content);
            message.setIsRead(false);
            messageService.sendMessage(message);
            return ApiResponse.success("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发送失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread/count")
    public ApiResponse getUnreadMessageCount(@RequestParam(required = false) Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                if (userId == null) {
                    return ApiResponse.error("用户未登录");
                }
                user = new User();
                user.setId(userId);
            }
            Long count = messageService.getUnreadCount(user.getId());
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/read")
    public ApiResponse markMessagesAsRead(@RequestParam Long senderId, @RequestParam(required = false) Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                if (userId == null) {
                    return ApiResponse.error("用户未登录");
                }
                user = new User();
                user.setId(userId);
            }
            messageService.markAsRead(senderId, user.getId());
            return ApiResponse.success("标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("标记失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread/count/{senderId}")
    public ApiResponse getUnreadMessageCountFromSender(@PathVariable Long senderId, @RequestParam(required = false) Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                if (userId == null) {
                    return ApiResponse.error("用户未登录");
                }
                user = new User();
                user.setId(userId);
            }
            Long count = messageService.getUnreadCountFromSender(senderId, user.getId());
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/contact/check")
    public ApiResponse checkContactExists(@RequestParam Long otherUserId, @RequestParam(required = false) Long userId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                if (userId == null) {
                    return ApiResponse.error("用户未登录");
                }
                user = new User();
                user.setId(userId);
            }
            boolean exists = messageService.checkContactExists(user.getId(), otherUserId);
            return ApiResponse.success("检查成功", exists);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/points")
    public ApiResponse getUserPoints(@RequestParam Long userId, HttpSession session) {
        try {
            if (userId == null) {
                return ApiResponse.error("用户ID不能为空");
            }
            User targetUser = userService.getUserById(userId);
            if (targetUser == null) {
                return ApiResponse.error("用户不存在");
            }
            Map<String, Object> result = new HashMap<>();
            result.put("points", targetUser.getPoints());
            result.put("username", targetUser.getUsername());
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/conversation/{otherUserId}")
    public ApiResponse getConversationAlt(@PathVariable Long otherUserId, @RequestParam Long currentUserId) {
        try {
            List<Message> messages = messageService.getConversation(currentUserId, otherUserId);
            return ApiResponse.success("获取成功", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread-count")
    public ApiResponse getUnreadCountAlt(@RequestParam Long userId, @RequestParam Long senderId) {
        try {
            Long count = messageService.getUnreadCountFromSender(senderId, userId);
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/read/{senderId}")
    public ApiResponse markAsReadAlt(@PathVariable Long senderId, @RequestParam Long currentUserId) {
        try {
            messageService.markAsRead(senderId, currentUserId);
            return ApiResponse.success("标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("标记失败: " + e.getMessage());
        }
    }

    @PostMapping("/initiate-chat/{userId}")
    @Transactional
    public ApiResponse initiateChat(@PathVariable Long userId, @RequestParam Long receiverId) {
        try {
            User initiator = userService.getUserById(userId);
            if (initiator == null) {
                return ApiResponse.error("发起者不存在");
            }

            PointsConfig config = pointsConfigService.getCurrentConfig();
            int firstChatCost = config != null && config.getFirstChatCost() != null ? config.getFirstChatCost() : 5;
            int repeatChatCost = config != null && config.getRepeatChatCost() != null ? config.getRepeatChatCost() : 3;

            boolean exists = messageService.checkContactExists(userId, receiverId);
            int pointsNeeded = exists ? repeatChatCost : firstChatCost;

            if (!userService.checkPoints(userId, pointsNeeded)) {
                return ApiResponse.error("积分不足，无法发起聊天");
            }

            boolean success = userService.deductPoints(userId, pointsNeeded);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("points", userService.getUserById(userId).getPoints());
            result.put("pointsDeducted", pointsNeeded);
            ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
            response.setMessage("发起聊天成功，消耗" + pointsNeeded + "积分");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发起聊天失败: " + e.getMessage());
        }
    }

    @PostMapping("/send-message")
    @Transactional
    public ApiResponse sendMessageAlt(@RequestParam Long receiverId, @RequestParam Long senderId) {
        try {
            User sender = userService.getUserById(senderId);
            if (sender == null) {
                return ApiResponse.error("发送者不存在");
            }

            PointsConfig config = pointsConfigService.getCurrentConfig();
            int messageCost = config != null && config.getMessageCost() != null ? config.getMessageCost() : 1;

            if (!userService.checkPoints(senderId, messageCost)) {
                return ApiResponse.error("积分不足，无法发送消息");
            }

            boolean success = userService.deductPoints(senderId, messageCost);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("points", userService.getUserById(senderId).getPoints());
            result.put("pointsDeducted", messageCost);
            ApiResponse<Map<String, Object>> response = ApiResponse.success(result);
            response.setMessage("消息发送成功，消耗" + messageCost + "积分");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/reply-reward")
    @Transactional
    public ApiResponse replyReward(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam(required = false) Long currentUserId) {
        try {
            Long id = currentUserId != null ? currentUserId : senderId;
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }

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
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("回复奖励失败: " + e.getMessage());
        }
    }

    @PostMapping("/receive-contact/{userId}")
    @Transactional
    public ApiResponse receiveContact(@PathVariable Long userId, @RequestParam(required = false) Long currentUserId) {
        try {
            Long id = currentUserId != null ? currentUserId : userId;
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }

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
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("接收联系失败: " + e.getMessage());
        }
    }

    @GetMapping("/check-contact/{userId}")
    public ApiResponse checkContact(@PathVariable Long userId, @RequestParam(required = false) Long currentUserId) {
        try {
            Long id = currentUserId != null ? currentUserId : userId;
            boolean exists = messageService.checkContactExists(id, userId);

            Long initiatorId = null;
            if (exists) {
                List<Message> messages = messageService.getConversation(id, userId);
                if (messages != null && !messages.isEmpty()) {
                    initiatorId = messages.get(0).getSenderId();
                }
            }

            User user = userService.getUserById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            result.put("points", user != null ? user.getPoints() : 0);
            result.put("initiatorId", initiatorId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("检查联系失败: " + e.getMessage());
        }
    }
}