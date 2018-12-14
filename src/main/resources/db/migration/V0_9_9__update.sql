START TRANSACTION;

ALTER TABLE `ocpp_tag`
  ADD `max_active_transaction_count` INTEGER NOT NULL DEFAULT 1 AFTER `expiry_date`;

UPDATE `ocpp_tag` SET `max_active_transaction_count` = 0 WHERE `blocked` = TRUE;

ALTER TABLE `ocpp_tag` DROP COLUMN `blocked`;

-- recreate this view, with derived "blocked" field to be transparent to java app
CREATE OR REPLACE VIEW ocpp_tag_activity AS
    SELECT
      ocpp_tag.*,
      COALESCE(tx_activity.active_transaction_count, 0) as 'active_transaction_count',
      CASE WHEN (active_transaction_count > 0) THEN TRUE ELSE FALSE END AS 'in_transaction',
      CASE WHEN (ocpp_tag.max_active_transaction_count = 0) THEN TRUE ELSE FALSE END AS 'blocked'
    FROM ocpp_tag
    LEFT JOIN
    (SELECT id_tag, count(id_tag) as 'active_transaction_count'
      FROM transaction
      WHERE stop_timestamp IS NULL
      AND stop_value IS NULL
      GROUP BY id_tag) tx_activity
    ON ocpp_tag.id_tag = tx_activity.id_tag;

COMMIT;
