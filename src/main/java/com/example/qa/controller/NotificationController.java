package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Notification;
import com.example.qa.entity.User;
import com.example.qa.service.NotificationService;
import com.example.qa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "消息通知", description = "消息通知接口")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
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
    
    @GetMapping
    @Operation(summary = "获取所有通知", description = "获取当前用户的所有通知")
    public ApiResponse<List<Notification>> getAllNotifications(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        List<Notification> notifications = notificationService.getAllNotifications(id);
        return ApiResponse.success(notifications);
    }
    
    @GetMapping("/unread")
    @Operation(summary = "获取未读通知", description = "获取当前用户的未读通知")
    public ApiResponse<List<Notification>> getUnreadNotifications(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        List<Notification> notifications = notificationService.getUnreadNotifications(id);
        return ApiResponse.success(notifications);
    }
    
    @GetMapping("/count")
    @Operation(summary = "获取未读通知数量", description = "获取当前用户的未读通知数量")
    public ApiResponse<Map<String, Integer>> getUnreadCount(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            Map<String, Integer> result = new HashMap<>();
            result.put("count", 0);
            return ApiResponse.success(result);
        }
        int count = notificationService.getUnreadCount(id);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return ApiResponse.success(result);
    }
    
    @PostMapping("/read-all")
    @Operation(summary = "全部标记为已读", description = "将当前用户的所有通知标记为已读")
    public ApiResponse<String> markAllAsRead(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        notificationService.markAllAsRead(id);
        return ApiResponse.success("已全部标记为已读");
    }
    
    @PostMapping("/{id}/read")
    @Operation(summary = "标记单条通知为已读", description = "将指定通知标记为已读")
    public ApiResponse<String> markAsRead(@PathVariable Long id, HttpSession session) {
        notificationService.markAsRead(id);
        return ApiResponse.success("已标记为已读");
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "删除指定通知")
    public ApiResponse<String> deleteNotification(@PathVariable Long id, HttpSession session) {
        notificationService.deleteNotification(id);
        return ApiResponse.success("删除成功");
    }
    
    @DeleteMapping("/all")
    @Operation(summary = "删除全部通知", description = "删除当前用户的所有通知")
    public ApiResponse<String> deleteAllNotifications(
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        notificationService.deleteAllNotifications(id);
        return ApiResponse.success("删除成功");
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取通知", description = "获取指定类型的通知")
    public ApiResponse<List<Notification>> getNotificationsByType(
            @PathVariable String type,
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        Long id = getUserIdFromSessionOrParam(session, userId);
        if (id == null) {
            return ApiResponse.error(401, "请先登录");
        }
        List<Notification> notifications = notificationService.getNotificationsByType(id, type);
        return ApiResponse.success(notifications);
    }
}
