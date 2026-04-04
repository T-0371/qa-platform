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
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private PointsConfigService pointsConfigService;

    @GetMapping("/conversation")
    public ApiResponse getMessagesBetweenUsers(@RequestParam Long otherUserId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            List<Message> messages = messageService.getConversation(user.getId(), otherUserId);
            return ApiResponse.success("获取成功", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/contacts")
    public ApiResponse getUserContacts(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
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
    public ApiResponse getUnreadMessageCount(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Long count = messageService.getUnreadCount(user.getId());
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/read")
    public ApiResponse markMessagesAsRead(@RequestParam Long senderId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            messageService.markAsRead(senderId, user.getId());
            return ApiResponse.success("标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("标记失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread/count/{senderId}")
    public ApiResponse getUnreadMessageCountFromSender(@PathVariable Long senderId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Long count = messageService.getUnreadCountFromSender(senderId, user.getId());
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/contact/check")
    public ApiResponse checkContactExists(@RequestParam Long otherUserId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
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
            int cost = config.getFirstChatCost() != null ? config.getFirstChatCost() : 2;

            if (initiator.getPoints() < cost) {
                return ApiResponse.error("积分不足，无法发起聊天");
            }

            initiator.setPoints(initiator.getPoints() - cost);
            userService.updateUser(initiator);

            Map<String, Object> result = new HashMap<>();
            result.put("pointsDeducted", cost);
            result.put("remainingPoints", initiator.getPoints());
            return ApiResponse.success("发起聊天成功", result);
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
            int cost = config.getRepeatChatCost() != null ? config.getRepeatChatCost() : 1;

            if (sender.getPoints() < cost) {
                return ApiResponse.error("积分不足，无法发送消息");
            }

            sender.setPoints(sender.getPoints() - cost);
            userService.updateUser(sender);

            Map<String, Object> result = new HashMap<>();
            result.put("pointsDeducted", cost);
            result.put("remainingPoints", sender.getPoints());
            return ApiResponse.success("消息发送成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/reply-reward")
    @Transactional
    public ApiResponse replyReward(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam Long currentUserId) {
        try {
            User user = userService.getUserById(currentUserId);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }

            PointsConfig config = pointsConfigService.getCurrentConfig();
            int reward = config.getReplyReward() != null ? config.getReplyReward() : 1;

            user.setPoints(user.getPoints() + reward);
            userService.updateUser(user);

            Map<String, Object> result = new HashMap<>();
            result.put("pointsAdded", reward);
            result.put("remainingPoints", user.getPoints());
            return ApiResponse.success("回复奖励成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("回复奖励失败: " + e.getMessage());
        }
    }
}