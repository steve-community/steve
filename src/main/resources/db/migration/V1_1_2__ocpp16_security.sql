CREATE TABLE IF NOT EXISTS certificate (
    certificate_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    charge_box_pk INT,
    certificate_type VARCHAR(50) NOT NULL,
    certificate_data MEDIUMTEXT NOT NULL,
    serial_number VARCHAR(255),
    issuer_name VARCHAR(500),
    subject_name VARCHAR(500),
    valid_from TIMESTAMP NULL DEFAULT NULL,
    valid_to TIMESTAMP NULL DEFAULT NULL,
    signature_algorithm VARCHAR(100),
    key_size INT,
    installed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'Installed',
    CONSTRAINT FK_certificate_charge_box FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_certificate_type (certificate_type),
    INDEX idx_status (status),
    INDEX idx_serial_number (serial_number)
);

CREATE TABLE IF NOT EXISTS security_event (
    event_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    charge_box_pk INT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL,
    tech_info MEDIUMTEXT,
    severity VARCHAR(20) NOT NULL,
    received_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_security_event_charge_box FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_event_type (event_type),
    INDEX idx_event_timestamp (event_timestamp),
    INDEX idx_severity (severity)
);

CREATE TABLE IF NOT EXISTS log_file (
    log_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    charge_box_pk INT NOT NULL,
    log_type VARCHAR(50) NOT NULL,
    request_id INT NOT NULL,
    request_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(1000),
    upload_status VARCHAR(50) DEFAULT 'Pending',
    upload_timestamp TIMESTAMP NULL DEFAULT NULL,
    bytes_uploaded BIGINT,
    CONSTRAINT FK_log_file_charge_box FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_log_type (log_type),
    INDEX idx_request_id (request_id),
    INDEX idx_upload_status (upload_status)
);

CREATE TABLE IF NOT EXISTS firmware_update (
    update_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    charge_box_pk INT NOT NULL,
    firmware_location VARCHAR(1000) NOT NULL,
    firmware_signature MEDIUMTEXT,
    retrieve_date TIMESTAMP NULL DEFAULT NULL,
    install_date TIMESTAMP NULL DEFAULT NULL,
    signing_certificate MEDIUMTEXT,
    signature_algorithm VARCHAR(100),
    request_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Pending',
    CONSTRAINT FK_firmware_update_charge_box FOREIGN KEY (charge_box_pk) REFERENCES charge_box (charge_box_pk) ON DELETE CASCADE,
    INDEX idx_charge_box_pk (charge_box_pk),
    INDEX idx_status (status),
    INDEX idx_retrieve_date (retrieve_date)
);

ALTER TABLE charge_box ADD COLUMN security_profile INT DEFAULT 0;
ALTER TABLE charge_box ADD COLUMN authorization_key VARCHAR(100);
ALTER TABLE charge_box ADD COLUMN cpo_name VARCHAR(255);
ALTER TABLE charge_box ADD COLUMN certificate_store_max_length INT DEFAULT 0;
ALTER TABLE charge_box ADD COLUMN additional_root_certificate_check BOOLEAN DEFAULT FALSE;