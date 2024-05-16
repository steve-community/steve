CREATE TABLE address
(
    address_pk   INT NOT NULL AUTO_INCREMENT,
    `street`     VARCHAR(1000),
    house_number varchar(255),
    zip_code     varchar(255),
    city         varchar(255),
    country      varchar(255),
    PRIMARY KEY (address_pk)
) CHARSET = utf8
  COLLATE utf8_unicode_ci;

--
-- Table structure for table `chargebox`
--

CREATE TABLE `charge_box`
(
    `charge_box_pk`                               INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `charge_box_id`                               VARCHAR(255)   NOT NULL,
    `endpoint_address`                            VARCHAR(255)   NULL DEFAULT NULL COMMENT '',
    `ocpp_protocol`                               VARCHAR(255)   NULL DEFAULT NULL,
    `charge_point_vendor`                         VARCHAR(255)   NULL DEFAULT NULL,
    `charge_point_model`                          VARCHAR(255)   NULL DEFAULT NULL,
    `charge_point_serial_number`                  VARCHAR(255)   NULL DEFAULT NULL,
    `charge_box_serial_number`                    VARCHAR(255)   NULL DEFAULT NULL,
    `fw_version`                                  VARCHAR(255)   NULL DEFAULT NULL,
    `fw_update_status`                            VARCHAR(255)   NULL DEFAULT NULL,
    `fw_update_timestamp`                         TIMESTAMP(6)   NULL DEFAULT NULL,
    `iccid`                                       VARCHAR(255)   NULL DEFAULT NULL COMMENT '',
    `imsi`                                        VARCHAR(255)   NULL DEFAULT NULL COMMENT '',
    `meter_type`                                  VARCHAR(255)   NULL DEFAULT NULL,
    `meter_serial_number`                         VARCHAR(255)   NULL DEFAULT NULL,
    `diagnostics_status`                          VARCHAR(255)   NULL DEFAULT NULL,
    `diagnostics_timestamp`                       TIMESTAMP(6)   NULL DEFAULT NULL,
    `last_heartbeat_timestamp`                    TIMESTAMP(6)   NULL DEFAULT NULL,
    description                                   TEXT,
    location_latitude                             DECIMAL(11, 8) NULL,
    location_longitude                            DECIMAL(11, 8) NULL,
    `note`                                        TEXT           NULL COMMENT '',
    address_pk                                    INT                 DEFAULT NULL,
    admin_address                                 varchar(255)   NULL,
    insert_connector_status_after_transaction_msg BOOLEAN             DEFAULT TRUE,
    UNIQUE KEY `chargeBoxId_UNIQUE` (`charge_box_id`),
    CONSTRAINT FK_charge_box_address_apk FOREIGN KEY (address_pk) REFERENCES address (address_pk) ON DELETE SET NULL ON UPDATE NO ACTION,
    INDEX `chargebox_op_ep_idx` (`ocpp_protocol`, `endpoint_address`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

--
-- Table structure for table `user`
--

CREATE TABLE `ocpp_tag`
(
    `ocpp_tag_pk`                  INT(11)      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '',
    `id_tag`                       VARCHAR(255) NOT NULL,
    `parent_id_tag`                VARCHAR(255) NULL     DEFAULT NULL,
    `expiry_date`                  TIMESTAMP(6) NULL     DEFAULT NULL,
    `max_active_transaction_count` INTEGER      NOT NULL DEFAULT 1,
    `note`                         TEXT         NULL COMMENT '',
    UNIQUE KEY `idTag_UNIQUE` (`id_tag`),
    CONSTRAINT `FK_ocpp_tag_parent_id_tag` FOREIGN KEY (`parent_id_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    INDEX `user_expiryDate_idx` (`expiry_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

--
-- Table structure for table `connector`
--

CREATE TABLE `connector`
(
    `connector_pk`  int(11) unsigned NOT NULL AUTO_INCREMENT,
    `charge_box_id` VARCHAR(255)     NOT NULL,
    `connector_id`  INT(11)          NOT NULL,
    PRIMARY KEY (`connector_pk`),
    UNIQUE KEY `connector_pk_UNIQUE` (`connector_pk`),
    UNIQUE KEY `connector_cbid_cid_UNIQUE` (`charge_box_id`, `connector_id`),
    CONSTRAINT `FK_connector_charge_box_cbid` FOREIGN KEY (`charge_box_id`) REFERENCES `charge_box` (`charge_box_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;


--
-- Table structure for table `connector_status`
--

CREATE TABLE `connector_status`
(
    `connector_pk`      int(11) unsigned NOT NULL,
    `status_timestamp`  TIMESTAMP(6)     NULL DEFAULT NULL,
    `status`            VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `error_code`        VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `error_info`        VARCHAR(255)     NULL DEFAULT NULL,
    `vendor_id`         VARCHAR(255)     NULL DEFAULT NULL,
    `vendor_error_code` VARCHAR(255)     NULL DEFAULT NULL,
    KEY `FK_cs_pk_idx` (`connector_pk`),
    CONSTRAINT `FK_cs_pk` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
    INDEX `connector_status_cpk_st_idx` (`connector_pk`, `status_timestamp`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction_start`
(
    `transaction_pk`  int(10) unsigned NOT NULL AUTO_INCREMENT,
    `event_timestamp` TIMESTAMP(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `connector_pk`    int(11) unsigned NOT NULL,
    `id_tag`          VARCHAR(255)     NOT NULL,
    `start_timestamp` TIMESTAMP(6)     NULL     DEFAULT NULL,
    `start_value`     VARCHAR(255)     NULL     DEFAULT NULL,
    PRIMARY KEY (`transaction_pk`),
    UNIQUE KEY `transaction_pk_UNIQUE` (`transaction_pk`),
    KEY `idTag_idx` (`id_tag`),
    KEY `connector_pk_idx` (`connector_pk`),
    CONSTRAINT `FK_connector_pk_t` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `FK_transaction_ocpp_tag_id_tag` FOREIGN KEY (`id_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE CASCADE ON UPDATE NO ACTION,
    INDEX `transaction_start_idx` (`start_timestamp`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

CREATE TABLE transaction_stop
(
    transaction_pk  INT(10) UNSIGNED NOT NULL,
    event_timestamp TIMESTAMP(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    event_actor     ENUM ('station', 'manual'),
    stop_timestamp  TIMESTAMP(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    stop_value      VARCHAR(255)     NOT NULL,
    stop_reason     VARCHAR(255),
    PRIMARY KEY (transaction_pk, event_timestamp),
    CONSTRAINT `FK_transaction_stop_transaction_pk` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE transaction_stop_failed
(
    transaction_pk  INT,
    event_timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    event_actor     ENUM ('station', 'manual'),
    stop_timestamp  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    stop_value      VARCHAR(255),
    stop_reason     VARCHAR(255),
    fail_reason     TEXT
);

CREATE OR REPLACE VIEW `transaction` AS
SELECT tx1.transaction_pk,
       tx1.connector_pk,
       tx1.id_tag,
       tx1.event_timestamp as 'start_event_timestamp',
        tx1.start_timestamp,
       tx1.start_value,
       tx2.event_actor     as 'stop_event_actor',
        tx2.event_timestamp as 'stop_event_timestamp',
        tx2.stop_timestamp,
       tx2.stop_value,
       tx2.stop_reason
FROM transaction_start tx1
         LEFT JOIN (SELECT s1.*
                    FROM transaction_stop s1
                    WHERE s1.event_timestamp = (SELECT MAX(event_timestamp)
                                                FROM transaction_stop s2
                                                WHERE s1.transaction_pk = s2.transaction_pk)
                    GROUP BY s1.transaction_pk, s1.event_timestamp) tx2
                   ON tx1.transaction_pk = tx2.transaction_pk;

CREATE OR REPLACE VIEW ocpp_tag_activity AS
SELECT ocpp_tag.*,
       COALESCE(tx_activity.active_transaction_count, 0)                              as 'active_transaction_count',
        CASE WHEN (active_transaction_count > 0) THEN TRUE ELSE FALSE END              AS 'in_transaction',
        CASE WHEN (ocpp_tag.max_active_transaction_count = 0) THEN TRUE ELSE FALSE END AS 'blocked'
FROM ocpp_tag
         LEFT JOIN
     (SELECT id_tag, count(id_tag) as 'active_transaction_count'
      FROM transaction
      WHERE stop_timestamp IS NULL
        AND stop_value IS NULL
      GROUP BY id_tag) tx_activity
     ON ocpp_tag.id_tag = tx_activity.id_tag;

--
-- Table structure for table `connector_metervalue`
--

CREATE TABLE `connector_meter_value`
(
    `connector_pk`    int(11) unsigned NOT NULL,
    `transaction_pk`  int(10) unsigned      DEFAULT NULL,
    `value_timestamp` TIMESTAMP(6)     NULL DEFAULT NULL,
    `value`           VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `reading_context` VARCHAR(255)     NULL DEFAULT NULL,
    `format`          VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `measurand`       VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `location`        VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `unit`            VARCHAR(255)     NULL DEFAULT NULL COMMENT '',
    `phase`           VARCHAR(255)          DEFAULT NULL,
    KEY `FK_cm_pk_idx` (`connector_pk`),
    KEY `FK_tid_cm_idx` (`transaction_pk`),
    CONSTRAINT `FK_pk_cm` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `FK_tid_cm` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE SET NULL ON UPDATE NO ACTION,
    INDEX `cmv_value_timestamp_idx` (`value_timestamp` ASC) COMMENT ''
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation`
(
    `reservation_pk`  int(10) unsigned NOT NULL AUTO_INCREMENT,
    `connector_pk`    INT(11) UNSIGNED NOT NULL,
    `transaction_pk`  INT(10) UNSIGNED      DEFAULT NULL,
    `id_tag`          VARCHAR(255)     NOT NULL,
    `start_datetime`  DATETIME         NULL DEFAULT NULL,
    `expiry_datetime` DATETIME         NULL DEFAULT NULL,
    `status`          VARCHAR(255)     NOT NULL COMMENT '',
    PRIMARY KEY (`reservation_pk`),
    UNIQUE KEY `reservation_pk_UNIQUE` (`reservation_pk`),
    KEY `FK_idTag_r_idx` (`id_tag`),
    CONSTRAINT `FK_reservation_ocpp_tag_id_tag` FOREIGN KEY (`id_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `FK_transaction_pk_r` FOREIGN KEY (`transaction_pk`) REFERENCES `transaction_start` (`transaction_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_connector_pk_reserv` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
    UNIQUE INDEX `transaction_pk_UNIQUE` (`transaction_pk` ASC),
    INDEX `reservation_start_idx` (`start_datetime`),
    INDEX `reservation_expiry_idx` (`expiry_datetime`),
    INDEX `reservation_status_idx` (`status`),
    INDEX `FK_connector_pk_reserv_idx` (`connector_pk` ASC)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

CREATE TABLE `settings`
(
    `app_id`                        VARCHAR(40) NOT NULL,
    `heartbeat_interval_in_seconds` INT(11)     NULL DEFAULT NULL,
    `hours_to_expire`               INT(11)     NULL DEFAULT NULL,
    `mail_enabled`                  BOOLEAN          DEFAULT FALSE,
    `mail_host`                     VARCHAR(255)     DEFAULT NULL,
    `mail_username`                 VARCHAR(255)     DEFAULT NULL,
    `mail_password`                 VARCHAR(255)     DEFAULT NULL,
    `mail_from`                     VARCHAR(255)     DEFAULT NULL,
    `mail_protocol`                 VARCHAR(255)     DEFAULT 'smtp',
    `mail_port`                     INT              DEFAULT 25,
    `mail_recipients`               TEXT COMMENT 'comma separated list of email addresses',
    `notification_features`         TEXT COMMENT 'comma separated list',
    PRIMARY KEY (`app_id`),
    UNIQUE KEY `settings_id_UNIQUE` (`app_id`)
) CHARSET = utf8
  COLLATE utf8_unicode_ci;

INSERT INTO `settings` (app_id, heartbeat_interval_in_seconds, hours_to_expire)
VALUES ('U3RlY2tkb3NlblZlcndhbHR1bmc=', 14400, 1);

CREATE TABLE user
(
    user_pk     INT          NOT NULL AUTO_INCREMENT,
    ocpp_tag_pk INT DEFAULT NULL,
    address_pk  INT DEFAULT NULL,
    first_name  varchar(255) NULL,
    last_name   varchar(255) NULL,
    birth_day   DATE,
    sex         CHAR(1),
    phone       varchar(255) NULL,
    e_mail      varchar(255) NULL,
    note        TEXT         NULL,
    PRIMARY KEY (user_pk),
    CONSTRAINT `FK_user_ocpp_tag_otpk` FOREIGN KEY (`ocpp_tag_pk`) REFERENCES `ocpp_tag` (`ocpp_tag_pk`) ON DELETE SET NULL ON UPDATE NO ACTION,
    CONSTRAINT FK_user_address_apk FOREIGN KEY (address_pk) REFERENCES address (address_pk) ON DELETE SET NULL ON UPDATE NO ACTION
) CHARSET = utf8
  COLLATE utf8_unicode_ci;

CREATE TABLE charging_profile
(
    charging_profile_pk      INT            NOT NULL AUTO_INCREMENT,
    stack_level              INT            NOT NULL,
    charging_profile_purpose varchar(255)   NOT NULL,
    charging_profile_kind    varchar(255)   NOT NULL,
    recurrency_kind          varchar(255)   NULL,
    valid_from               TIMESTAMP(6)   NULL,
    valid_to                 TIMESTAMP(6)   NULL,

    duration_in_seconds      INT            NULL,
    start_schedule           TIMESTAMP(6)   NULL NULL,
    charging_rate_unit       varchar(255)   NOT NULL,
    min_charging_rate        decimal(15, 1) NULL, -- according to ocpp, at most one digit fraction.

    description              varchar(255)   null,
    note                     TEXT           null,

    PRIMARY KEY (charging_profile_pk)
);

CREATE TABLE charging_schedule_period
(
    charging_profile_pk     INT            NOT NULL,
    start_period_in_seconds INT            NOT NULL,
    power_limit_in_amperes  decimal(15, 1) NOT NULL, -- according to ocpp, at most one digit fraction.
    number_phases           INT            NULL,
    UNIQUE `UQ_charging_schedule_period` (`charging_profile_pk`, `start_period_in_seconds`),
    CONSTRAINT `FK_charging_schedule_period_charging_profile_pk` FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE connector_charging_profile
(
    connector_pk        INT(11) UNSIGNED NOT NULL,
    charging_profile_pk INT              NOT NULL,
    UNIQUE `UQ_connector_charging_profile` (`connector_pk`, `charging_profile_pk`),
    CONSTRAINT `FK_connector_charging_profile_charging_profile_pk` FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_connector_charging_profile_connector_pk` FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
);
