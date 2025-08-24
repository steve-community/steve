ALTER TABLE `transaction`
  ADD COLUMN `stop_reason` VARCHAR(255) DEFAULT NULL AFTER `stop_value`;

ALTER TABLE `connector_meter_value`
  ADD COLUMN `phase` VARCHAR(255) DEFAULT NULL AFTER `unit`;