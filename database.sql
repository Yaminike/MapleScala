CREATE DATABASE  IF NOT EXISTS `MapleScala` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `MapleScala`;
-- MySQL dump 10.13  Distrib 5.6.22, for osx10.8 (x86_64)
--
-- Host: 127.0.0.1    Database: MapleScala
-- ------------------------------------------------------
-- Server version	5.6.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(150) NOT NULL,
  `isGM` bit(1) NOT NULL,
  `pin` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Admin','$pbkdf2-sha512$20000$r58eHRueKC.voGHqaYduyi4jcOPIN0AW$XEdOCHYphlZ.ewxmr8Mtz94TEOkiO8a0imL4i6G1D28',1,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `characters`
--

DROP TABLE IF EXISTS `characters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `characters` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(10) unsigned NOT NULL,
  `name` varchar(13) NOT NULL,
  `world` tinyint(4) NOT NULL,
  `gender` bit(1) NOT NULL,
  `skinColor` tinyint(4) NOT NULL,
  `face` int(11) NOT NULL,
  `hair` int(11) NOT NULL,
  `level` tinyint(4) NOT NULL,
  `job` smallint(6) NOT NULL,
  `str` smallint(6) NOT NULL,
  `dex` smallint(6) NOT NULL,
  `int` smallint(6) NOT NULL,
  `luk` smallint(6) NOT NULL,
  `hp` smallint(6) NOT NULL,
  `maxHp` smallint(6) NOT NULL,
  `mp` smallint(6) NOT NULL,
  `maxMp` smallint(6) NOT NULL,
  `ap` smallint(6) NOT NULL,
  `sp` smallint(6) NOT NULL,
  `exp` int(11) NOT NULL,
  `fame` smallint(6) NOT NULL,
  `gachaExp` int(11) NOT NULL,
  `map` int(11) NOT NULL,
  `spawnpoint` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `userId_idx` (`userId`),
  CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `characterId` int(10) unsigned NOT NULL,
  `itemId` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `position` tinyint(4) NOT NULL,
  `amount` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `character_idx` (`characterId`),
  CONSTRAINT `character` FOREIGN KEY (`characterId`) REFERENCES `characters` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itemstats`
--

DROP TABLE IF EXISTS `itemstats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `itemstats` (
  `itemId` bigint(20) unsigned NOT NULL,
  `str` smallint(6) NOT NULL,
  `dex` smallint(6) NOT NULL,
  `int` smallint(6) NOT NULL,
  `luk` smallint(6) NOT NULL,
  `wAtt` smallint(6) NOT NULL,
  `wDef` smallint(6) NOT NULL,
  `mAtt` smallint(6) NOT NULL,
  `mDef` smallint(6) NOT NULL,
  `acc` smallint(6) NOT NULL,
  `eva` smallint(6) NOT NULL,
  `speed` smallint(6) NOT NULL,
  `jump` smallint(6) NOT NULL,
  `hp` smallint(6) NOT NULL,
  `mp` smallint(6) NOT NULL,
  `slots` tinyint(4) NOT NULL,
  `flags` tinyint(4) NOT NULL,
  PRIMARY KEY (`itemId`),
  CONSTRAINT `item` FOREIGN KEY (`itemId`) REFERENCES `items` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
