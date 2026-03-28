package com.example.qa.service;

import com.example.qa.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 * 定义标签相关的业务方法
 */
public interface TagService {
    /**
     * 获取标签列表
     * @return 标签列表
     */
    List<Tag> getTagList();
    
    /**
     * 根据ID获取标签详情
     * @param id 标签ID
     * @return 标签详情
     */
    Tag getTagById(Long id);
    
    /**
     * 创建标签
     * @param tag 标签信息
     * @return 创建成功的标签信息
     */
    Tag createTag(Tag tag);
    
    /**
     * 更新标签
     * @param id 标签ID
     * @param tag 标签信息
     * @return 更新后的标签信息
     */
    Tag updateTag(Long id, Tag tag);
    
    /**
     * 删除标签
     * @param id 标签ID
     */
    void deleteTag(Long id);
}