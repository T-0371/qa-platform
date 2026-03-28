package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 积分配置实体类
 * 用于存储积分相关的配置参数
 */
@Data
@TableName("points_config")
public class PointsConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 首次聊天消耗积分
     */
    private Integer firstChatCost;
    
    /**
     * 再次聊天消耗积分
     */
    private Integer repeatChatCost;
    
    /**
     * 每条消息消耗积分
     */
    private Integer messageCost;
    
    /**
     * 发布问题获得积分
     */
    private Integer questionReward;
    
    /**
     * 回复问题获得积分
     */
    private Integer answerReward;
    
    /**
     * 首次接收联系获得积分
     */
    private Integer firstContactReward;
    
    /**
     * 再次接收联系获得积分
     */
    private Integer repeatContactReward;
    
    /**
     * 回复消息获得积分
     */
    private Integer replyReward;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
}
