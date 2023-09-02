CREATE TABLE `settings` (
    `appId` VARCHAR(40),
    `heartbeatIntervalInSeconds` INT,
    `hoursToExpire` INT,
    PRIMARY KEY (`appId`),
    UNIQUE KEY `settings_id_UNIQUE` (`appId`)
);

INSERT INTO `settings` (appId, heartbeatIntervalInSeconds, hoursToExpire)
VALUES ('U3RlY2tkb3NlblZlcndhbHR1bmc=', 14400, 1);