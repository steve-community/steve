CREATE DATABASE  IF NOT EXISTS `stevedb` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `stevedb`;
-- MySQL dump 10.13  Distrib 5.6.13, for osx10.7 (x86_64)
--
-- Host: localhost    Database: stevedb
-- ------------------------------------------------------
-- Server version	5.5.32-0ubuntu0.13.04.1

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
-- Table structure for table `chargebox`
--

DROP TABLE IF EXISTS `chargebox`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chargebox` (
  `chargeBoxId` varchar(30) NOT NULL,
  `endpoint_address` varchar(45) DEFAULT NULL,
  `ocppVersion` varchar(3) DEFAULT NULL,
  `chargePointVendor` varchar(20) DEFAULT NULL,
  `chargePointModel` varchar(20) DEFAULT NULL,
  `chargePointSerialNumber` varchar(25) DEFAULT NULL,
  `chargeBoxSerialNumber` varchar(25) DEFAULT NULL,
  `fwVersion` varchar(20) DEFAULT NULL,
  `fwUpdateStatus` varchar(25) DEFAULT NULL,
  `fwUpdateTimestamp` timestamp NULL DEFAULT NULL,
  `iccid` varchar(20) DEFAULT NULL,
  `imsi` varchar(20) DEFAULT NULL,
  `meterType` varchar(25) DEFAULT NULL,
  `meterSerialNumber` varchar(25) DEFAULT NULL,
  `diagnosticsStatus` varchar(20) DEFAULT NULL,
  `diagnosticsTimestamp` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`chargeBoxId`),
  UNIQUE KEY `chargeBoxId_UNIQUE` (`chargeBoxId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `connector`
--

DROP TABLE IF EXISTS `connector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `connector` (
  `connector_pk` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `chargeBoxId` varchar(30) NOT NULL,
  `connectorId` int(11) NOT NULL,
  PRIMARY KEY (`connector_pk`),
  UNIQUE KEY `connector_pk_UNIQUE` (`connector_pk`),
  UNIQUE KEY `connector_cbid_cid_UNIQUE` (`chargeBoxId`,`connectorId`),
  CONSTRAINT `FK_chargeBoxId_c` FOREIGN KEY (`chargeBoxId`) REFERENCES `chargebox` (`chargeBoxId`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `connector_metervalue`
--

DROP TABLE IF EXISTS `connector_metervalue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `connector_metervalue` (
  `connector_pk` int(11) unsigned NOT NULL,
  `transaction_pk` int(10) unsigned DEFAULT NULL,
  `valueTimestamp` timestamp NULL DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
  `readingContext` varchar(20) DEFAULT NULL,
  `format` varchar(20) DEFAULT NULL,
  `measurand` varchar(40) DEFAULT NULL,
  `location` varchar(10) DEFAULT NULL,
  `unit` varchar(10) DEFAULT NULL,
  KEY `FK_cm_pk_idx` (`connector_pk`),
  KEY `FK_tid_cm_idx` (`transaction_pk`),
  CONSTRAINT `FK_pk_cm` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_tid_cm` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction` (`transaction_pk`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `connector_status`
--

DROP TABLE IF EXISTS `connector_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `connector_status` (
  `connector_pk` int(11) unsigned NOT NULL,
  `statusTimestamp` timestamp NULL DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `errorCode` varchar(25) DEFAULT NULL,
  `errorInfo` varchar(50) DEFAULT NULL,
  `vendorId` varchar(255) DEFAULT NULL,
  `vendorErrorCode` varchar(50) DEFAULT NULL,
  KEY `FK_cs_pk_idx` (`connector_pk`),
  CONSTRAINT `FK_cs_pk` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dbVersion`
--

DROP TABLE IF EXISTS `dbVersion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbVersion` (
  `version` varchar(10) NOT NULL,
  `upateTimestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbVersion`
--

LOCK TABLES `dbVersion` WRITE;
/*!40000 ALTER TABLE `dbVersion` DISABLE KEYS */;
INSERT INTO `dbVersion` (version) VALUES ('0.6.6');
/*!40000 ALTER TABLE `dbVersion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation` (
  `reservation_pk` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idTag` varchar(15) NOT NULL,
  `chargeBoxId` varchar(30) NOT NULL,
  `startDatetime` datetime DEFAULT NULL,
  `expiryDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`reservation_pk`),
  UNIQUE KEY `reservation_pk_UNIQUE` (`reservation_pk`),
  KEY `FK_idTag_r_idx` (`idTag`),
  KEY `FK_chargeBoxId_r_idx` (`chargeBoxId`),
  CONSTRAINT `FK_chargeBoxId_r` FOREIGN KEY (`chargeBoxId`) REFERENCES `chargebox` (`chargeBoxId`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_idTag_r` FOREIGN KEY (`idTag`) REFERENCES `user` (`idTag`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_expired`
--

DROP TABLE IF EXISTS `reservation_expired`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation_expired` (
  `reservation_pk` int(10) unsigned NOT NULL,
  `idTag` varchar(15) NOT NULL,
  `chargeBoxId` varchar(30) NOT NULL,
  `startDatetime` datetime NOT NULL,
  `expiryDatetime` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `transaction_pk` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `connector_pk` int(11) unsigned NOT NULL,
  `idTag` varchar(15) NOT NULL,
  `startTimestamp` timestamp NULL DEFAULT NULL,
  `startValue` varchar(45) DEFAULT NULL,
  `stopTimestamp` timestamp NULL DEFAULT NULL,
  `stopValue` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`transaction_pk`),
  UNIQUE KEY `transaction_pk_UNIQUE` (`transaction_pk`),
  KEY `idTag_idx` (`idTag`),
  KEY `connector_pk_idx` (`connector_pk`),
  CONSTRAINT `FK_connector_pk_t` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_idTag_t` FOREIGN KEY (`idTag`) REFERENCES `user` (`idTag`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `transaction_AINS` AFTER INSERT ON transaction FOR EACH ROW

UPDATE user SET user.inTransaction=1 WHERE user.idTag=NEW.idTag */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `transaction_AUPD` AFTER UPDATE ON transaction FOR EACH ROW
-- Edit trigger body code below this line. Do not edit lines above this one
UPDATE user SET user.inTransaction=0 WHERE user.idTag=NEW.idTag */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `idTag` varchar(15) NOT NULL,
  `parentIdTag` varchar(15) DEFAULT NULL,
  `expiryDate` timestamp NULL DEFAULT NULL,
  `inTransaction` tinyint(1) unsigned NOT NULL,
  `blocked` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`idTag`),
  UNIQUE KEY `idTag_UNIQUE` (`idTag`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'stevedb'
--
/*!50106 SET @save_time_zone= @@TIME_ZONE */ ;
/*!50106 DROP EVENT IF EXISTS `expire_reservations` */;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8 */ ;;
/*!50003 SET character_set_results = utf8 */ ;;
/*!50003 SET collation_connection  = utf8_general_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = '' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`root`@`localhost`*/ /*!50106 EVENT `expire_reservations` ON SCHEDULE EVERY 1 DAY STARTS '2013-11-16 03:00:00' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
INSERT INTO reservation_expired (SELECT * FROM reservation WHERE reservation.expiryDatetime <= NOW());
DELETE FROM reservation WHERE reservation.expiryDatetime <= NOW();
END */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-11-29 15:37:19
