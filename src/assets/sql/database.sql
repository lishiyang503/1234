-- 创建数据库
CREATE DATABASE IF NOT EXISTS yiyang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE yiyang;

-- 1. 用户表（用于登录认证）
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色（admin/user）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0禁用，1启用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 房间表
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '房间ID',
    room_number VARCHAR(20) NOT NULL UNIQUE COMMENT '房间号',
    room_type VARCHAR(50) NOT NULL COMMENT '房间类型',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1空闲，2已入住，3维修中）',
    bed_count INT NOT NULL DEFAULT 0 COMMENT '床位数',
    description TEXT COMMENT '房间描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 3. 床位表
CREATE TABLE IF NOT EXISTS beds (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '床位ID',
    bed_number VARCHAR(20) NOT NULL COMMENT '床位号',
    room_id INT NOT NULL COMMENT '所属房间ID',
    resident_id INT DEFAULT NULL COMMENT '入住老人ID（关联residents表）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1空闲，2已入住，3维修中）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位表';

-- 4. 膳食需求表
CREATE TABLE IF NOT EXISTS diet_requirements (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '膳食需求ID',
    resident_id INT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    bed_number VARCHAR(20) NOT NULL COMMENT '床位号',
    diet_type VARCHAR(50) NOT NULL COMMENT '饮食类型（diabetes/hypertension等）',
    description TEXT NOT NULL COMMENT '详细说明',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='膳食需求表';

-- 5. 入住登记表
CREATE TABLE IF NOT EXISTS residents (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '老人ID',
    name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    age INT NOT NULL COMMENT '年龄',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    emergency_contact VARCHAR(50) NOT NULL COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) NOT NULL COMMENT '紧急联系电话',
    entry_date DATE NOT NULL COMMENT '入住日期',
    exit_date DATE DEFAULT NULL COMMENT '退房日期',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    bed_number VARCHAR(20) NOT NULL COMMENT '床位号',
    status VARCHAR(20) NOT NULL DEFAULT '入住' COMMENT '状态（入住/退房）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住登记表';

-- 6. 健康档案表
CREATE TABLE IF NOT EXISTS health_records (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '健康档案ID',
    resident_id INT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    bed_number VARCHAR(20) NOT NULL COMMENT '床位号',
    medical_history TEXT NOT NULL COMMENT '既往病史',
    allergy_history TEXT NOT NULL COMMENT '过敏史',
    blood_type VARCHAR(5) NOT NULL COMMENT '血型',
    height DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
    weight DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    health_notes TEXT COMMENT '健康状况说明',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

-- 7. 入住历史表
CREATE TABLE IF NOT EXISTS resident_history (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '历史记录ID',
    resident_id INT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    bed_number VARCHAR(20) NOT NULL COMMENT '床位号',
    entry_date DATE NOT NULL COMMENT '入住日期',
    exit_date DATE DEFAULT NULL COMMENT '退房日期',
    duration INT DEFAULT NULL COMMENT '入住时长（天）',
    reason VARCHAR(255) DEFAULT NULL COMMENT '退房原因',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住历史表';

-- 8. 服务套餐表
CREATE TABLE IF NOT EXISTS service_packages (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '服务套餐ID',
    name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    level VARCHAR(20) NOT NULL COMMENT '套餐级别（基础/中级/高级）',
    price DECIMAL(10,2) NOT NULL COMMENT '价格(元/月)',
    description TEXT NOT NULL COMMENT '套餐描述',
    features JSON NOT NULL COMMENT '服务内容（JSON格式）',
    status VARCHAR(10) NOT NULL DEFAULT '启用' COMMENT '状态（启用/禁用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务套餐表';

-- 9. 服务购买记录表
CREATE TABLE IF NOT EXISTS service_purchase_records (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '购买记录ID',
    resident_id INT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    bed_number VARCHAR(20) NOT NULL COMMENT '床位号',
    service_id INT NOT NULL COMMENT '服务套餐ID',
    service_name VARCHAR(100) NOT NULL COMMENT '服务套餐名称',
    service_level VARCHAR(20) NOT NULL COMMENT '套餐级别',
    price DECIMAL(10,2) NOT NULL COMMENT '购买金额(元)',
    purchase_date DATE NOT NULL COMMENT '购买日期',
    expire_date DATE NOT NULL COMMENT '到期日期',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    status VARCHAR(10) NOT NULL DEFAULT '有效' COMMENT '状态（有效/过期）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    FOREIGN KEY (resident_id) REFERENCES residents(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES service_packages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务购买记录表';

-- 添加测试数据

-- 用户表测试数据（密码均为：123456，加密存储）
INSERT INTO users (username, password, role, status) VALUES 
('admin', '$2a$10$e4e5b0d8f7a6c5b4a3d2e1f0a9b8c7d6e5f4a3b2c1d0a9b8c7d6e5f4a3b2', 'admin', 1),
('user', '$2a$10$e4e5b0d8f7a6c5b4a3d2e1f0a9b8c7d6e5f4a3b2c1d0a9b8c7d6e5f4a3b2', 'user', 1);

-- 房间表测试数据
INSERT INTO rooms (room_number, room_type, status, bed_count, description) VALUES 
('101', '单人间', 1, 1, '朝南，带独立卫生间'),
('102', '双人间', 1, 2, '朝西，带公共卫生间'),
('103', '三人间', 1, 3, '朝北，带公共卫生间');

-- 床位表测试数据
INSERT INTO beds (bed_number, room_id, status) VALUES 
('1', 1, 1),
('1', 2, 1),
('2', 2, 1),
('1', 3, 1),
('2', 3, 1),
('3', 3, 1);

-- 服务套餐测试数据
INSERT INTO service_packages (name, level, price, description, features) VALUES 
('基础护理套餐', '基础', 2000.00, '提供基本的日常护理服务', '["日常护理", "餐饮服务", "清洁服务"]'),
('中级护理套餐', '中级', 4000.00, '提供更全面的护理服务', '["日常护理", "医疗护理", "餐饮服务", "清洁服务", "文化娱乐"]'),
('高级护理套餐', '高级', 6000.00, '提供全方位的高级护理服务', '["日常护理", "医疗护理", "康复训练", "心理咨询", "文化娱乐", "餐饮服务", "清洁服务"]');
