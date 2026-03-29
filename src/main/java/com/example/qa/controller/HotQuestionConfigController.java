package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.HotQuestionConfig;
import com.example.qa.entity.Question;
import com.example.qa.entity.User;
import com.example.qa.service.HotQuestionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/hot-question/config")
public class HotQuestionConfigController {

    @Autowired
    private HotQuestionConfigService hotQuestionConfigService;

    @PostMapping
    public ApiResponse createConfig(@RequestBody HotQuestionConfig config, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            hotQuestionConfigService.createConfig(config);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateConfig(@PathVariable Long id, @RequestBody HotQuestionConfig config, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            config.setId(id);
            hotQuestionConfigService.updateConfig(config);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteConfig(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            hotQuestionConfigService.deleteConfig(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse getConfigById(@PathVariable Long id) {
        try {
            HotQuestionConfig config = hotQuestionConfigService.getConfigById(id);
            if (config == null) {
                return ApiResponse.error("配置不存在");
            }
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getAllConfigs() {
        try {
            List<HotQuestionConfig> configs = hotQuestionConfigService.getAllConfigs();
            return ApiResponse.success("获取成功", configs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PutMapping("/enable/{id}")
    public ApiResponse enableConfig(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null || !"admin".equals(user.getRole())) {
                return ApiResponse.error("权限不足");
            }
            hotQuestionConfigService.enableConfig(id);
            return ApiResponse.success("启用成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("启用失败: " + e.getMessage());
        }
    }

    @GetMapping("/current")
    public ApiResponse getCurrentConfig() {
        try {
            HotQuestionConfig config = hotQuestionConfigService.getCurrentConfig();
            return ApiResponse.success("获取成功", config);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/hot-questions")
    public ApiResponse getHotQuestions() {
        try {
            List<Question> questions = hotQuestionConfigService.getHotQuestions();
            return ApiResponse.success("获取成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping("/preview")
    public ApiResponse previewConfig(@RequestBody HotQuestionConfig config) {
        try {
            List<Question> questions = hotQuestionConfigService.previewConfig(config);
            return ApiResponse.success("预览成功", questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("预览失败: " + e.getMessage());
        }
    }
}