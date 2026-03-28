-- 添加密保字段到用户表
ALTER TABLE `user` ADD COLUMN `security_question` VARCHAR(100) NULL COMMENT '密保问题' AFTER `points`;
ALTER TABLE `user` ADD COLUMN `security_answer` VARCHAR(100) NULL COMMENT '密保答案' AFTER `security_question`;
