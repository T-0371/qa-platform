package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/user")
public class UserStatusController {

    @Autowired
    private UserStatusService userStatusService;

    private static final Map<Long, Long> userChattingWith = new ConcurrentHashMap<>();

    @GetMapping("/status")
    public ApiResponse getUserStatus(@RequestParam Long userId) {
        try {
            Map<String, Object> status = new HashMap<>();

            boolean online = userStatusService.isUserOnline(userId);
            status.put("online", online);

            Set<Long> chatUsers = userStatusService.getChattingUsers(userId);
            if (chatUsers != null) {
                status.put("chattingWith", chatUsers);
            } else {
                status.put("chattingWith", java.util.Collections.emptySet());
            }

            return ApiResponse.success(status);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("online", false);
            fallback.put("chattingWith", java.util.Collections.emptySet());
            return ApiResponse.success(fallback);
        }
    }

    @PostMapping("/status/heartbeat")
    public ApiResponse heartbeat(@RequestParam Long userId) {
        try {
            userStatusService.updateHeartbeat(userId);
            return ApiResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.success(true);
        }
    }

    public static void updateUserStatus(Long userId, String status, Long chatWithUserId) {
        if ("ONLINE".equals(status)) {
            userChattingWith.put(userId, chatWithUserId);
        } else if ("OFFLINE".equals(status)) {
            userChattingWith.remove(userId);
        }
    }
}
