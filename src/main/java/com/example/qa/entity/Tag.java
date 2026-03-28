package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

/**
 * 标签实体类
 * 用于存储问题的标签信息
 */
@Data
@Builder
@TableName("tag")
public class Tag {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 标签描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private Date createdAt;
}
