package com.example.qa.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String bio;
    private String gender;
    private Integer age;
    private String phone;
    private String role;
    private Integer points;
    private String securityQuestion;
    private String securityAnswer;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String loginToken;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date loginTime;
    private Date createdAt;
    private Date updatedAt;
}
