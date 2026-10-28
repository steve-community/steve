ALTER TABLE charge_box
    ADD COLUMN last_pong_timestamp TIMESTAMP(6) NULL DEFAULT NULL
        AFTER last_heartbeat_timestamp;
