package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 问题标签关联实体类
 * 用于存储问题和标签之间的多对多关系
 */
@Data
@TableName("question_tag")
public class QuestionTag {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 问题ID
     */
    private Long questionId;
    
    /**
     * 标签ID
     */
    private Long tagId;
}