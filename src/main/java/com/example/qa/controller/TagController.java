package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Tag;
import com.example.qa.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ApiResponse getTags() {
        try {
            List<Tag> tags = tagService.getTags();
            return ApiResponse.success("获取成功", tags);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse getTagById(@PathVariable Long id) {
        try {
            Tag tag = tagService.getTagById(id);
            if (tag == null) {
                return ApiResponse.error("标签不存在");
            }
            return ApiResponse.success("获取成功", tag);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse createTag(@RequestBody Tag tag) {
        try {
            tagService.createTag(tag);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            tag.setId(id);
            tagService.updateTag(tag);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
}