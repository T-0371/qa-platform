package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.User;
import com.example.qa.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ApiResponse vote(@RequestParam Long targetId, @RequestParam String targetType, @RequestParam Integer type, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            voteService.vote(user.getId(), targetId, targetType, type);
            return ApiResponse.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ApiResponse cancelVote(@RequestParam Long targetId, @RequestParam String targetType, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            voteService.cancelVote(user.getId(), targetId, targetType);
            return ApiResponse.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }
}