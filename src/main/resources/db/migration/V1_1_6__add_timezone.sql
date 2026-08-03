ALTER TABLE address
    ADD COLUMN time_zone VARCHAR(255) NULL
        AFTER longitude;
