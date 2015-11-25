CREATE TABLE address (
  address_id INT NOT NULL AUTO_INCREMENT,
  street_and_house_number varchar(1000) NOT NULL,
  zip_code varchar(255) NOT NULL,
  city varchar(255) NOT NULL,
  country varchar(255) NOT NULL,
  PRIMARY KEY (address_id)
);

ALTER TABLE `charge_box`
ADD address_id INT DEFAULT NULL,
ADD CONSTRAINT FK_charge_box_address_aid
FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `user`
ADD address_id INT DEFAULT NULL,
ADD CONSTRAINT FK_user_address_aid
FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `charge_box`
ADD description TEXT AFTER last_heartbeat_timestamp,
ADD location_latitude DECIMAL(11,8) NULL,
ADD location_longitude DECIMAL(11,8) NULL;

ALTER TABLE `user`
ADD first_name varchar(255) NULL,
ADD last_name varchar(255) NULL,
ADD birth_day DATE,
ADD sex CHAR(1),
ADD phone varchar(255) NULL,
ADD e_mail varchar(255) NULL;

