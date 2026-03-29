package com.example.qa.controller;

import com.example.qa.dto.request.CommentCreateRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Comment;
import com.example.qa.entity.User;
import com.example.qa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ApiResponse createComment(@RequestBody CommentCreateRequest request, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Comment comment = commentService.createComment(request, user.getId());
            return ApiResponse.success("创建成功", comment);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getComments(@RequestParam String targetType, @RequestParam Long targetId) {
        try {
            List<Comment> comments = commentService.getCommentsByTarget(targetType, targetId);
            return ApiResponse.success("获取成功", comments);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateComment(@PathVariable Long id, @RequestParam String content, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Comment comment = commentService.updateComment(id, content, user.getId());
            return ApiResponse.success("更新成功", comment);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteComment(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            commentService.deleteComment(id, user.getId());
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
}
