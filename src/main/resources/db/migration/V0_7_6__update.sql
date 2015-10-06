ALTER TABLE `user`
ADD COLUMN `note` TEXT NULL COMMENT '' AFTER `blocked`;

ALTER TABLE `chargebox`
ADD COLUMN `note` TEXT NULL COMMENT '' AFTER `lastHeartbeatTimestamp`;
