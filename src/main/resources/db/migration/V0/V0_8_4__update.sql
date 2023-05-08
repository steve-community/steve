--
-- drop foreign keys
--

ALTER TABLE `user` DROP FOREIGN KEY `FK_user_parent_id_tag`;
ALTER TABLE `reservation` DROP FOREIGN KEY `FK_reservation_user_id_tag`;
ALTER TABLE `transaction` DROP FOREIGN KEY `FK_transaction_user_id_tag`;

-- -------------------------------------------------------------------------
-- START: rename table "user" to "ocpp_tag"
-- -------------------------------------------------------------------------

RENAME TABLE `user` TO `ocpp_tag`;

ALTER TABLE `ocpp_tag`
CHANGE COLUMN `user_pk` `ocpp_tag_pk` INT(11) NOT NULL AUTO_INCREMENT COMMENT '';

-- -------------------------------------------------------------------------
-- END: rename table "user" to "ocpp_tag"
-- -------------------------------------------------------------------------

--
-- add foreign keys back
--

ALTER TABLE `ocpp_tag`
ADD CONSTRAINT `FK_ocpp_tag_parent_id_tag`
FOREIGN KEY (`parent_id_tag`)
REFERENCES `ocpp_tag` (`id_tag`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `reservation`
ADD CONSTRAINT `FK_reservation_ocpp_tag_id_tag`
FOREIGN KEY (`id_tag`)
REFERENCES `ocpp_tag` (`id_tag`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `transaction`
ADD CONSTRAINT `FK_transaction_ocpp_tag_id_tag`
FOREIGN KEY (`id_tag`)
REFERENCES `ocpp_tag` (`id_tag`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

--
-- update the triggers
--

DELIMITER $$
DROP TRIGGER IF EXISTS `transaction_AINS`$$
CREATE TRIGGER `transaction_AINS` AFTER INSERT ON `transaction` FOR EACH ROW
  UPDATE `ocpp_tag`
  SET `ocpp_tag`.`in_transaction` = 1
  WHERE `ocpp_tag`.`id_tag` = NEW.`id_tag`$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS `transaction_AUPD`$$
CREATE TRIGGER `transaction_AUPD` AFTER UPDATE ON `transaction` FOR EACH ROW
  UPDATE `ocpp_tag`
  SET `ocpp_tag`.`in_transaction` = 0
  WHERE `ocpp_tag`.`id_tag` = NEW.`id_tag`$$
DELIMITER ;
