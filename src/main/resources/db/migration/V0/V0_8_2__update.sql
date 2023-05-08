--
-- change names from "camel case" to "snake case"
--

DROP PROCEDURE `getStats`;

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
    SELECT COUNT(id_tag) INTO num_users FROM `user`;
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
