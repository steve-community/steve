--
-- Table structure for table `chargebox`
--

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

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `idTag` varchar(15) NOT NULL,
  `parentIdTag` varchar(15) DEFAULT NULL,
  `expiryDate` timestamp NULL DEFAULT NULL,
  `inTransaction` tinyint(1) unsigned NOT NULL,
  `blocked` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`idTag`),
  UNIQUE KEY `idTag_UNIQUE` (`idTag`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `connector`
--

CREATE TABLE `connector` (
  `connector_pk` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `chargeBoxId` varchar(30) NOT NULL,
  `connectorId` int(11) NOT NULL,
  PRIMARY KEY (`connector_pk`),
  UNIQUE KEY `connector_pk_UNIQUE` (`connector_pk`),
  UNIQUE KEY `connector_cbid_cid_UNIQUE` (`chargeBoxId`,`connectorId`),
  CONSTRAINT `FK_chargeBoxId_c` FOREIGN KEY (`chargeBoxId`) REFERENCES `chargebox` (`chargeBoxId`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


--
-- Table structure for table `connector_status`
--

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

--
-- Table structure for table `dbVersion`
--

CREATE TABLE `dbVersion` (
  `version` varchar(10) NOT NULL,
  `upateTimestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dbVersion`
--

INSERT INTO `dbVersion` (`version`) VALUES ('0.6.6');

--
-- Table structure for table `transaction`
--

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

--
-- Triggers on table `transaction`
--

DELIMITER ;;
CREATE TRIGGER `transaction_AINS` AFTER INSERT ON transaction FOR EACH ROW
  UPDATE user SET user.inTransaction=1 WHERE user.idTag=NEW.idTag;;
DELIMITER ;

DELIMITER ;;
CREATE TRIGGER `transaction_AUPD` AFTER UPDATE ON transaction FOR EACH ROW
  UPDATE user SET user.inTransaction=0 WHERE user.idTag=NEW.idTag;;
DELIMITER ;

--
-- Table structure for table `connector_metervalue`
--

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

--
-- Table structure for table `reservation`
--

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

--
-- Table structure for table `reservation_expired`
--

CREATE TABLE `reservation_expired` (
  `reservation_pk` int(10) unsigned NOT NULL,
  `idTag` varchar(15) NOT NULL,
  `chargeBoxId` varchar(30) NOT NULL,
  `startDatetime` datetime NOT NULL,
  `expiryDatetime` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Dumping events for database
--

DELIMITER ;;

CREATE EVENT `expire_reservations`
  ON SCHEDULE EVERY 1 DAY STARTS '2013-11-16 03:00:00' ON COMPLETION NOT PRESERVE ENABLE DO
  BEGIN
    INSERT INTO reservation_expired (SELECT * FROM reservation WHERE reservation.expiryDatetime <= NOW());
    DELETE FROM reservation WHERE reservation.expiryDatetime <= NOW();
  END;;

DELIMITER ;
