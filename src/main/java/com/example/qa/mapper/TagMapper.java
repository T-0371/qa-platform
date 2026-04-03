package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Tag;

/**
 * 标签Mapper接口
 * 用于标签相关的数据库操作
 */
public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据标签名称获取标签信息
     * @param name 标签名称
     * @return 标签信息
     */
    Tag findByName(String name);
}