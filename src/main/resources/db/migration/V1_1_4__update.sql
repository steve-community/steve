START TRANSACTION;

--
-- Step 1: Refactor existing connector
-- * CONNECTOR table to EVSE
-- * CONNECTOR_PK/CONNECTOR_ID columns to EVSE_PK/EVSE_ID columns in EVSE table
-- * CONNECTOR_PK references to EVSE_PK in other tables
--

DROP VIEW IF EXISTS `transaction`;

ALTER TABLE connector_charging_profile
    DROP FOREIGN KEY FK_connector_charging_profile_connector_pk;

ALTER TABLE connector_meter_value
    DROP FOREIGN KEY FK_pk_cm;

ALTER TABLE connector_status
    DROP FOREIGN KEY FK_cs_pk;

ALTER TABLE reservation
    DROP FOREIGN KEY FK_connector_pk_reserv;

ALTER TABLE transaction_start
    DROP FOREIGN KEY FK_connector_pk_t;

ALTER TABLE connector
    DROP FOREIGN KEY FK_connector_charge_box_cbid;

RENAME TABLE connector TO evse;

ALTER TABLE evse
    CHANGE COLUMN connector_pk evse_pk int(11) unsigned NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN connector_id evse_id int(11) NOT NULL,
    ADD COLUMN evse_id_external varchar(255) DEFAULT NULL AFTER evse_id,
    ADD COLUMN topology_source enum('ocpp1', 'ocpp2') NOT NULL DEFAULT 'ocpp1' AFTER evse_id_external,
    ADD COLUMN created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    DROP INDEX connector_pk_UNIQUE,
    DROP INDEX connector_cbid_cid_UNIQUE,
    ADD UNIQUE KEY evse_pk_UNIQUE (evse_pk),
    ADD UNIQUE KEY evse_cbid_source_eid_UNIQUE (charge_box_id, topology_source, evse_id),
    ADD CONSTRAINT FK_evse_charge_box_cbid FOREIGN KEY (charge_box_id)
        REFERENCES charge_box (charge_box_id) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE evse
    ALTER COLUMN topology_source DROP DEFAULT;

ALTER TABLE connector_charging_profile
    CHANGE COLUMN connector_pk evse_pk int(11) unsigned NOT NULL,
    DROP INDEX UQ_connector_charging_profile,
    ADD UNIQUE KEY UQ_connector_charging_profile (evse_pk, charging_profile_pk),
    ADD CONSTRAINT FK_connector_charging_profile_evse_pk FOREIGN KEY (evse_pk)
        REFERENCES evse (evse_pk) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE connector_meter_value
    CHANGE COLUMN connector_pk evse_pk int(11) unsigned NOT NULL,
    DROP INDEX FK_cm_pk_idx,
    ADD KEY FK_cmv_evse_pk_idx (evse_pk),
    ADD CONSTRAINT FK_cmv_evse_pk FOREIGN KEY (evse_pk)
        REFERENCES evse (evse_pk) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE connector_status
    CHANGE COLUMN connector_pk evse_pk int(11) unsigned NOT NULL,
    DROP INDEX FK_cs_pk_idx,
    DROP INDEX connector_status_cpk_st_idx,
    ADD KEY FK_cs_evse_pk_idx (evse_pk),
    ADD KEY connector_status_epk_st_idx (evse_pk, status_timestamp),
    ADD CONSTRAINT FK_cs_evse_pk FOREIGN KEY (evse_pk)
        REFERENCES evse (evse_pk) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE reservation
    CHANGE COLUMN connector_pk evse_pk int(11) unsigned NOT NULL AFTER reservation_pk,
    DROP INDEX FK_connector_pk_reserv_idx,
    ADD KEY FK_evse_pk_reserv_idx (evse_pk),
    ADD CONSTRAINT FK_evse_pk_reserv FOREIGN KEY (evse_pk)
        REFERENCES evse (evse_pk) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE transaction_start
    CHANGE COLUMN connector_pk evse_pk int(11) unsigned NOT NULL,
    DROP INDEX connector_pk_idx,
    ADD KEY evse_pk_idx (evse_pk),
    ADD CONSTRAINT FK_evse_pk_t FOREIGN KEY (evse_pk)
        REFERENCES evse (evse_pk) ON DELETE CASCADE ON UPDATE NO ACTION;

CREATE OR REPLACE VIEW `transaction` AS
SELECT
    tx1.transaction_pk,
    tx1.evse_pk,
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
    AND tx2.event_timestamp = (SELECT MAX(event_timestamp) FROM transaction_stop s2 WHERE tx2.transaction_pk = s2.transaction_pk);

COMMIT;
