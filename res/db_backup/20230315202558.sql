-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: supervision-spot
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `supervision-spot`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `supervision-spot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `supervision-spot`;

--
-- Table structure for table `account-security_admin`
--

DROP TABLE IF EXISTS `account-security_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account-security_admin` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `role_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色的uuid',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称',
  `password` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `failed_retry_count` tinyint NOT NULL COMMENT '失败重复计数',
  `frozen_timestamp` bigint DEFAULT NULL COMMENT '冻结时间戳',
  `frozen_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冻结时间',
  `login_token` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登陆令牌',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号状态\\nNORMAL：正常\\nFROZEN：冻结\\nLOCK：锁定\\nDELETE：删除',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `query` (`uuid`,`role_uuid`,`name`,`password`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号安全_管理员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account-security_admin`
--

LOCK TABLES `account-security_admin` WRITE;
/*!40000 ALTER TABLE `account-security_admin` DISABLE KEYS */;
INSERT INTO `account-security_admin` VALUES ('011710a1947242aa8e5c3a31597d1aaa','5dc6e3e89a844d15870592d53f88653b','271724','b7b7cbef48b94adcf1f9f6287e6ce487',0,NULL,NULL,'011710a1947242aa8e5c3a31597d1aaa','NORMAL',1678872262625,'2023-03-15 17:24:22',NULL),('07a7f86f262842c2b45256262534408b','5dc6e3e89a844d15870592d53f88653b','270518','1a6d6c7ea8cd373fc6b3028b75bca946',0,NULL,NULL,'07a7f86f262842c2b45256262534408b','NORMAL',1678872261907,'2023-03-15 17:24:21',NULL),('0d848459090e42f8a3ecfa4d4380463f','5dc6e3e89a844d15870592d53f88653b','271632','d9de0754e7f31f6af81a0a52cb8d9c82',0,NULL,NULL,'0d848459090e42f8a3ecfa4d4380463f','NORMAL',1678872262825,'2023-03-15 17:24:22',NULL),('0daefbd1dda641a49ad3030e129d47b4','5dc6e3e89a844d15870592d53f88653b','270692','2f058a79e7b5a4bfce14afbb326ce6b9',0,NULL,NULL,'0daefbd1dda641a49ad3030e129d47b4','NORMAL',1678872260807,'2023-03-15 17:24:20',NULL),('0faebed28aaf4198848e39338068b594','b2639d4ff7754ffb9e659ccd9f3fe271','27-0205','725f4244e2ea68082e843a738a383710',0,NULL,NULL,'0faebed28aaf4198848e39338068b594','NORMAL',1678872262881,'2023-03-15 17:24:22',NULL),('1183e22aeb7b49548e2e0f46a6d4ae45','5dc6e3e89a844d15870592d53f88653b','270722','81e88b2fc722cfd893949586827c007e',0,NULL,NULL,'1183e22aeb7b49548e2e0f46a6d4ae45','NORMAL',1678872262214,'2023-03-15 17:24:22',NULL),('18b5a7a8e48e4af68e0164dfb1c5de62','5dc6e3e89a844d15870592d53f88653b','270847','b70d990fbf0f5c20adb0c93d90120558',0,NULL,NULL,'18b5a7a8e48e4af68e0164dfb1c5de62','NORMAL',1678872260419,'2023-03-15 17:24:20',NULL),('1a53dfb159b24cefa121b7fa23ba56e8','b2639d4ff7754ffb9e659ccd9f3fe271','27-0217','cb53379e785b0fd0b22b2b984b790093',0,NULL,NULL,'1a53dfb159b24cefa121b7fa23ba56e8','NORMAL',1678872261604,'2023-03-15 17:24:21',NULL),('26968f0d03fa4941a78b7a16a442b42a','5dc6e3e89a844d15870592d53f88653b','271601','d2251e441692cda0b5e933f28b0dbd50',0,NULL,NULL,'26968f0d03fa4941a78b7a16a442b42a','NORMAL',1678872262389,'2023-03-15 17:24:22',NULL),('2958fe86078a4935b47602491aa3d329','5dc6e3e89a844d15870592d53f88653b','271720','55d706e3aa29edcdca43723d931ba00f',0,NULL,NULL,'2958fe86078a4935b47602491aa3d329','NORMAL',1678872260224,'2023-03-15 17:24:20',NULL),('47bb36daac8249978f2496945eb54683','5dc6e3e89a844d15870592d53f88653b','270449','9179f96ea1e870908c6fad69d1f0a0d4',0,NULL,NULL,'47bb36daac8249978f2496945eb54683','NORMAL',1678872260166,'2023-03-15 17:24:20',NULL),('47e061d12d164e1184c018876cf86cab','5dc6e3e89a844d15870592d53f88653b','271659','d68ad260be1b09b864012f7365b9f2c0',0,NULL,NULL,'47e061d12d164e1184c018876cf86cab','NORMAL',1678872262089,'2023-03-15 17:24:22',NULL),('49304f11ff244b75857025e8fcb52fd1','5dc6e3e89a844d15870592d53f88653b','271505','27d6d54d357fbf9981461e78e440c35f',0,NULL,NULL,'49304f11ff244b75857025e8fcb52fd1','NORMAL',1678872262148,'2023-03-15 17:24:22',NULL),('57025bccd38c4ba9aef4d3318ecd0c4d','5dc6e3e89a844d15870592d53f88653b','270391','7dbb67a3af035bfca42e67a10466ef78',0,NULL,NULL,'57025bccd38c4ba9aef4d3318ecd0c4d','NORMAL',1678872260554,'2023-03-15 17:24:20',NULL),('584fd6e2085a4a99b10a3b64b9f5dc86','b2639d4ff7754ffb9e659ccd9f3fe271','27-0070','88a3de1eaeb270e18305ffd9e8c87215',0,NULL,NULL,'584fd6e2085a4a99b10a3b64b9f5dc86','NORMAL',1678872261844,'2023-03-15 17:24:21',NULL),('63f31fc5d0be4048847c15d4419cafeb','5dc6e3e89a844d15870592d53f88653b','270928','899d30ecc97100d89c655fb212cb3015',0,NULL,NULL,'63f31fc5d0be4048847c15d4419cafeb','NORMAL',1678872262332,'2023-03-15 17:24:22',NULL),('6549452151f1482bac100c5258eb8a1d','5dc6e3e89a844d15870592d53f88653b','270818','9bf71a7b45b6a0f8aaf74d1841d9cbe9',0,NULL,NULL,'6549452151f1482bac100c5258eb8a1d','NORMAL',1678872262033,'2023-03-15 17:24:22',NULL),('67ccf26fd6d545c3a7a52938fe827f8a','5dc6e3e89a844d15870592d53f88653b','270379','a789d74f86121b0bf9bbfe2f38d1fd40',0,NULL,NULL,'67ccf26fd6d545c3a7a52938fe827f8a','NORMAL',1678872260283,'2023-03-15 17:24:20',NULL),('6a96891f15c244fe9b4a10fbf8e08dc7','5dc6e3e89a844d15870592d53f88653b','271749','ba210d73147c88b263da78056cb47da9',0,NULL,NULL,'6a96891f15c244fe9b4a10fbf8e08dc7','NORMAL',1678872262764,'2023-03-15 17:24:22',NULL),('6b49620130194bee8e806cd852b24859','b2639d4ff7754ffb9e659ccd9f3fe271','27-0215','8bf310399ebb9c6977c3e98ef8f4b778',0,NULL,NULL,'6b49620130194bee8e806cd852b24859','NORMAL',1678872261712,'2023-03-15 17:24:21',NULL),('6d3a2761784f4b20ad933ff764070c36','5dc6e3e89a844d15870592d53f88653b','271639','7c1ef54a3fd7eefc88d143ed10ceec33',0,NULL,NULL,'6d3a2761784f4b20ad933ff764070c36','NORMAL',1678872261363,'2023-03-15 17:24:21',NULL),('6d3ca24356d44d5cb2a81566f01f7ef5','09b8b6a6c8be41788f7a170d334fe57a','admin','21232f297a57a5a743894a0e4a801fc3',0,NULL,NULL,'b2b1da1097a64e8682c858b4ac1684a8','NORMAL',1658295862635,'2022-07-20 13:44:22',NULL),('7c18ccf4640f44428c31b8d856878bf4','5dc6e3e89a844d15870592d53f88653b','271585','664d91ba528e9c8b737d68034fd4f55b',0,NULL,NULL,'7c18ccf4640f44428c31b8d856878bf4','NORMAL',1678872262506,'2023-03-15 17:24:22',NULL),('7e3a1a45167444f3bd9b2fe95c4c8110','b2639d4ff7754ffb9e659ccd9f3fe271','27-0216','25b6be264e842b1317d5c3440c2f5315',0,NULL,NULL,'7e3a1a45167444f3bd9b2fe95c4c8110','NORMAL',1678872261529,'2023-03-15 17:24:21',NULL),('8257050e60e14f91ba478434c7fcb92b','b2639d4ff7754ffb9e659ccd9f3fe271','27-0005','38018d6d151c334af1481b8aa8dccabe',0,NULL,NULL,'8257050e60e14f91ba478434c7fcb92b','NORMAL',1678872263006,'2023-03-15 17:24:23',NULL),('8727559bd7654077ab0f86081edeb382','5dc6e3e89a844d15870592d53f88653b','270181','468b78adfb123a35b8c8f1866e1f2fda',0,NULL,NULL,'8727559bd7654077ab0f86081edeb382','NORMAL',1678872260109,'2023-03-15 17:24:20',NULL),('93fd97a0d8b2471fbb3693d515be826d','b2639d4ff7754ffb9e659ccd9f3fe271','27-0264','aa22275c8cf7d927f7cf02c0be398a5f',0,NULL,NULL,'93fd97a0d8b2471fbb3693d515be826d','NORMAL',1678872261778,'2023-03-15 17:24:21',NULL),('95205dceefbd4dadbea7eea799312141','b1168f9741d344b6ade833667a92ae03','shaowen','59b42bb1b494cbefc67d92d65db84ec2',0,NULL,NULL,'7197eb00dbe34fd78ce108e9d129fe6a','NORMAL',1672822109895,'2023-01-04 16:48:29',NULL),('978e8e25a6bd40bc944a2ff3128fd027','5dc6e3e89a844d15870592d53f88653b','270284','b689e768a759399af6e43444b0adc1c5',0,NULL,NULL,'978e8e25a6bd40bc944a2ff3128fd027','NORMAL',1678872260351,'2023-03-15 17:24:20',NULL),('9caf7f504d414b609204a42a8c64c8f3','5dc6e3e89a844d15870592d53f88653b','271888','a9fe6fccb51b0676a9d93eebdfdf39c1',0,NULL,NULL,'9caf7f504d414b609204a42a8c64c8f3','NORMAL',1678872262447,'2023-03-15 17:24:22',NULL),('a30a122f1e344144a6bbed3320f83e9b','5dc6e3e89a844d15870592d53f88653b','270672','d067cdbf77bcb6568d7f743e27a77e80',0,NULL,NULL,'a30a122f1e344144a6bbed3320f83e9b','NORMAL',1678872260481,'2023-03-15 17:24:20',NULL),('a7d4d989b68e40879b98e278ca14614f','5dc6e3e89a844d15870592d53f88653b','271861','493e761249de034b5a9ebf476b7ef648',0,NULL,NULL,'a7d4d989b68e40879b98e278ca14614f','NORMAL',1678872261132,'2023-03-15 17:24:21',NULL),('bf12caa58a344b1a9e2d83b896ab3c9c','b1168f9741d344b6ade833667a92ae03','weishan','94fec324868bb58c621186e4cf08b224',0,NULL,NULL,'b50eb424d05547bdb96fcb2a0d4bdf59','NORMAL',1677554265948,'2023-02-28 11:17:45',NULL),('c60d6a2e95be4242b5fbf5ba202f8239','5dc6e3e89a844d15870592d53f88653b','271894','cc8161008478c7149b675d2c31b3f55a',0,NULL,NULL,'c60d6a2e95be4242b5fbf5ba202f8239','NORMAL',1678872260979,'2023-03-15 17:24:20',NULL),('c7b75347c8c9400493658e0788b5ec4a','cdea19ca2ebf4b6996396d0d0fbec1dd','test','202cb962ac59075b964b07152d234b70',0,NULL,NULL,'c7b75347c8c9400493658e0788b5ec4a','NORMAL',1658295862635,'2022-07-20 13:44:22',NULL),('c85a133fa8bf4718b6fbb09e06308c90','5dc6e3e89a844d15870592d53f88653b','270397','5500862cc4edb2439fd2752430d45dcc',0,NULL,NULL,'c85a133fa8bf4718b6fbb09e06308c90','NORMAL',1678872261974,'2023-03-15 17:24:21',NULL),('cb11018fd3c1487fb8c492fb7eaa81f2','5dc6e3e89a844d15870592d53f88653b','270839','97879edbb05faed90a1c9cd6c84fb5f8',0,NULL,NULL,'cb11018fd3c1487fb8c492fb7eaa81f2','NORMAL',1678872262567,'2023-03-15 17:24:22',NULL),('ce97133a99304040b4ca9218469a3989','b1168f9741d344b6ade833667a92ae03','jack','202cb962ac59075b964b07152d234b70',0,NULL,NULL,'8648a5c21a264808896133c6b612bab8','NORMAL',1658295862635,'2022-07-20 13:44:22',NULL),('d0e8714274294825a0e1b63499a00cdf','b2639d4ff7754ffb9e659ccd9f3fe271','27-0081','15f881c422cde9840026c7efc07e4d4e',0,NULL,NULL,'d0e8714274294825a0e1b63499a00cdf','NORMAL',1678872262951,'2023-03-15 17:24:22',NULL),('d6972bc636514b2aab3ceed0e7589ff0','5dc6e3e89a844d15870592d53f88653b','270333','0affc770062b2960c0fc825e357a90cc',0,NULL,NULL,'d6972bc636514b2aab3ceed0e7589ff0','NORMAL',1678872260010,'2023-03-15 17:24:20',NULL),('fd3ed3db663d4ac282d3bbc9d0d1751d','5dc6e3e89a844d15870592d53f88653b','270780','8503e52c14deb99ab8c2c8149cd6d959',0,NULL,NULL,'fd3ed3db663d4ac282d3bbc9d0d1751d','NORMAL',1678872262273,'2023-03-15 17:24:22',NULL);
/*!40000 ALTER TABLE `account-security_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account-security_menu`
--

DROP TABLE IF EXISTS `account-security_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account-security_menu` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `parent_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级菜单的uuid',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `text` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '显示文本',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `link` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链接',
  `level` int NOT NULL COMMENT '级别（从1开始计算）',
  `order` tinyint NOT NULL COMMENT '排序编号',
  `order_group` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '排序编号组（%03d为一级，最多20级，所以长度为60，如果级数扩大，则增加对应长度）',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`parent_uuid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号安全_菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account-security_menu`
--

LOCK TABLES `account-security_menu` WRITE;
/*!40000 ALTER TABLE `account-security_menu` DISABLE KEYS */;
INSERT INTO `account-security_menu` VALUES ('03707d32ffce43c792a3def3268c410d','121170c6c4c4490b9883aa5c5fd2d90b','whdayManagement','工作休息日管理','工作休息日管理。','../supervision/place_whday_management.html',3,6,'003003006',1658295862635,'2022-07-20 13:44:22',1677221356299),('0bd6f7d48038471eb649ed25e4c6821e','121170c6c4c4490b9883aa5c5fd2d90b','placeManagement','地点管理','地点管理。','../supervision/place_management.html',3,4,'003003004',1658295862635,'2022-07-20 13:44:22',1677221301068),('0d6debc53c354627a51fdf1bd610f110','121170c6c4c4490b9883aa5c5fd2d90b','problemDepartmentTypeManagement','单位问题类型管理','单位问题类型管理。','../supervision_spot/problem_department_type_management.html',3,2,'003003002',1677854985404,'2023-03-03 22:49:45',NULL),('0dcd89e6c9d94056988f0f0e2202a7c9','121170c6c4c4490b9883aa5c5fd2d90b','placeTypeManagement','地点类型管理','地点类型管理。','../supervision/place_type_management.html',3,2,'003003002',1658295862635,'2022-07-20 13:44:22',1677221294205),('0dd1ff5aaa0b4d79885ee993d23bf616','8bbeafcb5c1d473e8deb26de4ddb5851','humanProblemManagement','人工问题管理','人工问题管理。','../supervision/human_problem_management.html',3,3,'003002003',1658295862635,'2022-07-20 13:44:22',1677221167272),('121170c6c4c4490b9883aa5c5fd2d90b','b8eab51abf654483b42c7f380e0e7548','problemManagement','问题管理','问题管理。',NULL,2,3,'003003',1658295862635,'2022-07-20 13:44:22',NULL),('1bf01d70c88b4a66834487f7b5a510a9','b8eab51abf654483b42c7f380e0e7548','reportManagement','报告管理','报告管理。',NULL,2,4,'003004',1658295862635,'2022-07-20 13:44:22',1677221454317),('249416d8a5e24d73ae3d99ad48c2e714','b8eab51abf654483b42c7f380e0e7548','sysManagement','系统管理','系统管理。',NULL,2,5,'003005',1672725167698,'2023-01-03 13:52:47',NULL),('2cf88166caac4d67b82cc1fae9970368','aab30dad4f424cfaababaaddd4747e35','developDocument','开发文档','开发文档功能相关。',NULL,2,1,'002001',1658295862635,'2022-07-20 13:44:22',NULL),('3020a24213eb46f3b400739a5f565ce8','9f1f10bdb9724c40a83447128ef6c567','adminManagement','管理员管理','后台管理员的添加、删除、修改、删除等操作。','../security_center/admin_management.html',3,5,'001001005',1658295862635,'2022-07-20 13:44:22',NULL),('32264d4792b54d43b7df308500b580e4','249416d8a5e24d73ae3d99ad48c2e714','sysConfig','系统配置','系统配置。','../supervision_spot/sys_config.html',3,1,'003005001',1677221528778,'2023-02-24 14:52:08',NULL),('386e06bdccbe4dc29c3a7adb6b070f04','99fc9c7a112f4c36a80874cdd3a89da0','departmentTypeManagement','类型管理','类型管理。','../supervision_spot/department_type_management.html',3,1,'003001001',1658295862635,'2022-07-20 13:44:22',NULL),('4de23bbf49c0474ebfc4a3d3ddd1e498','121170c6c4c4490b9883aa5c5fd2d90b','svAdminManagement','督查管理员管理','督查管理员管理。','../supervision/admin_management.html',3,9,'003003009',1658295862635,'2022-07-20 13:44:22',1677221419550),('503a8f254e114d569a92188473e010c3','8bbeafcb5c1d473e8deb26de4ddb5851','problemFeedbackManagement','问题反馈管理','问题反馈管理。','../supervision/problem_feedback_management.html',3,5,'003002005',1658295862635,'2022-07-20 13:44:22',1677221170399),('52379b05083e43aab7dfe349620477ab','249416d8a5e24d73ae3d99ad48c2e714','deleteFailRecordManagement','删除失败记录管理','删除失败记录管理。','../supervision_spot/delete_fail_record_management.html',3,2,'003005002',1672725248525,'2023-01-03 13:54:08',NULL),('5a0883aa60154ca4b55a64000a3e3c32','9f1f10bdb9724c40a83447128ef6c567','rolePermission','角色权限','配置角色（系统级）使用的接口权限。','../security_center/role_permission.html',3,4,'001001004',1658295862635,'2022-07-20 13:44:22',NULL),('5aec7f39f39641e88b2d2b84c8d37150','0','securityCenter','安全中心','系统安全功能相关。',NULL,1,1,'001',1658295862635,'2022-07-20 13:44:22',NULL),('7ec18ebf8e5143e6908f94f7daab0b8a','121170c6c4c4490b9883aa5c5fd2d90b','globalHolidayManagement','全局假期管理','全局假期管理。','../supervision/global_holiday_management.html',3,8,'003003008',1658295862635,'2022-07-20 13:44:22',1677221359799),('845779fa9a43435ba9e3878e605771c2','9f1f10bdb9724c40a83447128ef6c567','roleMenu','角色菜单','配置角色（系统级）在后台管理中的菜单使用权限。','../security_center/role_menu.html',3,3,'001001003',1658295862635,'2022-07-20 13:44:22',NULL),('8a1674ebbbc248d592df4fd2203e78a4','1bf01d70c88b4a66834487f7b5a510a9','reportTemplate','报告模板','报告模板。','../supervision/report_template.html',3,1,'003004001',1658295862635,'2022-07-20 13:44:22',1677221454317),('8bbeafcb5c1d473e8deb26de4ddb5851','b8eab51abf654483b42c7f380e0e7548','personManagement','人员管理','人员管理。',NULL,2,2,'003002',1658295862635,'2022-07-20 13:44:22',1677567975150),('99fc9c7a112f4c36a80874cdd3a89da0','b8eab51abf654483b42c7f380e0e7548','organizational_structure','组织架构','组织架构。',NULL,2,1,'003001',1658295862635,'2022-07-20 13:44:22',NULL),('9b8b53e26a264ee28da0e07ef38e2cb3','121170c6c4c4490b9883aa5c5fd2d90b','placeAttributeManagement','地点属性管理','地点属性管理。','../supervision/place_attribute_management.html',3,3,'003003003',1658295862635,'2022-07-20 13:44:22',1677221297771),('9f1f10bdb9724c40a83447128ef6c567','5aec7f39f39641e88b2d2b84c8d37150','accountSecurity','账户安全','账户安全功能相关。',NULL,2,1,'001001',1658295862635,'2022-07-20 13:44:22',NULL),('a228b7e7e65746489ac903950a4a0630','121170c6c4c4490b9883aa5c5fd2d90b','cameraManagement','摄像头管理','摄像头管理。','../supervision/camera_management.html',3,5,'003003005',1658295862635,'2022-07-20 13:44:22',1677221304263),('a8d4947340584c90bd1bbfa863d7d06d','9f1f10bdb9724c40a83447128ef6c567','roleManagement','角色管理','角色（系统级）的添加、删除、修改、删除等操作。','../security_center/role_management.html',3,2,'001001002',1658295862635,'2022-07-20 13:44:22',NULL),('aab30dad4f424cfaababaaddd4747e35','0','documentCenter','文档中心','文档功能相关。',NULL,1,2,'002',1658295862635,'2022-07-20 13:44:22',NULL),('b74a8edfbf5648fda7ae3b6f3a4370cc','2cf88166caac4d67b82cc1fae9970368','apiInterface','API接口','API接口。','../document_center/api.html',3,1,'002001001',1658295862635,'2022-07-20 13:44:22',NULL),('b8eab51abf654483b42c7f380e0e7548','0','supervision_spot','现场督查','现场督查模块功能相关。',NULL,1,3,'003',1658295862635,'2022-07-20 13:44:22',NULL),('bbd3d0f287b7463db6db77e2112a1a8d','8bbeafcb5c1d473e8deb26de4ddb5851','problemLevelManagement','等级管理','等级管理。','../supervision/problem_level_management.html',3,2,'003002002',1658295862635,'2022-07-20 13:44:22',1677221163796),('be56747855954ed4b2dda4393bd421ca','99fc9c7a112f4c36a80874cdd3a89da0','departmentManagement','单位管理','单位管理。','../supervision_spot/department_management.html',3,2,'003001002',1677220369000,'2023-02-24 14:32:49',NULL),('ca4cbb8c7aac421d8b4a2014117e8cac','8bbeafcb5c1d473e8deb26de4ddb5851','adminManagement','管理员管理','管理员管理。','../supervision_spot/admin_management.html',3,1,'003002001',1658295862635,'2022-07-20 13:44:22',1677567975150),('d5d01dcbadcc4b3083b7ea4c308865b3','121170c6c4c4490b9883aa5c5fd2d90b','problemPersonTypeManagement','个人问题类型管理','个人问题类型管理。','../supervision_spot/problem_person_type_management.html',3,1,'003003001',1658295862635,'2022-07-20 13:44:22',NULL),('d7554016092c4fd9a150ef566703a78e','9f1f10bdb9724c40a83447128ef6c567','menuManagement','菜单管理','管理后台菜单的添加、删除、修改、删除等操作。','../security_center/menu_management.html',3,1,'001001001',1658295862635,'2022-07-20 13:44:22',NULL);
/*!40000 ALTER TABLE `account-security_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account-security_role`
--

DROP TABLE IF EXISTS `account-security_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account-security_role` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称（一定要预留足够的长度）',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '以分号分割的权限',
  `menus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '角色可操作的菜单（JSONArray格式）',
  `order` tinyint NOT NULL COMMENT '排序编号',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `query` (`uuid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号安全_角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account-security_role`
--

LOCK TABLES `account-security_role` WRITE;
/*!40000 ALTER TABLE `account-security_role` DISABLE KEYS */;
INSERT INTO `account-security_role` VALUES ('09b8b6a6c8be41788f7a170d334fe57a','admin','用于broadcast项目管理员操作。','security.AccountInformation.removeUser;security.AccountInformation.addAdmin;security.AccountInformation.addUser;security.AccountInformation.removeAdmin;security.AccountInformation.modifyAdmin;security.AccountInformation.modifyUser;security.AccountInformation.getAdminAccount;security.AccountInformation.getUserAccount;security.AccountInformation.refreshUserToken;security.AccountInformation.modifyAdminSelfPassword;security.AccountInformation.modifyUserSelfPassword;security.AccountInformation.refreshAdminToken;security.AccountInformation.ping;security.AccountMenu.addMenu;security.AccountMenu.removeMenu;security.AccountMenu.modifyMenu;security.AccountMenu.getMenu;security.AccountMenu.ping;security.AccountRole.getRole;security.AccountRole.removeRole;security.AccountRole.addRole;security.AccountRole.modifyRoleMenu;security.AccountRole.modifyRoleInfo;security.AccountRole.modifyRolePermission;security.AccountRole.ping;security.LogOF.adminLogoff;security.LogOF.userLogoff;security.LogOF.adminLogon;security.LogOF.userLogon;security.LogOF.ping;security.ModuleMethod.getModuleMethod;security.ModuleMethod.ping;','[{\"parent_uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"name\":\"configManagement\",\"link\":\"../yunyou/config_management.html\",\"description\":\"配置管理。\",\"text\":\"配置管理\",\"uuid\":\"386e06bdccbe4dc29c3a7adb6b070f04\",\"order\":1},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"name\":\"orderCenter\",\"description\":\"订单中心。\",\"text\":\"订单中心\",\"uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"order\":2},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"name\":\"yunyouConfig\",\"description\":\"云游配置。\",\"text\":\"云游配置\",\"uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"order\":1},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"order_management\",\"link\":\"../broadcast/order_management.html\",\"description\":\"订单管理。\",\"text\":\"订单管理\",\"uuid\":\"b07c8428851545a387b0f883c74e606b\",\"order\":2},{\"parent_uuid\":\"0\",\"name\":\"yunyou\",\"description\":\"云游模块功能相关。\",\"text\":\"云游\",\"uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"order\":2},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"orderStatistics\",\"link\":\"../broadcast/order_statistics.html\",\"description\":\"订单统计。\",\"text\":\"订单统计\",\"uuid\":\"ca4cbb8c7aac421d8b4a2014117e8cac\",\"order\":1}]',2,1658295862635,'2022-07-20 13:44:22',NULL),('3e7f20f64f994a3992729bed16e81b5d','department_admin','单位管理员。','*',NULL,7,1677550807273,'2023-02-28 10:20:07',NULL),('5dc6e3e89a844d15870592d53f88653b','min_jing','民警。','*',NULL,8,1677550837253,'2023-02-28 10:20:37',NULL),('6d18372a4d2a4bd7b0809e2c734fcbb6','test','测试。','security.Admin.addAdmin;','[{\"parent_uuid\":\"5aec7f39f39641e88b2d2b84c8d37150\",\"name\":\"accountSecurity\",\"description\":\"账户安全功能相关。\",\"text\":\"账户安全\",\"uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"order\":1},{\"parent_uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"name\":\"menuManagement\",\"link\":\"../security_center/menu_management.html\",\"description\":\"管理后台菜单的添加、删除、修改、删除等操作。\",\"text\":\"菜单管理\",\"uuid\":\"d7554016092c4fd9a150ef566703a78e\",\"order\":1}]',3,1658295862635,'2022-07-20 13:44:22',NULL),('b06f0958abc24854b26fc58cc484c459','department_leader','单位领导。','*',NULL,6,1677550791902,'2023-02-28 10:19:51',NULL),('b1168f9741d344b6ade833667a92ae03','superadmin','超级管理员，拥有所有权限。','*','[{\"parent_uuid\":\"0\",\"level\":1,\"name\":\"securityCenter\",\"description\":\"系统安全功能相关。\",\"text\":\"安全中心\",\"uuid\":\"5aec7f39f39641e88b2d2b84c8d37150\",\"order_group\":\"001\",\"order\":1},{\"parent_uuid\":\"5aec7f39f39641e88b2d2b84c8d37150\",\"level\":2,\"name\":\"accountSecurity\",\"description\":\"账户安全功能相关。\",\"text\":\"账户安全\",\"uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"order_group\":\"001001\",\"order\":1},{\"parent_uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"level\":3,\"name\":\"menuManagement\",\"link\":\"../security_center/menu_management.html\",\"description\":\"管理后台菜单的添加、删除、修改、删除等操作。\",\"text\":\"菜单管理\",\"uuid\":\"d7554016092c4fd9a150ef566703a78e\",\"order_group\":\"001001001\",\"order\":1},{\"parent_uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"level\":3,\"name\":\"roleManagement\",\"link\":\"../security_center/role_management.html\",\"description\":\"角色（系统级）的添加、删除、修改、删除等操作。\",\"text\":\"角色管理\",\"uuid\":\"a8d4947340584c90bd1bbfa863d7d06d\",\"order_group\":\"001001002\",\"order\":2},{\"parent_uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"level\":3,\"name\":\"roleMenu\",\"link\":\"../security_center/role_menu.html\",\"description\":\"配置角色（系统级）在后台管理中的菜单使用权限。\",\"text\":\"角色菜单\",\"uuid\":\"845779fa9a43435ba9e3878e605771c2\",\"order_group\":\"001001003\",\"order\":3},{\"parent_uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"level\":3,\"name\":\"rolePermission\",\"link\":\"../security_center/role_permission.html\",\"description\":\"配置角色（系统级）使用的接口权限。\",\"text\":\"角色权限\",\"uuid\":\"5a0883aa60154ca4b55a64000a3e3c32\",\"order_group\":\"001001004\",\"order\":4},{\"parent_uuid\":\"9f1f10bdb9724c40a83447128ef6c567\",\"level\":3,\"name\":\"adminManagement\",\"link\":\"../security_center/admin_management.html\",\"description\":\"后台管理员的添加、删除、修改、删除等操作。\",\"text\":\"管理员管理\",\"uuid\":\"3020a24213eb46f3b400739a5f565ce8\",\"order_group\":\"001001005\",\"order\":5},{\"parent_uuid\":\"0\",\"level\":1,\"name\":\"documentCenter\",\"description\":\"文档功能相关。\",\"text\":\"文档中心\",\"uuid\":\"aab30dad4f424cfaababaaddd4747e35\",\"order_group\":\"002\",\"order\":2},{\"parent_uuid\":\"aab30dad4f424cfaababaaddd4747e35\",\"level\":2,\"name\":\"developDocument\",\"description\":\"开发文档功能相关。\",\"text\":\"开发文档\",\"uuid\":\"2cf88166caac4d67b82cc1fae9970368\",\"order_group\":\"002001\",\"order\":1},{\"parent_uuid\":\"2cf88166caac4d67b82cc1fae9970368\",\"level\":3,\"name\":\"apiInterface\",\"link\":\"../document_center/api.html\",\"description\":\"API接口。\",\"text\":\"API接口\",\"uuid\":\"b74a8edfbf5648fda7ae3b6f3a4370cc\",\"order_group\":\"002001001\",\"order\":1},{\"parent_uuid\":\"0\",\"level\":1,\"name\":\"supervision_spot\",\"description\":\"现场督查模块功能相关。\",\"text\":\"现场督查\",\"uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"order_group\":\"003\",\"order\":3},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"level\":2,\"name\":\"organizational_structure\",\"description\":\"组织架构。\",\"text\":\"组织架构\",\"uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"order_group\":\"003001\",\"order\":1},{\"parent_uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"level\":3,\"name\":\"departmentTypeManagement\",\"link\":\"../supervision_spot/department_type_management.html\",\"description\":\"类型管理。\",\"text\":\"类型管理\",\"uuid\":\"386e06bdccbe4dc29c3a7adb6b070f04\",\"order_group\":\"003001001\",\"order\":1},{\"parent_uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"level\":3,\"name\":\"departmentManagement\",\"link\":\"../supervision_spot/department_management.html\",\"description\":\"单位管理。\",\"text\":\"单位管理\",\"uuid\":\"be56747855954ed4b2dda4393bd421ca\",\"order_group\":\"003001002\",\"order\":2},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"level\":2,\"name\":\"problemManagement\",\"description\":\"问题管理。\",\"text\":\"问题管理\",\"uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"order_group\":\"003003\",\"order\":3},{\"parent_uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"level\":3,\"name\":\"problemPersonTypeManagement\",\"link\":\"../supervision_spot/problem_person_type_management.html\",\"description\":\"个人问题类型管理。\",\"text\":\"个人问题类型管理\",\"uuid\":\"d5d01dcbadcc4b3083b7ea4c308865b3\",\"order_group\":\"003003001\",\"order\":1},{\"parent_uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"level\":3,\"name\":\"problemDepartmentTypeManagement\",\"link\":\"../supervision_spot/problem_department_type_management.html\",\"description\":\"单位问题类型管理。\",\"text\":\"单位问题类型管理\",\"uuid\":\"0d6debc53c354627a51fdf1bd610f110\",\"order_group\":\"003003002\",\"order\":2},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"level\":2,\"name\":\"sysManagement\",\"description\":\"系统管理。\",\"text\":\"系统管理\",\"uuid\":\"249416d8a5e24d73ae3d99ad48c2e714\",\"order_group\":\"003005\",\"order\":5},{\"parent_uuid\":\"249416d8a5e24d73ae3d99ad48c2e714\",\"level\":3,\"name\":\"sysConfig\",\"link\":\"../supervision_spot/sys_config.html\",\"description\":\"系统配置。\",\"text\":\"系统配置\",\"uuid\":\"32264d4792b54d43b7df308500b580e4\",\"order_group\":\"003005001\",\"order\":1},{\"parent_uuid\":\"249416d8a5e24d73ae3d99ad48c2e714\",\"level\":3,\"name\":\"deleteFailRecordManagement\",\"link\":\"../supervision_spot/delete_fail_record_management.html\",\"description\":\"删除失败记录管理。\",\"text\":\"删除失败记录管理\",\"uuid\":\"52379b05083e43aab7dfe349620477ab\",\"order_group\":\"003005002\",\"order\":2}]',1,1658295862635,'2022-07-20 13:44:22',NULL),('b2639d4ff7754ffb9e659ccd9f3fe271','fu_jing','辅警。','*',NULL,9,1677550855368,'2023-02-28 10:20:55',NULL),('cdea19ca2ebf4b6996396d0d0fbec1dd','ds_admin','督审管理员。','*','[{\"parent_uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"name\":\"couponManagementOfCompany\",\"link\":\"../youtuantuan/coupon_management_of_company.html\",\"description\":\"优惠券管理（企业）。\",\"text\":\"优惠券管理（企业）\",\"uuid\":\"0dcd89e6c9d94056988f0f0e2202a7c9\",\"order\":2},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"shopManagementOfCompany\",\"link\":\"../youtuantuan/shop_management_of_company.html\",\"description\":\"门店管理（企业）。\",\"text\":\"门店管理（企业）\",\"uuid\":\"0dd1ff5aaa0b4d79885ee993d23bf616\",\"order\":3},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"name\":\"marketingCenter\",\"description\":\"营销中心。\",\"text\":\"营销中心\",\"uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"order\":3},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"name\":\"rechargeableCard\",\"description\":\"充值卡。\",\"text\":\"充值卡\",\"uuid\":\"1bf01d70c88b4a66834487f7b5a510a9\",\"order\":4},{\"parent_uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"name\":\"couponUseQueryStatistics\",\"link\":\"../youtuantuan/coupon_use_query_statistics.html\",\"description\":\"优惠券使用订单查询。\",\"text\":\"优惠券使用订单查询\",\"uuid\":\"386e06bdccbe4dc29c3a7adb6b070f04\",\"order\":1},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"spiManagementOfCompany\",\"link\":\"../youtuantuan/shop_public_info_management_of_company.html\",\"description\":\"门店公开信息管理（企业）。\",\"text\":\"门店公开信息管理（企业）\",\"uuid\":\"503a8f254e114d569a92188473e010c3\",\"order\":5},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"smgManagementOfCompany\",\"link\":\"../youtuantuan/shop_market_goods_management_of_company.html\",\"description\":\"门店超市商品管理（企业）。\",\"text\":\"门店超市商品管理（企业）\",\"uuid\":\"6768048ab4544f609a0135a3f9cb6763\",\"order\":17},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"smeManagementOfCompany\",\"link\":\"../youtuantuan/shop_station_employee_management_of_company.html\",\"description\":\"门店超市员工管理（企业）。\",\"text\":\"门店超市员工管理（企业）\",\"uuid\":\"7c7b8286a17a4ff68ac5561cf4348b0b\",\"order\":20},{\"parent_uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"name\":\"mrManagementOfCompany\",\"link\":\"../youtuantuan/marketing_rule_management_of_company.html\",\"description\":\"营销规则管理（企业）。\",\"text\":\"营销规则管理（企业）\",\"uuid\":\"7ec18ebf8e5143e6908f94f7daab0b8a\",\"order\":8},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"name\":\"companyShop\",\"description\":\"企业门店。\",\"text\":\"企业门店\",\"uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"order\":2},{\"parent_uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"name\":\"queryStatistics\",\"description\":\"查询统计。\",\"text\":\"查询统计\",\"uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"order\":1},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"ssgManagementOfCompany\",\"link\":\"../youtuantuan/shop_station_goods_management_of_company.html\",\"description\":\"门店站点商品管理（企业）。\",\"text\":\"门店站点商品管理（企业）\",\"uuid\":\"9c06d67f6ce84fdd8d0fd55496e223af\",\"order\":11},{\"parent_uuid\":\"121170c6c4c4490b9883aa5c5fd2d90b\",\"name\":\"mcManagementOfCompany\",\"link\":\"../youtuantuan/marketing_campaign_management_of_company.html\",\"description\":\"营销活动管理（企业）。\",\"text\":\"营销活动管理（企业）\",\"uuid\":\"a228b7e7e65746489ac903950a4a0630\",\"order\":5},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"shopDeviceManagementOfCompany\",\"link\":\"../youtuantuan/shop_device_management_of_company.html\",\"description\":\"门店设备管理（企业）。\",\"text\":\"门店设备管理（企业）\",\"uuid\":\"a36021757335483892cb7031b7616c52\",\"order\":8},{\"parent_uuid\":\"0\",\"name\":\"youtuantuan\",\"description\":\"油团团模块功能相关。\",\"text\":\"智慧易加\",\"uuid\":\"b8eab51abf654483b42c7f380e0e7548\",\"order\":3},{\"parent_uuid\":\"8bbeafcb5c1d473e8deb26de4ddb5851\",\"name\":\"sseManagementOfCompany\",\"link\":\"../youtuantuan/shop_station_employee_management_of_company.html\",\"description\":\"门店站点员工管理（企业）。\",\"text\":\"门店站点员工管理（企业）\",\"uuid\":\"bcd8f79d01334105bcc0def6f472a257\",\"order\":14},{\"parent_uuid\":\"99fc9c7a112f4c36a80874cdd3a89da0\",\"name\":\"ssgManagementOfPlatform\",\"link\":\"../youtuantuan/shop_station_goods_management_of_platform.html\",\"description\":\"门店站点商品管理（平台）。\",\"text\":\"门店站点商品管理（平台）\",\"uuid\":\"eb7ead3d7bcf4d4bbd961c795033fde0\",\"order\":10},{\"parent_uuid\":\"1bf01d70c88b4a66834487f7b5a510a9\",\"name\":\"rcManagementOfCompany\",\"link\":\"../youtuantuan/rechargeable_card_management_of_company.html\",\"description\":\"充值卡管理（企业）。\",\"text\":\"充值卡管理（企业）\",\"uuid\":\"f8218187e4bc46a1a1a0410351e20728\",\"order\":2}]',4,1658295862635,'2022-07-20 13:44:22',NULL),('f60a18f8a2e24dfd944c9e1218d3de53','ds_user','督审用户。','*',NULL,5,1677220140729,'2023-02-24 14:29:00',NULL);
/*!40000 ALTER TABLE `account-security_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account-security_user`
--

DROP TABLE IF EXISTS `account-security_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account-security_user` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `role_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色的uuid',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称',
  `password` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `failed_retry_count` tinyint NOT NULL COMMENT '失败重复计数',
  `frozen_timestamp` bigint DEFAULT NULL COMMENT '冻结时间戳',
  `frozen_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冻结时间',
  `login_token` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登陆令牌',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号状态\\nNORMAL：正常\\nFROZEN：冻结\\nLOCK：锁定\\nDELETE：删除',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `query` (`uuid`,`role_uuid`,`name`,`password`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号安全_用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account-security_user`
--

LOCK TABLES `account-security_user` WRITE;
/*!40000 ALTER TABLE `account-security_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `account-security_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_admin-info`
--

DROP TABLE IF EXISTS `svp_admin-info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_admin-info` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `admin_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员的uuid',
  `real_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `department_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位的uuid',
  `problem_count` tinyint NOT NULL COMMENT '问题计数',
  `score` int NOT NULL COMMENT '分值',
  `manage_departments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '管理单位（单位的uuid集合，分号分割）',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `admin_uuid_UNIQUE` (`admin_uuid`),
  KEY `query` (`uuid`,`admin_uuid`,`department_uuid`,`remove_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_管理员-信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_admin-info`
--

LOCK TABLES `svp_admin-info` WRITE;
/*!40000 ALTER TABLE `svp_admin-info` DISABLE KEYS */;
INSERT INTO `svp_admin-info` VALUES ('91200e45676e4efa8f3a0ae47fadb4fe','bf12caa58a344b1a9e2d83b896ab3c9c','伟山','a5602cb9a86b44758cfafaff1289b3f0',0,10,NULL,NULL),('bf714056169d401295af0083fbaf8c7d','ce97133a99304040b4ca9218469a3989','老袁','a5602cb9a86b44758cfafaff1289b3f0',0,10,NULL,NULL),('fec4a474ef254492b73e8a29b89b5873','95205dceefbd4dadbea7eea799312141','少文','a5602cb9a86b44758cfafaff1289b3f0',0,10,NULL,NULL);
/*!40000 ALTER TABLE `svp_admin-info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_department`
--

DROP TABLE IF EXISTS `svp_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_department` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `parent_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级单位的uuid（顶级为0）',
  `type_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型的uuid',
  `level` int NOT NULL COMMENT '级别',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `order` int NOT NULL COMMENT '排序编号（注意：考虑到地点的数量，因此不能用TINYINT类型）',
  `order_group` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '排序编号组（%06d为一级，最多10级，所以长度为60，如果级数扩大，则增加对应长度）',
  `problem_types` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '问题类型（JSONArray格式）',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`parent_uuid`,`type_uuid`,`create_timestamp`,`remove_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_单位-类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_department`
--

LOCK TABLES `svp_department` WRITE;
/*!40000 ALTER TABLE `svp_department` DISABLE KEYS */;
INSERT INTO `svp_department` VALUES ('02dde08a69fe42eea4c402e67c47b449','e77b1abee2fa48ff8932eca1024ae53e','bdeff8d7f053484485c1b593b9878413',3,'月牙河派出所社区警务一队',1,'000001000008000001',NULL,1678882224274,'2023-03-15 20:10:24',NULL),('046385a30e6742688deab94630e83ab5','e77b1abee2fa48ff8932eca1024ae53e','bdeff8d7f053484485c1b593b9878413',3,'月牙河派出所综合指挥室',3,'000001000008000003',NULL,1678882235097,'2023-03-15 20:10:35',NULL),('05c438a508034554be1c48a9574e1eff','5ebe314ee1fc4c398b02d53fff62980b','bdeff8d7f053484485c1b593b9878413',3,'政治处综合科',2,'000001000019000002',NULL,1678882750094,'2023-03-15 20:19:10',NULL),('0769d8f0f6a448cb8320bba21166b61c','d56df3641fd1437492094edb60a58f1f','bdeff8d7f053484485c1b593b9878413',3,'特警支队一大队',1,'000001000016000001',NULL,1678882601244,'2023-03-15 20:16:41',NULL),('07732fcaed7445dc84f092506f5ad40e','40889c723ddf438d9b9299ab3ff3a23a','bdeff8d7f053484485c1b593b9878413',3,'墙子派出所社区警务一队',1,'000001000007000001',NULL,1678882175423,'2023-03-15 20:09:35',NULL),('09c4d821cae8451b8e55a2be2cd96177','b2dcd563e940427d997d3df1f63b0763','bdeff8d7f053484485c1b593b9878413',3,'鸿顺里派出所社区警务二队',2,'000001000005000002',NULL,1678882054689,'2023-03-15 20:07:34',NULL),('0e522f851855472097086a2a08dcad89','f82272a1a0dc4b57ac2726705a1f5095','bdeff8d7f053484485c1b593b9878413',3,'国内安全保卫支队一大队',1,'000001000014000001',NULL,1678882517225,'2023-03-15 20:15:17',NULL),('10d111616c2f47d9a256ffa84c142238','d56df3641fd1437492094edb60a58f1f','bdeff8d7f053484485c1b593b9878413',3,'特警支队二大队',2,'000001000016000002',NULL,1678882606984,'2023-03-15 20:16:46',NULL),('12eff3a29d404d748f12cc04d35e37dc','14aa993041934c6d9035cc1f780984ef','bdeff8d7f053484485c1b593b9878413',3,'望海楼派出所社区警务一队',1,'000001000002000001',NULL,1678881724082,'2023-03-15 20:02:04',NULL),('14a4f765f6764762b083804508b69913','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'宁园街派出所',6,'000001000006',NULL,1678882091346,'2023-03-15 20:08:11',NULL),('14aa993041934c6d9035cc1f780984ef','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'望海楼派出所',2,'000001000002',NULL,1678881705732,'2023-03-15 20:01:45',NULL),('15aec531e29244068d3ec594b26cf038','b675a80b384043e8a033d36c546fa5b2','bdeff8d7f053484485c1b593b9878413',3,'指挥室研究宣传科',5,'000001000012000005',NULL,1678882456796,'2023-03-15 20:14:16',NULL),('17b6f466d00d42f2bfcd066eac08e3fa','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'警务保障室',20,'000001000020',NULL,1678882784094,'2023-03-15 20:19:44',NULL),('198c3b8fb1bb4808a9df6ccd309c6785','5ebe314ee1fc4c398b02d53fff62980b','bdeff8d7f053484485c1b593b9878413',3,'政治处绩效考核科',5,'000001000019000005',NULL,1678882766815,'2023-03-15 20:19:26',NULL),('1b0656506c8c4c7db13ecae48e48121e','e77b1abee2fa48ff8932eca1024ae53e','bdeff8d7f053484485c1b593b9878413',3,'月牙河派出所案件办理队',4,'000001000008000004',NULL,1678882240485,'2023-03-15 20:10:40',NULL),('1de0eacd2ba74af79b8a6e39d21dc000','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'建昌道派出所',10,'000001000010',NULL,1678882297572,'2023-03-15 20:11:37',NULL),('1f248850107d4448982f18652784ae0c','22ba0f3875c8498eacc0a964c575e3bd','bdeff8d7f053484485c1b593b9878413',3,'光复道派出所综合指挥室',3,'000001000001000003',NULL,1678881656387,'2023-03-15 20:00:56',NULL),('22ba0f3875c8498eacc0a964c575e3bd','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'光复道派出所',1,'000001000001',NULL,1678881611452,'2023-03-15 20:00:11',NULL),('235bc0208a1c49648b6ca51b89343912','1de0eacd2ba74af79b8a6e39d21dc000','bdeff8d7f053484485c1b593b9878413',3,'建昌道派出所社区警务一队',1,'000001000010000001',NULL,1678882308712,'2023-03-15 20:11:48',NULL),('260664803e8c4caf8a7fd1f59cc5eb77','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'法制支队',11,'000001000011',NULL,1678882345682,'2023-03-15 20:12:25',NULL),('28049429f1404d7fbd7678f07fad1494','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队八大队',8,'000001000023000008',NULL,1678882904931,'2023-03-15 20:21:44',NULL),('288e105f219f453dba064e4f0a01cb7e','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'铁东路派出所',3,'000001000003',NULL,1678881777084,'2023-03-15 20:02:57',NULL),('2ae824a824f5450791923af60038e2a1','40889c723ddf438d9b9299ab3ff3a23a','bdeff8d7f053484485c1b593b9878413',3,'墙子派出所社区警务二队',2,'000001000007000002',NULL,1678882181408,'2023-03-15 20:09:41',NULL),('2cb41b5a35ba43e29973421c3fb7a610','f82272a1a0dc4b57ac2726705a1f5095','bdeff8d7f053484485c1b593b9878413',3,'国内安全保卫支队三大队',3,'000001000014000003',NULL,1678882528420,'2023-03-15 20:15:28',NULL),('2d04e98657204bf4834a5059c90a9df7','5ebe314ee1fc4c398b02d53fff62980b','bdeff8d7f053484485c1b593b9878413',3,'政治处党建工作科',1,'000001000019000001',NULL,1678882744549,'2023-03-15 20:19:04',NULL),('2e0bb8c5a95b4ad7973fa6cb36c590b3','14aa993041934c6d9035cc1f780984ef','bdeff8d7f053484485c1b593b9878413',3,'望海楼派出所社区警务二队',2,'000001000002000002',NULL,1678881735885,'2023-03-15 20:02:15',NULL),('31a1110d34554af3acba297ffdba85cc','a1704ec322fb4329943c500b1335a41a','bdeff8d7f053484485c1b593b9878413',3,'江都路派出所社区警务二队',2,'000001000009000002',NULL,1678882270909,'2023-03-15 20:11:10',NULL),('324cb873eb47411287bf7383dfa493df','1de0eacd2ba74af79b8a6e39d21dc000','bdeff8d7f053484485c1b593b9878413',3,'建昌道派出所综合指挥室',3,'000001000010000003',NULL,1678882320728,'2023-03-15 20:12:00',NULL),('375bde38a14d48dab0d593e605ea73c7','c61457ade59c441ca58952473c5b419d','bdeff8d7f053484485c1b593b9878413',3,'治安管理支队三大队',3,'000001000022000003',NULL,1678882825691,'2023-03-15 20:20:25',NULL),('3b0b65546f4449e4a7c4542cfab5ed24','40889c723ddf438d9b9299ab3ff3a23a','bdeff8d7f053484485c1b593b9878413',3,'墙子派出所综合指挥室',3,'000001000007000003',NULL,1678882186853,'2023-03-15 20:09:46',NULL),('3fcbc4eb5e164951a9e75163b5972458','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队三大队',3,'000001000023000003',NULL,1678882874937,'2023-03-15 20:21:14',NULL),('4001cbc4a16943d5927366fc95d06ccc','260664803e8c4caf8a7fd1f59cc5eb77','bdeff8d7f053484485c1b593b9878413',3,'法制支队二大队',2,'000001000011000002',NULL,1678882362186,'2023-03-15 20:12:42',NULL),('40889c723ddf438d9b9299ab3ff3a23a','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'墙子派出所',7,'000001000007',NULL,1678882165110,'2023-03-15 20:09:25',NULL),('495ca6dd5ce04405b0caf9f09d410410','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'看守所',18,'000001000018',NULL,1678882655059,'2023-03-15 20:17:35',NULL),('499911f71fc94bbcbba7d5c2f954d777','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'新开河派出所',4,'000001000004',NULL,1678881981180,'2023-03-15 20:06:21',NULL),('4b4defa48c9240e7b382f161ad54ff93','22ba0f3875c8498eacc0a964c575e3bd','bdeff8d7f053484485c1b593b9878413',3,'光复道派出所社区警务一队',1,'000001000001000001',NULL,1678881630601,'2023-03-15 20:00:30',NULL),('4bfaaf6450d349b18ffd92c1e69462a9','f82272a1a0dc4b57ac2726705a1f5095','bdeff8d7f053484485c1b593b9878413',3,'国内安全保卫支队四大队',4,'000001000014000004',NULL,1678882534743,'2023-03-15 20:15:34',NULL),('4c80b90891254e8b9c912d3497e42aef','1de0eacd2ba74af79b8a6e39d21dc000','bdeff8d7f053484485c1b593b9878413',3,'建昌道派出所案件办理队',4,'000001000010000004',NULL,1678882328861,'2023-03-15 20:12:08',NULL),('54c9d6c7de42410989c9064c42559e7d','c61457ade59c441ca58952473c5b419d','bdeff8d7f053484485c1b593b9878413',3,'治安管理支队四大队',4,'000001000022000004',NULL,1678882830956,'2023-03-15 20:20:30',NULL),('56565cdba9e5458082cad3fb88dd21e8','5ebe314ee1fc4c398b02d53fff62980b','bdeff8d7f053484485c1b593b9878413',3,'政治处宣传科',3,'000001000019000003',NULL,1678882755519,'2023-03-15 20:19:15',NULL),('57235c50b8c440c4a30ac4580ff1aa8f','499911f71fc94bbcbba7d5c2f954d777','bdeff8d7f053484485c1b593b9878413',3,'新开河派出所综合指挥室',3,'000001000004000003',NULL,1678882014714,'2023-03-15 20:06:54',NULL),('588471f86f734198ae4c96b55e0ecee9','499911f71fc94bbcbba7d5c2f954d777','bdeff8d7f053484485c1b593b9878413',3,'新开河派出所社区警务二队',2,'000001000004000002',NULL,1678882008796,'2023-03-15 20:06:48',NULL),('5dc26bd920464e97a9aba81ba96cbfb1','c61457ade59c441ca58952473c5b419d','bdeff8d7f053484485c1b593b9878413',3,'治安管理支队一大队',1,'000001000022000001',NULL,1678882814304,'2023-03-15 20:20:14',NULL),('5ebe314ee1fc4c398b02d53fff62980b','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'政治处',19,'000001000019',NULL,1678882730735,'2023-03-15 20:18:50',NULL),('612acb4ee9714529a0c88c09acabab73','f82272a1a0dc4b57ac2726705a1f5095','bdeff8d7f053484485c1b593b9878413',3,'国内安全保卫支队二大队',2,'000001000014000002',NULL,1678882522692,'2023-03-15 20:15:22',NULL),('632eb6a692c6473b947e7f09107622ce','1de0eacd2ba74af79b8a6e39d21dc000','bdeff8d7f053484485c1b593b9878413',3,'建昌道派出所社区警务二队',2,'000001000010000002',NULL,1678882314911,'2023-03-15 20:11:54',NULL),('65d42c61972c4d98a9ab9e1226aa3641','f82272a1a0dc4b57ac2726705a1f5095','bdeff8d7f053484485c1b593b9878413',3,'国内安全保卫支队五大队',5,'000001000014000005',NULL,1678882541470,'2023-03-15 20:15:41',NULL),('66d1bcb8593e4fcea9158a260977f412','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'纪委办公室',21,'000001000021',NULL,1678882791328,'2023-03-15 20:19:51',NULL),('67810901548c40abb288c1d69a86062a','b2dcd563e940427d997d3df1f63b0763','bdeff8d7f053484485c1b593b9878413',3,'鸿顺里派出所综合指挥室',3,'000001000005000003',NULL,1678882062059,'2023-03-15 20:07:42',NULL),('69938fc7da6c4dada29bc372997eb126','b1c7c2ec821e478886e6e45cefbe46d7','bdeff8d7f053484485c1b593b9878413',3,'科技信息化支队一大队',1,'000001000017000001',NULL,1678882636046,'2023-03-15 20:17:16',NULL),('6a6e278ab67d4707aeb35e10588c1520','288e105f219f453dba064e4f0a01cb7e','bdeff8d7f053484485c1b593b9878413',3,'铁东路派出所综合指挥室',3,'000001000003000003',NULL,1678881935242,'2023-03-15 20:05:35',NULL),('6e0113ae5808467fa4f33169615a690b','b675a80b384043e8a033d36c546fa5b2','bdeff8d7f053484485c1b593b9878413',3,'指挥室情报科',1,'000001000012000001',NULL,1678882396435,'2023-03-15 20:13:16',NULL),('71b3f3f7868244329c7b31f6ce9fe596','260664803e8c4caf8a7fd1f59cc5eb77','bdeff8d7f053484485c1b593b9878413',3,'法制支队三大队',3,'000001000011000003',NULL,1678882367363,'2023-03-15 20:12:47',NULL),('720bcda60aa640bc99482a53eeb9bee5','d56df3641fd1437492094edb60a58f1f','bdeff8d7f053484485c1b593b9878413',3,'特警支队三大队',3,'000001000016000003',NULL,1678882613182,'2023-03-15 20:16:53',NULL),('762879c922cf463884f70574deff7ea2','e9b855c1fae7413594f9d3b9d4032814','bdeff8d7f053484485c1b593b9878413',3,'督查审计支队二大队',2,'000001000013000002',NULL,1678882495758,'2023-03-15 20:14:55',NULL),('7629c175bacc4a1dad5364c42be1ba74','b675a80b384043e8a033d36c546fa5b2','bdeff8d7f053484485c1b593b9878413',3,'指挥室巡逻防控管理科',2,'000001000012000002',NULL,1678882410072,'2023-03-15 20:13:30',NULL),('78004c0c06fe48738fe1a57664f2bbcf','5ebe314ee1fc4c398b02d53fff62980b','bdeff8d7f053484485c1b593b9878413',3,'政治处辅警人员管理科',4,'000001000019000004',NULL,1678882760738,'2023-03-15 20:19:20',NULL),('7d27f4568f144cbd9f35a52eadad2f05','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队九大队',9,'000001000023000009',NULL,1678882912051,'2023-03-15 20:21:52',NULL),('7d79e0689e6545e38123e8d1f9b48466','14aa993041934c6d9035cc1f780984ef','bdeff8d7f053484485c1b593b9878413',3,'望海楼派出所案件办理队',4,'000001000002000004',NULL,1678881761504,'2023-03-15 20:02:41',NULL),('8191b1af50b445079591aa0d9cfe6d90','22ba0f3875c8498eacc0a964c575e3bd','bdeff8d7f053484485c1b593b9878413',3,'光复道派出所社区警务二队',2,'000001000001000002',NULL,1678881642166,'2023-03-15 20:00:42',NULL),('8c5ccb3741eb4f03b64a9d2f6d607ecc','a1704ec322fb4329943c500b1335a41a','bdeff8d7f053484485c1b593b9878413',3,'江都路派出所社区警务一队',1,'000001000009000001',NULL,1678882265541,'2023-03-15 20:11:05',NULL),('8d4f195ddd8c484b9cf1303c0bb20944','b675a80b384043e8a033d36c546fa5b2','bdeff8d7f053484485c1b593b9878413',3,'指挥室机要保密科',3,'000001000012000003',NULL,1678882445724,'2023-03-15 20:14:05',NULL),('96c3c1719b944cafb9e00dc1f627b68e','260664803e8c4caf8a7fd1f59cc5eb77','bdeff8d7f053484485c1b593b9878413',3,'法制支队一大队',1,'000001000011000001',NULL,1678882357248,'2023-03-15 20:12:37',NULL),('9a0bf5f3eecb415dbff6e59eb04a3698','14a4f765f6764762b083804508b69913','bdeff8d7f053484485c1b593b9878413',3,'宁园街派出所社区警务一队',1,'000001000006000001',NULL,1678882100648,'2023-03-15 20:08:20',NULL),('a1704ec322fb4329943c500b1335a41a','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'江都路派出所',9,'000001000009',NULL,1678882255999,'2023-03-15 20:10:55',NULL),('a17dc20c0fc94680a6f4decc37ee29c5','288e105f219f453dba064e4f0a01cb7e','bdeff8d7f053484485c1b593b9878413',3,'铁东路派出所社区警务二队',2,'000001000003000002',NULL,1678881923112,'2023-03-15 20:05:23',NULL),('a38b9d025a0c4158a87789274e3f8b06','c61457ade59c441ca58952473c5b419d','bdeff8d7f053484485c1b593b9878413',3,'治安管理支队二大队',2,'000001000022000002',NULL,1678882820571,'2023-03-15 20:20:20',NULL),('a3e1374055ba414f8be216eccccc3769','14a4f765f6764762b083804508b69913','bdeff8d7f053484485c1b593b9878413',3,'宁园街派出所社区警务二队',2,'000001000006000002',NULL,1678882106692,'2023-03-15 20:08:26',NULL),('a454cff198104053ba0c49939dd02f87','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队四大队',4,'000001000023000004',NULL,1678882880943,'2023-03-15 20:21:20',NULL),('a5430c45d4ba49d6b48c3ca3282ecbe4','b675a80b384043e8a033d36c546fa5b2','bdeff8d7f053484485c1b593b9878413',3,'指挥室综合科',6,'000001000012000006',NULL,1678882462409,'2023-03-15 20:14:22',NULL),('a5602cb9a86b44758cfafaff1289b3f0','0','44704aec8a5e401c8a6b9f7bde7fd419',1,'天津市公安局河北分局',1,'000001',NULL,1678873483258,'2023-03-15 17:44:43',NULL),('a7221935abbb43498cd4093853f70989','b675a80b384043e8a033d36c546fa5b2','bdeff8d7f053484485c1b593b9878413',3,'指挥室指挥调度科',4,'000001000012000004',NULL,1678882451248,'2023-03-15 20:14:11',NULL),('af6c25818c0e47c7aefb9d3bbeb24973','499911f71fc94bbcbba7d5c2f954d777','bdeff8d7f053484485c1b593b9878413',3,'新开河派出所案件办理队',4,'000001000004000004',NULL,1678882020363,'2023-03-15 20:07:00',NULL),('b1c7c2ec821e478886e6e45cefbe46d7','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'科技信息化支队',17,'000001000017',NULL,1678882625918,'2023-03-15 20:17:05',NULL),('b2dcd563e940427d997d3df1f63b0763','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'鸿顺里派出所',5,'000001000005',NULL,1678882038855,'2023-03-15 20:07:18',NULL),('b675a80b384043e8a033d36c546fa5b2','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'指挥室',12,'000001000012',NULL,1678882386848,'2023-03-15 20:13:06',NULL),('b7376dbd185e466498e6cac46d6acceb','288e105f219f453dba064e4f0a01cb7e','bdeff8d7f053484485c1b593b9878413',3,'铁东路派出所社区警务一队',1,'000001000003000001',NULL,1678881916601,'2023-03-15 20:05:16',NULL),('bad18a49acbc4b639c511d159510a129','c61457ade59c441ca58952473c5b419d','bdeff8d7f053484485c1b593b9878413',3,'治安管理支队五大队',5,'000001000022000005',NULL,1678882836018,'2023-03-15 20:20:36',NULL),('c61457ade59c441ca58952473c5b419d','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'治安管理支队',22,'000001000022',NULL,1678882799744,'2023-03-15 20:19:59',NULL),('cc0b4375baa54d888bc3e6dfd1abb0ca','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队七大队',7,'000001000023000007',NULL,1678882898247,'2023-03-15 20:21:38',NULL),('ceaf672cc2514d2fada041ade9aeeaae','a1704ec322fb4329943c500b1335a41a','bdeff8d7f053484485c1b593b9878413',3,'江都路派出所案件办理队',4,'000001000009000004',NULL,1678882281935,'2023-03-15 20:11:21',NULL),('cf704b4907df4246b64efb57d354118c','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队六大队',6,'000001000023000006',NULL,1678882891969,'2023-03-15 20:21:31',NULL),('cf9aeb94018d4c6f901e2a405588f110','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队二大队',2,'000001000023000002',NULL,1678882868556,'2023-03-15 20:21:08',NULL),('d278e6e598454c19a5ae6192b86eaba9','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队一大队',1,'000001000023000001',NULL,1678882863032,'2023-03-15 20:21:03',NULL),('d551f72552c74bcf927399da902491d7','b2dcd563e940427d997d3df1f63b0763','bdeff8d7f053484485c1b593b9878413',3,'鸿顺里派出所案件办理队',4,'000001000005000004',NULL,1678882068504,'2023-03-15 20:07:48',NULL),('d56df3641fd1437492094edb60a58f1f','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'特警支队',16,'000001000016',NULL,1678882572353,'2023-03-15 20:16:12',NULL),('d64843303176418f828c915be7530867','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'打击犯罪侦查支队',23,'000001000023',NULL,1678882849295,'2023-03-15 20:20:49',NULL),('d76d2b7c3e9a47a1bc698e020a097b11','499911f71fc94bbcbba7d5c2f954d777','bdeff8d7f053484485c1b593b9878413',3,'新开河派出所社区警务一队',1,'000001000004000001',NULL,1678882002699,'2023-03-15 20:06:42',NULL),('dee265b7d6aa4edbae770151c61e0a96','e77b1abee2fa48ff8932eca1024ae53e','bdeff8d7f053484485c1b593b9878413',3,'月牙河派出所社区警务二队',2,'000001000008000002',NULL,1678882229855,'2023-03-15 20:10:29',NULL),('df04d6dcf2e84e59b4a94645306ff93a','b1c7c2ec821e478886e6e45cefbe46d7','bdeff8d7f053484485c1b593b9878413',3,'科技信息化支队二大队',2,'000001000017000002',NULL,1678882641255,'2023-03-15 20:17:21',NULL),('e5351988ceeb48a4b4a718bffb50c45e','288e105f219f453dba064e4f0a01cb7e','bdeff8d7f053484485c1b593b9878413',3,'铁东路派出所案件办理队',4,'000001000003000004',NULL,1678881941022,'2023-03-15 20:05:41',NULL),('e57ac14d8e994493befb4a29c110444a','e9b855c1fae7413594f9d3b9d4032814','bdeff8d7f053484485c1b593b9878413',3,'督查审计支队一大队',1,'000001000013000001',NULL,1678882490426,'2023-03-15 20:14:50',NULL),('e5afddc2ccc441d9aa1507f8babd6f5a','40889c723ddf438d9b9299ab3ff3a23a','bdeff8d7f053484485c1b593b9878413',3,'墙子派出所案件办理队',4,'000001000007000004',NULL,1678882193570,'2023-03-15 20:09:53',NULL),('e71b8095b8c9490d82e16157c3fdc623','d64843303176418f828c915be7530867','bdeff8d7f053484485c1b593b9878413',3,'打击犯罪侦查支队五大队',5,'000001000023000005',NULL,1678882886457,'2023-03-15 20:21:26',NULL),('e77b1abee2fa48ff8932eca1024ae53e','a5602cb9a86b44758cfafaff1289b3f0','3f5e6e68e88b4ae6a17f19c7e2c88fb7',2,'月牙河派出所',8,'000001000008',NULL,1678882214485,'2023-03-15 20:10:14',NULL),('e981bbfae8ba45bd859a6383fe73d28d','b2dcd563e940427d997d3df1f63b0763','bdeff8d7f053484485c1b593b9878413',3,'鸿顺里派出所社区警务一队',1,'000001000005000001',NULL,1678882049382,'2023-03-15 20:07:29',NULL),('e9b855c1fae7413594f9d3b9d4032814','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'督查审计支队',13,'000001000013',NULL,1678882478216,'2023-03-15 20:14:38',NULL),('f3196cb684c5412bbd558b3615f74b2b','14a4f765f6764762b083804508b69913','bdeff8d7f053484485c1b593b9878413',3,'宁园街派出所综合指挥室',3,'000001000006000003',NULL,1678882115950,'2023-03-15 20:08:35',NULL),('f32a0b62d0704b048edf4295610d9155','22ba0f3875c8498eacc0a964c575e3bd','bdeff8d7f053484485c1b593b9878413',3,'光复道派出所案件办理队',4,'000001000001000004',NULL,1678881683356,'2023-03-15 20:01:23',NULL),('f6f5a85894d5404482bfa2bc72d377ee','260664803e8c4caf8a7fd1f59cc5eb77','bdeff8d7f053484485c1b593b9878413',3,'法制支队四大队',4,'000001000011000004',NULL,1678882372516,'2023-03-15 20:12:52',NULL),('f70c0ec2a2354d30ad23ed68e340b467','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'网络安全保卫支队',15,'000001000015',NULL,1678882562339,'2023-03-15 20:16:02',NULL),('f82272a1a0dc4b57ac2726705a1f5095','a5602cb9a86b44758cfafaff1289b3f0','0bdd5462a3c2443bbaf99e351b788371',2,'国内安全保卫支队',14,'000001000014',NULL,1678882506659,'2023-03-15 20:15:06',NULL),('fb309fa7b7dc4fddafa3dc75caa83e64','14aa993041934c6d9035cc1f780984ef','bdeff8d7f053484485c1b593b9878413',3,'望海楼派出所综合指挥室',3,'000001000002000003',NULL,1678881746428,'2023-03-15 20:02:26',NULL),('fc329ff19b474e22a938fc842c9f2ba5','14a4f765f6764762b083804508b69913','bdeff8d7f053484485c1b593b9878413',3,'宁园街派出所案件办理队',4,'000001000006000004',NULL,1678882123589,'2023-03-15 20:08:43',NULL),('fdcae2f0850446e09574e15882b90361','a1704ec322fb4329943c500b1335a41a','bdeff8d7f053484485c1b593b9878413',3,'江都路派出所综合指挥室',3,'000001000009000003',NULL,1678882276263,'2023-03-15 20:11:16',NULL);
/*!40000 ALTER TABLE `svp_department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_department-type`
--

DROP TABLE IF EXISTS `svp_department-type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_department-type` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `order` tinyint NOT NULL COMMENT '排序编号',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_单位-类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_department-type`
--

LOCK TABLES `svp_department-type` WRITE;
/*!40000 ALTER TABLE `svp_department-type` DISABLE KEYS */;
INSERT INTO `svp_department-type` VALUES ('0bdd5462a3c2443bbaf99e351b788371','支队',3,NULL),('3f5e6e68e88b4ae6a17f19c7e2c88fb7','派出所',2,NULL),('44704aec8a5e401c8a6b9f7bde7fd419','分局',1,NULL),('bdeff8d7f053484485c1b593b9878413','大队',4,NULL);
/*!40000 ALTER TABLE `svp_department-type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_feedback`
--

DROP TABLE IF EXISTS `svp_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_feedback` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `problem_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题的uuid',
  `from_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源的uuid（如果是个人问题，则记录用户的uuid；如果是单位问题，依旧记录用户的uuid，但接口在查找的时候一定要将单位的uuid一并返回，单位的uuid是现场督查_问题表里type为单位的to_uuid）',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `last_update_timestamp` bigint DEFAULT NULL COMMENT '最后修改时间戳',
  `last_update_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后修改时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `problem_uuid_UNIQUE` (`problem_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_反馈';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_feedback`
--

LOCK TABLES `svp_feedback` WRITE;
/*!40000 ALTER TABLE `svp_feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `svp_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_logon-logoff-info`
--

DROP TABLE IF EXISTS `svp_logon-logoff-info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_logon-logoff-info` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型',
  `ip_addr` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ip地址',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`name`,`type`,`ip_addr`,`create_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_登入-登出-信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_logon-logoff-info`
--

LOCK TABLES `svp_logon-logoff-info` WRITE;
/*!40000 ALTER TABLE `svp_logon-logoff-info` DISABLE KEYS */;
INSERT INTO `svp_logon-logoff-info` VALUES ('00858e0ac5994c8bb2d0ad06ab16a911','jack','LOGIN','192.168.10.196',1677641830530,'2023-03-01 11:37:10',NULL),('008cc3c7de4d469083c9e0ff4144a246','shaowen','LOGIN','192.168.10.242',1677639375176,'2023-03-01 10:56:15',NULL),('03d1dcfc1f92451ba9d4a55cce44da21','jack','LOGIN','192.168.10.242',1677639069762,'2023-03-01 10:51:09',NULL),('0a37a972ae5a4e6fb6f948ee7220f46e','jack','LOGIN','192.168.10.196',1677640979586,'2023-03-01 11:22:59',NULL),('0c37fd5a1259459bb691bd1f8e42299c','jack','LOGIN','192.168.10.196',1677564628283,'2023-02-28 14:10:28',NULL),('0c5a08eb4ebc44a69d7ec77691eaf186','jack','LOGIN','192.168.10.196',1678881402245,'2023-03-15 19:56:42',NULL),('0db5eebae8424020a0cdca761ae48fec','jack','LOGIN','192.168.10.196',1677641561110,'2023-03-01 11:32:41',NULL),('0df3bd5320b54e59abeceec138ffb7d5','jack','LOGIN','192.168.10.196',1677639409296,'2023-03-01 10:56:49',NULL),('0f1c24fe1e184a9da89489d16f5570d5','jack','LOGIN','192.168.10.242',1677579942369,'2023-02-28 18:25:42',NULL),('0f780770dffc4128990ba20a810189bc','jack','LOGIN','192.168.10.196',1678346492356,'2023-03-09 15:21:32',NULL),('102c5abf9e6d408c90ded673ae770d49','jack','LOGIN','0:0:0:0:0:0:0:1',1677637984432,'2023-03-01 10:33:04',NULL),('1222048ca69b43a78b4f662b6a4e7c56','jack','LOGIN','192.168.10.242',1677637761492,'2023-03-01 10:29:21',NULL),('1263032b3d8b4f139e5924213013039a','shaowen','LOGIN','192.168.10.242',1677726767805,'2023-03-02 11:12:47',NULL),('132d4fd7388d458b8ff03ac5cac739da','shaowen','LOGIN','192.168.10.242',1677657933016,'2023-03-01 16:05:33',NULL),('1389e7cb662e4814a7bbc5cc1d5a57c8','shaowen','LOGIN','192.168.10.242',1677635882966,'2023-03-01 09:58:02',NULL),('145a9e9f98b040959b48c2a3fd8e5afd','shaowen','LOGIN','192.168.10.242',1677648588345,'2023-03-01 13:29:48',NULL),('14d6031d08314da18bf8ea5934df9b7c','jack','LOGIN','192.168.10.242',1677638987164,'2023-03-01 10:49:47',NULL),('1760a8a67a7d473f98b271ad02b964f1','weishan','LOGIN','192.168.10.7',1677571142819,'2023-02-28 15:59:02',NULL),('1b18a1046cd64c59be9a6aa8adf2e653','jack','LOGIN','192.168.10.242',1677580141152,'2023-02-28 18:29:01',NULL),('223543d0744d4ba99db18b255ca02dd6','jack','LOGIN','192.168.10.196',1677554365729,'2023-02-28 11:19:25',NULL),('29960f766bfb49d4b71c9d9284238665','jack','LOGIN','192.168.10.196',1677642325227,'2023-03-01 11:45:25',NULL),('333037b48f6d4121b4de82c349f323fc','shaowen','LOGIN','192.168.10.242',1677647744166,'2023-03-01 13:15:44',NULL),('36d3af82d33448c9b0aa8d4d8f7834e6','shaowen','LOGIN','192.168.10.242',1677649250222,'2023-03-01 13:40:50',NULL),('38432246ef3f407c9e8277c0f19c0817','jack','LOGIN','192.168.10.242',1677582019494,'2023-02-28 19:00:19',NULL),('39b858fb8d19474ea5aa257fd46916f8','jack','LOGIN','192.168.10.242',1677645956005,'2023-03-01 12:45:56',NULL),('3b4ebe31aa094cbf9c4b81d29e2ca26c','shaowen','LOGIN','192.168.10.242',1677638035136,'2023-03-01 10:33:55',NULL),('3b67c35e8a784168a05fa9da926425b5','jack','LOGIN','192.168.10.196',1677567982284,'2023-02-28 15:06:22',NULL),('3d714ebf8dcb45cc8915757c20145abf','jack','LOGIN','192.168.10.196',1677639393650,'2023-03-01 10:56:33',NULL),('3ee6a9dab24e4b5db3c263d05d5c0bce','jack','LOGIN','192.168.10.242',1677636689210,'2023-03-01 10:11:29',NULL),('3f19944eae8b4c3ea4971edb6d67c89c','jack','LOGIN','192.168.10.196',1677642182406,'2023-03-01 11:43:02',NULL),('3f84bcb5a7cf42dca761dabb6e859025','jack','LOGIN','192.168.10.242',1677636860269,'2023-03-01 10:14:20',NULL),('41671ea242de434bb3d19aac0f941032','jack','LOGIN','192.168.10.196',1677639974507,'2023-03-01 11:06:14',NULL),('41c281971ef645e9b01626f7848e4448','shaowen','LOGIN','192.168.10.242',1677487714078,'2023-02-27 16:48:34',NULL),('439a92a9a5d84ce381569462e5cc8f95','shaowen','LOGIN','192.168.10.242',1677647113937,'2023-03-01 13:05:13',NULL),('44f852ba25ff4205a847807f78acea77','shaowen','LOGIN','192.168.10.242',1677656433779,'2023-03-01 15:40:33',NULL),('4722775c3e0c45c7a3444308f62eac4a','shaowen','LOGIN','192.168.10.242',1677722381930,'2023-03-02 09:59:41',NULL),('477dbfc40f204bfc90cbd37cf4285f42','shaowen','LOGIN','192.168.10.242',1677722767241,'2023-03-02 10:06:07',NULL),('47c41541815f4044bdfc5db86598da4c','jack','LOGIN','192.168.10.242',1677577825265,'2023-02-28 17:50:25',NULL),('487847f6043340a9b94e4ce5d774536b','shaowen','LOGIN','192.168.10.242',1677647744888,'2023-03-01 13:15:44',NULL),('488be1c7845143c0a98e4b94e98cd50f','jack','LOGIN','192.168.10.196',1677640996184,'2023-03-01 11:23:16',NULL),('48af5f8ab4834c1c8c896d6067d7d632','shaowen','LOGIN','192.168.10.242',1677653036234,'2023-03-01 14:43:56',NULL),('4adccae5e4334e08820e15e7c42f7b46','jack','LOGIN','192.168.10.196',1677545989201,'2023-02-28 08:59:49',NULL),('4b5b52bad65542d0ae0a005c25976ad3','jack','LOGIN','192.168.10.242',1677580936439,'2023-02-28 18:42:16',NULL),('4bfc761e18c3483c9cb90fdd79cbe261','jack','LOGIN','192.168.10.196',1677642172922,'2023-03-01 11:42:52',NULL),('4e01eb4ae6814e7cb36bef8c787590f8','jack','LOGIN','192.168.10.196',1677569232604,'2023-02-28 15:27:12',NULL),('4e4b6839022045f29ce19a76f48cdeb5','jack','LOGIN','192.168.10.242',1677580537867,'2023-02-28 18:35:37',NULL),('50258d5782b14d48bac6863ec1c21360','jack','LOGIN','127.0.0.1',1677854856175,'2023-03-03 22:47:36',NULL),('5558490b46cd4d87b73bee1eec796666','shaowen','LOGIN','192.168.10.242',1677652174322,'2023-03-01 14:29:34',NULL),('5657bc6bbbbf4c5786018f0f6571dad8','shaowen','LOGIN','192.168.10.242',1677651655737,'2023-03-01 14:20:55',NULL),('56d25b123f2346a9afdd76182b520634','shaowen','LOGIN','192.168.10.242',1677550536853,'2023-02-28 10:15:36',NULL),('58c0ca262f26436188a2f3f1161175be','jack','LOGIN','192.168.10.242',1677642956898,'2023-03-01 11:55:56',NULL),('5b541eee7534410f971f4f272e681a27','shaowen','LOGIN','192.168.10.242',1677651694687,'2023-03-01 14:21:34',NULL),('5c03e9bbde6c412ba9a510d9f4dc5f90','jack','LOGIN','192.168.10.196',1677639647940,'2023-03-01 11:00:47',NULL),('5c9cc4abc08a4704a6514567b6c691bd','jack','LOGIN','192.168.10.242',1677649041425,'2023-03-01 13:37:21',NULL),('5e17d95d3d994764b08c81721e22a9ad','jack','LOGIN','192.168.10.196',1677564701666,'2023-02-28 14:11:41',NULL),('6017746df2904f978526c00fc86e7470','jack','LOGIN','192.168.10.196',1677641082700,'2023-03-01 11:24:42',NULL),('603eb8bde7f8491eb35988734cf920e9','shaowen','LOGIN','192.168.10.242',1677657194680,'2023-03-01 15:53:14',NULL),('6129b639ba2a4c5ab4b1b067205dc73e','shaowen','LOGIN','192.168.10.242',1677638868433,'2023-03-01 10:47:48',NULL),('62e01bb4d9e8443dbbd15aa3e2fb5639','jack','LOGIN','192.168.10.242',1677582251429,'2023-02-28 19:04:11',NULL),('69ecaf11f77647678e7329b5105010be','shaowen','LOGIN','192.168.10.242',1677648708019,'2023-03-01 13:31:48',NULL),('6ab1b61e5d7a450cb2c26e9b7b461857','shaowen','LOGIN','192.168.10.242',1677647742607,'2023-03-01 13:15:42',NULL),('6d75c4fd13d241958167740d88fd6c70','shaowen','LOGIN','192.168.10.242',1677650265920,'2023-03-01 13:57:45',NULL),('6da526bd581b4b0b822e5b39888a740a','jack','LOGIN','192.168.10.196',1678439743429,'2023-03-10 17:15:43',NULL),('6dbe39acdcdd4eb2b40a7d4dbf0c960c','shaowen','LOGIN','192.168.10.242',1677655875909,'2023-03-01 15:31:15',NULL),('6ddc5de8a5c846438f73d992134b9060','jack','LOGIN','192.168.10.196',1677551977866,'2023-02-28 10:39:37',NULL),('78ef8a8b1c1045239356c5612bbd39a9','shaowen','LOGIN','192.168.10.242',1677658593994,'2023-03-01 16:16:33',NULL),('7b27c66b20e5438ca79558cdfa3d0326','jack','LOGIN','192.168.10.242',1677641997595,'2023-03-01 11:39:57',NULL),('7c38764e32c041148c6e94b6a8fe2fb5','jack','LOGIN','192.168.10.196',1677566507668,'2023-02-28 14:41:47',NULL),('7ee7b0abb08d4683b1fbdf37c9ef858c','jack','LOGIN','192.168.10.196',1677479471884,'2023-02-27 14:31:11',NULL),('81c458a0ae9f41a2a87973794e645940','jack','LOGIN','192.168.10.196',1677639570325,'2023-03-01 10:59:30',NULL),('81cf86fa42b64c028b398087356454ec','jack','LOGIN','192.168.10.196',1678068232326,'2023-03-06 10:03:52',NULL),('8780acf97c2d4cf4add21b2601cd114f','shaowen','LOGIN','192.168.10.242',1677649193685,'2023-03-01 13:39:53',NULL),('879d47b13a174c538f8d44dca9a98dd5','jack','LOGIN','192.168.10.242',1677637159955,'2023-03-01 10:19:19',NULL),('8a5e1fd838d944859d8dd26cf3889c1d','jack','LOGIN','192.168.10.196',1677651009672,'2023-03-01 14:10:09',NULL),('8a895aac869d4dd09372d996bf33d1da','shaowen','LOGIN','192.168.10.242',1677657078611,'2023-03-01 15:51:18',NULL),('8ab8e89fd01249179d14eebaa2b20806','jack','LOGIN','192.168.10.242',1677577883773,'2023-02-28 17:51:23',NULL),('8afd83f66a7d469f8910f1916eb858f2','jack','LOGIN','192.168.10.196',1677640705235,'2023-03-01 11:18:25',NULL),('8b73e2e02605493080307a1896209c2c','shaowen','LOGIN','192.168.10.242',1677657825645,'2023-03-01 16:03:45',NULL),('8d9b4220c0d746f3bcb864a3cc4f3c17','jack','LOGIN','192.168.10.196',1677649159902,'2023-03-01 13:39:19',NULL),('8e96d96d7a364025abacb0997e4788fc','jack','LOGIN','127.0.0.1',1677854996925,'2023-03-03 22:49:56',NULL),('8f38400965924d9f96c12cbbbedcb5bd','jack','LOGIN','192.168.10.196',1677639372916,'2023-03-01 10:56:12',NULL),('912f7d628a0a4779af8558012d73712d','shaowen','LOGIN','192.168.10.242',1677656731253,'2023-03-01 15:45:31',NULL),('9424bd519ff3499198337d5b2725705b','shaowen','LOGIN','192.168.10.242',1677652334525,'2023-03-01 14:32:14',NULL),('9442cf9bf323434a98f641aaa41b13b0','jack','LOGIN','192.168.10.196',1677641236365,'2023-03-01 11:27:16',NULL),('98ec1315d6a64e3da1e21fb6404bc5e6','shaowen','LOGIN','192.168.10.242',1677724204750,'2023-03-02 10:30:04',NULL),('9d20a7cec3bb4ddabee6f57ee38ac3e5','shaowen','LOGIN','192.168.10.242',1677648993563,'2023-03-01 13:36:33',NULL),('9da7d142d627403d8348b5006805daa4','jack','LOGIN','192.168.10.242',1677652215233,'2023-03-01 14:30:15',NULL),('a2094b5c56264473a98bf7dbbc3f5971','shaowen','LOGIN','192.168.10.242',1677649656933,'2023-03-01 13:47:36',NULL),('a2cc100456f542a39fae2c7657de1e07','jack','LOGIN','192.168.10.196',1677639502376,'2023-03-01 10:58:22',NULL),('a5502afdcb9447eca58bbf53bd741c7e','shaowen','LOGIN','192.168.10.242',1677656348360,'2023-03-01 15:39:08',NULL),('a95c8c724afa46c19b15ad0cc344c043','shaowen','LOGIN','192.168.10.242',1677550372112,'2023-02-28 10:12:52',NULL),('ad522d079ead48479fa37de2005f34b4','jack','LOGIN','192.168.10.196',1677660068727,'2023-03-01 16:41:08',NULL),('ad98e3ee50654491b4dd734b44573eda','jack','LOGIN','192.168.10.196',1678070339795,'2023-03-06 10:38:59',NULL),('ae95a5b42c9241bfb76b34be2be48ca6','shaowen','LOGIN','192.168.10.242',1677647357902,'2023-03-01 13:09:17',NULL),('aff94c0869944a0ca7cc576c811799d5','shaowen','LOGIN','192.168.10.242',1677657139459,'2023-03-01 15:52:19',NULL),('b0b77134443e4c4498f39b008c159c9c','weishan','LOGIN','192.168.10.196',1677554605045,'2023-02-28 11:23:25',NULL),('b0eded061af040a1bde72d5957779751','shaowen','LOGIN','192.168.10.242',1677657894869,'2023-03-01 16:04:54',NULL),('b4ab62cea7e44f9486dcb9cfc586c0bc','jack','LOGIN','127.0.0.1',1677934505998,'2023-03-04 20:55:05',NULL),('b4d064ea4be848699c0565dd677cb249','jack','LOGIN','192.168.10.242',1677636345364,'2023-03-01 10:05:45',NULL),('b6941f89e77841d087c694bdc43985be','shaowen','LOGIN','192.168.10.242',1677658653261,'2023-03-01 16:17:33',NULL),('b6e618ace9b04bf59173bdafecb3c4ce','jack','LOGIN','192.168.10.196',1677640733597,'2023-03-01 11:18:53',NULL),('b7f85bd9fe7a4ec99490cef858e94140','weishan','LOGIN','192.168.10.7',1677554611578,'2023-02-28 11:23:31',NULL),('bb18d831d1eb489d9dcd95d703ff4608','jack','LOGIN','192.168.10.196',1677657546588,'2023-03-01 15:59:06',NULL),('bbb46fb2199d4d2899eb6f09b86b1173','shaowen','LOGIN','192.168.10.242',1677648587945,'2023-03-01 13:29:47',NULL),('bc61b3abde29462cbd60ddc76563855b','jack','LOGIN','127.0.0.1',1677855056939,'2023-03-03 22:50:56',NULL),('bd3c589ca5cd40249bc96ed9a627e6ba','shaowen','LOGIN','192.168.10.242',1677655282863,'2023-03-01 15:21:22',NULL),('be0242dbca60453286bb8cc17bb2efb4','jack','LOGIN','192.168.10.242',1677637989455,'2023-03-01 10:33:09',NULL),('bf00738a36d54b679ee7406cd1fdd137','jack','LOGIN','192.168.10.196',1677640827285,'2023-03-01 11:20:27',NULL),('c0b4f8c9859a4b62943a3d916337a9c9','shaowen','LOGIN','192.168.10.242',1677638949971,'2023-03-01 10:49:09',NULL),('c415341dcc83498fb1646c0e754c54d8','shaowen','LOGIN','192.168.10.242',1677639428530,'2023-03-01 10:57:08',NULL),('c83692ae9b214339ab36ff2792c29331','shaowen','LOGIN','192.168.10.242',1677656893586,'2023-03-01 15:48:13',NULL),('c95b62665f5a46398f84d2df117d60bc','shaowen','LOGIN','192.168.10.242',1677553979604,'2023-02-28 11:12:59',NULL),('cb611b0c4b4a479c9eed83237a07e462','shaowen','LOGIN','192.168.10.242',1677647359834,'2023-03-01 13:09:19',NULL),('cf0f4e01c9cb4dddb0d08c76511eeee2','jack','LOGIN','192.168.10.196',1677569251509,'2023-02-28 15:27:31',NULL),('cfc09bc4ad3b4816ac1d402e0a4851f6','jack','LOGIN','192.168.10.196',1678267007972,'2023-03-08 17:16:47',NULL),('d03a73d899724b97af1cf7bb951c91bb','shaowen','LOGIN','192.168.10.242',1677553908601,'2023-02-28 11:11:48',NULL),('d2a054d61b224ae9ba55b20875510baa','jack','LOGIN','192.168.10.242',1677648620168,'2023-03-01 13:30:20',NULL),('d373d742ff2f417db2c02d4a468caec6','jack','LOGIN','192.168.10.196',1677549658250,'2023-02-28 10:00:58',NULL),('d431e756364641178bd57eff5b063b2d','jack','LOGIN','192.168.10.196',1677569958351,'2023-02-28 15:39:18',NULL),('d7c94fa8c7db4bd1bcfc205497c2143b','shaowen','LOGIN','192.168.10.242',1677650343425,'2023-03-01 13:59:03',NULL),('dc7effeccf7c4400974dd1ca97a3eaf6','shaowen','LOGIN','192.168.10.242',1677656960997,'2023-03-01 15:49:20',NULL),('de60d56b0bdb45a6bbdd7e2d2e3df50c','shaowen','LOGIN','192.168.10.242',1677657763264,'2023-03-01 16:02:43',NULL),('e153990414ae4f0fa0c153626a6b4b2d','jack','LOGIN','192.168.10.196',1677641007933,'2023-03-01 11:23:27',NULL),('e4bdecbad0ec497ebfdea1b7228fc0c8','jack','LOGIN','192.168.10.242',1677636103721,'2023-03-01 10:01:43',NULL),('e4e1ed48c44148ddb6fbdb3edee497f9','shaowen','LOGIN','192.168.10.242',1677550651774,'2023-02-28 10:17:31',NULL),('e552534959d84ffc942769c6d74c29e1','shaowen','LOGIN','192.168.10.242',1677643329508,'2023-03-01 12:02:09',NULL),('e6f4b8c67e6c4d6385359825447b44f5','jack','LOGIN','192.168.10.242',1677580843970,'2023-02-28 18:40:43',NULL),('e760263f90ce4ba09108e4537e852bc8','shaowen','LOGIN','192.168.10.242',1677722941685,'2023-03-02 10:09:01',NULL),('e8422484d5844d4490cbdc48e42f430e','shaowen','LOGIN','192.168.10.242',1677647743361,'2023-03-01 13:15:43',NULL),('e88081678d1f46aa8a7dac725b42aaaf','shaowen','LOGIN','192.168.10.242',1677658072275,'2023-03-01 16:07:52',NULL),('ea7249f2ef81481b9f2a3edb69664f2b','jack','LOGIN','192.168.10.196',1677641257345,'2023-03-01 11:27:37',NULL),('ea92db1fdaa14eafaebf9233f85916c5','shaowen','LOGIN','192.168.10.242',1677647369506,'2023-03-01 13:09:29',NULL),('eac95d9e0783431e98cf8564bade529f','jack','LOGIN','192.168.10.196',1677650301989,'2023-03-01 13:58:21',NULL),('ed130d6ef53b4fc9bcde6b94a8aa18df','jack','LOGIN','192.168.10.196',1678068309953,'2023-03-06 10:05:09',NULL),('ed68406f9b9d445c8b7f2d377d77cbe9','jack','LOGIN','192.168.10.196',1677639669509,'2023-03-01 11:01:09',NULL),('ed9cf148677b4af58c28253801f818ec','shaowen','LOGIN','192.168.10.242',1677549812710,'2023-02-28 10:03:32',NULL),('edc5a3eaff4349fdbef28294fb7010cc','shaowen','LOGIN','192.168.10.242',1677655211376,'2023-03-01 15:20:11',NULL),('ede9c413173b4c3b8aa9eb689ca0bb0c','jack','LOGIN','192.168.10.196',1677639632164,'2023-03-01 11:00:32',NULL),('f0db0f6c38d340118e8cf36305e17bc9','jack','LOGIN','0:0:0:0:0:0:0:1',1677637323025,'2023-03-01 10:22:03',NULL),('f146f0113a7f416db29f917564287df7','jack','LOGIN','192.168.10.196',1677640857589,'2023-03-01 11:20:57',NULL),('f3f597abe9a54e6b963d73ac6f90d4da','shaowen','LOGIN','192.168.10.242',1677639434562,'2023-03-01 10:57:14',NULL),('f84f87d2b9f546d7b95a724c0f8b1c41','jack','LOGIN','192.168.10.242',1677648720150,'2023-03-01 13:32:00',NULL),('fa15988ce2be4179beaf06630e3e1bd9','shaowen','LOGIN','192.168.10.242',1677656774000,'2023-03-01 15:46:14',NULL),('fa4c30ee59524f299ced8a50b3f59038','shaowen','LOGIN','192.168.10.242',1677552741383,'2023-02-28 10:52:21',NULL),('faba1a7b7f5d46588a90f525a946850e','jack','LOGIN','192.168.10.196',1677487563946,'2023-02-27 16:46:03',NULL),('fbdbda277b3947f4b632def3ab146a4d','shaowen','LOGIN','192.168.10.242',1677652482162,'2023-03-01 14:34:42',NULL),('fd5e5b6511c249a5bd19cab0dbddc044','shaowen','LOGIN','192.168.10.242',1677657825719,'2023-03-01 16:03:45',NULL),('fff76a1ca98f48558a5a5a4a6f7fafb2','shaowen','LOGIN','192.168.10.242',1677649161668,'2023-03-01 13:39:21',NULL);
/*!40000 ALTER TABLE `svp_logon-logoff-info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_message`
--

DROP TABLE IF EXISTS `svp_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_message` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `from_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源的uuid',
  `to_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标的uuid',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `remark` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `is_read` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否已读（在未读的状态下才能更新为已读）',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `read_timestamp` bigint DEFAULT NULL COMMENT '已读时间戳',
  `read_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '已读时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`from_uuid`,`create_timestamp`,`remove_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='督察_消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_message`
--

LOCK TABLES `svp_message` WRITE;
/*!40000 ALTER TABLE `svp_message` DISABLE KEYS */;
INSERT INTO `svp_message` VALUES ('344a2c332a69457bb6b4c998d5035729','95205dceefbd4dadbea7eea799312141','ce97133a99304040b4ca9218469a3989','整改通知','测试：请立即整改',NULL,'UN_READ',1677652394557,'2023-03-01 14:33:14',NULL,NULL,NULL);
/*!40000 ALTER TABLE `svp_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_problem`
--

DROP TABLE IF EXISTS `svp_problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_problem` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `number` bigint unsigned NOT NULL COMMENT '编号',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型（0：个人；1：单位）',
  `from_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源的uuid（一定是用户的uuid）',
  `to_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标的uuid（可能是用户的uuid，也可能是单位的uuid，根据type判断）',
  `type_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型的uuid（如果type是个人，那么指向个人类型；如果type是单位，那么指向单位问题）',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `is_read` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否已读（如果是单位问题，只要负责人已读，在未读的状态下就更新为已读）',
  `is_fact` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否属实',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否反馈',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `read_timestamp` bigint DEFAULT NULL COMMENT '已读时间戳（只有未读时才能更新）',
  `read_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '已读时间（只有未读时才能更新）',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`),
  UNIQUE KEY `number_UNIQUE` (`number`),
  KEY `query` (`uuid`,`from_uuid`,`to_uuid`,`type_uuid`,`content`,`type`,`create_timestamp`,`remove_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_问题';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_problem`
--

LOCK TABLES `svp_problem` WRITE;
/*!40000 ALTER TABLE `svp_problem` DISABLE KEYS */;
/*!40000 ALTER TABLE `svp_problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_problem-department-type`
--

DROP TABLE IF EXISTS `svp_problem-department-type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_problem-department-type` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `parent_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级菜单的uuid',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `level` int NOT NULL COMMENT '级别（从1开始计算）',
  `order` tinyint NOT NULL COMMENT '排序编号',
  `order_group` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '排序编号组（%03d为一级，最多20级，所以长度为60，如果级数扩大，则增加对应长度）',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`parent_uuid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_问题-单位-类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_problem-department-type`
--

LOCK TABLES `svp_problem-department-type` WRITE;
/*!40000 ALTER TABLE `svp_problem-department-type` DISABLE KEYS */;
INSERT INTO `svp_problem-department-type` VALUES ('0827aa7026dc446099d4886fe198fe44','78672c7e26a744dea801582e3ae7808e','党风廉政建设',NULL,2,11,'005011',1678346640046,'2023-03-09 15:24:00',NULL),('0f7cb21df5a24687b4d737dabf219c3d','ecb3542b3ef3450486f115b604bf012b','值班岗位脱空岗',NULL,3,2,'002001002',1677856260774,'2023-03-03 23:11:00',NULL),('1085bd0b9d544d0390b06cff13b3062a','78672c7e26a744dea801582e3ae7808e','110接处警工作',NULL,2,8,'005008',1678346620638,'2023-03-09 15:23:40',NULL),('119a6a2dbf4f4dcaa77a7abfb361c6f3','78672c7e26a744dea801582e3ae7808e','重点人管控',NULL,2,7,'005007',1678346614236,'2023-03-09 15:23:34',NULL),('2613d2fbe3704095abff51966bd8c30a','78672c7e26a744dea801582e3ae7808e','文明执法执勤',NULL,2,6,'005006',1678346607429,'2023-03-09 15:23:27',NULL),('28214763e8634b279ca38170f7e7f1de','0','网上督察问题',NULL,1,2,'002',1677856145689,'2023-03-03 23:09:05',NULL),('28271913bebb4cc3a15cb3195c2a42cf','825a856017b44e3b9a7de7713e2919ae','督审支队类问题',NULL,2,1,'001001',1677856187466,'2023-03-03 23:09:47',NULL),('284f4eb4cfa5487cb3311b15aea8bead','78672c7e26a744dea801582e3ae7808e','值班备勤',NULL,2,1,'005001',1678346570408,'2023-03-09 15:22:50',NULL),('2f3eb1d23b314ea0acc7694b6ef025ab','78672c7e26a744dea801582e3ae7808e','会风会纪',NULL,2,4,'005004',1678346594289,'2023-03-09 15:23:14',NULL),('367fd95d37e84f8fa412fa8d5782d002','78672c7e26a744dea801582e3ae7808e','数字证书日常管理和使用',NULL,2,16,'005016',1678346670906,'2023-03-09 15:24:30',NULL),('36a3d09ce189487398419ef5da720b8c','0','个人问题',NULL,1,4,'004',1677856161231,'2023-03-03 23:09:21',NULL),('3856040599884d698c65258be9d33028','28271913bebb4cc3a15cb3195c2a42cf','公务用枪',NULL,3,1,'001001001',1677856197025,'2023-03-03 23:09:57',NULL),('3b4ccdc40fe648ccad9f65dda28a9479','0','外部平台导入问题',NULL,1,3,'003',1677856153783,'2023-03-03 23:09:13',NULL),('3c8c86b57883456ab5601bdd35cc1c69','78672c7e26a744dea801582e3ae7808e','执法监督',NULL,2,13,'005013',1678346652225,'2023-03-09 15:24:12',NULL),('638ed078c1604b8f840a4f88290c81b6','78672c7e26a744dea801582e3ae7808e','处置公民控告申诉',NULL,2,9,'005009',1678346627254,'2023-03-09 15:23:47',NULL),('6563d66d2a574a2b8101ed3356063a92','0','其它问题',NULL,1,6,'006',1677856169473,'2023-03-03 23:09:29',NULL),('78672c7e26a744dea801582e3ae7808e','0','自查问题',NULL,1,5,'005',1678346543312,'2023-03-09 15:22:23',NULL),('825a856017b44e3b9a7de7713e2919ae','0','现场督察问题',NULL,1,1,'001',1677856132140,'2023-03-03 23:08:52',NULL),('839e51c89dee4dd9a62ea1f0b4e4513b','ecb3542b3ef3450486f115b604bf012b','单人询讯问',NULL,3,1,'002001001',1677856250885,'2023-03-03 23:10:50',NULL),('8ef9b1d27afd465da8667350a1611eb7','28271913bebb4cc3a15cb3195c2a42cf','警容风纪',NULL,3,2,'001001002',1677856203377,'2023-03-03 23:10:03',NULL),('981229fde57340f48eb8dfad1e98c5d2','825a856017b44e3b9a7de7713e2919ae','治安支队类问题',NULL,2,2,'001002',1677856220458,'2023-03-03 23:10:20',NULL),('9df1c187cb08445a9c8f31dc225f5ecb','78672c7e26a744dea801582e3ae7808e','一标四实信息管理工作',NULL,2,15,'005015',1678346664989,'2023-03-09 15:24:24',NULL),('9e59397355be463da175e040a60f7278','36a3d09ce189487398419ef5da720b8c','个人作风问题',NULL,2,1,'004001',1677856287787,'2023-03-03 23:11:27',NULL),('9f0e992537e64d36a8596c1eb1738fca','825a856017b44e3b9a7de7713e2919ae','xxx支队类问题',NULL,2,3,'001003',1677856228074,'2023-03-03 23:10:28',NULL),('a829f7c4b7d145e2a8f94f505875eb22','36a3d09ce189487398419ef5da720b8c','个人XXX问题',NULL,2,2,'004002',1677856297172,'2023-03-03 23:11:37',NULL),('a90094481608418c9060da039653e928','78672c7e26a744dea801582e3ae7808e','内部安全管理',NULL,2,12,'005012',1678346645783,'2023-03-09 15:24:05',NULL),('ad37dee24cec49a48600854a9ee623d3','78672c7e26a744dea801582e3ae7808e','社区警务工作',NULL,2,14,'005014',1678346658024,'2023-03-09 15:24:18',NULL),('b90f47ef6c554a38a4d316a4d5132c92','78672c7e26a744dea801582e3ae7808e','办案区管理使用',NULL,2,5,'005005',1678346601208,'2023-03-09 15:23:21',NULL),('ecb3542b3ef3450486f115b604bf012b','28214763e8634b279ca38170f7e7f1de','智慧视频督察问题',NULL,2,1,'002001',1677856239323,'2023-03-03 23:10:39',NULL),('f0996bbcec47411b80030109c8c41d86','78672c7e26a744dea801582e3ae7808e','内务卫生',NULL,2,3,'005003',1678346583881,'2023-03-09 15:23:03',NULL),('f483d19e95654f3487811c21a3721f4f','78672c7e26a744dea801582e3ae7808e','党建工作',NULL,2,10,'005010',1678346634368,'2023-03-09 15:23:54',NULL),('f7ec67916bab40bb8d3a58c519f375ce','78672c7e26a744dea801582e3ae7808e','警容风纪',NULL,2,2,'005002',1678346577378,'2023-03-09 15:22:57',NULL);
/*!40000 ALTER TABLE `svp_problem-department-type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_problem-person-type`
--

DROP TABLE IF EXISTS `svp_problem-person-type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_problem-person-type` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `order` tinyint NOT NULL COMMENT '排序编号',
  `score` tinyint NOT NULL COMMENT '分值',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_问题-个人-类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_problem-person-type`
--

LOCK TABLES `svp_problem-person-type` WRITE;
/*!40000 ALTER TABLE `svp_problem-person-type` DISABLE KEYS */;
INSERT INTO `svp_problem-person-type` VALUES ('03a4e84b0c304ac0bbe0952fe6df8172','行为举止不佳现象',8,1,NULL),('1436e4f62c154d24aa409d99d7ed9978','非因就餐、补餐等吃零食',7,1,NULL),('14e5f89ff78a4e369111ea08abbe885c','着制式服装敞怀、挽袖、卷裤腿',2,1,NULL),('24b8406ca9d34845b7b715c08676d819','警用标志缀饰不齐全等警容风纪问题',4,1,NULL),('43d692f09860429dad947e65fad4e007','行为不雅',9,1,NULL),('5fcc058dfb624357bb7338aec063f9ed','警便混穿',3,1,NULL),('9ae7fba9309f4b28b9e1d73abbf25cc8','警容不严整',1,1,NULL),('d63af67cf299434b9050120b7b943c24','警姿不规范',5,1,NULL),('fe4b10c12c684c6393dec26ccbe57f3c','追逐打闹',6,1,NULL);
/*!40000 ALTER TABLE `svp_problem-person-type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_storage-file`
--

DROP TABLE IF EXISTS `svp_storage-file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_storage-file` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `associate_uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联的uuid',
  `suffix` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '后缀',
  `url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '地址',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`associate_uuid`,`suffix`,`create_timestamp`,`remove_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_存储-文件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_storage-file`
--

LOCK TABLES `svp_storage-file` WRITE;
/*!40000 ALTER TABLE `svp_storage-file` DISABLE KEYS */;
INSERT INTO `svp_storage-file` VALUES ('0324787cc88943c5bdbb150e45a9fee5','6c956b53f98b4dba8fee765049c27f57','png','8c8300c708de471e8689dcd26d24d47e.png',1677647743463,'2023-03-01 13:15:43',NULL),('0a71f901f80d4af6b2b6fc8e0b242107','feb01e6f024e41b4809b1d4055e3f460','png','be57e7ff1d6446da8b9e4fb7f0410299.png',1677657191543,'2023-03-01 15:53:11',NULL),('1388031cf1be4b3487ec420beeb3a5d7','72586ce81f1f4e3f8f1261c6293e4b53','png','432dd08f80d14b11a27c71cd885f9961.png',1677647114014,'2023-03-01 13:05:14',NULL),('15389bb9a5124c31864b30c0bcfe7da6','43d5d9c37f44483aa1e8aa9189647d10','png','5b9326b027b84208a983866f9d6a522e.png',1677724145882,'2023-03-02 10:29:05',NULL),('1583ff65c2e243ffbf6cea5268328bb4','c72bfcb55b10495ebddfcc58fd20895b','png','1c843c02d8304d7d89fce351c994f5cf.png',1677724100511,'2023-03-02 10:28:20',NULL),('2ab9088a37144b58bd157c07eb31f287','cd107ce0e8134db5a147972ea43de29a','png','4976e1f1a7914f1abd6321e6954bfaa8.png',1677724087480,'2023-03-02 10:28:07',NULL),('2e01bd71518d4cf0934820ceac6f2e3d','11b65a630cee4c4ca4058cce66e885ad','png','491d161eb2354ceabc74fcab230ee092.png',1677657134665,'2023-03-01 15:52:14',NULL),('394115dc33ba46149a06f32eda8f88b7','6c453866c8e84817ba604ff2bd5a52d7','png','1d13b738b0b64c179950321b1a74e35a.png',1677649161739,'2023-03-01 13:39:21',NULL),('3c262c4f323c451e90a769502e549340','feb01e6f024e41b4809b1d4055e3f460','png','3d70eb3b50854f24875b7c96f033b2c4.png',1677657191548,'2023-03-01 15:53:11',NULL),('3e7aef900c924732b71d501f83256864','5fb5eca4d81f4f98aafa417492be5a51','png','47ba6bf91512438e8cc99233d6e5347a.png',1677657195990,'2023-03-01 15:53:15',NULL),('47527bc6860043d5b23ea3df4ba9e27a','e0feea54f457484ea091d8a21b94c079','png','9ae9ccbd6b6b479ba99bf08c3ef57318.png',1677648708075,'2023-03-01 13:31:48',NULL),('4da4849ddf3645ef8ae1ff28f2e180a0','11b65a630cee4c4ca4058cce66e885ad','png','89894f8f1d8d42db97d257909e01a253.png',1677657134677,'2023-03-01 15:52:14',NULL),('59067af849fa48e4bb3531f8dcca56ca','05ff2c6cc5704c19b3992408317cf257','png','bb1473409f2f4e20add9147b78a3dc63.png',1677724204827,'2023-03-02 10:30:04',NULL),('5b9f0d2b91db48b095cdd71706d1da70','feb01e6f024e41b4809b1d4055e3f460','png','c58d5d7ab63a4beb87bb318d990182b0.png',1677657191553,'2023-03-01 15:53:11',NULL),('5fee343294184669b2540eff319a46dd','37c09740da5841acab0918d90107a0a7','png','4df7720e50454064a34f2783eb2dffba.png',1677649324682,'2023-03-01 13:42:04',NULL),('620fc1c90cdd485982da184569c6258c','5fb5eca4d81f4f98aafa417492be5a51','jpg','7db05acebe0d4eb49eda01548c222e40.jpg',1677657196005,'2023-03-01 15:53:16',NULL),('6635033375e84bf0aff2626c4a2fbf08','c72bfcb55b10495ebddfcc58fd20895b','png','3098ae363cb54eadad6bad3cb22d0bf3.png',1677724100514,'2023-03-02 10:28:20',NULL),('6ecb78d3ca3744c7aa3737a1ea7500e9','05ff2c6cc5704c19b3992408317cf257','png','c38ffa8958014343b522c171e5a9be6b.png',1677724204830,'2023-03-02 10:30:04',NULL),('7132e7ec1872432797e5b1a8f57a33d4','40d408caed384756a8565f5e2a7098a8','png','73a662d47f48406c9417f08a44cb552d.png',1677657923157,'2023-03-01 16:05:23',NULL),('73c4c162fbfb40fb86a3c01c7eeb2df0','dc1f90b5cf074e0cabda0c759158d306','png','1bf1ac28e3d042bd90b54f9601fc1aa2.png',1677724217131,'2023-03-02 10:30:17',NULL),('80c8ee97d252427d97f053910a7ab336','feb01e6f024e41b4809b1d4055e3f460','jpg','1d7716c3ae474d36b57262394bb4e3e9.jpg',1677657191558,'2023-03-01 15:53:11',NULL),('80dc063dbde24a73ab188585d6bc8dc2','ad8c7e6f7ce44847a82b9d9ffee3decf','png','5cc5484783d447308a982f164f7c8e07.png',1677656955603,'2023-03-01 15:49:15',NULL),('856483a355b842f69e3a9a0a30660b4e','40d408caed384756a8565f5e2a7098a8','jpg','587f8e70979d42fa8ecdb6e81ce0028b.jpg',1677657923172,'2023-03-01 16:05:23',NULL),('85d2ad2e228b442287e066be162d002f','37c09740da5841acab0918d90107a0a7','png','3530c1ac74214eedbe2b8bc740e6c091.png',1677649324685,'2023-03-01 13:42:04',NULL),('883f3909579742749a9676bc4ca8582d','6c453866c8e84817ba604ff2bd5a52d7','png','336f889edf7d4e4186754155b87c4dda.png',1677649161737,'2023-03-01 13:39:21',NULL),('88875cad9b41432391997bfcc59bdc1e','3ffea8f835874141887aa9ff3896f9f9','png','25babf106c6148ef939a94bcb8772885.png',1677647744985,'2023-03-01 13:15:44',NULL),('88a868b1107d44c19036c428007810e4','43d5d9c37f44483aa1e8aa9189647d10','png','97dd126b333e4e4b88b8823fb9e49c66.png',1677724145877,'2023-03-02 10:29:05',NULL),('8b8c09000d3149ce9af23c510db9a49e','dd8223822389464aa1510da0ec1d03a2','png','10eaae7febbf4dc9801cc67642161d9b.png',1677657075446,'2023-03-01 15:51:15',NULL),('92f31b4b69f84500a8988e788aba5a22','1c5a32795bf04a3180f8e6ec8d186af2','png','9f96250e483b44ce89464cb356719055.png',1677647369612,'2023-03-01 13:09:29',NULL),('940c2f035eb64d03bc17286c1661d273','b9a73f206f4d4722b4e34ff9cbfd4e28','jpg','9a91e8b9e9c44d07ba335e125a23e317.jpg',1677658111942,'2023-03-01 16:08:31',NULL),('997f01d7cb844d949ae40f2bf81b3671','bc41cdae0b7c4da6bd42de6b3a484a59','png','7f8936d9d0f94ffa90803650dc281695.png',1677647744313,'2023-03-01 13:15:44',NULL),('99b31f9b9a98425d9efaf70eff8158a3','b9a73f206f4d4722b4e34ff9cbfd4e28','png','489be2006c004e55b56be9e79e41f510.png',1677658111937,'2023-03-01 16:08:31',NULL),('9b523d597d774fe88029492529344eb6','dd8223822389464aa1510da0ec1d03a2','jpg','16015962d72b45929429bcf9893744c3.jpg',1677657075462,'2023-03-01 15:51:15',NULL),('9b785bba8ac84c6f929210d0527468b0','ad8c7e6f7ce44847a82b9d9ffee3decf','png','ed071d1306274e5a8ee304ac9a0149ab.png',1677656955615,'2023-03-01 15:49:15',NULL),('a09fe384c85440b4baaae80918d9574c','c72bfcb55b10495ebddfcc58fd20895b','png','f4739c1c9c104316a8928ea8e0fc28c5.png',1677724100496,'2023-03-02 10:28:20',NULL),('a1f0615155034a6ba325e6b6ac506900','c2d393e35587461ab90c5416206cc40f','png','8db428435c524bf6a7fe31fde61fb098.png',1677643329715,'2023-03-01 12:02:09',NULL),('a215b5ab8502436981738230b4928be0','d2a39635065b4a328e64d1a26292ce41','png','68e8dcaf64634a0186a92abaf416deff.png',1677647358035,'2023-03-01 13:09:18',NULL),('aa523651f591423687ffe911b6a6979d','11b65a630cee4c4ca4058cce66e885ad','png','482bdd063bb345499e13cd4b478048fd.png',1677657134672,'2023-03-01 15:52:14',NULL),('aae4cc8fab304e03a46b19bec11f25e3','43d5d9c37f44483aa1e8aa9189647d10','png','4b5abe04f30242928d530ce3447f2db8.png',1677724145870,'2023-03-02 10:29:05',NULL),('acc2120819b6417790ea84b4bd1b9d99','a8c758fc14d547f0b1635b55caf37897','png','be680c3c97624de5a62818422f204e53.png',1677647742716,'2023-03-01 13:15:42',NULL),('b9d837c3fcab429cb79b48a708f90f56','cd107ce0e8134db5a147972ea43de29a','png','8bbeac1efcb5404aa78aeeec3e9186d3.png',1677724087501,'2023-03-02 10:28:07',NULL),('c08d25870ba54a0996cffa03db6d2309','37c09740da5841acab0918d90107a0a7','jpg','fd894dd29b6d435091653ceaa365c9dc.jpg',1677649324687,'2023-03-01 13:42:04',NULL),('c7e0392a80344d3081a6cca56919ac0b','dd8223822389464aa1510da0ec1d03a2','png','46584b85a42a4a0eb745306c0fc2f448.png',1677657075458,'2023-03-01 15:51:15',NULL),('c965d3a570cd468ea2d6c521c7150c46','dd8223822389464aa1510da0ec1d03a2','png','ad29cd9cffa14d0fbc9fe66b55880441.png',1677657075453,'2023-03-01 15:51:15',NULL),('cdc4539d6844407499b8039bfc6fcc89','cd107ce0e8134db5a147972ea43de29a','png','7a4907ecbd3d40aeb0954823be32edd0.png',1677724083834,'2023-03-02 10:28:03',NULL),('d1d1f8be786740bd8c09a1adcc4e3ad4','6c453866c8e84817ba604ff2bd5a52d7','png','5f21932c30ed425a9817e804c918d115.png',1677649161734,'2023-03-01 13:39:21',NULL),('d3f941c91f404b9c9d3afff5b2bd0b4a','ad8c7e6f7ce44847a82b9d9ffee3decf','jpg','bcb2361688bb436ba78b340ff6aa0912.jpg',1677656955618,'2023-03-01 15:49:15',NULL),('d8175b98f9ff4785ac452b9f269a7db9','b9a73f206f4d4722b4e34ff9cbfd4e28','png','58f5e9e99cbc431db02cb7d0edfbcc1d.png',1677658111934,'2023-03-01 16:08:31',NULL),('da8d03aa7b15442687da6a2a7ed9f04e','dc1f90b5cf074e0cabda0c759158d306','png','ae0af6db7efb4e3897d1847f62323679.png',1677724217125,'2023-03-02 10:30:17',NULL),('dad85b7fba094c69a78c20f9b130ad3e','05ff2c6cc5704c19b3992408317cf257','png','f0c1b2d97e8f41aca5d283f57f476821.png',1677724204833,'2023-03-02 10:30:04',NULL),('dae198030a844049bde72d54be1a8de2','3a12359d8d0549a0b20f2c3fad14ee5a','png','df36b900ebcb49388117df0774a92fee.png',1677642325640,'2023-03-01 11:45:25',NULL),('dc813f69624a4afda255047624311a01','11b65a630cee4c4ca4058cce66e885ad','jpg','321bd23ac0bd4cc6a5c3a9da4641ca19.jpg',1677657134682,'2023-03-01 15:52:14',NULL),('ddaffa14a52546aa915b8e87109d2fb7','b9a73f206f4d4722b4e34ff9cbfd4e28','png','e4571d5e9bc647408b26be4a71ecd949.png',1677658111939,'2023-03-01 16:08:31',NULL),('dfcad805eda347bd9598cef0d6cdedf8','6c453866c8e84817ba604ff2bd5a52d7','jpg','7bfb1af24c304e248e7022f7f613d1b1.jpg',1677649161742,'2023-03-01 13:39:21',NULL),('e164d6ee54dd4ec08820db65bee238bc','2612a0b2289443b1952df7cf38f6d9a2','png','96252f4060ef4eae81744e1151e35edd.png',1677647359955,'2023-03-01 13:09:19',NULL),('e27e7491f9cf49068e7a2b5d6259be89','40d408caed384756a8565f5e2a7098a8','png','681a9a835a094682903414ba9870e44e.png',1677657923167,'2023-03-01 16:05:23',NULL),('e84c5f3ca754412190115a5a49e632fe','37c09740da5841acab0918d90107a0a7','png','366e1d128bf64267abe5cb8b2883ce2e.png',1677649324678,'2023-03-01 13:42:04',NULL),('e860fd6d677446debe65b235c1fb0911','5fb5eca4d81f4f98aafa417492be5a51','png','b3dca0dde2ba4cd6b338962730a09033.png',1677657196002,'2023-03-01 15:53:16',NULL),('eebe35640f5d4dd98489c29b4bf2cc70','ad8c7e6f7ce44847a82b9d9ffee3decf','png','978a04c6b05445059a57e6e6f6d49bf7.png',1677656955611,'2023-03-01 15:49:15',NULL),('f3dd78f3ce744f9d844ac765965edea3','40d408caed384756a8565f5e2a7098a8','png','6f88e88f87824f848382ca2b58d6b104.png',1677657923162,'2023-03-01 16:05:23',NULL),('f50ecd5015e9468e8cb2d87ce11d22fa','dc1f90b5cf074e0cabda0c759158d306','png','f23d2628ba634434b704421d9d31c98c.png',1677724217137,'2023-03-02 10:30:17',NULL),('f85926cc6f224b5f8195ab4aceff1272','728c91344be24349ae7e34f0087989b0','png','571574f7022244c7816e16d9208b967b.png',1677648765733,'2023-03-01 13:32:45',NULL),('f9b67d39c4e24d94bc8674f32feaada5','5fb5eca4d81f4f98aafa417492be5a51','png','37c34cad2a4147d58536a52bbb512bdd.png',1677657195997,'2023-03-01 15:53:15',NULL);
/*!40000 ALTER TABLE `svp_storage-file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_storage-file-delete-fail-record`
--

DROP TABLE IF EXISTS `svp_storage-file-delete-fail-record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_storage-file-delete-fail-record` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '地址',
  `create_timestamp` bigint NOT NULL COMMENT '创建时间戳',
  `create_datetime` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建时间',
  `remove_timestamp` bigint DEFAULT NULL COMMENT '删除时间戳',
  PRIMARY KEY (`uuid`),
  KEY `query` (`uuid`,`create_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_存储-文件-删除-失败-记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_storage-file-delete-fail-record`
--

LOCK TABLES `svp_storage-file-delete-fail-record` WRITE;
/*!40000 ALTER TABLE `svp_storage-file-delete-fail-record` DISABLE KEYS */;
/*!40000 ALTER TABLE `svp_storage-file-delete-fail-record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `svp_system-config`
--

DROP TABLE IF EXISTS `svp_system-config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `svp_system-config` (
  `uuid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一标识',
  `person_problem_trigger_report_count` tinyint NOT NULL COMMENT '个人问题触发报告次数',
  `person_init_score` int NOT NULL COMMENT '个人初始分值',
  `problem_filed_days` tinyint NOT NULL COMMENT '问题归档时间（单位：天）',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='现场督察_系统-配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `svp_system-config`
--

LOCK TABLES `svp_system-config` WRITE;
/*!40000 ALTER TABLE `svp_system-config` DISABLE KEYS */;
INSERT INTO `svp_system-config` VALUES ('b150c5428ba646b89dc1c1656959edf1',2,10,7);
/*!40000 ALTER TABLE `svp_system-config` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-03-15 20:25:58
