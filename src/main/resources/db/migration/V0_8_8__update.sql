ALTER TABLE `settings`
  ADD COLUMN `mail_enabled` BOOLEAN DEFAULT FALSE,

  ADD COLUMN `mail_host` VARCHAR(255) DEFAULT NULL,
  ADD COLUMN `mail_username` VARCHAR(255) DEFAULT NULL,
  ADD COLUMN `mail_password` VARCHAR(255) DEFAULT NULL,
  ADD COLUMN `mail_from` VARCHAR(255) DEFAULT NULL,
  ADD COLUMN `mail_protocol` VARCHAR(255) DEFAULT 'smtp',

  ADD COLUMN `mail_port` INT DEFAULT 25,

  ADD COLUMN `mail_recipients` TEXT COMMENT 'comma separated list of email addresses',
  ADD COLUMN `notification_features` TEXT COMMENT 'comma separated list';