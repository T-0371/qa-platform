-- 远程数据库同步脚本
-- 基于本地数据库结构生成
-- 生成时间: 2026-04-10

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =================== 表结构同步 ===================

-- 1. points_config 表
CREATE TABLE IF NOT EXISTS `points_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_chat_cost` int DEFAULT '0',
  `repeat_chat_cost` int DEFAULT '0',
  `message_cost` int DEFAULT '0',
  `question_reward` int DEFAULT '5',
  `answer_reward` int DEFAULT '2',
  `first_contact_reward` int DEFAULT '10',
  `repeat_contact_reward` int DEFAULT '2',
  `reply_reward` int DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- 2. message 表
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者用户名',
  `sender_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者头像',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `receiver_username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者用户名',
  `receiver_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者头像',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `question_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sender_receiver` (`sender_id`,`receiver_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  KEY `idx_question_id` (`question_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='聊天消息表';

-- 3. 其他表结构检查（如果不存在则创建）
CREATE TABLE IF NOT EXISTS `answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `vote_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `is_accepted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被采纳',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_accepted`(`is_accepted` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_answer_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '回答表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_question`(`user_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_collect_question`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_collect_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型：QUESTION/ANSWER',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_target_type`(`target_type` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `hot_question_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `time_range_days` int NULL DEFAULT 7 COMMENT '时间范围（天）',
  `min_view_count` int NULL DEFAULT 100 COMMENT '最小浏览量',
  `min_answer_count` int NULL DEFAULT 5 COMMENT '最小回答数',
  `min_vote_count` int NULL DEFAULT 10 COMMENT '最小投票数',
  `sort_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'composite' COMMENT '排序方式：view/answer/vote/composite',
  `display_count` int NULL DEFAULT 10 COMMENT '显示数量',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '热门问题配置表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `question_id` bigint NULL DEFAULT NULL COMMENT '相关问题ID',
  `answer_id` bigint NULL DEFAULT NULL COMMENT '相关回答ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型: LIKE/ANSWER',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知内容',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  `from_user_id` bigint NULL DEFAULT NULL COMMENT '触发通知的用户ID',
  `from_username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '触发通知的用户名',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `answer_count` int NULL DEFAULT 0 COMMENT '回答数',
  `vote_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'OPEN' COMMENT '状态：OPEN/CLOSED',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  CONSTRAINT `fk_question_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '问题表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `question_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_tag`(`question_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_tag_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_question_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '问题标签关联表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TechQA' COMMENT '网站名称',
  `site_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网站描述',
  `background_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'gradient' COMMENT '背景类型: gradient-渐变, image-图片, color-纯色',
  `background_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '背景值: 渐变CSS或图片URL或颜色值',
  `layout_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'default' COMMENT '布局类型: default-默认, compact-紧凑, wide-宽屏',
  `logo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Logo图片URL',
  `favicon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网站图标URL',
  `primary_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '#ec4899' COMMENT '主题色',
  `secondary_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '#8b5cf6' COMMENT '辅助色',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签描述',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `avatar` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人简介',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别：MALE/FEMALE/OTHER',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `security_question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题',
  `security_answer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保答案',
  `login_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `login_time` datetime NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  INDEX `idx_login_token`(`login_token` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `vote` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型：QUESTION/ANSWER/COMMENT',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `vote_type` tinyint NOT NULL COMMENT '点赞类型：1表示点赞，-1表示踩',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_target_user`(`target_type` ASC, `target_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_vote_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 167 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '点赞表' ROW_FORMAT = DYNAMIC;

-- =================== 基础数据同步 ===================

-- 1. 确保 points_config 有基础数据
INSERT IGNORE INTO `points_config` (`id`, `first_chat_cost`, `repeat_chat_cost`, `message_cost`, `question_reward`, `answer_reward`, `first_contact_reward`, `repeat_contact_reward`, `reply_reward`, `created_at`, `updated_at`)
VALUES (1, 5, 3, 1, 5, 3, 5, 3, 1, NOW(), NOW());

-- 2. 确保 system_config 有基础数据
INSERT IGNORE INTO `system_config` (`id`, `site_name`, `site_description`, `background_type`, `background_value`, `layout_type`, `logo_url`, `favicon_url`, `primary_color`, `secondary_color`, `created_at`, `updated_at`)
VALUES (1, 'IT技术问答', '探索技术前沿 · 分享编程智慧 · 共同成长进步', 'image', '/uploads/f1439cad-3084-4acf-bca7-d271687e8712.png', 'default', '', '', '#cd66db', '#6ecf86', NOW(), NOW());

-- 3. 确保热门问题配置有基础数据
INSERT IGNORE INTO `hot_question_config` (`id`, `time_range_days`, `min_view_count`, `min_answer_count`, `min_vote_count`, `sort_by`, `display_count`, `enabled`, `created_at`, `updated_at`, `created_by`)
VALUES (1, 7, 50, 2, 5, 'composite', 10, 1, NOW(), NOW(), 1);

-- 4. 确保标签表有基础数据
INSERT IGNORE INTO `tag` (`id`, `name`, `description`, `created_at`)
VALUES 
(1, 'Java', 'Java编程语言', NOW()),
(2, 'Spring', 'Spring框架', NOW()),
(3, 'MySQL', 'MySQL数据库', NOW()),
(4, 'Redis', 'Redis缓存', NOW()),
(5, 'Docker', 'Docker容器', NOW()),
(6, 'Kubernetes', 'Kubernetes容器编排', NOW()),
(7, 'React', 'React前端框架', NOW()),
(8, 'Vue', 'Vue前端框架', NOW()),
(9, 'Python', 'Python编程语言', NOW()),
(10, 'Go', 'Go编程语言', NOW());

-- 5. 确保管理员用户存在
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `email`, `avatar`, `bio`, `gender`, `age`, `phone`, `role`, `points`, `security_question`, `security_answer`, `login_token`, `login_time`, `created_at`, `updated_at`)
VALUES (1, 'admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', NULL, '系统管理员', 'MALE', 30, '13800138000', 'ADMIN', 1000, '你最喜欢的编程语言是什么？', 'Java', NULL, NULL, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- =================== 同步完成 ===================
-- 执行此脚本后，远程数据库将与本地数据库结构保持一致
-- 注意：此脚本不会覆盖现有数据，只会补充缺失的表和基础数据
