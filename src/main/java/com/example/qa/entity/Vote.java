package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 点赞实体类
 * 用于存储用户对问题、回答和评论的点赞/踩记录
 */
@Data
@TableName("vote")
public class Vote {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 目标类型：QUESTION、ANSWER、COMMENT
     */
    private String targetType;
    
    /**
     * 目标ID
     */
    private Long targetId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 点赞类型：1表示点赞，-1表示踩
     */
    private Integer voteType;
    
    /**
     * 创建时间
     */
    private Date createdAt;
}