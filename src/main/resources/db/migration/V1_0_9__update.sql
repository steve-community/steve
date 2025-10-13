ALTER TABLE `user`
    ADD COLUMN `notification_features` TEXT NULL DEFAULT NULL COMMENT 'comma separated list' COLLATE 'utf8mb3_unicode_ci' AFTER `e_mail`;