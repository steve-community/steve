ALTER TABLE charge_box
    CHANGE COLUMN last_heartbeat_timestamp last_ocpp_message_timestamp TIMESTAMP(6) NULL DEFAULT NULL;
