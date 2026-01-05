-- 健康监测相关表结构

-- 血压记录表
CREATE TABLE IF NOT EXISTS health_blood_pressure (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resident_id BIGINT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    systolic INT NOT NULL COMMENT '收缩压(mmHg)',
    diastolic INT NOT NULL COMMENT '舒张压(mmHg)',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    notes VARCHAR(255) DEFAULT '' COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES resident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血压记录表';

-- 血糖记录表
CREATE TABLE IF NOT EXISTS health_blood_sugar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resident_id BIGINT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    level DECIMAL(5,2) NOT NULL COMMENT '血糖值(mmol/L)',
    status VARCHAR(20) NOT NULL COMMENT '测量状态：空腹、餐后、随机',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    notes VARCHAR(255) DEFAULT '' COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES resident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血糖记录表';

-- 心率记录表
CREATE TABLE IF NOT EXISTS health_heart_rate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resident_id BIGINT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    rate INT NOT NULL COMMENT '心率值(次/分)',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    notes VARCHAR(255) DEFAULT '' COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES resident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心率记录表';

-- 病史记录表
CREATE TABLE IF NOT EXISTS health_medical_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resident_id BIGINT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    record_type VARCHAR(20) NOT NULL COMMENT '记录类型：疾病诊断、手术记录',
    disease_name VARCHAR(100) NOT NULL COMMENT '疾病/手术名称',
    diagnose_date DATE NOT NULL COMMENT '诊断/手术日期',
    hospital VARCHAR(100) NOT NULL COMMENT '医院名称',
    doctor VARCHAR(50) NOT NULL COMMENT '医生姓名',
    notes VARCHAR(500) DEFAULT '' COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES resident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病史记录表';

-- 用药记录表
CREATE TABLE IF NOT EXISTS health_medication (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resident_id BIGINT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    medicine_name VARCHAR(100) NOT NULL COMMENT '药品名称',
    dosage VARCHAR(50) NOT NULL COMMENT '剂量',
    frequency VARCHAR(50) NOT NULL COMMENT '服用频率',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE DEFAULT NULL COMMENT '结束日期',
    notes VARCHAR(500) DEFAULT '' COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resident_id) REFERENCES resident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药记录表';

-- 健康预警表
CREATE TABLE IF NOT EXISTS health_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resident_id BIGINT NOT NULL COMMENT '老人ID',
    resident_name VARCHAR(50) NOT NULL COMMENT '老人姓名',
    alert_type VARCHAR(20) NOT NULL COMMENT '预警类型：血压异常、血糖异常、心率异常',
    alert_level VARCHAR(10) NOT NULL COMMENT '预警级别：danger、warning',
    title VARCHAR(100) NOT NULL COMMENT '预警标题',
    description VARCHAR(255) NOT NULL COMMENT '预警描述',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    is_handled TINYINT(1) DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理',
    handled_time DATETIME DEFAULT NULL COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (resident_id) REFERENCES resident(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康预警表';

-- 为健康记录表创建索引
CREATE INDEX idx_bp_resident_id ON health_blood_pressure(resident_id);
CREATE INDEX idx_bp_measure_time ON health_blood_pressure(measure_time);

CREATE INDEX idx_bs_resident_id ON health_blood_sugar(resident_id);
CREATE INDEX idx_bs_measure_time ON health_blood_sugar(measure_time);

CREATE INDEX idx_hr_resident_id ON health_heart_rate(resident_id);
CREATE INDEX idx_hr_measure_time ON health_heart_rate(measure_time);

CREATE INDEX idx_mh_resident_id ON health_medical_history(resident_id);
CREATE INDEX idx_mh_record_type ON health_medical_history(record_type);

CREATE INDEX idx_med_resident_id ON health_medication(resident_id);
CREATE INDEX idx_med_medicine_name ON health_medication(medicine_name);

CREATE INDEX idx_alerts_resident_id ON health_alerts(resident_id);
CREATE INDEX idx_alerts_is_handled ON health_alerts(is_handled);
CREATE INDEX idx_alerts_create_time ON health_alerts(create_time);
