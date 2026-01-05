-- 护理管理系统数据库SQL语句

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS yiyang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE yiyang;

-- 1. 护理级别表
CREATE TABLE IF NOT EXISTS nursing_levels (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '护理级别ID',
    name VARCHAR(50) NOT NULL COMMENT '级别名称',
    level INT NOT NULL COMMENT '级别（1-基础，2-中级，3-高级）',
    description TEXT COMMENT '级别描述',
    base_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '基础费用（元/月）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
    UNIQUE KEY uk_level (level) COMMENT '级别唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理级别表';

-- 2. 护理内容表
CREATE TABLE IF NOT EXISTS nursing_contents (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '护理内容ID',
    name VARCHAR(100) NOT NULL COMMENT '内容名称',
    category INT NOT NULL COMMENT '护理类别（1-日常护理，2-医疗护理，3-康复训练）',
    parent_id INT DEFAULT 0 COMMENT '上级内容ID（0表示无上级）',
    description TEXT COMMENT '内容描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
    KEY idx_category (category) COMMENT '类别索引',
    KEY idx_parent_id (parent_id) COMMENT '上级ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理内容表';

-- 3. 护理记录表
CREATE TABLE IF NOT EXISTS nursing_records (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '护理记录ID',
    resident_id INT COMMENT '老人ID（关联resident表）',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    nursing_content VARCHAR(200) NOT NULL COMMENT '护理内容',
    nursing_content_id INT COMMENT '关联护理内容ID',
    execute_time DATETIME NOT NULL COMMENT '执行时间',
    executor VARCHAR(50) NOT NULL COMMENT '执行人',
    completed TINYINT DEFAULT 0 COMMENT '是否完成（1-已完成，0-未完成）',
    evaluation INT DEFAULT 2 COMMENT '效果评估（1-差，2-中，3-好）',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_resident_id (resident_id) COMMENT '老人ID索引',
    KEY idx_execute_time (execute_time) COMMENT '执行时间索引',
    KEY idx_completed (completed) COMMENT '完成状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理记录表';

-- 插入示例数据

-- 插入护理级别数据
INSERT INTO nursing_levels (name, level, description, base_cost) VALUES
('基础护理', 1, '提供基本生活照料，包括饮食、起居、卫生等', 3000.00),
('中级护理', 2, '在基础护理基础上，增加医疗护理和康复训练', 5000.00),
('高级护理', 3, '提供全面的医疗护理和康复训练，适合需要特殊照料的老人', 8000.00);

-- 插入护理内容数据

-- 日常护理主类别及子项目
INSERT INTO nursing_contents (name, category, parent_id, description) VALUES
('日常护理', 1, 0, '提供老人日常生活照料'),
('翻身', 1, 1, '定期帮助老人翻身，预防褥疮'),
('喂药', 1, 1, '按照医嘱帮助老人服药'),
('洗漱', 1, 1, '帮助老人进行洗漱清洁'),
('饮食照料', 1, 1, '帮助老人进食、饮水等'),
('衣物更换', 1, 1, '帮助老人更换衣物，保持清洁');

-- 医疗护理主类别及子项目
INSERT INTO nursing_contents (name, category, parent_id, description) VALUES
('医疗护理', 2, 0, '提供基本医疗护理服务'),
('测血压', 2, 7, '定期测量老人血压并记录'),
('测血糖', 2, 7, '定期测量老人血糖并记录'),
('生命体征监测', 2, 7, '监测老人体温、脉搏、呼吸等生命体征'),
('伤口护理', 2, 7, '帮助老人进行伤口清洁和护理'),
('用药指导', 2, 7, '指导老人正确用药');

-- 康复训练主类别及子项目
INSERT INTO nursing_contents (name, category, parent_id, description) VALUES
('康复训练', 3, 0, '提供基础康复训练服务'),
('基础肢体训练', 3, 13, '帮助老人进行肢体活动，预防肌肉萎缩'),
('关节活动训练', 3, 13, '帮助老人进行关节活动，保持关节灵活性'),
('平衡训练', 3, 13, '帮助老人进行平衡训练，预防跌倒'),
('步行训练', 3, 13, '帮助老人进行步行训练，提高行走能力');

-- 插入护理记录示例数据
INSERT INTO nursing_records (resident_name, nursing_content, execute_time, executor, completed, evaluation, remark) VALUES
('张三', '翻身', '2026-01-01 08:30:00', '护理员A', 1, 3, '翻身操作规范，老人无不适'),
('李四', '测血压', '2026-01-01 09:00:00', '护理员B', 1, 2, '血压略有偏高，已记录'),
('王五', '基础肢体训练', '2026-01-01 10:00:00', '康复师C', 1, 3, '训练效果良好，老人配合度高'),
('赵六', '喂药', '2026-01-01 12:00:00', '护理员A', 1, 3, '按时服药，无不良反应'),
('孙七', '测血糖', '2026-01-01 14:30:00', '护理员B', 1, 1, '血糖偏高，已通知医生'),
('周八', '洗漱', '2026-01-01 18:00:00', '护理员C', 1, 2, '洗漱完成，老人状态良好'),
('吴九', '伤口护理', '2026-01-02 09:00:00', '护士D', 1, 3, '伤口愈合良好，无感染迹象'),
('郑十', '关节活动训练', '2026-01-02 10:30:00', '康复师C', 1, 2, '关节活动度有所改善'),
('钱一', '平衡训练', '2026-01-02 14:00:00', '康复师C', 0, 2, '训练未完成，老人体力不支'),
('孙二', '步行训练', '2026-01-02 15:30:00', '康复师C', 1, 3, '步行距离有所增加');

-- 查询示例

-- 查询所有护理级别
SELECT * FROM nursing_levels;

-- 查询所有护理内容，按类别分组
SELECT * FROM nursing_contents ORDER BY category, parent_id, id;

-- 查询已完成的护理记录
SELECT * FROM nursing_records WHERE completed = 1 ORDER BY execute_time DESC;

-- 查询效果评估为"好"的护理记录
SELECT * FROM nursing_records WHERE evaluation = 3 ORDER BY execute_time DESC;

-- 查询某个老人的护理记录
SELECT * FROM nursing_records WHERE resident_name = '张三' ORDER BY execute_time DESC;
