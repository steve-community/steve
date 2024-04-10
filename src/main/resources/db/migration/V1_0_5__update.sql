-- came with https://github.com/steve-community/steve/issues/1219
CREATE OR REPLACE VIEW ocpp_tag_activity AS
select `o`.*,
       count(`t`.`id_tag`)                                                AS `active_transaction_count`,
       case when count(`t`.`id_tag`) > 0 then 1 else 0 end                AS `in_transaction`,
       case when `o`.`max_active_transaction_count` = 0 then 1 else 0 end AS `blocked`
from `ocpp_tag` `o` left join `transaction` `t` on (
    `o`.`id_tag` = `t`.`id_tag` and
    `t`.`stop_timestamp` is null and
    `t`.`stop_value` is null)
group by
    `o`.`ocpp_tag_pk`,
    `o`.`parent_id_tag`,
    `o`.`expiry_date`,
    `o`.`max_active_transaction_count`,
    `o`.`note`;

CREATE OR REPLACE VIEW `transaction` AS
SELECT
    tx1.transaction_pk,
    tx1.connector_pk,
    tx1.id_tag,
    tx1.event_timestamp as 'start_event_timestamp',
    tx1.start_timestamp,
    tx1.start_value,
    tx2.event_actor as 'stop_event_actor',
    tx2.event_timestamp as 'stop_event_timestamp',
    tx2.stop_timestamp,
    tx2.stop_value,
    tx2.stop_reason
FROM transaction_start tx1
LEFT JOIN transaction_stop tx2
    ON tx1.transaction_pk = tx2.transaction_pk
    AND tx2.event_timestamp = (SELECT MAX(event_timestamp) FROM transaction_stop s2 WHERE tx2.transaction_pk = s2.transaction_pk)
