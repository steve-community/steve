START TRANSACTION;

ALTER TABLE charge_box
    ADD COLUMN security_profile INT DEFAULT 0,
    ADD COLUMN auth_password VARCHAR(500) DEFAULT NULL;

CREATE TABLE IF NOT EXISTS charge_box_security_event (
    charge_box_pk INT NOT NULL,
    `type` VARCHAR(100) NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    tech_info VARCHAR(500),
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_type (`type`),
    INDEX idx_timestamp (`timestamp`)
);

CREATE TABLE IF NOT EXISTS charge_box_firmware_update_job (
    job_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    firmware_location VARCHAR(1000) NOT NULL,
    retrieve_datetime TIMESTAMP NULL DEFAULT NULL,
    install_datetime TIMESTAMP NULL DEFAULT NULL,
    signing_certificate MEDIUMTEXT,
    signature MEDIUMTEXT
);

CREATE TABLE IF NOT EXISTS charge_box_firmware_update_event (
    job_id INT NOT NULL,
    charge_box_pk INT NOT NULL,

    event_status VARCHAR(100) NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (job_id) REFERENCES charge_box_firmware_update_job (job_id) ON DELETE CASCADE,
    FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,

    INDEX idx_job_id (job_id),
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_event_status (event_status),
    INDEX idx_event_timestamp (event_timestamp)
);

CREATE TABLE IF NOT EXISTS charge_box_log_upload_job (
    job_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    log_type VARCHAR(100) NOT NULL,
    remote_location VARCHAR(1000),
    oldest_timestamp TIMESTAMP NULL DEFAULT NULL,
    latest_timestamp TIMESTAMP NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS charge_box_log_upload_event (
    job_id INT NOT NULL,
    charge_box_pk INT NOT NULL,

    event_status VARCHAR(100) NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (job_id) REFERENCES charge_box_log_upload_job (job_id) ON DELETE CASCADE,
    FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,

    INDEX idx_job_id (job_id),
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_event_status (event_status),
    INDEX idx_event_timestamp (event_timestamp)
);


CREATE OR REPLACE VIEW charge_box_status_event AS
SELECT
    job_id,
    charge_box_pk,
    event_status,
    event_timestamp,
    'FirmwareUpdate' AS event_type
FROM charge_box_firmware_update_event

UNION ALL

SELECT
    job_id,
    charge_box_pk,
    event_status,
    event_timestamp,
    'LogUpload' AS event_type
FROM charge_box_log_upload_event;

CREATE TABLE IF NOT EXISTS certificate (
    certificate_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    serial_number VARCHAR(255),
    issuer_name VARCHAR(500),
    subject_name VARCHAR(500),
    organization_name VARCHAR(500),
    common_name VARCHAR(500),
    key_size INT,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    signature_algorithm ENUM('RSA', 'ECDSA'),
    certificate_chain_pem MEDIUMTEXT NOT NULL,

    INDEX idx_serial_number (serial_number),
    INDEX idx_created_at (created_at),
    INDEX idx_valid_from (valid_from),
    INDEX idx_valid_to (valid_to)
);

CREATE TABLE IF NOT EXISTS charge_box_certificate (
    certificate_id INT NOT NULL,
    charge_box_pk INT NOT NULL,
    accepted BOOL NOT NULL,
    responded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (certificate_id) REFERENCES certificate (certificate_id) ON DELETE CASCADE,
    FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS charge_box_certificate_installed (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- synthetic PK to be able to identify a row (for ex for deletions)
    charge_box_pk INT NOT NULL,
    responded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    certificate_type VARCHAR(255) NOT NULL,
    hash_algorithm VARCHAR(128) NOT NULL,
    issuer_name_hash VARCHAR(128) NOT NULL,
    issuer_key_hash VARCHAR(128) NOT NULL,
    serial_number VARCHAR(255) NOT NULL,

    FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_certificate_type (certificate_type)
);

COMMIT;
