--
-- add some indexes
--

ALTER TABLE `chargebox` ADD INDEX `chargebox_op_ep_idx` (`ocppProtocol`, `endpoint_address`);

ALTER TABLE `connector_status` ADD INDEX `connector_status_cpk_st_idx` (`connector_pk`, `statusTimestamp`);

ALTER TABLE `user`
ADD INDEX `user_expiryDate_idx` (`expiryDate`),
ADD INDEX `user_inTransaction_idx` (`inTransaction`),
ADD INDEX `user_blocked_idx` (`blocked`);

ALTER TABLE `reservation`
ADD INDEX `reservation_start_idx` (`startDatetime`),
ADD INDEX `reservation_expiry_idx` (`expiryDatetime`),
ADD INDEX `reservation_status_idx` (`status`);

ALTER TABLE `transaction`
ADD INDEX `transaction_start_idx` (`startTimestamp`),
ADD INDEX `transaction_stop_idx` (`stopTimestamp`);
