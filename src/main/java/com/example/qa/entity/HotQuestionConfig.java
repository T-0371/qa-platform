package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * 热门问题配置实体
 * 用于配置热门问题排行榜的筛选条件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hot_question_config")
public class HotQuestionConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 时间范围（天）
     * 例如：7表示最近7天，30表示最近30天
     */
    private Integer timeRangeDays;
    
    /**
     * 最小浏览量
     */
    private Integer minViewCount;
    
    /**
     * 最小回答数
     */
    private Integer minAnswerCount;
    
    /**
     * 最小投票数
     */
    private Integer minVoteCount;
    
    /**
     * 排序方式
     * view: 按浏览量排序
     * answer: 按回答数排序
     * vote: 按投票数排序
     * composite: 综合排序
     */
    private String sortBy;
    
    /**
     * 显示数量
     */
    private Integer displayCount;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * 创建者ID
     */
    private Long createdBy;
}
