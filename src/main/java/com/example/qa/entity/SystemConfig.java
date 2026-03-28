package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_config")
public class SystemConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String siteName;
    
    private String siteDescription;
    
    private String backgroundType;
    
    private String backgroundValue;
    
    private String layoutType;
    
    private String logoUrl;
    
    private String faviconUrl;
    
    private String primaryColor;
    
    private String secondaryColor;
    
    private Date createdAt;
    
    private Date updatedAt;
}
