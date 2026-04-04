package com.example.qa.controller;

import com.example.qa.dto.request.UserLoginRequest;
import com.example.qa.dto.request.UserRegisterRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.User;
import com.example.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse register(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.register(request);
            return ApiResponse.success("注册成功", user);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody UserLoginRequest request, HttpSession session) {
        try {
            User user = userService.login(request);
            session.setAttribute("user", user);
            return ApiResponse.success("登录成功", user);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            // 清除数据库中的登录令牌
            userService.logout(user.getId());
            session.invalidate();
            return ApiResponse.success("退出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("退出失败: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse getCurrentUser(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            return ApiResponse.success("获取成功", user);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/me")
    public ApiResponse updateUser(@RequestBody User user, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ApiResponse.error("用户未登录");
            }
            user.setId(currentUser.getId());
            User updatedUser = userService.updateUser(user);
            session.setAttribute("user", updatedUser);
            return ApiResponse.success("更新成功", updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse searchUsers(@RequestParam String keyword) {
        try {
            List<User> users = userService.searchUsers(keyword);
            return ApiResponse.success("搜索成功", users);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ApiResponse getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ApiResponse.success("获取成功", users);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getUsers(@RequestParam(defaultValue = "1") int page, 
                               @RequestParam(defaultValue = "10") int size, 
                               @RequestParam(required = false) String keyword) {
        try {
            List<User> users;
            if (keyword != null && !keyword.isEmpty()) {
                users = userService.searchUsers(keyword);
            } else {
                users = userService.getAllUsers();
            }
            return ApiResponse.success("获取成功", users);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestParam String username, 
                                    @RequestParam String securityAnswer, 
                                    @RequestParam String newPassword) {
        try {
            boolean verified = userService.verifySecurityAnswer(username, securityAnswer);
            if (!verified) {
                return ApiResponse.error("密保答案错误");
            }
            userService.resetPassword(username, newPassword);
            return ApiResponse.success("密码重置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("密码重置失败: " + e.getMessage());
        }
    }

    @GetMapping("/security-question/{username}")
    public ApiResponse getSecurityQuestion(@PathVariable String username) {
        try {
            String question = userService.getSecurityQuestion(username);
            return ApiResponse.success("获取成功", question);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success("获取成功", user);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
}