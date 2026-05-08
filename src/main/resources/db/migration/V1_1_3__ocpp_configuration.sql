START TRANSACTION;

ALTER TABLE `charge_box`
    ADD COLUMN `ocpp_configuration` JSON COMMENT 'OCPP configuration keys and their values';

UPDATE `charge_box`
SET `ocpp_configuration` = JSON_OBJECT('CpoName', `cpo_name`)
WHERE `cpo_name` IS NOT NULL
  AND `cpo_name` <> '';

ALTER TABLE `charge_box`
    DROP COLUMN `cpo_name`;

COMMIT;
