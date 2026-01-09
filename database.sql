/* 
  养老院管理系统完整数据库脚本 
  适配MySQL 5.5.28，包含所有表结构、触发器、测试数据 
  创建顺序：无外键表 → 基础核心表 → 依赖表 → 触发器 → 测试数据 
 */ 
 SET NAMES utf8mb4; 
 SET FOREIGN_KEY_CHECKS = 0; 
 
 -- ====================================== 
 -- 1. 用户表（无外键依赖） 
 -- ====================================== 
 DROP TABLE IF EXISTS `users`; 
 CREATE TABLE `users`  ( 
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID', 
   `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名', 
   `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（加密存储）', 
   `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user' COMMENT '角色（admin/user）', 
   `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态（0禁用，1启用）', 
   `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间', 
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
   `resident_id` bigint(20) NULL DEFAULT NULL COMMENT '绑定的老人ID，对应residents表的id', 
   PRIMARY KEY (`id`) USING BTREE, 
   UNIQUE INDEX `username`(`username`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Compact; 
 
 -- ====================================== 
 -- 2. 房间表（无外键依赖） 
 -- ====================================== 
 DROP TABLE IF EXISTS `rooms`; 
 CREATE TABLE `rooms`  ( 
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '房间ID', 
   `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '房间号', 
   `room_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '房间类型', 
   `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态（1空闲，2已入住，3维修中）', 
   `bed_count` int(11) NOT NULL DEFAULT 0 COMMENT '床位数', 
   `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房间描述', 
   `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间', 
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
   `repair_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '维修原因', 
   `repair_start_date` datetime NULL DEFAULT NULL COMMENT '维修开始日期', 
   `repair_end_date` datetime NULL DEFAULT NULL COMMENT '维修结束日期', 
   PRIMARY KEY (`id`) USING BTREE, 
   UNIQUE INDEX `room_number`(`room_number`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间表' ROW_FORMAT = Compact; 
 
 -- ====================================== 
 -- 3. 护理级别表（无外键依赖） 
 -- ====================================== 
 DROP TABLE IF EXISTS `nursing_levels`; 
 CREATE TABLE `nursing_levels`  ( 
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '护理级别ID', 
   `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '级别名称', 
   `level` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, 
   `description` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '级别描述', 
   `base_cost` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '基础费用（元/月）', 
   `nurse_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '负责该护理级别的护工姓名', 
   `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间', 
   `update_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间', 
   `status` tinyint(4) DEFAULT 1 COMMENT '状态（1-启用，0-禁用）', 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '护理级别表' ROW_FORMAT = Compact; 
 
 -- ====================================== 
 -- 4. 护理内容表（无外键依赖） 
 -- ====================================== 
 DROP TABLE IF EXISTS `nursing_contents`; 
 CREATE TABLE `nursing_contents`  ( 
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '护理内容ID', 
   `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容名称', 
   `category` int(11) NOT NULL COMMENT '护理类别（1-日常护理，2-医疗护理，3-康复训练）', 
   `parent_id` int(11) NULL DEFAULT 0 COMMENT '上级内容ID（0表示无上级）', 
   `applicable_level` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '适用护理级别', 
   `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容描述', 
   `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间', 
   `update_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间', 
   `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态（1-启用，0-禁用）', 
   PRIMARY KEY (`id`) USING BTREE, 
   INDEX `idx_category`(`category`) USING BTREE, 
   INDEX `idx_parent_id`(`parent_id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '护理内容表' ROW_FORMAT = Compact; 
 
 -- ====================================== 
 -- 5. 服务套餐表（无外键依赖） 
 -- ====================================== 
 DROP TABLE IF EXISTS `service_packages`; 
 CREATE TABLE `service_packages`  ( 
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '服务套餐ID', 
   `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '套餐名称', 
   `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '套餐级别', 
   `price` decimal(10, 2) NOT NULL COMMENT '价格(元/月)', 
   `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '套餐描述', 
   `features` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务内容', 
   `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '启用' COMMENT '状态', 
   `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间', 
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务套餐表' ROW_FORMAT = Compact;