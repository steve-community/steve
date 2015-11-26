CREATE TABLE address (
  address_pk INT NOT NULL AUTO_INCREMENT,
  street_and_house_number varchar(1000) NOT NULL,
  zip_code varchar(255) NOT NULL,
  city varchar(255) NOT NULL,
  country varchar(255) NOT NULL,
  PRIMARY KEY (address_pk)
);

CREATE TABLE user (
  user_pk INT NOT NULL AUTO_INCREMENT,
  ocpp_tag_pk INT DEFAULT NULL,
  address_pk INT DEFAULT NULL,
  first_name varchar(255) NULL,
  last_name varchar(255) NULL,
  birth_day DATE,
  sex CHAR(1),
  phone varchar(255) NULL,
  e_mail varchar(255) NULL,
  note TEXT NULL,
  PRIMARY KEY (user_pk)
);

ALTER TABLE `charge_box`
ADD description TEXT AFTER last_heartbeat_timestamp,
ADD location_latitude DECIMAL(11,8) NULL,
ADD location_longitude DECIMAL(11,8) NULL;

ALTER TABLE `charge_box`
ADD address_pk INT DEFAULT NULL,
ADD CONSTRAINT FK_charge_box_address_apk
FOREIGN KEY (address_pk) REFERENCES address (address_pk) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `user`
ADD CONSTRAINT `FK_user_ocpp_tag_otpk`
FOREIGN KEY (`ocpp_tag_pk`) REFERENCES `ocpp_tag` (`ocpp_tag_pk`) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `user`
ADD CONSTRAINT FK_user_address_apk
FOREIGN KEY (address_pk) REFERENCES address (address_pk) ON DELETE SET NULL ON UPDATE NO ACTION;

