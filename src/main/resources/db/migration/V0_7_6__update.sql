ALTER TABLE `stevedb`.`user`
ADD COLUMN `note` TEXT NULL COMMENT '' AFTER `blocked`;

ALTER TABLE `stevedb`.`chargebox`
ADD COLUMN `note` TEXT NULL COMMENT '' AFTER `lastHeartbeatTimestamp`;