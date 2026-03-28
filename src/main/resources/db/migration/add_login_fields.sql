-- 添加登录状态字段
ALTER TABLE user ADD COLUMN login_token VARCHAR(255) DEFAULT NULL AFTER security_answer;
ALTER TABLE user ADD COLUMN login_time DATETIME DEFAULT NULL AFTER login_token;

CREATE INDEX idx_login_token ON user(login_token);
