UPDATE `stevedb`.`dbVersion` SET `version` = '0.6.9';

ALTER TABLE `stevedb`.`user`
ADD CONSTRAINT `FK_user_parentIdTag`
  FOREIGN KEY (`parentIdTag`)
  REFERENCES `stevedb`.`user` (`idTag`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
