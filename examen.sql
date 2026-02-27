CREATE DATABASE  IF NOT EXISTS `examen` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `examen`;
-- MySQL dump 10.13  Distrib 8.0.45, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: examen
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'ce3a8acc-0e08-11f1-bf46-6a5d91c31c41:1-117';

--
-- Table structure for table `attempt_answers`
--

DROP TABLE IF EXISTS `attempt_answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attempt_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempt_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `selected_option_id` bigint DEFAULT NULL,
  `correct_option_id` bigint NOT NULL,
  `is_correct` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_answer_attempt_id` (`attempt_id`),
  KEY `idx_answer_question_id` (`question_id`),
  CONSTRAINT `fk_answer_attempt` FOREIGN KEY (`attempt_id`) REFERENCES `exam_attempts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attempt_answers`
--

LOCK TABLES `attempt_answers` WRITE;
/*!40000 ALTER TABLE `attempt_answers` DISABLE KEYS */;
INSERT INTO `attempt_answers` VALUES (1,1,4,NULL,14,0),(2,1,2,NULL,7,0),(3,2,3,NULL,10,0),(4,2,4,NULL,14,0),(5,3,3,NULL,10,0),(6,3,4,NULL,14,0),(7,4,1,NULL,2,0),(8,5,4,NULL,14,0),(9,5,2,NULL,7,0),(10,5,3,NULL,10,0),(11,5,1,NULL,2,0),(12,5,5,NULL,17,0),(13,6,4,NULL,14,0),(14,6,1,NULL,2,0),(15,6,3,NULL,10,0),(16,6,5,NULL,17,0),(17,6,2,NULL,7,0),(18,7,1,2,2,1),(19,7,4,13,14,0),(20,7,2,7,7,1),(21,8,4,13,14,0),(22,8,1,3,2,0),(23,8,2,7,7,1),(24,8,5,17,17,1),(25,8,3,10,10,1),(26,9,4,NULL,14,0),(27,9,1,NULL,2,0),(28,9,3,NULL,10,0),(29,9,2,NULL,7,0),(30,9,5,NULL,17,0),(31,10,3,NULL,10,0),(32,10,5,NULL,17,0),(33,10,2,NULL,7,0),(34,10,1,NULL,2,0),(35,10,4,NULL,14,0),(36,11,6,23,23,1),(37,11,3,10,10,1),(38,11,1,2,2,1),(39,11,4,13,14,0),(40,11,2,7,7,1),(41,11,5,17,17,1),(42,12,1,2,2,1),(43,12,6,23,23,1),(44,13,7,26,26,1),(45,13,8,29,29,1);
/*!40000 ALTER TABLE `attempt_answers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Matemática',1),(2,'Web',1),(3,'Java',1),(4,'Estructuras',1),(5,'Reporte Regulatorios',1);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_attempts`
--

DROP TABLE IF EXISTS `exam_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_attempts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `total_questions` int NOT NULL,
  `time_limit_minutes` int NOT NULL,
  `started_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `finished_at` datetime DEFAULT NULL,
  `score` double DEFAULT NULL,
  `correct_count` int DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'IN_PROGRESS',
  PRIMARY KEY (`id`),
  KEY `idx_attempt_user_id` (`user_id`),
  KEY `idx_attempt_status` (`status`),
  CONSTRAINT `fk_attempt_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_attempts`
--

LOCK TABLES `exam_attempts` WRITE;
/*!40000 ALTER TABLE `exam_attempts` DISABLE KEYS */;
INSERT INTO `exam_attempts` VALUES (1,1,2,10,'2026-02-26 23:43:53','2026-02-27 00:07:41',NULL,NULL,'CANCELED'),(2,1,2,1,'2026-02-26 23:44:27','2026-02-27 00:07:43',NULL,NULL,'CANCELED'),(3,1,2,10,'2026-02-26 23:46:15','2026-02-27 00:07:46',NULL,NULL,'CANCELED'),(4,2,1,10,'2026-02-26 23:46:36','2026-02-27 00:07:48',NULL,NULL,'CANCELED'),(5,1,5,10,'2026-02-26 23:49:45','2026-02-27 00:07:50',NULL,NULL,'CANCELED'),(6,1,5,10,'2026-02-26 23:51:46','2026-02-27 00:07:52',NULL,NULL,'CANCELED'),(7,2,3,10,'2026-02-26 23:52:28','2026-02-26 23:52:41',13.33,2,'FINISHED'),(8,1,5,10,'2026-02-26 23:53:06','2026-02-26 23:53:16',12,3,'FINISHED'),(9,1,5,10,'2026-02-26 23:53:28','2026-02-26 23:53:34',0,0,'FINISHED'),(10,1,5,1,'2026-02-26 23:53:44','2026-02-26 23:54:44',0,0,'FINISHED'),(11,1,6,10,'2026-02-26 23:59:16','2026-02-26 23:59:31',16.67,5,'FINISHED'),(12,3,2,10,'2026-02-27 00:03:11','2026-02-27 00:03:15',20,2,'FINISHED'),(13,3,2,2,'2026-02-27 00:38:26','2026-02-27 00:38:34',20,2,'FINISHED');
/*!40000 ALTER TABLE `exam_attempts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_options`
--

DROP TABLE IF EXISTS `question_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_options` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL,
  `option_text` tinytext COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_correct` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_option_question_id` (`question_id`),
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_options`
--

LOCK TABLES `question_options` WRITE;
/*!40000 ALTER TABLE `question_options` DISABLE KEYS */;
INSERT INTO `question_options` VALUES (1,1,'10',0),(2,1,'12',1),(3,1,'13',0),(4,1,'14',0),(5,2,'200',0),(6,2,'301',0),(7,2,'404',1),(8,2,'500',0),(9,3,'implements',0),(10,3,'extends',1),(11,3,'inherit',0),(12,3,'super',0),(13,4,'ArrayList',0),(14,4,'HashSet',1),(15,4,'LinkedList',0),(16,4,'Vector',0),(17,5,'7',1),(18,5,'9',0),(19,5,'8',0),(20,5,'5',0),(21,6,'No sé',0),(22,6,'Si sé',0),(23,6,'usando un gestor de datos como SSMS o MySQL',1),(24,6,'la C',0),(25,7,'Sí',0),(26,7,'No',1),(27,7,'No es necesario',0),(28,7,'Si es necesario',0),(29,8,'Código de actividad',1),(30,8,'La A',0),(31,8,'La A',0),(32,8,'No sé',0);
/*!40000 ALTER TABLE `question_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `statement` tinytext COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `difficulty` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EASY',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'¿Cuál es el resultado de 7 + 5?','Matemática','EASY',1,'2026-02-26 23:38:16'),(2,'¿Qué HTTP status code significa \'Not Found\'?','Web','EASY',1,'2026-02-26 23:38:16'),(3,'En Java, ¿qué palabra reservada se usa para heredar de una clase?','Java','EASY',1,'2026-02-26 23:38:16'),(4,'¿Cuál estructura NO permite duplicados?','Estructuras','MEDIUM',1,'2026-02-26 23:38:16'),(5,'Salida de: System.out.println(3 * 2 + 1)?','Java','EASY',1,'2026-02-26 23:38:16'),(6,'¿Como se debería de ingresar a una bae de datos?','Tecnologia','EASY',1,'2026-02-26 23:58:51'),(7,'¿Se pueden crear nuevos reportes que no sean de la SBS?','Reporte Regulatorios','EASY',1,'2026-02-27 00:37:35'),(8,'¿Que significa el código CIIU?','Reporte Regulatorios','EASY',1,'2026-02-27 00:38:07');
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$mc8Y.IcAd99IHiVnzrYS1.Kouu0F0jk5PRLR16j57WM7unr8vhpg.','ADMIN',1,'2026-02-26 23:38:16'),(2,'user','$2a$10$qfhHM8yDnT8jI7JQ.rxMLupvVW/GOj16fmZ3Qc/beP1hoQKmXnWDC','USER',1,'2026-02-26 23:38:16'),(3,'PykeVD','$2a$10$S3eAGVG16oJzo6HfWB/Fneshb8IjA4r6At1hxRY/MJUFFTSBv4Sp2','ADMIN',1,'2026-02-27 00:02:17');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-27  0:42:33
