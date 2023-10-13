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
    `o`.`id_tag`,
    `o`.`parent_id_tag`,
    `o`.`expiry_date`,
    `o`.`max_active_transaction_count`,
    `o`.`note`;
