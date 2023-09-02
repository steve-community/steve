--
-- all timestamps have fractional seconds with the precision 6 now.
-- fractional seconds are unfortunately not default, for compatibility with previous MySQL versions
-- (https://dev.mysql.com/doc/refman/5.6/en/fractional-seconds.html)
--

ALTER TABLE `chargebox`
CHANGE COLUMN `fwUpdateTimestamp` `fwUpdateTimestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `diagnosticsTimestamp` `diagnosticsTimestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `lastHeartbeatTimestamp` `lastHeartbeatTimestamp` TIMESTAMP(6) NULL DEFAULT NULL;

ALTER TABLE `connector_metervalue`
CHANGE COLUMN `valueTimestamp` `valueTimestamp` TIMESTAMP(6) NULL DEFAULT NULL;

ALTER TABLE `connector_status`
CHANGE COLUMN `statusTimestamp` `statusTimestamp` TIMESTAMP(6) NULL DEFAULT NULL;

ALTER TABLE `transaction`
CHANGE COLUMN `startTimestamp` `startTimestamp` TIMESTAMP(6) NULL DEFAULT NULL,
CHANGE COLUMN `stopTimestamp` `stopTimestamp` TIMESTAMP(6) NULL DEFAULT NULL;

ALTER TABLE `user`
CHANGE COLUMN `expiryDate` `expiryDate` TIMESTAMP(6) NULL DEFAULT NULL;