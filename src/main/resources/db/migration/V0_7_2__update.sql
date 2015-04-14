ALTER TABLE `stevedb`.`chargebox` 
CHANGE COLUMN `ocppVersion` `ocppProtocol` VARCHAR(10) NULL DEFAULT NULL ;

--
-- Migrate existing charge points from old 'version' scheme to the newer 'protocol' scheme
--
UPDATE `stevedb`.`chargebox` SET `ocppProtocol`='ocpp1.2S' WHERE `ocppProtocol`='1.2';
UPDATE `stevedb`.`chargebox` SET `ocppProtocol`='ocpp1.5S' WHERE `ocppProtocol`='1.5';