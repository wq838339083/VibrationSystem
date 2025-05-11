-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS vibration_app;

-- 使用数据库
USE vibration_app;

-- 创建震动模式表
CREATE TABLE IF NOT EXISTS vibration_patterns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pattern VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 添加索引以提高查询性能
CREATE INDEX idx_created_at ON vibration_patterns(created_at);

-- 创建一个测试数据
INSERT INTO vibration_patterns (pattern, created_at) VALUES ('single', NOW());