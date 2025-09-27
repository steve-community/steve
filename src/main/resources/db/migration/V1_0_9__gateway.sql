CREATE TABLE IF NOT EXISTS gateway_config (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    protocol ENUM('OCPI', 'OICP') NOT NULL,
    version VARCHAR(10) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    endpoint_url VARCHAR(255),
    party_id VARCHAR(3),
    country_code VARCHAR(2),
    api_key VARCHAR(255),
    token VARCHAR(255),
    last_sync TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY unique_protocol_version (protocol, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS gateway_partner (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    protocol ENUM('OCPI', 'OICP') NOT NULL,
    party_id VARCHAR(3),
    country_code VARCHAR(2),
    endpoint_url VARCHAR(255),
    token VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role ENUM('CPO', 'EMSP', 'HUB') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY unique_partner (protocol, party_id, country_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS gateway_session_mapping (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    transaction_pk INT UNSIGNED NOT NULL,
    protocol ENUM('OCPI', 'OICP') NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    partner_id INT UNSIGNED,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY unique_transaction_protocol (transaction_pk, protocol),
    UNIQUE KEY unique_session_id (session_id),
    INDEX idx_transaction_pk (transaction_pk),
    INDEX idx_partner_id (partner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS gateway_cdr_mapping (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    transaction_pk INT UNSIGNED NOT NULL,
    protocol ENUM('OCPI', 'OICP') NOT NULL,
    cdr_id VARCHAR(36) NOT NULL,
    partner_id INT UNSIGNED,
    sent_at TIMESTAMP NULL DEFAULT NULL,
    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY unique_cdr_id (cdr_id),
    INDEX idx_transaction_pk (transaction_pk),
    INDEX idx_partner_id (partner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS gateway_token_mapping (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    ocpp_tag_pk INT UNSIGNED NOT NULL,
    protocol ENUM('OCPI', 'OICP') NOT NULL,
    token_uid VARCHAR(36) NOT NULL,
    partner_id INT UNSIGNED,
    valid BOOLEAN NOT NULL DEFAULT TRUE,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY unique_token (protocol, token_uid),
    INDEX idx_ocpp_tag_pk (ocpp_tag_pk),
    INDEX idx_partner_id (partner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;