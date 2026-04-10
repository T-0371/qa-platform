package com.example.qa.service.impl;

import com.example.qa.service.UserStatusService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserStatusServiceImpl implements UserStatusService {

    private static final long ONLINE_THRESHOLD_MS = 30000;

    private static final ConcurrentHashMap<Long, Long> userLastHeartbeat = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Set<Long>> userChattingWith = new ConcurrentHashMap<>();

    @Override
    public boolean isUserOnline(Long userId) {
        Long lastHeartbeat = userLastHeartbeat.get(userId);
        if (lastHeartbeat == null) {
            return false;
        }
        return (System.currentTimeMillis() - lastHeartbeat) < ONLINE_THRESHOLD_MS;
    }

    @Override
    public Set<Long> getChattingUsers(Long userId) {
        return userChattingWith.getOrDefault(userId, Collections.emptySet());
    }

    @Override
    public void updateHeartbeat(Long userId) {
        userLastHeartbeat.put(userId, System.currentTimeMillis());
    }

    public void setUserChattingWith(Long userId, Long chatWithUserId) {
        if (chatWithUserId != null) {
            userChattingWith.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(chatWithUserId);
        }
    }

    public void removeUserChattingWith(Long userId, Long chatWithUserId) {
        Set<Long> set = userChattingWith.get(userId);
        if (set != null) {
            set.remove(chatWithUserId);
            if (set.isEmpty()) {
                userChattingWith.remove(userId);
            }
        }
    }
}
