package com.example.qa.controller;

import com.example.qa.dto.request.AnswerCreateRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Answer;
import com.example.qa.entity.User;
import com.example.qa.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @GetMapping
    public ApiResponse getAllAnswers() {
        try {
            List<Answer> answers = answerService.getAllAnswers();
            return ApiResponse.success("获取成功", answers);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse createAnswer(@RequestParam Long questionId, @RequestBody AnswerCreateRequest answerRequest, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            Answer answer = answerService.createAnswer(questionId, answerRequest, user.getId());
            return ApiResponse.success("创建成功", answer);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @GetMapping("/question/{questionId}")
    public ApiResponse getAnswersByQuestionId(@PathVariable Long questionId, 
                                             @RequestParam(defaultValue = "1") int page, 
                                             @RequestParam(defaultValue = "10") int size) {
        try {
            List<Answer> answers = answerService.getAnswersByQuestionId(questionId, page, size);
            return ApiResponse.success("获取成功", answers);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateAnswer(@PathVariable Long id, @RequestBody AnswerCreateRequest answerRequest, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            answerService.updateAnswer(id, answerRequest, user.getId());
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteAnswer(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            answerService.deleteAnswer(id, user.getId());
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/accept")
    public ApiResponse acceptAnswer(@PathVariable Long id, @RequestParam Long questionId, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            answerService.acceptAnswer(id, questionId, user.getId());
            return ApiResponse.success("采纳成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("采纳失败: " + e.getMessage());
        }
    }
}