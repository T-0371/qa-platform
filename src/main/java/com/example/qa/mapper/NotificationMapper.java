package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    @Update("UPDATE notification SET is_read = true WHERE user_id = #{userId}")
    void markAllAsRead(@Param("userId") Long userId);
    
    @Update("UPDATE notification SET is_read = true WHERE id = #{id}")
    void markAsRead(@Param("id") Long id);
}
