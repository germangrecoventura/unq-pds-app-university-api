-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: localhost    Database: app-universidad
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `admin_university`
--

DROP TABLE IF EXISTS `admin_university`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_university` (
  `id` bigint NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_bfou1wt67yddqelrro9d1oxnr` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_university`
--

LOCK TABLES `admin_university` WRITE;
/*!40000 ALTER TABLE `admin_university` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_university` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `branch`
--

DROP TABLE IF EXISTS `branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch`
--

LOCK TABLES `branch` WRITE;
/*!40000 ALTER TABLE `branch` DISABLE KEYS */;
/*!40000 ALTER TABLE `branch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_teacher`
--

DROP TABLE IF EXISTS `comment_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) NOT NULL,
  `date` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_teacher`
--

LOCK TABLES `comment_teacher` WRITE;
/*!40000 ALTER TABLE `comment_teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission`
--

DROP TABLE IF EXISTS `commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `four_month_period` enum('FIRST_PERIOD','SECOND_PERIOD') NOT NULL,
  `year` int NOT NULL,
  `matter_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9flbacipapfehpmbyclkide22` (`matter_id`),
  CONSTRAINT `FK9flbacipapfehpmbyclkide22` FOREIGN KEY (`matter_id`) REFERENCES `matter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission`
--

LOCK TABLES `commission` WRITE;
/*!40000 ALTER TABLE `commission` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission_groups_students`
--

DROP TABLE IF EXISTS `commission_groups_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission_groups_students` (
  `commission_id` bigint NOT NULL,
  `groups_students_id` bigint NOT NULL,
  PRIMARY KEY (`commission_id`,`groups_students_id`),
  KEY `FK1vdm9waet1wmgoy0y35r9nap9` (`groups_students_id`),
  CONSTRAINT `FK1vdm9waet1wmgoy0y35r9nap9` FOREIGN KEY (`groups_students_id`) REFERENCES `group_app` (`id`),
  CONSTRAINT `FKdirrjlglas31pom6tv24fpkl9` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_groups_students`
--

LOCK TABLES `commission_groups_students` WRITE;
/*!40000 ALTER TABLE `commission_groups_students` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission_groups_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission_students`
--

DROP TABLE IF EXISTS `commission_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission_students` (
  `commission_id` bigint NOT NULL,
  `students_id` bigint NOT NULL,
  PRIMARY KEY (`commission_id`,`students_id`),
  KEY `FKtly1vi7enb8api5u6osdcrpfn` (`students_id`),
  CONSTRAINT `FKgb48iggivfwnkr1vmud9md1re` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`),
  CONSTRAINT `FKtly1vi7enb8api5u6osdcrpfn` FOREIGN KEY (`students_id`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_students`
--

LOCK TABLES `commission_students` WRITE;
/*!40000 ALTER TABLE `commission_students` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission_teachers`
--

DROP TABLE IF EXISTS `commission_teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission_teachers` (
  `commission_id` bigint NOT NULL,
  `teachers_id` bigint NOT NULL,
  PRIMARY KEY (`commission_id`,`teachers_id`),
  KEY `FK9ft7yisv3wfcaxk4s67wn15ys` (`teachers_id`),
  CONSTRAINT `FK9ft7yisv3wfcaxk4s67wn15ys` FOREIGN KEY (`teachers_id`) REFERENCES `teacher` (`id`),
  CONSTRAINT `FKirjhgh5copxxdt2lq1hx1i4nk` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission_teachers`
--

LOCK TABLES `commission_teachers` WRITE;
/*!40000 ALTER TABLE `commission_teachers` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission_teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commit`
--

DROP TABLE IF EXISTS `commit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commit` (
  `node_id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commit`
--

LOCK TABLES `commit` WRITE;
/*!40000 ALTER TABLE `commit` DISABLE KEYS */;
/*!40000 ALTER TABLE `commit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_app`
--

DROP TABLE IF EXISTS `group_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_app` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_app`
--

LOCK TABLES `group_app` WRITE;
/*!40000 ALTER TABLE `group_app` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_app` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_app_members`
--

DROP TABLE IF EXISTS `group_app_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_app_members` (
  `group_id` bigint NOT NULL,
  `members_id` bigint NOT NULL,
  PRIMARY KEY (`group_id`,`members_id`),
  KEY `FK4xb1nu0064ci6v1vp4hb39tc3` (`members_id`),
  CONSTRAINT `FK4xb1nu0064ci6v1vp4hb39tc3` FOREIGN KEY (`members_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKj3agvopb84s1c2a88vwf3jyay` FOREIGN KEY (`group_id`) REFERENCES `group_app` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_app_members`
--

LOCK TABLES `group_app_members` WRITE;
/*!40000 ALTER TABLE `group_app_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_app_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_app_projects`
--

DROP TABLE IF EXISTS `group_app_projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_app_projects` (
  `group_id` bigint NOT NULL,
  `projects_id` bigint NOT NULL,
  PRIMARY KEY (`group_id`,`projects_id`),
  UNIQUE KEY `UK_jw2du0ctmad4k1w5r27nqw0po` (`projects_id`),
  CONSTRAINT `FKexre4nwk5i2o4jadibwubt150` FOREIGN KEY (`projects_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FKq8fj4cl8jhke2n9vtowevx42k` FOREIGN KEY (`group_id`) REFERENCES `group_app` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_app_projects`
--

LOCK TABLES `group_app_projects` WRITE;
/*!40000 ALTER TABLE `group_app_projects` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_app_projects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (6);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `issue`
--

DROP TABLE IF EXISTS `issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issue` (
  `id` int NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issue`
--

LOCK TABLES `issue` WRITE;
/*!40000 ALTER TABLE `issue` DISABLE KEYS */;
/*!40000 ALTER TABLE `issue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matter`
--

DROP TABLE IF EXISTS `matter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2h3v3va436kxeu6vkm1dqdolj` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matter`
--

LOCK TABLES `matter` WRITE;
/*!40000 ALTER TABLE `matter` DISABLE KEYS */;
/*!40000 ALTER TABLE `matter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `owner_github` varchar(255) DEFAULT NULL,
  `token_github` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_repositories`
--

DROP TABLE IF EXISTS `project_repositories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_repositories` (
  `project_id` bigint NOT NULL,
  `repositories_id` bigint NOT NULL,
  PRIMARY KEY (`project_id`,`repositories_id`),
  UNIQUE KEY `UK_g3klxl2p1ycdq1hq1mkk4l2wn` (`repositories_id`),
  CONSTRAINT `FKdbpeqd3a1qlsrj4wm490pk8h` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FKrh8uh0xi975tkargceji6i0pb` FOREIGN KEY (`repositories_id`) REFERENCES `repository` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_repositories`
--

LOCK TABLES `project_repositories` WRITE;
/*!40000 ALTER TABLE `project_repositories` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_repositories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pulls`
--

DROP TABLE IF EXISTS `pulls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pulls` (
  `id` int NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pulls`
--

LOCK TABLES `pulls` WRITE;
/*!40000 ALTER TABLE `pulls` DISABLE KEYS */;
/*!40000 ALTER TABLE `pulls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository` (
  `id` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository`
--

LOCK TABLES `repository` WRITE;
/*!40000 ALTER TABLE `repository` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository_branches`
--

DROP TABLE IF EXISTS `repository_branches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository_branches` (
  `repository_id` bigint NOT NULL,
  `branches_id` bigint NOT NULL,
  UNIQUE KEY `UK_mkma2g7m2b85ifj52cvc5smbw` (`branches_id`),
  KEY `FK9eeu5ii2bl16j4xfqxvpud853` (`repository_id`),
  CONSTRAINT `FK54fcebjrtexohh16dknb8pbjb` FOREIGN KEY (`branches_id`) REFERENCES `branch` (`id`),
  CONSTRAINT `FK9eeu5ii2bl16j4xfqxvpud853` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository_branches`
--

LOCK TABLES `repository_branches` WRITE;
/*!40000 ALTER TABLE `repository_branches` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository_branches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository_comments_teacher`
--

DROP TABLE IF EXISTS `repository_comments_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository_comments_teacher` (
  `repository_id` bigint NOT NULL,
  `comments_teacher_id` bigint NOT NULL,
  UNIQUE KEY `UK_54chawujylkw7v6nms0dlul6e` (`comments_teacher_id`),
  KEY `FKkgh1re3pugwv2jh1l3hqecojg` (`repository_id`),
  CONSTRAINT `FK6i8j0lbhtbptpv8dmwcuq5khx` FOREIGN KEY (`comments_teacher_id`) REFERENCES `comment_teacher` (`id`),
  CONSTRAINT `FKkgh1re3pugwv2jh1l3hqecojg` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository_comments_teacher`
--

LOCK TABLES `repository_comments_teacher` WRITE;
/*!40000 ALTER TABLE `repository_comments_teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository_comments_teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository_commits`
--

DROP TABLE IF EXISTS `repository_commits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository_commits` (
  `repository_id` bigint NOT NULL,
  `commits_node_id` varchar(255) NOT NULL,
  UNIQUE KEY `UK_cy0kwmvv1noar13wqx6bllk2` (`commits_node_id`),
  KEY `FK7hjfhgobb9mxdf41ai8sxq6od` (`repository_id`),
  CONSTRAINT `FK7hjfhgobb9mxdf41ai8sxq6od` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`),
  CONSTRAINT `FKjjtk50oqtk5eujngo4gqv5ks4` FOREIGN KEY (`commits_node_id`) REFERENCES `commit` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository_commits`
--

LOCK TABLES `repository_commits` WRITE;
/*!40000 ALTER TABLE `repository_commits` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository_commits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository_issues`
--

DROP TABLE IF EXISTS `repository_issues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository_issues` (
  `repository_id` bigint NOT NULL,
  `issues_id` int NOT NULL,
  UNIQUE KEY `UK_1rjvkrxirn3l7niutwbvboblb` (`issues_id`),
  KEY `FKgj2pxrgpwuadsehffp5cmldaf` (`repository_id`),
  CONSTRAINT `FK82oram1awqaxcdtth15rn825s` FOREIGN KEY (`issues_id`) REFERENCES `issue` (`id`),
  CONSTRAINT `FKgj2pxrgpwuadsehffp5cmldaf` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository_issues`
--

LOCK TABLES `repository_issues` WRITE;
/*!40000 ALTER TABLE `repository_issues` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository_issues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository_pull_requests`
--

DROP TABLE IF EXISTS `repository_pull_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository_pull_requests` (
  `repository_id` bigint NOT NULL,
  `pull_requests_id` int NOT NULL,
  UNIQUE KEY `UK_7qsffddkao973cpqxxbnq25a4` (`pull_requests_id`),
  KEY `FKn97grx0wur2ayyxq1xmxsvw2q` (`repository_id`),
  CONSTRAINT `FKn97grx0wur2ayyxq1xmxsvw2q` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`),
  CONSTRAINT `FKoiqi5mm6ctl3hfm41i3wserey` FOREIGN KEY (`pull_requests_id`) REFERENCES `pulls` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository_pull_requests`
--

LOCK TABLES `repository_pull_requests` WRITE;
/*!40000 ALTER TABLE `repository_pull_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository_pull_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repository_tags`
--

DROP TABLE IF EXISTS `repository_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repository_tags` (
  `repository_id` bigint NOT NULL,
  `tags_node_id` varchar(255) NOT NULL,
  UNIQUE KEY `UK_88f0dqv9ljx7tna2trv5a1s8r` (`tags_node_id`),
  KEY `FKmiq45hhrgdk2dq6g8f973f1ht` (`repository_id`),
  CONSTRAINT `FKmiq45hhrgdk2dq6g8f973f1ht` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`),
  CONSTRAINT `FKqi3npqnx6tr9h1n49qpkjwkh9` FOREIGN KEY (`tags_node_id`) REFERENCES `tag` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repository_tags`
--

LOCK TABLES `repository_tags` WRITE;
/*!40000 ALTER TABLE `repository_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `repository_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` bigint NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fe0i52si7ybu0wjedj6motiim` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `node_id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tar_url` varchar(255) DEFAULT NULL,
  `zip_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_3kv6k1e64a9gylvkn3gnghc2q` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-24 21:16:49
