package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Collect;
import com.example.qa.entity.User;
import com.example.qa.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/collects")
public class CollectController {

    @Autowired
    private CollectService collectService;

    @PostMapping
    public ApiResponse addCollect(@RequestParam Long questionId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Collect collect = collectService.collectQuestion(questionId, user.getId());
            return ApiResponse.success("收藏成功", collect);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("收藏失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse removeCollect(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            collectService.cancelCollect(id, user.getId());
            return ApiResponse.success("取消收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("取消收藏失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getUserCollects(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            List<Collect> collects = collectService.getUserCollects(user.getId());
            return ApiResponse.success("获取成功", collects);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ApiResponse checkCollect(@RequestParam Long questionId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            boolean collected = collectService.isCollected(questionId, user.getId());
            return ApiResponse.success("检查成功", collected);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
}