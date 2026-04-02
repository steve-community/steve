-- Docker initialization script for SteVe database
-- This script combines the baseline schema (B1_0_5) with all subsequent migrations
-- to create the complete database schema on first startup.

SET NAMES utf8mb4;
SET TIME_ZONE='+00:00';
SET character_set_client = utf8;
SET default_storage_engine=InnoDB;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';

-- ============================================================
-- Baseline schema (B1_0_5)
-- ============================================================

CREATE TABLE IF NOT EXISTS `address` (
  `address_pk` int(11) NOT NULL AUTO_INCREMENT,
  `street` varchar(1000) DEFAULT NULL,
  `house_number` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`address_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE IF NOT EXISTS `charge_box` (
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

CREATE TABLE IF NOT EXISTS `charging_profile` (
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

CREATE TABLE IF NOT EXISTS `charging_schedule_period` (
  `charging_profile_pk` int(11) NOT NULL,
  `start_period_in_seconds` int(11) NOT NULL,
  `power_limit` decimal(15,1) NOT NULL,
  `number_phases` int(11) DEFAULT NULL,
  UNIQUE KEY `UQ_charging_schedule_period` (`charging_profile_pk`,`start_period_in_seconds`),
  CONSTRAINT `FK_charging_schedule_period_charging_profile_pk` FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE IF NOT EXISTS `connector` (
  `connector_pk` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `charge_box_id` varchar(255) NOT NULL,
  `connector_id` int(11) NOT NULL,
  PRIMARY KEY (`connector_pk`),
  UNIQUE KEY `connector_pk_UNIQUE` (`connector_pk`),
  UNIQUE KEY `connector_cbid_cid_UNIQUE` (`charge_box_id`,`connector_id`),
  CONSTRAINT `FK_connector_charge_box_cbid` FOREIGN KEY (`charge_box_id`) REFERENCES `charge_box` (`charge_box_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE IF NOT EXISTS `connector_charging_profile` (
  `connector_pk` int(11) unsigned NOT NULL,
  `charging_profile_pk` int(11) NOT NULL,
  UNIQUE KEY `UQ_connector_charging_profile` (`connector_pk`,`charging_profile_pk`),
  KEY `FK_connector_charging_profile_charging_profile_pk` (`charging_profile_pk`),
  CONSTRAINT `FK_connector_charging_profile_charging_profile_pk` FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_connector_charging_profile_connector_pk` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE IF NOT EXISTS `ocpp_tag` (
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

CREATE TABLE IF NOT EXISTS `transaction_start` (
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

CREATE TABLE IF NOT EXISTS `transaction_stop` (
  `transaction_pk` int(10) unsigned NOT NULL,
  `event_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `event_actor` enum('station','manual') DEFAULT NULL,
  `stop_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `stop_value` varchar(255) NOT NULL,
  `stop_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`transaction_pk`,`event_timestamp`),
  CONSTRAINT `FK_transaction_stop_transaction_pk` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE IF NOT EXISTS `transaction_stop_failed` (
  `transaction_pk` int(11) DEFAULT NULL,
  `charge_box_id` varchar(255) DEFAULT NULL,
  `event_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `event_actor` enum('station','manual') DEFAULT NULL,
  `stop_timestamp` timestamp(6) NOT NULL DEFAULT current_timestamp(6),
  `stop_value` varchar(255) DEFAULT NULL,
  `stop_reason` varchar(255) DEFAULT NULL,
  `fail_reason` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE IF NOT EXISTS `connector_meter_value` (
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

CREATE TABLE IF NOT EXISTS `connector_status` (
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

CREATE TABLE IF NOT EXISTS `reservation` (
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

CREATE TABLE IF NOT EXISTS `schema_version` (
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

CREATE TABLE IF NOT EXISTS `settings` (
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

INSERT INTO `settings` (`app_id`, `heartbeat_interval_in_seconds`, `hours_to_expire`, `mail_enabled`, `mail_host`, `mail_username`, `mail_password`, `mail_from`, `mail_protocol`, `mail_port`, `mail_recipients`, `notification_features`) VALUES
	('U3RlY2tkb3NlblZlcndhbHR1bmc=', 14400, 1, 0, NULL, NULL, NULL, NULL, 'smtp', 25, NULL, NULL);

CREATE TABLE IF NOT EXISTS `user` (
  `user_pk` int(11) NOT NULL AUTO_INCREMENT,
  `address_pk` int(11) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `birth_day` date DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `e_mail` varchar(255) DEFAULT NULL,
  `note` text DEFAULT NULL,
  PRIMARY KEY (`user_pk`),
  KEY `FK_user_address_apk` (`address_pk`),
  CONSTRAINT `FK_user_address_apk` FOREIGN KEY (`address_pk`) REFERENCES `address` (`address_pk`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- Views
CREATE OR REPLACE VIEW `transaction` AS select
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

CREATE OR REPLACE VIEW `ocpp_tag_activity` AS select
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

-- ============================================================
-- V1_0_6: Create web_user table
-- ============================================================
CREATE TABLE IF NOT EXISTS `web_user` (
    `web_user_pk` INT          NOT NULL AUTO_INCREMENT,
    `username`    varchar(500) NOT NULL,
    `password`    varchar(500) NOT NULL,
    `api_token`   varchar(500) NULL,
    `enabled`     BOOLEAN      NOT NULL,
    `authorities` JSON         NOT NULL,

    PRIMARY KEY (`web_user_pk`),
    UNIQUE KEY (`username`),

    CONSTRAINT `authorities_must_be_array` CHECK (json_type(`authorities`) = convert('ARRAY' using utf8))
);

-- ============================================================
-- V1_0_7: Rename api_token to api_password
-- ============================================================
ALTER TABLE `web_user` CHANGE COLUMN `api_token` `api_password` varchar(500) NULL;

-- ============================================================
-- V1_0_8: Create user_ocpp_tag junction table
-- ============================================================
CREATE TABLE IF NOT EXISTS `user_ocpp_tag` (
    `user_pk` int(11) NOT NULL,
    `ocpp_tag_pk` int(11) NOT NULL,
    PRIMARY KEY (`user_pk`, `ocpp_tag_pk`),
    UNIQUE (`ocpp_tag_pk`),
    FOREIGN KEY (`user_pk`) REFERENCES `user`(`user_pk`) ON DELETE CASCADE,
    FOREIGN KEY (`ocpp_tag_pk`) REFERENCES `ocpp_tag`(`ocpp_tag_pk`) ON DELETE CASCADE
);

-- ============================================================
-- V1_0_9: Add notification_features to user
-- ============================================================
ALTER TABLE `user`
    ADD COLUMN `notification_features` TEXT NULL DEFAULT NULL COMMENT 'comma separated list' COLLATE 'utf8mb3_unicode_ci' AFTER `e_mail`;

-- ============================================================
-- V1_1_0: Move lat/long from charge_box to address
-- ============================================================
ALTER TABLE `address`
    ADD COLUMN `latitude` DECIMAL(11, 8),
    ADD COLUMN `longitude` DECIMAL(11, 8);

ALTER TABLE `charge_box`
    DROP COLUMN `location_latitude`,
    DROP COLUMN `location_longitude`;

-- ============================================================
-- V1_1_1: OCPP 1.6 Security tables
-- ============================================================
ALTER TABLE `charge_box`
    ADD COLUMN `security_profile` INT DEFAULT 0,
    ADD COLUMN `auth_password` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `cpo_name` VARCHAR(255) DEFAULT NULL;

CREATE TABLE IF NOT EXISTS `charge_box_security_event` (
    `charge_box_pk` INT NOT NULL,
    `type` VARCHAR(100) NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    `tech_info` VARCHAR(500),
    `event_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`charge_box_pk`) REFERENCES `charge_box` (`charge_box_pk`) ON DELETE CASCADE,
    INDEX `idx_charge_box_pk` (`charge_box_pk`),
    INDEX `idx_type` (`type`),
    INDEX `idx_timestamp` (`timestamp`)
);

CREATE TABLE IF NOT EXISTS `charge_box_firmware_update_job` (
    `job_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `firmware_location` VARCHAR(1000) NOT NULL,
    `retrieve_datetime` TIMESTAMP NULL DEFAULT NULL,
    `install_datetime` TIMESTAMP NULL DEFAULT NULL,
    `signing_certificate` MEDIUMTEXT,
    `signature` MEDIUMTEXT
);

CREATE TABLE IF NOT EXISTS `charge_box_firmware_update_event` (
    `job_id` INT DEFAULT NULL,
    `charge_box_pk` INT NOT NULL,
    `event_status` VARCHAR(100) NOT NULL,
    `event_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`job_id`) REFERENCES `charge_box_firmware_update_job` (`job_id`) ON DELETE CASCADE,
    FOREIGN KEY (`charge_box_pk`) REFERENCES `charge_box` (`charge_box_pk`) ON DELETE CASCADE,
    INDEX `idx_job_id` (`job_id`),
    INDEX `idx_charge_box_pk` (`charge_box_pk`),
    INDEX `idx_event_status` (`event_status`),
    INDEX `idx_event_timestamp` (`event_timestamp`)
);

CREATE TABLE IF NOT EXISTS `charge_box_log_upload_job` (
    `job_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `log_type` VARCHAR(100) NOT NULL,
    `remote_location` VARCHAR(1000),
    `oldest_timestamp` TIMESTAMP NULL DEFAULT NULL,
    `latest_timestamp` TIMESTAMP NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `charge_box_log_upload_event` (
    `job_id` INT DEFAULT NULL,
    `charge_box_pk` INT NOT NULL,
    `event_status` VARCHAR(100) NOT NULL,
    `event_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`job_id`) REFERENCES `charge_box_log_upload_job` (`job_id`) ON DELETE CASCADE,
    FOREIGN KEY (`charge_box_pk`) REFERENCES `charge_box` (`charge_box_pk`) ON DELETE CASCADE,
    INDEX `idx_job_id` (`job_id`),
    INDEX `idx_charge_box_pk` (`charge_box_pk`),
    INDEX `idx_event_status` (`event_status`),
    INDEX `idx_event_timestamp` (`event_timestamp`)
);

CREATE OR REPLACE VIEW `charge_box_status_event` AS
SELECT
    `job_id`,
    `charge_box_pk`,
    `event_status`,
    `event_timestamp`,
    'FirmwareUpdate' AS `event_type`
FROM `charge_box_firmware_update_event`
UNION ALL
SELECT
    `job_id`,
    `charge_box_pk`,
    `event_status`,
    `event_timestamp`,
    'LogUpload' AS `event_type`
FROM `charge_box_log_upload_event`;

CREATE TABLE IF NOT EXISTS `certificate` (
    `certificate_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `serial_number` VARCHAR(255),
    `issuer_name` VARCHAR(500),
    `subject_name` VARCHAR(500),
    `organization_name` VARCHAR(500),
    `common_name` VARCHAR(500),
    `key_size` INT,
    `valid_from` TIMESTAMP,
    `valid_to` TIMESTAMP,
    `signature_algorithm` ENUM('RSA', 'ECDSA'),
    `certificate_chain_pem` MEDIUMTEXT NOT NULL,
    INDEX `idx_serial_number` (`serial_number`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_valid_from` (`valid_from`),
    INDEX `idx_valid_to` (`valid_to`)
);

CREATE TABLE IF NOT EXISTS `charge_box_certificate_signed` (
    `certificate_id` INT NOT NULL,
    `charge_box_pk` INT NOT NULL,
    `accepted` BOOL NOT NULL,
    `responded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`certificate_id`) REFERENCES `certificate` (`certificate_id`) ON DELETE CASCADE,
    FOREIGN KEY (`charge_box_pk`) REFERENCES `charge_box` (`charge_box_pk`) ON DELETE CASCADE
);

CREATE OR REPLACE VIEW `charge_box_certificate_signed_view` AS
SELECT c.*,
       cb.`charge_box_id`,
       cbcs.`charge_box_pk`,
       cbcs.`accepted`,
       cbcs.`responded_at`
FROM `certificate` c
INNER JOIN `charge_box_certificate_signed` cbcs ON cbcs.`certificate_id` = c.`certificate_id`
INNER JOIN `charge_box` cb ON cb.`charge_box_pk` = cbcs.`charge_box_pk`;

CREATE TABLE IF NOT EXISTS `charge_box_certificate_installed` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `charge_box_pk` INT NOT NULL,
    `responded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `certificate_type` VARCHAR(255) NOT NULL,
    `hash_algorithm` VARCHAR(128) NOT NULL,
    `issuer_name_hash` VARCHAR(128) NOT NULL,
    `issuer_key_hash` VARCHAR(128) NOT NULL,
    `serial_number` VARCHAR(255) NOT NULL,
    FOREIGN KEY (`charge_box_pk`) REFERENCES `charge_box` (`charge_box_pk`) ON DELETE CASCADE,
    INDEX `idx_charge_box_pk` (`charge_box_pk`),
    INDEX `idx_certificate_type` (`certificate_type`)
);

-- Reset settings
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
