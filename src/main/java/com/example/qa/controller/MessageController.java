package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Message;
import com.example.qa.entity.User;
import com.example.qa.service.MessageService;
import com.example.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

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
            return ApiResponse.success("获取成功", targetUser.getPoints());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
}