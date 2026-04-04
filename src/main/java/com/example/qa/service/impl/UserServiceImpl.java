package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qa.dto.request.UserRegisterRequest;
import com.example.qa.dto.request.UserLoginRequest;
import com.example.qa.entity.User;
import com.example.qa.mapper.UserMapper;
import com.example.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("ALTER TABLE `user` ADD COLUMN `security_question` VARCHAR(100) NULL COMMENT '密保问题' AFTER `points`");
        } catch (Exception e) {
            // 字段已存在，忽略错误
        }
        try {
            jdbcTemplate.execute("ALTER TABLE `user` ADD COLUMN `security_answer` VARCHAR(100) NULL COMMENT '密保答案' AFTER `security_question`");
        } catch (Exception e) {
            // 字段已存在，忽略错误
        }
    }
    
    /**
     * 用户注册
     * @param request 用户注册请求
     * @return 注册成功的用户信息
     */
    @Override
    public User register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (getUserByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (getUserByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(request.getPassword().getBytes()));
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());
        user.setRole("USER");
        user.setPoints(0);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        // 保存用户
        userMapper.insert(user);
        return user;
    }
    
    /**
     * 用户登录
     * @param request 用户登录请求
     * @return 登录成功的用户信息
     */
    @Override
    public User login(UserLoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String password = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        System.out.println("=== Login check for user: " + user.getUsername() + " ===");
        System.out.println("=== Current loginToken from DB: " + user.getLoginToken() + " ===");
        System.out.println("=== ForceLogin: " + request.getForceLogin() + " ===");
        
        // 检查登录令牌是否过期（2小时过期）
        boolean tokenExpired = false;
        if (user.getLoginTime() != null) {
            long hoursSinceLogin = (System.currentTimeMillis() - user.getLoginTime().getTime()) / (1000 * 60 * 60);
            if (hoursSinceLogin >= 2) {
                tokenExpired = true;
                System.out.println("=== Login token expired, allowing new login ===");
            }
        }
        
        // 如果存在有效令牌且未强制登录，则抛出冲突
        if (user.getLoginToken() != null && !user.getLoginToken().isEmpty() 
                && !Boolean.TRUE.equals(request.getForceLogin()) 
                && !tokenExpired) {
            System.out.println("=== LOGIN_CONFLICT detected! ===");
            throw new RuntimeException("LOGIN_CONFLICT");
        }
        
        String loginToken = UUID.randomUUID().toString().replace("-", "");
        user.setLoginToken(loginToken);
        user.setLoginTime(new Date());
        userMapper.updateById(user);
        
        System.out.println("=== Login success, new token: " + loginToken + " ===");
        
        User result = userMapper.findUserById(user.getId());
        result.setPassword(null);
        System.out.println("=== Returning user with loginToken: " + result.getLoginToken() + " ===");
        return result;
    }
    
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
    
    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public User getUserByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectOne(wrapper);
    }
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @Override
    public User updateUser(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getBio() != null) {
            existingUser.setBio(user.getBio());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getAge() != null) {
            existingUser.setAge(user.getAge());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getPoints() != null) {
            existingUser.setPoints(user.getPoints());
        }
        if (user.getSecurityQuestion() != null) {
            existingUser.setSecurityQuestion(user.getSecurityQuestion());
        }
        if (user.getSecurityAnswer() != null) {
            existingUser.setSecurityAnswer(user.getSecurityAnswer());
        }
        
        existingUser.setUpdatedAt(new Date());
        userMapper.updateById(existingUser);
        return existingUser;
    }
    
    @Override
    public java.util.List<User> getAllUsers() {
        return userMapper.selectList(null);
    }
    
    @Override
    public java.util.List<User> searchUsers(String keyword) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.like("username", keyword)
                    .or()
                    .like("email", keyword);
        return userMapper.selectList(queryWrapper);
    }
    
    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    @Override
    public String getSecurityQuestion(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isEmpty()) {
            throw new RuntimeException("该用户未设置密保问题");
        }
        return user.getSecurityQuestion();
    }
    
    @Override
    public boolean verifySecurityAnswer(String username, String securityAnswer) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getSecurityAnswer() == null || user.getSecurityAnswer().isEmpty()) {
            throw new RuntimeException("该用户未设置密保问题");
        }
        return user.getSecurityAnswer().equals(securityAnswer);
    }
    
    @Override
    public void resetPassword(String username, String newPassword) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        user.setUpdatedAt(new Date());
        userMapper.updateById(user);
    }
    
    @Override
    public void logout(Long userId) {
        System.out.println("=== Logout called for userId: " + userId + " ===");
        User user = userMapper.findUserById(userId);
        System.out.println("=== Found user: " + (user != null ? user.getUsername() : "null") + " ===");
        System.out.println("=== Current loginToken: " + (user != null ? user.getLoginToken() : "null") + " ===");
        if (user != null) {
            user.setLoginToken(null);
            user.setLoginTime(null);
            int result = userMapper.updateById(user);
            System.out.println("=== Update result: " + result + " ===");
            User updatedUser = userMapper.findUserById(userId);
            System.out.println("=== After update, loginToken: " + (updatedUser != null ? updatedUser.getLoginToken() : "null") + " ===");
        }
    }
    
    @Override
    public void addPoints(Long userId, int points) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPoints(user.getPoints() + points);
            user.setUpdatedAt(new Date());
            userMapper.updateById(user);
        }
    }
    
    @Override
    public boolean deductPoints(Long userId, int points) {
        User user = userMapper.selectById(userId);
        if (user != null && user.getPoints() >= points) {
            user.setPoints(user.getPoints() - points);
            user.setUpdatedAt(new Date());
            userMapper.updateById(user);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean checkPoints(Long userId, int points) {
        User user = userMapper.selectById(userId);
        return user != null && user.getPoints() >= points;
    }
}