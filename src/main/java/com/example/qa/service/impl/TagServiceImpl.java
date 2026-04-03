package com.example.qa.service.impl;

import com.example.qa.entity.Tag;
import com.example.qa.mapper.TagMapper;
import com.example.qa.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 标签服务实现类
 * 实现标签相关的业务逻辑
 */
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    /**
     * 获取标签列表
     * @return 标签列表
     */
    @Override
    public List<Tag> getTagList() {
        return tagMapper.selectList(null);
    }
    
    /**
     * 根据ID获取标签详情
     * @param id 标签ID
     * @return 标签详情
     */
    @Override
    public Tag getTagById(Long id) {
        return tagMapper.selectById(id);
    }
    
    /**
     * 创建标签
     * @param tag 标签信息
     * @return 创建成功的标签信息
     */
    @Override
    public Tag createTag(Tag tag) {
        // 检查标签名称是否已存在
        Tag existingTag = tagMapper.findByName(tag.getName());
        if (existingTag != null) {
            throw new RuntimeException("标签名称已存在");
        }
        
        // 设置创建时间
        tag.setCreatedAt(new Date());
        
        // 保存标签
        tagMapper.insert(tag);
        
        return tag;
    }
    
    /**
     * 更新标签
     * @param id 标签ID
     * @param tag 标签信息
     * @return 更新后的标签信息
     */
    @Override
    public Tag updateTag(Long id, Tag tag) {
        // 获取标签
        Tag existingTag = tagMapper.selectById(id);
        if (existingTag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 检查标签名称是否已被其他标签使用
        Tag nameTag = tagMapper.findByName(tag.getName());
        if (nameTag != null && !nameTag.getId().equals(id)) {
            throw new RuntimeException("标签名称已被使用");
        }
        
        // 更新标签信息
        existingTag.setName(tag.getName());
        existingTag.setDescription(tag.getDescription());
        
        // 保存更新
        tagMapper.updateById(existingTag);
        
        return existingTag;
    }
    
    /**
     * 删除标签
     * @param id 标签ID
     */
    @Override
    public void deleteTag(Long id) {
        // 检查标签是否存在
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // TODO: 检查标签是否被问题使用
        
        // 删除标签
        tagMapper.deleteById(id);
    }
}