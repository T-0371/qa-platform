package com.example.qa.controller;

import com.example.qa.dto.response.ApiResponse;
import com.example.qa.entity.Tag;
import com.example.qa.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 * 实现标签相关的API接口
 */
@RestController
@RequestMapping("/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "标签的创建、查询、更新、删除等接口")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    /**
     * 获取标签列表
     * @return 标签列表
     */
    @GetMapping
    @Operation(summary = "获取标签列表", description = "获取所有标签的列表")
    public ApiResponse<List<Tag>> getTagList() {
        List<Tag> tags = tagService.getTagList();
        return ApiResponse.success(tags);
    }
    
    /**
     * 获取标签详情
     * @param id 标签ID
     * @return 标签详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情", description = "根据标签ID获取标签的详细信息")
    public ApiResponse<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        return ApiResponse.success(tag);
    }
    
    /**
     * 创建标签
     * @param tag 标签信息
     * @return 创建成功的标签信息
     */
    @PostMapping
    @Operation(summary = "创建标签", description = "创建新的标签")
    public ApiResponse<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ApiResponse.success(createdTag);
    }
    
    /**
     * 更新标签
     * @param id 标签ID
     * @param tag 标签信息
     * @return 更新后的标签信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新标签", description = "更新指定标签的信息")
    public ApiResponse<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        Tag updatedTag = tagService.updateTag(id, tag);
        return ApiResponse.success(updatedTag);
    }
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 删除成功的响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "删除指定的标签")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ApiResponse.success(null);
    }
}