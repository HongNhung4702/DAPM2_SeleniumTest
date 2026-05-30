-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: bookingfootball
-- ------------------------------------------------------
-- Server version	8.0.44

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

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `stadium_id` bigint NOT NULL,
  `booking_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` enum('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `stadium_id` (`stadium_id`),
  CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`stadium_id`) REFERENCES `stadium` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES (11,5,1,'2025-06-24','14:00:00','17:00:00','REJECTED','2025-06-23 16:44:03'),(12,5,3,'2025-06-25','19:00:00','21:00:00','CANCELLED','2025-06-23 16:44:41'),(13,5,7,'2025-06-24','19:00:00','20:00:00','APPROVED','2025-06-23 16:45:24'),(14,5,7,'2025-11-30','09:00:00','11:00:00','REJECTED','2025-11-30 07:14:14');
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stadium`
--

DROP TABLE IF EXISTS `stadium`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stadium` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) NOT NULL,
  `area` enum('Quy Nhơn','An Lão','Hoài Ân','Hoài Nhơn','Phù Cát','Phù Mỹ','Tây Sơn','Tuy Phước','Vân Canh','Vĩnh Thạnh','An Nhơn') NOT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(15) DEFAULT NULL,
  `field_type` enum('Sân 5','Sân 7','Sân 11') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stadium`
--

LOCK TABLES `stadium` WRITE;
/*!40000 ALTER TABLE `stadium` DISABLE KEYS */;
INSERT INTO `stadium` VALUES (1,'Sân số 1','123 Lê Lợi, TP. Quy Nhơn','Quy Nhơn',150000.00,'Sân bóng hiện đại, 5 người, có mái che nắng','/images/stadiums/stadium_1764482770438.jfif','0901234567','Sân 5','2025-05-23 07:54:00',1),(2,'Sân số 2','123 Lê Lợi, TP. Quy Nhơn','Phù Cát',200000.00,'Sân bóng 7 người, có nhân tạo chất lượng','/images/stadiums/stadium_1764482752087.jfif','0912345678','Sân 7','2025-05-23 07:54:00',1),(3,'Sân số 3','123 Lê Lợi, TP. Quy Nhơn','Tây Sơn',250000.00,'Sân bóng 11 người, cỏ nhân tạo chất lượng cao, có khán đài cổ vũ','/images/stadiums/stadium_1764482738808.jfif','0923456789','Sân 11','2025-05-23 07:54:00',1),(4,'Sân số 4','123 Lê Lợi, TP. Quy Nhơn','Hoài Nhơn',220000.00,'Sân bóng 5 người, có đèn chiếu sáng xịn, có mái che, cỏ nhân tạo chất lượng','/images/stadiums/stadium_1764482716844.jfif','0934567890','Sân 5','2025-05-23 07:54:00',1),(5,'Sân An Nhơn','654 Hùng Vương, Thị xã An Nhơn','An Nhơn',180000.00,'Sân bóng 7 người, không gian thoáng mát','/images/stadiums/stadium_1750442440823.jfif','0945678901','Sân 7','2025-05-23 07:54:00',0),(6,'Sân số 5','123 Lê Lợi, TP. Quy Nhơn','Quy Nhơn',200000.00,'có trà đá miễn phí, mái che','/images/stadiums/stadium_1764482708278.jfif',NULL,'Sân 7','2025-06-21 09:46:30',1),(7,'Sân số 6','123 Lê Lợi, TP. Quy Nhơn','Quy Nhơn',140000.00,'sân cỏ tự nhiên, có ghế cổ vũ, mái che, đèn chiếu sáng xịn','/images/stadiums/stadium_1764482698744.jfif',NULL,'Sân 11','2025-06-22 10:18:43',1),(8,'Sân Lê Lợi 1','ffug','Quy Nhơn',150000.00,'ẻtgewg','/images/stadiums/stadium_1750671300718.jpg',NULL,'Sân 7','2025-06-23 09:35:01',0),(9,'Sân Lê Lợi 1','123 Lê Lợi, TP. Đà Nẵng','Quy Nhơn',500000.00,'yegfb','/images/stadiums/stadium_1750671516462.jpg',NULL,'Sân 11','2025-06-23 09:38:36',0),(10,'Sân Lê Lợi 2','123 Lê Lợi 2, TP. Quy Nhơn','Quy Nhơn',123000.00,'fdsf','/images/stadiums/stadium_1750671571423.jpg',NULL,'Sân 5','2025-06-23 09:39:31',0);
/*!40000 ALTER TABLE `stadium` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `role` enum('USER','ADMIN') DEFAULT 'USER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (4,'admin','admin123','Admin User','admin@example.com','0123456789','QuyNhon','ADMIN','2025-06-23 08:03:27'),(5,'User','User123','Hồng Nhung','nhung@gmail.com','0382157155','123 Hoa Lư, TP Quy Nhơn','USER','2025-06-23 16:38:41');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-30 14:53:22
