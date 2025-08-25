CREATE TABLE charging_profile (
  charging_profile_pk INT NOT NULL AUTO_INCREMENT,
  stack_level INT NOT NULL,
  charging_profile_purpose varchar(255) NOT NULL,
  charging_profile_kind varchar(255) NOT NULL,
  recurrency_kind varchar(255) NULL,
  valid_from TIMESTAMP(6) NULL,
  valid_to TIMESTAMP(6) NULL,

  duration_in_seconds INT NULL,
  start_schedule TIMESTAMP(6) NULL NULL,
  charging_rate_unit varchar(255) NOT NULL,
  min_charging_rate decimal(15, 1) NULL, -- according to ocpp, at most one digit fraction.

  description varchar(255) null,
  note TEXT null,

  PRIMARY KEY (charging_profile_pk)
);

CREATE TABLE charging_schedule_period (
  charging_profile_pk INT NOT NULL,
  start_period_in_seconds INT NOT NULL,
  power_limit_in_amperes decimal(15, 1) NOT NULL, -- according to ocpp, at most one digit fraction.
  number_phases INT NULL
);

CREATE TABLE connector_charging_profile (
  connector_pk INT(11) UNSIGNED NOT NULL,
  charging_profile_pk INT NOT NULL
);

ALTER TABLE `connector_charging_profile`
ADD UNIQUE `UQ_connector_charging_profile`(`connector_pk`, `charging_profile_pk`);

ALTER TABLE `charging_schedule_period`
ADD UNIQUE `UQ_charging_schedule_period`(`charging_profile_pk`, `start_period_in_seconds`);

ALTER TABLE `charging_schedule_period`
ADD CONSTRAINT `FK_charging_schedule_period_charging_profile_pk`
FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `connector_charging_profile`
ADD CONSTRAINT `FK_connector_charging_profile_charging_profile_pk`
FOREIGN KEY (`charging_profile_pk`) REFERENCES `charging_profile` (`charging_profile_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `connector_charging_profile`
ADD CONSTRAINT `FK_connector_charging_profile_connector_pk`
FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION;
