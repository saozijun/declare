/*
 Navicat Premium Data Transfer

 Source Server         : l2
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : declare

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 02/05/2025 16:30:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_receiver`(`sender_id`, `receiver_id`) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id`, `is_read`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (1, 23, 2, '嗨', 1, '2025-05-01 21:45:01');
INSERT INTO `chat_message` VALUES (2, 2, 23, 'hello', 1, '2025-05-01 22:22:45');
INSERT INTO `chat_message` VALUES (3, 2, 23, '很急', 1, '2025-05-01 22:23:45');
INSERT INTO `chat_message` VALUES (4, 25, 24, '你好', 1, '2025-05-02 01:35:51');
INSERT INTO `chat_message` VALUES (5, 24, 25, '1', 0, '2025-05-02 16:29:29');

-- ----------------------------
-- Table structure for expert
-- ----------------------------
DROP TABLE IF EXISTS `expert`;
CREATE TABLE `expert`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `qualification` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `expertise` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `education` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `work_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '审核中',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `expert_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expert
-- ----------------------------
INSERT INTO `expert` VALUES (1, 2, '用户', '男', '13325154154', '1234@qq.com', '资质证明', '金融', '教育背景', '工作经验', '主要成绩', '已通过');
INSERT INTO `expert` VALUES (2, 25, '147', '女', '13325154154', '123@qq.com', '15', '阿萨德', '阿萨德', '艾师傅', '艾师傅', '已通过');
INSERT INTO `expert` VALUES (3, 26, '小王', '男', '17621700571', '1@qq.com', '阿是', '阿萨德', '', '', '', '已通过');

-- ----------------------------
-- Table structure for expert_assignment
-- ----------------------------
DROP TABLE IF EXISTS `expert_assignment`;
CREATE TABLE `expert_assignment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `application_id` bigint NOT NULL COMMENT '项目申报ID',
  `expert_id` bigint NOT NULL COMMENT '专家用户ID',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '分配状态：0-待评审 1-已评审',
  `assign_time` datetime(0) NULL DEFAULT NULL COMMENT '分配时间',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '评审完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id`) USING BTREE,
  INDEX `idx_expert_id`(`expert_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expert_assignment
-- ----------------------------
INSERT INTO `expert_assignment` VALUES (1, 6, 2, '1', '2025-05-01 18:56:01', '2025-05-01 19:46:18');
INSERT INTO `expert_assignment` VALUES (2, 7, 2, '1', '2025-05-01 23:21:56', '2025-05-01 23:35:24');
INSERT INTO `expert_assignment` VALUES (3, 7, 25, '1', '2025-05-01 23:21:56', '2025-05-02 00:29:11');
INSERT INTO `expert_assignment` VALUES (4, 8, 25, '1', '2025-05-02 16:10:43', '2025-05-02 16:11:56');

-- ----------------------------
-- Table structure for expert_review
-- ----------------------------
DROP TABLE IF EXISTS `expert_review`;
CREATE TABLE `expert_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignment_id` bigint NOT NULL COMMENT '专家分配ID',
  `application_id` bigint NOT NULL COMMENT '项目申报ID',
  `expert_id` bigint NOT NULL COMMENT '专家用户ID',
  `technical_feasibility_score` int NULL DEFAULT 0 COMMENT '技术可行性得分(20分)',
  `innovation_score` int NULL DEFAULT 0 COMMENT '创新性得分(15分)',
  `maturity_score` int NULL DEFAULT 0 COMMENT '成熟度得分(15分)',
  `technical_remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技术评审备注',
  `budget_reasonability_score` int NULL DEFAULT 0 COMMENT '预算合理性得分(15分)',
  `cost_benefit_score` int NULL DEFAULT 0 COMMENT '成本效益得分(15分)',
  `contract_terms_score` int NULL DEFAULT 0 COMMENT '合同条款得分(10分)',
  `business_remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商务评审备注',
  `risk_identification_score` int NULL DEFAULT 0 COMMENT '风险识别得分(10分)',
  `compliance_score` int NULL DEFAULT 0 COMMENT '合规性得分(10分)',
  `risk_compliance_remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '风险与合规评审备注',
  `total_score` int NULL DEFAULT 0 COMMENT '总分',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '评审时间',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '评审状态：0-草稿 1-已提交',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_assignment_id`(`assignment_id`) USING BTREE,
  INDEX `idx_application_id`(`application_id`) USING BTREE,
  INDEX `idx_expert_id`(`expert_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expert_review
-- ----------------------------
INSERT INTO `expert_review` VALUES (1, 1, 6, 2, 5, 7, 11, '66', 8, 12, 3, '嘎嘎', 7, 7, 'ag ', 60, '2025-05-01 19:46:18', '1');
INSERT INTO `expert_review` VALUES (2, 2, 7, 2, 17, 15, 15, '很好', 15, 15, 10, '拉满了', 9, 9, '非常不错', 105, '2025-05-01 23:35:24', '1');
INSERT INTO `expert_review` VALUES (5, 3, 7, 25, 17, 12, 13, '好', 11, 12, 9, '可以', 8, 8, '还好', 90, '2025-05-02 00:29:11', '1');
INSERT INTO `expert_review` VALUES (6, 4, 8, 25, 12, 7, 13, '', 12, 11, 9, '', 8, 10, '', 82, '2025-05-02 16:11:56', '1');

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目描述',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '项目状态：0-待发布，1-已发布',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `leader` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `budget` decimal(12, 2) NULL DEFAULT NULL COMMENT '项目预算',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES (1, '123', '哈哈', 1, NULL, '1', '2025-05-01 12:59:49', '2025-05-01 13:52:00', '123', '123', '2025-05-02', '2025-05-17', NULL);
INSERT INTO `project` VALUES (3, '智慧城市平台建设', '建设城市大数据平台，整合各类城市资源数据，实现智能化管理和服务', 1, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '张三', '13800138001', '2024-08-01', '2025-07-31', 5000000.00);
INSERT INTO `project` VALUES (4, '乡村道路改造工程', '对农村地区道路进行升级改造，提高通行能力和安全性', 2, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '李四', '13800138002', '2024-09-01', '2025-03-31', 3000000.00);
INSERT INTO `project` VALUES (5, '新型冠状病毒快速检测试剂研发', '研发快速、准确的新冠病毒检测试剂，提高检测效率', 3, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '王五', '13800138003', '2024-07-15', '2025-01-15', 2000000.00);
INSERT INTO `project` VALUES (6, '在线教育平台建设', '构建覆盖K12阶段的在线教育平台，提供优质教育资源', 4, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '赵六', '13800138004', '2024-08-10', '2025-08-09', 1500000.00);
INSERT INTO `project` VALUES (7, '城市垃圾分类处理系统', '建设城市垃圾分类收集、运输和处理系统，提高资源利用率', 5, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '钱七', '13800138005', '2024-09-15', '2025-09-14', 4000000.00);
INSERT INTO `project` VALUES (8, '高效节水灌溉技术推广', '推广应用高效节水灌溉技术，提高农业用水效率', 6, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '孙八', '13800138006', '2024-10-01', '2025-04-30', 1000000.00);
INSERT INTO `project` VALUES (9, '传统文化数字化保护工程', '对非物质文化遗产进行数字化采集、整理和保护', 7, NULL, '0', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '周九', '13800138007', '2024-11-01', '2025-10-31', 2500000.00);
INSERT INTO `project` VALUES (10, '社区养老服务中心建设', '建设社区养老服务中心，提供专业化、多元化的养老服务', 8, NULL, '0', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '吴十', '13800138008', '2024-12-01', '2025-11-30', 3500000.00);
INSERT INTO `project` VALUES (11, '5G网络安全防护系统', '研发5G网络环境下的安全防护系统，保障网络通信安全', 1, NULL, '0', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '郑十一', '13800138009', '2024-08-15', '2025-08-14', 4500000.00);
INSERT INTO `project` VALUES (12, '城市地下管网监测系统', '建设城市地下管网实时监测系统，预防安全事故', 2, NULL, '0', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '冯十二', '13800138010', '2024-09-10', '2025-03-09', 3200000.00);
INSERT INTO `project` VALUES (13, '中医药现代化研究', '对传统中医药进行现代化研究，提高临床疗效', 3, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '陈十三', '13800138011', '2024-07-20', '2025-07-19', 2800000.00);
INSERT INTO `project` VALUES (14, '职业技能在线培训平台', '构建面向各行业的职业技能在线培训平台，提升劳动者素质', 4, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '楚十四', '13800138012', '2024-08-05', '2025-02-04', 1800000.00);
INSERT INTO `project` VALUES (15, '新能源汽车充电桩网络建设', '建设城市新能源汽车充电桩网络，促进绿色出行', 5, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '魏十五', '13800138013', '2024-07-01', '2025-06-30', 3800000.00);
INSERT INTO `project` VALUES (16, '智慧农业物联网示范基地', '建设基于物联网技术的智慧农业示范基地，推动农业现代化', 6, NULL, '0', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '蒋十六', '13800138014', '2024-10-15', '2025-10-14', 2600000.00);
INSERT INTO `project` VALUES (17, '数字博物馆建设', '建设数字化博物馆，提供沉浸式文化体验', 7, NULL, '0', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '沈十七', '13800138015', '2024-11-15', '2025-05-14', 2200000.00);
INSERT INTO `project` VALUES (18, '智能社区安防系统', '建设基于人工智能的社区安防系统，提高居民安全感', 8, NULL, '1', '2025-05-02 02:11:04', '2025-05-02 02:11:04', '韩十八', '13800138016', '2024-07-10', '2025-01-09', 1200000.00);

-- ----------------------------
-- Table structure for project_application
-- ----------------------------
DROP TABLE IF EXISTS `project_application`;
CREATE TABLE `project_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `user_id` bigint NOT NULL COMMENT '申报用户ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '申报状态：0-待审核，1-已通过，2-已拒绝，3-已分配，4-已评审',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `organization` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申报单位',
  `leader` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目负责人',
  `period` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目周期',
  `budget` decimal(20, 2) NOT NULL COMMENT '预算总额',
  `technical_solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技术方案',
  `expected_results` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '预期成果',
  `risk_identification` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '风险识别',
  `economic_benefits` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '经济效益',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_project_id`(`project_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目申报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_application
-- ----------------------------
INSERT INTO `project_application` VALUES (6, 1, 23, '4', '123', '456', '456', '456', 456.00, '456', '456', '456', '456', '2025-05-01 17:30:50', '2025-05-01 17:56:32');
INSERT INTO `project_application` VALUES (7, 1, 24, '4', '123', 'shenbaodanwei', 'fuzeren', '5个月左右', 5000.00, '我的技术方案是', '预期很高', '一般般', '经济效益啊', '2025-05-01 23:17:33', '2025-05-01 23:19:41');
INSERT INTO `project_application` VALUES (8, 18, 23, '4', '智能社区安防系统', '111', 'xcv', '3', 16542.00, '111', '111', '111', '111', '2025-05-02 16:09:47', '2025-05-02 16:10:28');

-- ----------------------------
-- Table structure for project_category
-- ----------------------------
DROP TABLE IF EXISTS `project_category`;
CREATE TABLE `project_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `create_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_category
-- ----------------------------
INSERT INTO `project_category` VALUES (1, '信息技术', '包括软件开发、网络安全、人工智能等信息技术相关项目', '2025-05-02 02:10:40', '2025-05-02 02:10:40');
INSERT INTO `project_category` VALUES (2, '基础设施', '包括道路、桥梁、水利等基础设施建设项目', '2025-05-02 02:10:42', '2025-05-02 02:10:42');
INSERT INTO `project_category` VALUES (3, '医疗健康', '包括医疗设备、药品研发、健康管理等项目', '2025-05-02 02:10:44', '2025-05-02 02:10:44');
INSERT INTO `project_category` VALUES (4, '教育培训', '包括教育资源开发、培训体系建设等项目', '2025-05-02 02:10:46', '2025-05-02 02:10:46');
INSERT INTO `project_category` VALUES (5, '环境保护', '包括环境治理、垃圾处理、清洁能源等项目', '2025-05-02 02:10:48', '2025-05-02 02:10:48');
INSERT INTO `project_category` VALUES (6, '农业发展', '包括农业技术、农产品加工、乡村振兴等项目', '2025-05-02 02:10:50', '2025-05-02 02:10:50');
INSERT INTO `project_category` VALUES (7, '文化创意', '包括文化产业、创意设计、传媒娱乐等项目', '2025-05-02 02:10:52', '2025-05-02 02:10:52');
INSERT INTO `project_category` VALUES (8, '社会服务', '包括社区建设、公共服务、养老服务等项目', '2025-05-02 02:10:55', '2025-05-02 02:10:55');

-- ----------------------------
-- Table structure for project_expert
-- ----------------------------
DROP TABLE IF EXISTS `project_expert`;
CREATE TABLE `project_expert`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `expert_id` bigint NOT NULL COMMENT '专家ID',
  `review` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `score` int NULL DEFAULT NULL COMMENT '评分',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '评审状态：0-未评审，1-已评审',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_project_id`(`project_id`) USING BTREE,
  INDEX `idx_expert_id`(`expert_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目专家关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_expert
-- ----------------------------

-- ----------------------------
-- Table structure for sys_banner
-- ----------------------------
DROP TABLE IF EXISTS `sys_banner`;
CREATE TABLE `sys_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_banner
-- ----------------------------
INSERT INTO `sys_banner` VALUES (1, '123', 'http://localhost:9091/file/db08ee22c5b74bcca472d30524f24dd5.jpg');
INSERT INTO `sys_banner` VALUES (2, '22', 'http://localhost:9091/file/9dd158ce4f7f4a12940ed6d17d7a01c8.png');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `url` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '下载链接',
  `type` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `md5` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件md5',
  `size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `enable` tinyint NULL DEFAULT 1 COMMENT '是否禁用(1-启用, 1-禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 191 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件上传的列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (183, '屏幕截图 2024-03-05 213717.png', 'http://localhost:9091/file/9dd158ce4f7f4a12940ed6d17d7a01c8.png', 'png', 'd231283fbbec525b7288bf978c5b06c0', 4, 1, '2025-05-01 16:12:44', '2025-05-01 16:12:44', 0);
INSERT INTO `sys_file` VALUES (184, '屏幕截图 2024-05-06 192246.png', 'http://localhost:9091/file/c682ee99f93f417287bae57a98a99c20.png', 'png', '8152e98d3a5afad8e0b42edf9a1a001e', 0, 1, '2025-05-01 16:14:39', '2025-05-01 16:14:39', 0);
INSERT INTO `sys_file` VALUES (185, '屏幕截图 2024-03-05 213717.png', 'http://localhost:9091/file/9dd158ce4f7f4a12940ed6d17d7a01c8.png', 'png', 'd231283fbbec525b7288bf978c5b06c0', 4, 1, '2025-05-01 16:14:43', '2025-05-01 16:14:43', 0);
INSERT INTO `sys_file` VALUES (186, '4FRclTDmDCGW63ae5b2d67c16c296f18e33b5578a214.png', 'http://localhost:9091/file/1b3ca59188614cc6964c21534df77425.png', 'png', '63ae5b2d67c16c296f18e33b5578a214', 8, 1, '2025-05-01 20:30:58', '2025-05-01 20:30:58', 0);
INSERT INTO `sys_file` VALUES (187, 'XSzUx6zQubnI63ae5b2d67c16c296f18e33b5578a214.png', 'http://localhost:9091/file/1b3ca59188614cc6964c21534df77425.png', 'png', '63ae5b2d67c16c296f18e33b5578a214', 8, 1, '2025-05-01 20:32:04', '2025-05-01 20:32:04', 0);
INSERT INTO `sys_file` VALUES (188, 'mtFstpGNUOds63ae5b2d67c16c296f18e33b5578a214.png', 'http://localhost:9091/file/1b3ca59188614cc6964c21534df77425.png', 'png', '63ae5b2d67c16c296f18e33b5578a214', 8, 1, '2025-05-01 20:32:17', '2025-05-01 20:32:17', 0);
INSERT INTO `sys_file` VALUES (189, '62d6ed1de68355384a9e556da152772db8847874f192-9zqEXz_fw1200.jpg', 'http://localhost:9091/file/db08ee22c5b74bcca472d30524f24dd5.jpg', 'jpg', 'd982200ac39910cf22e65f9f92186f1f', 63, 1, '2025-05-02 02:23:59', '2025-05-02 02:23:59', 0);
INSERT INTO `sys_file` VALUES (190, '62d6ed1de68355384a9e556da152772db8847874f192-9zqEXz_fw1200.jpg', 'http://localhost:9091/file/db08ee22c5b74bcca472d30524f24dd5.jpg', 'jpg', 'd982200ac39910cf22e65f9f92186f1f', 63, 1, '2025-05-02 02:24:13', '2025-05-02 02:24:13', 0);
INSERT INTO `sys_file` VALUES (191, '62d6ed1de68355384a9e556da152772db8847874f192-9zqEXz_fw1200.jpg', 'http://localhost:9091/file/db08ee22c5b74bcca472d30524f24dd5.jpg', 'jpg', 'd982200ac39910cf22e65f9f92186f1f', 63, 1, '2025-05-02 02:25:08', '2025-05-02 02:25:08', 0);
INSERT INTO `sys_file` VALUES (192, '屏幕截图 2024-03-05 213717.png', 'http://localhost:9091/file/9dd158ce4f7f4a12940ed6d17d7a01c8.png', 'png', 'd231283fbbec525b7288bf978c5b06c0', 4, 1, '2025-05-02 16:14:49', '2025-05-02 16:14:49', 0);
INSERT INTO `sys_file` VALUES (193, 'hG18HppSc4lL63ae5b2d67c16c296f18e33b5578a214.png', 'http://localhost:9091/file/1b3ca59188614cc6964c21534df77425.png', 'png', '63ae5b2d67c16c296f18e33b5578a214', 8, 1, '2025-05-02 16:28:12', '2025-05-02 16:28:12', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `flag` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', '管理员', 'admin');
INSERT INTO `sys_role` VALUES (2, '用户', '用户', 'user');
INSERT INTO `sys_role` VALUES (3, '专家', '专家', 'expert');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '头像',
  `email` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role_id` int NULL DEFAULT NULL COMMENT '角色  0超级管理员  1管理员 2普通账号',
  `status` tinyint NULL DEFAULT 1 COMMENT '是否有效 1有效 0无效',
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin', '管理员', NULL, '1234@qq.com', 1, 1, 'admin');
INSERT INTO `sys_user` VALUES (2, 'user', 'user', '用户', NULL, '1234@qq.com', 3, 1, 'expert');
INSERT INTO `sys_user` VALUES (23, '456', '456', '小张', NULL, '1234@qq.com', 2, 1, 'user');
INSERT INTO `sys_user` VALUES (24, '789', '789', 'xiaoz', 'http://localhost:9091/file/1b3ca59188614cc6964c21534df77425.png', 'cee@qq.com', 2, 1, 'user');
INSERT INTO `sys_user` VALUES (25, '147', '147', '147', NULL, NULL, 3, 1, 'expert');
INSERT INTO `sys_user` VALUES (26, '258', '258', '小王', NULL, '1@qq.com', 3, 1, 'expert');

SET FOREIGN_KEY_CHECKS = 1;
