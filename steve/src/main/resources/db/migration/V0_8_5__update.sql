CREATE TABLE address (
  address_pk INT NOT NULL AUTO_INCREMENT,
  street_and_house_number varchar(1000),
  zip_code varchar(255),
  city varchar(255),
  country varchar(255),
  PRIMARY KEY (address_pk)
);

CREATE TABLE user (
  user_pk INT NOT NULL AUTO_INCREMENT,
  ocpp_tag_pk INT DEFAULT NULL,
  address_pk INT DEFAULT NULL,
  first_name varchar(255) NULL,
  last_name varchar(255) NULL,
  birth_day DATE,
  sex CHAR(1),
  phone varchar(255) NULL,
  e_mail varchar(255) NULL,
  note TEXT NULL,
  PRIMARY KEY (user_pk)
);

ALTER TABLE `charge_box`
ADD description TEXT AFTER last_heartbeat_timestamp,
ADD location_latitude DECIMAL(11,8) NULL,
ADD location_longitude DECIMAL(11,8) NULL;

ALTER TABLE `charge_box`
ADD address_pk INT DEFAULT NULL,
ADD CONSTRAINT FK_charge_box_address_apk
FOREIGN KEY (address_pk) REFERENCES address (address_pk) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `user`
ADD CONSTRAINT `FK_user_ocpp_tag_otpk`
FOREIGN KEY (`ocpp_tag_pk`) REFERENCES `ocpp_tag` (`ocpp_tag_pk`) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `user`
ADD CONSTRAINT FK_user_address_apk
FOREIGN KEY (address_pk) REFERENCES address (address_pk) ON DELETE SET NULL ON UPDATE NO ACTION;

--
-- update the procedure
--

DROP PROCEDURE IF EXISTS `get_stats`;

DELIMITER ;;
CREATE PROCEDURE `get_stats`(
  OUT num_charge_boxes INT,
  OUT num_ocpp_tags INT,
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
    -- we can compute these once, and reuse the instances in the following queries,
    -- instead of calculating them every time
    --
    DECLARE today DATE DEFAULT CURRENT_DATE();
    DECLARE yesterday DATE DEFAULT DATE_SUB(today, INTERVAL 1 DAY);

    -- # of chargeboxes
    SELECT COUNT(charge_box_id) INTO num_charge_boxes FROM charge_box;
    -- # of ocpp tags
    SELECT COUNT(ocpp_tag_pk) INTO num_ocpp_tags FROM ocpp_tag;
    -- # of users
    SELECT COUNT(user_pk) INTO num_users FROM `user`;
    -- # of reservations
    SELECT COUNT(reservation_pk) INTO num_reservations FROM reservation WHERE expiry_datetime > CURRENT_TIMESTAMP AND `status` = 'Accepted';
    -- # of active transactions
    SELECT COUNT(transaction_pk) INTO num_transactions FROM `transaction` WHERE stop_timestamp IS NULL;

    -- # of today's heartbeats
    SELECT COUNT(last_heartbeat_timestamp) INTO heartbeats_today FROM charge_box
    WHERE DATE(last_heartbeat_timestamp) = today;
    -- # of yesterday's heartbeats
    SELECT COUNT(last_heartbeat_timestamp) INTO heartbeats_yesterday FROM charge_box
    WHERE DATE(last_heartbeat_timestamp) = yesterday;
    -- # of earlier heartbeats
    SELECT COUNT(last_heartbeat_timestamp) INTO heartbeats_earlier FROM charge_box
    WHERE DATE(last_heartbeat_timestamp) < yesterday;

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
