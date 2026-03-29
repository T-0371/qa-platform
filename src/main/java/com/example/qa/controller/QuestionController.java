package com.example.qa.controller;

import com.example.qa.dto.request.QuestionRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Question;
import com.example.qa.entity.User;
import com.example.qa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ApiResponse createQuestion(@RequestBody QuestionRequest questionRequest, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Question question = questionService.createQuestion(
                questionRequest.getTitle(),
                questionRequest.getContent(),
                questionRequest.getTagIds(),
                user.getId()
            );
            return ApiResponse.success("创建成功", question);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            List<Question> questions = questionService.getQuestions(page, size, keyword);
            long total = questionService.getQuestionCount(keyword);
            return ApiResponse.success("获取成功", Map.of("list", questions, "total", total));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse getQuestionById(@PathVariable Long id) {
        try {
            Question question = questionService.getQuestionById(id);
            if (question == null) {
                return ApiResponse.error("问题不存在");
            }
            return ApiResponse.success("获取成功", question);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public ApiResponse getQuestionDetail(@PathVariable Long id) {
        try {
            Question question = questionService.getQuestionDetail(id);
            if (question == null) {
                return ApiResponse.error("问题不存在");
            }
            return ApiResponse.success("获取成功", question);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateQuestion(@PathVariable Long id, @RequestBody QuestionRequest questionRequest, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Question question = questionService.getQuestionById(id);
            if (question == null) {
                return ApiResponse.error("问题不存在");
            }
            if (!question.getUserId().equals(user.getId()) && !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            
            questionService.updateQuestion(id, questionRequest.getTitle(), questionRequest.getContent(), questionRequest.getTagIds());
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteQuestion(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Question question = questionService.getQuestionById(id);
            if (question == null) {
                return ApiResponse.error("问题不存在");
            }
            if (!question.getUserId().equals(user.getId()) && !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            
            questionService.deleteQuestion(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/hot/list")
    public ApiResponse getHotQuestions() {
        try {
            List<Question> questions = questionService.getHotQuestions();
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest/list")
    public ApiResponse getLatestQuestions() {
        try {
            List<Question> questions = questionService.getLatestQuestions();
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse searchQuestions(@RequestParam String keyword) {
        try {
            List<Question> questions = questionService.searchQuestions(keyword);
            return ApiResponse.success("搜索成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }
}