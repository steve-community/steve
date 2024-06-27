/*!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.4.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: SteveDB
-- ------------------------------------------------------
-- Server version	11.4.2-MariaDB

# Set some DB settings
SET NAMES utf8mb4;
SET TIME_ZONE='+00:00';
SET character_set_client = utf8;

# Save Settings of Unique check, Foreign Kex check and the current sql mode.
# Then deactivate the checks and set sql mode to 'NO_AUTO_VALUE_ON_ZERO'
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_pk` int(11) NOT NULL AUTO_INCREMENT,
  `street` varchar(1000) DEFAULT NULL,
  `house_number` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`address_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `charge_box`
--

DROP TABLE IF EXISTS `charge_box`;
CREATE TABLE `charge_box` (
  `charge_box_pk` int(11) NOT NULL AUTO_INCREMENT,
  `charge_box_id` varchar(255) NOT NULL,
  `endpoint_address` varchar(255) DEFAULT NULL,
  `ocpp_protocol` varchar(255) DEFAULT NULL,
  `registration_status` varchar(255) NOT NULL DEFAULT 'Accepted',
  `charge_point_vendor` varchar(255) DEFAULT NULL,
  `charge_point_model` varchar(255) DEFAULT NULL,
  `charge_point_serial_number` varchar(255) DEFAULT NULL,
  `charge_box_serial_number` varchar(255) DEFAULT NULL,
  `fw_version` varchar(255) DEFAULT NULL,
  `fw_update_status` varchar(255) DEFAULT NULL,
  `fw_update_timestamp` timestamp(6) NULL DEFAULT NULL,
  `iccid` varchar(255) DEFAULT NULL,
  `imsi` varchar(255) DEFAULT NULL,
  `meter_type` varchar(255) DEFAULT NULL,
  `meter_serial_number` varchar(255) DEFAULT NULL,
  `diagnostics_status` varchar(255) DEFAULT NULL,
  `diagnostics_timestamp` timestamp(6) NULL DEFAULT NULL,
  `last_heartbeat_timestamp` timestamp(6) NULL DEFAULT NULL,
  `description` mediumtext DEFAULT NULL,
  `note` mediumtext DEFAULT NULL,
  `location_latitude` decimal(11,8) DEFAULT NULL,
  `location_longitude` decimal(11,8) DEFAULT NULL,
  `address_pk` int(11) DEFAULT NULL,
  `admin_address` varchar(255) DEFAULT NULL,
  `insert_connector_status_after_transaction_msg` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`charge_box_pk`),
  UNIQUE KEY `chargeBoxId_UNIQUE` (`charge_box_id`),
  KEY `chargebox_op_ep_idx` (`ocpp_protocol`,`endpoint_address`),
  KEY `FK_charge_box_address_apk` (`address_pk`),
  CONSTRAINT `FK_charge_box_address_apk` FOREIGN KEY (`address_pk`) REFERENCES `address` (`address_pk`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `charging_profile`
--

DROP TABLE IF EXISTS `charging_profile`;
CREATE TABLE `charging_profile` (
  `charging_profile_pk` int(11) NOT NULL AUTO_INCREMENT,
  `stack_level` int(11) NOT NULL,
  `charging_profile_purpose` varchar(255) NOT NULL,
  `charging_profile_kind` varchar(255) NOT NULL,
  `recurrency_kind` varchar(255) DEFAULT NULL,
  `valid_from` timestamp(6) NULL DEFAULT NULL,
  `valid_to` timestamp(6) NULL DEFAULT NULL,
  `duration_in_seconds` int(11) DEFAULT NULL,
  `start_schedule` timestamp(6) NULL DEFAULT NULL,
  `charging_rate_unit` varchar(255) NOT NULL,
  `min_charging_rate` decimal(15,1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `note` text DEFAULT NULL,
  PRIMARY KEY (`charging_profile_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `charging_schedule_period`
--

DROP TABLE IF EXISTS `charging_schedule_period`;
CREATE TABLE `charging_schedule_period` (
  `charging_profile_pk` int(11) NOT NULL,
  `start_period_in_seconds` int(11) NOT NULL,
  `power_limit` decimal(15,1) NOT NULL,
  `number_phases` int(11) DEFAULT NULL,
  UNIQUE KEY `UQ_charging_schedule_period` (`charging_profile_pk`,`start_period_in_seconds`),
  CONSTRAINT `FK_charging_schedule_period_charging_profile_pk` FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `connector`
--

DROP TABLE IF EXISTS `connector`;
CREATE TABLE `connector` (
  `connector_pk` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `charge_box_id` varchar(255) NOT NULL,
  `connector_id` int(11) NOT NULL,
  PRIMARY KEY (`connector_pk`),
  UNIQUE KEY `connector_pk_UNIQUE` (`connector_pk`),
  UNIQUE KEY `connector_cbid_cid_UNIQUE` (`charge_box_id`,`connector_id`),
  CONSTRAINT `FK_connector_charge_box_cbid` FOREIGN KEY (`charge_box_id`) REFERENCES `charge_box` (`charge_box_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `connector_charging_profile`
--

DROP TABLE IF EXISTS `connector_charging_profile`;
CREATE TABLE `connector_charging_profile` (
  `connector_pk` int(11) unsigned NOT NULL,
  `charging_profile_pk` int(11) NOT NULL,
  UNIQUE KEY `UQ_connector_charging_profile` (`connector_pk`,`charging_profile_pk`),
  KEY `FK_connector_charging_profile_charging_profile_pk` (`charging_profile_pk`),
  CONSTRAINT `FK_connector_charging_profile_charging_profile_pk` FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_connector_charging_profile_connector_pk` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `connector_meter_value`
--

DROP TABLE IF EXISTS `connector_meter_value`;
CREATE TABLE `connector_meter_value` (
  `connector_pk` int(11) unsigned NOT NULL,
  `transaction_pk` int(10) unsigned DEFAULT NULL,
  `value_timestamp` timestamp(6) NULL DEFAULT NULL,
  `value` text DEFAULT NULL,
  `reading_context` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `measurand` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `phase` varchar(255) DEFAULT NULL,
  KEY `FK_cm_pk_idx` (`connector_pk`),
  KEY `FK_tid_cm_idx` (`transaction_pk`),
  KEY `cmv_value_timestamp_idx` (`value_timestamp`),
  CONSTRAINT `FK_pk_cm` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_tid_cm` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `connector_status`
--

DROP TABLE IF EXISTS `connector_status`;
CREATE TABLE `connector_status` (
  `connector_pk` int(11) unsigned NOT NULL,
  `status_timestamp` timestamp(6) NULL DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `error_code` varchar(255) DEFAULT NULL,
  `error_info` varchar(255) DEFAULT NULL,
  `vendor_id` varchar(255) DEFAULT NULL,
  `vendor_error_code` varchar(255) DEFAULT NULL,
  KEY `FK_cs_pk_idx` (`connector_pk`),
  KEY `connector_status_cpk_st_idx` (`connector_pk`,`status_timestamp`),
  CONSTRAINT `FK_cs_pk` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `ocpp_tag`
--

DROP TABLE IF EXISTS `ocpp_tag`;
CREATE TABLE `ocpp_tag` (
  `ocpp_tag_pk` int(11) NOT NULL AUTO_INCREMENT,
  `id_tag` varchar(255) NOT NULL,
  `parent_id_tag` varchar(255) DEFAULT NULL,
  `expiry_date` timestamp(6) NULL DEFAULT NULL,
  `max_active_transaction_count` int(11) NOT NULL DEFAULT 1,
  `note` mediumtext DEFAULT NULL,
  PRIMARY KEY (`ocpp_tag_pk`),
  UNIQUE KEY `idTag_UNIQUE` (`id_tag`),
  KEY `user_expiryDate_idx` (`expiry_date`),
  KEY `FK_ocpp_tag_parent_id_tag` (`parent_id_tag`),
  CONSTRAINT `FK_ocpp_tag_parent_id_tag` FOREIGN KEY (`parent_id_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
  `reservation_pk` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `connector_pk` int(11) unsigned NOT NULL,
  `transaction_pk` int(10) unsigned DEFAULT NULL,
  `id_tag` varchar(255) NOT NULL,
  `start_datetime` datetime DEFAULT NULL,
  `expiry_datetime` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`reservation_pk`),
  UNIQUE KEY `reservation_pk_UNIQUE` (`reservation_pk`),
  UNIQUE KEY `transaction_pk_UNIQUE` (`transaction_pk`),
  KEY `FK_idTag_r_idx` (`id_tag`),
  KEY `reservation_start_idx` (`start_datetime`),
  KEY `reservation_expiry_idx` (`expiry_datetime`),
  KEY `reservation_status_idx` (`status`),
  KEY `FK_connector_pk_reserv_idx` (`connector_pk`),
  CONSTRAINT `FK_connector_pk_reserv` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_reservation_ocpp_tag_id_tag` FOREIGN KEY (`id_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_transaction_pk_r` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `app_id` varchar(40) NOT NULL,
  `heartbeat_interval_in_seconds` int(11) DEFAULT NULL,
  `hours_to_expire` int(11) DEFAULT NULL,
  `mail_enabled` tinyint(1) DEFAULT 0,
  `mail_host` varchar(255) DEFAULT NULL,
  `mail_username` varchar(255) DEFAULT NULL,
  `mail_password` varchar(255) DEFAULT NULL,
  `mail_from` varchar(255) DEFAULT NULL,
  `mail_protocol` varchar(255) DEFAULT 'smtp',
  `mail_port` int(11) DEFAULT 25,
  `mail_recipients` text DEFAULT NULL COMMENT 'comma separated list of email addresses',
  `notification_features` text DEFAULT NULL COMMENT 'comma separated list',
  PRIMARY KEY (`app_id`),
  UNIQUE KEY `settings_id_UNIQUE` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


-- Exported data of table stevedb.settings: ~1 rows (approx.)
INSERT INTO `settings` (`app_id`, `heartbeat_interval_in_seconds`, `hours_to_expire`, `mail_enabled`, `mail_host`, `mail_username`, `mail_password`, `mail_from`, `mail_protocol`, `mail_port`, `mail_recipients`, `notification_features`) VALUES
	('U3RlY2tkb3NlblZlcndhbHR1bmc=', 14400, 1, 0, NULL, NULL, NULL, NULL, 'smtp', 25, NULL, NULL);


DROP TABLE IF EXISTS `transaction_start`;
CREATE TABLE `transaction_start` (
  `transaction_pk` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `connector_pk` int(11) unsigned NOT NULL,
  `id_tag` varchar(255) NOT NULL,
  `start_timestamp` timestamp(6) NULL DEFAULT NULL,
  `start_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`transaction_pk`),
  UNIQUE KEY `transaction_pk_UNIQUE` (`transaction_pk`),
  KEY `idTag_idx` (`id_tag`),
  KEY `connector_pk_idx` (`connector_pk`),
  KEY `transaction_start_idx` (`start_timestamp`),
  CONSTRAINT `FK_connector_pk_t` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_transaction_ocpp_tag_id_tag` FOREIGN KEY (`id_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `transaction_stop`
--

DROP TABLE IF EXISTS `transaction_stop`;
CREATE TABLE `transaction_stop` (
  `transaction_pk` int(10) unsigned NOT NULL,
  `event_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `event_actor` enum('station','manual') DEFAULT NULL,
  `stop_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `stop_value` varchar(255) NOT NULL,
  `stop_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`transaction_pk`,`event_timestamp`),
  CONSTRAINT `FK_transaction_stop_transaction_pk` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `transaction_stop_failed`
--

DROP TABLE IF EXISTS `transaction_stop_failed`;
CREATE TABLE `transaction_stop_failed` (
  `transaction_pk` int(11) DEFAULT NULL,
  `charge_box_id` varchar(255) DEFAULT NULL,
  `event_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `event_actor` enum('station','manual') DEFAULT NULL,
  `stop_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `stop_value` varchar(255) DEFAULT NULL,
  `stop_reason` varchar(255) DEFAULT NULL,
  `fail_reason` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_pk` int(11) NOT NULL AUTO_INCREMENT,
  `ocpp_tag_pk` int(11) DEFAULT NULL,
  `address_pk` int(11) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `birth_day` date DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `e_mail` varchar(255) DEFAULT NULL,
  `note` text DEFAULT NULL,
  PRIMARY KEY (`user_pk`),
  KEY `FK_user_ocpp_tag_otpk` (`ocpp_tag_pk`),
  KEY `FK_user_address_apk` (`address_pk`),
  CONSTRAINT `FK_user_address_apk` FOREIGN KEY (`address_pk`) REFERENCES `address` (`address_pk`) ON DELETE SET NULL ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_ocpp_tag_otpk` FOREIGN KEY (`ocpp_tag_pk`) REFERENCES `ocpp_tag` (`ocpp_tag_pk`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Final view structure for view `transaction`
--

DROP VIEW IF EXISTS `transaction`;
CREATE ALGORITHM=UNDEFINED
VIEW `transaction` AS select 
    `tx1`.`transaction_pk` AS `transaction_pk`,
    `tx1`.`connector_pk` AS `connector_pk`,
    `tx1`.`id_tag` AS `id_tag`,
    `tx1`.`event_timestamp` AS `start_event_timestamp`,
    `tx1`.`start_timestamp` AS `start_timestamp`,
    `tx1`.`start_value` AS `start_value`,
    `tx2`.`event_actor` AS `stop_event_actor`,
    `tx2`.`event_timestamp` AS `stop_event_timestamp`,
    `tx2`.`stop_timestamp` AS `stop_timestamp`,
    `tx2`.`stop_value` AS `stop_value`,
    `tx2`.`stop_reason` AS `stop_reason`
 from (`transaction_start` `tx1`
    left join `transaction_stop` `tx2`
    on(`tx1`.`transaction_pk` = `tx2`.`transaction_pk`
    and `tx2`.`event_timestamp` = (select max(`s2`.`event_timestamp`)
    from `transaction_stop` `s2` where `tx2`.`transaction_pk` = `s2`.`transaction_pk`)));


--
-- Final view structure for view `ocpp_tag_activity`
--

DROP VIEW IF EXISTS `ocpp_tag_activity`;
CREATE ALGORITHM=UNDEFINED
VIEW `ocpp_tag_activity` AS select
    `o`.`ocpp_tag_pk` AS `ocpp_tag_pk`,
    `o`.`id_tag` AS `id_tag`,
    `o`.`parent_id_tag` AS `parent_id_tag`,
    `o`.`expiry_date` AS `expiry_date`,
    `o`.`max_active_transaction_count` AS `max_active_transaction_count`,
    `o`.`note` AS `note`,
    count(`t`.`id_tag`) AS `active_transaction_count`,
    case when count(`t`.`id_tag`) > 0 then 1 else 0 end AS `in_transaction`,
    case when `o`.`max_active_transaction_count` = 0 then 1 else 0 end AS `blocked` 
from (`ocpp_tag` `o` left join `transaction` `t` on(
    `o`.`id_tag` = `t`.`id_tag` and
    `t`.`stop_timestamp` is null and
    `t`.`stop_value` is null)) 
group by `o`.`ocpp_tag_pk`,
    `o`.`parent_id_tag`,
    `o`.`expiry_date`,
    `o`.`max_active_transaction_count`,
    `o`.`note`;

# Reset the checks and sql mode to the value before executing this script
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

