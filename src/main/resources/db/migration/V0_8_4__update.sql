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

--
-- update the procedure
--

DROP PROCEDURE IF EXISTS `get_stats`;

DELIMITER ;;
CREATE PROCEDURE `get_stats`(
  OUT num_charge_boxes INT,
  OUT num_users INT,
  OUT num_reservations INT,
  OUT num_transactions INT,
  OUT heartbeats_today INT,
  OUT heartbeats_yesterday INT,
  OUT heartbeats_earlier INT,
  OUT connectors_available INT,
  OUT connectors_occupied INT,
  OUT connectors_faulted INT,
  OUT connectors_unavailable INT)
  BEGIN
    -- # of chargeboxes
    SELECT COUNT(charge_box_id) INTO num_charge_boxes FROM charge_box;
    -- # of users
    SELECT COUNT(user_pk) INTO num_users FROM `user`;
    -- # of reservations
    SELECT COUNT(reservation_pk) INTO num_reservations FROM reservation WHERE expiry_datetime > CURRENT_TIMESTAMP AND `status` = 'Accepted';
    -- # of active transactions
    SELECT COUNT(transaction_pk) INTO num_transactions FROM `transaction` WHERE stop_timestamp IS NULL;

    -- # of today's heartbeats
    SELECT COUNT(last_heartbeat_timestamp) INTO heartbeats_today FROM charge_box
    WHERE last_heartbeat_timestamp >= CURDATE();
    -- # of yesterday's heartbeats
    SELECT COUNT(last_heartbeat_timestamp) INTO heartbeats_yesterday FROM charge_box
    WHERE last_heartbeat_timestamp >= DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND last_heartbeat_timestamp < CURDATE();
    -- # of earlier heartbeats
    SELECT COUNT(last_heartbeat_timestamp) INTO heartbeats_earlier FROM charge_box
    WHERE last_heartbeat_timestamp < DATE_SUB(CURDATE(), INTERVAL 1 DAY);

    -- # of latest AVAILABLE statuses
    SELECT COUNT(cs.status) INTO connectors_available
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(status_timestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.status_timestamp = t1.Max
    WHERE cs.status = 'AVAILABLE';

    -- # of latest OCCUPIED statuses
    SELECT COUNT(cs.status) INTO connectors_occupied
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(status_timestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.status_timestamp = t1.Max
    WHERE cs.status = 'OCCUPIED';

    -- # of latest FAULTED statuses
    SELECT COUNT(cs.status) INTO connectors_faulted
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(status_timestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.status_timestamp = t1.Max
    WHERE cs.status = 'FAULTED';

    -- # of latest UNAVAILABLE statuses
    SELECT COUNT(cs.status) INTO connectors_unavailable
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(status_timestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.status_timestamp = t1.Max
    WHERE cs.status = 'UNAVAILABLE';
  END ;;
DELIMITER ;
