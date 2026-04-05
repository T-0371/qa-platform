-- 为 message 表添加 question_id 字段
ALTER TABLE message ADD COLUMN question_id BIGINT AFTER created_at;

-- 添加索引以提高查询性能
CREATE INDEX idx_question_id ON message(question_id);

-- 为 message 表添加 receiver_avatar 字段
ALTER TABLE message ADD COLUMN receiver_avatar VARCHAR(255) DEFAULT NULL COMMENT '接收者头像' AFTER receiver_username;