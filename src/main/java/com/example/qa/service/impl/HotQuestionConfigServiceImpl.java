package com.example.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.qa.entity.HotQuestionConfig;
import com.example.qa.mapper.HotQuestionConfigMapper;
import com.example.qa.service.HotQuestionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 热门问题配置服务实现类
 */
@Service
public class HotQuestionConfigServiceImpl implements HotQuestionConfigService {
    
    @Autowired
    private HotQuestionConfigMapper hotQuestionConfigMapper;
    
    @Override
    @Transactional
    public HotQuestionConfig createConfig(HotQuestionConfig config) {
        config.setCreatedAt(new Date());
        config.setUpdatedAt(new Date());
        if (config.getEnabled() == null) {
            config.setEnabled(true);
        }
        hotQuestionConfigMapper.insert(config);
        return config;
    }
    
    @Override
    @Transactional
    public HotQuestionConfig updateConfig(HotQuestionConfig config) {
        HotQuestionConfig existingConfig = hotQuestionConfigMapper.selectById(config.getId());
        if (existingConfig == null) {
            throw new RuntimeException("配置不存在");
        }
        config.setUpdatedAt(new Date());
        hotQuestionConfigMapper.updateById(config);
        return hotQuestionConfigMapper.selectById(config.getId());
    }
    
    @Override
    @Transactional
    public void deleteConfig(Long id) {
        hotQuestionConfigMapper.deleteById(id);
    }
    
    @Override
    public HotQuestionConfig getConfigById(Long id) {
        return hotQuestionConfigMapper.selectById(id);
    }
    
    @Override
    public List<HotQuestionConfig> getAllConfigs() {
        QueryWrapper<HotQuestionConfig> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        return hotQuestionConfigMapper.selectList(wrapper);
    }
    
    @Override
    public HotQuestionConfig getEnabledConfig() {
        HotQuestionConfig config = hotQuestionConfigMapper.selectEnabledConfig();
        if (config == null) {
            // 返回默认配置
            return HotQuestionConfig.builder()
                    .timeRangeDays(7)
                    .minViewCount(100)
                    .minAnswerCount(5)
                    .minVoteCount(10)
                    .sortBy("composite")
                    .displayCount(10)
                    .enabled(true)
                    .build();
        }
        return config;
    }
    
    @Override
    @Transactional
    public void enableConfig(Long id) {
        // 先将所有配置禁用
        List<HotQuestionConfig> allConfigs = getAllConfigs();
        for (HotQuestionConfig config : allConfigs) {
            config.setEnabled(false);
            config.setUpdatedAt(new Date());
            hotQuestionConfigMapper.updateById(config);
        }
        
        // 启用指定配置
        HotQuestionConfig config = new HotQuestionConfig();
        config.setId(id);
        config.setEnabled(true);
        config.setUpdatedAt(new Date());
        hotQuestionConfigMapper.updateById(config);
    }
}
