DROP INDEX user_inTransaction_idx ON ocpp_tag;

ALTER TABLE ocpp_tag DROP COLUMN in_transaction;

CREATE OR REPLACE VIEW ocpp_tag_activity AS
    SELECT
      ocpp_tag.*,
      COALESCE(tx_activity.active_transaction_count, 0) as 'active_transaction_count',
      CASE WHEN (active_transaction_count > 0) THEN TRUE ELSE FALSE END AS 'in_transaction'
    FROM ocpp_tag
    LEFT JOIN
    (SELECT id_tag, count(id_tag) as 'active_transaction_count'
      FROM transaction
      WHERE stop_timestamp IS NULL
      AND stop_value IS NULL
      GROUP BY id_tag) tx_activity
    ON ocpp_tag.id_tag = tx_activity.id_tag;
