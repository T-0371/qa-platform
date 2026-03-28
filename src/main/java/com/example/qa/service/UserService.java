package com.example.qa.service;

import com.example.qa.dto.request.UserRegisterRequest;
import com.example.qa.dto.request.UserLoginRequest;
import com.example.qa.entity.User;

/**
 * 用户服务接口
 * 定义用户相关的业务方法
 */
public interface UserService {
    /**
     * 用户注册
     * @param request 用户注册请求
     * @return 注册成功的用户信息
     */
    User register(UserRegisterRequest request);
    
    /**
     * 用户登录
     * @param request 用户登录请求
     * @return 登录成功的用户信息
     */
    User login(UserLoginRequest request);
    
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);
    
    /**
     * 获取所有用户
     * @return 所有用户列表
     */
    java.util.List<User> getAllUsers();
    
    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @return 匹配的用户列表
     */
    java.util.List<User> searchUsers(String keyword);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 获取用户的密保问题
     * @param username 用户名
     * @return 密保问题
     */
    String getSecurityQuestion(String username);
    
    /**
     * 验证密保答案
     * @param username 用户名
     * @param securityAnswer 密保答案
     * @return 验证结果
     */
    boolean verifySecurityAnswer(String username, String securityAnswer);
    
    /**
     * 重置密码
     * @param username 用户名
     * @param newPassword 新密码
     */
    void resetPassword(String username, String newPassword);
    
    /**
     * 用户退出登录
     * @param userId 用户ID
     */
    void logout(Long userId);
    
    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 增加的积分
     */
    void addPoints(Long userId, int points);
    
    /**
     * 减少用户积分
     * @param userId 用户ID
     * @param points 减少的积分
     * @return 是否成功减少积分
     */
    boolean deductPoints(Long userId, int points);
    
    /**
     * 检查用户积分是否足够
     * @param userId 用户ID
     * @param points 需要的积分
     * @return 积分是否足够
     */
    boolean checkPoints(Long userId, int points);
}