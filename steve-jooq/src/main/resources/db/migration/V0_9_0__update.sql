--
-- store connector_pk in reservation table.
--

START TRANSACTION;

-- add column without constraints

ALTER TABLE `reservation`
ADD COLUMN `connector_pk` INT(11) UNSIGNED AFTER `reservation_pk`;


-- set connector_pk of existing reservations to connector 0 of the corresponding charge box.

UPDATE `reservation`
SET `connector_pk` = (
  SELECT `connector`.`connector_pk`
  FROM `connector`
  WHERE `connector`.`charge_box_id` = `reservation`.`charge_box_id` AND `connector`.`connector_id` = 0
);


-- now that all connector_pk columns have values set, add constraints

ALTER TABLE `reservation`
  MODIFY COLUMN `connector_pk` INT(11) UNSIGNED NOT NULL AFTER `reservation_pk`,
  ADD INDEX `FK_connector_pk_reserv_idx` (`connector_pk` ASC);

ALTER TABLE `reservation`
ADD CONSTRAINT `FK_connector_pk_reserv` FOREIGN KEY (`connector_pk`)
REFERENCES `connector` (`connector_pk`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;


-- charge_box_id column is redundant, remove it.

ALTER TABLE `reservation`
  DROP FOREIGN KEY `FK_reservation_charge_box_cbid`,
  DROP COLUMN `charge_box_id`,
  DROP INDEX `FK_chargeBoxId_r_idx` ;

COMMIT;
