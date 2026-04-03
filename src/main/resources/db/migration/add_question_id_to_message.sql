-- 为message表添加questionId字段
ALTER TABLE message ADD COLUMN question_id BIGINT AFTER created_at;

-- 添加索引以提高查询性能
CREATE INDEX idx_question_id ON message(question_id);