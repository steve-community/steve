-- OCPP 2.0.1 Base Schema
-- Core tables for OCPP 2.0 protocol support

-- OCPP 2.0 Boot Notifications
CREATE TABLE IF NOT EXISTS ocpp20_boot_notification (
    boot_notification_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    charge_box_pk INT NOT NULL,
    timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    -- Charging Station Info
    charging_station_vendor VARCHAR(50) NOT NULL,
    charging_station_model VARCHAR(50) NOT NULL,
    charging_station_serial_number VARCHAR(25),
    firmware_version VARCHAR(50),

    -- Modem Info (optional)
    modem_iccid VARCHAR(20),
    modem_imsi VARCHAR(20),

    -- Boot Reason
    boot_reason VARCHAR(50) NOT NULL,

    -- Response
    status VARCHAR(50) NOT NULL,
    response_time TIMESTAMP(6) NULL DEFAULT NULL,
    interval_seconds INT,

    PRIMARY KEY (boot_notification_pk),
    CONSTRAINT fk_ocpp20_boot_charge_box
        FOREIGN KEY (charge_box_pk)
        REFERENCES charge_box(charge_box_pk)
        ON DELETE CASCADE,
    INDEX idx_charge_box_timestamp (charge_box_pk, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OCPP 2.0 Variables (Device Model)
CREATE TABLE IF NOT EXISTS ocpp20_variable (
    variable_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    charge_box_pk INT NOT NULL,
    component_name VARCHAR(50) NOT NULL,
    component_instance VARCHAR(50),
    component_evse_id INT,
    component_evse_connector_id INT,
    variable_name VARCHAR(50) NOT NULL,
    variable_instance VARCHAR(50),

    PRIMARY KEY (variable_pk),
    CONSTRAINT fk_ocpp20_variable_charge_box
        FOREIGN KEY (charge_box_pk)
        REFERENCES charge_box(charge_box_pk)
        ON DELETE CASCADE,
    UNIQUE KEY unique_variable (charge_box_pk, component_name, component_instance,
                                component_evse_id, component_evse_connector_id,
                                variable_name, variable_instance),
    INDEX idx_charge_box_component (charge_box_pk, component_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OCPP 2.0 Variable Attributes
CREATE TABLE IF NOT EXISTS ocpp20_variable_attribute (
    attribute_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    variable_pk INT UNSIGNED NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'Actual',
    value TEXT,
    mutability VARCHAR(20),
    persistent BOOLEAN,
    constant BOOLEAN,
    last_updated TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (attribute_pk),
    CONSTRAINT fk_ocpp20_attribute_variable
        FOREIGN KEY (variable_pk)
        REFERENCES ocpp20_variable(variable_pk)
        ON DELETE CASCADE,
    UNIQUE KEY unique_attribute (variable_pk, type),
    INDEX idx_variable_type (variable_pk, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OCPP 2.0 Transactions
CREATE TABLE IF NOT EXISTS ocpp20_transaction (
    transaction_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    charge_box_pk INT NOT NULL,
    transaction_id VARCHAR(36) NOT NULL,
    evse_id INT NOT NULL,
    connector_id INT,

    -- ID Token (nullable per OCPP 2.0.1 spec - idToken is optional in TransactionEvent)
    id_token VARCHAR(36) NULL,
    id_token_type VARCHAR(50) NULL,

    -- Transaction State
    started_at TIMESTAMP(6) NOT NULL,
    stopped_at TIMESTAMP(6) NULL DEFAULT NULL,
    stopped_reason VARCHAR(50),

    -- Meter Values
    start_meter_value DECIMAL(15,3),
    stop_meter_value DECIMAL(15,3),

    -- Remote Start
    remote_start_id INT,

    PRIMARY KEY (transaction_pk),
    CONSTRAINT fk_ocpp20_transaction_charge_box
        FOREIGN KEY (charge_box_pk)
        REFERENCES charge_box(charge_box_pk)
        ON DELETE CASCADE,
    UNIQUE KEY unique_transaction (charge_box_pk, transaction_id),
    INDEX idx_charge_box_evse (charge_box_pk, evse_id),
    INDEX idx_id_token (id_token),
    INDEX idx_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OCPP 2.0 Transaction Events
CREATE TABLE IF NOT EXISTS ocpp20_transaction_event (
    event_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    transaction_pk INT UNSIGNED NOT NULL,
    charge_box_pk INT NOT NULL,

    -- Event Info
    timestamp TIMESTAMP(6) NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    trigger_reason VARCHAR(50) NOT NULL,
    seq_no INT NOT NULL,

    -- Transaction Info
    transaction_id VARCHAR(36) NOT NULL,

    -- ID Token
    id_token VARCHAR(36),
    id_token_type VARCHAR(50),

    -- EVSE
    evse_id INT,
    connector_id INT,

    -- Meter Value
    meter_value DECIMAL(15,3),
    meter_value_unit VARCHAR(20),
    meter_value_context VARCHAR(20),
    meter_value_measurand VARCHAR(50),

    -- Charging State
    charging_state VARCHAR(50),

    PRIMARY KEY (event_pk),
    CONSTRAINT fk_ocpp20_event_transaction
        FOREIGN KEY (transaction_pk)
        REFERENCES ocpp20_transaction(transaction_pk)
        ON DELETE CASCADE,
    CONSTRAINT fk_ocpp20_event_charge_box
        FOREIGN KEY (charge_box_pk)
        REFERENCES charge_box(charge_box_pk)
        ON DELETE CASCADE,
    INDEX idx_transaction_timestamp (transaction_pk, timestamp),
    INDEX idx_charge_box_timestamp (charge_box_pk, timestamp),
    INDEX idx_event_type (event_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OCPP 2.0 Authorization Cache
CREATE TABLE IF NOT EXISTS ocpp20_authorization (
    authorization_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    charge_box_pk INT NOT NULL,
    id_token VARCHAR(36) NOT NULL,
    id_token_type VARCHAR(50) NOT NULL,

    -- Authorization Info
    status VARCHAR(50) NOT NULL,
    cache_expiry_date TIMESTAMP(6) NULL DEFAULT NULL,

    -- Personal Message
    message_format VARCHAR(20),
    message_language VARCHAR(8),
    message_content VARCHAR(512),

    -- Group ID Token
    group_id_token VARCHAR(36),

    -- Cached at
    cached_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    last_used TIMESTAMP(6) NULL DEFAULT NULL,

    PRIMARY KEY (authorization_pk),
    CONSTRAINT fk_ocpp20_auth_charge_box
        FOREIGN KEY (charge_box_pk)
        REFERENCES charge_box(charge_box_pk)
        ON DELETE CASCADE,
    UNIQUE KEY unique_id_token (charge_box_pk, id_token, id_token_type),
    INDEX idx_cache_expiry (cache_expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OCPP 2.0 Charging Profiles (Smart Charging)
CREATE TABLE IF NOT EXISTS ocpp20_charging_profile (
    profile_pk INT UNSIGNED NOT NULL AUTO_INCREMENT,
    charge_box_pk INT NOT NULL,
    profile_id INT NOT NULL,
    stack_level INT NOT NULL,
    charging_profile_purpose VARCHAR(50) NOT NULL,
    charging_profile_kind VARCHAR(50) NOT NULL,

    -- Schedule
    recurrency_kind VARCHAR(20),
    valid_from TIMESTAMP(6) NULL DEFAULT NULL,
    valid_to TIMESTAMP(6) NULL DEFAULT NULL,

    -- Transaction
    transaction_pk INT UNSIGNED,

    -- Profile Data (JSON)
    charging_schedule JSON NOT NULL,

    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (profile_pk),
    CONSTRAINT fk_ocpp20_profile_charge_box
        FOREIGN KEY (charge_box_pk)
        REFERENCES charge_box(charge_box_pk)
        ON DELETE CASCADE,
    CONSTRAINT fk_ocpp20_profile_transaction
        FOREIGN KEY (transaction_pk)
        REFERENCES ocpp20_transaction(transaction_pk)
        ON DELETE CASCADE,
    UNIQUE KEY unique_profile (charge_box_pk, profile_id),
    INDEX idx_charge_box_purpose (charge_box_pk, charging_profile_purpose),
    INDEX idx_valid_period (valid_from, valid_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add OCPP protocol version to charge_box table (IF NOT EXISTS requires MySQL 8.0.12+)
ALTER TABLE charge_box
ADD COLUMN ocpp_version VARCHAR(10) DEFAULT '1.6' AFTER ocpp_protocol;

-- Add OCPP 2.0 specific fields to charge_box
ALTER TABLE charge_box
ADD COLUMN ocpp20_enabled BOOLEAN DEFAULT FALSE AFTER ocpp_version;

UPDATE charge_box
SET ocpp_version = '1.6'
WHERE ocpp_protocol IS NOT NULL AND ocpp_version IS NULL;