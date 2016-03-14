--
-- the proc does not include status counts anymore, because we handle them separately
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
  OUT heartbeats_earlier INT)
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

  END ;;
DELIMITER ;
