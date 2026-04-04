-- 添加登录状态字段到 user 表
ALTER TABLE user ADD COLUMN IF NOT EXISTS login_token VARCHAR(255) DEFAULT NULL AFTER security_answer;
ALTER TABLE user ADD COLUMN IF NOT EXISTS login_time DATETIME DEFAULT NULL AFTER login_token;

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_login_token ON user(login_token);

-- 更新现有用户数据（如果有的话）
UPDATE user SET login_token = NULL, login_time = NULL WHERE login_token IS NULL AND login_time IS NULL;
