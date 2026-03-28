package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 收藏实体类
 * 用于存储用户对问题的收藏记录
 */
@Data
@TableName("collect")
public class Collect {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 问题ID
     */
    private Long questionId;
    
    /**
     * 创建时间
     */
    private Date createdAt;
}