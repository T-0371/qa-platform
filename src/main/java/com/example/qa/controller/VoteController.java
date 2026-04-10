package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.User;
import com.example.qa.entity.Vote;
import com.example.qa.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ApiResponse vote(@RequestParam String targetType, @RequestParam Long targetId, @RequestParam Integer voteType, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Vote vote = voteService.vote(targetType, targetId, user.getId(), voteType);
            return ApiResponse.success("操作成功", vote);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse cancelVote(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            voteService.cancelVote(id, user.getId());
            return ApiResponse.success("取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("取消失败: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ApiResponse checkVote(@RequestParam String targetType, @RequestParam Long targetId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Vote vote = voteService.getVoteByTargetAndUser(targetType, targetId, user.getId());
            return ApiResponse.success("检查成功", vote);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
}