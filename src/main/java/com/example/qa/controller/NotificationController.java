package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Notification;
import com.example.qa.entity.User;
import com.example.qa.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ApiResponse getNotifications(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            List<Notification> notifications = notificationService.getAllNotifications(user.getId());
            return ApiResponse.success("获取成功", notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread")
    public ApiResponse getUnreadNotifications(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            List<Notification> notifications = notificationService.getUnreadNotifications(user.getId());
            return ApiResponse.success("获取成功", notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/unread/count")
    public ApiResponse getUnreadCount(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            int count = notificationService.getUnreadCount(user.getId());
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/read/all")
    public ApiResponse markAllAsRead(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            notificationService.markAllAsRead(user.getId());
            return ApiResponse.success("标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("标记失败: " + e.getMessage());
        }
    }

    @PutMapping("/read/{id}")
    public ApiResponse markAsRead(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            notificationService.markAsRead(id);
            return ApiResponse.success("标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("标记失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteNotification(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            notificationService.deleteNotification(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ApiResponse deleteAllNotifications(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            notificationService.deleteAllNotifications(user.getId());
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/type/{type}")
    public ApiResponse getNotificationsByType(@PathVariable String type, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            List<Notification> notifications = notificationService.getNotificationsByType(user.getId(), type);
            return ApiResponse.success("获取成功", notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
}