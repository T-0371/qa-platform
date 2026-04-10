package com.example.qa.service;

import java.util.Set;

public interface UserStatusService {
    boolean isUserOnline(Long userId);
    Set<Long> getChattingUsers(Long userId);
    void updateHeartbeat(Long userId);
}
