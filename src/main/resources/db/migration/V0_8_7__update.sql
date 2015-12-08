--
-- split "street_and_house_number" into "street" and "house_number"
--

ALTER TABLE `address`
CHANGE COLUMN `street_and_house_number` `street` VARCHAR(1000),
ADD house_number varchar(255) AFTER `street`;
