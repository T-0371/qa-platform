package com.example.qa.controller;

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
    public ApiResponse register(@RequestBody UserRegisterRequest registerRequest) {
        try {
            userService.register(registerRequest.getUsername(), registerRequest.getPassword(), 
                                registerRequest.getEmail(), registerRequest.getPhone(),
                                registerRequest.getSecurityQuestion(), registerRequest.getSecurityAnswer());
            return ApiResponse.success("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("user", user);
            return ApiResponse.success("登录成功", user);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("登录失败: " + e.getMessage());
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
            userService.updateUser(user);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
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

    @GetMapping
    public ApiResponse getUsers(@RequestParam(required = false) String keyword) {
        try {
            List<User> users = userService.getUsers(keyword);
            return ApiResponse.success("获取成功", users);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            userService.deleteUser(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateUser(@PathVariable Long id, @RequestBody User user, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return ApiResponse.error("权限不足");
            }
            user.setId(id);
            userService.updateUser(user);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/security/question")
    public ApiResponse getSecurityQuestion(@RequestParam String username) {
        try {
            String question = userService.getSecurityQuestion(username);
            return ApiResponse.success("获取成功", question);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/security/verify")
    public ApiResponse verifySecurityAnswer(@RequestParam String username, @RequestParam String answer) {
        try {
            boolean result = userService.verifySecurityAnswer(username, answer);
            return ApiResponse.success("验证成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("验证失败: " + e.getMessage());
        }
    }

    @PostMapping("/password/reset")
    public ApiResponse resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        try {
            userService.resetPassword(username, newPassword);
            return ApiResponse.success("重置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("重置失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success("退出成功");
    }
}