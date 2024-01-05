ALTER TABLE `transaction_stop_failed`
    ADD COLUMN `charge_box_id` varchar(255) default null AFTER `transaction_pk`;
