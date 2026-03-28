package com.example.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qa.entity.HotQuestionConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 热门问题配置Mapper接口
 */
@Mapper
public interface HotQuestionConfigMapper extends BaseMapper<HotQuestionConfig> {
    
    /**
     * 获取当前启用的配置
     * @return 启用的热门问题配置
     */
    @Select("SELECT * FROM hot_question_config WHERE enabled = 1 ORDER BY updated_at DESC LIMIT 1")
    HotQuestionConfig selectEnabledConfig();
}
