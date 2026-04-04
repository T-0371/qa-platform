package com.example.qa.controller;

import com.example.qa.dto.request.QuestionCreateRequest;
import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Question;
import com.example.qa.entity.User;
import com.example.qa.entity.PointsConfig;
import com.example.qa.service.QuestionService;
import com.example.qa.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PointsConfigService pointsConfigService;

    @PostMapping
    public ApiResponse createQuestion(@RequestBody QuestionCreateRequest questionRequest, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Question question = questionService.createQuestion(questionRequest, user.getId());
            
            // 从配置中读取积分奖励
            PointsConfig config = pointsConfigService.getCurrentConfig();
            int rewardPoints = config != null && config.getQuestionReward() != null ? config.getQuestionReward() : 5;
            
            // 返回成功消息，包含积分提示
            return ApiResponse.success("发布成功！获得 " + rewardPoints + " 积分奖励", question);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Question> questions = questionService.getQuestionList(page, size);
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/hot/list")
    public ApiResponse getHotQuestions(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Question> questions = questionService.getHotQuestions(limit);
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest/list")
    public ApiResponse getLatestQuestions(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Question> questions = questionService.getLatestQuestions(limit);
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/hot")
    public ApiResponse getHotQuestionsByParam(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Question> questions = questionService.getHotQuestions(limit);
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    public ApiResponse getLatestQuestionsByParam(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Question> questions = questionService.getLatestQuestions(limit);
            return ApiResponse.success("获取成功", questions);
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
            Question question = questionService.getQuestionByIdWithoutViewCount(id);
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
    public ApiResponse updateQuestion(@PathVariable Long id, @RequestBody QuestionCreateRequest questionRequest, HttpSession session) {
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
            
            questionService.updateQuestion(id, questionRequest, user.getId());
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
            
            questionService.deleteQuestion(id, user.getId());
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse searchQuestions(@RequestParam String keyword, 
                                      @RequestParam(defaultValue = "1") int page, 
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            List<Question> questions = questionService.searchQuestions(keyword, page, size);
            return ApiResponse.success("搜索成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }
}