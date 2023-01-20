-- came with https://github.com/RWTH-i5-IDSG/steve/issues/310
ALTER TABLE `charging_schedule_period`
    CHANGE COLUMN `power_limit_in_amperes` `power_limit` DECIMAL(15, 1) NOT NULL;
