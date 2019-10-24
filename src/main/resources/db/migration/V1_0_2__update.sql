ALTER TABLE `charge_box`
    ADD COLUMN `registration_status` VARCHAR(255) NOT NULL DEFAULT 'Accepted' AFTER `ocpp_protocol`;