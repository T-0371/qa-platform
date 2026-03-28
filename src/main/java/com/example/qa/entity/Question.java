package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Integer viewCount;
    private Integer answerCount;
    private Integer voteCount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    
    @TableField(exist = false)
    private List<Long> tagIds;
    
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String avatar;
}
