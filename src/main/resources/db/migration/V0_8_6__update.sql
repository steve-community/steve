--
-- update the character set for existing installations.

-- mysql will refuse to change the character set of tables with foreign key relationships,
-- so we disable foreign key checks (we could also first drop FKs and after changes insert FKs again).
-- but then, to ensure data integrity, we should block other access to these tables. hence, the lock tables.
--
-- and also we cannot touch book keeping table "schema_version" of flyway,
-- since it will be in use during the migration.
--

LOCK TABLES
  address WRITE,
  charge_box WRITE,
  connector WRITE,
  connector_meter_value WRITE,
  connector_status WRITE,
  ocpp_tag WRITE,
  reservation WRITE,
  settings WRITE,
  `transaction` WRITE,
  `user` WRITE,
  `schema_version` WRITE; 

SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `user` CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE settings CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE connector_status CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE connector_meter_value CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE address CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE `transaction` 
DROP FOREIGN KEY `FK_transaction_ocpp_tag_id_tag` ,
CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE reservation 
DROP FOREIGN KEY `FK_reservation_charge_box_cbid` ,
DROP FOREIGN KEY `FK_reservation_ocpp_tag_id_tag`,
CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE ocpp_tag 
DROP FOREIGN KEY `FK_ocpp_tag_parent_id_tag` ,
CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE connector 
DROP FOREIGN KEY `FK_connector_charge_box_cbid` ,
CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE charge_box  CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;


ALTER TABLE `transaction`
ADD CONSTRAINT `FK_transaction_ocpp_tag_id_tag`
FOREIGN KEY (`id_tag`)
REFERENCES `ocpp_tag` (`id_tag`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
 
ALTER TABLE `reservation`
ADD CONSTRAINT `FK_reservation_charge_box_cbid`
FOREIGN KEY (`charge_box_id`)
REFERENCES `charge_box` (`charge_box_id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;
 
ALTER TABLE `ocpp_tag`
ADD CONSTRAINT `FK_ocpp_tag_parent_id_tag`
FOREIGN KEY (`parent_id_tag`)
REFERENCES `ocpp_tag` (`id_tag`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
 
ALTER TABLE `connector`
ADD CONSTRAINT `FK_connector_charge_box_cbid`
FOREIGN KEY (`charge_box_id`)
REFERENCES `charge_box` (`charge_box_id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

SET FOREIGN_KEY_CHECKS = 1;

UNLOCK TABLES;

REPAIR TABLE
  address,
  charge_box,
  connector,
  connector_meter_value,
  connector_status,
  ocpp_tag,
  reservation,
  settings,
  `transaction`,
  `user`,
 `schema_version`;

OPTIMIZE TABLE
  address,
  charge_box,
  connector,
  connector_meter_value,
  connector_status,
  ocpp_tag,
  reservation,
  settings,
  `transaction`,
  `user`,
 `schema_version`;
