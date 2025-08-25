UPDATE `dbVersion` SET `version` = '0.6.8';

DROP PROCEDURE `getStats`;

DELIMITER ;;
CREATE PROCEDURE `getStats`(
  OUT numChargeBoxes INT,
  OUT numUsers INT,
  OUT numReservs INT,
  OUT numTranses INT,
  OUT heartbeatToday INT,
  OUT heartbeatYester INT,
  OUT heartbeatEarl INT,
  OUT connAvail INT,
  OUT connOcc INT,
  OUT connFault INT,
  OUT connUnavail INT)
  BEGIN
    -- # of chargeboxes
    SELECT COUNT(chargeBoxId) INTO numChargeBoxes FROM chargebox;
    -- # of users
    SELECT COUNT(idTag) INTO numUsers FROM user;
    -- # of reservations
    SELECT COUNT(reservation_pk) INTO numReservs FROM reservation;
    -- # of active transactions
    SELECT COUNT(transaction_pk) INTO numTranses FROM transaction WHERE stopTimestamp IS NULL;

    -- # of today's heartbeats
    SELECT COUNT(lastHeartbeatTimestamp) INTO heartbeatToday FROM chargebox
    WHERE lastHeartbeatTimestamp >= CURDATE();
    -- # of yesterday's heartbeats
    SELECT COUNT(lastHeartbeatTimestamp) INTO heartbeatYester FROM chargebox
    WHERE lastHeartbeatTimestamp >= DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND lastHeartbeatTimestamp < CURDATE();
    -- # of earlier heartbeats
    SELECT COUNT(lastHeartbeatTimestamp) INTO heartbeatEarl FROM chargebox
    WHERE lastHeartbeatTimestamp < DATE_SUB(CURDATE(), INTERVAL 1 DAY);

    -- # of latest AVAILABLE statuses
    SELECT COUNT(cs.status) INTO connAvail
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(statusTimestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.statusTimestamp = t1.Max
    WHERE cs.status = 'AVAILABLE';

    -- # of latest OCCUPIED statuses
    SELECT COUNT(cs.status) INTO connOcc
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(statusTimestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.statusTimestamp = t1.Max
    WHERE cs.status = 'OCCUPIED';

    -- # of latest FAULTED statuses
    SELECT COUNT(cs.status) INTO connFault
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(statusTimestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.statusTimestamp = t1.Max
    WHERE cs.status = 'FAULTED';

    -- # of latest UNAVAILABLE statuses
    SELECT COUNT(cs.status) INTO connUnavail
    FROM connector_status cs
      INNER JOIN (SELECT connector_pk, MAX(statusTimestamp) AS Max FROM connector_status GROUP BY connector_pk)
        AS t1 ON cs.connector_pk = t1.connector_pk AND cs.statusTimestamp = t1.Max
    WHERE cs.status = 'UNAVAILABLE';
  END ;;
DELIMITER ;
