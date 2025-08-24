--
-- drop foreign keys that will be affected by the rename process
--

ALTER TABLE `connector` DROP FOREIGN KEY `FK_chargeBoxId_c`;
ALTER TABLE `reservation` DROP FOREIGN KEY `FK_chargeBoxId_r`;
ALTER TABLE `reservation` DROP FOREIGN KEY `FK_idTag_r`;
ALTER TABLE `transaction` DROP FOREIGN KEY `FK_idTag_t`;
ALTER TABLE `user` DROP FOREIGN KEY `FK_user_parentIdTag`;

-- -------------------------------------------------------------------------
-- START: change table and column names from "camel case" to "snake case"
-- -------------------------------------------------------------------------

--
-- table charge_box
--

RENAME TABLE `chargebox` TO `charge_box`;

ALTER TABLE `charge_box`
CHANGE COLUMN `chargeBoxId` `charge_box_id` VARCHAR(255) NOT NULL,
CHANGE COLUMN `ocppProtocol` `ocpp_protocol` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `chargePointVendor` `charge_point_vendor` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `chargePointModel` `charge_point_model` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `chargePointSerialNumber` `charge_point_serial_number` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `chargeBoxSerialNumber` `charge_box_serial_number` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `fwVersion` `fw_version` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `fwUpdateStatus` `fw_update_status` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `fwUpdateTimestamp` `fw_update_timestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `meterType` `meter_type` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `meterSerialNumber` `meter_serial_number` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `diagnosticsStatus` `diagnostics_status` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `diagnosticsTimestamp` `diagnostics_timestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `lastHeartbeatTimestamp` `last_heartbeat_timestamp` TIMESTAMP(6) NULL DEFAULT NULL;

--
-- table connector
--

ALTER TABLE `connector`
CHANGE COLUMN `chargeBoxId` `charge_box_id` VARCHAR(255) NOT NULL,
CHANGE COLUMN `connectorId` `connector_id` INT(11) NOT NULL;

--
-- table connector_meter_value
--

RENAME TABLE `connector_metervalue` TO `connector_meter_value`;

ALTER TABLE `connector_meter_value`
CHANGE COLUMN `valueTimestamp` `value_timestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `readingContext` `reading_context` VARCHAR(255) NULL DEFAULT NULL;

--
-- table connector_status
--

ALTER TABLE `connector_status`
CHANGE COLUMN `statusTimestamp` `status_timestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `errorCode` `error_code` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `errorInfo` `error_info` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `vendorId` `vendor_id` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `vendorErrorCode` `vendor_error_code` VARCHAR(255) NULL DEFAULT NULL;

--
-- table reservation
--

ALTER TABLE `reservation`
CHANGE COLUMN `idTag` `id_tag` VARCHAR(255) NOT NULL,
CHANGE COLUMN `chargeBoxId` `charge_box_id` VARCHAR(255) NOT NULL,
CHANGE COLUMN `startDatetime` `start_datetime` DATETIME NULL DEFAULT NULL,
CHANGE COLUMN `expiryDatetime` `expiry_datetime` DATETIME NULL DEFAULT NULL;

--
-- table settings
--

ALTER TABLE `settings`
CHANGE COLUMN `appId` `app_id` VARCHAR(40) NOT NULL,
CHANGE COLUMN `heartbeatIntervalInSeconds` `heartbeat_interval_in_seconds` INT(11) NULL DEFAULT NULL,
CHANGE COLUMN `hoursToExpire` `hours_to_expire` INT(11) NULL DEFAULT NULL;

--
-- table transaction
--

ALTER TABLE `transaction`
CHANGE COLUMN `idTag` `id_tag` VARCHAR(255) NOT NULL,
CHANGE COLUMN `startTimestamp` `start_timestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `startValue` `start_value` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `stopTimestamp` `stop_timestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `stopValue` `stop_value` VARCHAR(255) NULL DEFAULT NULL;

--
-- table user
--

ALTER TABLE `user`
CHANGE COLUMN `idTag` `id_tag` VARCHAR(255) NOT NULL,
CHANGE COLUMN `parentIdTag` `parent_id_tag` VARCHAR(255) NULL DEFAULT NULL,
CHANGE COLUMN `expiryDate` `expiry_date` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `inTransaction` `in_transaction` TINYINT(1) UNSIGNED NOT NULL;

-- -------------------------------------------------------------------------
-- END: change table and column names from "camel case" to "snake case"
-- -------------------------------------------------------------------------

--
-- add foreign keys back
--

ALTER TABLE `connector`
ADD CONSTRAINT `FK_connector_charge_box_cbid`
FOREIGN KEY (`charge_box_id`)
REFERENCES `charge_box` (`charge_box_id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `reservation`
ADD CONSTRAINT `FK_reservation_charge_box_cbid`
FOREIGN KEY (`charge_box_id`)
REFERENCES `charge_box` (`charge_box_id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `reservation`
ADD CONSTRAINT `FK_reservation_user_id_tag`
FOREIGN KEY (`id_tag`)
REFERENCES `user` (`id_tag`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `transaction`
ADD CONSTRAINT `FK_transaction_user_id_tag`
FOREIGN KEY (`id_tag`)
REFERENCES `user` (`id_tag`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `user`
ADD CONSTRAINT `FK_user_parent_id_tag`
FOREIGN KEY (`parent_id_tag`)
REFERENCES `user` (`id_tag`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


--
-- update the triggers
--

DELIMITER $$
DROP TRIGGER IF EXISTS `transaction_AINS`$$
CREATE TRIGGER `transaction_AINS` AFTER INSERT ON `transaction` FOR EACH ROW
  UPDATE `user`
  SET `user`.`in_transaction` = 1
  WHERE `user`.`id_tag` = NEW.`id_tag`$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS `transaction_AUPD`$$
CREATE TRIGGER `transaction_AUPD` AFTER UPDATE ON `transaction` FOR EACH ROW
  UPDATE `user`
  SET `user`.`in_transaction` = 0
  WHERE `user`.`id_tag` = NEW.`id_tag`$$
DELIMITER ;


--
-- use auto incremented integers as PKs
--

ALTER TABLE `charge_box`
DROP PRIMARY KEY,
ADD `charge_box_pk` INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

ALTER TABLE `user`
DROP PRIMARY KEY,
ADD `user_pk` INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;
