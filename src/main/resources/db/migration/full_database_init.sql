-- 完整的数据库初始化脚本
-- 适用于 Railway 远程数据库

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS railway DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE railway;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `avatar` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别：MALE/FEMALE/OTHER',
  `age` int DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
  `points` int DEFAULT '0' COMMENT '积分',
  `security_question` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密 保问题',
  `security_answer` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密保 答案',
  `login_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_email` (`email`) USING BTREE,
  KEY `idx_login_token` (`login_token`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- 问题表
CREATE TABLE IF NOT EXISTS `question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内 容',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `answer_count` int DEFAULT '0' COMMENT '回答数',
  `vote_count` int DEFAULT '0' COMMENT '点赞数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'OPEN' COMMENT '状态：OPEN/CLOSED',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  KEY `idx_view_count` (`view_count`) USING BTREE,
  CONSTRAINT `fk_question_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='问题表';

-- 回答表
CREATE TABLE IF NOT EXISTS `answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内 容',
  `vote_count` int DEFAULT '0' COMMENT '点赞数',
  `is_accepted` tinyint(1) DEFAULT '0' COMMENT '是否被采纳',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_question_id` (`question_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_is_accepted` (`is_accepted`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_answer_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='回答表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型：QUESTION/ANSWER',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_target_type` (`target_type`) USING BTREE,
  KEY `idx_target_id` (`target_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签表';

-- 问题标签关联表
CREATE TABLE IF NOT EXISTS `question_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_question_tag` (`question_id`,`tag_id`) USING BTREE,
  KEY `idx_question_id` (`question_id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  CONSTRAINT `fk_question_tag_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_question_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='问题标签关联表';

-- 点赞表
CREATE TABLE IF NOT EXISTS `vote` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型：QUESTION/ANSWER/COMMENT',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `vote_type` tinyint NOT NULL COMMENT '点赞类型：1表示点赞，-1表示踩',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_target_user` (`target_type`,`target_id`,`user_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `fk_vote_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_question` (`user_id`,`question_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `fk_collect_question` (`question_id`) USING BTREE,
  CONSTRAINT `fk_collect_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='收藏表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者用户名',
  `sender_avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者 头像',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `receiver_username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者用户名',
  `receiver_avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者头像',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `question_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sender_receiver` (`sender_id`,`receiver_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `question_id` bigint DEFAULT NULL COMMENT '相关问题ID',
  `answer_id` bigint DEFAULT NULL COMMENT '相关回答ID',
  `type` varchar(20) NOT NULL COMMENT '通知类型: LIKE/ANSWER',
  `title` varchar(200) DEFAULT NULL COMMENT '通知标题',
  `content` varchar(500) DEFAULT NULL COMMENT '通知内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `from_user_id` bigint DEFAULT NULL COMMENT '触发通知的用户ID',
  `from_username` varchar(50) DEFAULT NULL COMMENT '触发通知的用户名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB AUTO_INCREMENT=377 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通知表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TechQA' COMMENT '网站名称',
  `site_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网站描述',
  `background_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'gradient' COMMENT '背景类型: gradient-渐变, image-图片, color-纯色',
  `background_value` text COLLATE utf8mb4_unicode_ci COMMENT '背景值: 渐变CSS或图片URL 或颜色值',
  `layout_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'default' COMMENT '布局类型: default-默认, compact-紧凑, wide-宽屏',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Logo图片URL',
  `favicon_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网站图标URL',
  `primary_color` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '#ec4899' COMMENT '主题色',
  `secondary_color` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '#8b5cf6' COMMENT '辅助色',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 积分配置表
CREATE TABLE IF NOT EXISTS `points_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_chat_cost` int DEFAULT '10',
  `repeat_chat_cost` int DEFAULT '5',
  `message_cost` int DEFAULT '1',
  `question_reward` int DEFAULT '10',
  `answer_reward` int DEFAULT '5',
  `first_contact_reward` int DEFAULT '10',
  `repeat_contact_reward` int DEFAULT '5',
  `reply_reward` int DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 热门问题配置表
CREATE TABLE IF NOT EXISTS `hot_question_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `view_weight` double DEFAULT '0.3',
  `answer_weight` double DEFAULT '0.4',
  `vote_weight` double DEFAULT '0.3',
  `time_decay` double DEFAULT '0.9',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认配置数据
INSERT IGNORE INTO `system_config` (`id`, `site_name`, `site_description`, `background_type`, `background_value`, `layout_type`, `logo_url`, `favicon_url`, `primary_color`, `secondary_color`) VALUES
(1, 'IT技术问答社区', '探索技术前沿，分享编程智慧，共同成长进步', 'gradient', 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 'default', NULL, NULL, '#ec4899', '#8b5cf6');

INSERT IGNORE INTO `points_config` (`id`, `first_chat_cost`, `repeat_chat_cost`, `message_cost`, `question_reward`, `answer_reward`, `first_contact_reward`, `repeat_contact_reward`, `reply_reward`) VALUES
(1, 10, 5, 1, 10, 5, 10, 5, 1);

INSERT IGNORE INTO `hot_question_config` (`id`, `view_weight`, `answer_weight`, `vote_weight`, `time_decay`) VALUES
(1, 0.3, 0.4, 0.3, 0.9);

-- 插入默认标签
INSERT IGNORE INTO `tag` (`id`, `name`, `description`) VALUES
(1, 'Java', 'Java 编程语言相关问题'),
(2, 'Python', 'Python 编程语言相关问题'),
(3, 'JavaScript', 'JavaScript 编程语言相关问题'),
(4, '前端', '前端开发相关问题'),
(5, '后端', '后端开发相关问题'),
(6, '数据库', '数据库相关问题'),
(7, '算法', '算法与数据结构相关问题'),
(8, '网络', '网络与安全相关问题'),
(9, '操作系统', '操作系统相关问题'),
(10, '工具', '开发工具相关问题');

-- 插入默认管理员用户（密码：123456）
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `email`, `role`, `points`) VALUES
(1, 'admin', '$2a$10$eX0Wj5Kt4yWX82bC7b7aBunLq9Q6B4Q2Q2Q2Q2Q2Q2Q2Q2Q2Q2Q', 'admin@example.com', 'ADMIN', 1000);

-- 插入测试用户
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `email`, `role`, `points`) VALUES
(2, 'user1', '$2a$10$eX0Wj5Kt4yWX82bC7b7aBunLq9Q6B4Q2Q2Q2Q2Q2Q2Q2Q2Q2Q2Q', 'user1@example.com', 'USER', 100),
(3, 'user2', '$2a$10$eX0Wj5Kt4yWX82bC7b7aBunLq9Q6B4Q2Q2Q2Q2Q2Q2Q2Q2Q2Q2Q', 'user2@example.com', 'USER', 100);

-- 插入测试问题
INSERT IGNORE INTO `question` (`id`, `title`, `content`, `user_id`, `status`) VALUES
(1, '如何学习 Java 编程？', '我是一名初学者，想学习 Java 编程，应该从哪里开始？有什么推荐的学习资源？', 2, 'OPEN'),
(2, 'Python 有哪些常用的数据分析库？', '我想学习 Python 数据分析，有哪些常用的库可以推荐？', 3, 'OPEN');

-- 插入测试回答
INSERT IGNORE INTO `answer` (`id`, `question_id`, `user_id`, `content`, `is_accepted`) VALUES
(1, 1, 3, '学习 Java 编程可以从以下几个方面入手：1. 学习 Java 基础语法；2. 了解面向对象编程概念；3. 学习常用的 Java 库；4. 实践项目开发。推荐的学习资源有 Oracle 官方文档、《Java 核心技术》等。', 0),
(2, 2, 2, 'Python 常用的数据分析库有：1. NumPy - 用于数值计算；2. Pandas - 用于数据处理和分析；3. Matplotlib - 用于数据可视化；4. Scikit-learn - 用于机器学习。', 0);

-- 插入测试评论
INSERT IGNORE INTO `comment` (`id`, `target_type`, `target_id`, `user_id`, `content`) VALUES
(1, 'QUESTION', 1, 3, '这个问题很有价值，我也想知道答案。'),
(2, 'ANSWER', 1, 2, '感谢回答，很有帮助！');

-- 插入测试问题标签关联
INSERT IGNORE INTO `question_tag` (`id`, `question_id`, `tag_id`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 2, 7);

-- 插入测试点赞
INSERT IGNORE INTO `vote` (`id`, `target_type`, `target_id`, `user_id`, `vote_type`) VALUES
(1, 'QUESTION', 1, 3, 1),
(2, 'ANSWER', 1, 2, 1),
(3, 'QUESTION', 2, 2, 1);

-- 插入测试收藏
INSERT IGNORE INTO `collect` (`id`, `user_id`, `question_id`) VALUES
(1, 2, 2),
(2, 3, 1);

-- 插入测试消息
INSERT IGNORE INTO `message` (`id`, `sender_id`, `sender_username`, `receiver_id`, `receiver_username`, `content`, `is_read`, `question_id`) VALUES
(1, 2, 'user1', 3, 'user2', '你好，关于 Java 学习的问题，我有一些经验可以分享。', 0, 1),
(2, 3, 'user2', 2, 'user1', '谢谢你的回答，我会按照你的建议学习的。', 0, 1);

-- 插入测试通知
INSERT IGNORE INTO `notification` (`id`, `user_id`, `question_id`, `answer_id`, `type`, `title`, `content`, `is_read`, `from_user_id`, `from_username`) VALUES
(1, 2, 1, 1, 'ANSWER', '你的问题有新回答', 'user2 回答了你的问题：如何学习 Java 编程？', 0, 3, 'user2'),
(2, 3, 2, 2, 'ANSWER', '你的问题有新回答', 'user1 回答了你的问题：Python 有哪些常用的数据分析库？', 0, 2, 'user1'),
(3, 3, 1, NULL, 'LIKE', '你的问题被点赞', 'user2 点赞了你的问题：如何学习 Java 编程？', 0, 2, 'user1');