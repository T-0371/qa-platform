package com.example.qa.service;

import com.example.qa.entity.SystemConfig;

public interface SystemConfigService {
    
    SystemConfig getConfig();
    
    SystemConfig updateConfig(SystemConfig config);
    
    SystemConfig resetToDefault();
}
