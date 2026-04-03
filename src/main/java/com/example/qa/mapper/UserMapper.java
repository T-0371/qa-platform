package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.User;

public interface UserMapper extends BaseMapper<User> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findUserById(Long userId);
}