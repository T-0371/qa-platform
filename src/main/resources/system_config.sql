-- 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_name` varchar(100) NOT NULL DEFAULT 'TechQA' COMMENT '网站名称',
  `site_description` varchar(500) DEFAULT NULL COMMENT '网站描述',
  `background_type` varchar(20) NOT NULL DEFAULT 'gradient' COMMENT '背景类型: gradient-渐变, image-图片, color-纯色',
  `background_value` text DEFAULT NULL COMMENT '背景值: 渐变CSS或图片URL或颜色值',
  `layout_type` varchar(20) NOT NULL DEFAULT 'default' COMMENT '布局类型: default-默认, compact-紧凑, wide-宽屏',
  `logo_url` varchar(500) DEFAULT NULL COMMENT 'Logo图片URL',
  `favicon_url` varchar(500) DEFAULT NULL COMMENT '网站图标URL',
  `primary_color` varchar(20) NOT NULL DEFAULT '#ec4899' COMMENT '主题色',
  `secondary_color` varchar(20) NOT NULL DEFAULT '#8b5cf6' COMMENT '辅助色',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表';

-- 插入默认配置
INSERT INTO `system_config` (`site_name`, `site_description`, `background_type`, `background_value`, `layout_type`, `primary_color`, `secondary_color`) VALUES 
('TechQA', '探索技术前沿 · 分享编程智慧 · 共同成长进步', 'gradient', 'linear-gradient(145deg, #0f0f1e 0%, #1a1a2e 25%, #16213e 50%, #0d0d1a 75%, #0a0a14 100%)', 'default', '#ec4899', '#8b5cf6');
