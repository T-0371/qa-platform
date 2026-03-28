package com.example.qa.controller;

import com.example.qa.dto.request.UserRegisterRequest;
import com.example.qa.dto.request.UserLoginRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.User;
import com.example.qa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 实现用户相关的API接口
 */
@RestController
@RequestMapping("/users")
@io.swagger.v3.oas.annotations.tags.Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * @param request 用户注册请求
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号")
    public ApiResponse<User> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        return ApiResponse.success(user);
    }
    
    /**
     * 用户登录
     * @param request 用户登录请求
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户账号登录")
    public ApiResponse<User> login(@RequestBody UserLoginRequest request) {
        try {
            User user = userService.login(request);
            return ApiResponse.success(user);
        } catch (RuntimeException e) {
            if ("LOGIN_CONFLICT".equals(e.getMessage())) {
                return ApiResponse.error(409, "您的账号已在其他设备登录，如需继续登录请确认");
            }
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     * @param userId 用户ID（从认证中获取）
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取登录用户的详细信息")
    public ApiResponse<User> getCurrentUser(@RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) String loginToken) {
        if (userId == null) {
            return ApiResponse.error(401, "用户未登录");
        }
        User user = userService.getUserById(userId);
        
        if (loginToken != null && user.getLoginToken() != null && !loginToken.equals(user.getLoginToken())) {
            return ApiResponse.error(401, "您的账号已在其他设备登录");
        }
        
        return ApiResponse.success(user);
    }
    
    /**
     * 更新用户信息
     * @param userId 用户ID（从认证中获取）
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    @Operation(summary = "更新用户信息", description = "更新登录用户的详细信息")
    public ApiResponse<User> updateCurrentUser(@RequestParam(required = false) Long userId, @RequestBody User user) {
        if (userId == null) {
            return ApiResponse.error(401, "用户未登录");
        }
        user.setId(userId);
        User updatedUser = userService.updateUser(user);
        return ApiResponse.success(updatedUser);
    }
    
    /**
     * 获取指定用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取指定用户信息", description = "根据用户ID获取用户详细信息")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
    
    /**
     * 获取所有用户（管理员功能）
     * @param keyword 搜索关键词（可选）
     * @return 所有用户列表
     */
    @GetMapping
    @Operation(summary = "获取所有用户", description = "获取系统中的所有用户列表，支持关键词搜索")
    public ApiResponse<java.util.List<User>> getAllUsers(@RequestParam(required = false) String keyword) {
        java.util.List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userService.searchUsers(keyword.trim());
        } else {
            users = userService.getAllUsers();
        }
        return ApiResponse.success(users);
    }
    
    /**
     * 删除用户（管理员功能）
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("用户删除成功");
    }
    
    /**
     * 更新用户（管理员功能）
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "管理员更新指定用户的信息")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ApiResponse.success(updatedUser);
    }
    
    /**
     * 获取用户的密保问题
     * @param username 用户名
     * @return 密保问题
     */
    @GetMapping("/security-question")
    @Operation(summary = "获取密保问题", description = "根据用户名获取密保问题")
    public ApiResponse<String> getSecurityQuestion(@RequestParam String username) {
        String question = userService.getSecurityQuestion(username);
        return ApiResponse.success(question);
    }
    
    /**
     * 验证密保答案
     * @param request 验证请求（包含用户名和密保答案）
     * @return 验证结果
     */
    @PostMapping("/verify-security-answer")
    @Operation(summary = "验证密保答案", description = "验证用户输入的密保答案是否正确")
    public ApiResponse<Boolean> verifySecurityAnswer(@RequestBody SecurityAnswerRequest request) {
        boolean valid = userService.verifySecurityAnswer(request.getUsername(), request.getSecurityAnswer());
        if (valid) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.error(400, "密保答案错误");
        }
    }
    
    /**
     * 重置密码
     * @param request 重置密码请求（包含用户名和新密码）
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "通过密保验证后重置密码")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getUsername(), request.getNewPassword());
        return ApiResponse.success("密码重置成功");
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户退出登录", description = "清除用户登录状态")
    public ApiResponse<String> logout(@RequestParam Long userId) {
        System.out.println("Logout called for userId: " + userId);
        userService.logout(userId);
        return ApiResponse.success("退出登录成功");
    }
    
    /**
     * 密保答案验证请求DTO
     */
    public static class SecurityAnswerRequest {
        private String username;
        private String securityAnswer;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getSecurityAnswer() {
            return securityAnswer;
        }
        
        public void setSecurityAnswer(String securityAnswer) {
            this.securityAnswer = securityAnswer;
        }
    }
    
    /**
     * 重置密码请求DTO
     */
    public static class ResetPasswordRequest {
        private String username;
        private String newPassword;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getNewPassword() {
            return newPassword;
        }
        
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}