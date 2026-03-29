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
            
            Collect collect = collectService.addCollect(user.getId(), questionId);
            return ApiResponse.success("收藏成功", collect);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("收藏失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{questionId}")
    public ApiResponse removeCollect(@PathVariable Long questionId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            collectService.removeCollect(user.getId(), questionId);
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
            return ApiResponse.success("获取收藏列表成功", collects);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取收藏列表失败: " + e.getMessage());
        }
    }
}