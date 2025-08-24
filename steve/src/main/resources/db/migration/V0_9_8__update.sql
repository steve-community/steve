START TRANSACTION;

ALTER TABLE `transaction`
  ADD `event_timestamp` TIMESTAMP(6) AFTER `transaction_pk`;

-- for backwards compatibility and existing data
UPDATE `transaction` SET `event_timestamp` = `start_timestamp`;

-- now that the values are set, add constraints
ALTER TABLE `transaction`
  MODIFY COLUMN `event_timestamp` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `transaction_pk`;

CREATE TABLE transaction_stop (
  transaction_pk INT(10) UNSIGNED NOT NULL,
  event_timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  event_actor ENUM('station', 'manual'),
  stop_timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  stop_value VARCHAR(255) NOT NULL,
  stop_reason VARCHAR(255),
  PRIMARY KEY(transaction_pk, event_timestamp)
);

ALTER TABLE `transaction_stop`
ADD CONSTRAINT `FK_transaction_stop_transaction_pk`
FOREIGN KEY (`transaction_pk`) REFERENCES `transaction` (`transaction_pk`) ON DELETE CASCADE ON UPDATE NO ACTION;

-- move data from transaction table to transaction_stop table
INSERT INTO `transaction_stop` (transaction_pk, event_timestamp, event_actor, stop_timestamp, stop_value, stop_reason)
SELECT t.transaction_pk, t.stop_timestamp, 'station', t.stop_timestamp, t.stop_value, t.stop_reason
  FROM `transaction` t
  WHERE t.stop_value IS NOT NULL AND t.stop_timestamp IS NOT NULL;

-- now that we moved the data, drop redundant columns
ALTER TABLE `transaction`
  DROP COLUMN `stop_timestamp`,
  DROP COLUMN `stop_value`,
  DROP COLUMN `stop_reason`,
  DROP INDEX `transaction_stop_idx`;

-- rename old table
RENAME TABLE `transaction` TO `transaction_start`;

-- reconstruct `transaction` as a view for database changes to be transparent to java app
-- select LATEST stop transaction events when joining
CREATE OR REPLACE VIEW `transaction` AS
 SELECT
  tx1.transaction_pk, tx1.connector_pk, tx1.id_tag, tx1.event_timestamp as 'start_event_timestamp', tx1.start_timestamp, tx1.start_value,
  tx2.event_actor as 'stop_event_actor', tx2.event_timestamp as 'stop_event_timestamp', tx2.stop_timestamp, tx2.stop_value, tx2.stop_reason
  FROM transaction_start tx1
  LEFT JOIN (
    SELECT s1.*
    FROM transaction_stop s1
    WHERE s1.event_timestamp = (SELECT MAX(event_timestamp) FROM transaction_stop s2 WHERE s1.transaction_pk = s2.transaction_pk)
    GROUP BY s1.transaction_pk, s1.event_timestamp) tx2
  ON tx1.transaction_pk = tx2.transaction_pk;

COMMIT;
