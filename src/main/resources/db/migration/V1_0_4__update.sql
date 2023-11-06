START TRANSACTION;

ALTER TABLE `ocpp_tag`
    ADD `whitelist` ENUM('always', 'allowed', 'allowed_offline', 'never') DEFAULT 'allowed_offline' NOT NULL AFTER `note`;

-- recreate this view, with derived "whitelist" field to be transparent to java app
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
